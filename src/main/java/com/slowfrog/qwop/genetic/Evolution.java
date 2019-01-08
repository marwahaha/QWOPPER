package com.slowfrog.qwop.genetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.slowfrog.qwop.Qwopper.COST_1_CHARS;
import static com.slowfrog.qwop.Qwopper.COST_1_CHARS_REGEX;
import static com.slowfrog.qwop.Qwopper.NOTES2;
import static com.slowfrog.qwop.Qwopper.letterMap;
import static com.slowfrog.qwop.genetic.EditDistance.compareCost;

public class Evolution {

    /**
     * Mutates a string by exactly 1 character (insertion, deletion, or edit).
     * <p>
     * Will not output empty string.
     */
    public static String mutateString(String str, double insertionChance, double deletionChance) {
        Random random = new Random();

        // if empty, insert a random character
        if (0 == str.length()) {
            return String.valueOf(NOTES2.charAt(random.nextInt(NOTES2.length())));
        }

        int mutSpot = random.nextInt(str.length());
        double r = random.nextDouble();

        // insertions
        if (r < insertionChance) {
            return str.substring(0, mutSpot)
                    + NOTES2.charAt(random.nextInt(NOTES2.length()))
                    + str.substring(mutSpot);
        }

        // deletions
        if (str.length() > 1 && r < (insertionChance + deletionChance)) {
            return str.substring(0, mutSpot) + str.substring(mutSpot + 1);
        }

        // edits
        char newLetter = NOTES2.replace(str.substring(mutSpot, mutSpot + 1), "")
                .charAt(random.nextInt(NOTES2.length() - 1));
        return str.substring(0, mutSpot) + newLetter + str.substring(mutSpot + 1);
    }


    /**
     * Mutates a string by exactly 1 "cost" as per EditDistance.modifiedLevenshtein (insertion, deletion, or edit).
     * <p>
     * Will only delete or insert characters of cost=1.
     * Can only handle valid input.
     * Will not output empty string.
     */
    public static String mutateStringModified(String str, double insertionChance, double deletionChance) {
        Random random = new Random();

        // if empty, insert a random character
        if (0 == str.length()) {
            return String.valueOf(COST_1_CHARS.charAt(random.nextInt(COST_1_CHARS.length())));
        }

        int mutSpot = random.nextInt(str.length());
        double r = random.nextDouble();

        // insertions
        if (r < insertionChance) {
            return str.substring(0, mutSpot)
                    + COST_1_CHARS.charAt(random.nextInt(COST_1_CHARS.length()))
                    + str.substring(mutSpot);
        }

        // deletions
        int numCost1Chars = str.length() - COST_1_CHARS_REGEX.matcher(str).replaceAll("").length();
        if (str.length() > 1 && numCost1Chars > 0 && r < (insertionChance + deletionChance)) {
            int deletionSpot = random.nextInt(numCost1Chars);
            Matcher m = COST_1_CHARS_REGEX.matcher(str);
            int idx = 0;
            while (m.find()) {
                if (idx == deletionSpot) {
                    return str.substring(0, m.start()) + str.substring(m.start() + 1);
                }
                idx++;
            }
        }

        // edits
        char charToReplace = str.charAt(mutSpot);
        List<Character> options = letterMap.keySet().stream()
                .filter(l -> 1 == compareCost(l, charToReplace))
                .collect(Collectors.toList());
        return str.substring(0, mutSpot) + options.get(random.nextInt(options.size())) + str.substring(mutSpot + 1);
    }


    /**
     * Generates up to num descendant strings of an initial string.
     * <p>
     * All descendants have levenshtein distance 1.
     */
    public static Set<String> generateDescendants(String inputStr, int maxNumDescendants, double insertionChance, double deletionChance) {
        List<String> output = new ArrayList<>();
        for (int idx = 0; idx < maxNumDescendants; idx++) {
            output.add(mutateString(inputStr, insertionChance, deletionChance));
        }
        return new HashSet<>(output);
    }

    /**
     * Generates up to num descendant strings from an initial string.
     * <p>
     * All descendants have modifiedLevenshtein distance 1.
     */
    public static Set<String> generateDescendantsModified(String inputStr, int maxNumDescendants, double insertionChance, double deletionChance) {
        List<String> output = new ArrayList<>();
        for (int idx = 0; idx < maxNumDescendants; idx++) {
            output.add(mutateStringModified(inputStr, insertionChance, deletionChance));
        }
        return new HashSet<>(output);
    }
}
