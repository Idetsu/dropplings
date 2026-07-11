package droppling.jhrdev.entity;

import droppling.jhrdev.behavior.CollectItemGoal;
import droppling.jhrdev.registry.ModSpecies;
import droppling.jhrdev.registry.ModItems;

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

    public DropplingEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world, ModSpecies.DROPPLING);
    }


    @Override
    protected void initGoals() {

        // ===== COMBATE =====

        // Ataca cuerpo a cuerpo cuando tiene objetivo.
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, false));


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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        AnimationController<DropplingEntity> controller = new AnimationController<>(
                this,
                "controller",
                0,
                state -> {
                    state.setAndContinue(
                            RawAnimation.begin().thenLoop(state.isMoving() ? "walk" : "idle")
                    );

                    return PlayState.CONTINUE;
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
}
