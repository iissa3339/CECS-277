package cecs277.passengers;

import java.util.Random;

import cecs277.Simulation;

public class StonerFactory implements PassengerFactory {
	int mWeight = 1;
	public void setmWeight(int weight) {
		mWeight = weight;
	}

	@Override
	public String factoryName() {
		return "Stoner";
	}

	@Override
	public String shortName() {
		return "S";
	}

	@Override
	public int factoryWeight() {
		return mWeight;
	}

	@Override
	public BoardingStrategy createBoardingStrategy(Simulation simulation) {
		return new CapacityBoarding();
	}

	@Override
	public TravelStrategy createTravelStrategy(Simulation simulation) {
		Random r = simulation.getRandom();
		// Look up the documentation for the .nextGaussian() method of the Random class.
		int randomFloor = r.nextInt(simulation.getBuilding().getFloorCount() - 1) + 2;
		long duration = (long)(r.nextGaussian() * (20*60) + (60*60));
		return new SingleDestinationTravel(randomFloor,duration);
	}

	@Override
	public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
		return new ClumsyEmbarking();
	}

	@Override
	public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
		return new ConfusedDebarking();
	}

}
