package kaptainwutax.updatesuppression;

import java.util.HashSet;

public class BlockPos {
	
	int posX = 0;
	int posY = 0;
	int posZ = 0;
	
	public BlockPos(int x, int y, int z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	
	public BlockPos() {
		this(0, 0, 0);
	}
	
	public int getX() {
		return this.posX;		
	}
	
	public int getY() {
		return this.posY;		
	}
	
	public int getZ() {
		return this.posZ;		
	}

	
	public BlockPos down() {
		return new BlockPos(this.posX, this.posY - 1, this.posZ);
	}

	public BlockPos up() {
		return new BlockPos(this.posX, this.posY + 1, this.posZ);
	}

	public BlockPos north() {
		return new BlockPos(this.posX, this.posY, this.posZ - 1);
	}

	public BlockPos south() {
		return new BlockPos(this.posX, this.posY, this.posZ + 1);
	}

	public BlockPos west() {
		return new BlockPos(this.posX - 1, this.posY, this.posZ);
	}

	public BlockPos east() {
		return new BlockPos(this.posX + 1, this.posY, this.posZ);
	}

	public BlockPos addX(int h) {
		return new BlockPos(this.posX + h, this.posY, this.posZ);
	}
	
	public BlockPos add(BlockPos pos) {		
		return new BlockPos(this.posX + pos.getX(), this.posY + pos.getY(), this.posZ + pos.getZ());
	}

	public static int hashCode(int x, int y, int z) {
		return z * 961 + y * 31 + x;
	}
	
	@Override
	public int hashCode() {
		return this.posZ * 961 + this.posY * 31 + this.posX;
	}

	@Override
	public boolean equals(Object pos) {
		if(!(pos instanceof BlockPos))return false;
		if(this.posX != ((BlockPos)pos).getX())return false;
		if(this.posY != ((BlockPos)pos).getY())return false;
		if(this.posZ != ((BlockPos)pos).getZ())return false;
		return true;
	}
	
	public HashSet<BlockPos> getNotifiersWithHash(int hash) {
		HashSet<BlockPos> notifiers = new HashSet<BlockPos>();
		notifiers.add(this.addX(hash));
		notifiers.add(this.down().addX(hash));
		notifiers.add(this.up().addX(hash));
		notifiers.add(this.north().addX(hash));
		notifiers.add(this.south().addX(hash));
		notifiers.add(this.west().addX(hash));
		notifiers.add(this.east().addX(hash));
		return notifiers;
	}

	public static int moveHash(int searchHash, BlockPos blockUpdate) {
		searchHash += blockUpdate.getX();
		searchHash += blockUpdate.getY() * 31;
		searchHash += blockUpdate.getZ() * 961;
		return searchHash;
	}
	
}
