package oneway.g3;
import java.util.LinkedList;

public class Simulator {
    private LinkedList<PlaceNode> system;
    private LinkedList<Parking> parkings;
    private LinkedList<Road> roads;
    private static int fullLength;
    private int nsegments;
    private int[] nblocks;
    private int[] capacity;
    private static int currentTime;

    public Simulator(int nsegments, int[] nblocks, int[] capacity) {

        this.nsegments = nsegments;
        this.nblocks = nblocks.clone();
        this.capacity = capacity.clone();
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
    
    public static int getFullLength() {
        return fullLength;
    }

    public static int getCurrentTime() {
        return currentTime;
    }

    public void update(int[] carsOnRoad, int[] leftq, int[] rightq) {        
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
        parkings.get(0).setLight(false, llights[0]);
        parkings.get(nsegments).setLight(rlights[nsegments-1], false);
        for (int i = 1; i < nsegments; i++) {
            parkings.get(i).setLight(rlights[i-1], llights[i]);
        }
        // potential new car - we assume that at least one car will come
        Car c;
        c = new Car(1, currentTime);
        parkings.get(0).add(c);
        c = new Car(-1, currentTime);
        parkings.get(nsegments).add(c);
        boolean safe = true;
        // first let all cars in road move
        for (Road r : roads)
            safe &= r.step();
        // then let cars in parking move
        for (Parking p : parkings)
            safe &= p.step();
        return safe;
    }

    public boolean safetyCheck(boolean[] llights, boolean[] rlights) {
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
        
        if (!oneStep(llights, rlights))
            return false;
        SafetyCheck sc = new SafetyCheck();
        if (!sc.check(system, parkings, roads))
            return false;
        
        // restore
        system = oldSystem;
        parkings = oldParkings;
        roads = oldRoads;
        return true;
    }
}

