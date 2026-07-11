package droppling.jhrdev.species;

import java.util.Map;
import net.minecraft.util.Identifier;

public record PreferenceSet(
        Map<Identifier, Double> values
) {
    public double getPreference(Identifier itemId) {
        return this.values.getOrDefault(itemId, 0.0D);
    }
}
