package droppling.jhrdev.evaluator;

import java.util.Map;
import java.util.Optional;

import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

/**
 * Registry of base priority values for items.
 * Values are in the range 1..100. Unknown items return DEFAULT_VALUE.
 */
public final class ItemPriorityRegistry {

    public static final int DEFAULT_VALUE = 5;

    private static final Map<Identifier, Integer> BASE_VALUES = Map.ofEntries(
            Map.entry(Registries.ITEM.getId(Items.DIAMOND), 80),
            Map.entry(Registries.ITEM.getId(Items.EMERALD), 75),
            Map.entry(Registries.ITEM.getId(Items.SLIME_BALL), 30),
            Map.entry(Registries.ITEM.getId(Items.IRON_INGOT), 40),
            Map.entry(Registries.ITEM.getId(Items.GOLD_INGOT), 45),
            Map.entry(Registries.ITEM.getId(Items.APPLE), 40)
    );

    private ItemPriorityRegistry() {}

    public static int getBaseValue(Identifier id) {
        return Optional.ofNullable(BASE_VALUES.get(id)).orElse(DEFAULT_VALUE);
    }
}
