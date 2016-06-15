package VRGenerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CESolarContainer extends Container
{
    public CESolarTileEntity tileEntity;
	public CESolarGui gui;
    public boolean sunIsVisible;
    public boolean initialized;

    public CESolarContainer(EntityPlayer entityplayer, CESolarTileEntity solar)
    {
		//現在の発電量を更新するために、あちこちに判定追加
        sunIsVisible = false;
        initialized = false;
        tileEntity = solar;
        addSlotToContainer(new CESolarSlot(solar, solar.tier, 0, 80, 26));

        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
            	addSlotToContainer(new Slot(entityplayer.inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (int j = 0; j < 9; j++)
        {
        	addSlotToContainer(new Slot(entityplayer.inventory, j, 8 + j * 18, 142));
        }
    }
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
		tileEntity.updateSunVisibility();
		sunIsVisible = tileEntity.sunIsVisible;
    }
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(var5, 1, 36, true))
                {
                    return null;
                }

                var4.onSlotChange(var5, var3);
            }
            else if (!this.mergeItemStack(var5, 0, 1, false))
            {
                return null;
            }

            if (var5.stackSize == 0)
            {
                var4.putStack((ItemStack)null);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.stackSize == var3.stackSize)
            {
                return null;
            }

            var4.onPickupFromSlot(par1EntityPlayer, var5);
        }

        return var3;
    }
    @Override
    public ItemStack slotClick(int slot, int button, int shift, EntityPlayer entityplayer)
    {
		ItemStack result = null;
		if(slot != 0 && shift == 1)
		{
			Slot topslot = (Slot)(this.inventorySlots.get(0));
			Slot currentSlot = (Slot)(this.inventorySlots.get(slot));
			ItemStack stack = currentSlot.getStack();
			if(topslot.getStack() == null && stack != null && topslot.isItemValid(stack))
			{
				ItemStack topstack = stack.copy();
				topstack.stackSize = 1;
				if(--stack.stackSize <= 0)
				{
					stack = null;
					currentSlot.putStack(stack);
				}
				
				topslot.putStack(topstack);
				result = stack;
			}
		}else{
			result = super.slotClick(slot, button, shift, entityplayer);
		}
		tileEntity.updateSunVisibility();
		sunIsVisible = tileEntity.sunIsVisible;
//		updateGui();
		return result;
    }

//	public void updateGui()
//	{
//		if(gui != null) gui.drawGuiContainerForegroundLayer();
//	}

    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); i++)
        {
            ICrafting icrafting = (ICrafting)crafters.get(i);

            if (sunIsVisible != tileEntity.sunIsVisible || !initialized)
            {
                icrafting.sendProgressBarUpdate(this, 0, tileEntity.sunIsVisible ? 1 : 0);
                initialized = true;
            }
        }

        sunIsVisible = tileEntity.sunIsVisible;
    }

    public void updateProgressBar(int i, int j)
    {
        switch (i)
        {
            case 0:
                tileEntity.sunIsVisible = j != 0;
                break;
        }
    }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return tileEntity.isUseableByPlayer(entityplayer);
    }

    public int guiInventorySize()
    {
        return 1;
    }

    public int getInput()
    {
        return 0;
    }
}
