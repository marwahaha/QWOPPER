package com.slowfrog.qwop;

import com.slowfrog.qwop.ui.QwopControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * This class will try to play QWOP and evolve some way to play well...
 * hopefully. Game at {@link http://foddy.net/Athletics.html}
 */
public class Qwopper {

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
     * Note    	Input
     * A	1		Q
     * B	2		W
     * C	3		O
     * D	4		P
     * E	5		QW
     * F	6		QO
     * G	7		QP
     * H	8		WO
     * I	9		WP
     * J	10		OP
     * K	11		QWO
     * L	12		QWP
     * M	13		QOP
     * N	14		WOP
     * O	15		QWOP
     * P	16		----  (no keys pressed)
     */
    private static final String NOTES2 = "ABCDEFGHIJKLMNOP";

    /**
     * Number of consecutive runs before we trigger a reload of the browser to
     * keep CPU and memory usage reasonable.
     */
    private static final int MAX_RUNS_BETWEEN_RELOAD = 40;
    private static final Logger LOGGER = LoggerFactory.getLogger(Qwopper.class);
    private final Robot rob;
    private QwopControl qwopControl;
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

    public Qwopper(Robot rob, QwopControl qwopControl) {
        this.rob = rob;
        this.qwopControl = qwopControl;
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
        Random random = new Random(System.currentTimeMillis());
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
            switch (c) {
                case 'A':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'B':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'C':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'D':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'E':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'F':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'G':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'H':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'I':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'J':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'K':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                case 'L':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'M':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'N':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'O':
                    rob.keyPress(KeyEvent.VK_Q);
                    rob.keyPress(KeyEvent.VK_W);
                    rob.keyPress(KeyEvent.VK_O);
                    rob.keyPress(KeyEvent.VK_P);
                    break;

                case 'P':
                    rob.keyRelease(KeyEvent.VK_Q);
                    rob.keyRelease(KeyEvent.VK_W);
                    rob.keyRelease(KeyEvent.VK_O);
                    rob.keyRelease(KeyEvent.VK_P);
                    break;

                default:
                    LOGGER.warn("Unknown 'note': {}", c);
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

    private String captureDistance() {
        // TODO document that you might need to tune this box to pick up on digits
        Rectangle distRect = new Rectangle();
        distRect.x = origin[0] + 200;
        distRect.y = origin[1] + 16;
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

    public float captureDistanceAsFloat() {
        try {
            return Float.parseFloat(captureDistance());
        } catch (NumberFormatException e) {
            if (qwopControl != null) {
                qwopControl.log("*****  captureDistance() returned empty string. Setting distance to 0 :-(");
            }
            return 0F;
        }
    }

    public RunInfo playOneGame(String str, long maxDuration) {
        if (qwopControl != null) {
            qwopControl.log("Playing " + str);
        }
        doWait(500); // 0.5s wait to be sure QWOP is ready to run
        long start = System.currentTimeMillis();
        if (maxDuration > 0) {
            this.timeLimit = start + maxDuration;
        } else {
            this.timeLimit = 0;
        }
        while (!isFinished() && !stop) {
            playString2(str);
        }
        stopRunning();

        long end = System.currentTimeMillis();
        doWait(1000);
        float distance = captureDistanceAsFloat();

        RunInfo info = new RunInfo(str, DELAY, !stop && distance < 100, stop, end - start, distance);

        if (++nbRuns == MAX_RUNS_BETWEEN_RELOAD) {
            nbRuns = 0;
            refreshBrowser();
            if (qwopControl != null) {
                qwopControl.log("Refreshed browser");
            }
        }
        return info;
    }
}
