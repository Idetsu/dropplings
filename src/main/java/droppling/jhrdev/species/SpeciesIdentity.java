package droppling.jhrdev.species;

import net.minecraft.util.Identifier;

public record SpeciesIdentity(
        Identifier id,
        String translationKey
) {
}
