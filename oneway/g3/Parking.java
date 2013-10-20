package oneway.g3;

import java.util.LinkedList;

// Our class to represent parking space. Has two queues.
// Left queue for cars that are heading left
// Right queue for cars that are heading right
// Right bound: 1
// Left bound: -1
public class Parking extends PlaceNode {
    public LinkedList<Car> leftq;
	public LinkedList<Car> rightq;
	public int capacity;
    // lights at the side of parking
    public boolean llight;
    public boolean rlight;

    public Parking(int capacity, oneway.sim.Parking leftCars, oneway.sim.Parking rightCars, boolean llight, boolean rlight) {
        this.capacity = capacity;
        this.llight = llight;
        this.rlight = rlight;
        leftq = new LinkedList<Car>();
        rightq = new LinkedList<Car>();
        if (leftCars != null) {
            for (Integer startTime : leftCars) {
                Car newC = new Car(-1, startTime);
                leftq.add(newC);
            }
        }
        if (rightCars != null) {
            for (Integer startTime : rightCars) {
                Car newC = new Car(1, startTime);
                rightq.add(newC);
            }
        }
    }

    public Parking(PlaceNode left, Parking p) {
        super(left);
        leftq = new LinkedList<Car>();
        rightq = new LinkedList<Car>();
        capacity = p.capacity;
        for (Car c : p.leftq) {
            Car newC = new Car(c);
            leftq.add(c);
        }
        for (Car c : p.rightq) {
            Car newC = new Car(c);
            rightq.add(c);
        }
        llight = p.llight;
        rlight = p.rlight;
    }

    public void updateParking(Parking p) {
        this.capacity = p.capacity;
        this.llight = p.llight;
        this.rlight = p.rlight;

        this.leftq = new LinkedList<Car>();
        for (Car c : p.leftq) {
            this.leftq.add(new Car(c));
        }
        this.rightq = new LinkedList<Car>();
        for (Car c : p.rightq) {
            this.rightq.add(new Car(c));
        }
    }

	public Parking(PlaceNode left, int capacity) {
        super(left);
		this.capacity = capacity;
        leftq = new LinkedList<Car>();
        rightq = new LinkedList<Car>();
    }

	public void add(Car c) {
        // System.out.println("CAR ADDED TO PARKING LOT WITH CAPACITY " + capacity);
		if (c.dir == -1) leftq.add(c);
		if (c.dir == 1) rightq.add(c);
	}

	private Car remove(int dir) {
        assert(dir == 1 || dir == -1);
		if (dir == -1) {
            assert(!leftq.isEmpty());
			return leftq.remove(); 
		}
		else {
            assert(!rightq.isEmpty());
			return rightq.remove();
		}
	}

    public void setLight(boolean llight, boolean rlight) {
        this.llight = llight;
        this.rlight = rlight;
    }

    public void setRightLight(boolean rlight) {;
        this.rlight = rlight;
    }

    public void setLeftLight(boolean llight) {
        this.llight = llight;
    }

    public int getCapacity() {
        return capacity;
    }
    
    public int load() {
        return leftq.size() + rightq.size();
    }

    public int leftLoad() {
        return leftq.size();
    }

    public int rightLoad() {
        return rightq.size();
    }

    public void pop(int dir) {
        if (dir == 1)
            rightq.pop();
        else
            leftq.pop();
    }

    public double getDeliveries() {
        double penalty = 0.0;
        // System.out.println("boom" + leftq.size());
        if(left == null) {
            //System.out.println("boom" + leftq.size());
            for (Car c : leftq) {
                double T = c.getTime();
                // System.out.println("boom" + T);
                penalty += (T+1)*Math.log10(T+1);
            }
        }
        if(right == null) {
            for (Car c : rightq) {
                double T = c.getTime();
                penalty += (T)*Math.log10(T);
            }
        }
        return penalty;
    }

    public boolean step() {
        // System.out.println("Parking step");
        // System.out.println("llight: " + llight);
        // System.out.println("rlight: " + rlight);
        if (llight && !leftq.isEmpty()) {
            if (left.getCar(-1, 0) == null &&
                left.getCar(-1, 1) == null) {
                // System.out.println("Move out left");
                Car c = remove(-1);
                left.add(c);
            }
        }
        if (rlight && !rightq.isEmpty()) {
            if (right.getCar(1, 0) == null &&
                right.getCar(1, 1) == null) {
                // System.out.println("Move out right");
                Car c = remove(1);
                right.add(c);
            }
        }

        if (left == null)
            leftq.clear();
        if (right == null)
            rightq.clear();

        if (load() > capacity)
            return false;
        else
            return true;

    }
}

