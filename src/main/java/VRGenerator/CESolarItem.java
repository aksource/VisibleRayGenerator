package VRGenerator;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CESolarItem extends ItemBlock
{
	public CESolarItem(Block block)
	{
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i)
	{
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		int meta = itemstack.getItemDamage();
		int solar = CESolarTileEntity.power.length;
		if(meta >= 0 && meta < solar)
		{
			return "CE.block.Solar." + meta;
		}
		else if(meta < solar + CEGeneratorTileEntity.power.length)
		{
			return "CE.block.Generator." + (meta - solar);
		}
		
		return null;
	}

}
