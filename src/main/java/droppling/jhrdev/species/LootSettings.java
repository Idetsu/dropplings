package droppling.jhrdev.species;

import net.minecraft.util.Identifier;

public record LootSettings(
        Identifier lootTable,
        float essenceDropChance
) {
}
