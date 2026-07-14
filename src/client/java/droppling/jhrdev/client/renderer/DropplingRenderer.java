package droppling.jhrdev.client.renderer;

import droppling.jhrdev.client.model.DropplingModel;
import droppling.jhrdev.client.renderer.layer.InventoryRenderLayer;
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

    private static final int FULL_BRIGHT_LIGHT = 15728880; // LightmapTextureManager.MAX_LIGHTMAP_COORDINATES

    public DropplingRenderer(EntityRendererFactory.Context context) {
        super(context, new DropplingModel());
        this.shadowRadius = 0.35F;
        this.addRenderLayer(new InventoryRenderLayer(this, context));
    }

    @Override
    @Nullable
    protected Identifier getTextureOverrideForBone(GeoBone bone, DropplingEntity animatable, float partialTick) {
        BoneRenderSettings settings = resolveBoneSettings(bone, animatable);
        if (settings != null && settings.texture().isPresent()) {
            return settings.texture().get();
        }
        return null;
    }

    @Override
    @Nullable
    protected RenderLayer getRenderTypeOverrideForBone(GeoBone bone, DropplingEntity animatable, Identifier texturePath, VertexConsumerProvider bufferSource, float partialTick) {
        BoneRenderSettings settings = resolveBoneSettings(bone, animatable);
        if (settings != null) {
            return toRenderLayer(settings.renderMode(), texturePath);
        }
        return null;
    }

    @Override
    protected boolean boneRenderOverride(MatrixStack poseStack, GeoBone bone, VertexConsumerProvider bufferSource, VertexConsumer buffer,
                                         float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        BoneRenderSettings settings = resolveBoneSettings(bone, this.animatable);
        if (settings != null) {
            if (!settings.visible()) {
                return true;
            }

            float targetAlpha = alpha * settings.alpha();
            int targetLight = applyEmissiveStrength(packedLight, settings.emissiveStrength());

            super.renderCubesOfBone(poseStack, bone, buffer, targetLight, packedOverlay, red, green, blue, targetAlpha);

            return true;
        }
        return false;
    }

    @Nullable
    private static BoneRenderSettings resolveBoneSettings(GeoBone bone, DropplingEntity animatable) {
        RenderProfile profile = animatable.getSpeciesData().renderProfile();
        return profile.getBoneSettings(bone.getName());
    }

    @Nullable
    private static RenderLayer toRenderLayer(RenderMode renderMode, Identifier texturePath) {
        return switch (renderMode) {
            case OPAQUE -> RenderLayer.getEntitySolid(texturePath);
            case CUTOUT -> RenderLayer.getEntityCutoutNoCull(texturePath);
            case TRANSLUCENT -> RenderLayer.getEntityTranslucent(texturePath);
        };
    }

    private static int applyEmissiveStrength(int packedLight, float emissiveStrength) {
        if (emissiveStrength <= 0.0F) {
            return packedLight;
        }
        if (emissiveStrength >= 1.0F) {
            return FULL_BRIGHT_LIGHT;
        }
        return (int) (packedLight + (FULL_BRIGHT_LIGHT - packedLight) * emissiveStrength);
    }
}
