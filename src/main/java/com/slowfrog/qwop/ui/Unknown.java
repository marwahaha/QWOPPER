package com.slowfrog.qwop.ui;

import com.slowfrog.qwop.Qwopper;
import com.slowfrog.qwop.RunInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Unknown {

    private static final Logger LOGGER = LoggerFactory.getLogger(Unknown.class);

    public static void main(String[] args) {

        int tries = 1;
        int count = 500;
        String str = null;
        if (args.length > 0) {
            try {
                tries = Integer.parseInt(args[0]);
                if (args.length > 1) {
                    count = Integer.parseInt(args[1]);
                }

            } catch (NumberFormatException e) {
                // First arg is not a number: probably a code string
                str = args[0];
            }
        }

        try {
            Robot rob = new Robot();
            Qwopper qwop = new Qwopper(rob);
            qwop.findRealOrigin();
            for (int round = 0; round < count; ++round) {
                if (count > 1) {
                    str = Qwopper.makeRealisticRandomString2(40);
                }
                testString(qwop, str, tries, round);
            }

        } catch (Exception t) {
            LOGGER.error("Error", t);
        }
    }

    private static void testString(Qwopper qwop, String str, int count, int round) {
        for (int i = 0; i < count; ++i) {
            LOGGER.info("Run #{}.{}\n", round, i);
            qwop.startGame();
            RunInfo info = qwop.playOneGame(str, 30000);
            LOGGER.info(info.toString());
            LOGGER.info(info.marshal());
            saveRunInfo("runs2.txt", info);
        }
    }

    private static void saveRunInfo(String filename, RunInfo info) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(filename, true));
            try {
                out.println(info.marshal());
            } finally {
                out.flush();
                out.close();
            }
        } catch (IOException ioe) {
            LOGGER.error("Error marshalling", ioe);
        }
    }

}
