package droppling.jhrdev.entity;

import droppling.jhrdev.inventory.DropplingInventory;
import droppling.jhrdev.evaluator.ItemPriorityRegistry;
import droppling.jhrdev.sensor.DropplingSensor;
import droppling.jhrdev.species.SoundProfile;
import droppling.jhrdev.species.SpeciesData;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;

public abstract class BaseDropplingEntity extends PathAwareEntity implements GeoEntity {

    private static final int STEP_SOUND_INTERVAL_TICKS = 32;
    private static final int DEFAULT_INVENTORY_CAPACITY = 5;
    private static final String NBT_INVENTORY_KEY = "DropplingInventory";
    private static final TrackedData<ItemStack> INVENTORY_SLOT_0 =
            DataTracker.registerData(BaseDropplingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> INVENTORY_SLOT_1 =
            DataTracker.registerData(BaseDropplingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> INVENTORY_SLOT_2 =
            DataTracker.registerData(BaseDropplingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> INVENTORY_SLOT_3 =
            DataTracker.registerData(BaseDropplingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<ItemStack> INVENTORY_SLOT_4 =
            DataTracker.registerData(BaseDropplingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    protected final SpeciesData speciesData;
    private final DropplingInventory inventory = new DropplingInventory(DEFAULT_INVENTORY_CAPACITY);
    private final DropplingSensor sensor = new DropplingSensor();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int stepSoundCooldown = STEP_SOUND_INTERVAL_TICKS;

    private int jumpCooldown = 0;

    protected BaseDropplingEntity(EntityType<? extends PathAwareEntity> entityType, World world, SpeciesData speciesData) {
        super(entityType, world);
        this.speciesData = speciesData;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INVENTORY_SLOT_0, ItemStack.EMPTY);
        this.dataTracker.startTracking(INVENTORY_SLOT_1, ItemStack.EMPTY);
        this.dataTracker.startTracking(INVENTORY_SLOT_2, ItemStack.EMPTY);
        this.dataTracker.startTracking(INVENTORY_SLOT_3, ItemStack.EMPTY);
        this.dataTracker.startTracking(INVENTORY_SLOT_4, ItemStack.EMPTY);
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

    public ItemStack[] getInventoryForRender() {
        TrackedData<ItemStack>[] slots = new TrackedData[] {
            INVENTORY_SLOT_0, INVENTORY_SLOT_1, INVENTORY_SLOT_2, INVENTORY_SLOT_3, INVENTORY_SLOT_4
        };
        ItemStack[] items = new ItemStack[slots.length];
        for (int i = 0; i < slots.length; i++) {
            items[i] = this.dataTracker.get(slots[i]);
        }
        return items;
    }

    private void syncInventory() {
        if (this.getWorld().isClient) {
            return;
        }
        var items = this.inventory.getItems();
        TrackedData<ItemStack>[] slots = new TrackedData[] {
            INVENTORY_SLOT_0, INVENTORY_SLOT_1, INVENTORY_SLOT_2, INVENTORY_SLOT_3, INVENTORY_SLOT_4
        };

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = i < items.size() ? items.get(i) : ItemStack.EMPTY;
            this.dataTracker.set(slots[i], stack);
        }
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

            syncInventory();
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

                syncInventory();
                return true;
            }
        }

        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        NbtCompound inventoryNbt = new NbtCompound();
        this.inventory.writeNbt(inventoryNbt);
        nbt.put(NBT_INVENTORY_KEY, inventoryNbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(NBT_INVENTORY_KEY, NbtElement.COMPOUND_TYPE)) {
            this.inventory.readNbt(nbt.getCompound(NBT_INVENTORY_KEY));
            syncInventory();
        }
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

        handleSlimeJump();
    }

    private void handleSlimeJump() {
        var jumpSettings = this.speciesData.movementProfile().slimeJump();

        if (!jumpSettings.enabled()) {
            return;
        }

        if (this.jumpCooldown > 0) {
            this.jumpCooldown--;
            return;
        }

        if (jumpSettings.jumpChance() > 0.0 && this.random.nextDouble() > jumpSettings.jumpChance()) {
            return;
        }

        if (!this.isOnGround()) {
            return;
        }

        if (!isMoving()) {
            return;
        }

        this.setVelocity(this.getVelocity().x, jumpSettings.jumpStrength(), this.getVelocity().z);
        this.jumpCooldown = jumpSettings.cooldownTicks();
    }

    private boolean isMoving() {
        return !this.getNavigation().isIdle() || hasVelocity();
    }

    private boolean hasVelocity() {
        return Math.abs(this.getVelocity().x) > 0.01 || Math.abs(this.getVelocity().z) > 0.01;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
