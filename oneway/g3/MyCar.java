package oneway.g3;

import oneway.sim.MovingCar;
import java.lang.Math;

//Our class to represent cars
public class MyCar
{
	// Right bound: 1
  // Left bound: -1
  public final int dir;
  public final int startTime;
	private int endTime;
	private int distCovered;
	private double estPenalty;

  public MyCar(MovingCar m) {
		this.dir = m.dir;
		this.startTime = m.startTime;
	}

	public MyCar(int d, int time) {
		this.dir = d;
		this.startTime = time;
	}

	public int updateTime() {
		endTime = startTime + Player.globalLength - distCovered;
		return endTime;
	}

	public double computePenalty() {
		estPenalty = endTime * Math.log10(endTime);
		return estPenalty;
	}
	
}

