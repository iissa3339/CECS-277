import java.util.Random;
import java.util.Scanner;
public class simulation {
	public static Random mRandom;
	public static Random getRandom() {
		return mRandom;
	}
	public static void main(String args[]) {
		System.out.print("Please enter a seed value: ");
		Scanner input = new Scanner(System.in);
		int seed = input.nextInt();
		while(seed < 0) {
			System.out.print("Please enter a seed value: ");
			seed = input.nextInt();
		}
		mRandom = new Random(seed);
		System.out.print("How many floors? ");
		int numberFloors = input.nextInt();
		System.out.print("How many elevators? ");
		int numberElevators = input.nextInt();
		building ourBuilding = new building(numberFloors, numberElevators);
		System.out.print(ourBuilding);
		System.out.println("Simulate how many steps? ");
		int numsteps = input.nextInt();
		while(numsteps != 0) {
			for(int b = 0; b < numsteps; b++) {
				System.out.println("Step " + (b+1));
				ourBuilding.tick();
				System.out.print(ourBuilding);
			}
			System.out.println("Simulate how many steps? ");
			numsteps = input.nextInt();
		}
		System.out.print(ourBuilding);
		input.close();
	}
}
