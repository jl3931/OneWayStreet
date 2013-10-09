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
	private int distCovered;
	private double estPenalty;

    public Car(Car c) {
        dir = c.dir;
        startTime = c.startTime;
        distCovered = c.distCovered;
        computePenalty();
    }
    
    public Car(MovingCar m) {
		this(m.dir, m.startTime);
	}
    
	public Car(int dir, int startTime) {
        assert(dir == 1 || dir == -1);
		this.dir = dir;
		this.startTime = startTime;
	}

    public void moveForward() {
        distCovered++;
        System.out.println("Move Forward: " + dir + " : "+ distCovered);
    }

	public int updateTime() {
		endTime = Simulator.getCurrentTime() + Simulator.getFullLength() - distCovered;
		return endTime;
	}

	public double computePenalty() {
        /*
          Penalty is the amount of score incresing if this car does not move
          A smooth way is to d/dx (L*log(L)) = log(L) + 1
        */
        updateTime();
		estPenalty = Math.log10(endTime - startTime) + 1;
		return estPenalty;
	}
}

