package com.github.alexthe666.biomereactors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerBiomeReactor extends Container
{
    private TileEntityBiomeReactor tileFurnace;
    private int lastPowerTime;
    private int lastBiomeTime;
    private int lastItemBurnTime;

    public ContainerBiomeReactor(InventoryPlayer playerInv, TileEntityBiomeReactor reactorInv)
    {
        this.tileFurnace = reactorInv;
        this.addSlotToContainer(new Slot(reactorInv, 0, 80, 23));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    public void addCraftingToCrafters(ICrafting crafter)
    {
        super.addCraftingToCrafters(crafter);
        crafter.sendProgressBarUpdate(this, 0, this.tileFurnace.getEnergyStored(null));
        crafter.sendProgressBarUpdate(this, 1, this.tileFurnace.biomeTime);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastPowerTime != this.tileFurnace.getEnergyStored(null))
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileFurnace.getEnergyStored(null));
            }

            if (this.lastBiomeTime != this.tileFurnace.biomeTime)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileFurnace.biomeTime);
            }
        }

        this.lastPowerTime = this.tileFurnace.getEnergyStored(null);
        this.lastBiomeTime = this.tileFurnace.biomeTime;
    }

    public boolean canInteractWith(EntityPlayer player)
    {
        return this.tileFurnace.isUseableByPlayer(player);
    }

    public ItemStack transferStackInSlot(EntityPlayer entity, int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(i);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (i == 0)
            {
                if (!this.mergeItemStack(itemstack1, 1, 37, true))
                {
                    return null;
                }
            }
            else
            {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(itemstack1))
                {
                    return null;
                }

                if (itemstack1.hasTagCompound() && itemstack1.stackSize == 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(itemstack1.copy());
                    itemstack1.stackSize = 0;
                }
                else if (itemstack1.stackSize >= 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(itemstack1.getItem(), 1, itemstack1.getItemDamage()));
                    --itemstack1.stackSize;
                }
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(entity, itemstack1);
        }

        return itemstack;
    }
}