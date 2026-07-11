package droppling.jhrdev.sensor;

import java.util.List;

import droppling.jhrdev.species.SpeciesPreferences;
import net.minecraft.entity.ItemEntity;

public final class DropplingItemEvaluator {

    private final SpeciesPreferences speciesPreferences;

    public DropplingItemEvaluator(SpeciesPreferences speciesPreferences) {
        this.speciesPreferences = speciesPreferences;
    }

    public ItemEntity evaluate(List<ItemEntity> detectedItems) {
        if (detectedItems == null || detectedItems.isEmpty()) {
            return null;
        }

        ItemEntity bestItem = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (ItemEntity itemEntity : detectedItems) {
            if (itemEntity == null || itemEntity.getStack().isEmpty()) {
                continue;
            }

            double score = this.speciesPreferences.getPreference(itemEntity.getStack().getItem());
            if (score > bestScore) {
                bestScore = score;
                bestItem = itemEntity;
            }
        }

        return bestScore > 0.0D ? bestItem : null;
    }
}
