package droppling.jhrdev.species;

import java.util.Optional;
import net.minecraft.util.Identifier;

public record BoneRenderSettings(
        String boneName,
        Optional<Identifier> texture,
        RenderMode renderMode,
        float alpha,
        boolean emissive,
        boolean visible
) {
    public static BoneRenderSettings of(String boneName, RenderMode renderMode, float alpha) {
        return new BoneRenderSettings(boneName, Optional.empty(), renderMode, alpha, false, true);
    }
    
    public static BoneRenderSettings emissive(String boneName, RenderMode renderMode, float alpha) {
        return new BoneRenderSettings(boneName, Optional.empty(), renderMode, alpha, true, true);
    }
    
    public static BoneRenderSettings textureOverride(String boneName, Identifier texture, RenderMode renderMode, float alpha) {
        return new BoneRenderSettings(boneName, Optional.of(texture), renderMode, alpha, false, true);
    }
}
