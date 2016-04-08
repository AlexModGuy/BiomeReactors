package com.github.alexthe666.biomereactors;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import biomereactors.api.BiomeMappings;
import biomereactors.api.BiomeMappings.MapEntry;
import cofh.api.energy.TileEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBiomeReactor extends TileEnergyHandler implements ISidedInventory{

	public int biomeTime;
	public ItemStack slot;
	public int ticksExisted;
	public int timesBiomeRefrenced;
	public int soundTicks;

	public TileEntityBiomeReactor()
	{
		this.storage.setCapacity(400000);
	}

	public int getSizeInventory()
	{
		return 1;
	}

	public ItemStack getStackInSlot(int i)
	{
		return slot;
	}

	public ItemStack decrStackSize(int i, int decreaseAmount)
	{
		if (this.slot != null)
		{
			ItemStack itemstack;

			if (this.slot.stackSize <= decreaseAmount)
			{
				itemstack = this.slot;
				this.slot = null;
				return itemstack;
			}
			else
			{
				itemstack = this.slot.splitStack(decreaseAmount);

				if (this.slot.stackSize == 0)
				{
					this.slot = null;
				}

				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int i)
	{
		if (this.slot != null)
		{
			ItemStack itemstack = this.slot;
			this.slot = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.slot = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	public String getInventoryName()
	{
		return "container.biomereactor";
	}

	public boolean hasCustomInventoryName()
	{
		return false;
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		NBTTagList nbttaglist = tag.getTagList("Items", 10);
		NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(0);
		this.slot = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		this.biomeTime = tag.getShort("BiomeTime");
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setShort("BiomeTime", (short)this.biomeTime);
		NBTTagList nbttaglist = new NBTTagList();
		if (this.slot != null)
		{
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound1.setByte("Slot", (byte) 1);
			this.slot.writeToNBT(nbttagcompound1);
			nbttaglist.appendTag(nbttagcompound1);
		}
		tag.setTag("Items", nbttaglist);
	}

	@SideOnly(Side.CLIENT)
	public int getPowerScaled(int i)
	{
		return this.getEnergyStored(null) * i / 400000;
	}

	@SideOnly(Side.CLIENT)
	public int getBiomeScaled(int i)
	{
		return this.biomeTime * i;
	}

	public int getInventoryStackLimit()
	{
		return 64;
	}

	public boolean isPowered()
	{
		return this.getEnergyStored(null) > 0;
	}

	public boolean isChangingBiome()
	{
		return this.biomeTime > 0;
	}

	public boolean canChangeBiome()
	{
		BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(this.xCoord, this.zCoord);
		return biome != getBiomeFromItem() ;
	}

	public boolean isItemApplicable(){
		return getBiomeFromItem() != null;
	}

	public BiomeGenBase getBiomeFromItem(){
		timesBiomeRefrenced++;

		if(slot != null && slot.getItem() != null && slot.getItem() instanceof ItemBlock){
			Block block = ((ItemBlock)slot.getItem()).field_150939_a;
			MapEntry firstMap = BiomeMappings.instance().new MapEntry(block, slot.getItemDamage());
			MapEntry secondMap = BiomeMappings.instance().new MapEntry(block);
			BiomeGenBase first = BiomeMappings.instance().getEntryBiome(firstMap);
			BiomeGenBase second = BiomeMappings.instance().getEntryBiome(secondMap);
			if(first != null){
				return first;
			}
			else if(second != null){
				return second;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	public void updateEntity()
	{
		if(this.getEnergyStored(null) < 0){
			this.setEnergyStore(0);
		}
		if(this.isChangingBiome() && this.isPowered()){
			if(soundTicks < 5){
				soundTicks++;
			}
			if(soundTicks == 5){
				soundTicks = 0;
				this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, "biomereactors:world.biome_reactor", 0.5F, 1);
			}
		}
		
		boolean flag1 = false;
		ticksExisted++;
		if (this.isPowered() && this.biomeTime > 0)
		{
			this.setEnergyStore(this.getEnergyStored(null) - 101);
		}
		if(this.getEnergyStored(null) > 0 && this.getBiomeFromItem() != null && ticksExisted % 40 == 0 && biomeTime < 100){
			this.biomeTime++;
			if(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == BiomeReactors.biome_reactor_off){
				BlockBiomeReactor.updateBlockState(this.getEnergyStored(null) > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}
		if(this.getBiomeFromItem() == null && biomeTime > 0){
			this.biomeTime = 0;
			if(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == BiomeReactors.biome_reactor_on){
				BlockBiomeReactor.updateBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}
		if(biomeTime == 100 && this.getBiomeFromItem() != null){
			this.changeBiome(this.getBiomeFromItem());
			this.biomeTime = 0;
			this.decrStackSize(1, 1);
			if(this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == BiomeReactors.biome_reactor_on){
				BlockBiomeReactor.updateBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}
		if (flag1)
		{
			this.markDirty();
		}	
	}

	public void changeBiome(BiomeGenBase biome) {
		for (int x = this.xCoord - 5; x <= this.xCoord + 5; ++x){
			for (int z = this.zCoord - 5; z <= this.zCoord + 5; ++z){
				this.worldObj.spawnParticle("townaura", x + new Random().nextFloat(), this.yCoord + 0.5D, z + new Random().nextFloat(),  new Random().nextFloat(),  new Random().nextFloat(),  new Random().nextFloat());
				this.worldObj.spawnParticle("townaura", x + new Random().nextFloat(), this.yCoord + 0.5D, z + new Random().nextFloat(),  new Random().nextFloat(),  new Random().nextFloat(),  new Random().nextFloat());
				this.worldObj.spawnParticle("townaura", x + new Random().nextFloat(), this.yCoord + 0.5D, z + new Random().nextFloat(),  new Random().nextFloat(),  new Random().nextFloat(),  new Random().nextFloat());
				this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, "random.fizz", new Random().nextFloat(), 1);
				if(!this.worldObj.isRemote){
					int chunkX = x & 0xF;
					int chunkZ = z & 0xF;
					if(biome != null){
						Chunk chunk = this.worldObj.getChunkFromBlockCoords(x, z);
						chunk.getBiomeArray()[ chunkZ << 4 | chunkX ] = (byte) biome.biomeID;
						chunk.isModified = true;

						BiomeReactors.channel.sendToAll(new MessageChangeBiome(x, z, biome.biomeID));
					}
				}
			}
		}
	}

	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	public boolean isItemValidForSlot(int i, ItemStack stack)
	{
		return true;
	}

	public int[] getAccessibleSlotsFromSide(int i)
	{
		return new int[]{0};
	}

	public boolean canInsertItem(int i, ItemStack stack, int b)
	{
		return this.isItemValidForSlot(i, stack);
	}

	public boolean canExtractItem(int i, ItemStack stack, int side)
	{
		return true;
	}

	public int getMaxEnergyStored(ForgeDirection from) {
		return 400000;
	}

	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		BiomeReactors.channel.sendToAll(new MessageResetEnergy(this.xCoord, this.yCoord, this.zCoord, this.getEnergyStored(null)));
		return super.receiveEnergy(from, maxReceive, simulate);
	}

	public int extractEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		BiomeReactors.channel.sendToAll(new MessageResetEnergy(this.xCoord, this.yCoord, this.zCoord, this.getEnergyStored(null)));
		return super.extractEnergy(from, maxReceive, simulate);
	}

	public void setEnergyStore(int en){
		this.storage.setEnergyStored(en);
	}

	public void openInventory() {}

	public void closeInventory() {}
}
