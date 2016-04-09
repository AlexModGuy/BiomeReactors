package com.github.alexthe666.biomereactors;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "biomereactors", version = "1.0.1", name = "Biome Reactors")
public class BiomeReactors
{
    @Instance
    public static BiomeReactors instance;
    public static SimpleNetworkWrapper channel;
    public static Block biome_reactor_off;
    public static Block biome_reactor_on;
	public static int config_energy_needed;
	public static boolean config_change_worldgen;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        config_energy_needed = config.get("Biome Reactors", "Ammount of RF needed for Biome Conversion", 400000).getInt(400000);
        config_change_worldgen = config.get("Biome Reactors", "Should changing the biome change the nearby World Gen?", false).getBoolean(false); 
        config.save();
        channel = NetworkRegistry.INSTANCE.newSimpleChannel("biomereactors");
        channel.registerMessage(MessageResetEnergy.Handler.class, MessageResetEnergy.class, 0, Side.CLIENT);
        channel.registerMessage(MessageChangeBiome.Handler.class, MessageChangeBiome.class, 1, Side.CLIENT);
    	BaseBiomeMappings.init();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    	biome_reactor_off = new BlockBiomeReactor(false);
    	biome_reactor_on = new BlockBiomeReactor(true);
    	GameRegistry.registerBlock(biome_reactor_off, "biome_reactor_off");
    	GameRegistry.registerBlock(biome_reactor_on, "biome_reactor_on");
    	GameRegistry.addRecipe(new ShapedOreRecipe(biome_reactor_off, "XYX", "ZUZ", "VYV", 'X', "dustGlowstone", 'Y', "ingotIron", 'Z', "gemDiamond", 'U', Items.bucket, 'V', "ingotGold"));
    	GameRegistry.registerTileEntity(TileEntityBiomeReactor.class, "biome_reactor");
    }
}
