package droppling.jhrdev.species;

import java.util.Map;
import net.minecraft.util.Identifier;

public record RenderProfile(
        Identifier defaultTexture,
        Map<String, BoneRenderSettings> bones
) {
    public BoneRenderSettings getBoneSettings(String boneName) {
        return this.bones.get(boneName);
    }
}
