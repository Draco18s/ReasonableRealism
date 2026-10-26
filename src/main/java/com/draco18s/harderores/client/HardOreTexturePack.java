package com.draco18s.harderores.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.internal.OreNameHelper;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.InclusiveRange;

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
	private static final Identifier OVERLAY = HarderOres.getRL("textures/block/ore/overlay");
	private static final int DENSITIES = 16;

	/** Generated texture (as a resource path) to the stone texture it is cut from. */
	private final Map<Identifier, Source> textures = new LinkedHashMap<Identifier, Source>();

	private record Source(Identifier stone, Identifier overlay) { }

	private HardOreTexturePack() {
		OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName) -> {
			Identifier stone = vanillaStoneTexture.withPath(path -> "textures/" + path + ".png");
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
			public PackMetadataResources openMetadata(PackLocationInfo location) {
				return new HardOreTexturePack();
			}

			@Override
			public Stream<PackResources> openResources(PackLocationInfo location, Pack.Metadata metadata) {
				return Stream.of(new HardOreTexturePack());
			}
		}, PackType.CLIENT_RESOURCES, SELECTION);
	}

	@Override
	@Nullable
	public IoSupplier<InputStream> getResource(PackType type, Identifier location) {
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
			// (Written as a PNG: writeToChannel is opened up by Hard Lib's access transformer.)
			ByteArrayOutputStream png = new ByteArrayOutputStream();
			if(!combined.writeToChannel(Channels.newChannel(png))) throw new IOException("Couldn't write " + source.stone() + " with " + source.overlay());
			return new ByteArrayInputStream(png.toByteArray());
		}
	}

	private static NativeImage read(ResourceManager resources, Identifier location) throws IOException {
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
	public <T> T getMetadataSection(MetadataSectionType<T> type) {
		if(!type.name().equals(PackMetadataSection.CLIENT_TYPE.name())) return null;
		return (T)new PackMetadataSection(Component.literal("Hard ore textures, made from the current stone textures"),
				new InclusiveRange<PackFormat>(SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES)));
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
