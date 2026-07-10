package droppling.jhrdev.registry;

import droppling.jhrdev.Dropplings;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item DROPPLING_SPAWN_EGG =
            Registry.register(
                    Registries.ITEM,
                    Dropplings.id("droppling_spawn_egg"),
                    new SpawnEggItem(
                            ModEntities.DROPPLING,
                            0x7CCB5A, // color principal
                            0xA6E57A, // manchas
                            new Item.Settings()
                    )
            );

    public static final Item DROPPLING_ESSENCE =
            Registry.register(
                    Registries.ITEM,
                    Dropplings.id("droppling_essence"),
                    new Item(new Item.Settings())
            );

    public static void registerModItems() {

        Dropplings.LOGGER.info("Registrando objetos...");

        ItemGroupEvents.modifyEntriesEvent(net.minecraft.item.ItemGroups.SPAWN_EGGS)
                .register(entries -> entries.add(DROPPLING_SPAWN_EGG));
        ItemGroupEvents.modifyEntriesEvent(net.minecraft.item.ItemGroups.INGREDIENTS)
                .register(entries -> entries.add(DROPPLING_ESSENCE));
    }
}