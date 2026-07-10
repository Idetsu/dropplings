package droppling.jhrdev.client.model;

import droppling.jhrdev.Dropplings;
import droppling.jhrdev.entity.DropplingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DropplingModel extends GeoModel<DropplingEntity> {

    @Override
    public Identifier getModelResource(DropplingEntity animatable) {
        return Dropplings.id("geo/droppling.geo.json");
    }

    @Override
    public Identifier getTextureResource(DropplingEntity animatable) {
        return Dropplings.id("textures/entity/poring.png");
    }

    @Override
    public Identifier getAnimationResource(DropplingEntity animatable) {
        return Dropplings.id("animations/droppling.animation.json");
    }
}