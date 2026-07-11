package droppling.jhrdev.sensor;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;

public final class DropplingSensor {

    private static final double DEFAULT_SCAN_RADIUS = 8.0D;

    private final double scanRadius;

    public DropplingSensor() {
        this(DEFAULT_SCAN_RADIUS);
    }

    public DropplingSensor(double scanRadius) {
        this.scanRadius = Math.max(0.0D, scanRadius);
    }

    public List<ItemEntity> scan(Entity owner) {
        if (owner == null || owner.getWorld() == null) {
            return List.of();
        }

        Box searchBox = owner.getBoundingBox().expand(this.scanRadius);
        return owner.getWorld().getEntitiesByClass(ItemEntity.class, searchBox, itemEntity -> true);
    }
}
