package kaptainwutax.updatesuppression.contraption;

import java.util.HashSet;

import kaptainwutax.updatesuppression.BlockPos;

public class FlatContraption {
	
	public int[][] contraption;	
	private int[][] contraptionTemp;
	
	public BlockPos powerPos;
	public int powerPosValue;
	private int sizeX;
	private int sizeZ;
	
	private BlockPos[] blockUpdateOrder = {
			new BlockPos().west(),
			new BlockPos().east(),
			new BlockPos().down(),
			new BlockPos().up(),
			new BlockPos().north(),
			new BlockPos().south()
	};
	
	public FlatContraption(int[][] contraptionMap, BlockPos powerPos, int value) {	
		this.contraption = contraptionMap;
		
		this.powerPos = powerPos;
		this.powerPosValue = value;
		
		sizeX = this.contraption[0].length;
		sizeZ = this.contraption.length;
		
		this.contraptionTemp = new int[this.sizeZ][this.sizeX];	
		this.resetTempContraption();
	}
	
	
	public static void main(String[] args) {
		int[][] contraptionMap = {
				{dust(0), dust(0), dust(0), dust(0), dust(0)},
				{dust(0), dust(0), dust(0), dust(0), dust(0)},
				{dust(0), dust(0), dust(0), dust(0), dust(0)},
				{dust(0), dust(0), dust(0), dust(0), dust(0)},
				{dust(0), dust(0),  dust(0),  dust(0),  dust(0)}
		};
		
		BlockPos mainPos = new BlockPos(0, 0, 0);
		int mainPosPower = 15;
		
		FlatContraption flatContraption = new FlatContraption(contraptionMap, mainPos, mainPosPower);
		
		//First parameter is the world position of the upper-left corner in the array. So the most -X and -Z.
		//Next is the position of the the dust that gets updated first.
		//Third is just the start depth, always -1.
		//Fourth is there if you want to print steps in console or not.
		flatContraption.getDepth(new BlockPos(0, 0, 0), mainPos, -1, true);
		System.out.println("Depth is " + flatContraption.highestDepth + ".");
		
		//int[] result = flatContraption.searchForDeepest();
		//System.out.println("Deepest is " + result[0] + " with hash of " + result[1] + ".");
	}
	
	public static int dust(int power) {
		return power;
	}
	
	public static int none() {
		return -1;
	}
	
	public int[] searchForDeepest() {
		int deepestCall = 0;
		int deepestCallHash = 0;
		
		int searchHash = 0;
		do {
			getDepth(new BlockPos(searchHash, 0, 0), this.powerPos, -1, false);
			int callDepth = this.highestDepth;
			this.highestDepth = 0;
			resetTempContraption();
			
			if(callDepth > deepestCall) {
				deepestCall = callDepth;
				deepestCallHash = searchHash;
			}
			
			if((searchHash & ((1 << 10) - 1)) == 0)System.out.println("Current search hash is " + searchHash + " with deepest " + deepestCall + " at hash " + deepestCallHash + ".");
			
			++searchHash;
		} while(searchHash != 0);
		
		//Generates the solution array for returning.
		int[] solution = {deepestCall, deepestCallHash};		
		return solution;
	}

	private void resetTempContraption() {	
		for(int z = 0; z < this.sizeZ; ++z) {
			for(int x = 0; x < this.sizeZ; ++x) {
				this.contraptionTemp[z][x] = this.contraption[z][x];		
			}			
		}		
	}

	public int highestDepth = 0;
	
	public int getDepth(BlockPos searchHash, BlockPos pos, int currentDepth, boolean printMap) {		
		int posX = pos.getX();
		int posZ = pos.getZ();
		if(posX < 0 || posX >= this.sizeX || posZ < 0 || posZ >= this.sizeZ)return highestDepth;
		
		if(currentDepth > this.highestDepth)this.highestDepth = currentDepth;
		
		int dustPowerLevel = this.contraptionTemp[posZ][posX];
		
		if(dustPowerLevel == -1)return this.highestDepth;
		
		HashSet<BlockPos> notifiers = null;
		
		int powerLevel = dustPowerLevel;
		int expectedPowerLevel = powerLevel;
		int highestPowerNeighbor = 0;
		
		BlockPos[] horizontalNeighbors = {
			pos.north(),
			pos.east(),
			pos.south(),
			pos.west()
		};
		
		//Gets the max redstone power around the dust.
		for(BlockPos newPos : horizontalNeighbors) {
			int x = newPos.getX();
			int z = newPos.getZ();
			
			if(x < 0 || x >= this.sizeX || z < 0 || z >= this.sizeZ)continue;
				
			int newDust = this.contraptionTemp[z][x];
			if(this.contraptionTemp[z][x] == -1)continue;
			highestPowerNeighbor = Math.max(highestPowerNeighbor, newDust);
		}
		
        if(highestPowerNeighbor > expectedPowerLevel) {
        	expectedPowerLevel = highestPowerNeighbor - 1;
        } else if(expectedPowerLevel > 0) {
        	/*This causes so many updates when depowering.*/
            --expectedPowerLevel;
        } else {
        	expectedPowerLevel = 0;
        }
        
        if(powerLevel == this.powerPosValue)return highestDepth;
        
        if(pos.equals(this.powerPos))expectedPowerLevel = this.powerPosValue;
        
        if(powerLevel != expectedPowerLevel) { 
        	this.contraptionTemp[posZ][posX] = expectedPowerLevel;
			if(printMap)printMap(pos);
	        notifiers = pos.getNotifiersWithHash(searchHash.getX());
	        
	        for(BlockPos notifier : notifiers) {
	        	for(BlockPos blockUpdate : this.blockUpdateOrder) {
	        		BlockPos newPos = notifier.add(new BlockPos(-searchHash.getX(), 0, 0)).add(blockUpdate);
	        		if(newPos.getY() == 0) {	        			
	        			getDepth(new BlockPos(BlockPos.moveHash(searchHash.getX(), newPos), 0 , 0), newPos, currentDepth + 1, printMap);
	        		}
	        	}
	        }
        }		
		return this.highestDepth;
	}

	private void printMap(BlockPos pos) {
		System.out.println("|||}=---------={FRAME}=---------={|||");
		for(int z = 0; z < this.sizeZ; ++z) {
			for(int x = 0; x < this.sizeX; ++x) {
				if(pos.equals(new BlockPos(x, 0, z)))System.out.print("[");
				else System.out.print(" ");
				
				if(this.contraptionTemp[z][x] == -1)System.out.print("   ");
				else {
					if(this.contraptionTemp[z][x] < 10)System.out.print(" ");
					
					System.out.print(this.contraptionTemp[z][x]);				
					if(pos.equals(new BlockPos(x, 0, z)))System.out.print("]");
					else System.out.print(" ");				
				
				}

			}
			System.out.println();
		}
	}
	
}
