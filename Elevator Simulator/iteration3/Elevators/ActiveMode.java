package cecs277.elevators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cecs277.Simulation;
import cecs277.buildings.Floor;
import cecs277.elevators.Elevator.Direction;
import cecs277.elevators.Elevator.ElevatorState;
import cecs277.events.ElevatorStateEvent;

/**
 * An ActiveMode elevator is handling at least one floor request.
 */
public class ActiveMode implements OperationMode {
	
	// TODO: implement this class.
	// An active elevator cannot be dispatched, and will ignore direction requests from its current floor. (Only idle
	//    mode elevators observe floors, so an ActiveMode elevator will never observe directionRequested.)
	// The bulk of your Project 2 tick() logic goes here, except that you will never be in IDLE_STATE when active.
	// If you used to schedule a transition to IDLE_STATE, you should instead schedule an operation change to
	//    IdleMode in IDLE_STATE.
	// Otherwise your code should be almost identical, except you are no longer in the Elevator class, so you need
	//    to use accessors and mutators instead of directly addressing the fields of Elevator.
	
	
	
	@Override
	public String toString() {
		return "Active";
	}

	@Override
	public boolean canBeDispatchedToFloor(Elevator elevator, Floor floor) {
		return false;
	}

	@Override
	public void dispatchToFloor(Elevator elevator, Floor targetFloor, Direction targetDirection) {
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


		// ******* DOORS OPENING ************************************************************************

		// if an elevator is decelerating towards our floor
		if(elevator.getmCurrentState() == ElevatorState.DOORS_OPENING){

			scheduleStateChange(ElevatorState.DOORS_OPEN, 2, elevator);
		}



		// ******* DOORS OPEN ***************************************************************************


		// if the elevator's current stat is doors open
		else if(elevator.getmCurrentState() == ElevatorState.DOORS_OPEN){
			// the number of passengers on the elevator before alerting that the doors have opened
			int onElevatorBefore = elevator.getPassengerCount();
			// the number of passengers waiting on the floor before alerting that the doors have opened
			int onFloorBefore = elevator.getCurrentFloor().getWaitingPassengers().size();

			//notify all the elevator's observers that the doors have opened
			// TODO: ADDED THIS !!!!!!!!!!!!!
			List<ElevatorObserver> copyObservers = new ArrayList<>(elevator.getObservers());

			for(ElevatorObserver obs : copyObservers){
				obs.elevatorDoorsOpened(elevator);
			}

			// the number of passengers on the elevator after the doors have opened
			int onElevatorAfter = elevator.getPassengerCount();
			// the number of passengers waiting on the floor after the doors have opened
			int onFloorAfter = elevator.getCurrentFloor().getWaitingPassengers().size();

			int changeOnFloor = onFloorBefore - onFloorAfter;

			int changeOnElevator = (onElevatorBefore + changeOnFloor) - onElevatorAfter;

			// how many passengers left the elevator, and how many new ones entered
			int passengerChangeCount = (int)Math.floor(1 + ((Math.abs(changeOnFloor) + Math.abs(changeOnElevator)) / 2));
			// transition to doors closing in 1 + x seconds (rounded down)
			// x is half of the passenger change count
			scheduleStateChange(ElevatorState.DOORS_CLOSING, passengerChangeCount, elevator);
		}


		//******* DOORS CLOSING ***************************************************************************

		else if(elevator.getmCurrentState() == ElevatorState.DOORS_CLOSING) {
			if(elevator.getFloorRequests().isEmpty()){
				elevator.setCurrentDirection(Direction.NOT_MOVING);
				elevator.scheduleModeChange(new IdleMode(),ElevatorState.IDLE_STATE,2);
				
			}
			if(((elevator.getmCurrentDirection() == Direction.MOVING_UP && nextRequestUp(elevator.getCurrentFloor().getNumber(), elevator)!=-1)
					|| (elevator.getmCurrentDirection() == Direction.MOVING_DOWN && nextRequestDown(elevator.getCurrentFloor().getNumber(), elevator)!=-1))
					&& !elevator.getFloorRequests().isEmpty()) {
				scheduleStateChange(ElevatorState.ACCELERATING,2, elevator);
			}
			else {
				if(elevator.getmCurrentDirection() == Direction.MOVING_UP && nextRequestDown(elevator.getCurrentFloor().getNumber(), elevator)!=-1){
					elevator.setCurrentDirection(Direction.MOVING_DOWN);
					scheduleStateChange(ElevatorState.DOORS_OPENING,2, elevator);
				}
				else if(elevator.getmCurrentDirection() == Direction.MOVING_DOWN && nextRequestUp(elevator.getCurrentFloor().getNumber(), elevator)!=-1) {
					elevator.setCurrentDirection(Direction.MOVING_UP);
					scheduleStateChange(ElevatorState.DOORS_OPENING,2, elevator);
				}
			}
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

			//Floor nextFloor;

			// if the elevator's direction is moving up
			if (elevator.getmCurrentDirection() == Direction.MOVING_UP) {
				// determine the next floor by adding 1 to the current floor
				elevator.setCurrentFloor(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() + 1));

				// if the elevator's floor requests contain the next floor OR if the next floor has
				// pressed the same direction button as the elevator is moving
				if(elevator.getFloorRequests().contains(elevator.getCurrentFloor().getNumber()) ||
						elevator.getCurrentFloor().directionIsPressed(Direction.MOVING_UP)){

					// transition to decelerating in 2 seconds
					scheduleStateChange(ElevatorState.DECELERATING, 2, elevator);
				}
				else{
					scheduleStateChange(ElevatorState.MOVING, 2, elevator);
				}
			}
			// if the elevator's direction is moving down
			else if (elevator.getmCurrentDirection() == Direction.MOVING_DOWN){
				// determine the next floor by subtracting 1 from the current floor
				elevator.setCurrentFloor(elevator.getBuilding().getFloor(elevator.getCurrentFloor().getNumber() - 1));

				// if the elevator's floor requests contain the next floor OR if the next floor has
				// pressed the same direction button as the elevator is moving
				if(elevator.getFloorRequests().contains(elevator.getCurrentFloor().getNumber()) ||
						elevator.getCurrentFloor().directionIsPressed(Direction.MOVING_DOWN)){

					// transition to decelerating in 2 seconds
					scheduleStateChange(ElevatorState.DECELERATING, 2, elevator);
				}
				else{
					scheduleStateChange(ElevatorState.MOVING, 2, elevator);
				}
			}

		}


		//******* DECELERATING ******************************************************************************


		// if the elevator's current state is decelerating
		else if(elevator.getmCurrentState() == ElevatorState.DECELERATING){
			// "Clear" the current floor from the elevator's floor requests
			//TODO: MODIFIED THISSSSS !!!!!!!!
			//mFloorRequests.remove(mCurrentFloor.getNumber()-1);
			//mCurrentFloor.clearDirection(mCurrentDirection);
			
			List<Integer> copiedFl = new ArrayList<>(elevator.getFloorRequests());
			for(int i = 0; i < copiedFl.size(); i++){
				if(copiedFl.get(i) == elevator.getCurrentFloor().getNumber()){
					elevator.removeRequest(copiedFl.get(i));
				}
			}
			
			// if the current floor has requested the same direction as the elevator is moving OR another elevator
			// passenger has requested a floor in our current direction we will retain our current direction
			if((elevator.getmCurrentDirection() == Direction.MOVING_UP && elevator.getCurrentFloor().directionIsPressed(Direction.MOVING_UP))
					|| nextRequestUp(elevator.getCurrentFloor().getNumber(), elevator) != -1){
				elevator.setCurrentDirection(Direction.MOVING_UP);
			}
			else if((elevator.getmCurrentDirection() == Direction.MOVING_DOWN && elevator.getCurrentFloor().directionIsPressed(Direction.MOVING_DOWN))
					|| nextRequestDown(elevator.getCurrentFloor().getNumber(), elevator) != -1){
				elevator.setCurrentDirection(Direction.MOVING_DOWN);
			}
			// if the opposite direction has been requested by the current floor
			else if(elevator.getmCurrentDirection() == Direction.MOVING_UP && elevator.getCurrentFloor().directionIsPressed(Direction.MOVING_DOWN)){
				// switch directions
				elevator.setCurrentDirection(Direction.MOVING_DOWN);
			}

			else if(elevator.getmCurrentDirection() == Direction.MOVING_DOWN && elevator.getCurrentFloor().directionIsPressed(Direction.MOVING_UP)){
				// switch directions
				elevator.setCurrentDirection(Direction.MOVING_UP);
			}
			// otherwise change direction to not moving
			else{
				if(elevator.getPassengerCount()==0) {
					elevator.setCurrentDirection(Direction.NOT_MOVING);
				}
			}
			
			// TODO: ADDED THIS !!!!!!!!!!!!!
			List<ElevatorObserver> copyObservers = new ArrayList<>(elevator.getObservers());

			// alert all observers that elevatorDecelerating has occured
			for(ElevatorObserver obs : copyObservers){
				obs.elevatorDecelerating(elevator);
			}
			// transition to doors opening in 3 seconds
			scheduleStateChange(ElevatorState.DOORS_OPENING, 3, elevator);
		}
	}
	/**determine the first floor larger than fromFloor that has been requested,
	 * i.e., if we're moving up starting at fromFloor, where is the next requested stop?
	 * Return -1 if there is none.
	 * */
	// TODO: ADDED THIS!!!!
	private int nextRequestUp(int fromFloor, Elevator elevator){
		// sort in ascending order
		Collections.sort(elevator.getFloorRequests());
		for(Integer request : elevator.getFloorRequests()) {
			if(request > fromFloor) {
				return request;
			}
		}
		return -1;

	}

	/**determine the first floor smaller than fromFloor that has been requested,
	 * i.e., if we're moving down starting at fromFloor, where is the next requested stop?
	 * Return -1 if there is none.
	 * */
	// TODO: ADDED THIS!!!!
	private int nextRequestDown(int fromFloor, Elevator elevator){
		elevator.getFloorRequests().sort(Collections.reverseOrder());
		for(Integer request : elevator.getFloorRequests()) {
			if (request < fromFloor) {
				return request;
			}
		}
		return -1;
	}
}
