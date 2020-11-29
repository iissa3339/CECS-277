package cecs277.passengers;

import java.util.List;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.events.PassengerNextDestinationEvent;

public class MultipleDestinationTravel implements TravelStrategy{
	private List<Integer> mDestinations;
	private List<Long> mDurations;
	public MultipleDestinationTravel (List<Integer> destinations, List<Long> durations) {
		mDestinations = destinations;
		mDurations = durations;
	}
	@Override
	public int getDestination() {
		if(mDestinations.isEmpty()) {
			return 1;
		}
		return mDestinations.get(0);
	}

	@Override
	public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {
		mDestinations.remove(0);
		Simulation sim = currentFloor.getBuilding().getSimulation();
		sim.scheduleEvent(new PassengerNextDestinationEvent(sim.currentTime()+mDurations.get(0),passenger,currentFloor));
		mDurations.remove(0);	
	}

}
