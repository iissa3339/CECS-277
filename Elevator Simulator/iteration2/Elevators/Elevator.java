package cecs277.elevators;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.elevators.Elevator.Direction;
import cecs277.elevators.Elevator.ElevatorState;
import cecs277.events.ElevatorStateEvent;
import cecs277.passengers.Passenger;

import java.util.*;
import java.util.stream.Collectors;

public class Elevator implements FloorObserver {
	
	public enum ElevatorState {
		IDLE_STATE,
		DOORS_OPENING,
		DOORS_CLOSING,
		DOORS_OPEN,
		ACCELERATING,
		DECELERATING,
		MOVING
	}
	
	public enum Direction {
		NOT_MOVING,
		MOVING_UP,
		MOVING_DOWN
	}
	
	
	private int mNumber;
	private Building mBuilding;

	private ElevatorState mCurrentState = ElevatorState.IDLE_STATE;
	private Direction mCurrentDirection = Direction.NOT_MOVING;
	private Floor mCurrentFloor;
	private List<Passenger> mPassengers = new ArrayList<>();
	
	private List<ElevatorObserver> mObservers = new ArrayList<>();

	// TODO: DONE!!!!
	// TODO: declare a field to keep track of which floors have been requested by passengers.
	private List<Integer> mFloorRequests = new ArrayList<>();
	
	
	public Elevator(int number, Building bld) {
		mNumber = number;
		mBuilding = bld;
		mCurrentFloor = bld.getFloor(1);
		
		scheduleStateChange(ElevatorState.IDLE_STATE, 0);
	}
	
	/**
	 * Helper method to schedule a state change in a given number of seconds from now.
	 */
	private void scheduleStateChange(ElevatorState state, long timeFromNow) {
		Simulation sim = mBuilding.getSimulation();
		sim.scheduleEvent(new ElevatorStateEvent(sim.currentTime() + timeFromNow, state, this));
	}

	/**determine the first floor larger than fromFloor that has been requested,
	 * i.e., if we're moving up starting at fromFloor, where is the next requested stop?
	 * Return -1 if there is none.
	 * */
	// TODO: ADDED THIS!!!!
	private int nextRequestUp(int fromFloor){
		// sort in ascending order
		Collections.sort(mFloorRequests);
		for(Integer request : mFloorRequests) {
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
	private int nextRequestDown(int fromFloor){
		mFloorRequests.sort(Collections.reverseOrder());
		for(Integer request : mFloorRequests) {
			if (request < fromFloor) {
				return request;
			}
		}
		return -1;
	}


	/**
	 * Adds the given passenger to the elevator's list of passengers, and requests the passenger's destination floor.
	 */
	public void addPassenger(Passenger passenger) {
	    // TODO: DONE????
		// TODO: add the passenger's destination to the set of requested floors.
		//mFloorRequests.set(passenger.getDestination() - 1);
		// TODO: MIGHT NEED TO CHANGE THE -1 !!!!!!!!!!!!!!!
        mFloorRequests.add(passenger.getDestination());
		mPassengers.add(passenger);
	}


	public void removePassenger(Passenger passenger) {
		mPassengers.remove(passenger);
	}
	
	
	/**
	 * Schedules the elevator's next state change based on its current state.
	 */
	public void tick() {
		// TODO: DONE!!!!
		// TODO: port the logic of your state changes from Project 1, accounting for the adjustments in the spec.
		// TODO: State changes are no longer immediate; they are scheduled using scheduleStateChange().
		
		// Example of how to trigger a state change:
		// scheduleStateChange(ElevatorState.MOVING, 3); // switch to MOVING and call tick(), 3 seconds from now.


		// ******* IDLE STATE ************************************************************************


		// if the elevator is in an idle state
		if(mCurrentState == ElevatorState.IDLE_STATE){
			//add elevator as an observer of the current floor
			mCurrentFloor.addObserver(this);

			// TODO: ADDED THIS !!!!!!!!!!!!!
			List<ElevatorObserver> copyObservers = new ArrayList<>(mObservers);

			//alert all the elevator's observers that it went idle
			for(ElevatorObserver obs : copyObservers){
				obs.elevatorWentIdle(this);
			}
		}


		// ******* DOORS OPENING ************************************************************************

		// if an elevator is decelerating towards our floor
		else if(mCurrentState == ElevatorState.DOORS_OPENING){

			scheduleStateChange(ElevatorState.DOORS_OPEN, 2);
			/*for(Passenger p : mCurrentFloor.getWaitingPassengers()) {
				mObservers.add(p);
			}*/
		}



		// ******* DOORS OPEN ***************************************************************************


		// if the elevator's current stat is doors open
		else if(mCurrentState == ElevatorState.DOORS_OPEN){
			// the number of passengers on the elevator before alerting that the doors have opened
			int onElevatorBefore = getPassengerCount();
			// the number of passengers waiting on the floor before alerting that the doors have opened
			int onFloorBefore = mCurrentFloor.getWaitingPassengers().size();

			//notify all the elevator's observers that the doors have opened
			// TODO: ADDED THIS !!!!!!!!!!!!!
			List<ElevatorObserver> copyObservers = new ArrayList<>(mObservers);

			for(ElevatorObserver obs : copyObservers){
				obs.elevatorDoorsOpened(this);
			}

			// the number of passengers on the elevator after the doors have opened
			int onElevatorAfter = getPassengerCount();
			// the number of passengers waiting on the floor after the doors have opened
			int onFloorAfter = mCurrentFloor.getWaitingPassengers().size();

			int changeOnFloor = onFloorBefore - onFloorAfter;

			int changeOnElevator = (onElevatorBefore + changeOnFloor) - onElevatorAfter;

			// how many passengers left the elevator, and how many new ones entered
			int passengerChangeCount = (int)Math.floor(1 + ((Math.abs(changeOnFloor) + Math.abs(changeOnElevator)) / 2));
			// transition to doors closing in 1 + x seconds (rounded down)
			// x is half of the passenger change count
			scheduleStateChange(ElevatorState.DOORS_CLOSING, passengerChangeCount);
		}


		//******* DOORS CLOSING ***************************************************************************

		else if(mCurrentState == ElevatorState.DOORS_CLOSING) {
			if(mFloorRequests.isEmpty()){
				this.setCurrentDirection(Direction.NOT_MOVING);
				scheduleStateChange(ElevatorState.IDLE_STATE,2);
			}
			if(((mCurrentDirection == Direction.MOVING_UP && nextRequestUp(mCurrentFloor.getNumber())!=-1)
					|| (mCurrentDirection == Direction.MOVING_DOWN && nextRequestDown(mCurrentFloor.getNumber())!=-1))
					&& !mFloorRequests.isEmpty()) {
				scheduleStateChange(ElevatorState.ACCELERATING,2);
			}
			else {
				if(mCurrentDirection == Direction.MOVING_UP && nextRequestDown(mCurrentFloor.getNumber())!=-1){
					this.setCurrentDirection(Direction.MOVING_DOWN);
					scheduleStateChange(ElevatorState.DOORS_OPENING,2);
				}
				else if(mCurrentDirection == Direction.MOVING_DOWN && nextRequestUp(mCurrentFloor.getNumber())!=-1) {
					this.setCurrentDirection(Direction.MOVING_UP);
					scheduleStateChange(ElevatorState.DOORS_OPENING,2);
				}
			}
		}



		//******* ACCELERATING ***************************************************************************


		// if the elevator's current state is accelerating
		else if(mCurrentState == ElevatorState.ACCELERATING){

			//remove the elevator as an observer of the current floor
			mCurrentFloor.removeObserver(this);

			scheduleStateChange(ElevatorState.MOVING, 3);
		}


		//******* MOVING **********************************************************************************


		// if the elevator's current state is moving
		else if(mCurrentState == ElevatorState.MOVING){

			//Floor nextFloor;

			// if the elevator's direction is moving up
			if (mCurrentDirection == Direction.MOVING_UP) {
				// determine the next floor by adding 1 to the current floor
				mCurrentFloor = mBuilding.getFloor(mCurrentFloor.getNumber() + 1);

				// if the elevator's floor requests contain the next floor OR if the next floor has
				// pressed the same direction button as the elevator is moving
				if(mFloorRequests.contains(mCurrentFloor.getNumber()) ||
						mCurrentFloor.directionIsPressed(Direction.MOVING_UP)){

					// transition to decelerating in 2 seconds
					scheduleStateChange(ElevatorState.DECELERATING, 2);
				}
				else{
					scheduleStateChange(ElevatorState.MOVING, 2);
				}
			}
			// if the elevator's direction is moving down
			else if (mCurrentDirection == Direction.MOVING_DOWN){
				// determine the next floor by subtracting 1 from the current floor
				mCurrentFloor = mBuilding.getFloor(mCurrentFloor.getNumber() - 1);

				// if the elevator's floor requests contain the next floor OR if the next floor has
				// pressed the same direction button as the elevator is moving
				if(mFloorRequests.contains(mCurrentFloor.getNumber()) ||
						mCurrentFloor.directionIsPressed(Direction.MOVING_DOWN)){

					// transition to decelerating in 2 seconds
					scheduleStateChange(ElevatorState.DECELERATING, 2);
				}
				else{
					scheduleStateChange(ElevatorState.MOVING, 2);
				}
			}

		}


		//******* DECELERATING ******************************************************************************


		// if the elevator's current state is decelerating
		else if(mCurrentState == ElevatorState.DECELERATING){
			// "Clear" the current floor from the elevator's floor requests
			//TODO: MODIFIED THISSSSS !!!!!!!!
			//mFloorRequests.remove(mCurrentFloor.getNumber()-1);
			//mCurrentFloor.clearDirection(mCurrentDirection);
			for(int i = 0; i < mFloorRequests.size(); i++){
				if(mFloorRequests.get(i) == mCurrentFloor.getNumber()){
					mFloorRequests.remove(i);
				}
			}

			// if the current floor has requested the same direction as the elevator is moving OR another elevator
			// passenger has requested a floor in our current direction we will retain our current direction
			if((mCurrentDirection == Direction.MOVING_UP && mCurrentFloor.directionIsPressed(Direction.MOVING_UP))
					|| nextRequestUp(mCurrentFloor.getNumber()) != -1){
				mCurrentDirection = Direction.MOVING_UP;
			}
			else if((mCurrentDirection == Direction.MOVING_DOWN && mCurrentFloor.directionIsPressed(Direction.MOVING_DOWN))
					|| nextRequestDown(mCurrentFloor.getNumber()) != -1){
				mCurrentDirection = Direction.MOVING_DOWN;
			}
			// if the opposite direction has been requested by the current floor
			else if(mCurrentDirection == Direction.MOVING_UP && mCurrentFloor.directionIsPressed(Direction.MOVING_DOWN)){
				// switch directions
				mCurrentDirection = Direction.MOVING_DOWN;
			}

			else if(mCurrentDirection == Direction.MOVING_DOWN && mCurrentFloor.directionIsPressed(Direction.MOVING_UP)){
				// switch directions
				mCurrentDirection = Direction.MOVING_UP;
			}
			// otherwise change direction to not moving
			else{
				mCurrentDirection = Direction.NOT_MOVING;
			}

			// TODO: ADDED THIS !!!!!!!!!!!!!
			List<ElevatorObserver> copyObservers = new ArrayList<>(mObservers);

			// alert all observers that elevatorDecelerating has occured
			for(ElevatorObserver obs : copyObservers){
				obs.elevatorDecelerating(this);
			}
			// transition to doors opening in 3 seconds
			scheduleStateChange(ElevatorState.DOORS_OPENING, 3);
		}
	}
	
	
	/**
	 * Sends an idle elevator to the given floor.
	 */
	public void dispatchTo(Floor floor) {
		// TODO: DONE!!!!
		// TODO: if we are currently idle and not on the given floor, change our direction to move towards the floor.
		// TODO: set a floor request for the given floor, and schedule a state change to ACCELERATING immediately.
		if(this.isIdle() && mCurrentFloor != floor){
			if(mCurrentFloor.getNumber() < floor.getNumber()){
				mCurrentDirection = Direction.MOVING_UP;
			}
			else if(mCurrentFloor.getNumber() > floor.getNumber()){
				mCurrentDirection = Direction.MOVING_DOWN;
			}
			mFloorRequests.add(floor.getNumber());
			scheduleStateChange(ElevatorState.ACCELERATING, 0);
		}
	}
	
	// Simple accessors
	public Floor getCurrentFloor() {
		return mCurrentFloor;
	}
	
	public Direction getCurrentDirection() {
		return mCurrentDirection;
	}
	
	public Building getBuilding() {
		return mBuilding;
	}

	
	/**
	 * Returns true if this elevator is in the idle state.
	 * @return
	 */
	public boolean isIdle() {
		// TODO: DONE!!!!
		// TODO: complete this method.
		if(mCurrentState == ElevatorState.IDLE_STATE){
			return true;
		}
		return false;
	}
	
	// All elevators have a capacity of 10, for now.
	public int getCapacity() {
		return 10;
	}
	
	public int getPassengerCount() {
		return mPassengers.size();
	}
	
	// Simple mutators
	public void setState(ElevatorState newState) {
		mCurrentState = newState;
	}
	
	public void setCurrentDirection(Direction direction) {
		mCurrentDirection = direction;
	}
	
	public void setCurrentFloor(Floor floor) {
		mCurrentFloor = floor;
	}
	
	// Observers
	public void addObserver(ElevatorObserver observer) {
		mObservers.add(observer);
	}
	
	public void removeObserver(ElevatorObserver observer) {
		mObservers.remove(observer);
	}
	
	
	// FloorObserver methods
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// Not used.
	}
	
	/**
	 * Triggered when our current floor receives a direction request.
	 */
	@Override
	public void directionRequested(Floor sender, Direction direction) {
	    // TODO: DONE!!!!
		// TODO: if we are currently idle, change direction to match the request. Then alert all our observers that we are decelerating,
		// TODO: then schedule an immediate state change to DOORS_OPENING.
        if(mCurrentState == ElevatorState.IDLE_STATE){
            mCurrentDirection = direction;

        }

		// TODO: ADDED THIS !!!!!!!!!!!!!
		List<ElevatorObserver> copyObservers = new ArrayList<>(mObservers);

		for(ElevatorObserver o : copyObservers){
			o.elevatorDecelerating(this);
		}

		scheduleStateChange(ElevatorState.DOORS_OPENING, 0);

	}
	
	
	
	
	// Voodoo magic.
	@Override
	public String toString() {
		return "Elevator " + mNumber + " - " + mCurrentFloor + " - " + mCurrentState + " - " + mCurrentDirection + " - "
		 + "[" + mPassengers.stream().map(p -> Integer.toString(p.getDestination())).collect(Collectors.joining(", "))
		 + "]";
	}
	
}
