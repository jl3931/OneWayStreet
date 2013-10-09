package oneway.g3;

import oneway.sim.MovingCar;
import oneway.sim.Parking;
import java.util.*;



public class Player extends oneway.sim.Player {

    private Simulator sim;
    public Player() { }

    public void init(int nsegments, int[] nblocks, int[] capacity) {
        this.nsegments = nsegments;
        this.capacity = capacity.clone();
        this.nblocks = nblocks.clone();
        sim = new Simulator(nsegments, nblocks, capacity);
    }

    public void setLights(MovingCar[] movingCars,
                          Parking[] left,
                          Parking[] right,
                          boolean[] llights,
                          boolean[] rlights)
    {
        // Strategy:
        // 1. initially turn all traffic lights off
        // 2. check each parking lot
        //    if it has pending cars, try to turn the light green
        //    a) if there is no opposite traffic, go ahead and turn right
        //    b) if there is opposite traffic, but the parking lot is piled up
        //       turn red the opposite traffic light.
        //       resume turning the traffic light after the traffic is clear
        // This strategy avoids car crash, but it cannot guarantee all cars
        // will be delivered in time and the parking lot is never full
        int[] carsOnRoad = new int[nsegments];
        int[] leftq = new int[nsegments+1];
        int[] rightq = new int[nsegments+1];
        for (MovingCar c : movingCars)
            carsOnRoad[c.segment]++;
        for (int i = 0; i <= nsegments; i++) {
            if (left[i] == null)
                leftq[i] = 0;
            else
                leftq[i] = left[i].size();
            if (right[i] == null)
                rightq[i] = 0;
            else
                rightq[i] = right[i].size();
        }
        sim.update(carsOnRoad, leftq, rightq);

        for (int i = 0; i != nsegments; ++i) {
            llights[i] = false;
            rlights[i] = false;
        }

        boolean[] indanger = new boolean[nsegments+1];
        
        // find out almost full parking lot
        for (int i = 1; i != nsegments; ++i) {
            if (leftq[i] + rightq[i] 
                > capacity[i] * 0.8) {
                indanger[i] = true;
            }            
        }

        for (int i = 0; i != nsegments; ++i) {
            // if right bound has car
            // and the next parking lot is not in danger
            boolean safe_to_send_right = !indanger[i+1] && !hasTraffic(movingCars, i, -1);
            boolean safe_to_continue_right = i!=0 && safe_to_send_right && hasTraffic(movingCars, i-1, 1); 
            if ((right[i].size() > 0 && safe_to_send_right) || safe_to_continue_right) {
                rlights[i] = true;
            }
            
            if (left[i+1].size() > 0 &&
                !indanger[i] &&
                !hasTraffic(movingCars, i, 1)) {
                llights[i] = true;
            }

            // // if both left and right is on
            // // find which dir is in more danger
            // if (rlights[i] && llights[i]) {
            //     double lratio = 1.0 * (left[i+1].size() + right[i+1].size()) / capacity[i+1];
            //     double rratio = 1.0 * (left[i].size() + right[i].size()) / capacity[i];
            //     if (lratio > rratio)
            //         rlights[i] = false;
            //     else
            //         llights[i] = false;
            // }
        }
        if (sim.safetyCheck(llights, rlights))
            System.out.println("Good");
        else
            System.out.println("Fail");

        sim.oneStep(llights, rlights);
    }


    // check if the segment has traffic
    private boolean hasTraffic(MovingCar[] cars, int seg, int dir) {
        for (MovingCar car : cars) {
            if (car.segment == seg && car.dir == dir)
                return true;
        }
        return false;
    }

    private int nsegments;
    private int[] nblocks;
    private int[] capacity;
}
