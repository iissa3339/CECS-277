package cecs277.passengers;

import cecs277.elevators.Elevator;

public class ClumsyEmbarking implements EmbarkingStrategy {
	private String floorsReq = "";
	private int elevatorNum;
	@Override
	public void enteredElevator(Passenger passenger, Elevator elevator) {
		elevatorNum = elevator.getNumber();
		floorsReq += passenger.getDestination() + " and ";
		Elevator.Direction dir = elevator.getCurrentDirection();
		// elevator.getBuilding().directionRequested(elevator.getBuilding().getFloor(passenger.getDestination()), dir);
		// Ignore the request if it's the same as the elevator is on
		if(dir == Elevator.Direction.MOVING_DOWN && (elevator.getCurrentFloor().getNumber()!=passenger.getDestination()+1)) {
			/*
			if(!elevator.getFloorRequests().contains(Integer.valueOf(passenger.getDestination()+1))) {
				elevator.requestFloor(passenger.getDestination()+1);
			}
			*/
			elevator.requestFloor(passenger.getDestination()+1);
			floorsReq += passenger.getDestination()+1;
		}
		else if(dir == Elevator.Direction.MOVING_UP && (elevator.getCurrentFloor().getNumber()!=passenger.getDestination()-1)){
			/*
			if(!elevator.getFloorRequests().contains(Integer.valueOf(passenger.getDestination()-1))) {
				elevator.requestFloor(passenger.getDestination()-1);
			}
			*/
			elevator.requestFloor(passenger.getDestination()-1);
			floorsReq += passenger.getDestination()-1;
		}
	}
	public String toString() {
		return "clumsily requested floors " + floorsReq + " on elevator " + elevatorNum;
	}
	
}
