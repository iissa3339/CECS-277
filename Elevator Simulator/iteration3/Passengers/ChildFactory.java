package cecs277.passengers;

import java.util.Random;

import cecs277.Simulation;
import cecs277.logging.Logger;

public class ChildFactory implements PassengerFactory {
	int mWeight = 3;
	public void setmWeight(int weight) {
		mWeight = weight;
	}

	@Override
	public String factoryName() {
		return "Child";
	}

	@Override
	public String shortName() {
		return "C";
	}

	@Override
	public int factoryWeight() {
		return mWeight;
	}

	@Override
	public BoardingStrategy createBoardingStrategy(Simulation simulation) {
		return new AwkwardBoarding(4);
	}

	@Override
	public TravelStrategy createTravelStrategy(Simulation simulation) {
		Random r = simulation.getRandom();
		// Look up the documentation for the .nextGaussian() method of the Random class.
		int randomFloor = r.nextInt(simulation.getBuilding().getFloorCount() - 1) + 2;
		long duration = (int)(r.nextGaussian() * (30*60) + (120*60));
		return new SingleDestinationTravel(randomFloor, duration);
	}

	@Override
	public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
		return new ClumsyEmbarking();
	}

	@Override
	public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
		return new DistractedDebarking();
	}

}
