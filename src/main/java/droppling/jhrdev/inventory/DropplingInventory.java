package droppling.jhrdev.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public final class DropplingInventory {

    private static final String NBT_ITEMS_KEY = "Items";

    private final int capacity;
    private final List<ItemStack> items = new ArrayList<>();

    public DropplingInventory(int capacity) {
        this.capacity = Math.max(0, capacity);
    }

    public boolean addItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty() || this.isFull()) {
            return false;
        }

        ItemStack copy = itemStack.copy();
        copy.setCount(1);

        if (this.containsEquivalent(copy)) {
            return false;
        }

        this.items.add(copy);
        return true;
    }

    public ItemStack removeItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.items.size()) {
            return ItemStack.EMPTY;
        }

        return this.items.remove(slotIndex);
    }

    public boolean containsEquivalent(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }

        for (ItemStack existing : this.items) {
            if (ItemStack.areEqual(existing, itemStack)) {
                return true;
            }
        }

        return false;
    }

    public List<ItemStack> getItems() {
        return this.items.stream()
                .map(ItemStack::copy)
                .toList();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean isFull() {
        return this.items.size() >= this.capacity;
    }

    public void writeNbt(NbtCompound nbt) {
        NbtList itemList = new NbtList();

        for (ItemStack stack : this.items) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            NbtCompound itemNbt = new NbtCompound();
            stack.writeNbt(itemNbt);
            itemList.add(itemNbt);
        }

        nbt.put(NBT_ITEMS_KEY, itemList);
    }

    public void readNbt(NbtCompound nbt) {
        this.items.clear();

        if (!nbt.contains(NBT_ITEMS_KEY, NbtElement.LIST_TYPE)) {
            return;
        }

        NbtList itemList = nbt.getList(NBT_ITEMS_KEY, NbtElement.COMPOUND_TYPE);

        for (int index = 0; index < itemList.size(); index++) {
            if (this.isFull()) {
                break;
            }

            NbtCompound itemNbt = itemList.getCompound(index);
            ItemStack stack = ItemStack.fromNbt(itemNbt);

            if (stack.isEmpty()) {
                continue;
            }

            stack.setCount(1);
            this.addItem(stack);
        }
    }
}
