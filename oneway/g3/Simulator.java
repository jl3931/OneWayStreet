package oneway.g3;
import java.util.LinkedList;
import oneway.sim.MovingCar;
public class Simulator {
    private LinkedList<PlaceNode> system;
    private LinkedList<Parking> parkings;
    private LinkedList<Road> roads;
    private static int fullLength;
    private int nsegments;
    private int[] nblocks;
    private int[] capacity;
    private static int currentTime;
    public static double score;

    public Simulator(int nsegments, int[] nblocks, int[] capacity) {

        this.nsegments = nsegments;
        this.nblocks = nblocks.clone();
        this.capacity = capacity.clone();
        score = 0.0;
        currentTime = 0;
        fullLength = 0;
        for (int l : nblocks)
            fullLength += l;

        system = new LinkedList<PlaceNode>();
        parkings = new LinkedList<Parking>();
        roads = new LinkedList<Road>();

        PlaceNode left = null;
        PlaceNode current = null;
        for (int i = 0; i < nsegments; i++) {
            // build parking lot
            current = new Parking(left, capacity[i]);
            parkings.add((Parking)current);
            system.add(current);
            if (left != null)
                left.setRight(current);
            left = current;
            // build road
            current = new Road(left, nblocks[i], i);
            roads.add((Road)current);            
            system.add(current);
            left.setRight(current);
            left = current;
        }

        // last parking lot, its right is null
        current = new Parking(left, capacity[nsegments]);
        parkings.add((Parking)current);
        system.add(current);
        if (left != null)
            left.setRight(current);

        // check for correctness
        assert(system.size() == (2 * nsegments + 1));
        current = system.peek();
        int count = 0;
        while (current != null) {
            count++;
            current = current.getRight();
        }
        assert(count == (2 * nsegments + 1));
	}

    public double evaluatePenalty() {
        double penalty = 0.0;
        for (Road r : roads) {
            for (int i = 0; i < r.cars.length; i++) {
                System.out.println(r.cars.length);
                if (r.cars[i] != null) {
                    double T = (double) r.cars[i].getTime();
                    penalty += (T+(fullLength-r.cars[i].distCovered))*Math.log10(T+(fullLength-r.cars[i].distCovered)) - (fullLength)*Math.log10(fullLength); 
                }
            }
        }
        for (Parking p : parkings) {
            for (Car c : p.leftq) {
                double T = (double) c.getTime();
                penalty += (T+(fullLength-c.distCovered))*Math.log10(T+(fullLength-c.distCovered)) - (fullLength)*Math.log10(fullLength);
            }
            for (Car c : p.rightq) {
                double T = (double) c.getTime();
                penalty += (T+(fullLength-c.distCovered))*Math.log10(T+(fullLength-c.distCovered)) - (fullLength)*Math.log10(fullLength);
            }
        }
        System.out.println("Penalty is: " + penalty);
        System.out.println("Score is: " + (score + penalty));
        return penalty;
    }

    public Car[] getMovingCars() {
        LinkedList<Car> cars = new LinkedList<Car>();
        int i = 0;
        for (Road r : roads) {
            for (int j = 0; j < r.cars.length; j++) {
                if (r.cars[j] != null) {
                    r.cars[j].segment = i;
                    r.cars[j].block = j;
                    cars.add(r.cars[j]); 
                }
            }
            i++;
        }
        Car[] t =  new Car[cars.size()];
        i = 0;
        for (Car c : cars) {
            t[i] = c;
            System.out.println(c);
            i++;
        }
        return t;
    }

    public static void updateDeliveries(double T) {

        score += (T)*Math.log10(T) - (fullLength)*Math.log10(fullLength);
    }

    public static int getFullLength() {
        return fullLength;
    }

    public static int getCurrentTime() {
        return currentTime;
    }

    public int calculateDistance(int dir, int seg, int blck) {
        System.out.println("car at: " + seg + "  " + blck);
        int dist = 0;
        if (dir == 1) {
            for (int i = 0; i < seg; i++) {
                dist += roads.get(i).length;
            }
            System.out.println("dist: " + (dist + blck + 1));
            dist += blck; 
        }
        else {
            for (int i = seg; i < roads.size(); i++) {
                dist += roads.get(i).length;
            }
            System.out.println("dist: " + (dist - blck));
            dist += blck; 
        }
        return dist;
    }

    public void update(MovingCar[] movingCars, int[] leftq, int[] rightq) {        
        int[] carsOnRoad = new int[nsegments];
        for (MovingCar c : movingCars) {
            carsOnRoad[c.segment]++;
            // generate a car for each moving one
            Road road = roads.get(c.segment);
            road.cars[c.block] = new Car(c);
            road.cars[c.block].distCovered = calculateDistance(c.dir, c.segment, c.block);
        }
        currentTime++;
        // infer car coming info - there can be no car, 1 car or more than 1 cars
        // at most one extra car on road, and it must be at first block
        if (roads.get(0).load() != carsOnRoad[0])
            roads.get(0).pop(1);
        if (roads.get(nsegments - 1).load() != carsOnRoad[nsegments - 1])
            roads.get(nsegments - 1).pop(-1);
        // number of cars in queue have to match
        if (leftq[0] + rightq[0] < parkings.get(0).load())
            parkings.get(0).pop(1);
        while (leftq[0] + rightq[0] > parkings.get(0).load())
            parkings.get(0).add(new Car(1, currentTime));
        if (leftq[nsegments] + rightq[nsegments] < parkings.get(nsegments).load())
            parkings.get(nsegments).pop(-1);
        while (leftq[nsegments] + rightq[nsegments] > parkings.get(nsegments).load())
            parkings.get(nsegments).add(new Car(-1, currentTime));

        // consistency checks
        for (int i = 0; i < nsegments; i++)
            assert(carsOnRoad[i] == roads.get(i).load());
        for (int i = 0; i <= nsegments; i++)
            assert(leftq[i] + rightq[i] == parkings.get(i).load());
    }

    public boolean oneStep(boolean[] llights, boolean[] rlights) {
         // set lights
        parkings.get(0).setLight(false, rlights[0]);
        parkings.get(nsegments).setLight(llights[nsegments - 1], false);
        for (int i = 1; i <= nsegments - 1; i++) {
            parkings.get(i).setLight(llights[i - 1], rlights[i]);
        }
        // potential new car - we assume that at least one car will come
        Car c;
        // c = new Car(1, currentTime);
        // parkings.get(0).add(c);
        // c = new Car(-1, currentTime);
        // parkings.get(nsegments).add(c);
        boolean safe = true;
        // first let all cars in road move
        for (Road r : roads)
            safe &= r.step();
        // then let cars in parking move
        for (Parking p : parkings)
            safe &= p.step();
        if (!safe)
            System.out.println("crash");
        return safe;
    }

    public boolean safetyCheck(boolean[] llights, boolean[] rlights) {
        boolean safe = true;
        // make snapshots
        LinkedList<PlaceNode> oldSystem = new LinkedList<PlaceNode>();
        LinkedList<Parking> oldParkings = new LinkedList<Parking>();
        LinkedList<Road> oldRoads = new LinkedList<Road>();
        // deep copy
        PlaceNode left = null;
        PlaceNode current = null;
        for (int i = 0; i < nsegments; i++) {
            // build parking lot
            current = new Parking(left, parkings.get(i));
            oldParkings.add((Parking)current);
            oldSystem.add(current);
            if (left != null)
                left.setRight(current);
            left = current;
            // build road
            current = new Road(left, roads.get(i));
            oldRoads.add((Road)current);            
            oldSystem.add(current);
            left.setRight(current);
            left = current;
        }
        current = new Parking(left, parkings.get(nsegments));
        oldParkings.add((Parking)current);
        oldSystem.add(current);
        if (left != null)
            left.setRight(current);
        
        if (oneStep(llights, rlights)) {
            SafetyCheck sc = new SafetyCheck();
            if (!sc.check(system, parkings, roads))
                safe = false;
        }
        else
            safe = false;
        
        // restore
        system = oldSystem;
        parkings = oldParkings;
        roads = oldRoads;
        return safe;
    }
}

