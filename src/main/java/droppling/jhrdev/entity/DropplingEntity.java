package droppling.jhrdev.entity;

import droppling.jhrdev.behavior.CollectItemGoal;
import droppling.jhrdev.registry.ModItems;
import droppling.jhrdev.registry.ModSpecies;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;


public class DropplingEntity extends BaseDropplingEntity {

    private int attackAnimationTicks;
    private boolean attackAnimationStarted;
    private boolean deathAnimationStarted;

    public DropplingEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world, ModSpecies.DROPPLING);
    }


    @Override
    protected void initGoals() {

        // ===== COMBATE =====

        // Ataca cuerpo a cuerpo cuando tiene objetivo.
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.5D, false));


        // ===== EXPLORACIÓN =====

        // Busca y se acerca a objetos preferidos.
        this.goalSelector.add(2, new CollectItemGoal(this));

        // Camina explorando el mundo.
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));


        // Observa jugadores cercanos.
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));


        // Mira alrededor cuando está tranquilo.
        this.goalSelector.add(5, new LookAroundGoal(this));


        // ===== DEFENSA =====

        // Ataca a quien lo golpee.
        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.attackAnimationTicks > 0) {
            this.attackAnimationTicks--;
        } else if (this.attackAnimationStarted) {
            this.attackAnimationStarted = false;
        }

        if (this.deathTime > 0 && !this.deathAnimationStarted) {
            this.deathAnimationStarted = true;
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit) {
            this.attackAnimationTicks = 10;
            this.attackAnimationStarted = true;
        }
        return hit;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        AnimationController<DropplingEntity> controller = new AnimationController<>(
                this,
                "controller",
                0,
                state -> {
                    if (this.deathAnimationStarted) {
                        state.setAndContinue(RawAnimation.begin().thenPlay("death"));
                        return PlayState.CONTINUE;
                    }

                    if (this.attackAnimationStarted && this.attackAnimationTicks > 0) {
                        state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
                        return PlayState.CONTINUE;
                    }

                    var jumpSettings = this.getSpeciesData().movementProfile().slimeJump();

                    if (jumpSettings.enabled()) {
                        if (state.isMoving()) {
                            state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
                        } else {
                            state.setAndContinue(RawAnimation.begin().thenLoop("idle_static"));
                        }
                    } else {
                        state.setAndContinue(
                                RawAnimation.begin().thenLoop(state.isMoving() ? "walk" : "idle")
                        );
                    }

                    return PlayState.CONTINUE;
                }
        );

        controllers.add(controller);
    }


    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {

        super.dropLoot(source, causedByPlayer);

        // Drop all stored inventory items (100% chance)
        for (var stack : this.getInventory().getItems()) {
            if (stack != null && !stack.isEmpty()) {
                this.dropStack(stack);
            }
        }

        // Natural loot: 50% chance to drop Droppling essence.
        // Quantity: 1-3 normally; Looting can increase up to 4 total.
        int looting = 0;
        if (source.getAttacker() instanceof PlayerEntity player) {
            looting = EnchantmentHelper.getLevel(Enchantments.LOOTING, player.getMainHandStack());
        }

        int max = Math.min(3 + looting, 4);
        if (this.random.nextFloat() < 0.5F) {
            int count = this.random.nextInt(max) + 1;
            for (int i = 0; i < count; i++) {
                this.dropStack(new ItemStack(ModItems.DROPPLING_ESSENCE));
            }
        }
    }
}
