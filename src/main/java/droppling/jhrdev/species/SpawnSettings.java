package droppling.jhrdev.species;

import java.util.Set;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public record SpawnSettings(
        Set<RegistryKey<Biome>> spawnBiomes,
        int weight,
        int minGroupSize,
        int maxGroupSize,
        int minLightLevel,
        int maxLightLevel
) {
}
