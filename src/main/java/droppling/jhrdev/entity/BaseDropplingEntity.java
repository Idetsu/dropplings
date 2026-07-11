package droppling.jhrdev.entity;

import droppling.jhrdev.inventory.DropplingInventory;
import droppling.jhrdev.species.SoundProfile;
import droppling.jhrdev.species.SpeciesData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;

public abstract class BaseDropplingEntity extends PathAwareEntity implements GeoEntity {

    private static final int STEP_SOUND_INTERVAL_TICKS = 19;
    private static final int DEFAULT_INVENTORY_CAPACITY = 5;

    protected final SpeciesData speciesData;
    private final DropplingInventory inventory = new DropplingInventory(DEFAULT_INVENTORY_CAPACITY);
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
