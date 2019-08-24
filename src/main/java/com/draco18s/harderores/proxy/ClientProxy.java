package com.draco18s.harderores.proxy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.client.ProspectorParticle;
import com.draco18s.harderores.client.ProspectorParticleDust;
import com.draco18s.harderores.client.rendering.BakedModelBasicSluice;
import com.draco18s.harderores.network.ToClientMessageOreParticles;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;
import com.draco18s.hardlib.client.ModelsCache;
import com.draco18s.hardlib.proxy.IProxy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ClientProxy implements IProxy {
	private enum ParticleTypes {
		DUST,RADAR
	}

	@EventBusSubscriber(modid = HarderOres.MODID, bus = EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
	public static class Registration {
		
		@SubscribeEvent
		public static void onModelBake(ModelBakeEvent ev) {
			/*IDynamicBakedModel bakedModelLoader = new IDynamicBakedModel() {
            @Override
            public boolean isGui3d() {
                return false;
            }

            @Override
            public boolean isBuiltInRenderer() {
                return false;
            }

            @Override
            public boolean isAmbientOcclusion() {
                return false;
            }

            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData modelData) {
                IBakedModel model;
                BlockState facadeState = Blocks.WATER.getDefaultState().with(BlockStateProperties.LEVEL_0_15, modelData.getData(SluiceTileEntity.WATER));
                model = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(facadeState);
                return model.getQuads(facadeState, side, rand, null);

            }

            @Override
            public TextureAtlasSprite getParticleTexture() {
                return MissingTextureSprite.func_217790_a();
            }

            @Override
            public ItemOverrideList getOverrides() {
                return null;
            }

            @Override
            @Nonnull
            public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
                return tileData;
            }
        };
        ev.getModelRegistry().put(new ResourceLocation(HarderOres.MODID,"sluice"), bakedModelLoader);*/

			ModelResourceLocation resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=north,flowing=true");
			IBakedModel water = ev.getModelRegistry().get(resLoc);
			ModelBakery bakery = ev.getModelLoader();
			//IBakedModel planking = ev.getModelRegistry().get(new ModelResourceLocation("harderores:sluice_bottom"));
			IBakedModel planking = ModelsCache.INSTANCE.getOrLoadBakedModel(bakery , new ModelResourceLocation("harderores:sluice_bottom"));

			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=south,flowing=true");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=east,flowing=true");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=west,flowing=true");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));

			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=north,flowing=false");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=south,flowing=false");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=east,flowing=false");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
			resLoc = new ModelResourceLocation(HarderOres.ModBlocks.sluice.getRegistryName(),"facing=west,flowing=false");
			ev.getModelRegistry().put(resLoc, new BakedModelBasicSluice(water,planking));
		}
	}
	@Override
	public <MSG> void spawnParticles(MSG message, Supplier<Context> ctx) {
		ClientPlayerEntity p = Minecraft.getInstance().player;
		List<BlockState> states = new ArrayList<BlockState>();
		ToClientMessageOreParticles msg= ((ToClientMessageOreParticles)message);
		for(BlockPos pos : msg.oresAt) {
			BlockState state = p.world.getBlockState(pos);
			if(!states.contains(state)) {
				states.add(state);
				drawParticle(p.world,getParticle(p.world, pos, msg.eventAt, ParticleTypes.DUST, 0));
			}
			drawParticle(p.world,getParticle(p.world, pos, msg.eventAt, ParticleTypes.RADAR, 0));
		}
	}

	public static void drawParticle(World world, Particle particle) {
		if(particle != null)
			Minecraft.getInstance().particles.addEffect(particle);
	}

	public static Particle getParticle(World world, BlockPos oreAt, BlockPos eventAt, ParticleTypes id, int startingAge) {
		Particle particle = null;
		float x, y, z;
		switch(id) {
		case RADAR:
			x = (float)Math.random() * .4f + 0.3f;
			y = (float)Math.random() * .4f + 0.3f;
			z = (float)Math.random() * .4f + 0.3f;
			particle = new ProspectorParticle(world, oreAt.getX()+x, oreAt.getY()+y, oreAt.getZ()+z, 0, 0, 0);
			break;
		case DUST:
			x = (float)Math.random() * 0.8f + 0.1f;
			y = (float)Math.random() * 0.5f + 0.5f;
			z = (float)Math.random() * 0.8f + 0.1f;
			particle = new ProspectorParticleDust(world, eventAt.getX()+x, eventAt.getY()+y, eventAt.getZ()+z, 0, 0, 0, startingAge);	
			break;
		default:
			return null;
		}
		BlockState state = world.getBlockState(oreAt);
		if(HardLibAPI.hardOres.isHardOre(state)) {
			Block block = state.getBlock();
			Color c = ((IBlockMultiBreak)block).getProspectorParticleColor(world, oreAt, state);
			particle.setColor(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
		}
		return particle;
	}
}
