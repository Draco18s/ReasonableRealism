package com.draco18s.industry.client.gui;

import org.apache.logging.log4j.Level;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.entity.FilterTileEntity;
import com.draco18s.industry.entity.FilterTileEntity.EnumAcceptType;
import com.draco18s.industry.inventory.FilterContainer;
import com.draco18s.industry.network.PacketHandler;
import com.draco18s.industry.network.ToServerFilterClick;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FilterGuiContainer extends ContainerScreen<FilterContainer> {

	FilterTileEntity tileEntity;
	Container container;
	private static ResourceLocation HOPPER_GUI_TEXTURE;

	public FilterGuiContainer(FilterContainer screenContainer, PlayerInventory inv, ITextComponent containerName) {
		super(screenContainer, inv, containerName);
		tileEntity = (FilterTileEntity)screenContainer.tileEntity;
		HOPPER_GUI_TEXTURE = new ResourceLocation("expindustry:textures/gui/filter.png");
		this.ySize = 160;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new LogicButton(this.guiLeft + 142, this.guiTop + 45, this::actionPerformed, tileEntity.getAcceptType()));
	}

	@Override
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground();
		super.render(p_render_1_, p_render_2_, p_render_3_);
		this.renderHoveredToolTip(p_render_1_, p_render_2_);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.font.drawString(tileEntity.getDisplayName().getFormattedText(), 8, 6, 4210752);
		//this.font.drawString(new TranslationTextComponent("container.inventory", new Object[0]).getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
		this.font.drawString(new TranslationTextComponent("gui.expindustry:rules_header").getFormattedText(), 8, 37, 4210752);
		
		if(!tileEntity.doIHaveFilters()) {
			this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
			GlStateManager.disableLighting();
			GlStateManager.color4f(1, 1, 1, 1);
			for (int i = 0; i < 5; ++i) {
				blit(8 + i * 18, 17, 176, 0, 16, 16);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

		this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.blit(x, y, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(Button button) {
		ExpandedIndustry.LOGGER.log(Level.DEBUG, "Button clicked");
		if(button instanceof LogicButton) {
			LogicButton lb = (LogicButton)button;
			EnumAcceptType t = lb.cycleType();
			tileEntity.setEnumType(t);
			PacketHandler.sendToServer(new ToServerFilterClick(0, tileEntity.getPos(), t.ordinal()));
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class LogicButton extends Button {
		private EnumAcceptType type;

		public LogicButton(int xIn, int yIn, IPressable onPress, EnumAcceptType acceptType) {
			super(xIn, yIn, 24, 22, "", onPress);
			type = acceptType;
		}

		public EnumAcceptType cycleType() {
			type = EnumAcceptType.values()[(type.ordinal()+1)%4];
			return type;
		}

		@Override
		public void renderButton(int x, int y, float partialTicks) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getInstance().getTextureManager().bindTexture(FilterGuiContainer.HOPPER_GUI_TEXTURE);
			short short1 = 160;
			int k = this.width * type.ordinal() + 48;
			int h = this.getYImage(this.isHovered()) - 1;
			this.blit(this.x, this.y, this.width*h, short1, this.width, this.height);
			
			this.blit(this.x, this.y, k, short1, this.width, this.height);
		}
	}
}
