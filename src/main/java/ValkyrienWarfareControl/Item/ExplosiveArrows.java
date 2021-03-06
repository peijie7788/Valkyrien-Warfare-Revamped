package ValkyrienWarfareControl.Item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ExplosiveArrows extends ItemArrow {
	
	public ExplosiveArrows(){
		super();
	}
	
	@Override
	public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter)
    {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, shooter){
        	private boolean doExpl = true;
        	@Override
        	public void onUpdate(){
        		super.onUpdate();
            }
        	@Override
            public boolean isImmuneToExplosions(){
                return true;
            }
        	@Override
            protected void onHit(RayTraceResult raytraceResultIn){
        		super.onHit(raytraceResultIn);
        		worldObj.createExplosion(this, posX, posY, posZ, 20F, true);
        	}
        };
        entitytippedarrow.setPotionEffect(stack);
        return entitytippedarrow;
    }

}
