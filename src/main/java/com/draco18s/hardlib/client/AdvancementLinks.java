package com.draco18s.hardlib.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.draco18s.hardlib.HardLib;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

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
				AdvancementNode parent = advancements.getTree().get(link.parent());
				AdvancementNode child = advancements.getTree().get(link.child());
				if(parent == null || child == null || child.holder().value().display().isEmpty()) continue;
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

		LinkWidget(AdvancementWidget from, AdvancementWidget to, AdvancementNode toAdvancement) {
			super(null, Minecraft.getInstance(), toAdvancement, toAdvancement.holder().value().display().get());
			this.from = from;
			this.to = to;
		}

		@Override
		public void drawConnectivity(GuiGraphics graphics, int x, int y, boolean dropShadow) {
			int i = x + from.getX() + 13;
			int j = x + from.getX() + 26 + 4;
			int k = y + from.getY() + 13;
			int l = x + to.getX() + 13;
			int i1 = y + to.getY() + 13;
			int color = dropShadow ? -16777216 : -1;
			if(dropShadow) {
				graphics.hLine(j, i, k - 1, color);
				graphics.hLine(j + 1, i, k, color);
				graphics.hLine(j, i, k + 1, color);
				graphics.hLine(l, j - 1, i1 - 1, color);
				graphics.hLine(l, j - 1, i1, color);
				graphics.hLine(l, j - 1, i1 + 1, color);
				graphics.vLine(j - 1, i1, k, color);
				graphics.vLine(j + 1, i1, k, color);
			}
			else {
				graphics.hLine(j, i, k, color);
				graphics.hLine(l, j, i1, color);
				graphics.vLine(j, i1, k, color);
			}
		}
	}
}
