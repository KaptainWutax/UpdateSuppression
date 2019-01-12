package kaptainwutax.updatesuppression.contraption;

public class BlockRedstoneWire {

	private int powerLevel = 0;
	
	public BlockRedstoneWire(int power) {
		setPowerLevel(power);
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	public void setPowerLevel(int powerLevel) {
		this.powerLevel = powerLevel;		
	}
	
}
