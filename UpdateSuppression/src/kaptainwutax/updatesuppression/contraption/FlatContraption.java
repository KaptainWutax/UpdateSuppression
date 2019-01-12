package kaptainwutax.updatesuppression.contraption;

import java.util.ArrayList;
import java.util.HashSet;

import kaptainwutax.updatesuppression.BlockPos;

public class FlatContraption {
	public BlockRedstoneWire[][] contraption;
	public BlockPos powerPos;
	public int powerPosValue;
	private int sizeX = 0;
	private int sizeZ = 0;
	
	private BlockPos[] blockUpdateOrder = {
			new BlockPos().west(),
			new BlockPos().east(),
			new BlockPos().down(),
			new BlockPos().up(),
			new BlockPos().north(),
			new BlockPos().south()
	};
	
	public FlatContraption(BlockRedstoneWire[][] contraption, BlockPos powerPos, int value) {
		this.contraption = contraption;
		
		sizeX = contraption[0].length;
		sizeZ = contraption.length;
		
		this.powerPos = powerPos;
		this.powerPosValue = value;
	}
	
	public int[] searchForDeepest() {
		int deepestCall = 0;
		ArrayList<Integer> deepestCallHash = new ArrayList<Integer>();
		
		//Does the search.
		int searchHash = 0;
		do {
			int callDepth = getDepth(searchHash, new BlockPos(), 0);
			
			if(callDepth > deepestCall) {
				deepestCall = callDepth;
				deepestCallHash.clear();
				deepestCallHash.add(searchHash);
			} else if(callDepth == deepestCall) {
				deepestCallHash.add(searchHash);
			}
			
			++searchHash;
		} while(searchHash != 0);
		
		//Generates the solution array for returning.
		int[] solution = new int[deepestCallHash.size() + 1];
		solution[0] = deepestCall;
		
		for(int i = 0; i < deepestCallHash.size(); ++i) {
			solution[i + 1] = deepestCallHash.get(i);			
		}
		
		return solution;
	}

	public int highestDepth = 0;
	
	public int getDepth(int searchHash, BlockPos pos, int currentDepth) {		
		int posX = pos.getX();
		int posZ = pos.getZ();
		if(posX < 0 || posX >= sizeX || posZ < 0 || posZ >= sizeZ)return 0;
		
		if(currentDepth > highestDepth)highestDepth = currentDepth;
		
		BlockRedstoneWire dust = contraption[posZ][posX];
		if(dust == null)return 0;
		
		HashSet<BlockPos> notifiers = null;
		
		int powerLevel = dust.getPowerLevel();
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
			
			if(x < 0 || x >= sizeX || z < 0 || z >= sizeZ)continue;
				
			BlockRedstoneWire newDust = contraption[z][x];
			if(contraption[z][x] == null)continue;
			highestPowerNeighbor = Math.max(highestPowerNeighbor, newDust.getPowerLevel());
		}
		
        if(highestPowerNeighbor > expectedPowerLevel) {
        	expectedPowerLevel = highestPowerNeighbor - 1;
        } else if(expectedPowerLevel > 0) {
        	/*This causes so many updates when depowering.*/
            --expectedPowerLevel;
        } else {
        	expectedPowerLevel = 0;
        }
        
        if(powerLevel == this.powerPosValue)return 0;
        
        if(pos.equals(this.powerPos))expectedPowerLevel = this.powerPosValue;
        
        if(powerLevel != expectedPowerLevel) { 
    		dust.setPowerLevel(expectedPowerLevel);
			printMap(pos);
	        notifiers = pos.getNotifiersWithHash(searchHash);
	        
	        for(BlockPos notifier : notifiers) {
	        	for(BlockPos blockUpdate : blockUpdateOrder) {
	        		BlockPos newPos = notifier.add(blockUpdate);
	        		if(newPos.getY() == 0) {	        			
	        			getDepth(searchHash, newPos, currentDepth + 1);
	        		}
	        	}
	        }
        }		
		return 0;
	}

	private void printMap(BlockPos pos) {
		System.out.println("|||}=---------={FRAME}=---------={|||");
		for(int z = 0; z < this.sizeZ; ++z) {
			for(int x = 0; x < this.sizeX; ++x) {
				if(pos.equals(new BlockPos(x, 0, z)))System.out.print("[");
				else System.out.print(" ");
				
				if(this.contraption[z][x] == null)System.out.print("   ");
				else {
					if(this.contraption[z][x].getPowerLevel() < 10)System.out.print(" ");
					
					System.out.print(this.contraption[z][x].getPowerLevel());				
					if(pos.equals(new BlockPos(x, 0, z)))System.out.print("]");
					else System.out.print(" ");				
				
				}

			}
			System.out.println();
		}
	}
	
}
