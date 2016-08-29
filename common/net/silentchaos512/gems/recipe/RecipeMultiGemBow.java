package net.silentchaos512.gems.recipe;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.api.tool.part.ToolPart;
import net.silentchaos512.gems.api.tool.part.ToolPartMain;
import net.silentchaos512.gems.api.tool.part.ToolPartRegistry;
import net.silentchaos512.gems.api.tool.part.ToolPartRod;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.lib.recipe.RecipeBase;

public class RecipeMultiGemBow extends RecipeBase {

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {

    boolean orientation = getOrientation(inv);
    ItemStack[] gems = getGems(inv, orientation);
    ItemStack[] rods = getRods(inv, orientation);
    ItemStack[] strings = getString(inv, orientation);

    if (gems == null || rods == null || strings == null)
      return null;

    // Check center is empty.
    if (inv.getStackInRowAndColumn(1, 1) != null)
      return null;

    EnumMaterialTier targetTier = null;
    for (ItemStack stack : gems) {
      ToolPart part = ToolPartRegistry.fromStack(stack);
      if (targetTier == null)
        targetTier = part.getTier();
      if (targetTier != part.getTier())
        return null;
    }
    for (ItemStack stack : rods) {
      ToolPart part = ToolPartRegistry.fromStack(stack);
      if (!part.validForToolOfTier(targetTier))
        return null;
    }

    ItemStack stackString = strings[0];
    if ((targetTier == EnumMaterialTier.SUPER
        && !stackString.isItemEqual(ModItems.craftingMaterial.gildedString)
        || (targetTier != EnumMaterialTier.SUPER
            && !stackString.isItemEqual(new ItemStack(Items.STRING)))))
      return null;

    return ModItems.bow.constructTool(rods[0], gems);
  }

  private boolean getOrientation(InventoryCrafting inv) {

    return ToolPartRegistry.fromStack(inv.getStackInSlot(0)) instanceof ToolPartRod;
  }

  private ItemStack[] getGems(InventoryCrafting inv, boolean orientation) {

    ItemStack gem1 = inv.getStackInRowAndColumn(1, 2);
    ItemStack gem2 = inv.getStackInRowAndColumn(orientation ? 0 : 2, 1);
    ItemStack gem3 = inv.getStackInRowAndColumn(1, 0);

    gem1 = ToolPartRegistry.fromStack(gem1) instanceof ToolPartMain ? gem1 : null;
    gem2 = ToolPartRegistry.fromStack(gem2) instanceof ToolPartMain ? gem2 : null;
    gem3 = ToolPartRegistry.fromStack(gem3) instanceof ToolPartMain ? gem3 : null;

    return gem1 == null || gem2 == null || gem3 == null ? null
        : new ItemStack[] { gem1, gem2, gem3 };
  }

  private ItemStack[] getRods(InventoryCrafting inv, boolean orientation) {

    ItemStack rod1 = inv.getStackInRowAndColumn(orientation ? 0 : 2, 0);
    ItemStack rod2 = inv.getStackInRowAndColumn(orientation ? 0 : 2, 2);

    ToolPart part1 = ToolPartRegistry.fromStack(rod1);
    ToolPart part2 = ToolPartRegistry.fromStack(rod2);

    if (part1 == null || part2 == null || !part1.equals(part2))
      return null;

    rod1 = ToolPartRegistry.fromStack(rod1) instanceof ToolPartRod ? rod1 : null;
    rod2 = ToolPartRegistry.fromStack(rod2) instanceof ToolPartRod ? rod2 : null;

    return rod1 == null || rod2 == null ? null : new ItemStack[] { rod1, rod2 };
  }

  private ItemStack[] getString(InventoryCrafting inv, boolean orientation) {

    ItemStack str1 = inv.getStackInRowAndColumn(orientation ? 2 : 0, 0);
    ItemStack str2 = inv.getStackInRowAndColumn(orientation ? 2 : 0, 1);
    ItemStack str3 = inv.getStackInRowAndColumn(orientation ? 2 : 0, 2);

    if (str1 == null || str2 == null || str3 == null || !str1.isItemEqual(str2)
        || !str1.isItemEqual(str3))
      return null;

    return new ItemStack[] { str1, str2, str3 };
  }

  @Override
  public int getRecipeSize() {

    return 10;
  }
}
