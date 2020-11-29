package cecs277.passengers;

import cecs277.elevators.Elevator;

public class ThresholdBoarding implements BoardingStrategy{
	int mThreshold;
	
	public ThresholdBoarding(int threshold) {
		mThreshold = threshold;
	}

	@Override
	public boolean willBoardElevator(Passenger passenger, Elevator elevator) {
		return elevator.getPassengerCount() <= mThreshold;
	}
	
}
