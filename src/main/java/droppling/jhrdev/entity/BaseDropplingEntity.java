package droppling.jhrdev.entity;

import droppling.jhrdev.inventory.DropplingInventory;
import droppling.jhrdev.evaluator.ItemPriorityRegistry;
// DropplingItemEvaluator removed; use ItemEvaluator+ItemPriorityRegistry instead
import droppling.jhrdev.sensor.DropplingSensor;
import droppling.jhrdev.species.SoundProfile;
import droppling.jhrdev.species.SpeciesData;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;

public abstract class BaseDropplingEntity extends PathAwareEntity implements GeoEntity {

    private static final int STEP_SOUND_INTERVAL_TICKS = 19;
    private static final int DEFAULT_INVENTORY_CAPACITY = 5;

    protected final SpeciesData speciesData;
    private final DropplingInventory inventory = new DropplingInventory(DEFAULT_INVENTORY_CAPACITY);
    private final DropplingSensor sensor = new DropplingSensor();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int stepSoundCooldown = STEP_SOUND_INTERVAL_TICKS;

    protected BaseDropplingEntity(EntityType<? extends PathAwareEntity> entityType, World world, SpeciesData speciesData) {
        super(entityType, world);
        this.speciesData = speciesData;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    public static DefaultAttributeContainer.Builder createAttributes(SpeciesData speciesData) {
        var attributes = speciesData.attributes();

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, attributes.maxHealth())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, attributes.movementSpeed())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, attributes.attackDamage())
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, attributes.followRange());
    }

    public SpeciesData getSpeciesData() {
        return this.speciesData;
    }

    public DropplingInventory getInventory() {
        return this.inventory;
    }

    public DropplingSensor getSensor() {
        return this.sensor;
    }

    /**
     * Attempt to collect an ItemEntity into this entity's inventory.
     * Returns true if the item was successfully stored and removed from the world.
     */
    public boolean collectItem(ItemEntity itemEntity) {
        if (itemEntity == null || itemEntity.isRemoved() || !itemEntity.isAlive()) {
            return false;
        }

        if (this.getWorld().isClient) {
            return false;
        }

        ItemStack worldStack = itemEntity.getStack();
        if (worldStack.isEmpty()) {
            return false;
        }

        ItemStack transferStack = worldStack.copy();
        transferStack.setCount(1);

        if (!this.inventory.isFull()) {
            if (!this.inventory.addItem(transferStack)) {
                return false;
            }

            worldStack.decrement(1);
            if (worldStack.isEmpty()) {
                itemEntity.discard();
            }

            return true;
        }

        var prefs = this.speciesData.preferences();
        Identifier incomingId = Registries.ITEM.getId(transferStack.getItem());
        int incomingBase = ItemPriorityRegistry.getBaseValue(incomingId);
        double incomingPref = prefs.getPreference(transferStack.getItem());
        double incomingModifier = incomingPref - prefs.defaultValue();
        int incomingScore = Math.max(1, Math.min(100, (int)Math.round(incomingBase + incomingModifier)));

        int lowestScore = Integer.MAX_VALUE;
        int lowestIndex = -1;
        for (int index = 0; index < this.inventory.getItems().size(); index++) {
            ItemStack s = this.inventory.getItems().get(index);
            if (s == null || s.isEmpty()) {
                continue;
            }

            Identifier id = Registries.ITEM.getId(s.getItem());
            int base = ItemPriorityRegistry.getBaseValue(id);
            double pref = prefs.getPreference(s.getItem());
            double mod = pref - prefs.defaultValue();
            int currentScore = Math.max(1, Math.min(100, (int)Math.round(base + mod)));

            if (currentScore < lowestScore) {
                lowestScore = currentScore;
                lowestIndex = index;
            }
        }

        if (lowestIndex >= 0 && incomingScore > lowestScore) {
            ItemStack replaced = this.inventory.removeItem(lowestIndex);
            if (!replaced.isEmpty()) {
                this.dropStack(replaced);
                if (!this.inventory.addItem(transferStack)) {
                    // rollback if add failed unexpectedly
                    this.inventory.addItem(replaced);
                    return false;
                }

                worldStack.decrement(1);
                if (worldStack.isEmpty()) {
                    itemEntity.discard();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient || !this.isAlive()) {
            return;
        }

        if (--this.stepSoundCooldown <= 0) {
            SoundProfile soundProfile = this.speciesData.soundProfile();

            this.playSound(
                    soundProfile.step(),
                    0.4F,
                    1.0F + (this.random.nextFloat() - 0.5F) * 0.2F
            );

            this.stepSoundCooldown = STEP_SOUND_INTERVAL_TICKS;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
