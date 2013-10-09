package oneway.g3;
import java.util.*;

public class SafetyCheck {
    class RoadInfo {
        public int dir;
        public int load;
        public RoadInfo(int dir, int load) {
            this.dir = dir;
            this.load = load;
        }
    }
    public boolean check(LinkedList<PlaceNode> system,
                         LinkedList<Parking> parkings,
                         LinkedList<Road> roads) {
        /*
          current: the current flow of the road
          next: the reaction flow to make sure that parking lot will not be overflown
        */
        HashMap<PlaceNode, RoadInfo> current = new HashMap<PlaceNode, RoadInfo>();
        HashMap<PlaceNode, RoadInfo> next = new HashMap<PlaceNode, RoadInfo>();
        for (Road r : roads) {
            int dir = r.getDir();
            assert(dir != Road.CRASH);
            int load = r.load();
            RoadInfo ri = new RoadInfo(dir, load);
            current.put(r, ri);
            ri = new RoadInfo(0, 0);
            next.put(r, ri);
        }
        LinkedList<PlaceNode> toVisit = new LinkedList<PlaceNode>(parkings);

        // The big loop to solve things
        while (!toVisit.isEmpty()){
            Parking p = (Parking)toVisit.poll();
            // skip end parking lot
            if (p.left == null || p.right == null)
                continue;

            PlaceNode left = p.left;
            PlaceNode right = p.right;
            int capacity = p.getCapacity();
            int load = p.load();
            assert(load <= capacity);
            // computer load
            RoadInfo ri;
            ri = current.get(left);
            if (ri.dir == 1)
                load += ri.load;
            ri = next.get(left);
            if (ri.dir == 1)
                load += ri.load;
            else if (ri.dir == -1)
                load -= ri.load;
            ri = current.get(right);
            if (ri.dir == -1)
                load += ri.load;
            ri = next.get(right);
            if (ri.dir == -1)
                load += ri.load;
            else if (ri.dir == 1)
                load -= ri.load;

            // case one - capacity can hold
            if (load <= capacity)
                continue;

            // stop for now
            return false;
            /*
            // case two - cannot sent it anywhere
            if (next.get(left).dir == 1 && next.get(right).dir == -1)
                return false;

            // infer next direction from current flow
            int nextdir = 0;
            // time in the future that one road is cleared
            int lastCar = 0;

            // case four - only one direction can go
            if (next.get(left).dir == 1) {
                ri = next.get(right);
                ri.dir = 1;
                assert(ri.load < (capacity - load));
                ri.load = capacity - load;
                toVisit.add(right.right);
                continue;
            }
            if (next.get(right).dir == -1) {
                ri = next.get(left);
                ri.dir = -1;
                assert(ri.load < (capacity - load));
                ri.load = capacity - load;
                toVisit.add(right.right);
                continue;
                }
            */
        }
        return true;
    }
}