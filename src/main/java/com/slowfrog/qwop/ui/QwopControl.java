package com.slowfrog.qwop.ui;

import com.slowfrog.qwop.Qwopper;
import com.slowfrog.qwop.RunInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;


public class QwopControl extends JFrame {

    private static final long serialVersionUID = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(QwopControl.class);
    private static final Font FONT = new Font("Lucida Sans", Font.BOLD, 24);

    private Qwopper qwopper;
    private Robot rob;
    private JTextArea logOutput;
    private JTextField sequence;
    private JLabel distance;
    private JLabel distance2;
    private JLabel distance3;
    private long startTime;
    private Random random;
    private Timer timer;
    private int gamesLeft;

    private JButton init;
    private JButton goRandom;
    private JButton go;
    private JButton goMultiple;
    private JButton stop;

    private JSpinner yOffsetSpinner;

    public QwopControl() throws AWTException {
        super("QWOP control");
        LOGGER.info("Hello world!");

        this.setLocation(200, 0);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        rob = new Robot();
        random = new Random(System.currentTimeMillis());
        qwopper = new Qwopper(rob);

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        c.add(bar, BorderLayout.SOUTH);

        init = new JButton("Find game area");
        go = new JButton("Run, Qwop, run!");
        goMultiple = new JButton("Run 5 times, 15s/run max");
        stop = new JButton("Stop");
        bar.add(init);
        bar.add(go);
        bar.add(goMultiple);
        bar.add(stop);

        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());

        JPanel topWest = new JPanel();
        SpinnerModel model = new SpinnerNumberModel(16, 6, 26, 1);
        yOffsetSpinner = new JSpinner(model);
        topWest.add(new JLabel("yOffset: "));
        topWest.add(yOffsetSpinner);
        topWest.add(new JLabel("Current: "));
        top.add(topWest, BorderLayout.WEST);

        sequence = new JTextField();
        sequence.setText("HHHIILL");
        top.add(sequence, BorderLayout.CENTER);
        goRandom = new JButton("Random...");
        top.add(goRandom, BorderLayout.EAST);

        JPanel bottom = new JPanel();
        top.add(bottom, BorderLayout.SOUTH);
        bottom.setLayout(new FlowLayout());
        distance = new JLabel();
        distance.setPreferredSize(new Dimension(200, 30));
        bottom.add(distance);
        distance2 = new JLabel();
        distance2.setPreferredSize(new Dimension(200, 30));
        bottom.add(distance2);
        distance3 = new JLabel();
        distance3.setFont(FONT);
        distance3.setPreferredSize(new Dimension(300, 30));
        bottom.add(distance3);
        c.add(top, BorderLayout.NORTH);

        logOutput = new JTextArea(20, 60);
        logOutput.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logOutput);
        logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        c.add(logScroll, BorderLayout.CENTER);

        // Add event handlers
        init.addActionListener(this::eventHandlerInit);
        goRandom.addActionListener(this::eventHandlerGoRandom);
        go.addActionListener(this::eventHandlerGo);
        goMultiple.addActionListener(this::eventHandlerGoMultiple);
        stop.addActionListener(this::eventHandlerStop);

        timer = new Timer(500, this::createTimer);
    }


    public static void main(String[] args) {
        try {
            JFrame f = new QwopControl();
            f.pack();
            f.setVisible(true);

        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    private void eventHandlerInit(ActionEvent _ev) {
        try {
            int[] origin = qwopper.findRealOrigin();
            LOGGER.info("Game at ({},{})", origin[0], origin[1]);
            log("Game at (" + origin[0] + "," + origin[1] + ")");

        } catch (Exception e) {
            LOGGER.error("Error finding game", e);
            log("Error finding game: " + e.getMessage());
        }
    }

    private void eventHandlerGoRandom(ActionEvent _ev) {
        String dna = Qwopper.makeRealisticRandomString2(4 + random.nextInt(8));
        sequence.setText(dna);
    }

    private void eventHandlerGo(ActionEvent _ev) {
        launchGames(sequence.getText(), 1, 0);
        go.setEnabled(false);
        goMultiple.setEnabled(false);
    }

    private void eventHandlerGoMultiple(ActionEvent _ev) {
        launchGames(sequence.getText(), 5, 15000);
        go.setEnabled(false);
        goMultiple.setEnabled(false);
    }

    private void eventHandlerStop(ActionEvent _ev) {
        qwopper.stop();
        go.setEnabled(true);
        goMultiple.setEnabled(true);
        timer.stop();
    }

    private void createTimer(ActionEvent _ev) {
        LOGGER.debug("QwopControl Timer!");
        long now = System.currentTimeMillis();

        long duration = (long) Math.max(((double) (now - startTime)) / ((double) 1000), 0.1);
        String time = (duration / 60) + ":" + new DecimalFormat("00").format(duration % 60);
        LOGGER.debug("QwopControl Timer! duration {}", duration);

        float runDistance = qwopper.captureDistanceAsFloat((int) yOffsetSpinner.getValue());
        if (Math.abs(-99F - runDistance) < 1e-10) {
            log("*****  Could not parse distance. Try adjusting the yOffset :-(");
        }
        updateDistanceDisplay();
        LOGGER.debug("QwopControl Timer! runDistance {}", runDistance);

        float speed = (runDistance / duration);
        DecimalFormatSymbols symbols = new DecimalFormat().getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.000", symbols);
        String speedStr = df.format(speed);
        LOGGER.debug("QwopControl Timer! speedStr {}", speedStr);

        distance3.setText(runDistance + "m, " + time + ", " + speedStr + "m/s");
    }

    private void updateDistanceDisplay() {
        ImageIcon icon = new ImageIcon();
        icon.setImage(qwopper.getLastCapture());
        distance.setIcon(icon);
        ImageIcon icon2 = new ImageIcon();
        icon2.setImage(qwopper.getLastTransformed());
        distance2.setIcon(icon2);
    }

    private void launchGames(final String dna, int count, int maxTimePerGame) {
        this.gamesLeft = count;
        log("Playing " + dna + " " + count + " time(s)");
        nextGame(dna, maxTimePerGame);
    }

    private void nextGame(final String dna, final int maxTimePerGame) {
        new Thread(() -> {
            Point screenPoint = MouseInfo.getPointerInfo().getLocation();
            startTime = System.currentTimeMillis();
            qwopper.startGame();
            rob.mouseMove(screenPoint.x, screenPoint.y); // Move cursor back to button that was pressed
            timer.start();

            RunInfo runInfo = qwopper.playOneGame(dna, maxTimePerGame, (int) yOffsetSpinner.getValue());
            log(runInfo.toString());

            if (!qwopper.isRunning()) {
                timer.stop();
                if (--gamesLeft == 0) {
                    go.setEnabled(true);
                    goMultiple.setEnabled(true);
                } else {
                    nextGame(sequence.getText(), maxTimePerGame);
                }
            }
        }).start();
    }

    public void log(final String message) {
        // Using setText() to enable auto-scrolling
        SwingUtilities.invokeLater(() -> logOutput.setText(logOutput.getText() + message + "\n"));
    }
}
