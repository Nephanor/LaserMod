package lasermod.api.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lasermod.api.ILaser;
import lasermod.api.ILaserReceiver;
import lasermod.api.LaserInGame;
import lasermod.util.LaserUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author ProPercivalalb
 */
public abstract class TileEntityMultiSidedReciever extends TileEntityLaserDevice implements ILaserReceiver {

	public LaserInGame lastCombinedLaser;
	public ArrayList<LaserInGame> lasers = new ArrayList<LaserInGame>();
	
	/**
	 * Override and will be called when there is an update to the lasers
	 */
	public abstract void sendUpdateDescription();
	
	public abstract void onLaserPass(World world);
	
	public abstract void onLaserRemoved(World world);
	
	public boolean checkPowerOnSide(ForgeDirection dir) {
		return true;
	}
	
	public List<ForgeDirection> getInputDirections() {
		return Arrays.asList(ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		int amount = tag.getInteger("laserCount");
		 for(int i = 0; i < amount; ++i)
			 this.lasers.add(new LaserInGame(tag.getCompoundTag("laser" + i)));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setInteger("laserCount", this.lasers.size());
		
		 for(int i = 0; i < lasers.size(); ++i)
			 tag.setTag("laser" + i, this.lasers.get(i).writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void updateLasers(boolean client) {
		
		if(!client) {
			if(!this.noLaserInputs()) {
				boolean change = false;
				
				for(ForgeDirection dir : this.getInputDirections())
					if(this.checkPowerOnSide(dir) && !LaserUtil.isValidSourceOfPowerOnSide(this, dir))
						if(this.removeAllLasersFromSide(dir))
							change = true;

				if(change) {
					this.sendUpdateDescription();
					this.onLaserRemoved(this.worldObj);
				}
					
			}
		}
	}
	
	@Override
	public void updateLaserAction(boolean client) {
		
	}
	
	@Override
	public int getX() {
		return this.xCoord;
	}

	@Override
	public int getY() {
		return this.yCoord;
	}

	@Override
	public int getZ() {
		return this.zCoord;
	}

	@Override
	public World getWorld() {
		return this.worldObj;
	}

	@Override
	public boolean canPassOnSide(World world, int orginX, int orginY, int orginZ, ForgeDirection dir, LaserInGame laserInGame) {
		return this.getInputDirections().contains(dir) && !Objects.equals(laserInGame, this.getLaserFromSide(dir));
	}

	@Override
	public void passLaser(World world, int orginX, int orginY, int orginZ, ForgeDirection dir, LaserInGame laserInGame) {
		this.addLaser(laserInGame, dir);
		this.onLaserPass(world);
		this.sendUpdateDescription();
		this.setUpdateRequired();
	}

	@Override
	public void removeLasersFromSide(World world, int orginX, int orginY, int orginZ, ForgeDirection dir) {
		boolean flag = this.removeAllLasersFromSide(dir);
		
		if(flag) {
			this.onLaserRemoved(world);
			this.sendUpdateDescription();
			this.setUpdateRequired();
		}
	}

	@Override
	public List<LaserInGame> getInputLasers() {
		return this.lasers;
	}

	//** TileEntityMultiSidedReciever helper methods **//
	
	public LaserInGame getLaserFromSide(ForgeDirection dir) {
		for(int i = 0; i < this.lasers.size(); ++i)
			if(this.lasers.get(i).getDirection() == dir)
				return this.lasers.get(i);
		return null;
	}
	
	public int getIndexOfLaserSide(ForgeDirection dir) {
		for(int i = 0; i < this.lasers.size(); ++i)
			if(this.lasers.get(i).getDirection() == dir)
				return i;
		return -1;
	}
	
	public boolean addLaser(LaserInGame laserInGame, ForgeDirection dir) {
		if(laserInGame == null)
			return false;
		
		int i = this.getIndexOfLaserSide(dir);
		if(i == -1)
			this.lasers.add(laserInGame);
		else
			this.lasers.set(i, laserInGame);
		return true;
	}
	
	public boolean removeAllLasersFromSide(ForgeDirection dir) {
		boolean change = false;
		for(int i = 0; i < lasers.size(); ++i) {
			if(this.lasers.get(i).getDirection() == dir) {
				this.lasers.remove(i);
				change = true;
			}
		}
		return change;
	}
	
	public boolean containsInputSide(ForgeDirection dir) {
		for(int i = 0; i < this.lasers.size(); ++i)
			if(this.lasers.get(i).getDirection() == dir)
				return true;
		return false;
	}
	
	public boolean noLaserInputs() {
		return this.lasers.size() == 0;
	}
	
	public LaserInGame getCombinedOutputLaser(ForgeDirection dir) {
		if(this.noLaserInputs())
			return null;
		
		ArrayList<ILaser> laserList = new ArrayList<ILaser>();
		for(LaserInGame lig : this.lasers)
			for(ILaser laser : lig.getLaserType()) 
				if(!laserList.contains(laser))
					laserList.add(laser);
		
		LaserInGame laserInGame = new LaserInGame(laserList);
		int red = lasers.get(0).red;
		int green = lasers.get(0).green;
		int blue = lasers.get(0).blue;
		
		for(int i = 1; i < lasers.size(); ++i) {
			red = (int)((red * 0.5D) + (lasers.get(i).red * 0.5D));
			green = (int)((green * 0.5D) + (lasers.get(i).green * 0.5D));
			blue = (int)((blue * 0.5D) + (lasers.get(i).blue * 0.5D));
		}
	
		laserInGame.red = red;
		laserInGame.green = green;
		laserInGame.blue = blue;
				
		double totalPower = 0.0D;
		for(LaserInGame laser : lasers)
			totalPower += laser.getStrength();
		
		laserInGame.setDirection(dir.getOpposite());
		laserInGame.setStrength(totalPower / lasers.size());
		
		return laserInGame;
	}
}
