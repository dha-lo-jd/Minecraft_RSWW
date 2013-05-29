package org.lo.d.minecraft.block;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lo.d.commons.coords.MetadataPoint3D;
import org.lo.d.commons.coords.Point3D;

import com.google.common.collect.Sets;

public abstract class AbstractBlockRSWW extends Block {

	protected static class DirectGetPowerFunction implements GetPowerFunction {
		private final AbstractBlockRSWW blockRSWW;

		public DirectGetPowerFunction(AbstractBlockRSWW blockRSWW) {
			this.blockRSWW = blockRSWW;
		}

		@Override
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata) {
			return blockRSWW.doGetIndirectPowerLevelTo(par1iBlockAccess, x, y, z, metadata);
		}
	}

	protected interface GetPowerFunction {
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata);
	}

	protected static class InternalGetPowerFunction implements GetPowerFunction {
		@Override
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata) {
			return par1iBlockAccess.isBlockProvidingPowerTo(x, y, z, metadata);
		}
	}

	protected Set<Point3D> updatePoints = Sets.newHashSet();

	public AbstractBlockRSWW(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 2);
		doNotify(par1World, par2, par3, par4);
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		Point3D p = new Point3D(par2, par3, par4);
		if (updatePoints.contains(p)) {
			return 0;
		}

		updatePoints.add(p);
		int power = par1iBlockAccess.getBlockMetadata(par2, par3, par4);
		updatePoints.remove(p);
		return power > 0 ? power - 1 : 0;
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		super.onBlockAdded(par1World, par2, par3, par4);
		update(par1World, par2, par3, par4);
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
		update(par1World, par2, par3, par4);
	}

	private void update(World par1World, int par2, int par3, int par4) {
		Point3D p = new Point3D(par2, par3, par4);
		if (!updatePoints.contains(p)) {
			updatePoints.add(p);
			boolean flag = doUpdate(par1World, par2, par3, par4);
			updatePoints.remove(p);
			if (flag) {
				doNotify(par1World, par2, par3, par4);
			}
		}
	}

	protected int doGetIndirectPowerLevelTo(IBlockAccess par1iBlockAccess, int par1, int par2, int par3, int par4)
	{
		if (par1iBlockAccess.isBlockNormalCube(par1, par2, par3))
		{
			return getPower(par1iBlockAccess, par1, par2, par3, new InternalGetPowerFunction());
		}
		else
		{
			int i1 = par1iBlockAccess.getBlockId(par1, par2, par3);
			return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingWeakPower(par1iBlockAccess, par1, par2, par3, par4);
		}
	}

	protected abstract int doGetPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4);

	protected void doNotify(World par1World, int par2, int par3, int par4) {
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, blockID);
	}

	protected boolean doUpdate(World par1World, int par2, int par3, int par4) {
		int oldPower = par1World.getBlockMetadata(par2, par3, par4);
		int power = doGetPower(par1World, par2, par3, par4);
		boolean flag = oldPower != power;
		if (flag) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, power, 2);
		}
		return flag;
	}

	protected int getIndirectPowerLevelTo(IBlockAccess par1iBlockAccess, int par2, int par3, int par4)
	{
		return getPower(par1iBlockAccess, par2, par3, par4, new DirectGetPowerFunction(this));
	}

	protected int getPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4,
			GetPowerFunction function) {
		return getPower(1, par1iBlockAccess, par2, par3, par4, function);
	}

	protected int getPower(int radius, IBlockAccess par1iBlockAccess, int par2, int par3, int par4,
			GetPowerFunction function) {
		int l = 0;
		for (MetadataPoint3D point : MetadataPoint3D.AllSurfacePoints) {
			MetadataPoint3D p = point.extendedPoint(radius);
			int x = par2 + p.getX();
			int y = par3 + p.getY();
			int z = par4 + p.getZ();
			l = Math.max(l, function.getPower(par1iBlockAccess, x, y, z, p.metadataSurface));

			if (l >= 15)
			{
				return l;
			}
		}
		return l;

	}

}
