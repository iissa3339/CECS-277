package cecs277.elevators;

import cecs277.Simulation;
import cecs277.buildings.Building;
import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.events.ElevatorModeEvent;
import cecs277.passengers.Passenger;

import java.util.*;
import java.util.stream.Collectors;

public class Elevator implements FloorObserver, OperationMode {
	
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
	private OperationMode mOperationMode;
	private List<ElevatorObserver> mObservers = new ArrayList<>();

	// TODO: DONE!!!!
	// TODO: declare a field to keep track of which floors have been requested by passengers.
	private List<Integer> mFloorRequests = new ArrayList<>();
	
	public Elevator(int number, Building bld) {
		mNumber = number;
		mBuilding = bld;
		mCurrentFloor = bld.getFloor(1);
		scheduleModeChange(new IdleMode(), ElevatorState.IDLE_STATE, 0);
	}
	public void requestFloor(int floorNum) {
		this.mFloorRequests.add(floorNum);
	}
	public ElevatorState getmCurrentState() {
		return mCurrentState;
	}
	public List<ElevatorObserver> getObservers(){
		return mObservers;
	}
	public List<Integer> getFloorRequests(){
		return mFloorRequests;
	}
	public Direction getmCurrentDirection() {
		return mCurrentDirection;
	}
	public void addFloorRequest(int floornum) {
		mFloorRequests.add(floornum);
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
	
	// Voodoo magic.
	@Override
	public String toString() {
		List<Integer> copiedFloors = new ArrayList<>(mFloorRequests);
		Set<Integer> cf = copiedFloors.stream().sorted().collect(Collectors.toSet());
		return "Elevator " + mNumber + " [" + mOperationMode.toString() + "] - " + mCurrentFloor + " - " + mCurrentState + " - " + mCurrentDirection + " - "
		 + "[" + mPassengers.stream().map(p -> p.getShortName() + p.getId()).collect(Collectors.joining(", "))
		 + "]" + " {" + cf.toString().replace("[", "").replace("]", "") + "}";
	}
	public void scheduleModeChange(OperationMode operationMode, ElevatorState state, int i) {
		Simulation sim = mBuilding.getSimulation();
		sim.scheduleEvent(new ElevatorModeEvent(sim.currentTime()+i, operationMode, state, this));
	}
	@Override
	public boolean canBeDispatchedToFloor(Elevator elevator, Floor floor) {
		return mOperationMode.canBeDispatchedToFloor(elevator, floor);
	}
	@Override
	public void dispatchToFloor(Elevator elevator, Floor targetFloor, Direction targetDirection) {
		mOperationMode.dispatchToFloor(elevator, targetFloor, targetDirection);
	}
	@Override
	public void directionRequested(Elevator elevator, Floor floor, Direction direction) {
		mOperationMode.directionRequested(elevator, floor, direction);
	}
	@Override
	public void tick(Elevator elevator) {
		mOperationMode.tick(elevator);
	}
	public void setMode(OperationMode newMode) {
		mOperationMode = newMode;
	}
	public void announceElevatorIdle() {
		List<ElevatorObserver> copyobs = mObservers;
		for(ElevatorObserver obs : copyobs) {
			obs.elevatorWentIdle(this);
		}
	}
	public void announceElevatorDecelerating() {
		List<ElevatorObserver> copyobs = new ArrayList<> (mObservers);
		for(ElevatorObserver obs : copyobs) {
			obs.elevatorDecelerating(this);
		}
	}
	@Override
	public void directionRequested(Floor sender, Direction direction) {
		mOperationMode.directionRequested(this,sender,direction);
	}
	public OperationMode getCurrentMode() {
		return this.mOperationMode;
	}
	public int getNumber() {
		return mNumber;
	}
	public void removeRequest(Integer valueOf) {
		this.mFloorRequests.remove(Integer.valueOf(valueOf));
	}
	
}
