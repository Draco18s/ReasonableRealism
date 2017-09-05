package com.draco18s.hardlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.DistanceTraveledTrigger.TravelType;
import com.draco18s.hardlib.api.interfaces.IBlockWithMapper;
import com.draco18s.hardlib.api.interfaces.IItemWithMeshDefinition;
import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * This code is lisenced under a "feel free to take this class and use it" license. The only 
 * provisions are that the package declaration is different as to not cause conflicts in the JVM
 * and that I retain authorship.<br>
 * EasyRegistry and ClientEasyRegistry are both proxy and event handlers. HardLib may be replaced as
 * the reference to any main mod class that treats these two classes as required in order to work:
 * you do not need to replicate the entirety of the HardLib package.<br>
 * Alternatively, you can take HardLib as a dependency. https://minecraft.curseforge.com/projects/hardlib/
 * @author Draco18s
 *
 */
public class EasyRegistry {
	private List<Block> blocksToReg = new ArrayList<Block>();
	private List<Item>  itemsToReg  = new ArrayList<Item>();
	private List<IForgeRegistryEntry> otherItems = new ArrayList<IForgeRegistryEntry>();
	protected HashMap<Block,Item> blockItems = new HashMap<Block,Item>();
	private static Method CriterionRegister;
	
	public static void registerBlock(Block block, String registryname) {
		HardLib.proxy._registerBlock(block, registryname);
	}
	
	public static void registerBlockWithItem(Block block, String registryname) {
		HardLib.proxy._registerBlockWithItem(block, registryname);
	}
	
	public static void registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		HardLib.proxy._registerBlockWithCustomItem(block, iBlock, registryname);
	}
	
	public static <T extends Block & IBlockWithMapper> void registerBlockWithCustomItemAndMapper(T block, ItemBlock iBlock, String registryname) {
		HardLib.proxy._registerBlockWithCustomItemAndMapper(block, iBlock, registryname);
	}

	public static void registerItem(Item item, String registryname) {
		HardLib.proxy._registerItem(item, registryname);
	}

	public static <T extends IMetaLookup> void registerItemWithVariants(Item item, String registryname, T variant) {
		HardLib.proxy._registerItemWithVariants(item, registryname, variant);
	}
	
	public static void registerItem(Item item, ResourceLocation registryname, String unlocalized) {
		HardLib.proxy._registerItem(item, registryname, unlocalized);
	}

	/**
	 * Registers other types of IForgeRegistryEntry items. Currently supports:<br>
	 * <ul><li>Enchantment</li>
	 * </ul>
	 * @param object
	 * @return
	 */
	public static <K extends IForgeRegistryEntry<K>> K registerOther(K object) {
		return HardLib.proxy._registerOther(object);
	}
	
	/**
	 * Registers an item and loads/registers models based on the item's SubItems.
	 * @param item - Must implement IItemWithMeshDefinition
	 * @param registryname
	 */
	public static <T extends Item & IItemWithMeshDefinition> void registerItemWithCustomMeshDefinition(T item, String registryname) {
		HardLib.proxy._registerItemWithCustomMeshDefinition(item, registryname);
	}
	
	/**
	 * Registers a specific model for variant item stack.
	 * @param item - Must implement IItemWithMeshDefinition
	 * @param variantStack
	 */
	public static <T extends Item & IItemWithMeshDefinition> void registerSpecificItemVariantsWithBakery(T item, ItemStack variantStack) {
		HardLib.proxy._registerSpecificItemVariantsWithBakery(item, variantStack);
	}
	
	public void _registerBlock(Block block, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		//GameRegistry.register(block);
		blocksToReg.add(block);
	}

	public void _registerBlockWithItem(Block block, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		ItemBlock ib = new ItemBlock(block);
		ib.setRegistryName(registryname);
		//GameRegistry.register(block);
		blocksToReg.add(block);
		//GameRegistry.register(ib);
		itemsToReg.add(ib);
		blockItems.put(block, ib);
	}

	public void _registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		iBlock.setRegistryName(registryname);
		//GameRegistry.register(block);
		blocksToReg.add(block);
		//GameRegistry.register(iBlock);
		itemsToReg.add(iBlock);
		blockItems.put(block, iBlock);
	}

	public <T extends Block & IBlockWithMapper> void _registerBlockWithCustomItemAndMapper(T block, ItemBlock iBlock, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		iBlock.setRegistryName(registryname);
		//GameRegistry.register(block);
		blocksToReg.add(block);
		//GameRegistry.register(iBlock);
		itemsToReg.add(iBlock);
		blockItems.put(block, iBlock);
	}

	public void _registerItem(Item item, String registryname) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		//GameRegistry.register(item);
		itemsToReg.add(item);
	}
	
	public void _registerItem(Item item, ResourceLocation registryname, String unlocalized) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(unlocalized);
		itemsToReg.add(item);
	}

	public <T extends Item & IItemWithMeshDefinition> void _registerItemWithCustomMeshDefinition(T item, String registryname) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		//GameRegistry.register(item);
		itemsToReg.add(item);
	}
	
	public <T extends Item & IItemWithMeshDefinition> void _registerSpecificItemVariantsWithBakery(T item, ItemStack variantStack) {
		//client only
	}

	public <T extends IMetaLookup> void _registerItemWithVariants(Item item, String registryname, T variant) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		//GameRegistry.register(item);
		itemsToReg.add(item);
	}

	public <K extends IForgeRegistryEntry<K>> K _registerOther(K object) {
		otherItems.add(object);
		return object; 
	}
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
	    event.getRegistry().registerAll(blocksToReg.toArray(new Block[blocksToReg.size()]));
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
	    event.getRegistry().registerAll(itemsToReg.toArray(new Item[itemsToReg.size()]));
	}
	
	@SubscribeEvent
	public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
		for(IForgeRegistryEntry e : otherItems) {
			if(e instanceof Enchantment) call(event, e);
		}
	}
	
	private <K extends IForgeRegistryEntry<K>, T> void call(RegistryEvent.Register<K> event, T value) {
		event.getRegistry().register((K) value);
	}

	public static <T extends ICriterionInstance> ICriterionTrigger<T> registerAdvancementTrigger(ICriterionTrigger<T> trigger) {
		if(CriterionRegister == null) {
			CriterionRegister = ReflectionHelper.findMethod(CriteriaTriggers.class, "register", "func_192118_a", ICriterionTrigger.class);
			CriterionRegister.setAccessible(true);
		}
		try {
			trigger = (ICriterionTrigger<T>) CriterionRegister.invoke(null, trigger);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Failed to register trigger " + trigger.getId() + "!");
			e.printStackTrace();
		}
		return trigger;
	}
	
	@SubscribeEvent
	public void onLivingUpdateEvent(PlayerTickEvent event) {
		if(event.phase == Phase.END && event.player instanceof EntityPlayerMP) {
			StatisticsManagerServer stats = ((EntityPlayerMP) event.player).getStatFile();
			int dist = stats.readStat(StatList.MINECART_ONE_CM);
			HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((EntityPlayerMP) event.player, dist/100f, TravelType.RAIL);
			dist = stats.readStat(StatList.BOAT_ONE_CM);
			HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((EntityPlayerMP) event.player, dist/100f, TravelType.BOAT);
			dist = stats.readStat(StatList.HORSE_ONE_CM);
			HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((EntityPlayerMP) event.player, dist/100f, TravelType.HORSE);
			dist = stats.readStat(StatList.PIG_ONE_CM);
			HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((EntityPlayerMP) event.player, dist/100f, TravelType.PIG);
			dist = stats.readStat(StatList.WALK_ONE_CM);
			HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((EntityPlayerMP) event.player, dist/100f, TravelType.WALK);
			dist = stats.readStat(StatList.FLY_ONE_CM);
			HardLibAPI.Advancements.DISTANCE_TRAVELED.trigger((EntityPlayerMP) event.player, dist/100f, TravelType.FLY);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == Phase.END && event.side == Side.SERVER) {
			long time = event.player.world.getWorldTime();
			if(time % 1000 == 0)
				HardLibAPI.Advancements.WORLD_TIME.trigger((EntityPlayerMP)event.player, time);
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player instanceof EntityPlayerMP) {
			HardLibAPI.Advancements.BLOCK_BREAK.trigger((EntityPlayerMP)player, event.getState());
		}
	}
}