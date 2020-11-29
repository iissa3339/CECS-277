import java.util.ArrayList;
import java.util.Random;
public class building {
	private ArrayList<elevator> elevators;
	private ArrayList<ArrayList<Integer>> floors;
	public building(int numfloors, int numelevator) {
		floors = new ArrayList<>();
		elevators = new ArrayList<>();
		for(int i = 0; i < numfloors; i++) {
			floors.add(new ArrayList<Integer>());
		}
		for(int j = 0; j < numelevator; j++) {
			elevators.add(new elevator(j,this));
		}
	}
	public ArrayList<Integer> getFloor(int floorNumber){
		return floors.get(floorNumber);
	}
	public int floorSize() {
		return floors.size();
	}
	public void tick() {
		Random rand = simulation.getRandom();
		for(int j = 0; j < floors.size(); j++) {
			int randomm = rand.nextInt(20);
			if(randomm == 0) {
				int toSpawn = rand.nextInt(floors.size());
				while (toSpawn == j){
					toSpawn = rand.nextInt(floors.size());
				}
				floors.get(j).add((toSpawn+1));
				System.out.printf("Adding passenger with destination %d to floor %d\n",toSpawn+1,(j+1));
				break;
			}
		}
		for(elevator m : elevators) {
			m.tick();
		}
	}
	public String toString() {
		String toreturn = "";
		for(int z = floors.size(); z >= 1; z--) {
			toreturn += String.format("%2d", z) + ": |";
			for(int x = 0; x < elevators.size(); x++) {
				if(elevators.get(x).getCurFloor() == z-1) {
					toreturn += " X |";
				}
				else{
					toreturn += "   |";
				}
				if(x == elevators.size()-1) {
					for(int pass : floors.get(z-1)) {
						toreturn += " " + pass;
					}
				}
			}
			toreturn += "\n";
		}
		for(elevator i : elevators) {
			toreturn += i;
		}
		return toreturn;
	}
}
