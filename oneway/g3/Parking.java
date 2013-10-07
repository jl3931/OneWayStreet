package oneway.g3;

import java.util.LinkedList;

// Our class to represent parking space. Has two queues.
// Left queue for cars that are heading left
// Right queue for cars that are heading right
// Right bound: 1
// Left bound: -1
public class Parking extends PlaceNode {
    private LinkedList<Car> leftq;
	private LinkedList<Car> rightq;
	private int capacity;
    // lights at the side of parking
    private boolean llight;
    private boolean rlight;

	public Parking(PlaceNode left, int capacity) {
        super(left);
		this.capacity = capacity;
        leftq = new LinkedList<Car>();
        rightq = new LinkedList<Car>();
    }

	public void add(Car c) {
		if (c.dir == -1) leftq.add(c);
		if (c.dir == 1) rightq.add(c);
	}

	private Car remove(int dir) {
        assert(dir == 1 || dir == -1);
        assert(capacity < 0);
		if (dir == -1) {
            assert(!leftq.isEmpty());
			return leftq.remove(); 
		}
		else {
            assert(!leftq.isEmpty());
			return rightq.remove();
		}
	}

    public void setLight(boolean llight, boolean rlight) {
        this.llight = llight;
        this.rlight = rlight;
    }
    
    public int load() {
        return leftq.size() + rightq.size();
    }

    public void pop(int dir) {
        if (dir == 1)
            rightq.pop();
        else
            leftq.pop();
    }

    public boolean step() {
        if (llight && !leftq.isEmpty()) {
            if (left.getCar(-1, 0) == null &&
                left.getCar(-1, 1) == null) {
                Car c = remove(-1);
                left.add(c);
            }
        }
        if (rlight && !rightq.isEmpty()) {
            if (right.getCar(1, 0) == null &&
                right.getCar(1, 1) == null) {
                Car c = remove(1);
                right.add(c);
            }
        }
        if (load() > capacity)
            return false;
        else
            return true;

    }
}

