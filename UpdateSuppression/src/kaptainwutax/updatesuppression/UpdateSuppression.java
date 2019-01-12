package kaptainwutax.updatesuppression;

import kaptainwutax.updatesuppression.contraption.BlockRedstoneWire;
import kaptainwutax.updatesuppression.contraption.FlatContraption;

public class UpdateSuppression {
		
	public static void main(String[] args) {
		BlockRedstoneWire[][] contraptionMap = {
			{dust(0), dust(0), dust(0), dust(0), dust(0)},
			{dust(0), none(), dust(0), none(), dust(0)},
			{dust(0), none(), dust(0), none(), dust(0)},
			{dust(0), dust(0), dust(0), dust(0), dust(0)}
		};
		
		BlockPos mainPos = new BlockPos(0, 0, 0);
		int mainPosPower = 15;
		
		FlatContraption contraption = new FlatContraption(contraptionMap, mainPos, mainPosPower);
		contraption.getDepth(0, mainPos, -1);
		System.out.println("Depth is " + contraption.highestDepth + ".");
	}
	
	public static BlockRedstoneWire dust(int power) {
		return new BlockRedstoneWire(power);
	}
	
	public static BlockRedstoneWire none() {
		return null;
	}
	
}
