package droppling.jhrdev.registry;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import droppling.jhrdev.Dropplings;
import droppling.jhrdev.species.EggColors;
import droppling.jhrdev.species.LootSettings;
import droppling.jhrdev.species.PreferenceSet;
import droppling.jhrdev.species.SoundProfile;
import droppling.jhrdev.species.SpeciesAttributes;
import droppling.jhrdev.species.SpeciesData;
import droppling.jhrdev.species.SpeciesIdentity;
import droppling.jhrdev.species.SpeciesPreferences;
import droppling.jhrdev.species.SpawnSettings;
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
                            Registries.ITEM.getId(Items.APPLE), 80.0D,
                            Registries.ITEM.getId(Items.SWEET_BERRIES), 65.0D,
                            Registries.ITEM.getId(Items.DIAMOND), 100.0D
                    )),
                    new PreferenceSet(Map.of())
            ),
            new SoundProfile(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    ModSounds.DROPPLING_PLOP
            )
    );

    private ModSpecies() {
    }
}
