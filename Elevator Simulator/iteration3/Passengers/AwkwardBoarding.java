package cecs277.passengers;

import cecs277.elevators.Elevator;

public class AwkwardBoarding implements BoardingStrategy{
	int mThreshold;
	
	public AwkwardBoarding(int threshold) {
		mThreshold = threshold;
	}

	@Override
	public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
		boolean toreturn = elevator.getPassengerCount() < mThreshold;
		if(!toreturn) {
			mThreshold += 2;
		}
		return toreturn;
	}
	
}
