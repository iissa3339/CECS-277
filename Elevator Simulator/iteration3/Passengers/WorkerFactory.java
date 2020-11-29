package cecs277.passengers;

import java.util.ArrayList;
import java.util.Random;

import cecs277.Simulation;

public class WorkerFactory implements PassengerFactory {
	int mWeight = 2;
	public void setWeight(int weight) {
		mWeight = weight;
	}

	@Override
	public String factoryName() {
		return "Worker";
	}

	@Override
	public String shortName() {
		return "W";
	}

	@Override
	public int factoryWeight() {
		return mWeight;
	}

	@Override
	public BoardingStrategy createBoardingStrategy(Simulation simulation) {
		return new ThresholdBoarding(3);
	}

	@Override
	public TravelStrategy createTravelStrategy(Simulation simulation) {
		Random r = simulation.getRandom();
		ArrayList<Integer> destinations = new ArrayList<>();
		ArrayList<Long> durations = new ArrayList<>();
		// random number from 2 to 5 inclusive that represents how many floors the worker will visit before returning
		// to floor 1
		int numFloorVisits = r.nextInt(4) + 2;
		int randFloor;
		int prevFloor = -1;
		for(int i = 0; i < numFloorVisits; i++){
			randFloor = r.nextInt(simulation.getBuilding().getFloorCount() - 1) + 2;

			while(randFloor == prevFloor){
				randFloor = r.nextInt(simulation.getBuilding().getFloorCount() - 1) + 2;
			}
			destinations.add(randFloor);
			prevFloor = randFloor;
		}

		for(int i = 0; i < numFloorVisits; i++){
			durations.add((long)(r.nextGaussian() * (3*60) + (10*60)));
		}
		return new MultipleDestinationTravel(destinations, durations);
	}

	@Override
	public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
		return new ResponsibleEmbarking();
	}

	@Override
	public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
		return new AttentiveDebarking();
	}

}
