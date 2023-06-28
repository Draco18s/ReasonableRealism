package com.draco18s.harderores.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.lukebemish.dynamicassetgenerator.api.ResourceGenerationContext;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.ITexSource;
import dev.lukebemish.dynamicassetgenerator.api.client.generators.TexSourceDataHolder;
import net.minecraft.server.packs.resources.IoSupplier;

public record MaskOverlayGenerator(ITexSource original, ITexSource overlay) implements ITexSource {
	public static final Codec<MaskOverlayGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ITexSource.CODEC.fieldOf("overlay").forGetter(s->s.overlay),
			ITexSource.CODEC.fieldOf("original").forGetter(s->s.original)
			).apply(instance, MaskOverlayGenerator::new));

	@Override
	public Codec<? extends ITexSource> codec() {
		return CODEC;
	}

	@Override
	public @Nullable IoSupplier<NativeImage> getSupplier(TexSourceDataHolder data, ResourceGenerationContext context) {
		OverlayPlanner planner = OverlayPlanner.of(this, data, context);

		return () -> {
			try (
					NativeImage rImg = planner.original.get();
					NativeImage oImg = planner.overlay.get()
					) {
				return ImageOverlay.combinedImage(rImg, oImg);
			}
		};
	}

	public static class OverlayPlanner {
		private IoSupplier<NativeImage> overlay;
		private IoSupplier<NativeImage> original;

		private OverlayPlanner() { }

		public static OverlayPlanner of(MaskOverlayGenerator info, TexSourceDataHolder data, ResourceGenerationContext context) {
			OverlayPlanner out = new OverlayPlanner();
			out.original = info.original.getSupplier(data, context);
			out.overlay = info.overlay.getSupplier(data, context);
			return out;
		}
	}
}
