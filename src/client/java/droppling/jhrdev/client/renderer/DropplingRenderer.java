package droppling.jhrdev.client.renderer;

import droppling.jhrdev.client.model.DropplingModel;
import droppling.jhrdev.entity.DropplingEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DropplingRenderer extends GeoEntityRenderer<DropplingEntity> {

    public DropplingRenderer(EntityRendererFactory.Context context) {
        super(context, new DropplingModel());

        this.shadowRadius = 0.35F;
    }
}