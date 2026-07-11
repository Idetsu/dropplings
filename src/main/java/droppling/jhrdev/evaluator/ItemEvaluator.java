package droppling.jhrdev.evaluator;

import java.util.List;

import droppling.jhrdev.entity.BaseDropplingEntity;
import droppling.jhrdev.species.SpeciesPreferences;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

/**
 * Determines which ItemEntity a Droppling should target based on base values
 * and species preferences. Does not perform any movement or collection.
 */
public final class ItemEvaluator {

    private ItemEvaluator() {}

    private static int clampScore(int v) {
        if (v < 1) return 1;
        if (v > 100) return 100;
        return v;
    }

    private static int computeScore(ItemEntity itemEntity, SpeciesPreferences prefs) {
        Item item = itemEntity.getStack().getItem();
        Identifier id = Registries.ITEM.getId(item);
        int base = ItemPriorityRegistry.getBaseValue(id);

        // SpeciesPreferences in this codebase exposes absolute numbers used historically.
        // Treat species preference as a modifier relative to its defaultValue to avoid
        // changing existing data formats: modifier = preference - defaultValue
        double speciesPref = prefs.getPreference(item);
        double modifier = speciesPref - prefs.defaultValue();

        int result = (int) Math.round(base + modifier);
        return clampScore(result);
    }

    /**
     * Evaluate the detected items and return the best ItemEntity to pursue, or null.
     * Rules:
     * - If inventory is not full: choose the visible item with highest final score.
     * - If inventory is full: only choose an item whose score > lowest-scored inventory item.
     */
    public static ItemEntity evaluate(List<ItemEntity> detectedItems, BaseDropplingEntity droppling) {
        if (detectedItems == null || detectedItems.isEmpty() || droppling == null) {
            return null;
        }

        SpeciesPreferences prefs = droppling.getSpeciesData().preferences();

        // compute inventory lowest score if full
        boolean inventoryFull = droppling.getInventory().isFull();
        int lowestInventoryScore = Integer.MAX_VALUE;
        if (inventoryFull) {
            for (var stack : droppling.getInventory().getItems()) {
                if (stack == null || stack.isEmpty()) continue;
                Identifier id = Registries.ITEM.getId(stack.getItem());
                int s = ItemPriorityRegistry.getBaseValue(id);
                double speciesPref = prefs.getPreference(stack.getItem());
                double modifier = speciesPref - prefs.defaultValue();
                int finalScore = clampScore((int)Math.round(s + modifier));
                if (finalScore < lowestInventoryScore) lowestInventoryScore = finalScore;
            }
            if (lowestInventoryScore == Integer.MAX_VALUE) lowestInventoryScore = 0;
        }

        ItemEntity best = null;
        int bestScore = Integer.MIN_VALUE;

        for (ItemEntity ie : detectedItems) {
            if (ie == null || ie.getStack().isEmpty() || !ie.isAlive()) continue;

            ItemStack candidate = ie.getStack().copy();
            candidate.setCount(1);
            if (droppling.getInventory().containsEquivalent(candidate)) {
                continue;
            }

            int score = computeScore(ie, prefs);

            if (inventoryFull) {
                if (score <= lowestInventoryScore) continue; // won't replace anything
            }

            if (score > bestScore) {
                bestScore = score;
                best = ie;
            }
        }

        return best;
    }
}
