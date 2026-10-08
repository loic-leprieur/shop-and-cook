package be.vives.loic.shopandcook.models;

import java.util.ArrayList;

/**
 * Turns the free-text method of a recipe into a list of cooking steps.
 */
public final class RecipeSteps {
    private RecipeSteps() {
    }

    // TheMealDB gives the method as one text block: one step per non-empty line, ignoring
    // "STEP 1"-style headings; a single-paragraph method is split on sentences instead.
    public static ArrayList<String> split(String instructions) {
        ArrayList<String> result = new ArrayList<>();
        if (instructions == null) {
            return result;
        }
        for (String line : instructions.split("\\r?\\n")) {
            String step = line.trim();
            if (step.isEmpty() || step.matches("(?i)(step\\s*)?\\d+[.:)]?")) {
                continue;
            }
            result.add(step);
        }
        if (result.size() == 1) {
            ArrayList<String> sentences = new ArrayList<>();
            for (String sentence : result.get(0).split("(?<=[.!?])\\s+")) {
                if (!sentence.trim().isEmpty()) {
                    sentences.add(sentence.trim());
                }
            }
            return sentences;
        }
        return result;
    }
}
