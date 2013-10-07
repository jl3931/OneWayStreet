package oneway.g3;

import java.util.Queue;

// Our class to represent parking space. Has two queues.
// Left queue for cars that are heading left
// Right queue for cars that are heading right
// Right bound: 1
// Left bound: -1
public class MyParking extends PlaceNode
{

  private Queue<MyCar> left;
	private Queue<MyCar> right;
	private int capacity;

	public MyParking(int c, Queue<MyCar> leftQueue, Queue<MyCar> rightQueue) {
		this.capacity = c;
		this.left = leftQueue;
		this.right = rightQueue;
	}	

	public void add(MyCar m, int dir){
		if (dir == -1) left.add(m);
		if (dir == 1) right.add(m);
		capacity++;
	}

	public MyCar remove(int dir){
		if (dir == -1) {
			capacity--;
			return left.remove(); 
		}
		else { //(dir == 1) 
			capacity--;
			return right.remove();
		}
	}

}

