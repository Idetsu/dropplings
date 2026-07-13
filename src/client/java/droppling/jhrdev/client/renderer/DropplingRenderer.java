package droppling.jhrdev.client.renderer;

import droppling.jhrdev.client.model.DropplingModel;
import droppling.jhrdev.entity.DropplingEntity;
import droppling.jhrdev.species.BoneRenderSettings;
import droppling.jhrdev.species.RenderMode;
import droppling.jhrdev.species.RenderProfile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;

public class DropplingRenderer extends DynamicGeoEntityRenderer<DropplingEntity> {

    public DropplingRenderer(EntityRendererFactory.Context context) {
        super(context, new DropplingModel());
        this.shadowRadius = 0.35F;
    }

    @Override
    @Nullable
    protected Identifier getTextureOverrideForBone(GeoBone bone, DropplingEntity animatable, float partialTick) {
        RenderProfile profile = animatable.getSpeciesData().renderProfile();
        BoneRenderSettings settings = profile.getBoneSettings(bone.getName());
        if (settings != null && settings.texture().isPresent()) {
            return settings.texture().get();
        }
        return null;
    }

    @Override
    @Nullable
    protected RenderLayer getRenderTypeOverrideForBone(GeoBone bone, DropplingEntity animatable, Identifier texturePath, VertexConsumerProvider bufferSource, float partialTick) {
        RenderProfile profile = animatable.getSpeciesData().renderProfile();
        BoneRenderSettings settings = profile.getBoneSettings(bone.getName());
        if (settings != null) {
            switch (settings.renderMode()) {
                case OPAQUE:
                    return RenderLayer.getEntitySolid(texturePath);
                case CUTOUT:
                    return RenderLayer.getEntityCutoutNoCull(texturePath);
                case TRANSLUCENT:
                    return RenderLayer.getEntityTranslucent(texturePath);
                case EMISSIVE:
                    return RenderLayer.getEyes(texturePath);
            }
        }
        return null;
    }

    @Override
    protected boolean boneRenderOverride(MatrixStack poseStack, GeoBone bone, VertexConsumerProvider bufferSource, VertexConsumer buffer,
                                         float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RenderProfile profile = this.animatable.getSpeciesData().renderProfile();
        BoneRenderSettings settings = profile.getBoneSettings(bone.getName());
        if (settings != null) {
            if (!settings.visible()) {
                return true;
            }

            float targetAlpha = alpha * settings.alpha();
            int targetLight = settings.emissive() ? 15728880 : packedLight; // 15728880 = LightmapTextureManager.MAX_LIGHTMAP_COORDINATES

            super.renderCubesOfBone(poseStack, bone, buffer, targetLight, packedOverlay, red, green, blue, targetAlpha);
            return true;
        }
        return false;
    }
}