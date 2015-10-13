package net.silentchaos512.gems.client.renderers.tool;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.silentchaos512.gems.core.util.LogHelper;
import net.silentchaos512.gems.core.util.ToolHelper;

/**
 * This is mostly copied from Tinker's Construct.
 * Fixes rendering on OpenComputer's robots.
 */
public class ToolItemRenderer implements IItemRenderer {
  
  private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

  private final boolean isEntity;
  private final boolean noEntityTranslation;

  public ToolItemRenderer() {

    this(false, false);
  }

  public ToolItemRenderer(boolean isEntity) {

    this(isEntity, false);
  }

  public ToolItemRenderer(boolean isEntity, boolean noEntityTranslation) {

    this.isEntity = isEntity;
    this.noEntityTranslation = noEntityTranslation;
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {

    if (!item.hasTagCompound())
      return false;

    switch (type) {
      case ENTITY:
        return true;
      case EQUIPPED:
        GL11.glTranslatef(0.03f, 0F, -0.09375F);
      case EQUIPPED_FIRST_PERSON:
        return !isEntity;
      case INVENTORY:
        return true;
      default:
        LogHelper.warning("Unhandled tool render case!");
      case FIRST_PERSON_MAP:
        return false;
    }
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
      ItemRendererHelper helper) {

    return handleRenderType(item, type)
        & helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

    int gemId = ToolHelper.getToolGemId(item);
    boolean supercharged = ToolHelper.getToolIsSupercharged(item);

    boolean isInventory = type == ItemRenderType.INVENTORY;
    Entity ent = null;
    if (data.length > 1) {
      ent = (Entity) data[1];
    }

    int iconParts = ToolRenderHelper.RENDER_PASS_COUNT;

    IIcon[] tempParts = new IIcon[iconParts];
//    label: {
//      if (!isInventory && ent instanceof EntityPlayer) {
//        EntityPlayer player = (EntityPlayer) ent;
//        ItemStack itemInUse = player.getItemInUse();
//        if (itemInUse != null) {
//          int useCount = player.getItemInUseCount();
//          for (int i = iconParts; i-- > 0;) {
//            tempParts[i] = ToolRenderHelper.instance.getIcon(item, i, gemId, supercharged); // TODO: Use itemInUse?
//          }
//          break label;
//        }
//      }
//      for (int i = iconParts; i-- > 0;) {
//        tempParts[i] = ToolRenderHelper.instance.getIcon(item, i, gemId, supercharged);
//      }
//    }
    for (int i = iconParts; i-- > 0;) {
      tempParts[i] = ToolRenderHelper.instance.getIcon(item, i, gemId, supercharged);
    }

    int count = 0;
    IIcon[] parts = new IIcon[iconParts];
    for (int i = 0; i < iconParts; ++i) {
      IIcon part = tempParts[i];
      if (part == null || part == ToolRenderHelper.instance.iconBlank) {
        ++count;
      } else {
        parts[i - count] = part;
      }
    }
    iconParts -= count;

    if (iconParts <= 0) {
      iconParts = 1;
      parts = new IIcon[] { ToolRenderHelper.instance.iconError };
    }

    Tessellator tess = Tessellator.instance;
    float[] xMax = new float[iconParts];
    float[] yMin = new float[iconParts];
    float[] xMin = new float[iconParts];
    float[] yMax = new float[iconParts];
    float depth = 1f / 16f;

    float[] width = new float[iconParts];
    float[] height = new float[iconParts];
    float[] xDiff = new float[iconParts];
    float[] yDiff = new float[iconParts];
    float[] xSub = new float[iconParts];
    float[] ySub = new float[iconParts];
    for (int i = 0; i < iconParts; ++i) {
      IIcon icon = parts[i];
      xMin[i] = icon.getMinU();
      xMax[i] = icon.getMaxU();
      yMin[i] = icon.getMinV();
      yMax[i] = icon.getMaxV();
      width[i] = icon.getIconWidth();
      height[i] = icon.getIconHeight();
      xDiff[i] = xMin[i] - xMax[i];
      yDiff[i] = yMin[i] - yMax[i];
      xSub[i] = 0.5f * (xMax[i] - xMin[i]) / width[i];
      ySub[i] = 0.5f * (yMax[i] - yMin[i]) / height[i];
    }
    GL11.glPushMatrix();

    // color
    int[] color = new int[iconParts];
    for (int i = 0; i < iconParts; i++)
      color[i] = item.getItem().getColorFromItemStack(item, i);

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);

    if (type == ItemRenderType.INVENTORY) {
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      GL11.glDisable(GL11.GL_BLEND);

      tess.startDrawingQuads();
      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        tess.addVertexWithUV(0, 16, 0, xMin[i], yMax[i]);
        tess.addVertexWithUV(16, 16, 0, xMax[i], yMax[i]);
        tess.addVertexWithUV(16, 0, 0, xMax[i], yMin[i]);
        tess.addVertexWithUV(0, 0, 0, xMin[i], yMin[i]);
      }
      tess.draw();
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_ALPHA_TEST);
      GL11.glEnable(GL11.GL_BLEND);
    } else {
      switch (type) {
        case EQUIPPED_FIRST_PERSON:
          break;
        case EQUIPPED:
          GL11.glTranslatef(0, -4 / 16f, 0);
          break;
        case ENTITY:
          GL11.glRotatef(180, 0, 1, 0);
          if (!noEntityTranslation)
            GL11.glTranslatef(-0.5f, -0.25f, depth); // correction of the rotation point when items lie on the ground
          break;
        default:
      }

      // one side
      tess.startDrawingQuads();
      tess.setNormal(0, 0, 1);
      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        tess.addVertexWithUV(0, 0, 0, xMax[i], yMax[i]);
        tess.addVertexWithUV(1, 0, 0, xMin[i], yMax[i]);
        tess.addVertexWithUV(1, 1, 0, xMin[i], yMin[i]);
        tess.addVertexWithUV(0, 1, 0, xMax[i], yMin[i]);
      }
      tess.draw();

      // other side
      tess.startDrawingQuads();
      tess.setNormal(0, 0, -1);
      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        tess.addVertexWithUV(0, 1, -depth, xMax[i], yMin[i]);
        tess.addVertexWithUV(1, 1, -depth, xMin[i], yMin[i]);
        tess.addVertexWithUV(1, 0, -depth, xMin[i], yMax[i]);
        tess.addVertexWithUV(0, 0, -depth, xMax[i], yMax[i]);
      }
      tess.draw();

      // make it have "depth"
      tess.startDrawingQuads();
      tess.setNormal(-1, 0, 0);
      float pos;
      float iconPos;

      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        float w = width[i], m = xMax[i], d = xDiff[i], s = xSub[i];
        for (int k = 0, e = (int) w; k < e; ++k) {
          pos = k / w;
          iconPos = m + d * pos - s;
          tess.addVertexWithUV(pos, 0, -depth, iconPos, yMax[i]);
          tess.addVertexWithUV(pos, 0, 0, iconPos, yMax[i]);
          tess.addVertexWithUV(pos, 1, 0, iconPos, yMin[i]);
          tess.addVertexWithUV(pos, 1, -depth, iconPos, yMin[i]);
        }
      }

      tess.draw();
      tess.startDrawingQuads();
      tess.setNormal(1, 0, 0);
      float posEnd;

      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        float w = width[i], m = xMax[i], d = xDiff[i], s = xSub[i];
        float d2 = 1f / w;
        for (int k = 0, e = (int) w; k < e; ++k) {
          pos = k / w;
          iconPos = m + d * pos - s;
          posEnd = pos + d2;
          tess.addVertexWithUV(posEnd, 1, -depth, iconPos, yMin[i]);
          tess.addVertexWithUV(posEnd, 1, 0, iconPos, yMin[i]);
          tess.addVertexWithUV(posEnd, 0, 0, iconPos, yMax[i]);
          tess.addVertexWithUV(posEnd, 0, -depth, iconPos, yMax[i]);
        }
      }

      tess.draw();
      tess.startDrawingQuads();
      tess.setNormal(0, 1, 0);

      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        float h = height[i], m = yMax[i], d = yDiff[i], s = ySub[i];
        float d2 = 1f / h;
        for (int k = 0, e = (int) h; k < e; ++k) {
          pos = k / h;
          iconPos = m + d * pos - s;
          posEnd = pos + d2;
          tess.addVertexWithUV(0, posEnd, 0, xMax[i], iconPos);
          tess.addVertexWithUV(1, posEnd, 0, xMin[i], iconPos);
          tess.addVertexWithUV(1, posEnd, -depth, xMin[i], iconPos);
          tess.addVertexWithUV(0, posEnd, -depth, xMax[i], iconPos);
        }
      }

      tess.draw();
      tess.startDrawingQuads();
      tess.setNormal(0, -1, 0);

      for (int i = 0; i < iconParts; ++i) {
        tess.setColorOpaque_I(color[i]);
        float h = height[i], m = yMax[i], d = yDiff[i], s = ySub[i];
        for (int k = 0, e = (int) h; k < e; ++k) {
          pos = k / h;
          iconPos = m + d * pos - s;
          tess.addVertexWithUV(1, pos, 0, xMin[i], iconPos);
          tess.addVertexWithUV(0, pos, 0, xMax[i], iconPos);
          tess.addVertexWithUV(0, pos, -depth, xMax[i], iconPos);
          tess.addVertexWithUV(1, pos, -depth, xMin[i], iconPos);
        }
      }
      
      // FIXME Start
//      if (item.hasEffect(ToolRenderHelper.RENDER_PASS_COUNT - 1))
//      {
//          GL11.glDepthFunc(GL11.GL_EQUAL);
//          GL11.glDisable(GL11.GL_LIGHTING);
//          Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
//          GL11.glEnable(GL11.GL_BLEND);
//          OpenGlHelper.glBlendFunc(768, 1, 1, 0);
//          float f7 = 0.76F;
//          GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
//          GL11.glMatrixMode(GL11.GL_TEXTURE);
//          GL11.glPushMatrix();
//          float f8 = 0.125F;
//          GL11.glScalef(f8, f8, f8);
//          float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
//          GL11.glTranslatef(f9, 0.0F, 0.0F);
//          GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//          renderItemIn2D(tess, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//          GL11.glPopMatrix();
//          GL11.glPushMatrix();
//          GL11.glScalef(f8, f8, f8);
//          f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
//          GL11.glTranslatef(-f9, 0.0F, 0.0F);
//          GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
//          renderItemIn2D(tess, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//          GL11.glPopMatrix();
//          GL11.glMatrixMode(GL11.GL_MODELVIEW);
//          GL11.glDisable(GL11.GL_BLEND);
//          GL11.glEnable(GL11.GL_LIGHTING);
//          GL11.glDepthFunc(GL11.GL_LEQUAL);
//      }
      // FIXME End

      tess.draw();
      GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    GL11.glPopMatrix();
  }

}
