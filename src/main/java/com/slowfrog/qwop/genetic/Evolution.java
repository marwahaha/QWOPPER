package com.slowfrog.qwop.genetic;

import java.util.Random;

import static com.slowfrog.qwop.Qwopper.NOTES2;

public class Evolution {

    /**
     * Mutates a string by 1 character (insertion, deletion, or edit).
     */
    public static String mutateString(String str) {
        Random random = new Random(System.currentTimeMillis() * 1000);

        if (0 == str.length()) {
            // insert a random character
            return String.valueOf(NOTES2.charAt(random.nextInt(NOTES2.length())));
        }

        int mutSpot = random.nextInt(str.length());
        double r = random.nextDouble();
        if (r < 0.2) {
            // insertions
            return str.substring(0, mutSpot)
                    + NOTES2.charAt(random.nextInt(NOTES2.length()))
                    + str.substring(mutSpot);
        }

        if (r < 0.4) {
            // deletions
            return str.substring(0, mutSpot) + str.substring(mutSpot + 1);
        }
        // edits
        char newLetter = NOTES2.replace(str.substring(mutSpot, mutSpot + 1), "")
                .charAt(random.nextInt(NOTES2.length() - 1));
        return str.substring(0, mutSpot) + newLetter + str.substring(mutSpot + 1);

    }
}
