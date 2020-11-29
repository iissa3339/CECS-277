package cecs277.passengers;

import cecs277.elevators.Elevator;

public class DisruptiveEmbarking implements EmbarkingStrategy {

	@Override
	public void enteredElevator(Passenger passenger, Elevator elevator) {
		elevator.requestFloor(passenger.getDestination());
        int numFloors = elevator.getBuilding().getFloorCount();

        if(elevator.getCurrentDirection() == Elevator.Direction.MOVING_UP){
            int nextUp = passenger.getDestination() +1;
            for(int i = nextUp; i <= numFloors; i++){
                elevator.requestFloor(i);
            }
        }
        else if(elevator.getCurrentDirection() == Elevator.Direction.MOVING_DOWN){
            int nextDown = passenger.getDestination() -1;
            for(int i = nextDown; i >= 1; i--){
                elevator.requestFloor(i);
            }
        }
	}
}
