package droppling.jhrdev.species;

import java.util.Map;
import net.minecraft.util.Identifier;

public record PreferenceSet(
        Map<Identifier, Double> values
) {
}
