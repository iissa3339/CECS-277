import java.util.ArrayList;
import java.util.Iterator;
public class elevator {
	private int curfloor;
	private state curstate;
	private direction curdirection;
	private ArrayList<Integer> people;
	private int elevatornum;
	private building buldn;
	public enum state{
		IDLE_STATE,
		DOORS_OPENING,
		LOADING_PASSENDGERS,
		UNLOADING_PASSENGERS,
		DOORS_CLOSING,
		ACCELERATING,
		MOVING,
		DECELERATING
	}
	
	public enum direction{
		NOT_MOVING,
		MOVING_UP,
		MOVING_DOWN
	}
	
	public elevator(int elevnum, building ref) {
		curfloor = 0; //the index of floor 1 is 0
		curstate = state.IDLE_STATE;
		curdirection = direction.NOT_MOVING;
		people = new ArrayList<>();
		elevatornum = elevnum;
		buldn = ref;
	}
	
	public int getCurFloor() {
		return curfloor;
	}
	
	public boolean isEmpty() {
		if(people.size() == 0) {
			return true;
		}
		return false;
	}
	
	public void tick() {
		if(curstate == state.IDLE_STATE) {
			if(people.size() >= 1) {
				curstate = state.ACCELERATING;
			}
			else {
				if(buldn.getFloor(curfloor).size() > 0) {
					curstate = state.DOORS_OPENING;
				}
				else {
					curstate = state.IDLE_STATE;
				}	
			}
		}
		else if(curstate == state.DOORS_OPENING) {
			for(Iterator<Integer> iterator = people.iterator(); iterator.hasNext();) {
				int passenger = iterator.next();
				if(passenger == curfloor+1) {
					curstate = state.UNLOADING_PASSENGERS;
				}
			}
			if(curstate == state.DOORS_OPENING) {
				curstate = state.LOADING_PASSENDGERS;
			}
		}
		else if(curstate == state.UNLOADING_PASSENGERS) {
			for(Iterator<Integer> iterator = people.iterator(); iterator.hasNext();) {
				int v = iterator.next();
				if(v == curfloor+1) {
					iterator.remove();
				}
			}
			if(people.size() == 0) {
				curdirection = direction.NOT_MOVING;
				if(buldn.getFloor(curfloor).isEmpty()) {
					curstate = state.DOORS_CLOSING;
				}
				else {
					curstate = state.LOADING_PASSENDGERS;
				}
			}
			for(Iterator<Integer> shs = buldn.getFloor(curfloor).iterator(); shs.hasNext();) {
				int vv = shs.next();
				if(vv > curfloor+1 && curdirection == direction.MOVING_UP) {
					curstate = state.LOADING_PASSENDGERS;
					break;
				}
				else if(vv < curfloor+1 && curdirection == direction.MOVING_DOWN) {
					curstate = state.LOADING_PASSENDGERS;
					break;
				}
			}
			if(curstate == state.UNLOADING_PASSENGERS) {
				curstate = state.DOORS_CLOSING;
			}
		}
		else if(curstate == state.LOADING_PASSENDGERS) {
			for(Iterator<Integer> iterator = buldn.getFloor(curfloor).iterator(); iterator.hasNext();) {
				int gg = iterator.next();
				if(curdirection == direction.NOT_MOVING) {
					people.add(gg);
					if(gg > curfloor+1) {
						curdirection = direction.MOVING_UP;
					}
					else {
						curdirection = direction.MOVING_DOWN;
					}
					iterator.remove();
				}
				else if(gg > curfloor+1 && curdirection == direction.MOVING_UP) {
					people.add(gg);
					iterator.remove();
				}
				else if(gg < curfloor+1 && curdirection == direction.MOVING_DOWN) {
					people.add(gg);
					iterator.remove();
				}
			}
			curstate = state.DOORS_CLOSING;
		}
		else if(curstate == state.DOORS_CLOSING) {
			if(people.size() > 0) {
				curstate = state.ACCELERATING;
			}
			else
				curstate = state.IDLE_STATE;
		}
		else if(curstate == state.ACCELERATING) {
			curstate = state.MOVING;
		}
		else if(curstate == state.MOVING) {
			int nextfloor = curfloor;
			if(curdirection == direction.MOVING_UP) {
				if(curfloor < buldn.floorSize()-1) {
					nextfloor+=1;
				}
			}
			else if(curdirection == direction.MOVING_DOWN) {
				if(curfloor > 0) {
					nextfloor-=1;
				}
			}
			curfloor = nextfloor;
			for(Iterator<Integer> iterator = people.iterator(); iterator.hasNext();) {
				int temp = iterator.next();
				if(temp == (curfloor+1)) {
					curstate = state.DECELERATING;
					break;
				}
			}
			if(0 < curfloor && curfloor < buldn.floorSize()-1 && curstate!= state.DECELERATING) {
				for(Iterator<Integer> iter = buldn.getFloor(curfloor).iterator(); iter.hasNext();) {
					int value = iter.next();
					if(value > curfloor+1 && curdirection == direction.MOVING_UP) {
						curstate = state.DECELERATING;
					}
					else if(value < curfloor+1 && curdirection == direction.MOVING_DOWN) {
						curstate = state.DECELERATING;
					}
				}
			}
		}
		else if(curstate == state.DECELERATING) {
			curstate = state.DOORS_OPENING;
			if(people.size() == 0) {
				curdirection = direction.NOT_MOVING;
			}
		}
	}
	public String toString() {
		return "Elevator " + (elevatornum+1) + " - Floor " + (curfloor+1) + " - " + curstate + " - " + curdirection + " - Passenger " + people + "\n";
	}
}
