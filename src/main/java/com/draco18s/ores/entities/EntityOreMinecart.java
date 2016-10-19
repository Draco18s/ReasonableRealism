package com.draco18s.ores.entities;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.ores.OreGuiHandler;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.inventory.ContainerOreCart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityOreMinecart extends EntityMinecartContainer {
	private static final DataParameter<Byte> DIRECTION = EntityDataManager.<Byte>createKey(EntityOreMinecart.class,
			DataSerializers.BYTE);
	private static final DataParameter<Float> FULLNESS = EntityDataManager.<Float>createKey(EntityOreMinecart.class,
			DataSerializers.FLOAT);
	private static final Random RANDOM = new Random();

	private BlockPos lastActivator = new BlockPos(0, 0, 0);
	private int timeOnActivator = 0;
	private DumpDir dumpingDirection;
	private float invenFullVal = 0;

	public EntityOreMinecart(World worldIn) {
		super(worldIn);
	}

	public EntityOreMinecart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dumpingDirection = DumpDir.RIGHT;
		this.dataManager.register(DIRECTION, (byte) dumpingDirection.ordinal());
		this.dataManager.register(FULLNESS, 0f);
	}

	public static void func_189681_a(DataFixer p_189681_0_) {
		EntityMinecartContainer.func_189680_b(p_189681_0_, "MinecartOrecart");
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		int px = MathHelper.floor_double(this.posX);
		int py = MathHelper.floor_double(this.posY);
		int pz = MathHelper.floor_double(this.posZ);
		BlockPos blockpos = new BlockPos(px, py, pz);
		IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
		if (iblockstate.getBlock() != Blocks.ACTIVATOR_RAIL || !blockpos.equals(lastActivator)) {
			timeOnActivator = 0;
			lastActivator = blockpos;
		}
		if (!this.worldObj.isRemote) {
			this.setInventoryFullness((float) Container.calcRedstoneFromInventory(this));
		}
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (receivingPower) {
			if (timeOnActivator % 10 == 0) {
				if (this.dumpingDirection == DumpDir.RIGHT)
					dropInventoryItems(this.worldObj, this.posX + Math.signum(this.motionZ), this.posY, this.posZ + Math.signum(this.motionX), this);
				else
					dropInventoryItems(this.worldObj, this.posX - Math.signum(this.motionZ), this.posY, this.posZ - Math.signum(this.motionX), this);
				this.markDirty();
			}
			timeOnActivator++;
		}
	}

	@Override
	public int getSizeInventory() {
		return 20;
	}

	@Override
	public Type getType() {
		return OresBase.oreCartEnum;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		this.addLoot(playerIn);
		return new ContainerOreCart(playerInventory, this, playerIn);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand) {
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player, stack, hand)))
			return true;
		if (!this.worldObj.isRemote) {
			player.openGui(OresBase.instance, OreGuiHandler.ORE_CART, this.worldObj, (int) this.getEntityId(), -1, -1);
		}

		return true;
	}

	private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
		int numDropped = 0;
		for (int i = 0; numDropped < 5 && i < inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);

			if (itemstack != null) {
				spawnItemStack(worldIn, x, y, z, itemstack);
				numDropped++;
				inventory.setInventorySlotContents(i, null);
			}
		}
	}

	public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
		if (worldIn.isRemote)
			return;
		float f = (float) RANDOM.nextGaussian() * 0.2f + 0.4f;
		float f1 = (float) RANDOM.nextGaussian() * 0.2f + 0.4f;
		float f2 = (float) RANDOM.nextGaussian() * 0.2f + 0.4f;

		// while (stack.stackSize > 0) {
		// int i = stack.stackSize;

		// stack.stackSize -= i;
		EntityItem entityitem = new EntityItem(worldIn, MathHelper.floor_double(x) + (double) f, MathHelper.floor_double(y) + (double) f1, MathHelper.floor_double(z) + (double) f2, stack);

		// if (stack.hasTagCompound()) {
		// entityitem.getEntityItem().setTagCompound(stack.getTagCompound().copy());
		// }

		float f3 = 0.05F;
		entityitem.motionX = 0;// RANDOM.nextGaussian() * 0.05000000074505806D;
		entityitem.motionY = 0;// RANDOM.nextGaussian() * 0.05000000074505806D +
								// 0.20000000298023224D;
		entityitem.motionZ = 0;// RANDOM.nextGaussian() * 0.05000000074505806D;
		worldIn.spawnEntityInWorld(entityitem);

		// }
	}

	@Override
	public String getGuiID() {
		return "minecraft:chest";
	}

	public DumpDir getDumpDir() {
		return DumpDir.values()[((Byte) this.dataManager.get(DIRECTION)).byteValue()];
	}

	public void setDumpDir(DumpDir newdir) {
		this.dumpingDirection = newdir;
		this.dataManager.set(DIRECTION, (byte) dumpingDirection.ordinal());
		this.markDirty();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("DumpSide", getDumpDir().ordinal());
		compound.setFloat("invenFullVal", invenFullVal);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setDumpDir(DumpDir.values()[compound.getInteger("DumpSide")]);
		setInventoryFullness(compound.getFloat("invenFullVal"));
	}

	public static enum DumpDir {
		LEFT, RIGHT;
	}

	public float getInventoryFullness() {
		return this.dataManager.get(FULLNESS) * 0.5f;
	}

	public void setInventoryFullness(float val) {
		this.invenFullVal = val;
		this.dataManager.set(FULLNESS, val);
		this.markDirty();
	}

	@Override
	public void killMinecart(DamageSource source) {
		this.setDead();

		if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
			InventoryHelper.dropInventoryItems(this.worldObj, this, this);
			ItemStack itemstack = new ItemStack(OresBase.oreMinecart, 1);

			if (this.getName() != null) {
				itemstack.setStackDisplayName(this.getName());
			}

			this.entityDropItem(itemstack, 0.0F);
		}
	}
}
