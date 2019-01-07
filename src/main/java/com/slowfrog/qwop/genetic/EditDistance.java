package com.slowfrog.qwop.genetic;

import static com.slowfrog.qwop.Qwopper.letterMap;

public class EditDistance {

    /**
     * Considers a "cost" of each change that depends on the character.
     * <p>
     * Since the letters A-P encode which of [Q,W,O,P] are pressed,
     * the transition between letters have different proportions of effects.
     * For example, "all off" (P) to "all on" (O) has a higher cost than "all off" (O) to "press Q" (A).
     */
    public static int modifiedLevenshtein(String s1, String s2) {
        if (0 == Math.min(s1.length(), s2.length())) {
            return Math.max(cost(s1), cost(s2));
        }
        return Math.min(
                Math.min(
                        cost(s1.charAt(0)) + modifiedLevenshtein(s1.substring(1), s2),
                        cost(s2.charAt(0)) + modifiedLevenshtein(s1, s2.substring(1))
                ),
                compareCost(s1.charAt(0), s2.charAt(0)) + modifiedLevenshtein(s1.substring(1), s2.substring(1))
        );
    }


    /**
     * Standard Levenshtein (edit distance) algorithm.
     * <p>
     * As written, it has poor asymptotic performance, but works so far.
     * See also https://en.wikipedia.org/wiki/Levenshtein_distance
     */
    public static int levenshtein(String s1, String s2) {
        if (0 == Math.min(s1.length(), s2.length())) {
            return Math.max(s1.length(), s2.length());
        }

        return Math.min(
                Math.min(
                        1 + levenshtein(s1.substring(1), s2),
                        1 + levenshtein(s1, s2.substring(1))
                ),
                (s1.charAt(0) == s2.charAt(0) ? 0 : 1) + levenshtein(s1.substring(1), s2.substring(1))
        );
    }

    /**
     * Compares cost between two letters from the A-P encoding.
     * <p>
     * It computes which QWOP letters are on and off, and counts the differences (via XOR).
     */
    static int compareCost(char letter1, char letter2) {
        return Integer.bitCount(letterMap.get(letter1) ^ letterMap.get(letter2));
    }

    /**
     * Cost of a letter in the A-P encoding.
     * <p>
     * This is the number of enabled inputs (of [Q,W,O,P]) encoded in the letter.
     * All letters should cost at least 1.
     */
    static int cost(char letter) {
        return Math.max(Integer.bitCount(letterMap.get(letter)), 1);
    }

    static int cost(String word) {
        int sum = 0;
        for (char l : word.toCharArray()) {
            sum += cost(l);
        }
        return sum;
    }
}

