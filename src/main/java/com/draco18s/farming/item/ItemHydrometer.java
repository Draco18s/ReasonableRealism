package com.draco18s.farming.item;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.internal.CropWeatherOffsets;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHydrometer extends Item {
	public ItemHydrometer() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
		this.setMaxDamage(0);
		
		this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter()
		{
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
				if (world == null && entity != null) {
					world = entity.worldObj;
				}
				if(world == null || entity == null) {
					return 6;
				}
				NBTTagCompound tag = stack.getTagCompound();
				
				Biome bio = world.getBiomeGenForCoords(new BlockPos(entity.posX, 0, entity.posZ));
				float t = bio.getRainfall();

				int flat = 0;
				int time = 0;
				if(tag != null) {
					flat = tag.getInteger("rainflat");
					time = tag.getInteger("raintime");
				}
				//TODO: time offset?
				t += flat;
				float n = Math.round((t-0.575f) * 8);
				n = 7 - Math.max(Math.min(n, 7),0);
				return n;
			}
		});
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = world.getBlockState(pos).getBlock();
		if(HardLibAPI.hardCrops.isCropBlock(block)) {
			NBTTagCompound tag = stack.getTagCompound();
			if(tag == null) {
				tag = new NBTTagCompound();
			}
			CropWeatherOffsets offsets = HardLibAPI.hardCrops.getCropOffsets(block);

			tag.setBoolean("HasOffsets", true);
			tag.setFloat("rainflat", offsets.rainfallFlat);
			tag.setFloat("tempflat", offsets.temperatureFlat);
			tag.setFloat("raintime", offsets.rainfallTimeOffset);
			tag.setFloat("temptime", offsets.temperatureTimeOffset);
			
			Item item = Item.getItemFromBlock(block);
			if (block == Blocks.WHEAT){
				item = (Items.WHEAT);
			}
			if (block == Blocks.PUMPKIN_STEM){
				item = Item.getItemFromBlock(Blocks.PUMPKIN);
			}
			if (block == Blocks.MELON_STEM){
				item = Item.getItemFromBlock(Blocks.MELON_BLOCK);
			}
			if(block == Blocks.REEDS) {
				item = Items.REEDS;
			}
			tag.setString("linkedCropName", I18n.format(item.getUnlocalizedName()+".name"));
			stack.setTagCompound(tag);
		}
		return EnumActionResult.PASS;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
		NBTTagCompound tag = stack.getTagCompound();
		if(tag != null) {
			tooltip.add(TextFormatting.ITALIC + I18n.format("tooltip.linkedcrop.text") + " " + tag.getString("linkedCropName"));
			tooltip.add(I18n.format("tooltip.unlink.text"));
		}
		else {
			tooltip.add(I18n.format("tooltip.link.text"));
		}
	}
}
