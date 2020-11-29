package cecs277.events;

import cecs277.buildings.Building;
import cecs277.passengers.Passenger;
import cecs277.passengers.PassengerFactory;
import cecs277.Simulation;
import java.util.List;
import java.util.Random;

/**
 * A simulation event that adds a new random passenger on floor 1, and then schedules the next spawn event.
 */
public class SpawnPassengerEvent extends SimulationEvent {
	private int priority = 4;

	// After executing, will reference the Passenger object that was spawned.
	private Passenger mPassenger;
	private Building mBuilding;
	
	public SpawnPassengerEvent(long scheduledTime, Building building) {
		super(scheduledTime);
		mBuilding = building;
	}
	
	@Override
	public String toString() {
		return super.toString() + "Adding " + mPassenger.getPassengerName() +" "+ mPassenger.getId() + " [-> "+ mPassenger.getDestination()+"]" + " to floor 1.";
	}
	
	
	@Override
	public void execute(Simulation sim) {
		Random rand = mBuilding.getSimulation().getRandom();
		List<PassengerFactory> passes = sim.getPassengers();
		int total = passes.stream().map(PassengerFactory::factoryWeight).reduce(0, (a,b) -> a+b);
		int r = rand.nextInt(total);
		int firstIndex = -1;
		int summ;
		for(int i = 0; i < passes.size(); i++) {
			summ = 0;
			for(int x = 0; x <= i; x++) {
				summ += passes.get(x).factoryWeight();
			}
			if(r < summ) {
				firstIndex = i;
				break;
			}
		}
		PassengerFactory Chosen = passes.get(firstIndex);
		mPassenger = new Passenger(Chosen.factoryName(),Chosen.shortName(),Chosen.createBoardingStrategy(sim),
				Chosen.createTravelStrategy(sim),Chosen.createDebarkingStrategy(sim),
				Chosen.createEmbarkingStrategy(sim));
		mBuilding.getFloor(1).addWaitingPassenger(mPassenger);
		
		/*
		 TODO: DONE!!!!
		 TODO: schedule the new SpawnPassengerEvent with the simulation. Construct a new SpawnPassengerEvent
		 with a scheduled time that is X seconds in the future, where X is a uniform random integer from
		 1 to 30 inclusive.
		*/
	
		SpawnPassengerEvent ev = new SpawnPassengerEvent(sim.currentTime() + (rand.nextInt(30) + 1),
				mBuilding);
		sim.scheduleEvent(ev);
	}

	@Override
	protected int getPriority() {
		return priority;
	}
	public int compareTo(SimulationEvent x) {
		if(this.getScheduledTime() == x.getScheduledTime()) {
			return Integer.compare(priority, x.getPriority());
		}
		return Long.compare(this.getScheduledTime(), x.getScheduledTime());
	}
	
	
}
