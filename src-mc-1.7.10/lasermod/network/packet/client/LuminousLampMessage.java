package lasermod.network.packet.client;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import lasermod.api.LaserInGame;
import lasermod.network.AbstractClientMessageHandler;
import lasermod.tileentity.TileEntityLuminousLamp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author ProPercivalalb
 */
public class LuminousLampMessage implements IMessage {
	
	public int x, y, z;
	public ArrayList<LaserInGame> lasers;
	
	public LuminousLampMessage() {}
	public LuminousLampMessage(TileEntityLuminousLamp luminousPanel) {
	    this.x = luminousPanel.xCoord;
	    this.y = luminousPanel.yCoord;
	    this.z = luminousPanel.zCoord;
	    this.lasers = luminousPanel.lasers;
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();

	    this.lasers = new ArrayList<LaserInGame>();
	    int count = buffer.readInt();
	    for(int i = 0; i < count; ++i)
	    	this.lasers.add(new LaserInGame().readFromPacket(buffer));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.z);
		
		buffer.writeInt(this.lasers.size());
		
		for(int i = 0; i < this.lasers.size(); ++i) 
			this.lasers.get(i).writeToPacket(buffer);
	}
	
	public static class Handler extends AbstractClientMessageHandler<LuminousLampMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, LuminousLampMessage message, MessageContext ctx) {
			World world = player.worldObj;
			TileEntity tileEntity = world.getTileEntity(message.x, message.y, message.z);
			
			if(!(tileEntity instanceof TileEntityLuminousLamp)) 
				return null;
			TileEntityLuminousLamp colourConverter = (TileEntityLuminousLamp)tileEntity;
			colourConverter.lasers = message.lasers;
			world.markBlockForUpdate(message.x, message.y, message.z);
			return null;
		}
	}
}
