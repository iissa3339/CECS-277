package cecs277.passengers;

import java.util.ArrayList;
import java.util.Random;

import cecs277.Simulation;

public class DeliveryPersonFactory implements PassengerFactory {
	int mWeight = 2;
	public void setmWeight(int weight) {
		mWeight = weight;
	}

	@Override
	public String factoryName() {
		return "Delivery Person";
	}

	@Override
	public String shortName() {
		return "D";
	}

	@Override
	public int factoryWeight() {
		return mWeight;
	}

	@Override
	public BoardingStrategy createBoardingStrategy(Simulation simulation) {
		return new ThresholdBoarding(5);
	}

	@Override
	public TravelStrategy createTravelStrategy(Simulation simulation) {
		
		ArrayList<Integer> destinations = new ArrayList<>();
		ArrayList<Long> durations = new ArrayList<>();
		Random r = simulation.getRandom();
		int floorCount = simulation.getBuilding().getFloorCount();
		int numDestinations = (floorCount * 2)/3;
		int count = r.nextInt(numDestinations)+1;
		int randFloor;
		for(int i = 0; i < count; i++) {
			randFloor = r.nextInt(floorCount - 1) +2;
			while(destinations.contains(randFloor)) {
				randFloor = r.nextInt(floorCount - 1) +2;
			}
			destinations.add(randFloor);
		}
		for(int i = 0; i < count; i++) {
			long num = (long) (r.nextGaussian()*10+60);
			durations.add(num);
		}
		return new MultipleDestinationTravel(destinations,durations);
		
	}

	@Override
	public EmbarkingStrategy createEmbarkingStrategy(Simulation simulation) {
		return new ResponsibleEmbarking();
	}

	@Override
	public DebarkingStrategy createDebarkingStrategy(Simulation simulation) {
		return new DistractedDebarking();
	}

}
