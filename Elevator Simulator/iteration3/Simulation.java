package cecs277;

import cecs277.buildings.Building;
import cecs277.events.SimulationEvent;
import cecs277.events.SpawnPassengerEvent;
import cecs277.logging.Logger;
import cecs277.logging.StandardOutLogger;
import cecs277.passengers.ChildFactory;
import cecs277.passengers.DeliveryPersonFactory;
import cecs277.passengers.JerkFactory;
import cecs277.passengers.PassengerFactory;
import cecs277.passengers.StonerFactory;
import cecs277.passengers.VisitorFactory;
import cecs277.passengers.WorkerFactory;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.*;

public class Simulation {
	private Random mRandom;
	private PriorityQueue<SimulationEvent> mEvents = new PriorityQueue<>();
	private long mCurrentTime;
	private Building mBuilding;
	private List<PassengerFactory> passengers = new ArrayList<>();
	
	public long getTime() {
		return mCurrentTime;
	}
	public List<PassengerFactory> getPassengers() {
		return passengers;
	}

	public Building getBuilding() {
		return mBuilding;
	}

	/**
	 * Seeds the Simulation with a given random number generator.
	 */
	public Simulation(Random random, List<PassengerFactory> toPut) {
		mRandom = random;
		passengers = toPut;
		Logger.setInstance(new StandardOutLogger(this));
	}
	
	/**
	 * Gets the current time of the simulation.
	 */
	public long currentTime() {
		return mCurrentTime;
	}
	
	/**
	 * Access the Random object for the simulation.
	 */
	public Random getRandom() {
		return mRandom;
	}
	
	/**
	 * Adds the given event to a priority queue sorted on the scheduled time of execution.
	 */
	public void scheduleEvent(SimulationEvent ev) {
		mEvents.add(ev);
	}

	
	public void startSimulation(Scanner input) {
		Scanner s = new Scanner(System.in);
		System.out.println("How many floors: ");
		int numFloors = s.nextInt();

		System.out.println("How many elevators: ");
		int numElevators = s.nextInt();

		mBuilding = new Building(numFloors, numElevators, this);
		SpawnPassengerEvent ev = new SpawnPassengerEvent(0, mBuilding);
		scheduleEvent(ev);
		
		long nextSimLength = -1;
		
		// Set this boolean to true to make the simulation run at "real time".
		boolean simulateRealTime = false;
		// Change the scale below to less than 1 to speed up the "real time".
		double realTimeScale = 1.0;
		
		// TODO: the simulation currently stops at 200s. Instead, ask the user how long they want to simulate.
		System.out.println("Simulate how many steps: ");
		nextSimLength = s.nextInt();

		while(nextSimLength > -1){

			long nextStopTime = mCurrentTime + nextSimLength;
			// If the next event in the queue occurs after the requested sim time, then just fast forward to the requested sim time.
			if (mEvents.peek().getScheduledTime() >= nextStopTime) {
				mCurrentTime = nextStopTime;
			}

			// As long as there are events that happen between "now" and the requested sim time, process those events and
			// advance the current time along the way.
			while (!mEvents.isEmpty() && mEvents.peek().getScheduledTime() <= nextStopTime) {
				SimulationEvent nextEvent = mEvents.poll();

				long diffTime = nextEvent.getScheduledTime() - mCurrentTime;
				if (simulateRealTime && diffTime > 0) {
					try {
						Thread.sleep((long)(realTimeScale * diffTime * 1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				mCurrentTime += diffTime;
				nextEvent.execute(this);
				System.out.println(nextEvent);
			}

			// TODO: print the Building after simulating the requested time.
			System.out.println(mBuilding);


		 //TODO: the simulation stops after one round of simulation. Write a loop that continues to ask the user
		 //how many seconds to simulate, simulates that many seconds, and stops only if they choose -1 seconds.
			System.out.println("Simulate how many steps: ");
			nextSimLength = s.nextInt();
		}
		s.close();
	}
	Logger st = new StandardOutLogger(this);
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		// ask user for a seed value
		
		System.out.println("Enter a seed value: ");

		// save the next integer input to seedValue
		int seedValue = s.nextInt();

		// TODO: ask the user for a seed value and change the line below.
		List<PassengerFactory> insert = new ArrayList<>();
		insert.add(new VisitorFactory());
		insert.add(new WorkerFactory());
		insert.add(new ChildFactory());
		insert.add(new DeliveryPersonFactory());
		insert.add(new StonerFactory());
		insert.add(new JerkFactory());
		
		Simulation sim = new Simulation(new Random(seedValue),insert);
		sim.startSimulation(s);
	}
}
