package com.github.alexthe666.biomereactors;

import java.text.NumberFormat;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBiomeReactor extends GuiContainer
{
    private static final ResourceLocation texture = new ResourceLocation("biomereactors:textures/gui/biome_reactor.png");
    
    private TileEntityBiomeReactor reactor;

    public GuiBiomeReactor(InventoryPlayer playerInv, TileEntityBiomeReactor reactor)
    {
        super(new ContainerBiomeReactor(playerInv, reactor));
        this.reactor = reactor;
    }

    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String s = this.reactor.hasCustomInventoryName() ? this.reactor.getInventoryName() : I18n.format(this.reactor.getInventoryName(), new Object[0]);
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.biomereactor.rf") + " " + NumberFormat.getIntegerInstance().format(reactor.getEnergyStored(null)), 8, this.ySize - 105, 0XFFD800, true);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.biomereactor.prog") + " " + NumberFormat.getIntegerInstance().format(reactor.biomeTime) + "%", 8, this.ySize - 115, 0X328913, true);

    }

    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

            int i1 = reactor.getPowerScaled(68);
            this.drawTexturedModalRect(k + 151, l + 72 - i1, 176, 65 - i1, 19, i1);

    }
}