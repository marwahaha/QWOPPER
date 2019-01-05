package com.slowfrog.qwop.ui;

import com.slowfrog.qwop.Qwopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
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
    private int runsLeft;
    private long timeLimit;

    public QwopControl() throws AWTException {
        super("QWOP control");
        LOGGER.info("Hello world!");

        this.setLocation(200, 0);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        rob = new Robot();
        random = new Random(System.currentTimeMillis());
        qwopper = new Qwopper(rob, this);

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        c.add(bar, BorderLayout.SOUTH);

        JButton init = new JButton("Find game area");
        bar.add(init);

        final JButton goRandom = new JButton("Random...");
        bar.add(goRandom);

        final JButton go = new JButton("Run, Qwop, run!");
        bar.add(go);

        final JButton go10 = new JButton("Run 10 times 60 s. max");
        bar.add(go10);

        final JButton stop = new JButton("Stop");
        bar.add(stop);

        JPanel top = new JPanel();
        top.setLayout(new BorderLayout());
        top.add(new JLabel("Current: "), BorderLayout.WEST);
        sequence = new JTextField();
        top.add(sequence, BorderLayout.CENTER);
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
        init.addActionListener(ev -> {
            try {
                int[] origin = qwopper.findRealOrigin();
                LOGGER.info("Origin at ({},{})", origin[0], origin[1]);
                log("Origin at (" + origin[0] + "," + origin[1] + ")");

            } catch (Exception e) {
                LOGGER.error("Error finding origin", e);
                log("Error finding origin: " + e.getMessage());
            }
        });

        goRandom.addActionListener(ev -> {
            String dna = Qwopper.makeRealisticRandomString2(10 + random.nextInt(21));
            sequence.setText(dna);
        });

        go.addActionListener(ev -> {
            runGame(sequence.getText(), 1, 0);
            go.setEnabled(false);
            go10.setEnabled(false);
        });

        go10.addActionListener(ev -> {
            runGame(sequence.getText(), 10, 60000);
            go.setEnabled(false);
            go10.setEnabled(false);
        });

        stop.addActionListener(ev -> {
            qwopper.stop();
            go.setEnabled(true);
            go10.setEnabled(true);
            timer.stop();
        });

        timer = new Timer(500, ev -> {
            LOGGER.debug("QwopControl Timer!");
            long now = System.currentTimeMillis();
            long duration = (long) Math.max(((double) (now - startTime)) / ((double) 1000), 0.1);
            LOGGER.debug("QwopControl Timer! duration {}", duration);

            String time = (duration / 60) + ":" + new DecimalFormat("00").format(duration % 60);

            float runDistance = 0F;
            try {
                String ret = qwopper.captureDistance();
                updateDistanceDisplay();
                runDistance = Float.parseFloat(ret);
            } catch (NumberFormatException e) {
                // pass
            }
            LOGGER.debug("QwopControl Timer! runDistance {}", runDistance);

            float speed = (runDistance / duration);
            DecimalFormatSymbols symbols = new DecimalFormat()
                    .getDecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("0.000", symbols);
            String speedStr = df.format(speed);
            LOGGER.debug("QwopControl Timer! speedStr {}", speedStr);

            distance3.setText(runDistance + "m, " + time + ", " + speedStr + "m/s");

            if ((timeLimit != 0) && (now > timeLimit)) {
                qwopper.stop();
            }

            if (!qwopper.isRunning()) {
                timer.stop();
                if (--runsLeft == 0) {
                    go.setEnabled(true);
                    go10.setEnabled(true);
                } else {
                    nextGame(sequence.getText(), 60000);
                }
            }
        });
        timer.setDelay(250);
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

    private void updateDistanceDisplay() {
        ImageIcon icon = new ImageIcon();
        icon.setImage(qwopper.getLastCapture());
        distance.setIcon(icon);
        ImageIcon icon2 = new ImageIcon();
        icon2.setImage(qwopper.getLastTransformed());
        distance2.setIcon(icon2);
    }

    private void runGame(final String dna, int count, int maxTime) {
        this.runsLeft = count;
        // This is to restore the mouse to its starting position
        // after having clicked on the QWOP window to transfer keyboard focus
        nextGame(dna, maxTime);
    }

    private void nextGame(final String dna, final int maxTime) {
        execOutOfAWT(() -> {
            Point screenPoint = MouseInfo.getPointerInfo().getLocation();
            startTime = System.currentTimeMillis();
            timeLimit = (maxTime > 0) ? startTime + maxTime : 0;
            qwopper.startGame();
            rob.mouseMove(screenPoint.x, screenPoint.y); // Move cursor back to button that was pressed
            timer.start();

            qwopper.playOneGame(dna, maxTime);
        });
    }

    private void execOutOfAWT(Runnable r) {
        Thread t = new Thread(r);
        t.start();
    }

    public void log(final String message) {
        // Using setText() to enable auto-scrolling
        SwingUtilities.invokeLater(() -> logOutput.setText(logOutput.getText() + message + "\n"));
    }

}
