package droppling.jhrdev.species;

public record SpeciesData(
        SpeciesIdentity identity,
        EggColors eggColors,
        SpeciesAttributes attributes,
        MovementProfile movementProfile,
        SpawnSettings spawnSettings,
        LootSettings lootSettings,
        SpeciesPreferences preferences,
        SoundProfile soundProfile,
        RenderProfile renderProfile,
        ItemRenderConfig itemRenderConfig
) {
}
