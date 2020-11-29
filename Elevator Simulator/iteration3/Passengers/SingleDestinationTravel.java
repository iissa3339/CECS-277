package cecs277.passengers;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.events.PassengerNextDestinationEvent;

public class SingleDestinationTravel implements TravelStrategy{
	int mDestination;
	long mDuration;
	
	public SingleDestinationTravel(int destination, long duration) {
		mDestination = destination;
		mDuration = duration;
	}

	@Override
	public int getDestination() {
		return mDestination;
	}

	@Override
	public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {
		mDestination = 1;
		Simulation s = currentFloor.getBuilding().getSimulation();
		PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() +
				mDuration, passenger, currentFloor);
		s.scheduleEvent(ev);
	}
	
}
