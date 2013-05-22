package org.lo.d.minecraft.block;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lo.d.minecraft.core.RSWWCore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BlockRSWWReceiver extends Block {

	private interface AxisPointFactory {
		MetadataPoint create(int axis);
	}

	private static class DirectGetPowerFunction implements GetPowerFunction {
		private final BlockRSWWReceiver blockRSWW;

		public DirectGetPowerFunction(BlockRSWWReceiver blockRSWW) {
			this.blockRSWW = blockRSWW;
		}

		@Override
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata) {
			return blockRSWW.getIndirectPowerLevelTo(par1iBlockAccess, x, y, z, metadata);
		}
	}

	private interface GetPowerFunction {
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata);
	}

	private static class InternalGetPowerFunction implements GetPowerFunction {
		@Override
		public int getPower(IBlockAccess par1iBlockAccess, int x, int y, int z, int metadata) {
			return par1iBlockAccess.isBlockProvidingPowerTo(x, y, z, metadata);
		}
	}

	private static class MetadataPoint extends Point {
		public final int metadataSurface;

		public MetadataPoint(int x, int y, int z, int m) {
			super(x, y, z);
			metadataSurface = m;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MetadataPoint other = (MetadataPoint) obj;
			if (metadataSurface != other.metadataSurface) {
				return false;
			}
			return true;
		}

		public MetadataPoint extendedPoint(int radius) {
			return new MetadataPoint(x * radius, y * radius, z * radius, metadataSurface);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + metadataSurface;
			return result;
		}
	}

	private static class Point {
		public final int x, y, z;

		public Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Point other = (Point) obj;
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			if (z != other.z) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}
	}

	private static class PointXAxis extends MetadataPoint {
		private static final AxisPointFactory FACTORY = new AxisPointFactory() {
			@Override
			public MetadataPoint create(int axis) {
				return new PointXAxis(axis);
			}
		};

		public PointXAxis(int x) {
			super(x, 0, 0, x < 0 ? 4 : 5);
		}
	}

	private static class PointYAxis extends MetadataPoint {
		private static final AxisPointFactory FACTORY = new AxisPointFactory() {
			@Override
			public MetadataPoint create(int axis) {
				return new PointYAxis(axis);
			}
		};

		public PointYAxis(int y) {
			super(0, y, 0, y < 0 ? 0 : 1);
		}
	}

	private static class PointZAxis extends MetadataPoint {
		private static final AxisPointFactory FACTORY = new AxisPointFactory() {
			@Override
			public MetadataPoint create(int axis) {
				return new PointZAxis(axis);
			}
		};

		public PointZAxis(int z) {
			super(0, 0, z, z < 0 ? 2 : 3);
		}
	}

	private static final AxisPointFactory[] axis3DFactorys = {
			PointXAxis.FACTORY,
			PointYAxis.FACTORY,
			PointZAxis.FACTORY,
	};

	private static final MetadataPoint[] AllSurfacePoints;

	static {
		List<Point> list = Lists.newArrayList();
		int[] direction = { -1, 1 };
		for (AxisPointFactory factory : axis3DFactorys) {
			for (int dir : direction) {
				list.add(factory.create(dir));
			}
		}
		AllSurfacePoints = list.toArray(new MetadataPoint[] {});
	}

	private Set<Point> calcurativePoints = Sets.newHashSet();

	public BlockRSWWReceiver(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
		updatePower(par1World, par2, par3, par4);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return isProvidingWeakPower(par1iBlockAccess, par2, par3, par4, par5);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return par1iBlockAccess.getBlockMetadata(par2, par3, par4);
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		super.onBlockAdded(par1World, par2, par3, par4);
		updatePower(par1World, par2, par3, par4);
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
		updatePower(par1World, par2, par3, par4);
	}

	private int doGetProvidingWeakPower(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		int l = 0;
		for (int i = 1; i < 8; i++) {
			l = Math.max(l, getPower(i, par1iBlockAccess, par2, par3, par4, par5, new DirectGetPowerFunction(this)));
			if (l >= 15)
			{
				return l;
			}
		}
		return l;
	}

	private int getIndirectPowerLevelTo(IBlockAccess par1iBlockAccess, int par1, int par2, int par3, int par4)
	{
		if (Block.blocksList[par1iBlockAccess.getBlockId(par1, par2, par3)] != RSWWCore.blockRSWWSender) {
			return 0;
		}
		if (par1iBlockAccess.isBlockNormalCube(par1, par2, par3))
		{
			return getPower(1, par1iBlockAccess, par1, par2, par3, par4, new InternalGetPowerFunction());
		}
		else
		{
			int i1 = par1iBlockAccess.getBlockId(par1, par2, par3);
			return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingWeakPower(par1iBlockAccess, par1, par2, par3, par4);
		}
	}

	private int getPower(int radius, IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5,
			GetPowerFunction function) {
		int l = 0;
		for (MetadataPoint point : AllSurfacePoints) {
			MetadataPoint p = point.extendedPoint(radius);
			l = Math.max(l, function.getPower(par1iBlockAccess, par2 + p.x, par3 + p.y, par4 + p.z, p.metadataSurface));

			if (l >= 15)
			{
				return l;
			}
		}
		return l;
	}

	private boolean updatePower(World par1World, int par2, int par3, int par4) {
		int oldPower = par1World.getBlockMetadata(par2, par3, par4);
		int power = doGetProvidingWeakPower(par1World, par2, par3, par4, 0);
		boolean flag = oldPower != power;
		if (flag) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, power, 2);
			int blockId = par1World.getBlockId(par2, par3, par4);
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4, blockId);
		}
		return flag;
	}
}
