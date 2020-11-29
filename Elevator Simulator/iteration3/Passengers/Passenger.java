package cecs277.passengers;

import cecs277.buildings.Floor;
import cecs277.buildings.FloorObserver;
import cecs277.elevators.Elevator;
import cecs277.elevators.ElevatorObserver;
import cecs277.logging.Logger;

/**
 * A passenger that is either waiting on a floor or riding an elevator.
 */
public class Passenger implements FloorObserver, ElevatorObserver, TravelStrategy, BoardingStrategy, EmbarkingStrategy, DebarkingStrategy {
	// An enum for determining whether a Passenger is on a floor, an elevator, or busy (visiting a room in the building).
	public enum PassengerState {
		WAITING_ON_FLOOR,
		ON_ELEVATOR,
		BUSY
	}
	
	// A cute trick for assigning unique IDs to each object that is created. (See the constructor.)
	private static int mNextId;

	protected static int nextPassengerId() {
		return ++mNextId;
	}
	
	private int mIdentifier;
	private PassengerState mCurrentState;
	private String passengerName;
	private String shortName;
	private BoardingStrategy board;
	private TravelStrategy trav;
	private DebarkingStrategy exit;
	private EmbarkingStrategy enter;
	
	public Passenger(String name, String sname, BoardingStrategy bb, TravelStrategy tt, DebarkingStrategy dd, EmbarkingStrategy ee) {
		mIdentifier = nextPassengerId();
		mCurrentState = PassengerState.WAITING_ON_FLOOR;
		passengerName = name;
		shortName = sname;
		board = bb;
		trav = tt;
		exit = dd;
		enter = ee;
	}
	public String getShortName() {
		return shortName;
	}
	public String getPassengerName() {
		return passengerName;
	}
	public void setState(PassengerState state) {
		mCurrentState = state;
	}

	// TODO: I added this on my own
	public PassengerState getState(){
		return mCurrentState;
	}
	
	/**
	 * Gets the passenger's unique identifier.
	 */
	public int getId() {
		return mIdentifier;
	}
	
	
	/**
	 * Handles an elevator arriving at the passenger's current floor.
	 */
	@Override
	public void elevatorArriving(Floor floor, Elevator elevator) {
		// This is a sanity check. A Passenger should never be observing a Floor they are not waiting on.
		if (floor.getWaitingPassengers().contains(this) && mCurrentState == PassengerState.WAITING_ON_FLOOR) {
			Elevator.Direction elevatorDirection = elevator.getCurrentDirection();

			// TODO: DONE!!!!
			// TODO: check if the elevator is either NOT_MOVING, or is going in the direction that this passenger wants.
			// If so, this passenger becomes an observer of the elevator.
			if(elevatorDirection == Elevator.Direction.NOT_MOVING ||
					(getDestination() > floor.getNumber() && elevatorDirection == Elevator.Direction.MOVING_UP) ||
					(getDestination() < floor.getNumber() && elevatorDirection == Elevator.Direction.MOVING_DOWN)){
				elevator.addObserver(this);
			}
			
		}
		// This else should not happen if your code is correct. Do not remove this branch; it reveals errors in your code.
		else {
			throw new RuntimeException(this.getPassengerName() + toString() + " is observing Floor " + floor.getNumber() + " but they are " +
			 "not waiting on that floor.");
		}
	}
	
	/**
	 * Handles an observed elevator opening its doors. Depart the elevator if we are on it; otherwise, enter the elevator.
	 */
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// The elevator is arriving at our destination. Remove ourselves from the elevator, and stop observing it.
		// Does NOT handle any "next" destination...
		if (mCurrentState == PassengerState.ON_ELEVATOR && exit.willLeaveElevator(this, elevator)) {
			// TODO: DONE!!!!
			// TODO: remove this passenger from the elevator, and as an observer of the elevator. Call the
			// leavingElevator method to allow a derived class to do something when the passenger departs.
			// Set the current state to BUSY.
			exit.departedElevator(this, elevator);			
		}
		// The elevator has arrived on the floor we are waiting on. If the elevator has room for us, remove ourselves
		// from the floor, and enter the elevator.
		else if (mCurrentState == PassengerState.WAITING_ON_FLOOR) {
			// TODO: DONE!!!!
			// TODO: determine if the passenger will board the elevator using willBoardElevator.
			// If so, remove the passenger from the current floor, and as an observer of the current floor;
			// then add the passenger as an observer of and passenger on the elevator. Then set the mCurrentState
			// to ON_ELEVATOR.
            Floor passengerfloor = null;
            for(int i = 1; i <= elevator.getBuilding().getFloorCount(); i++){
                Floor x = elevator.getBuilding().getFloor(i);
                if(x.getWaitingPassengers().contains(this)){
                    passengerfloor = x;
                    break;
                }
            }
            if(board.willBoardElevator(this, elevator) && elevator.getCurrentFloor() == passengerfloor) {
				elevator.getCurrentFloor().removeWaitingPassenger(this);
				elevator.getCurrentFloor().removeObserver(this);
				elevator.addPassenger(this);
				if(!elevator.getObservers().contains(this)) {
					elevator.addObserver(this);
				}
				mCurrentState = PassengerState.ON_ELEVATOR;
				enter.enteredElevator(this, elevator);
			}
			else {
				if(elevator.getCurrentFloor().getNumber() > getDestination()) {
					passengerfloor.requestDirection(Elevator.Direction.MOVING_DOWN);
				}
				else {
					passengerfloor.requestDirection(Elevator.Direction.MOVING_UP);
				}
			}
		}
	}
	public TravelStrategy getTravStrat() {
		return this.trav;
	}
	
	// This will be overridden by derived types.
	@Override
	public String toString() {
		return Integer.toString(getDestination());
	}
	
	@Override
	public void directionRequested(Floor sender, Elevator.Direction direction) {
		// Don't care.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		// Don't care about this.
	}
	
	// The next two methods allow Passengers to be used in data structures, using their id for equality. Don't change 'em.
	@Override
	public int hashCode() {
		return Integer.hashCode(mIdentifier);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Passenger passenger = (Passenger)o;
		return mIdentifier == passenger.mIdentifier;
	}

	@Override
	public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
		return exit.willLeaveElevator(this, elevator);
	}

	@Override
	public void departedElevator(Passenger passenger, Elevator elevator) {
		exit.departedElevator(this, elevator);
	}

	@Override
	public void enteredElevator(Passenger passenger, Elevator elevator) {
		enter.enteredElevator(this, elevator);
		Logger.getInstance().logEvent(enter);
	}

	@Override
	public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
		return board.willBoardElevator(this, elevator);
	}

	@Override
	public void scheduleNextDestination(Passenger passenger, Floor currentFloor) {
		trav.scheduleNextDestination(this, currentFloor);
	}

	@Override
	public int getDestination() {
		return trav.getDestination();
	}

	@Override
	public void elevatorDecelerating(Elevator sender) {
		
	}
	
}