package cecs277.Buildings;

import cecs277.elevators.ElevatorObserver;
import cecs277.passengers.Passenger;
import cecs277.elevators.Elevator;

import java.util.*;

public class Floor implements ElevatorObserver {
	private Building mBuilding;
	private List<Passenger> mPassengers = new ArrayList<>();
	private ArrayList<FloorObserver> mObservers = new ArrayList<>();
	private int mNumber;

	//TODO: DONE!!!!
	// TODO: declare a field(s) to help keep track of which direction buttons are currently pressed.
	// You can assume that every floor has both up and down buttons, even the ground and top floors.
	private boolean upButton;// = false;
	private boolean downButton;// = false;
	
	public Floor(int number, Building building) {
		mNumber = number;
		mBuilding = building;
	}
	
	
	/**
	 * Sets a flag that the given direction has been requested by a passenger on this floor. If the direction
	 * had NOT already been requested, then all observers of the floor are notified that directionRequested.
	 * @param direction
	 */
	public void requestDirection(Elevator.Direction direction) {

		// TODO: implement this method as described in the comment.

		//ArrayList<FloorObserver> copyObservers = new ArrayList<>(mObservers);

		if(direction == Elevator.Direction.MOVING_UP){
			if(!upButton){
				for(FloorObserver o : mObservers){
					o.directionRequested(this, direction);
				}
				upButton = true;
			}
		}
		else if(direction == Elevator.Direction.MOVING_DOWN){
			if(!downButton){
				for(FloorObserver o : mObservers){
					o.directionRequested(this, direction);
				}
				downButton = true;
			}
		}
	}
	
	/**
	 * Returns true if the given direction button has been pressed.
	 */
	public boolean directionIsPressed(Elevator.Direction direction) {
	    // TODO: DONE!!!!
		// TODO: complete this method.
        if(direction == Elevator.Direction.MOVING_UP && upButton){
            return true;
        }
        else if(direction == Elevator.Direction.MOVING_DOWN && downButton){
            return true;
        }
		return false;
	}
	
	/**
	 * Clears the given direction button so it is no longer pressed.
	 */
	public void clearDirection(Elevator.Direction direction) {
        // TODO: DONE!!!!
		// TODO: complete this method.
        // if direction is up up button false, if direction down, downbutton false
        if(direction == Elevator.Direction.NOT_MOVING && upButton){
            upButton = false;
        }
        else if(direction == Elevator.Direction.NOT_MOVING && downButton){
            downButton = false;
        }
	}
	
	/**
	 * Adds a given Passenger as a waiting passenger on this floor, and presses the passenger's direction button.
	 */
	public void addWaitingPassenger(Passenger p) {
		mPassengers.add(p);
		addObserver(p);
		p.setState(Passenger.PassengerState.WAITING_ON_FLOOR);

		// TODO: DONE!!!!
		// TODO: call requestDirection with the appropriate direction for this passenger's destination.
        int destination = p.getDestination() ;
        // if the passengers destination is greater than the current floor
        if(destination > mNumber){
            requestDirection(Elevator.Direction.MOVING_UP);
        }
        // if the passengers destination is less than the current floor
        else if(destination < mNumber){
            requestDirection(Elevator.Direction.MOVING_DOWN);
        }
	}
	
	/**
	 * Removes the given Passenger from the floor's waiting passengers.
	 */
	public void removeWaitingPassenger(Passenger p) {
		mPassengers.remove(p);
	}
	
	
	// Simple accessors.
	public int getNumber() {
		return mNumber;
	}
	
	public List<Passenger> getWaitingPassengers() {
		return mPassengers;
	}
	
	@Override
	public String toString() {
		return "Floor " + mNumber;
	}
	
	// Observer methods.
	public void removeObserver(FloorObserver observer) {
		mObservers.remove(observer);
	}
	
	public void addObserver(FloorObserver observer) {
		mObservers.add(observer);
	}

	// TODO: MIGHT NEED TO ADD GET OBSERVER !!!!!!!!!!!
	
	// Observer methods.
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// TODO: if the elevator is arriving at THIS FLOOR, alert all the floor's observers that elevatorArriving.
		// TODO:    then clear the elevator's current direction from this floor's requested direction buttons.
		if(elevator.getCurrentFloor() == this){

			for(FloorObserver obs : mObservers){
				obs.elevatorArriving(this, elevator);
			}
			this.clearDirection(elevator.getCurrentDirection());
		}

		
	}
	
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Not needed.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// Not needed.
	}
}
