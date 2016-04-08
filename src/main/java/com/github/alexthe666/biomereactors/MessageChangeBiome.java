package com.github.alexthe666.biomereactors;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageChangeBiome implements IMessage{
	private int x;
	private int z;
	private int biomeID;
	
	public MessageChangeBiome(){}

	public MessageChangeBiome(int x, int z, int biomeID) 
	{
		this.x = x;
		this.z = z;
		this.biomeID = biomeID;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		x = buf.readInt();
		z = buf.readInt();
		biomeID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(x);
		buf.writeInt(z);
		buf.writeInt(biomeID);
	}

	public static class Handler implements IMessageHandler<MessageChangeBiome, IMessage> 
	{
		@Override
		public IMessage onMessage(MessageChangeBiome message, MessageContext ctx) 
		{
			Chunk chunk = Minecraft.getMinecraft().thePlayer.worldObj.getChunkFromBlockCoords(message.x, message.z);
			int chunkX = message.x & 0xF;
			int chunkZ = message.z & 0xF;
			chunk.getBiomeArray()[ chunkZ << 4 | chunkX ] = (byte) message.biomeID;
			Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(message.x - 7, 0, message.z - 7, message.x + 7, 255, message.z + 7);
			return null;
		}
	}

}
