package net.silentchaos512.gems.recipe;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.silentchaos512.gems.api.lib.ToolPartPosition;
import net.silentchaos512.gems.api.tool.part.ToolPart;
import net.silentchaos512.gems.api.tool.part.ToolPartRegistry;

public class IngredientToolPart extends Ingredient {

  ToolPartPosition pos;

  public IngredientToolPart(ToolPartPosition pos) {

    this.pos = pos;
  }

  @Override
  public boolean apply(@Nullable ItemStack stack) {

    ToolPart part = ToolPartRegistry.fromStack(stack);
    if (part == null) {
      return false;
    }
    return part.validForPosition(this.pos);
  }
}
