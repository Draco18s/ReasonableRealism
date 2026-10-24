package com.draco18s.hardlib.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.draco18s.hardlib.HardLib;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Extra connector lines on the advancements screen, for advancements that need a second parent (vanilla draws only
 * the line to the one tree parent). The requirement itself is a hardlib:advancement_earned criterion; this only draws
 * it. Each link is a widget added as a child of the extra parent's widget, so vanilla draws its line in the normal
 * connector pass: under the icons, inside the tab, under tooltips.
 */
@EventBusSubscriber(modid = HardLib.MODID, value = Dist.CLIENT)
public class AdvancementLinks {
	private record Link(ResourceLocation parent, ResourceLocation child) { }

	private static final List<Link> LINKS = Collections.synchronizedList(new ArrayList<Link>());
	/** Widgets already given their link, for the screen currently open (a new screen makes new widgets). */
	private static final Map<AdvancementWidget, Boolean> LINKED = new WeakHashMap<AdvancementWidget, Boolean>();

	/** Draw a line from parent to child as if parent were also child's tree parent. Both must be in the same tab. */
	public static void add(ResourceLocation parent, ResourceLocation child) {
		LINKS.add(new Link(parent, child));
	}

	@SubscribeEvent
	public static void onScreenRender(ScreenEvent.Render.Pre event) {
		if(!(event.getScreen() instanceof AdvancementsScreen screen) || Minecraft.getInstance().getConnection() == null) return;
		ClientAdvancements advancements = Minecraft.getInstance().getConnection().getAdvancements();
		synchronized(LINKS) {
			for(Link link : LINKS) {
				Advancement parent = advancements.getAdvancements().get(link.parent());
				Advancement child = advancements.getAdvancements().get(link.child());
				if(parent == null || child == null) continue;
				AdvancementWidget parentWidget = screen.getAdvancementWidget(parent);
				AdvancementWidget childWidget = screen.getAdvancementWidget(child);
				// Widgets only exist for advancements the player can see, and only within the same tab.
				if(parentWidget == null || childWidget == null || LINKED.containsKey(parentWidget)) continue;
				parentWidget.addChild(new LinkWidget(parentWidget, childWidget, child));
				LINKED.put(parentWidget, true);
			}
		}
	}

	/** Draws a connector from one widget to another, the same way a widget draws the line to its parent. */
	private static class LinkWidget extends AdvancementWidget {
		private final AdvancementWidget from;
		private final AdvancementWidget to;

		LinkWidget(AdvancementWidget from, AdvancementWidget to, Advancement toAdvancement) {
			super(null, Minecraft.getInstance(), toAdvancement, toAdvancement.getDisplay());
			this.from = from;
			this.to = to;
		}

		@Override
		public void drawConnectivity(PoseStack poseStack, int x, int y, boolean dropShadow) {
			int i = x + from.getX() + 13;
			int j = x + from.getX() + 26 + 4;
			int k = y + from.getY() + 13;
			int l = x + to.getX() + 13;
			int i1 = y + to.getY() + 13;
			int color = dropShadow ? -16777216 : -1;
			if(dropShadow) {
				hLine(poseStack, j, i, k - 1, color);
				hLine(poseStack, j + 1, i, k, color);
				hLine(poseStack, j, i, k + 1, color);
				hLine(poseStack, l, j - 1, i1 - 1, color);
				hLine(poseStack, l, j - 1, i1, color);
				hLine(poseStack, l, j - 1, i1 + 1, color);
				vLine(poseStack, j - 1, i1, k, color);
				vLine(poseStack, j + 1, i1, k, color);
			}
			else {
				hLine(poseStack, j, i, k, color);
				hLine(poseStack, l, j, i1, color);
				vLine(poseStack, j, i1, k, color);
			}
		}
	}
}
