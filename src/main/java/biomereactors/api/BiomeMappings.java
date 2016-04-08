package biomereactors.api;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.collect.Maps;

public class BiomeMappings {
	private static final BiomeMappings INSTANCE = new BiomeMappings();
	private Map<MapEntry, BiomeGenBase> list;
	
	public static BiomeMappings instance() {
		return INSTANCE;
	}

	public void addToBiomeMappings(MapEntry entry, BiomeGenBase biome) {
		if (list == null) {
			list = Maps.newHashMap();
		}
		if(!list.containsKey(entry)){
			list.put(entry, biome);
		}
	}
	
	/***Use this if you want to remove a vanilla mapping and replace it with your own.***/
	public void removeBiomeMapping(MapEntry entry, BiomeGenBase biome) {
		if (list == null) {
			list = Maps.newHashMap();
		}
		if(!list.containsKey(entry)){
			list.remove(entry);
			list.put(entry, biome);
		}
	}

	public BiomeGenBase getEntryBiome(MapEntry entry) {
		MapEntry olderEntry = null;
		MapEntry[] entries = list.keySet().toArray(new MapEntry[0]);
		for(int i = 0; i < entries.length; i++){
			if(entries[i].matches(entry)){
				olderEntry = entries[i];
				break;
			}
		}
		if (list.containsKey(olderEntry)) {
			return list.get(olderEntry);
		}else{
			return null;
		}
	}
	
	public class MapEntry{
		public Block block;
		public int metadata;
		public MapEntry(Block block, int metadata){
			this.block = block;
			this.metadata = metadata;
		}
		public MapEntry(Block block){
			this.block = block;
			this.metadata = Integer.MAX_VALUE;
		}
		
		public String toString(){
			return "Map Value of Block: " + block.getLocalizedName() + ", Map Value of Meta " + this.metadata;
		}
		
		public boolean matches(MapEntry otherMap){
			boolean b = otherMap.metadata == Integer.MAX_VALUE;
			if(b){
				return otherMap.block.getUnlocalizedName().equals(this.block.getUnlocalizedName());
			}else{
				return otherMap.block.getUnlocalizedName().equals(this.block.getUnlocalizedName()) && otherMap.metadata == this.metadata;

			}
		}
	}
}
