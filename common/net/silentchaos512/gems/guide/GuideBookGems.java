package net.silentchaos512.gems.guide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.ITool;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.client.gui.config.GuiConfigSilentGems;
import net.silentchaos512.gems.config.GemsConfig;
import net.silentchaos512.gems.guide.page.PageDebugTool;
import net.silentchaos512.gems.guide.page.PageOreSpawn;
import net.silentchaos512.gems.guide.page.PageSoulSkill;
import net.silentchaos512.gems.init.ModBlocks;
import net.silentchaos512.gems.init.ModEnchantments;
import net.silentchaos512.gems.init.ModItems;
import net.silentchaos512.gems.item.ItemChaosOrb;
import net.silentchaos512.gems.item.ItemCrafting;
import net.silentchaos512.gems.item.ItemEnchantmentToken;
import net.silentchaos512.gems.item.ItemTipUpgrade;
import net.silentchaos512.gems.lib.ChaosBuff;
import net.silentchaos512.gems.lib.EnumGem;
import net.silentchaos512.gems.lib.soul.SoulSkill;
import net.silentchaos512.gems.util.ArmorHelper;
import net.silentchaos512.gems.util.ToolHelper;
import net.silentchaos512.gems.util.ToolRandomizer;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.chapter.GuideChapter;
import net.silentchaos512.lib.guidebook.entry.GuideEntry;
import net.silentchaos512.lib.guidebook.page.PageCrafting;
import net.silentchaos512.lib.guidebook.page.PageFurnace;
import net.silentchaos512.lib.guidebook.page.PagePicture;
import net.silentchaos512.lib.guidebook.page.PageTextOnly;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.StackHelper;

public class GuideBookGems extends GuideBook {

  public static final String TOOL_OWNER_NAME = "Guide Book";

  private GuideEntry entryGettingStarted;
  private GuideEntry entryBlocks;
  private GuideEntry entryItems;
  private GuideEntry entryTools;
  private GuideEntry entrySouls;
  private GuideEntry entryEnchantments;
  private GuideEntry entryDebug;

  public GuideBookGems() {

    super(SilentGems.MODID);
    this.resourceGui = new ResourceLocation(SilentGems.MODID, "textures/guide/gui_guide.png");
    this.resourceGadgets = new ResourceLocation(SilentGems.MODID,
        "textures/guide/gui_guide_gadgets.png");

    edition = SilentGems.BUILD_NUM;
  }

  @Override
  public void initEntries() {

    entryGettingStarted = new GuideEntry(this, "gettingStarted").setImportant();
    entryBlocks = new GuideEntry(this, "blocks");
    entryItems = new GuideEntry(this, "items");
    entryTools = new GuideEntry(this, "tools");
    entrySouls = new GuideEntry(this, "souls");
    entryEnchantments = new GuideEntry(this, "enchantments");
    if (edition == 0 || GemsConfig.DEBUG_MODE)
      entryDebug = new GuideEntry(this, "debug").setSpecial();
  }

  @SuppressWarnings("unused")
  @Override
  public void initChapters() {

    RecipeMaker rec = SilentGems.registry.recipes;

    //@formatter:off

    // Getting Started

    // Introduction
    new GuideChapter(this, "introduction", entryGettingStarted, new ItemStack(ModItems.gem, 1, SilentGems.random.nextInt(32)), 1000,
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2),
        new PageTextOnly(this, 3)).setSpecial();
    // Progression
    ItemStack flintPickaxe = ModItems.pickaxe.constructTool(false, new ItemStack(Items.FLINT));
    ToolHelper.setOriginalOwner(flintPickaxe, TOOL_OWNER_NAME);
    ItemStack flintPickaxeBroken = StackHelper.safeCopy(flintPickaxe);
    flintPickaxeBroken.setItemDamage(ToolHelper.getMaxDamage(flintPickaxeBroken));
    ItemStack ironTipUpgrade = new ItemStack(ModItems.tipUpgrade);
    ItemStack flintPickaxeIronTips = ModItems.tipUpgrade.applyToTool(flintPickaxe, ironTipUpgrade);
    ItemStack gravel = new ItemStack(Blocks.GRAVEL);
    ItemStack gemPickaxe = ModItems.pickaxe.constructTool(new ItemStack(Items.STICK), EnumGem.RUBY.getItem(), EnumGem.SAPPHIRE.getItem(), EnumGem.RUBY.getItem());
    ToolHelper.setOriginalOwner(gemPickaxe, TOOL_OWNER_NAME);
    ItemStack diamondTipUpgrade = new ItemStack(ModItems.tipUpgrade, 1, 2);
    ItemStack gemPickaxeDiamondTips = ModItems.tipUpgrade.applyToTool(gemPickaxe, diamondTipUpgrade);
    ItemStack katana = ModItems.katana.constructTool(true, EnumGem.LEPIDOLITE.getItemSuper(), EnumGem.OPAL.getItemSuper(), EnumGem.BLACK_DIAMOND.getItemSuper());
    ToolHelper.setOriginalOwner(katana, TOOL_OWNER_NAME);
    new GuideChapter(this, "progression", entryGettingStarted, flintPickaxeIronTips, 100,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapeless(new ItemStack(Items.FLINT), gravel, gravel)),
        new PageCrafting(this, 3, rec.makeShapedOre(flintPickaxe, "fff", " s ", " s ", 'f', Items.FLINT, 's', "stickWood")),
        new PageTextOnly(this, 4),
        new PageCrafting(this, 5, rec.makeShapeless(flintPickaxe, flintPickaxeBroken, Items.FLINT, Items.FLINT)),
        new PageTextOnly(this, 6),
        new PageCrafting(this, 7, rec.makeShapelessOre(flintPickaxeIronTips, flintPickaxe, ironTipUpgrade)),
        new PageCrafting(this, 8, rec.makeShapedOre(gemPickaxe, "rsr", " t ", " t ", 'r', EnumGem.RUBY.getItem(), 's',
            EnumGem.SAPPHIRE.getItem(), 't', "stickWood")),
        new PageTextOnly(this, 9),
        new PageCrafting(this, 10, rec.makeShapeless(gemPickaxeDiamondTips, gemPickaxe, diamondTipUpgrade)),
        new PageTextOnly(this, 11),
        new PageCrafting(this, 12, rec.makeShapedOre(katana, "lo", "d ", "r ", 'l', EnumGem.LEPIDOLITE.getItemSuper(), 'o',
            EnumGem.OPAL.getItemSuper(), 'd', EnumGem.BLACK_DIAMOND.getItemSuper(), 'r', ModItems.craftingMaterial.toolRodGold)),
        new PageTextOnly(this, 13)).setImportant();

    // Tools, Armor, and Parts

    // Parts
//    List<IGuidePage> pagesParts = Lists.newArrayList();
//    pagesParts.add(new PageTextOnly(this, 1));
//    for (ToolPart part : ToolPartRegistry.getMains()) {
//      pagesParts.add(new PageToolPart(this, 0, part));
//    }
//    new GuideChapter(this, "toolParts", entryTools, EnumGem.getRandom().getItem(), 100,
//        pagesParts.toArray(new IGuidePage[pagesParts.size()]));
    // Axes
    ItemStack toolsEntryRod = SilentGems.random.nextFloat() < 0.67f ? ModItems.craftingMaterial.toolRodGold : ModItems.craftingMaterial.toolRodSilver;
    ItemStack chAxeGem = EnumGem.getRandom().getItemSuper();
    ItemStack chAxe = makeTool(ModItems.axe, toolsEntryRod, chAxeGem, 3);
    new GuideChapter(this, "axe", entryTools, chAxe,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chAxe, "gg", "gr", " r", 'g', chAxeGem, 'r', toolsEntryRod)).setNoText());
    // Bows
    ItemStack chBowGem = EnumGem.getRandom().getItemSuper();
    ItemStack chBow = makeTool(ModItems.bow, toolsEntryRod, chBowGem, 3);
    new GuideChapter(this, "bow", entryTools, chBow,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chBow, "rgs", "g s", "rgs", 'g', chBowGem, 'r', toolsEntryRod, 's', ModItems.craftingMaterial.gildedString)));
    // Daggers
    ItemStack chDaggerGem = EnumGem.getRandom().getItemSuper();
    ItemStack chDagger = makeTool(ModItems.dagger, toolsEntryRod, chDaggerGem, 1);
    new GuideChapter(this, "dagger", entryTools, chDagger,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chDagger, "g", "r", "f", 'g', chDaggerGem, 'r', toolsEntryRod, 'f', "ingotGold")));
    // Hoes
    ItemStack chHoeGem = EnumGem.getRandom().getItemSuper();
    ItemStack chHoe = makeTool(ModItems.hoe, toolsEntryRod, chHoeGem, 2);
    new GuideChapter(this, "hoe", entryTools, chHoe,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chHoe, "gg", " r", " r", 'g', chHoeGem, 'r', toolsEntryRod)).setNoText());
    // Katana
    ItemStack chKatanaGem = EnumGem.getRandom().getItemSuper();
    ItemStack chKatana = makeTool(ModItems.katana, toolsEntryRod, chKatanaGem, 3);
    new GuideChapter(this, "katana", entryTools, chKatana,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chKatana, "gg", "g ", "r ", 'g', chKatanaGem, 'r', toolsEntryRod)).setNoText());
    // Machetes
    ItemStack chMacheteGem = EnumGem.getRandom().getItemSuper();
    ItemStack chMachete = makeTool(ModItems.machete, toolsEntryRod, chMacheteGem, 3);
    new GuideChapter(this, "machete", entryTools, chMachete,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chMachete, " gg", " g ", "r  ", 'g', chMacheteGem, 'r', toolsEntryRod)).setNoText(),
        new PageTextOnly(this, 3),
        new PageTextOnly(this, 4));
    // Paxels
    ItemStack chPaxelGem = EnumGem.getRandom().getItemSuper();
    ItemStack chPaxel = makeTool(ModItems.paxel, toolsEntryRod, chPaxelGem, 6);
    new GuideChapter(this, "paxel", entryTools, chPaxel,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chPaxel, "ggg", "grg", "gr ", 'g', chPaxelGem, 'r', toolsEntryRod)).setNoText());
    // Pickaxes
    ItemStack chPickaxeGem = EnumGem.getRandom().getItemSuper();
    ItemStack chPickaxe = makeTool(ModItems.pickaxe, toolsEntryRod, chPickaxeGem, 3);
    new GuideChapter(this, "pickaxe", entryTools, chPickaxe,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chPickaxe, "ggg", " r ", " r ", 'g', chPickaxeGem, 'r', toolsEntryRod)).setNoText());
    // Scepters
    ItemStack chScepterGem = EnumGem.getRandom().getItemSuper();
    ItemStack chScepter = makeTool(ModItems.scepter, toolsEntryRod, chScepterGem, 5);
    new GuideChapter(this, "scepter", entryTools, chScepter,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chScepter, " g ", "grg", "grg", 'g', chScepterGem, 'r', toolsEntryRod)).setNoText(),
        new PageTextOnly(this, 3));
    ItemStack chShieldGem = EnumGem.getRandom().getItemSuper();
    ItemStack chShield = makeTool(ModItems.shield, toolsEntryRod, chShieldGem, 3);
    new GuideChapter(this, "shield", entryTools, chShield,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chShield, "gwg", "wrw", " g ", 'g', chShieldGem, 'r', toolsEntryRod, 'w', "plankWood")).setNoText());
    // Shovels
    ItemStack chShovelGem = EnumGem.getRandom().getItemSuper();
    ItemStack chShovel = makeTool(ModItems.shovel, toolsEntryRod, chShovelGem, 1);
    new GuideChapter(this, "shovel", entryTools, chShovel,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chShovel, "g", "r", "r", 'g', chShovelGem, 'r', toolsEntryRod)).setNoText());
    // Sickles
    ItemStack chSickleGem = EnumGem.getRandom().getItemSuper();
    ItemStack chSickle = makeTool(ModItems.sickle, toolsEntryRod, chSickleGem, 3);
    new GuideChapter(this, "sickle", entryTools, chSickle,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chSickle, " g", "gg", "r ", 'g', chSickleGem, 'r', toolsEntryRod)).setNoText());
    // Swords
    ItemStack chSwordGem = EnumGem.getRandom().getItemSuper();
    ItemStack chSword = makeTool(ModItems.sword, toolsEntryRod, chSwordGem, 2);
    new GuideChapter(this, "sword", entryTools, chSword,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chSword, "g", "g", "r", 'g', chSwordGem, 'r', toolsEntryRod)).setNoText());
    // Tomahawks
    ItemStack chTomahawkGem = EnumGem.getRandom().getItemSuper();
    ItemStack chTomahawk = makeTool(ModItems.tomahawk, toolsEntryRod, chTomahawkGem, 4);
    new GuideChapter(this, "tomahawk", entryTools, chTomahawk,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapedOre(chTomahawk, "ggg", "gr ", " r ", 'g', chTomahawkGem, 'r', toolsEntryRod)).setNoText());
    // Armor
    ItemStack chHelmetGem = EnumGem.getRandom().getItemSuper();
    ItemStack chHelmet = ModItems.gemHelmet.constructArmor(EnumMaterialTier.SUPER, chHelmetGem);
    ItemStack chHelmetFrame = ModItems.armorFrame.getFrameForArmorPiece(ModItems.gemHelmet, EnumMaterialTier.SUPER);
    ArmorHelper.setOriginalOwner(chHelmet, TOOL_OWNER_NAME);
    new GuideChapter(this, "armor", entryTools, chHelmet, -10,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, ModItems.craftingMaterial.recipeLatticeMundane).setNoText(),
        new PageCrafting(this, 3, ModItems.craftingMaterial.recipeLatticeRegular).setNoText(),
        new PageCrafting(this, 4, ModItems.craftingMaterial.recipeLatticeSuper).setNoText(),
        new PageCrafting(this, 5, rec.makeShaped(chHelmetFrame, "lll", "l l", 'l', ModItems.craftingMaterial.armorLatticeSuper)),
        new PageCrafting(this, 6, rec.makeShapedOre(chHelmet, " g ", "gfg", " g ", 'g', chHelmetGem, 'f', chHelmetFrame)));

    // Blocks

    // Ores
    new GuideChapter(this, "ores", entryBlocks, new ItemStack(ModBlocks.gemOre, 1, SilentGems.random.nextInt(16)), 10,
        new PageTextOnly(this, 1),
        new PageOreSpawn(this, 2, GemsConfig.WORLD_GEN_GEMS),
        new PageOreSpawn(this, 3, GemsConfig.WORLD_GEN_GEMS_DARK),
        new PageOreSpawn(this, 8, GemsConfig.WORLD_GEN_GEMS_LIGHT),
        new PageOreSpawn(this, 4, GemsConfig.WORLD_GEN_CHAOS),
        new PageFurnace(this, 5, ModItems.craftingMaterial.chaosEssence),
        new PageOreSpawn(this, 6, GemsConfig.WORLD_GEN_ENDER),
        new PageFurnace(this, 7, ModItems.craftingMaterial.enderEssence)).setImportant();
    // Chaos Altar
    new GuideChapter(this, "chaosAltar", entryBlocks, new ItemStack(ModBlocks.chaosAltar),
        new PageCrafting(this, 1, ModBlocks.chaosAltar.recipe),
        new PageTextOnly(this, 2),
        new PageTextOnly(this, 3),
        new PageTextOnly(this, 4));
    // Chaos Flower Pot
    new GuideChapter(this, "chaosFlowerPot", entryBlocks, new ItemStack(ModBlocks.chaosFlowerPot),
        new PageCrafting(this, 1, ModBlocks.chaosFlowerPot.recipe),
        new PageTextOnly(this, 2));
    // Chaos Node
    new GuideChapter(this, "chaosNode", entryBlocks, new ItemStack(ModBlocks.chaosNode),
        new PagePicture(this, 3, new ResourceLocation(SilentGems.MODID, "textures/guide/chaosnode.png"), 125),
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));
    // Chaos Pylons
    new GuideChapter(this, "chaosPylon", entryBlocks, new ItemStack(ModBlocks.chaosPylon),
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));
    // Fluffy Blocks
    new GuideChapter(this, "fluffyBlocks", entryBlocks, new ItemStack(ModBlocks.fluffyBlock),
        new PageCrafting(this, 1, rec.makeShaped(new ItemStack(ModBlocks.fluffyBlock), "ff", "ff", 'f', ModItems.craftingMaterial.fluffyFabric)),
        new PageTextOnly(this, 2));
    // Glow Rose
    new GuideChapter(this, "glowRose", entryBlocks, new ItemStack(ModBlocks.glowRose),
        new PageTextOnly(this, 1));
    // Material Grader
    new GuideChapter(this, "materialGrader", entryBlocks, new ItemStack(ModBlocks.materialGrader),
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));
    // Decorative Gem Blocks
    new GuideChapter(this, "gemDecoBlocks", entryBlocks, new ItemStack(ModBlocks.gemBrickCoated, 1, SilentGems.random.nextInt(16)), -10,
        new PageTextOnly(this, 1));

    // Items

    // Crafting Materials
    List<IGuidePage> pages = Lists.newArrayList();
    pages.add(new PageTextOnly(this, 1));
    for (String str : ItemCrafting.SORTED_NAMES) {
      ItemStack stack = ModItems.craftingMaterial.getStack(str);
      IRecipe recipe = ModItems.craftingMaterial.guideRecipeMap.get(stack.getItemDamage());

      if (stack.isItemEqual(ModItems.craftingMaterial.chaosEssence) || stack.isItemEqual(ModItems.craftingMaterial.enderEssence) || stack.isItemEqual(ModItems.craftingMaterial.chaosIron))
        pages.add(new PageFurnace(this, 100 + stack.getItemDamage(), stack));
      else if (recipe != null)
        pages.add(new PageCrafting(this, 100 + stack.getItemDamage(), recipe));
      else
        pages.add(new PageTextOnly(this, 100 + stack.getItemDamage()));
    }
    new GuideChapter(this, "craftingMaterial", entryItems, ModItems.craftingMaterial.chaosEssence,
        pages.toArray(new IGuidePage[pages.size()]));
    // Chaos Gems
    ItemStack chChaosGem = new ItemStack(ModItems.chaosGem, 1, EnumGem.getRandom().ordinal());
    ModItems.chaosGem.receiveCharge(chChaosGem, ModItems.chaosGem.getMaxCharge(chChaosGem), false);
    ItemStack chChaosGemWithBuffs = StackHelper.safeCopy(chChaosGem);
    ItemStack chChaosGemRuneStrength = new ItemStack(ModItems.chaosRune);
    ModItems.chaosRune.setBuff(chChaosGemRuneStrength, ChaosBuff.STRENGTH);
    ItemStack chChaosGemRuneResistance = new ItemStack(ModItems.chaosRune);
    ModItems.chaosRune.setBuff(chChaosGemRuneResistance, ChaosBuff.RESISTANCE);
    ModItems.chaosGem.addBuff(chChaosGemWithBuffs, ChaosBuff.STRENGTH);
    ModItems.chaosGem.addBuff(chChaosGemWithBuffs, ChaosBuff.STRENGTH);
    ModItems.chaosGem.addBuff(chChaosGemWithBuffs, ChaosBuff.RESISTANCE);
    ModItems.chaosGem.addBuff(chChaosGemWithBuffs, ChaosBuff.RESISTANCE);
    new GuideChapter(this, "chaosGem", entryItems, chChaosGem,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapeless(chChaosGemWithBuffs, chChaosGem, chChaosGemRuneStrength, chChaosGemRuneStrength, chChaosGemRuneResistance, chChaosGemRuneResistance)),
        new PageTextOnly(this, 3));
    // Chaos Orbs
    ItemStack chChaosOrb = new ItemStack(ModItems.chaosOrb, 1, ItemChaosOrb.Type.SUPREME.ordinal());
    ModItems.chaosOrb.receiveCharge(chChaosOrb, ModItems.chaosOrb.getMaxCharge(chChaosOrb), false);
    new GuideChapter(this, "chaosOrb", entryItems, chChaosOrb,
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));
    // Drawing Compass
    ItemStack chDrawingCompass = new ItemStack(ModItems.drawingCompass);
    new GuideChapter(this, "drawingCompass", entryItems, chDrawingCompass,
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));
    // Enchantment Tokens
    ItemStack chEnchantmentToken = new ItemStack(ModItems.enchantmentToken, 1, ItemEnchantmentToken.BLANK_META);
    ItemStack tokenSharpness = ModItems.enchantmentToken.constructToken(Enchantments.SHARPNESS);
    ItemStack chEnchantmentTokenPickaxe = ToolRandomizer.INSTANCE.randomize(new ItemStack(ModItems.pickaxe), 0.75f);
    ItemStack chEnchantmentTokenPickaxeEnchanted = StackHelper.safeCopy(chEnchantmentTokenPickaxe);
    ItemStack tokenUnbreaking = ModItems.enchantmentToken.constructToken(Enchantments.UNBREAKING);
    ItemStack tokenFortune = ModItems.enchantmentToken.constructToken(Enchantments.FORTUNE);
    for (int i = 0; i < 3; ++i) {
      ModItems.enchantmentToken.applyTokenToTool(tokenFortune, chEnchantmentTokenPickaxeEnchanted);
      ModItems.enchantmentToken.applyTokenToTool(tokenUnbreaking, chEnchantmentTokenPickaxeEnchanted);
    }
    new GuideChapter(this, "enchantmentToken", entryItems, chEnchantmentToken,
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2),
        new PageCrafting(this, 3, rec.makeShapedOre(new ItemStack(ModItems.enchantmentToken, 12, ItemEnchantmentToken.BLANK_META), "ggg", "lcl", "ggg", 'g', "ingotGold", 'l', "gemLapis", 'c', "gemChaos")),
        new PageCrafting(this, 4, rec.makeShapedOre(tokenSharpness, "r r", "fbf", "fff", 'r', "gemRuby", 'f', Items.FLINT, 'b', chEnchantmentToken)),
        new PageTextOnly(this, 5),
        new PageCrafting(this, 6, rec.makeShapeless(chEnchantmentTokenPickaxeEnchanted, chEnchantmentTokenPickaxe, tokenUnbreaking, tokenUnbreaking, tokenUnbreaking, tokenFortune, tokenFortune, tokenFortune)));
    // Fluffy Puffs
    new GuideChapter(this, "fluffyPuff", entryItems, new ItemStack(ModItems.fluffyPuff),
        new PageTextOnly(this, 1));
    // Gems
    EnumGem chGem = EnumGem.getRandom();
    ItemStack craftedShards = StackHelper.setCount(StackHelper.safeCopy(chGem.getShard()), 9);
    new GuideChapter(this, "gem", entryItems, chGem.getItem(),
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapelessOre(craftedShards, chGem.getItem())),
        new PageCrafting(this, 3, rec.makeShapedOre(chGem.getItemSuper(), "cgc", "cdc", "cgc", 'c', ModItems.craftingMaterial.chaosEssence, 'g', chGem.getItem(), 'd', "dustGlowstone")));
    // Holding Gem
    ItemStack chHoldingGem = ModItems.holdingGem.construct(EnumGem.getRandom());
    ItemStack chHoldingGemIcon = StackHelper.safeCopy(chHoldingGem);
    chHoldingGemIcon.setItemDamage(0);
    ItemStack chHoldingGemSet = StackHelper.safeCopy(chHoldingGem);
    ModItems.holdingGem.setBlockPlaced(chHoldingGemSet, Blocks.COBBLESTONE.getDefaultState());
    chHoldingGemSet.setItemDamage(chHoldingGemSet.getMaxDamage() - 1);
    new GuideChapter(this, "holdingGem", entryItems, chHoldingGemIcon,
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShapeless(chHoldingGemSet, chHoldingGem, new ItemStack(Blocks.COBBLESTONE))),
        new PageTextOnly(this, 3));
    // Tip upgrades
    ItemStack chTipUpgrade = new ItemStack(ModItems.tipUpgrade, 1, 2);
    pages = Lists.newArrayList();
    pages.add(new PageTextOnly(this, 1));
    pages.add(new PageTextOnly(this, 2));
    pages.add(new PageCrafting(this, 3, rec.makeShapelessOre(ModItems.craftingMaterial.upgradeBase, Items.FLINT, Items.FLINT, "stickWood", "plankWood")));
    for (IRecipe recipe : ItemTipUpgrade.RECIPES) {
      pages.add(new PageCrafting(this, 0, recipe).setNoText());
    }
    new GuideChapter(this, "tipUpgrade", entryItems, chTipUpgrade,
        pages.toArray(new IGuidePage[pages.size()]));
    // Torch Bandolier
    ItemStack chTorchBandolier = new ItemStack(ModItems.torchBandolier);
    new GuideChapter(this, "torchBandolier", entryItems, chTorchBandolier,
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));

    /*******************
     * Souls and Skills
     *******************/

    // Important Notice
//    new GuideChapter(this, "soulsImportant", entrySouls, new ItemStack(Items.DIAMOND),
//        new PageTextOnly(this, 1)).setImportant();

    // Tool Souls
    new GuideChapter(this, "toolSoul", entrySouls, new ItemStack(ModItems.toolSoul),
        new PageTextOnly(this, 1),
        new PageCrafting(this, 2, rec.makeShaped(new ItemStack(ModItems.toolSoul), " s ", "sds", " s ", 's', ModItems.soulGem, 'd', ModItems.craftingMaterial.soulShell)));

    // Soul Skills (includes a page for each skill!)
    pages = new ArrayList<>();
    pages.add(new PageTextOnly(this, 1));
    pages.add(new PageTextOnly(this, 2));
    for (SoulSkill skill : SoulSkill.getSkillList()) {
      pages.add(new PageSoulSkill(this, skill));
    }
    new GuideChapter(this, "soulSkills", entrySouls, new ItemStack(ModItems.skillOrb),
        pages.toArray(new IGuidePage[pages.size()]));

    // Enchantments

    // Gravity
    new GuideChapter(this, "enchantmentGravity", entryEnchantments, ModItems.enchantmentToken.constructToken(ModEnchantments.gravity),
        new PageTextOnly(this, 1));
    // Ice Aspect
    new GuideChapter(this, "enchantmentIceAspect", entryEnchantments, ModItems.enchantmentToken.constructToken(ModEnchantments.iceAspect),
        new PageTextOnly(this, 1));
    // Life Steal
    new GuideChapter(this, "enchantmentLifeSteal", entryEnchantments, ModItems.enchantmentToken.constructToken(ModEnchantments.lifeSteal),
        new PageTextOnly(this, 1),
        new PageTextOnly(this, 2));
    // Lightning Aspect
    new GuideChapter(this, "enchantmentLightningAspect", entryEnchantments, ModItems.enchantmentToken.constructToken(ModEnchantments.lightningAspect),
        new PageTextOnly(this, 1));
    // Magic Damage (Concentration)
    new GuideChapter(this, "enchantmentMagicDamage", entryEnchantments, ModItems.enchantmentToken.constructToken(ModEnchantments.magicDamage),
        new PageTextOnly(this, 1));

    // Debug

    if (entryDebug != null) {
      // Tool test
      new GuideChapter(this, "toolTest", entryDebug, ModItems.craftingMaterial.ironPotato,
          new PageDebugTool(this, 1),
          new PageDebugTool(this, 2));
    }

    // @formatter:on
  }

  public static final String[] QUOTES = { //@formatter:off
      "The flowers probably won't kill you.",
      "Try the donuts!",
      "May contain unintended &cR&6a&ei&an&9b&do&5w&0s!".replaceAll("&", "\u00a7"),
      "Shake well and refrigerate after opening.",
      "Drowning in [slightly fewer] JSON files...",
      "Download only from CurseForge!",
      "Rabbit poop coffee!",
      "It stares into your soul.",
      "Pot now included... flower pot, that is.",
      "Did you know Chaos Gems are finally back?",
      "Also try Extra Parts!",
      "Your wish has been granted!",
      "Voted most unnecessarily complicated mod in high school.",
      "I like your gems!@HockeyStick",
      "Also try JEI! Seriously, learn to look up the recipes... How do you play without mods like this?",
      "How do you craft the upgrades?@Everyone Ever",
      "Scathing comments since 2017!",
      "Muffin button not included.",
      "Now with more enchantments!",
      "Send help.@SilentChaos512",
      "Good lookin' mainframes!"
  };//@formatter:on

  @Override
  public String[] getQuotes() {

    return QUOTES;
  }

  @Override
  public @Nonnull String selectQuote(Random rand) {

    if (rand.nextInt(100) == 0) {
      return "Lolis are love, lolis are life!";
    }
    return super.selectQuote(rand);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public GuiScreen getConfigScreen(GuiScreen parent) {

    return new GuiConfigSilentGems(parent);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public GuiScreen getAchievementScreen(GuiScreen parent) {

    // TODO Auto-generated method stub
    return null;
  }

  private ItemStack makeTool(ITool tool, ItemStack rod, ItemStack gem, int gemCount) {

    ItemStack[] array = new ItemStack[gemCount];
    for (int i = 0; i < array.length; ++i)
      array[i] = gem;
    ItemStack ret = tool.constructTool(rod, array);
    ToolHelper.setOriginalOwner(ret, TOOL_OWNER_NAME);
    return ret;
  }
}
