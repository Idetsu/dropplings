package droppling.jhrdev.behavior;

import java.util.List;

import droppling.jhrdev.entity.BaseDropplingEntity;
import droppling.jhrdev.sensor.DropplingItemEvaluator;
import droppling.jhrdev.sensor.DropplingSensor;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;

public class CollectItemGoal extends Goal {

    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final double MOVE_SPEED = 1.0D;

    private final BaseDropplingEntity droppling;
    private final DropplingSensor sensor;
    private final DropplingItemEvaluator evaluator;

    private ItemEntity targetItem;
    private int scanCooldown;

    public CollectItemGoal(BaseDropplingEntity droppling) {
        this.droppling = droppling;
        this.sensor = droppling.getSensor();
        this.evaluator = droppling.getItemEvaluator();
    }

    @Override
    public boolean canStart() {
        if (this.droppling == null || this.droppling.isDead()) {
            return false;
        }

        return this.findBestTarget() != null;
    }

    @Override
    public void start() {
        this.targetItem = this.findBestTarget();
        this.scanCooldown = 0;
    }

    @Override
    public void tick() {
        if (this.targetItem == null || this.targetItem.isRemoved() || !this.targetItem.isAlive()) {
            this.targetItem = this.findBestTarget();
            if (this.targetItem == null) {
                return;
            }
        }

        if (this.droppling.getNavigation().isIdle()) {
            this.droppling.getNavigation().startMovingTo(this.targetItem, MOVE_SPEED);
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.targetItem == null || this.targetItem.isRemoved() || !this.targetItem.isAlive()) {
            return false;
        }

        return this.droppling.squaredDistanceTo(this.targetItem) > 1.0D;
    }

    @Override
    public void stop() {
        this.droppling.getNavigation().stop();
        this.targetItem = null;
        this.scanCooldown = 0;
    }

    private ItemEntity findBestTarget() {
        if (this.scanCooldown > 0) {
            this.scanCooldown--;
            return this.targetItem;
        }

        this.scanCooldown = SCAN_INTERVAL_TICKS;

        List<ItemEntity> detectedItems = this.sensor.scan(this.droppling);
        return this.evaluator.evaluate(detectedItems);
    }
}
