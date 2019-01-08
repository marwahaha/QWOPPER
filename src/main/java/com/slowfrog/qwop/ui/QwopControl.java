package com.slowfrog.qwop.ui;

import com.slowfrog.qwop.Qwopper;
import com.slowfrog.qwop.RunInfo;
import com.slowfrog.qwop.genetic.Evolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.slowfrog.qwop.genetic.Evolution.computeFitness;
import static com.slowfrog.qwop.genetic.Evolution.generateDescendants;
import static com.slowfrog.qwop.genetic.Evolution.generateDescendantsModified;


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
    private JButton go;
    private JButton goMultiple;
    private JButton goAndEvolve;
    private JButton stop;
    private JButton makeInputRandom;
    private JButton mutateInput;

    private JSpinner yOffsetSpinner;

    public QwopControl() throws AWTException {
        super("QWOP control");
        LOGGER.info("Hello world!");

        this.setLocation(200, 0);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        rob = new Robot();
        random = new Random();
        qwopper = new Qwopper(rob);

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        c.add(bar, BorderLayout.SOUTH);

        init = new JButton("Find game area");
        go = new JButton("Run once");
        goMultiple = new JButton("Run 5 times (<15s/run)");
        goAndEvolve = new JButton("Evolve (3 rounds)");
        stop = new JButton("Stop");
        // disable some buttons on default
        go.setEnabled(false);
        goMultiple.setEnabled(false);
        goAndEvolve.setEnabled(false);

        bar.add(init);
        bar.add(go);
        bar.add(goMultiple);
        bar.add(goAndEvolve);
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
        JPanel topEast = new JPanel();
        makeInputRandom = new JButton("Random...");
        mutateInput = new JButton("Mutate");
        topEast.add(makeInputRandom);
        topEast.add(mutateInput);
        top.add(topEast, BorderLayout.EAST);

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
        go.addActionListener(this::eventHandlerGo);
        goMultiple.addActionListener(this::eventHandlerGoMultiple);
        goAndEvolve.addActionListener(this::eventHandlerGoAndEvolve);
        stop.addActionListener(this::eventHandlerStop);
        makeInputRandom.addActionListener(this::eventHandlerMakeInputRandom);
        mutateInput.addActionListener(this::eventHandlerMutateInput);

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

    private static List<String> displayScores(List<String> candidates, Map<String, List<RunInfo>> results) {
        return candidates.stream().map(str -> new DecimalFormat("#.##").format(computeFitness(results.get(str)))).collect(Collectors.toList());
    }

    private void eventHandlerInit(ActionEvent _ev) {
        try {
            int[] origin = qwopper.findRealOrigin();
            LOGGER.info("Game at ({},{})", origin[0], origin[1]);
            log("Game at (" + origin[0] + "," + origin[1] + ")");
            go.setEnabled(true);
            goMultiple.setEnabled(true);
            goAndEvolve.setEnabled(true);
        } catch (Exception e) {
            LOGGER.error("Error finding game", e);
            log("Error finding game: " + e.getMessage());
            go.setEnabled(false);
            goMultiple.setEnabled(false);
            goAndEvolve.setEnabled(false);
        }
    }

    private void eventHandlerGo(ActionEvent _ev) {
        String dna = sequence.getText();
        new Thread(() -> {
            playAll(Collections.singleton(dna), 1, 0);
            go.setEnabled(true);
            goMultiple.setEnabled(true);
            goAndEvolve.setEnabled(true);
        }).start();
        go.setEnabled(false);
        goMultiple.setEnabled(false);
        goAndEvolve.setEnabled(false);
    }

    private void eventHandlerGoMultiple(ActionEvent _ev) {
        String dna = sequence.getText();
        new Thread(() -> {
            playAll(Collections.singleton(dna), 5, 15000);
            go.setEnabled(true);
            goMultiple.setEnabled(true);
            goAndEvolve.setEnabled(true);
        }).start();
        go.setEnabled(false);
        goMultiple.setEnabled(false);
        goAndEvolve.setEnabled(false);
    }

    private void eventHandlerGoAndEvolve(ActionEvent _ev) {
        new Thread(() -> {
            runEvolution(Collections.singletonList(sequence.getText()),
                    1, 15000,
                    5, 0.1, 0.1,
                    3, 3, true);
            go.setEnabled(true);
            goMultiple.setEnabled(true);
            goAndEvolve.setEnabled(true);
        }).start();
        go.setEnabled(false);
        goMultiple.setEnabled(false);
        goAndEvolve.setEnabled(false);
    }

    private void eventHandlerStop(ActionEvent _ev) {
        qwopper.stop();
        timer.stop();
    }

    private void eventHandlerMakeInputRandom(ActionEvent _ev) {
        sequence.setText(Qwopper.makeRealisticRandomString2(4 + random.nextInt(8)));
    }

    private void eventHandlerMutateInput(ActionEvent _ev) {
        sequence.setText(Evolution.mutateString(sequence.getText(), 0.1, 0.1));
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

    private Map<String, List<RunInfo>> playAll(Set<String> candidates, int numGames, long maxDurationMs) {
        HashMap<String, List<RunInfo>> output = new HashMap<>();
        for (String str : candidates) {
            List<RunInfo> runs = new ArrayList<>();
            log("Playing " + str + " " + numGames + " time(s)");
            for (int idx = 0; idx < numGames; idx++) {
                Point screenPoint = MouseInfo.getPointerInfo().getLocation();
                startTime = System.currentTimeMillis();
                qwopper.startGame();
                timer.start();
                rob.mouseMove(screenPoint.x, screenPoint.y); // Move cursor back to button that was pressed

                RunInfo runInfo = qwopper.playOneGame(str, maxDurationMs, (int) yOffsetSpinner.getValue());
                log(runInfo.toString());
                timer.stop();
                runs.add(runInfo);
            }
            output.put(str, runs);
        }
        return output;
    }

    private Map<String, List<RunInfo>> runEvolution(List<String> initialCandidates,
                                                    int numGames, long maxDurationMs,
                                                    int maxNumDescendants, double insertionChance, double deletionChance,
                                                    int survivorsPerRound, int numEvolutions, boolean modifiedLevenshtein) {

        List<String> candidates = initialCandidates;
        Map<String, List<RunInfo>> results = playAll(new HashSet<>(candidates), numGames, maxDurationMs);
        for (int idx = 0; idx < numEvolutions; idx++) {
            log("surviving candidates: " + candidates);
            log("scores: " + displayScores(candidates, results));
            log("Evolution #" + (idx + 1) + ":");
            // TODO make mutation step adjustable (instead of just 1 mutation away)
            Set<String> descendants = candidates.stream()
                    .map(str -> modifiedLevenshtein
                            ? generateDescendantsModified(str, maxNumDescendants, insertionChance, deletionChance)
                            : generateDescendants(str, maxNumDescendants, insertionChance, deletionChance))
                    .flatMap(Collection::stream)
                    .filter(str -> !results.keySet().contains(str))
                    .collect(Collectors.toSet());
            log("descendants: " + descendants);
            results.putAll(playAll(descendants, numGames, maxDurationMs));
            candidates = results.keySet().stream()
                    .sorted(Comparator.comparingDouble(s -> 0 - computeFitness(results.get(s))))
                    .limit(survivorsPerRound)
                    .collect(Collectors.toList());
        }
        log("Evolution results:");
        log("winning: " + candidates);
        log("scores: " + displayScores(candidates, results));
        return results;
    }

    private void log(final String message) {
        // Using setText() to enable auto-scrolling
        SwingUtilities.invokeLater(() -> logOutput.setText(logOutput.getText() + message + "\n"));
    }
}
