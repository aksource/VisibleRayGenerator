package VRGenerator;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IC2Items;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CESolarBlock extends BlockContainer {
    private IIcon[] top = new IIcon[14];
    private IIcon[] side = new IIcon[14];
    private IIcon[] bottom = new IIcon[14];

    public CESolarBlock() {
        super(Material.iron);
        setHardness(3F);
        setStepSound(soundTypeMetal);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconReg) {
        for (int i = 0; i < 14; i++) {
            top[i] = iconReg.registerIcon(String.format("%sGenTop%d", VisibleRayGenerator.TexDomain, i));
            side[i] = iconReg.registerIcon(String.format("%sGenSide%d", VisibleRayGenerator.TexDomain, i));
            bottom[i] = iconReg.registerIcon(String.format("%sGenBottom%d", VisibleRayGenerator.TexDomain, i));
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0)
            return this.bottom[meta];
        else if (side == 1)
            return this.top[meta];
        else
            return this.side[meta];
    }

    @Override
    public Item getItemDropped(int i, Random random, int j) {
        return IC2Items.getItem("machine").getItem();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> list = Lists.newArrayList();
        list.add(IC2Items.getItem("machine").copy());
        return list;
    }

    @Override
    public int damageDropped(int i) {
        return i;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return new ItemStack(VisibleRayGenerator.solarBlock, 1, getMetaFromTileEntity(tileEntity));
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void randomDisplayTick(World world, int i, int j, int k, Random random) {
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null)
            return false;
        else if (entityplayer.getCurrentEquippedItem() != null) {
            if (entityplayer.getCurrentEquippedItem().getItem() == IC2Items.getItem("wrench").getItem() || entityplayer.getCurrentEquippedItem().getItem() == IC2Items.getItem("electricWrench").getItem()) {
                if (tile instanceof CESolarTileEntity) {
                    for (int l = 0; l < ((CESolarTileEntity) tile).getSizeInventory(); l++) {
                        ItemStack ist = ((CESolarTileEntity) tile).getStackInSlot(l);
                        if (ist == null || ist.stackSize <= 0) {
                            continue;
                        }
                        EntityItem eit = new EntityItem(world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, ist);
                        if (!world.isRemote)
                            world.spawnEntityInWorld(eit);
                    }
                }
                return false;
            } else if (entityplayer.getCurrentEquippedItem().getItem() == IC2Items.getItem("ecMeter").getItem()) {
                return false;
            } else {
                if (tile instanceof CEGeneratorTileEntity) return false;
                if (entityplayer.isSneaking()) return false;
                entityplayer.openGui(VisibleRayGenerator.instance, VisibleRayGenerator.artificialSunGuiID, world, x, y, z);
                return true;
            }
        } else {
            if (tile instanceof CEGeneratorTileEntity) return false;
            if (entityplayer.isSneaking()) return false;
            entityplayer.openGui(VisibleRayGenerator.instance, VisibleRayGenerator.artificialSunGuiID, world, x, y, z);
            return true;
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block id, int meta) {
        CETileEntityGenerator tile = (CETileEntityGenerator) world.getTileEntity(x, y, z);
        if (tile != null) {
            if (tile instanceof CESolarTileEntity) {
                for (int l = 0; l < ((CESolarTileEntity) tile).getSizeInventory(); l++) {
                    ItemStack ist = ((CESolarTileEntity) tile).getStackInSlot(l);
                    if (ist == null || ist.stackSize <= 0) {
                        continue;
                    }
                    EntityItem eit = new EntityItem(world, (double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, ist);
                    world.spawnEntityInWorld(eit);
                }
            }
            tile.onChunkUnload();
        }
        super.breakBlock(world, x, y, z, id, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
        for (int i = 0; i < CESolarTileEntity.power.length + CEGeneratorTileEntity.power.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int i) {
        int solar = CESolarTileEntity.power.length;
        if (i >= 0 && i < solar) {
            return new CESolarTileEntity(i);
        } else if (i < solar + CEGeneratorTileEntity.power.length) {
            return new CEGeneratorTileEntity(i - solar);
        }
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int i) {
        int solar = CESolarTileEntity.power.length;
        if (i >= 0 && i < solar) {
            return new CESolarTileEntity(i);
        } else if (i < solar + CEGeneratorTileEntity.power.length) {
            return new CEGeneratorTileEntity(i - solar);
        }
        return null;
    }

    private int getMetaFromTileEntity(TileEntity tileEntity) {
        int meta = 0;
        if (tileEntity instanceof CESolarTileEntity) {
            meta = ((CESolarTileEntity)tileEntity).level;
        } else if (tileEntity instanceof CEGeneratorTileEntity) {
            meta = CESolarTileEntity.power.length + ((CEGeneratorTileEntity)tileEntity).level;
        }
        return meta;
    }
}
