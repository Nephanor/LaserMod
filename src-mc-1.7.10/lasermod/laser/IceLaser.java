package lasermod.laser;

import java.util.List;

import lasermod.api.ILaser;
import lasermod.util.BlockActionPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author ProPercivalalb
 */
public class IceLaser implements ILaser {
	
	@Override
	public void performActionOnEntitiesServer(List<Entity> entities, ForgeDirection dir) {
		for(Entity entity : entities) {
			
		}
	}
	
	@Override
	public void performActionOnEntitiesClient(List<Entity> entities, ForgeDirection dir) {
	}
	
	@Override
	public void performActionOnEntitiesBoth(List<Entity> entities, ForgeDirection dir) {
		for(Entity entity : entities) {
			double verticalSpeed = 0.120000000000000003D;
            double maxSpeed = 0.19999999999999999D;     
		}
	}
	
	@Override
	public boolean shouldRenderLaser(EntityPlayer player, ForgeDirection dir) {
		return true;
	}
	
	@Override
	public void actionOnBlock(BlockActionPos action) {
		
	}
}
