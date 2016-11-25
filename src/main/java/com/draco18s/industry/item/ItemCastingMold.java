package com.draco18s.industry.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCastingMold extends Item {
	public ItemCastingMold() {
		//setHasSubtypes(true);
		setMaxDamage(32);
		setNoRepair();
		setCreativeTab(CreativeTabs.MATERIALS);
		
		this.addPropertyOverride(new ResourceLocation("state"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				if(!stack.hasTagCompound())
					return 0;
				else
					return 1;
			}
		});
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		ItemStack base = new ItemStack(itemIn);
		subItems.add(base);
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_AXE)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_HOE)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_PICKAXE)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_SHOVEL)));
		
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_SWORD)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_HELMET)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_CHESTPLATE)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_LEGGINGS)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.IRON_BOOTS)));

		subItems.add(addImprint(base.copy(), new ItemStack(Items.SHEARS)));
		subItems.add(addImprint(base.copy(), new ItemStack(Items.BUCKET)));
		subItems.add(addImprint(base.copy(), new ItemStack(Blocks.RAIL, 16)));
    }
	
	private static ItemStack addImprint(ItemStack out, ItemStack imprint) {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound itemTag = new NBTTagCompound();
		imprint.writeToNBT(itemTag);
		nbt.setTag("expindustry:item_mold", itemTag);
		out.setTagCompound(nbt);
		return out;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
			ItemStack result = ItemStack.loadItemStackFromNBT(itemTags);
			tooltip.add(I18n.format("tooltip.expindustry:imprint.text") + ": " + result.getDisplayName());
		}
	}
}
