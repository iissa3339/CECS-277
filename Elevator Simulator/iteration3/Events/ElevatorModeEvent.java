package cecs277.events;

import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.elevators.OperationMode;

public class ElevatorModeEvent extends SimulationEvent{
	private int priority = 1;
	private OperationMode newMode;
	private Elevator mElevator;
	private Elevator.ElevatorState mNewState;
	public ElevatorModeEvent(long scheduledTime, OperationMode mode,Elevator.ElevatorState newState, Elevator elevator) {
		super(scheduledTime);
		mNewState = newState;
		mElevator = elevator;
		newMode = mode;
	}
	@Override
	public void execute(Simulation sim) {
		mElevator.setMode(newMode);
		mElevator.setState(mNewState);
		mElevator.tick(mElevator);
	}
	public int getPriority() {
		return priority;
	}
	public int compareTo(SimulationEvent x) {
		if(this.getScheduledTime() == x.getScheduledTime()) {
			return Integer.compare(priority, x.getPriority());
		}
		return Long.compare(this.getScheduledTime(), x.getScheduledTime());
	}
	public String toString() {
		return super.toString() + mElevator.toString();
	}

}
