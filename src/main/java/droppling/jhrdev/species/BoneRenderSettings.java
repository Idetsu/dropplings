package droppling.jhrdev.species;

import java.util.Optional;
import net.minecraft.util.Identifier;

/**
 * Per-bone render configuration. The bone name is the key in {@link RenderProfile#bones()};
 * it is intentionally not stored here to avoid duplication.
 */
public record BoneRenderSettings(
        Optional<Identifier> texture,
        RenderMode renderMode,
        float alpha,
        float emissiveStrength,
        boolean visible
) {
    private static final float MIN = 0.0F;
    private static final float MAX = 1.0F;

    public BoneRenderSettings {
        alpha = clamp(alpha);
        emissiveStrength = clamp(emissiveStrength);
    }

    public static BoneRenderSettings of(RenderMode renderMode, float alpha) {
        return of(renderMode, alpha, MIN);
    }

    public static BoneRenderSettings of(RenderMode renderMode, float alpha, float emissiveStrength) {
        return new BoneRenderSettings(Optional.empty(), renderMode, alpha, emissiveStrength, true);
    }

    public static BoneRenderSettings textureOverride(Identifier texture, RenderMode renderMode, float alpha) {
        return textureOverride(texture, renderMode, alpha, MIN);
    }

    public static BoneRenderSettings textureOverride(Identifier texture, RenderMode renderMode, float alpha, float emissiveStrength) {
        return new BoneRenderSettings(Optional.of(texture), renderMode, alpha, emissiveStrength, true);
    }

    public static BoneRenderSettings hidden(RenderMode renderMode) {
        return new BoneRenderSettings(Optional.empty(), renderMode, MIN, MIN, false);
    }

    private static float clamp(float value) {
        return Math.max(MIN, Math.min(MAX, value));
    }
}
