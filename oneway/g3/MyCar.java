package oneway.g3;

import oneway.sim.MovingCar;

//Our class to represent cars
public class MyCar
{
	// Right bound: 1
    // Left bound: -1
    public final int dir;
    public final int startTime;

    public MyCar(MovingCar m){
		this.dir=m.dir;
		this.startTime=m.startTime;
	}
	public MyCar(int d, int time){
		this.dir=d;
		this.startTime=time;
	}
}

