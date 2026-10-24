package com.draco18s.harderores.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.internal.OreNameHelper;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * A built-in resource pack whose hard ore textures are made as they're loaded: for each density, the stone texture
 * where that density's overlay is opaque (so the stone hides more of the vanilla ore underneath as the ore runs out).
 * The stone and overlay textures are read through the resource manager, so the hard ores follow resource packs.
 * (Replaces Dynamic Asset Generator, which has no Forge 1.21 build.)
 */
public class HardOreTexturePack implements PackResources {
	private static final String ID = HarderOres.MODID + "_hard_ore_textures";
	private static final PackLocationInfo LOCATION = new PackLocationInfo(ID, Component.literal("Harder Ores textures"), PackSource.BUILT_IN, Optional.empty());
	/** Always on and out of the way: below the user's packs, so a pack can still replace a generated texture outright. */
	private static final PackSelectionConfig SELECTION = new PackSelectionConfig(true, Pack.Position.BOTTOM, true);
	private static final ResourceLocation OVERLAY = HarderOres.getRL("textures/block/ore/overlay");
	private static final int DENSITIES = 16;

	/** Generated texture (as a resource path) to the stone texture it is cut from. */
	private final Map<ResourceLocation, Source> textures = new LinkedHashMap<ResourceLocation, Source>();

	private record Source(ResourceLocation stone, ResourceLocation overlay) { }

	private HardOreTexturePack() {
		OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName) -> {
			ResourceLocation stone = vanillaStoneTexture.withPath(path -> "textures/" + path + ".png");
			for(int i = 1; i <= DENSITIES; i++) {
				String sfx = "_" + i;
				textures.put(harderOreBlockName.withPath(path -> "textures/block/" + path + sfx + ".png"),
						new Source(stone, OVERLAY.withSuffix(sfx + ".png")));
			}
		});
	}

	/** The pack, for AddPackFindersEvent (client resources). */
	public static Pack create() {
		return Pack.readMetaAndCreate(LOCATION, new Pack.ResourcesSupplier() {
			@Override
			public PackResources openPrimary(PackLocationInfo location) {
				return new HardOreTexturePack();
			}

			@Override
			public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
				return new HardOreTexturePack();
			}
		}, PackType.CLIENT_RESOURCES, SELECTION);
	}

	@Override
	@Nullable
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		Source source = type == PackType.CLIENT_RESOURCES ? textures.get(location) : null;
		return source == null ? null : () -> generate(source);
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
		if(type != PackType.CLIENT_RESOURCES) return;
		String prefix = path.endsWith("/") ? path : path + "/";
		textures.forEach((location, source) -> {
			if(location.getNamespace().equals(namespace) && location.getPath().startsWith(prefix)) {
				output.accept(location, () -> generate(source));
			}
		});
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return type == PackType.CLIENT_RESOURCES ? Set.of(HarderOres.MODID) : Set.of();
	}

	/**
	 * Reads the inputs from the resource manager. Textures load during a reload, after the manager has switched to
	 * the new pack list, so this sees the packs being loaded (none of which can be asking this pack for stone).
	 */
	private static InputStream generate(Source source) throws IOException {
		ResourceManager resources = Minecraft.getInstance().getResourceManager();
		try(NativeImage stone = read(resources, source.stone()); NativeImage overlay = read(resources, source.overlay());
				NativeImage combined = ImageOverlay.combinedImage(stone, overlay)) {
			return new ByteArrayInputStream(combined.asByteArray());
		}
	}

	private static NativeImage read(ResourceManager resources, ResourceLocation location) throws IOException {
		Resource resource = resources.getResource(location).orElseThrow(() -> new IOException("Missing texture " + location));
		try(InputStream in = resource.open()) {
			return NativeImage.read(in);
		}
	}

	@Override
	@Nullable
	public IoSupplier<InputStream> getRootResource(String... path) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
		if(!serializer.getMetadataSectionName().equals(PackMetadataSection.TYPE.getMetadataSectionName())) return null;
		return (T)new PackMetadataSection(Component.literal("Hard ore textures, made from the current stone textures"),
				SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES), Optional.empty());
	}

	@Override
	public PackLocationInfo location() {
		return LOCATION;
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public void close() { }
}
