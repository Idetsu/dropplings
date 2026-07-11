package droppling.jhrdev.species;

import net.minecraft.registry.Registries;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public record SpeciesPreferences(
        PreferenceSet basePreferences,
        PreferenceSet randomPreferences
) {
    public double getPreference(Identifier itemId) {
        return this.basePreferences.getPreference(itemId) + this.randomPreferences.getPreference(itemId);
    }

    public double getPreference(Item item) {
        return this.getPreference(Registries.ITEM.getId(item));
    }
}
