package ValkyrienWarfareBase.PhysicsManagement;

import java.util.HashMap;

import ValkyrienWarfareControl.Piloting.ClientPilotingManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class DimensionPhysObjectManager {

	private HashMap<World, WorldPhysObjectManager> managerPerWorld;

	private WorldPhysObjectManager cachedManager;

	public DimensionPhysObjectManager() {
		managerPerWorld = new HashMap<World, WorldPhysObjectManager>();
	}

	// Put the ship in the manager queues
	public void onShipLoad(PhysicsWrapperEntity justLoaded) {
		getManagerForWorld(justLoaded.worldObj).onLoad(justLoaded);
	}

	// Remove the ship from the damn queues
	public void onShipUnload(PhysicsWrapperEntity justUnloaded) {
		getManagerForWorld(justUnloaded.worldObj).onUnload(justUnloaded);
	}

	public void initWorld(World toInit) {
		if (!managerPerWorld.containsKey(toInit)) {
			managerPerWorld.put(toInit, new WorldPhysObjectManager(toInit));
		}
	}

	public WorldPhysObjectManager getManagerForWorld(World world) {
		if(world == null){
			//I'm not quite sure what to do here
		}
		if (cachedManager == null || cachedManager.worldObj != world) {
			cachedManager = managerPerWorld.get(world);
		}
		if (cachedManager == null) {
			System.err.println("getManagerForWorld just requested for a World without one!!! Assuming that this is a new world, so making a new WorldPhysObjectManager for it.");
			cachedManager = new WorldPhysObjectManager(world);
			//Make sure to add the cachedManager to the world managers
			managerPerWorld.put(world, cachedManager);
		}
		return cachedManager;
	}

	public void removeWorld(World world) {
		if (managerPerWorld.containsKey(world)) {
			getManagerForWorld(world).physicsEntities.clear();
		}
		managerPerWorld.remove(world);
		// System.out.println("cleared Mounting Entity");
		//This is critical!!! Failure to clear these on world unload will force Java to keep the ENTIRE WORLD LOADED. HUGE MEMORY LEAK!!! Don't change
		ClientPilotingManager.setMountedWrapperEntity(null);
		ClientPilotingManager.setPilotedWrapperEntity(null);
	}

	/**
	 * Returns the PhysicsWrapperEntity that claims this chunk if there is one; returns null if there is no loaded entity managing it
	 *
	 * @param chunk
	 * @return
	 */

	//If you caused an Entity$1 crash, it probably started here >:(
	public PhysicsWrapperEntity getObjectManagingChunk(Chunk chunk) {
		if (chunk == null) {
			return null;
		}
		WorldPhysObjectManager physManager = getManagerForWorld(chunk.worldObj);
		if (physManager == null) {
			return null;
		}
		return physManager.getManagingObjectForChunk(chunk);
	}

	public PhysicsWrapperEntity getObjectManagingPos(World world, BlockPos pos) {
		if(world == null || pos == null){
			return null;
		}
		if(world.getChunkProvider() == null){
//			System.out.println("Retard Devs coded a World with no Chunks in it!");
			return null;
		}
		//NoClassFound Entity$1.class FIX
		if(!world.isRemote){
			if(world.getChunkProvider() instanceof ChunkProviderServer){
				ChunkProviderServer providerServer =  (ChunkProviderServer) world.getChunkProvider();
				//The chunk at the given pos isn't loaded? Don't bother with the next step, you'll create an infinite loop!
				if(!providerServer.chunkExists(pos.getX() >> 4, pos.getZ() >> 4)){
					return null;
				}
			}
		}
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		return getObjectManagingChunk(chunk);
	}

	public boolean isEntityFixed(Entity entity) {
		return getManagerForWorld(entity.worldObj).isEntityFixed(entity);
	}

	public PhysicsWrapperEntity getShipFixedOnto(Entity entity) {
		return getManagerForWorld(entity.worldObj).getShipFixedOnto(entity, false);
	}

}
