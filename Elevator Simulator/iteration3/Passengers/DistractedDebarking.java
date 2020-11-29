package cecs277.passengers;

import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.events.PassengerNextDestinationEvent;
import cecs277.logging.Logger;

public class DistractedDebarking implements DebarkingStrategy {
	private boolean WannaMistake = true;
	private boolean reappeared;
	@Override
	public boolean willLeaveElevator(Passenger passenger, Elevator elevator) {
		if(WannaMistake && elevator.getCurrentFloor().getNumber()==passenger.getDestination()) {
			WannaMistake = false;
			Logger.getInstance().logString(elevator.getBuilding().getSimulation().getTime()+"s: "+
			passenger.getPassengerName()+" "+passenger.getId()+" is distracted and missed their stop on floor "+
					passenger.getDestination()+" !");
			return false;
		}
		else if(reappeared && !WannaMistake && elevator.getCurrentFloor().getNumber() == passenger.getDestination()) {
			return true;
		}
		else if(!reappeared && !WannaMistake && elevator.getCurrentFloor().getNumber() != passenger.getDestination()) {
			return true;
		}
		return false;
		/*
		if(elevator.getCurrentDirection()==Elevator.Direction.MOVING_UP) {
			if(elevator.getCurrentFloor().getNumber()<=passenger.getDestination()) {
				return false;
			}
		}
		else if(elevator.getCurrentDirection()==Elevator.Direction.MOVING_DOWN) {
			if(elevator.getCurrentFloor().getNumber()>=passenger.getDestination()) {
				return false;
			}
		}
		else if(elevator.getCurrentDirection()==Elevator.Direction.NOT_MOVING) {
			if(elevator.getCurrentFloor().getNumber() == passenger.getDestination()) {
				return false;
			}
		}
		return true;
		*/
	}

	@Override
	public void departedElevator(Passenger passenger, Elevator elevator) {
		// Got off wrong floor, will reappear after 5 seconds to go to correct destination
		if(elevator.getCurrentFloor().getNumber() != passenger.getDestination() && !reappeared) {
			elevator.removeObserver(passenger);
			elevator.removePassenger(passenger);
			elevator.removeRequest(Integer.valueOf((passenger.getDestination())));
			Simulation sim = elevator.getBuilding().getSimulation();
			PassengerNextDestinationEvent event = new PassengerNextDestinationEvent(sim.currentTime()+5,passenger,elevator.getCurrentFloor());
			sim.scheduleEvent(event);
			reappeared = true;
		}
		// Finally got to their destination
		else if(elevator.getCurrentFloor().getNumber()==passenger.getDestination() && passenger.getDestination() != 1) {
			Logger.getInstance().logString(elevator.getBuilding().getSimulation().getTime()+"s: "+
					passenger.getPassengerName()+" "+passenger.getId()+" finally debarked at their destination floor "
					+ passenger.getDestination());
			elevator.removeObserver(passenger);
			elevator.removePassenger(passenger);
			elevator.removeRequest(Integer.valueOf((passenger.getDestination())));
			passenger.scheduleNextDestination(passenger, elevator.getCurrentFloor());
		}
		// Going to leave the building and return home
		else if(elevator.getCurrentFloor().getNumber()==passenger.getDestination()&&passenger.getDestination()==1){
			elevator.removeObserver(passenger);
			elevator.removePassenger(passenger);
			elevator.removeRequest(Integer.valueOf((passenger.getDestination())));
		}
	}

}
