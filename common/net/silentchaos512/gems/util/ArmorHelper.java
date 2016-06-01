package net.silentchaos512.gems.util;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.lib.EnumDecoPos;
import net.silentchaos512.gems.api.lib.EnumMaterialGrade;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.api.tool.part.ToolPart;
import net.silentchaos512.gems.api.tool.part.ToolPartMain;
import net.silentchaos512.gems.api.tool.part.ToolPartRegistry;
import net.silentchaos512.lib.registry.IRegistryObject;
import net.silentchaos512.lib.util.LocalizationHelper;

public class ArmorHelper {

  public static final float VARIETY_BONUS = ToolHelper.VARIETY_BONUS;

  /*
   * NBT keys
   */

  public static final String NBT_ROOT_CONSTRUCTION = "SGConstruction";
  public static final String NBT_ROOT_DECORATION = "SGDecoration";
  public static final String NBT_ROOT_PROPERTIES = "SGProperties";
  public static final String NBT_ROOT_STATISTICS = "SGStatistics";

  // Construction
  public static final String NBT_PART_WEST = "Part0";
  public static final String NBT_PART_NORTH = "Part1";
  public static final String NBT_PART_EAST = "Part2";
  public static final String NBT_PART_SOUTH = "Part3";

  public static final String NBT_ARMOR_TIER = "ArmorTier";

  // Decoration
  public static final String NBT_DECO_WEST = "DecoWest";
  public static final String NBT_DECO_NORTH = "DecoNorth";
  public static final String NBT_DECO_EAST = "DecoEast";
  public static final String NBT_DECO_SOUTH = "DecoSouth";

  // Stats
  public static final String NBT_PROP_DURABILITY = "Durability";
  public static final String NBT_PROP_PROTECTION = "Protection";
  public static final String NBT_PROP_ENCHANTABILITY = "Enchantability";

  public static void recalculateStats(ItemStack armor) {

    ToolPart[] parts = getConstructionParts(armor);
    EnumMaterialGrade[] grades = getConstructionGrades(armor);

    if (parts.length == 0)
      return;

    // TODO: Reset render cache? Will that even be a thing for armor?

    float sumDurability = 0f;
    float sumProtection = 0f;
    // TODO: Toughness?
    float sumEnchantability = 0f;

    Set<ToolPart> uniqueParts = Sets.newConcurrentHashSet();

    // Get sum stats for parts.
    for (int i = 0; i < parts.length; ++i) {
      ToolPart part = parts[i];
      EnumMaterialGrade grade = grades[i];
      int multi = 100 + grade.bonusPercent;

      sumDurability += part.getDurability() * multi / 100;
      sumProtection += part.getProtection() * multi / 100;
      sumEnchantability += part.getEnchantability() * multi / 100;
      uniqueParts.add(part);
    }

    // TODO: Set render colors?

    // Variety bonus
    int variety = MathHelper.clamp_int(uniqueParts.size(), 1, 3);
    float bonus = 1.0f + VARIETY_BONUS * (variety - 1);

    // Average stats
    float durability = bonus * sumDurability / parts.length;
    float protection = bonus * sumProtection / parts.length;
    float enchantability = bonus * sumEnchantability / parts.length;

    // Set NBT
    setTagInt(armor, NBT_ROOT_PROPERTIES, NBT_PROP_DURABILITY, (int) durability);
    setTagInt(armor, NBT_ROOT_PROPERTIES, NBT_PROP_PROTECTION, (int) protection);
    setTagInt(armor, NBT_ROOT_PROPERTIES, NBT_PROP_ENCHANTABILITY, (int) enchantability);
    setTagInt(armor, NBT_ROOT_PROPERTIES, NBT_ARMOR_TIER, parts[0].getTier().ordinal());
  }

  /*
   * Armor Properties
   */

  public static int getProtection(ItemStack armor) {

    if (isBroken(armor))
      return 0;

    return getTagInt(armor, NBT_ROOT_PROPERTIES, NBT_PROP_PROTECTION);
  }

  public static int getItemEnchantability(ItemStack armor) {

    return getTagInt(armor, NBT_ROOT_PROPERTIES, NBT_PROP_ENCHANTABILITY);
  }

  public static int getMaxDamage(ItemStack armor) {

    return getTagInt(armor, NBT_ROOT_PROPERTIES, NBT_PROP_DURABILITY);
  }

  public static boolean isBroken(ItemStack armor) {

    return armor.getItemDamage() >= getMaxDamage(armor);
  }

  // ==========================================================================
  // Armor construction and decoration
  // ==========================================================================

  public static EnumMaterialTier getArmorTier(ItemStack armor) {

    int id = getTagInt(armor, NBT_ROOT_PROPERTIES, NBT_ARMOR_TIER);
    return EnumMaterialTier.values()[MathHelper.clamp_int(id, 0,
        EnumMaterialTier.values().length - 1)];
  }

  public static ToolPart[] getConstructionParts(ItemStack armor) {

    return ToolHelper.getConstructionParts(armor);
  }

  public static EnumMaterialGrade[] getConstructionGrades(ItemStack armor) {

    return ToolHelper.getConstructionGrades(armor);
  }

  public static ItemStack constructArmor(Item item, ItemStack... materials) {

    // Shortcut for JEI recipes.
    if (materials.length == 1) {
      ItemStack[] newMats = new ItemStack[4];
      for (int i = 0; i < newMats.length; ++i)
        newMats[i] = materials[0];
      materials = newMats;
    }

    ItemStack result = new ItemStack(item);

    // Construction
    ToolPart part;
    EnumMaterialGrade grade;
    for (int i = 0; i < materials.length; ++i) {
      if (materials[i] == null) {
        String str = "ArmorHelper.constructArmor: null part! ";
        for (ItemStack stack: materials)
          str += stack + ", ";
        throw new IllegalArgumentException(str);
      }

      part = ToolPartRegistry.fromStack(materials[i]);
      grade = EnumMaterialGrade.fromStack(materials[i]);
      setTagPart(result, "Part" + i, part, grade);
    }

    // Create name
    String displayName = ToolHelper.createToolName(item, materials);
    result.setStackDisplayName(displayName);

    recalculateStats(result);

    return result;
  }

  /*
   * Decoration Methods
   */

  public static ItemStack decorateArmor(ItemStack armor, ItemStack west, ItemStack north,
      ItemStack east, ItemStack south) {

    if (armor == null) {
      return null;
    }

    ItemStack result = armor.copy();
    result = decorate(result, west, EnumDecoPos.WEST);
    result = decorate(result, north, EnumDecoPos.NORTH);
    result = decorate(result, east, EnumDecoPos.EAST);
    result = decorate(result, south, EnumDecoPos.SOUTH);
    return result;
  }

  private static ItemStack decorate(ItemStack armor, ItemStack material, EnumDecoPos pos) {

    if (armor == null)
      return null;
    if (material == null)
      return armor;

    ToolPart part = ToolPartRegistry.fromStack(material);
    if (part == null)
      return null;

    // Only main parts (like gems) work
    if (!(part instanceof ToolPartMain))
      return armor;

    ItemStack result = armor.copy();
    switch (pos) {
      case WEST:
        setTagPart(result, NBT_DECO_WEST, part, EnumMaterialGrade.fromStack(material));
        break;
      case NORTH:
        setTagPart(result, NBT_DECO_NORTH, part, EnumMaterialGrade.fromStack(material));
        break;
      case EAST:
        setTagPart(result, NBT_DECO_EAST, part, EnumMaterialGrade.fromStack(material));
        break;
      case SOUTH:
        setTagPart(result, NBT_DECO_SOUTH, part, EnumMaterialGrade.fromStack(material));
        break;
      default:
        SilentGems.instance.logHelper.warning("ArmorHelper.decorate: invalid deco pos " + pos);
        break;
    }
    return result;
  }

  // ==========================================================================
  // NBT helper methods
  // ==========================================================================

  private static NBTTagCompound getRootTag(ItemStack tool, String key) {

    if (key != null && !key.isEmpty()) {
      if (!tool.getTagCompound().hasKey(key)) {
        tool.getTagCompound().setTag(key, new NBTTagCompound());
      }
      return tool.getTagCompound().getCompoundTag(key);
    }
    return tool.getTagCompound();
  }

  private static void initRootTag(ItemStack tool) {

    if (!tool.hasTagCompound())
      tool.setTagCompound(new NBTTagCompound());
  }

  private static int getTagInt(ItemStack tool, String root, String name) {

    if (!tool.hasTagCompound())
      return 0;
    return getRootTag(tool, root).getInteger(name);
  }

  private static int getTagInt(ItemStack tool, String root, String name, int defaultValue) {

    if (!tool.hasTagCompound() || !getRootTag(tool, root).hasKey(name))
      return defaultValue;
    return getRootTag(tool, root).getInteger(name);
  }

  private static void setTagInt(ItemStack tool, String root, String name, int value) {

    initRootTag(tool);
    getRootTag(tool, root).setInteger(name, value);
  }

  private static float getTagFloat(ItemStack tool, String root, String name) {

    if (!tool.hasTagCompound())
      return 0.0f;
    return getRootTag(tool, root).getFloat(name);
  }

  private static void setTagFloat(ItemStack tool, String root, String name, float value) {

    initRootTag(tool);
    getRootTag(tool, root).setFloat(name, value);
  }

  private static boolean getTagBoolean(ItemStack tool, String root, String name) {

    if (!tool.hasTagCompound())
      return false;
    return getRootTag(tool, root).getBoolean(name);
  }

  private static void setTagBoolean(ItemStack tool, String root, String name, boolean value) {

    initRootTag(tool);
    getRootTag(tool, root).setBoolean(name, value);
  }

  private static String getTagString(ItemStack tool, String root, String name) {

    if (!tool.hasTagCompound())
      return "";
    return getRootTag(tool, root).getString(name);
  }

  private static void setTagString(ItemStack tool, String root, String name, String value) {

    initRootTag(tool);
    getRootTag(tool, root).setString(name, value);
  }

  private static void setTagPart(ItemStack tool, String name, ToolPart part,
      EnumMaterialGrade grade) {

    initRootTag(tool);
    getRootTag(tool, NBT_ROOT_CONSTRUCTION).setString(name, part.getKey() + "#" + grade.name());
  }
}
