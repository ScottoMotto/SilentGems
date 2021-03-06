package net.silentchaos512.gems.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.inventory.ContainerSL;
import net.silentchaos512.lib.util.StackHelper;

public class ContainerBurnerPylon extends Container {

  private final IInventory tilePylon;

  public ContainerBurnerPylon(InventoryPlayer playerInventory, IInventory pylonInventory) {

    this.tilePylon = pylonInventory;
    this.addSlotToContainer(new Slot(pylonInventory, 0, 80, 34));

    int i;
    for (i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (i = 0; i < 9; ++i) {
      this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer player) {

    return tilePylon.isUsableByPlayer(player);
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

    ItemStack stack = StackHelper.empty();
    Slot slot = (Slot) this.inventorySlots.get(slotIndex);

    if (slot != null && slot.getHasStack()) {
      ItemStack stack1 = slot.getStack();
      stack = StackHelper.safeCopy(stack1);

      if (slotIndex != 0) {
        if (tilePylon.isItemValidForSlot(0, stack1)) {
          if (!this.mergeItemStack(stack1, 0, 1, false)) {
            return StackHelper.empty();
          }
        } else if (slotIndex >= 1 && slotIndex < 28) {
          if (!this.mergeItemStack(stack1, 28, 37, false)) {
            return StackHelper.empty();
          }
        } else if (slotIndex >= 28 && slotIndex < 37
            && !this.mergeItemStack(stack1, 1, 28, false)) {
          return StackHelper.empty();
        }
      } else if (!this.mergeItemStack(stack1, 1, 37, false)) {
        return StackHelper.empty();
      }

      if (StackHelper.isEmpty(stack1)) {
        slot.putStack(StackHelper.empty());
      } else {
        slot.onSlotChanged();
      }

      if (StackHelper.getCount(stack1) == StackHelper.getCount(stack)) {
        return StackHelper.empty();
      }

      ContainerSL.onTakeFromSlot(slot, player, stack1);
    }

    return stack;
  }
}
