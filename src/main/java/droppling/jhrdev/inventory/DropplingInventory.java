package droppling.jhrdev.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public final class DropplingInventory {

    private final int capacity;
    private final List<ItemStack> items = new ArrayList<>();

    public DropplingInventory(int capacity) {
        this.capacity = Math.max(0, capacity);
    }

    public boolean addItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty() || this.isFull()) {
            return false;
        }

        this.items.add(itemStack.copy());
        return true;
    }

    public boolean removeItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }

        for (int index = 0; index < this.items.size(); index++) {
            if (this.items.get(index).equals(itemStack)) {
                this.items.remove(index);
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
}
