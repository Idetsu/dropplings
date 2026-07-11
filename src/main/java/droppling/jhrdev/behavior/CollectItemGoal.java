package droppling.jhrdev.behavior;

import java.util.List;

import droppling.jhrdev.entity.BaseDropplingEntity;
import droppling.jhrdev.sensor.DropplingSensor;
import droppling.jhrdev.evaluator.ItemEvaluator;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;

public class CollectItemGoal extends Goal {

    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final double MOVE_SPEED = 1.0D;
    private static final double PICKUP_DISTANCE = 1.75D; // blocks

    private final BaseDropplingEntity droppling;
    private DropplingSensor sensor;

    private ItemEntity targetItem;
    private int scanCooldown;

    public CollectItemGoal(BaseDropplingEntity droppling) {
        this.droppling = droppling;
        this.sensor = null; // lazy-resolved to avoid init ordering NPE
        // evaluator removed; selection delegated to ItemEvaluator
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

        // If we're within pickup range, delegate collection to the entity
        double pickupRangeSq = PICKUP_DISTANCE * PICKUP_DISTANCE;
        if (this.droppling.squaredDistanceTo(this.targetItem) <= pickupRangeSq) {
            boolean collected = this.droppling.collectItem(this.targetItem);
            if (collected) {
                // item removed or partially collected; clear target and stop navigation
                this.targetItem = null;
                this.droppling.getNavigation().stop();
                return;
            }

            // The target is not collectible or already duplicated; abandon it so a new target can be selected.
            this.targetItem = null;
            this.droppling.getNavigation().stop();
            return;
        }

        if (this.droppling.getNavigation().isIdle()) {
            this.droppling.getNavigation().startMovingTo(this.targetItem, MOVE_SPEED);
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.targetItem != null && !this.targetItem.isRemoved() && this.targetItem.isAlive();
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
        DropplingSensor sensor = this.sensor == null ? this.droppling.getSensor() : this.sensor;

        if (sensor == null) {
            return null;
        }

        // cache resolved instances for future ticks
        this.sensor = sensor;

        List<ItemEntity> detectedItems = sensor.scan(this.droppling);

        // Use new ItemEvaluator which combines base registry values and species preferences
        return ItemEvaluator.evaluate(detectedItems, this.droppling);
    }
}
