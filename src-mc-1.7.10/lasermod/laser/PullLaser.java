package lasermod.laser;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import lasermod.api.ILaser;
import lasermod.util.BlockActionPos;

/**
 * @author ProPercivalalb
 */
public class PullLaser implements ILaser {

	public static final float SPEED_MULTIPLYER = 1.0F;
	
	@Override
	public void performActionOnEntitiesServer(List<Entity> entities, int direction) {

	}
	
	@Override
	public void performActionOnEntitiesClient(List<Entity> entities, int direction) {
		
	}
	
	@Override
	public void performActionOnEntitiesBoth(List<Entity> entities, int direction) {
		for(Entity entity : entities) {
			double verticalSpeed = 0.180000000000000003D;
            double maxSpeed = 0.29999999999999999D;
            verticalSpeed *= SPEED_MULTIPLYER;

            if(entity instanceof EntityItem)
                maxSpeed *= 2.34D;

            if (entity instanceof EntityMinecart)
                verticalSpeed *= 0.5D;

            if ((entity instanceof EntityFallingBlock) && direction == 0)
                verticalSpeed = 0.0D;

            if (direction == 1 && entity.motionY > -maxSpeed)
                entity.motionY += -verticalSpeed;

            if (direction == 0 && entity.motionY < maxSpeed * 0.5D)
                entity.motionY += verticalSpeed;

            if (direction == 3 && entity.motionZ > -maxSpeed)
                entity.motionZ += -verticalSpeed;

            if (direction == 2 && entity.motionZ < maxSpeed)
                entity.motionZ += verticalSpeed;

            if (direction == 5 && entity.motionX > -maxSpeed)
                entity.motionX += -verticalSpeed;

            if (direction == 4 && entity.motionX < maxSpeed)
                entity.motionX += verticalSpeed;
		}
	}
	
	@Override
	public boolean shouldRenderLaser(EntityPlayer player, int direction) {
		return true;
	}
	
	@Override
	public void actionOnBlock(BlockActionPos action) {
		
	}
}
