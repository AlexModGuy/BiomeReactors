package com.github.alexthe666.biomereactors;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageResetEnergy implements IMessage{
	private int x;
	private int y;
	private int z;
	private int energy;

	public MessageResetEnergy(){}

	public MessageResetEnergy(int x, int y, int z, int energy) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.energy = energy;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		energy = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(energy);
	}

	public static class Handler implements IMessageHandler<MessageResetEnergy, IMessage> 
	{
		@Override
		public IMessage onMessage(MessageResetEnergy message, MessageContext ctx) 
		{
			TileEntity entity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.x, message.y, message.z);
			if(entity != null && entity instanceof TileEntityBiomeReactor){
				((TileEntityBiomeReactor)entity).setEnergyStore(message.energy);
			}
			return null;
		}
	}

}
