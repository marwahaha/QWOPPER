package com.slowfrog.qwop;

import java.util.Random;

public class DefaultMutationStrategy implements MutationStrategy {
    private static final String DEFAULT_NOTES = "ABCDEFGHIJKLMNOP"; // Encoding 2

    private final String notes;
    private final int notesLength;

    public DefaultMutationStrategy() {
        this(DEFAULT_NOTES);
    }

    public DefaultMutationStrategy(String notes) {
        this.notes = notes;
        this.notesLength = notes.length();
    }

    @Override
    public String mutate(String runner) {
        Random random = new Random(System.currentTimeMillis());
        int mutateLocation = random.nextInt(runner.length());
        int mutationIndex = random.nextInt(notesLength);
        String theMutation = notes.substring(mutationIndex, mutationIndex + 1);

        return runner.substring(0, mutateLocation) + theMutation + runner.substring(mutateLocation + 1);
    }

}
