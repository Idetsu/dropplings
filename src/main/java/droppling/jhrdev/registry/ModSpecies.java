package droppling.jhrdev.registry;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import droppling.jhrdev.Dropplings;
import droppling.jhrdev.species.EggColors;
import droppling.jhrdev.species.ItemRenderConfig;
import droppling.jhrdev.species.ItemRenderPosition;
import droppling.jhrdev.species.LootSettings;
import droppling.jhrdev.species.MovementProfile;
import droppling.jhrdev.species.PreferenceSet;
import droppling.jhrdev.species.SoundProfile;
import droppling.jhrdev.species.SpeciesAttributes;
import droppling.jhrdev.species.SpeciesData;
import droppling.jhrdev.species.SpeciesIdentity;
import droppling.jhrdev.species.SpeciesPreferences;
import droppling.jhrdev.species.SpawnSettings;
import droppling.jhrdev.species.BoneRenderSettings;
import droppling.jhrdev.species.RenderMode;
import droppling.jhrdev.species.RenderProfile;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public final class ModSpecies {

    public static final SpeciesData DROPPLING = new SpeciesData(
            new SpeciesIdentity(
                    Dropplings.id("droppling"),
                    "entity.dropplings.droppling"
            ),
            new EggColors(
                    0x7CCB5A,
                    0xA6E57A
            ),
            new SpeciesAttributes(
                    10.0D,
                    0.25D,
                    2.0D,
                    16.0D
            ),
            MovementProfile.standard(),
            new SpawnSettings(
                    Set.of(),
                    0,
                    1,
                    1,
                    0,
                    15
            ),
            new LootSettings(
                    Dropplings.id("loot_tables/entities/droppling"),
                    1.0F
            ),
            new SpeciesPreferences(
                    new PreferenceSet(Map.of(
                            Registries.ITEM.getId(Items.DIAMOND), 8.0D,
                            Registries.ITEM.getId(Items.EMERALD), 7.0D,
                            Registries.ITEM.getId(Items.SLIME_BALL), 5.0D,
                            Registries.ITEM.getId(Items.IRON_INGOT), 4.0D,
                            Registries.ITEM.getId(Items.GOLD_INGOT), 5.0D
                    )),
                    new PreferenceSet(Map.of()),
                    1.0D
            ),
            new SoundProfile(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    ModSounds.DROPPLING_PLOP
            ),
            new RenderProfile(
                    Dropplings.id("textures/entity/poring.png"),
                    Map.of(
                            "face", BoneRenderSettings.of(RenderMode.CUTOUT, 1.0F),
                            "body", BoneRenderSettings.of(RenderMode.TRANSLUCENT, 0.65F),
                            "essence", BoneRenderSettings.of(RenderMode.TRANSLUCENT, 1.0F)
                    )
            ),
            createDefaultItemRenderConfig()
    );

    private static ItemRenderConfig createDefaultItemRenderConfig() {
        ItemRenderPosition[] positions = new ItemRenderPosition[5];
        positions[0] = ItemRenderPosition.of(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4F, 0.5F);
        positions[1] = ItemRenderPosition.of(0.15F, 0.25F, 0.1F, 0.0F, 0.0F, 0.0F, 0.35F, 0.6F);
        positions[2] = ItemRenderPosition.of(-0.15F, 0.35F, 0.05F, 0.0F, 0.0F, 0.0F, 0.38F, 0.4F);
        positions[3] = ItemRenderPosition.of(0.1F, 0.4F, -0.1F, 0.0F, 0.0F, 0.0F, 0.32F, 0.7F);
        positions[4] = ItemRenderPosition.of(-0.1F, 0.2F, -0.05F, 0.0F, 0.0F, 0.0F, 0.36F, 0.55F);

        return ItemRenderConfig.of(positions);
    }

    private ModSpecies() {
    }
}
