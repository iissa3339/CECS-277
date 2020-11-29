package cecs277.passengers;

import cecs277.elevators.Elevator;

public class ConfusedDebarking implements DebarkingStrategy {

	@Override
	public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
		if(elevator.getCurrentFloor().getNumber() == 1) {
			return true;
		}
		return false;
	}

	@Override
	public void departedElevator(Passenger passenger, Elevator elevator) {
		elevator.removeObserver(passenger);
		elevator.removePassenger(passenger);
		elevator.removeRequest(Integer.valueOf((passenger.getDestination())));
		elevator.getCurrentFloor().removeWaitingPassenger(passenger);
		elevator.getCurrentFloor().removeObserver(passenger);
	}

}
