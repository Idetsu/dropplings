package droppling.jhrdev.entity;

import droppling.jhrdev.registry.ModItems;
import droppling.jhrdev.registry.ModSounds;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;


public class DropplingEntity extends PathAwareEntity implements GeoEntity {

    private static final int PLOP_SOUND_INTERVAL_TICKS = 19;

    private final AnimatableInstanceCache cache =
            new SingletonAnimatableInstanceCache(this);

    private int plopSoundCooldown = PLOP_SOUND_INTERVAL_TICKS;


    public DropplingEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);

        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }


    public static DefaultAttributeContainer.Builder createDropplingAttributes() {

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D);
    }


    @Override
    protected void initGoals() {

        // ===== COMBATE =====

        // Ataca cuerpo a cuerpo cuando tiene objetivo.
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, false));


        // ===== EXPLORACIÓN =====

        // Camina explorando el mundo.
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));


        // Observa jugadores cercanos.
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));


        // Mira alrededor cuando está tranquilo.
        this.goalSelector.add(4, new LookAroundGoal(this));


        // ===== DEFENSA =====

        // Ataca a quien lo golpee.
        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient || !this.isAlive()) {
            return;
        }

        if (--this.plopSoundCooldown <= 0) {
            this.playSound(
                    ModSounds.DROPPLING_PLOP,
                    0.4F,
                    1.0F + (this.random.nextFloat() - 0.5F) * 0.2F
            );

            this.plopSoundCooldown = PLOP_SOUND_INTERVAL_TICKS;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        AnimationController<DropplingEntity> controller = new AnimationController<>(
                this,
                "controller",
                0,
                state -> {
                    return state.setAndContinue(
                            RawAnimation.begin().thenLoop(state.isMoving() ? "walk" : "idle")
                    );
                }
        );

        controllers.add(controller);
    }


    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {

        super.dropLoot(source, causedByPlayer);

        this.dropStack(
                new ItemStack(ModItems.DROPPLING_ESSENCE)
        );
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {

        return cache;
    }
}
