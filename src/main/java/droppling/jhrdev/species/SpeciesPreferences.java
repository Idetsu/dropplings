package droppling.jhrdev.species;

import net.minecraft.registry.Registries;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public record SpeciesPreferences(
        PreferenceSet basePreferences,
        PreferenceSet randomPreferences,
        double defaultValue
) {
    public double getPreference(Identifier itemId) {
        double base = this.basePreferences.values().getOrDefault(itemId, this.defaultValue);
        double random = this.randomPreferences.getPreference(itemId);
        return base + random;
    }

    public double getPreference(Item item) {
        return this.getPreference(Registries.ITEM.getId(item));
    }
}
