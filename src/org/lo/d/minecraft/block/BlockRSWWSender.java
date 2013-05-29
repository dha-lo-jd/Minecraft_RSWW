package org.lo.d.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lo.d.commons.coords.MetadataPoint3D;
import org.lo.d.minecraft.core.RSWWCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRSWWSender extends AbstractBlockRSWW {

	public static final int SEND_RANGE_MAX = 16;

	protected Icon iconOn;
	protected Icon iconOff;
	protected Icon iconSupply;

	public BlockRSWWSender(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBlockTexture(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		int power = par1iBlockAccess.getBlockMetadata(par2, par3, par4);
		if (power == 0) {
			return iconOff;
		}

		int offsetX = Facing.offsetsXForSide[par5];
		int offsetY = Facing.offsetsYForSide[par5];
		int offsetZ = Facing.offsetsZForSide[par5];
		for (int i = 1; i <= getSendPowerRange(par1iBlockAccess, par2, par3, par4); i++) {
			int x = par2 + (offsetX * i);
			int y = par3 + (offsetY * i);
			int z = par4 + (offsetZ * i);
			int blockId = par1iBlockAccess.getBlockId(x, y, z);
			if (Block.blocksList[blockId] == RSWWCore.blockRSWWReceiver) {
				return iconSupply;
			}
		}

		return iconOn;

	}

	public int getSendPowerRange(IBlockAccess par1World, int par2, int par3, int par4) {
		int power = par1World.getBlockMetadata(par2, par3, par4);
		return power > SEND_RANGE_MAX ? SEND_RANGE_MAX : power;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);
		iconOn = par1IconRegister.registerIcon("rsww:blockRSWWSender");
		iconOff = par1IconRegister.registerIcon("rsww:blockRSWWSender_off");
		iconSupply = par1IconRegister.registerIcon("rsww:blockRSWWSender_supply");
	}

	private void sendPower(int radius, World par1iBlockAccess, int par2, int par3, int par4) {
		for (MetadataPoint3D point : MetadataPoint3D.AllSurfacePoints) {
			MetadataPoint3D p = point.extendedPoint(radius);
			int x = par2 + p.getX();
			int y = par3 + p.getY();
			int z = par4 + p.getZ();
			int blockId = par1iBlockAccess.getBlockId(x, y, z);
			if (Block.blocksList[blockId] == RSWWCore.blockRSWWReceiver) {
				par1iBlockAccess.notifyBlockOfNeighborChange(x, y, z, blockId);
			}
		}
	}

	@Override
	protected int doGetPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
		return getIndirectPowerLevelTo(par1iBlockAccess, par2, par3, par4);
	}

	@Override
	protected void doNotify(World par1World, int par2, int par3, int par4) {
		super.doNotify(par1World, par2, par3, par4);
		sendPower(par1World, par2, par3, par4);
	}

	protected void sendPower(World par1World, int par2, int par3, int par4) {
		for (int i = 1; i < SEND_RANGE_MAX; i++) {
			sendPower(i, par1World, par2, par3, par4);
		}
	}
}
