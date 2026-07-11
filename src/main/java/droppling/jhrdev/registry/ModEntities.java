package droppling.jhrdev.registry;

import droppling.jhrdev.Dropplings;
import droppling.jhrdev.entity.BaseDropplingEntity;
import droppling.jhrdev.entity.DropplingEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {

    public static final EntityType<DropplingEntity> DROPPLING =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    ModSpecies.DROPPLING.identity().id(),
                    FabricEntityTypeBuilder.createMob()
                            .entityFactory(DropplingEntity::new)
                            .spawnGroup(SpawnGroup.CREATURE)
                            .dimensions(EntityDimensions.fixed(0.8f, 0.8f))
                            .defaultAttributes(() -> BaseDropplingEntity.createAttributes(ModSpecies.DROPPLING))
                            .build()
            );

    public static void registerModEntities() {
        Dropplings.LOGGER.info("Registrando entidades...");
    }
}
