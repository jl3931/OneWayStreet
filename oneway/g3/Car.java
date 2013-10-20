package oneway.g3;

import oneway.sim.MovingCar;
import java.lang.Math;

public class Car {
	// Right bound: 1
    // Left bound: -1
    public final int dir;
    public final int startTime;
	private int endTime;
    // car move "from road to road" or "from road to parking" +1
	public int distCovered;
    public int segment;
    public int block;
	private double estPenalty;

    public Car(Car c) {
        this.dir = c.dir;
        this.startTime = c.startTime;
        this.distCovered = c.distCovered;
        this.segment = c.segment;
        this.block = c.block;
    }
    
    public Car(MovingCar m) {
		this.dir = m.dir;
        this.startTime = m.startTime;
        this.segment = m.segment;
        this.block = m.block;
	}
    
	public Car(int dir, int startTime) {
        assert(dir == 1 || dir == -1);
		this.dir = dir;
		this.startTime = startTime;
	}

    public void moveForward() {
        distCovered++;
        if (distCovered == Simulator.getFullLength()) {
            // System.out.println("DELIVERED!");
            Simulator.updateDeliveries(getTime());
        }
        // System.out.println("Move Forward: " + dir + " : "+ distCovered);
    }

	public int updateTime() {
		endTime = Simulator.getCurrentTime();
		return endTime;
	}

    public String toString() {
        String rpr = "Start: " + startTime + "\n";
        rpr += "Segment: " + this.segment + "\n";
        rpr += "Blck: " + this.block + "\n";
        return rpr;
    }

	public double getTime() {
        /*
          Penalty is the amount of score incresing if this car does not move
          A smooth way is to d/dx (L*log(L)) = log(L) + 1
        */
        updateTime();
		
		return 1 + endTime - startTime;
	}
}

