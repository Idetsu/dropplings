package droppling.jhrdev.species;

public record SpeciesData(
        SpeciesIdentity identity,
        EggColors eggColors,
        SpeciesAttributes attributes,
        SpawnSettings spawnSettings,
        LootSettings lootSettings,
        SpeciesPreferences preferences,
        SoundProfile soundProfile
) {
}
