package cecs277.Buildings;

import cecs277.passengers.Passenger;
import cecs277.Simulation;
import cecs277.elevators.Elevator;
import cecs277.elevators.ElevatorObserver;

import java.util.*;

public class Building implements ElevatorObserver, FloorObserver {
	private List<Elevator> mElevators = new ArrayList<>();
	private List<Floor> mFloors = new ArrayList<>();
	private Simulation mSimulation;
	private Queue<Integer> mWaitingFloors = new ArrayDeque<>();
	
	public Building(int floors, int elevatorCount, Simulation sim) {
		mSimulation = sim;
		
		// Construct the floors, and observe each one.
		for (int i = 0; i < floors; i++) {
			Floor f = new Floor(i + 1, this);
			f.addObserver(this);
			mFloors.add(f);
		}
		
		// Construct the elevators, and observe each one.
		for (int i = 0; i < elevatorCount; i++) {
			Elevator elevator = new Elevator(i + 1, this);
			elevator.addObserver(this);
			for (Floor f : mFloors) {
				elevator.addObserver(f);
			}
			mElevators.add(elevator);
		}
	}


	// TODO: DONE!!!!
	// TODO: recreate your toString() here.
	public String toString(){
		String finalString1 = "";
		// for every floor in the building
		for(int floorNumber = mFloors.size(); floorNumber > 0; floorNumber-- ){
			// add the floor number padded to 3 digits with spaces to the string
			finalString1 += String.format("%3d: |", floorNumber);

			// loops through each elevator on a floor
			for(int i = 0; i < mElevators.size(); i++){
				// checking if the elevator is on the current floor
				if(mElevators.get(i).getCurrentFloor().getNumber() == floorNumber){
					finalString1 += " X |";
				}
				else{
					finalString1 += "   |";
				}
			}
			// for every waiting passengers on the current floor
			for(int i = 0; i < mFloors.get(floorNumber-1).getWaitingPassengers().size(); i++){
				// add the waiting passenger to the the string
				finalString1 += " " + (mFloors.get(floorNumber-1).getWaitingPassengers().get(i).getDestination());// +1 after getDest()
			}
			finalString1 += "\n";
		}
		String finalString2 = "";

		// for each elevator in the building
		for(int i = 0; i < mElevators.size(); i++){
			finalString2 += mElevators.get(i).toString() + "\n";
		}

		return finalString1 + finalString2;
	}
	
	
	public int getFloorCount() {
		return mFloors.size();
	}
	
	public Floor getFloor(int floor) {
		return mFloors.get(floor - 1);
	}
	
	public Simulation getSimulation() {
		return mSimulation;
	}
	
	
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// Have to implement all interface methods even if we don't use them.
	}
	
	@Override
	public void elevatorDoorsOpened(Elevator elevator) {
		// Don't care.
	}
	
	@Override
	public void elevatorWentIdle(Elevator elevator) {
		//TODO: DONE!!!!
		// TODO: if mWaitingFloors is not empty, remove the first entry from the queue and dispatch the elevator to that floor.
		if(!mWaitingFloors.isEmpty()){
			elevator.dispatchTo(mFloors.get(mWaitingFloors.remove() -1));
		}
	}
	
	@Override
	public void elevatorArriving(Floor sender, Elevator elevator) {
		//TODO: DONE!!!!
		// TODO: add the floor mWaitingFloors if it is not already in the queue.
		/*if(!mWaitingFloors.contains(sender.getNumber())){
			mWaitingFloors.add(sender.getNumber());
		}*/
		if(sender.getWaitingPassengers().isEmpty() == false){
			if(mWaitingFloors.contains(sender) != true){
				mWaitingFloors.add(sender.getNumber());
			}
		}
	}
	
	@Override
	public void directionRequested(Floor floor, Elevator.Direction direction) {
		//TODO: DONE!!!!
		// TODO: go through each elevator. If an elevator is idle, dispatch it to the given floor.
		// TODO: if no elevators are idle, then add the floor number to the mWaitingFloors queue.
		int idle = 0;
		for(Elevator e : mElevators){
			if(e.isIdle()){
				e.dispatchTo(floor);
				idle++;
			}
		}
		if(idle == 0){
			mWaitingFloors.add(floor.getNumber());
		}
		
	}
}
