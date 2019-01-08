package com.slowfrog.qwop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * This class will try to play QWOP and evolve some way to play well...
 * hopefully. Game at {@link http://foddy.net/Athletics.html}
 */
public class Qwopper {
    /**
     * DNA Letter to QWOP values in binary
     */
    public static final Map<Character, Integer> letterMap;
    public static final String NOTES2 = "ABCDEFGHIJKLMNOP";
    public static final String COST_1_CHARS = "ABCDP";
    public static final Pattern COST_1_CHARS_REGEX = Pattern.compile("[" + COST_1_CHARS + "]");

    /**
     * Tolerance for color comparison.
     * // TODO document that you might need to tune this to pick up game
     */
    private static final int RGB_TOLERANCE = 30;
    /**
     * Unit delay in milliseconds when playing a 'string'
     */
    private static final int DELAY = 150;
    /**
     * Number of consecutive runs before we trigger a reload of the browser to
     * keep CPU and memory usage reasonable.
     */
    private static final int MAX_RUNS_BETWEEN_RELOAD = 40;
    private static final Logger LOGGER = LoggerFactory.getLogger(Qwopper.class);

    static {
        letterMap = new HashMap<>();
        letterMap.put('A', 0b1000);
        letterMap.put('B', 0b0100);
        letterMap.put('C', 0b0010);
        letterMap.put('D', 0b0001);
        letterMap.put('E', 0b1100);
        letterMap.put('F', 0b1010);
        letterMap.put('G', 0b1001);
        letterMap.put('H', 0b0110);
        letterMap.put('I', 0b0101);
        letterMap.put('J', 0b0011);
        letterMap.put('K', 0b1110);
        letterMap.put('L', 0b1101);
        letterMap.put('M', 0b1011);
        letterMap.put('N', 0b0111);
        letterMap.put('O', 0b1111);
        letterMap.put('P', 0b0000);
    }

    private final Robot rob;
    private int[] origin;
    private boolean finished;
    private long timeLimit;
    private boolean stop;
    private String string;
    private int nbRuns;
    private BufferedImage capture;
    private BufferedImage transformed;

    public Qwopper(Robot rob) {
        this.rob = rob;
    }

    /**
     * Distance between two colors.
     */
    private static int colorDistance(int rgb1, int rgb2) {
        int dr = Math.abs(((rgb1 & 0xff0000) >> 16) - ((rgb2 & 0xff0000) >> 16));
        int dg = Math.abs(((rgb1 & 0xff00) >> 8) - ((rgb2 & 0xff00) >> 8));
        int db = Math.abs((rgb1 & 0xff) - (rgb2 & 0xff));
        return dr + dg + db;
    }

    /**
     * Checks if a color matches another within a given tolerance.
     */
    private static boolean colorMatches(int ref, int other) {
        return colorDistance(ref, other) < RGB_TOLERANCE;
    }

    /**
     * Checks if from a given x,y position we can find the pattern that identifies
     * the blue border of the message box.
     */
    private static boolean matchesBlueBorder(BufferedImage img, int x, int y) {
        int refColor = 0x9dbcd0;
        return ((y > 4) && (y < img.getHeight() - 4) && (x < img.getWidth() - 12) &&
                colorMatches(img.getRGB(x, y), refColor) &&
                colorMatches(img.getRGB(x + 4, y), refColor) &&
                colorMatches(img.getRGB(x + 8, y), refColor) &&
                colorMatches(img.getRGB(x + 12, y), refColor) &&
                colorMatches(img.getRGB(x, y + 4), refColor) &&
                !colorMatches(img.getRGB(x, y - 4), refColor) && !colorMatches(
                img.getRGB(x + 4, y + 4), refColor));
    }

    /**
     * From a position that matches the blue border, slide left and top until the
     * corner is found.
     */
    private static int[] slideTopLeft(BufferedImage img, int x, int y) {
        int ax = x;
        int ay = y;

        OUTER_LOOP:

        while (ax >= 0) {
            --ax;
            if (matchesBlueBorder(img, ax, ay)) {
                continue;
            } else {
                ++ax;
                while (ay >= 0) {
                    --ay;
                    if (matchesBlueBorder(img, ax, ay)) {
                        continue;
                    } else {
                        ++ay;
                        break OUTER_LOOP;
                    }
                }
            }
        }
        return new int[]{ax, ay};
    }

    /**
     * Move the mouse cursor to a given screen position and click with the left
     * mouse button.
     */
    private static void clickAt(Robot rob, int x, int y) {
        rob.mouseMove(x, y);
        rob.mousePress(InputEvent.BUTTON1_MASK);
        rob.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /**
     * Wait for a few milliseconds, without fear of an InterruptedException.
     */
    private static void doWait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.debug("interrupted, but who cares?", e);
        }
    }

    /**
     * This function creates a random string using Encoding 2 in which each
     * permutation of possible simultaneous inputs is encoded to a single
     * character or "note."
     *
     * @param duration number of total inputs
     * @return the created string
     */
    public static String makeRealisticRandomString2(int duration) {
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        int cur = 0;
        while (cur < duration) {
            int rnd = random.nextInt(NOTES2.length());
            String k = NOTES2.substring(rnd, rnd + 1);
            char kc = k.charAt(0);
            str.append(kc);
            ++cur;
        }

        return str.toString();

    }

    /**
     * Look for the origin of the game area on screen.
     */
    private static int[] findOrigin(Robot rob) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage shot = rob.createScreenCapture(new Rectangle(dim));
        for (int x = 0; x < dim.width; x += 4) {
            for (int y = 0; y < dim.height; y += 4) {
                if (matchesBlueBorder(shot, x, y)) {
                    LOGGER.info("found blue border: ({}, {})", x, y);
                    int[] corner = slideTopLeft(shot, x, y);
                    return new int[]{corner[0] - 124, corner[1] - 103};
                }
            }
        }
        throw new RuntimeException("Origin not found. Make sure the game is open and fully visible.");
    }

    private void makeKeystrokes(int n) {
        if (1 == ((n >> 3) & 1)) {
            rob.keyPress(KeyEvent.VK_Q);
        } else {
            rob.keyRelease(KeyEvent.VK_Q);
        }
        if (1 == ((n >> 2) & 1)) {
            rob.keyPress(KeyEvent.VK_W);
        } else {
            rob.keyRelease(KeyEvent.VK_W);
        }
        if (1 == ((n >> 1) & 1)) {
            rob.keyPress(KeyEvent.VK_O);
        } else {
            rob.keyRelease(KeyEvent.VK_O);
        }
        if (1 == ((n >> 0) & 1)) {
            rob.keyPress(KeyEvent.VK_P);
        } else {
            rob.keyRelease(KeyEvent.VK_P);
        }
    }

    /**
     * Play a string. Interpret a string of ABCDEFGHIJKLMNOP as a music sheet.
     * <p>
     * Strings use a new encoding in which each
     * permutation of possible simultaneous inputs is encoded to a single
     * character or "note."
     */
    private void playString2(String str) {
        this.string = str;
        long lastTick = System.currentTimeMillis();
        for (int i = 0; i < str.length(); ++i) {
            if (stop) {
                return;
            }
            char c = str.charAt(i);
            try {
                makeKeystrokes(letterMap.get(c));
            } catch (NullPointerException npe) {
                LOGGER.warn("Unknown letter: {}", c);
            }

            int waitTime = (int) ((lastTick + DELAY) - System.currentTimeMillis());
            if (waitTime > 0) {
                doWait(waitTime);
            }
            long newTick = System.currentTimeMillis();
            LOGGER.debug("wait={}ms diff={}ms", waitTime, newTick - lastTick);
            lastTick = newTick;
            if ((this.timeLimit != 0) && (newTick > this.timeLimit)) {
                this.stop = true;
                return;
            }
            // After each DELAY, check the screen to see if it's finished
            if (isFinished()) {
                return;
            }
        }
    }

    public int[] getOrigin() {
        return this.origin;
    }

    public String getString() {
        return this.string;
    }

    public BufferedImage getLastCapture() {
        return this.capture;
    }

    public BufferedImage getLastTransformed() {
        return this.transformed;
    }

    /**
     * Checks if the game is finished by looking at the two yellow medals.
     */
    private boolean isFinished() {
        Color col1 = rob.getPixelColor(origin[0] + 157, origin[1] + 126);
        if (colorMatches(
                (col1.getRed() << 16) | (col1.getGreen() << 8) | col1.getBlue(),
                0xffff00)) {
            Color col2 = rob.getPixelColor(origin[0] + 482, origin[1] + 126);
            if (colorMatches(
                    (col2.getRed() << 16) | (col2.getGreen() << 8) | col2.getBlue(),
                    0xffff00)) {
                finished = true;
                return true;
            }

        }
        finished = false;
        return false;
    }

    public boolean isRunning() {
        return !(this.stop || this.finished);
    }

    /**
     * Find the real origin of the game.
     */
    public int[] findRealOrigin() {
        origin = findOrigin(rob);
        if (isFinished()) {
            origin = new int[]{origin[0] - 5, origin[1] + 4};
        }

        return origin;
    }

    /**
     * Start a game.
     * <p>
     * Changes stop=false.
     * Clicks into the game and restarts it.
     */
    public void startGame() {
        stop = false;
        clickAt(rob, origin[0], origin[1]); // clicks into game
        clickAt(rob, origin[0], origin[1]); // clicks into game (in case the first click only selected the browser)
        if (isFinished()) {
            rob.keyPress(KeyEvent.VK_SPACE);
            rob.keyRelease(KeyEvent.VK_SPACE);
        } else {
            // Press 'R' for restart
            rob.keyPress(KeyEvent.VK_R);
            rob.keyRelease(KeyEvent.VK_R);
        }
    }

    public void stop() {
        this.stop = true;
    }

    private void stopRunning() {
        Point before = MouseInfo.getPointerInfo().getLocation();

        // Restore focus to QWOP (after a button click on QwopControl)
        clickAt(rob, origin[0], origin[1]);
        clickAt(rob, origin[0], origin[1]); // clicks into game (in case the first click only selected the browser)
        // Make sure all possible keys are released
        rob.keyPress(KeyEvent.VK_Q);
        rob.keyPress(KeyEvent.VK_W);
        rob.keyPress(KeyEvent.VK_O);
        rob.keyPress(KeyEvent.VK_P);
        doWait(20);
        rob.keyRelease(KeyEvent.VK_Q);
        rob.keyRelease(KeyEvent.VK_W);
        rob.keyRelease(KeyEvent.VK_O);
        rob.keyRelease(KeyEvent.VK_P);

        // Return the mouse cursor to its initial position...
        rob.mouseMove(before.x, before.y);
    }

    private void refreshBrowser() {
        Point before = MouseInfo.getPointerInfo().getLocation();

        // Click out of the flash rectangle to give focus to the browser
        clickAt(rob, origin[0] - 5, origin[1] - 5);

        // Reload (Windows: F5)
        rob.keyPress(KeyEvent.VK_F5);
        doWait(20);
        rob.keyRelease(KeyEvent.VK_F5);

        // Reload (Mac: CMD-R)
        rob.keyPress(KeyEvent.VK_META);
        rob.keyPress(KeyEvent.VK_R);
        doWait(20);
        rob.keyRelease(KeyEvent.VK_META);
        rob.keyRelease(KeyEvent.VK_R);

        rob.mouseMove(before.x, before.y);

        // Wait some time and try to find the window again
        for (int i = 0; i < 10; ++i) {
            doWait(2000);
            try {
                this.findRealOrigin();
                return;
            } catch (RuntimeException e) {
                // Probably not available yet
            }
        }
        throw new RuntimeException("Could not find origin after browser reload");
    }

    private String captureDistance(int yOffset) {
        Rectangle distRect = new Rectangle();
        distRect.x = origin[0] + 200;
        distRect.y = origin[1] + yOffset;
        distRect.width = 200;
        distRect.height = 30;
        this.capture = rob.createScreenCapture(distRect);

        BufferedImage thresholded = ImageReader.threshold(this.capture);
        List<Rectangle> parts = ImageReader.segment(thresholded);
        LOGGER.debug("number of segments: {}", parts.size());
        this.transformed = ImageReader.drawParts(thresholded, parts);
        String digits = ImageReader.readDigits(thresholded, parts);
        LOGGER.debug("digits: {}", digits);
        return digits;
    }

    public float captureDistanceAsFloat(int yOffset) {
        try {
            return Float.parseFloat(captureDistance(yOffset));
        } catch (NumberFormatException e) {
            LOGGER.warn("**** Could not parse distance: {}", e.getMessage());
            return -99F;
        }
    }

    public RunInfo playOneGame(String str, long maxDurationMs, int yOffsetDistanceCapture) {
        doWait(500); // 0.5s wait to be sure QWOP is ready to run
        long start = System.currentTimeMillis();
        if (maxDurationMs > 0) {
            this.timeLimit = start + maxDurationMs;
        } else {
            this.timeLimit = 0;
        }
        while (!isFinished() && !stop) {
            playString2(str);
        }
        stopRunning();

        long end = System.currentTimeMillis();
        doWait(1000);
        float distance = captureDistanceAsFloat(yOffsetDistanceCapture);

        RunInfo info = new RunInfo(str, DELAY, !stop && distance < 100, stop, end - start, distance);

        if (++nbRuns == MAX_RUNS_BETWEEN_RELOAD) {
            nbRuns = 0;
            refreshBrowser();
            LOGGER.info("Refreshed browser");
        }
        LOGGER.info(info.marshal());
        return info;
    }
}
