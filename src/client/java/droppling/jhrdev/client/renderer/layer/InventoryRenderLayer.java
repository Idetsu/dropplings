package droppling.jhrdev.client.renderer.layer;

import droppling.jhrdev.client.renderer.DropplingRenderer;
import droppling.jhrdev.entity.DropplingEntity;
import droppling.jhrdev.species.BoneRenderSettings;
import droppling.jhrdev.species.ItemRenderConfig;
import droppling.jhrdev.species.ItemRenderPosition;
import droppling.jhrdev.species.RenderProfile;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public final class InventoryRenderLayer extends GeoRenderLayer<DropplingEntity> {

    private final ItemRenderer itemRenderer;

    public InventoryRenderLayer(DropplingRenderer renderer, EntityRendererFactory.Context context) {
        super(renderer);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void renderForBone(MatrixStack poseStack, DropplingEntity animatable, GeoBone bone, net.minecraft.client.render.RenderLayer renderType,
                              VertexConsumerProvider bufferSource, net.minecraft.client.render.VertexConsumer buffer, float partialTick,
                              int packedLight, int packedOverlay) {
        if (!"body".equals(bone.getName())) {
            return;
        }

        BoneRenderSettings settings = resolveBoneSettings(animatable, bone);
        if (settings != null && !settings.visible()) {
            return;
        }

        ItemStack[] items = animatable.getInventoryForRender();
        if (items.length == 0) {
            return;
        }

        ItemRenderConfig config = animatable.getSpeciesData().itemRenderConfig();
        ItemRenderPosition[] positions = config.slotPositions();

        for (int slot = 0; slot < Math.min(items.length, positions.length); slot++) {
            ItemStack stack = items[slot];
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            renderItemInBody(stack, positions[slot], poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderItemInBody(ItemStack stack, ItemRenderPosition position, MatrixStack poseStack, VertexConsumerProvider bufferSource,
                                  int packedLight, int packedOverlay) {
        poseStack.push();

        poseStack.translate(position.offsetX(), position.offsetY(), position.offsetZ());

        float time = (System.currentTimeMillis() / 1000f) * position.rotationSpeed();
        poseStack.multiply(new org.joml.Quaternionf().rotationX((float)Math.toRadians(position.rotationX() + time)));
        poseStack.multiply(new org.joml.Quaternionf().rotationY((float)Math.toRadians(position.rotationY() + time * 0.7f)));
        poseStack.multiply(new org.joml.Quaternionf().rotationZ((float)Math.toRadians(position.rotationZ() + time * 0.5f)));

        poseStack.scale(position.scale(), position.scale(), position.scale());

        BakedModel model = this.itemRenderer.getModels().getModel(stack.getItem());
        this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, false, poseStack, bufferSource, packedLight, packedOverlay, model);

        poseStack.pop();
    }

    @Nullable
    private static BoneRenderSettings resolveBoneSettings(DropplingEntity animatable, GeoBone bone) {
        RenderProfile profile = animatable.getSpeciesData().renderProfile();
        return profile.getBoneSettings(bone.getName());
    }
}
