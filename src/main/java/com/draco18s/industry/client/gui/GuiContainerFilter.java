package com.draco18s.industry.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.draco18s.industry.ExpandedIndustryBase;
import com.draco18s.industry.entities.TileEntityFilter;
import com.draco18s.industry.entities.TileEntityFilter.EnumAcceptType;
import com.draco18s.industry.inventory.ContainerFilter;
import com.draco18s.industry.network.CtoSMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiContainerFilter extends GuiContainer {
	private TileEntityFilter te;
	private static ResourceLocation texture;
	public static GuiContainerFilter self;
	
	public GuiContainerFilter(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		self = this;
	}
	
	public GuiContainerFilter (InventoryPlayer inventoryPlayer, TileEntityFilter tileEntity) {
		//the container is instanciated and passed to the superclass for handling
		super(new ContainerFilter(inventoryPlayer, tileEntity));
		texture = new ResourceLocation("expindustry:textures/gui/filter.png");
		te = tileEntity;
		ySize = 160;
		self = this;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new LogicButton(0, this.guiLeft + 142, this.guiTop + 45, te.getEnumType()));
		buttonList.add(new HintButton(1, this.guiLeft + 157, this.guiTop + 3));
	}
	
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		fontRenderer.drawString(new TextComponentTranslation(te.getName()).getUnformattedText(), 8, 6, 4210752);
		fontRenderer.drawString(new TextComponentTranslation("container.inventory").getUnformattedText(), 8, this.ySize - 94, 4210752);
		fontRenderer.drawString(new TextComponentTranslation("gui.expindustry:rules_header").getUnformattedText(), 8, 37, 4210752);
		if(!te.doIHaveFilters()) {
			this.mc.renderEngine.bindTexture(texture);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor3f(1, 1, 1);
			for (int i = 0; i < te.getSizeInventory(); ++i) {
				drawTexturedModalRect(8 + i * 18, 17, 176, 0, 16, 16);
			}
			fontRenderer.drawString(new TextComponentTranslation("gui.expindustry:no_accept1").getUnformattedText(), 100, 17, 4210752);
			fontRenderer.drawString(new TextComponentTranslation("gui.expindustry:no_accept2").getUnformattedText(), 100, 26, 4210752);
		}
		Iterator<GuiButton> iterator = this.buttonList.iterator();

		while (iterator.hasNext())
		{
			GuiButton guibutton = iterator.next();

			if (guibutton.isMouseOver())
			{
				guibutton.drawButtonForegroundLayer(param1 - this.guiLeft, param2 - this.guiTop);
				break;
			}
		}
	}
	
	public void drawText(String p_146279_1_, int p_146279_2_, int p_146279_3_) {
		this.drawHoveringText(p_146279_1_, p_146279_2_, p_146279_3_);
	}

	public void drawText(List p_146279_1_, int p_146279_2_, int p_146279_3_) {
		this.drawHoveringText(p_146279_1_, p_146279_2_, p_146279_3_);
	}
	
	protected void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			LogicButton lb = (LogicButton)button;
			EnumAcceptType t = lb.cycleType();
			ExpandedIndustryBase.networkWrapper.sendToServer(new CtoSMessage(te.getWorld().provider.getDimension(), te.getPos(), t.ordinal()));
		}
	}
	
	@SideOnly(Side.CLIENT)
	static class LogicButton extends GuiButton {
		private EnumAcceptType type;
		private static final String __OBFID = "CL_00000743";

		protected LogicButton(int buttonID, int posx, int posy, EnumAcceptType t) {
			super(buttonID, posx, posy, 24, 22, "");
			type = t;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				short short1 = 160;
				int k = this.width * type.ordinal() + 48;
				int h = this.getHoverState(this.hovered) - 1;
				this.drawTexturedModalRect(this.x, this.y, this.width*h, short1, this.width, this.height);

				/*if (!texture.equals(this.buttonTexture)) {
					p_146112_1_.getTextureManager().bindTexture(this.buttonTexture);
				}*/

				this.drawTexturedModalRect(this.x, this.y, k, short1, this.width, this.height);
			}
		}

		@Override
		public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_) {
			List list = new ArrayList<String>();
			list.add(I18n.format("gui.expindustry:logic_type"));
			list.add(I18n.format("gui.expindustry:"+this.type.name().toLowerCase()));
			GuiContainerFilter.self.drawText(list, p_146111_1_-180, p_146111_2_+16);
			//GuiContainerFilter.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), p_146111_1_, p_146111_2_);
		}

		public EnumAcceptType cycleType() {
			this.type = newType();
			return this.type;
		}

		private EnumAcceptType newType() {
			return EnumAcceptType.values()[(type.ordinal()+1)%4];
		}
	}



	@SideOnly(Side.CLIENT)
	static class HintButton extends GuiButton {
		private static final String __OBFID = "CL_00000743";

		protected HintButton(int buttonID, int posx, int posy) {
			super(buttonID, posx, posy, 16, 16, "");
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				//short short1 = 160;
				//int k = this.width;
				//int h = this.getHoverState(this.field_146123_n) - 1;
				//this.drawTexturedModalRect(this.xPosition, this.yPosition, this.width*h, short1, this.width, this.height);

				//this.drawTexturedModalRect(this.xPosition, this.yPosition, k, short1, this.width, this.height);
				this.drawTexturedModalRect(this.x, this.y, 0, 182, this.width, this.height);
			}
		}

		@Override
		public void drawButtonForegroundLayer(int p_146111_1_, int p_146111_2_) {
			List list = new ArrayList<String>();
			list.add(I18n.format("gui.expindustry:filter_explain1"));
			list.add(I18n.format("gui.expindustry:filter_explain2"));
			list.add(I18n.format("gui.expindustry:filter_explain3"));
			list.add(I18n.format("gui.expindustry:filter_explain4"));
			GuiContainerFilter.self.drawText(list, p_146111_1_-200, p_146111_2_+16);
			//GuiContainerFilter.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), p_146111_1_, p_146111_2_);
		}
	}
}
