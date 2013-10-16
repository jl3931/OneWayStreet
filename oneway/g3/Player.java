package oneway.g3;

import oneway.sim.MovingCar;
// Parking;
import java.util.*;



public class Player extends oneway.sim.Player {

    private Simulator sim;
    public Parking[] parkingLots; 
    public Player() { }
    public final int RIGHT = 1;
    public final int LEFT = -1;

    public void init(int nsegments, int[] nblocks, int[] capacity) {
        this.nsegments = nsegments;
        this.capacity = capacity.clone();
        this.nblocks = nblocks.clone();
        sim = new Simulator(nsegments, nblocks, capacity);
    }

    public void setLights(MovingCar[] movingCars, oneway.sim.Parking[] left, oneway.sim.Parking[] right, boolean[] llights, boolean[] rlights) {
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

        // WE FIRST GENERATE ALL OF THE ABSTARCTED OBJECTS TO MAKE THE STRATEGIES EASIER TO UNDERSTAND
        this.parkingLots = generateParkingLots(left, right, llights, rlights);
        // HERE WE GENERATE A BUNCH OF SUCCESSORS NODES BASED ON DIFFERENT STRATEGIES

        boolean[][] strategy = basic_strategy(movingCars, this.parkingLots);
        setStrategy(strategy, llights, rlights);
        // WE THEN SELECT THE STRATEGY WITH THE BEST HEURISTIC THAT PASSES THE SAFETY CHECK AND SIMULATE INTO THE FUTURE. 


        if (sim.safetyCheck(llights, rlights))
            System.out.println("Good");
        else
            System.out.println("Fail");

        sim.oneStep(llights, rlights);
    }

    public void setStrategy(boolean[][] strategy, boolean[] llights, boolean[] rlights) {
        for (int i=1; i != strategy[0].length; i++) {
            llights[i-1] = strategy[0][i];
        }
        for (int i=0; i != strategy[1].length-1; i++) {
            rlights[i] = strategy[1][i];
        }
    }

    public boolean[][] basic_strategy(MovingCar[] movingCars, Parking[] parkings) {
        boolean safe_to_send_right;
        boolean safe_to_continue_right;
        boolean future_overflow_possibility_R;
        boolean incoming_right;
        boolean cars_going_left;
        boolean safe_to_send_left;
        boolean safe_to_continue_left;
        boolean future_overflow_possibility_L;
        boolean incoming_left;
        int priority = RIGHT;

        boolean[][] strategy = new boolean[2][parkings.length];

        for (int i = 0; i != parkings.length; ++i) {
            parkings[i].setLight(false, false);
        }

        boolean[] indanger = new boolean[parkings.length];
        
        // find out almost full parking lot
        for (int i = 0; i != nsegments+1; ++i) {
            // System.out.println("capacity is " + parkings[i].getCapacity());
            // System.out.println("load is " + parkings[i].load() + " + " + countTraffic(movingCars, i-1, 1) + countTraffic(movingCars, i, -1));
            if (parkings[i].load() + countTraffic(movingCars, i-1, RIGHT) + countTraffic(movingCars, i, LEFT) >= parkings[i].getCapacity()) {
                indanger[i] = true;
            }
            System.out.println(indanger[i]);            
        }

        // WE HANDLE EDGE CASES SEPARATELY FROM THE MIDDLE CASES

        // FIRST PARKING LOT
        safe_to_send_right = !hasTraffic(movingCars, 0, LEFT) && !indanger[1];
        if (safe_to_send_right) {
            parkings[0].setRightLight(true);
        }
        if (parkings[0].load() == 0 && parkings[1].load() > 0) {
            parkings[0].setRightLight(false);
        }

        // WE HANDLE THE MIDDLE CASES
        for (int i = 1; i != parkings.length-1; ++i) {
            safe_to_send_right = !hasTraffic(movingCars, i, LEFT) && (!parkings[i+1].llight || priority == RIGHT);
            
            safe_to_continue_right = safe_to_send_right && hasTraffic(movingCars, i-1, RIGHT); 
            future_overflow_possibility_R = hasTraffic(movingCars, i+1, LEFT);
            if (future_overflow_possibility_R) {
                safe_to_continue_right =  safe_to_continue_right && (countTraffic(movingCars, i, RIGHT) + countTraffic(movingCars, i+1, LEFT) + parkings[i+1].load()) < parkings[i+1].getCapacity();
            }
            incoming_left = hasTraffic(movingCars, i+1, LEFT) && !parkings[i+1].llight;
            if ((parkings[i].rightLoad() > 0 && safe_to_send_right && !indanger[i+1]) || safe_to_continue_right) {
                parkings[i].setRightLight(true);
            }
            
            cars_going_left = parkings[i].leftLoad() > 0;
            safe_to_send_left = !hasTraffic(movingCars, i-1, RIGHT) && (!parkings[i-1].rlight || priority == LEFT);
            
            safe_to_continue_left = safe_to_send_left && hasTraffic(movingCars, i, LEFT);
            future_overflow_possibility_L = hasTraffic(movingCars, i-1, RIGHT);
            if (future_overflow_possibility_L) {
                safe_to_continue_left =  safe_to_continue_left && (countTraffic(movingCars, i-1, RIGHT) + countTraffic(movingCars, i, LEFT) + parkings[i-1].load()) < parkings[i-1].getCapacity();
            } 
            if ((cars_going_left && safe_to_send_left && !indanger[i-1]) || safe_to_continue_left) {
                parkings[i].setLeftLight(true);
            }
        }

        // LAST PARKING LOT
        safe_to_send_left = !hasTraffic(movingCars, nsegments-1, RIGHT) && !parkings[parkings.length-2].rlight;
        if (safe_to_send_left) {
            parkings[parkings.length-1].setLeftLight(true);
        }

        // we now make sure no oppossing lights are both on at the same segment 
        for (int i = 0; i != nsegments-1; ++i) {
            // CHANGE TO TURN OFF ONE WITH MOST ACCUMULATED PENALTY RATHER THAN JUST ARBITRARY
            if (parkings[i].rlight && parkings[i+1].llight) {
                parkings[i+1].setLeftLight(false);
            }

        }
        for (int i = 1; i != nsegments; ++i) {
            // CHANGE TO TURN OFF ONE WITH MOST ACCUMULATED PENALTY RATHER THAN JUST ARBITRARY
            if (parkings[i].llight && parkings[i-1].rlight) {
                parkings[i-1].setRightLight(false);
            }
        }

        for (int i = 0; i != parkings.length; i++) {
            strategy[0][i] = parkings[i].llight;
            strategy[1][i] = parkings[i].rlight;
        }
        return strategy;
    }


    private Parking[] generateParkingLots(oneway.sim.Parking[] left, oneway.sim.Parking[] right, boolean[] llights, boolean[] rlights) {
        int numParkings = capacity.length;
        Parking[] parkingLots = new Parking[numParkings];
        // leftmost parking lot
        parkingLots[0] = new Parking(capacity[0], left[0], right[0], true, rlights[0]);
        for (int i=1; i!= numParkings-1; i++) {
            parkingLots[i] = new Parking(capacity[i], left[i], right[i], llights[i-1], rlights[i]); 
        }
        // rightmost parking lot
        parkingLots[numParkings-1] = new Parking(capacity[numParkings-1], left[numParkings-1], right[numParkings-1], llights[numParkings-2], true);
        return parkingLots;
    }


    // check if the segment has traffic
    private int countTraffic(MovingCar[] cars, int seg, int dir) {
        int count = 0;
        for (MovingCar car : cars) {
            if (car.segment == seg && car.dir == dir)
                count++;
        }
        return count;
    }


    // check if the segment has traffic
    private boolean hasTraffic(MovingCar[] cars, int seg, int dir) {
        for (MovingCar car : cars) {
            if (car.segment == seg && car.dir == dir) {
                if (car.dir == -1 && car.block == 0) {
                }
                else if (car.dir == 1 && car.block == nblocks[seg]-1) {
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }

    private int nsegments;
    private int[] nblocks;
    private int[] capacity;
}
