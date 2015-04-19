package VRGenerator;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class CETileEntityGenerator extends TileEntity implements IWrenchable, IEnergySource
{

    public static final int LV = 32;
    public static final int MV = 128;
    public static final int HV = 512;
    public static final int EV = 2048;
    public int production;
    public boolean addedToEnergyNet = false;
    public int activityMeter = 0;
    public int ticksSinceLastActiveUpdate = 0;
    public int tier = 1;

    public int getTierFromProduction(int prod) {
        return (prod <= LV)? 1 : (prod <= MV)? 2 : (prod <= HV)? 3 : (prod <= EV)? 4: 5;
    }

    @Override
    public void updateEntity() {
    	if(!addedToEnergyNet)
    	{
    		if(!this.worldObj.isRemote)
    		{
    			EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
    			MinecraftForge.EVENT_BUS.post(event);
    		}
    		this.addedToEnergyNet = true;
    	}
    }

    @Override
    public void invalidate() {
        super.invalidate();
        onChunkUnload();
    }

    @Override
    public void onChunkUnload()
    {
    	if(this.addedToEnergyNet)
    	{
    		EnergyTileUnloadEvent event = new EnergyTileUnloadEvent(this);
    		MinecraftForge.EVENT_BUS.post(event);
    		this.addedToEnergyNet = false;
    	}
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);
        this.tier = nbttagcompound.getInteger("tier");
        this.activityMeter = nbttagcompound.getInteger("activitymeter");
        this.production = nbttagcompound.getInteger("production");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("tier", this.tier);
        nbttagcompound.setInteger("activitymeter", this.activityMeter);
        nbttagcompound.setInteger("production", this.production);
    }

	@Override
	public double getOfferedEnergy() {
		return production;
	}

	@Override
	public void drawEnergy(double amount) {
		
	}

    @Override
    public int getSourceTier() {
        return this.tier;
    }

    @Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return true;
	}
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return false;
	}

	@Override
	public short getFacing() {
		return 0;
	}

	@Override
	public void setFacing(short facing) {

	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0f;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(this.getBlockType(), 1, this.getBlockMetadata());
	}
}