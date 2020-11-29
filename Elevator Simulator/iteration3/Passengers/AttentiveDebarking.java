package cecs277.passengers;

import cecs277.elevators.Elevator;

public class AttentiveDebarking implements DebarkingStrategy {

	@Override
	public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
		return elevator.getCurrentFloor().getNumber() == passenger.getDestination();
		
	}

	@Override
	public void departedElevator(Passenger passenger, Elevator elevator) {
		elevator.removeObserver(passenger);
		elevator.removePassenger(passenger);
		elevator.removeRequest(Integer.valueOf((passenger.getDestination())));
		if(elevator.getCurrentFloor().getNumber()!=1) {
			passenger.scheduleNextDestination(passenger, elevator.getCurrentFloor());
		}
	}

}
