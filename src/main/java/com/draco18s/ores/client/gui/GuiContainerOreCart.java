package com.draco18s.ores.client.gui;

import java.util.Iterator;

import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.EntityOreMinecart;
import com.draco18s.ores.entities.EntityOreMinecart.DumpDir;
import com.draco18s.ores.entities.TileEntitySifter;
import com.draco18s.ores.inventory.ContainerOreCart;
import com.draco18s.ores.networking.ToServerMessageOreCart;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiContainerOreCart extends GuiContainer {
	private static ResourceLocation ORECART_GUI_TEXTURE;
	protected EntityOreMinecart entity; //because this is client side, we can do this
	protected Container container;

	public GuiContainerOreCart(Container inventorySlotsIn, EntityOreMinecart te) {
		super(inventorySlotsIn);
		ORECART_GUI_TEXTURE = new ResourceLocation("harderores:textures/gui/ore_cart.png");
		ySize = 184;
		entity = te;
	}

	public void initGui() {
		super.initGui();
		buttonList.add(new DropSideButton(0, this.guiLeft + 123, this.guiTop + 69));
		buttonList.add(new DropSideButton(1, this.guiLeft + 145, this.guiTop + 69));
	}
	
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(new TextComponentTranslation("container.harderores:ore_cart", new Object[0]).getUnformattedText(), 8, 6, 4210752);
		this.fontRenderer.drawString(new TextComponentTranslation("container.inventory", new Object[0]).getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		
		this.fontRenderer.drawString(new TextComponentTranslation("container.harderores:drop_side", new Object[0]).getUnformattedText(), 117, 52, 4210752);
		
		Iterator<GuiButton> iterator = this.buttonList.iterator();

		//this.mc.getTextureManager().bindTexture(ORECART_GUI_TEXTURE);
		
		while (iterator.hasNext()) {
			DropSideButton guibutton = (DropSideButton)iterator.next();
			guibutton.setCurrentState(entity.getDumpDir());
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

		this.mc.getTextureManager().bindTexture(ORECART_GUI_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	protected void actionPerformed(GuiButton button) {
		if(button.id == 0 || button.id == 1) {
			DropSideButton sideBtn = (DropSideButton)button;
			DumpDir dir = DumpDir.values()[sideBtn.id];
			//System.out.println("===###Sending packet to server###===");
			OresBase.networkWrapper.sendToServer(new ToServerMessageOreCart(entity.getEntityId(), dir));
			//IndustryBase.networkWrapper.sendToServer(new CtoSMessage(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord, t.ordinal()));
			entity.setDumpDir(dir);
		}
	}
	
	@SideOnly(Side.CLIENT)
	static class DropSideButton extends GuiButton {
		private DumpDir curState;
		protected DropSideButton(int buttonID, int posx, int posy) {
			super(buttonID, posx, posy, 22, 22, "");
		}
		
		public void setCurrentState(DumpDir dir) {
			curState = dir;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			int k = 176;
			int short1 = 0;
			
			boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			//mc.getTextureManager().bindTexture();
			
			if(this.id == 1) {
				k += 22;
			}
			//DumpDir curState = GuiContainerOreCart.entity.getDumpDir();
			if(curState != null && this.id == curState.ordinal()) {
				short1 += 22;
			}
			if(flag) {
				short1 += 44;
			}
			
			this.drawTexturedModalRect(this.x, this.y, k, short1, this.width, this.height);
		}
	}
}
