package com.github.alexthe666.biomereactors;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBiomeReactor extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon front;
	private static boolean keepFurnaceInventory = false;
	private final boolean isActive;

	protected BlockBiomeReactor(boolean isActive) {
		super(Material.iron);
		this.isActive = isActive;
		this.setHardness(3);
		this.setHarvestLevel("pickaxe", 1);
		this.setStepSound(Block.soundTypeMetal);
		if(isActive){
			this.setLightLevel(0.5F);
			this.setBlockName("biomereactors.biomereactoroff");
		}else{
			this.setLightLevel(0F);
			this.setBlockName("biomereactors.biomereactoron");
			this.setCreativeTab(CreativeTabs.tabRedstone);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityBiomeReactor();
	}

	public static void updateBlockState(boolean isActive, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		keepFurnaceInventory = true;
		if (isActive) {
			world.setBlock(x, y, z, BiomeReactors.biome_reactor_on);
		} else {
			world.setBlock(x, y, z, BiomeReactors.biome_reactor_off);
		}
		keepFurnaceInventory = false;
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(x, y, z, tileentity);
		}
	}

	@Override
	public Item getItemDropped(int i, Random rand, int l) {
		return Item.getItemFromBlock(BiomeReactors.biome_reactor_off);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.setDefaultDirection(world, x, y, z);
	}

	private void setDefaultDirection(World world, int x, int y, int z) {
		if (!world.isRemote) {
			Block block = world.getBlock(x, y, z - 1);
			Block block1 = world.getBlock(x, y, z + 1);
			Block block2 = world.getBlock(x - 1, y, z);
			Block block3 = world.getBlock(x + 1, y, z);
			byte b0 = 3;

			if (block.func_149730_j() && !block1.func_149730_j()) {
				b0 = 3;
			}

			if (block1.func_149730_j() && !block.func_149730_j()) {
				b0 = 2;
			}

			if (block2.func_149730_j() && !block3.func_149730_j()) {
				b0 = 5;
			}

			if (block3.func_149730_j() && !block2.func_149730_j()) {
				b0 = 4;
			}

			world.setBlockMetadataWithNotify(x, y, z, b0, 2);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("biomereactors:biome_reactor_side");
		this.front = this.isActive ? iconRegister.registerIcon("biomereactors:biome_reactor_front_on") : iconRegister.registerIcon("biomereactors:biome_reactor_front_off");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta == 0 && side == 3)
		{
			return front;
		}
		return side == 1 ? this.blockIcon : (side == 0 ? this.blockIcon : (side != meta ? this.blockIcon : this.front));
	}


	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
		int rotate = MathHelper.floor_double((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if(rotate == 0){
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}

		if(rotate == 1){
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}

		if(rotate == 2){
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}

		if(rotate == 3){
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}

	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int var6, float var7, float var8, float var9) {
		if(world.isRemote){
			return true;
		}else{
			TileEntityBiomeReactor tileentity = (TileEntityBiomeReactor)world.getTileEntity(x, y, z);
			if (tileentity != null) {
				player.openGui(BiomeReactors.instance, 0, world, x, y, z);
			}
			return true;
		}
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(BiomeReactors.biome_reactor_off);
	}

	public void breakBlock(World world, int x, int y, int z, Block block, int i){
		if(!this.keepFurnaceInventory){
			TileEntityBiomeReactor tileentityfurnace = (TileEntityBiomeReactor)world.getTileEntity(x, y, z);
			Random rand = new Random();
			if (tileentityfurnace != null)
			{
				ItemStack itemstack = tileentityfurnace.getStackInSlot(1);

				if (itemstack != null)
				{
					float f = rand.nextFloat() * 0.8F + 0.1F;
					float f1 = rand.nextFloat() * 0.8F + 0.1F;
					float f2 = rand.nextFloat() * 0.8F + 0.1F;

					while (itemstack.stackSize > 0)
					{
						int j1 = rand.nextInt(21) + 10;

						if (j1 > itemstack.stackSize)
						{
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
						if (itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						}
						float f3 = 0.05F;
						entityitem.motionX = (double)((float)rand.nextGaussian() * f3);
						entityitem.motionY = (double)((float)rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double)((float)rand.nextGaussian() * f3);
						world.spawnEntityInWorld(entityitem);
					}
				}
				world.func_147453_f(x, y, z, block);
			}
		}
		super.breakBlock(world, x, y, z, block, i);
	}
}
