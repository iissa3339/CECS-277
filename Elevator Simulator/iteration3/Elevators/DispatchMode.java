package cecs277.elevators;

import java.util.ArrayList;
import java.util.List;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.elevators.Elevator.Direction;
import cecs277.elevators.Elevator.ElevatorState;
import cecs277.events.ElevatorStateEvent;

/**
 * A DispatchMode elevator is in the midst of a dispatch to a target floor in order to handle a request in a target
 * direction. The elevator will not stop on any floor that is not its destination, and will not respond to any other
 * request until it arrives at the destination.
 */
public class DispatchMode implements OperationMode {
	// The destination floor of the dispatch.
	private Floor mDestination;
	// The direction requested by the destination floor; NOT the direction the elevator must move to get to that floor.
	private Elevator.Direction mDesiredDirection;
	
	public DispatchMode(Floor destination, Elevator.Direction desiredDirection) {
		mDestination = destination;
		mDesiredDirection = desiredDirection;
	}
	
	// TODO: implement the other methods of the OperationMode interface.
	// Only Idle elevators can be dispatched.
	// A dispatching elevator ignores all other requests.
	// It does not check to see if it should stop of floors that are not the destination.
	// Its flow of ticks should go: IDLE_STATE -> ACCELERATING -> MOVING -> ... -> MOVING -> DECELERATING.
	//    When decelerating to the destination floor, change the elevator's direction to the desired direction,
	//    announce that it is decelerating, and then schedule an operation change in 3 seconds to
	//    ActiveOperation in the DOORS_OPENING state.
	// A DispatchOperation elevator should never be in the DOORS_OPENING, DOORS_OPEN, or DOORS_CLOSING states.
	
	
	@Override
	public String toString() {
		return "Dispatching to " + mDestination.getNumber() + " " + mDesiredDirection;
	}

	@Override
	public boolean canBeDispatchedToFloor(Elevator elevator, Floor floor) {
		return false;
	}

	@Override
	public void dispatchToFloor(Elevator elevator, Floor targetFloor, Direction targetDirection) {
		elevator.setCurrentDirection(targetDirection);
		elevator.addFloorRequest(targetFloor.getNumber());
	}

	@Override
	public void directionRequested(Elevator elevator, Floor floor, Direction direction) {
	}
	/**
	 * Helper method to schedule a state change in a given number of seconds from now.
	 */
	private void scheduleStateChange(ElevatorState state, long timeFromNow, Elevator elevator) {
		Simulation sim = elevator.getBuilding().getSimulation();
		sim.scheduleEvent(new ElevatorStateEvent(sim.currentTime() + timeFromNow, state, elevator));
	}
	
	@Override
	public void tick(Elevator elevator) {
		if(elevator.getmCurrentState()==Elevator.ElevatorState.IDLE_STATE) {
			
			if(elevator.getCurrentFloor().getNumber()>mDestination.getNumber()) {
				elevator.setCurrentDirection(Elevator.Direction.MOVING_DOWN);
			}
			else if(elevator.getCurrentFloor().getNumber()<mDestination.getNumber()) {
				elevator.setCurrentDirection(Elevator.Direction.MOVING_UP);
			}
			
			scheduleStateChange(ElevatorState.ACCELERATING,0,elevator);
		}
		
		//******* ACCELERATING ***************************************************************************


		// if the elevator's current state is accelerating
		else if(elevator.getmCurrentState() == ElevatorState.ACCELERATING){

			//remove the elevator as an observer of the current floor
			elevator.getCurrentFloor().removeObserver(elevator);

			scheduleStateChange(ElevatorState.MOVING, 3, elevator);
		}


		//******* MOVING **********************************************************************************


		// if the elevator's current state is moving
		else if(elevator.getmCurrentState() == ElevatorState.MOVING){
			// if the elevator's direction is moving up
			if (elevator.getmCurrentDirection() == Direction.MOVING_UP) {
				// determine the next floor by adding 1 to the current floor
				elevator.setCurrentFloor(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1));
				if(elevator.getCurrentFloor() == mDestination) {
					scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2, elevator);
				}
				else {
					scheduleStateChange(ElevatorState.MOVING, 2, elevator);
				}
			}
			// if the elevator's direction is moving down
			else if (elevator.getmCurrentDirection() == Direction.MOVING_DOWN){
				// determine the next floor by subtracting 1 from the current floor
				if(elevator.getCurrentFloor().getNumber() != 1) {
					elevator.setCurrentFloor(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() - 1));
				}
				if(elevator.getCurrentFloor() == mDestination) {
					scheduleStateChange(Elevator.ElevatorState.DECELERATING, 2, elevator);
				}
				else {
					scheduleStateChange(ElevatorState.MOVING, 2, elevator);
				}
			}
		}


		//******* DECELERATING ******************************************************************************


		// if the elevator's current state is decelerating
		else if(elevator.getmCurrentState() == ElevatorState.DECELERATING){
			// "Clear" the current floor from the elevator's floor requests
			elevator.getCurrentFloor().clearDirection(elevator.getCurrentDirection());
			for(int i = 0; i < elevator.getFloorRequests().size(); i++){
				if(elevator.getFloorRequests().get(i) == elevator.getCurrentFloor().getNumber()){
					elevator.getFloorRequests().remove(i);
					i--;
				}
			}
			elevator.setCurrentDirection(mDesiredDirection);
			// TODO: ADDED THIS !!!!!!!!!!!!!
			List<ElevatorObserver> copyObservers = new ArrayList<>(elevator.getObservers());

			// alert all observers that elevatorDecelerating has occured
			for(ElevatorObserver obs : copyObservers){
				obs.elevatorDecelerating(elevator);
			}
			// transition to Active mode
			elevator.scheduleModeChange(new ActiveMode(), ElevatorState.DOORS_OPENING, 3);
		}
	}
}
