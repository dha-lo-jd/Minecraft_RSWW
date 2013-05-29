package org.lo.d.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Facing;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

import org.lo.d.minecraft.core.RSWWCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRSWWReceiver extends AbstractBlockRSWW {

	protected static class RecievePowerFunction implements GetPowerFunction {
		private final BlockRSWWReceiver blockRSWW;
		private final int radius;

		public RecievePowerFunction(BlockRSWWReceiver blockRSWW, int radius) {
			this.blockRSWW = blockRSWW;
			this.radius = radius;
		}

		@Override
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata) {
			return blockRSWW.doReceivePowerLevelTo(radius, par1iBlockAccess, x, y, z, metadata);
		}
	}

	protected Icon iconOn;
	protected Icon iconOff;
	protected Icon iconSupply;

	public BlockRSWWReceiver(int par1, Material par2Material) {
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
		for (int i = 1; i < BlockRSWWSender.SEND_RANGE_MAX; i++) {
			int x = par2 + (offsetX * i);
			int y = par3 + (offsetY * i);
			int z = par4 + (offsetZ * i);
			if (doReceivePowerLevelTo(i, par1iBlockAccess, x, y, z, Facing.oppositeSide[par5]) > 0) {
				return iconSupply;
			}
		}

		return iconOn;

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		super.registerIcons(par1IconRegister);

		iconOn = par1IconRegister.registerIcon("rsww:blockRSWWReceiver");
		iconOff = par1IconRegister.registerIcon("rsww:blockRSWWReceiver_off");
		iconSupply = par1IconRegister.registerIcon("rsww:blockRSWWReceiver_supply");
	}

	@Override
	protected int doGetPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4) {
		return doGetProvidingWeakPower(par1iBlockAccess, par2, par3, par4, 0);
	}

	protected int doGetProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		int l = 0;
		for (int i = 1; i < BlockRSWWSender.SEND_RANGE_MAX; i++) {
			l = Math.max(l, getPower(i, par1iBlockAccess, par2, par3, par4, new RecievePowerFunction(this, i)));
			if (l >= 15)
			{
				return l;
			}
		}
		return l;
	}

	protected int doReceivePowerLevelTo(int radius, IBlockAccess par1iBlockAccess, int x, int y, int z,
			int meta)
	{
		int blockId = par1iBlockAccess.getBlockId(x, y, z);
		Block block = Block.blocksList[blockId];
		if (block != RSWWCore.blockRSWWSender
				|| ((BlockRSWWSender) block).getSendPowerRange(par1iBlockAccess, x, y, z) < radius) {
			return 0;
		}
		return block.isProvidingWeakPower(par1iBlockAccess, x, y, z, meta);
	}

}
