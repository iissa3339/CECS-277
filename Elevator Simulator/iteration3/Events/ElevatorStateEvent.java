package cecs277.events;

import cecs277.Simulation;
import cecs277.elevators.Elevator;

/**
 * A simulation event that sets an elevator's state and calls its tick() method.
 */
public class ElevatorStateEvent extends SimulationEvent {
	private int priority = 2;
	private Elevator.ElevatorState mNewState;
	private Elevator mElevator;
	
	public ElevatorStateEvent(long scheduledTime, Elevator.ElevatorState newState, Elevator elevator) {
		super(scheduledTime);
		mNewState = newState;
		mElevator = elevator;
	}
	@Override
	public void execute(Simulation sim) {
		mElevator.setState(mNewState);
		mElevator.tick(mElevator);
	}
	
	@Override
	public String toString() {
		return super.toString() + mElevator;
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
