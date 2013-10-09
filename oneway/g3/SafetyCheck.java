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
            
            Road left = (Road)p.left;
            Road right = (Road)p.right;
            int capacity = p.getCapacity();
            int load = p.load();
            assert(load <= capacity);
            // infer next direction from current flow
            int nextdir = 0;
            // time in the future that one road is cleared
            int lastCarl = 0;
            int lastCarr = 0;
            int incomingLoad = 0;
            // minnimal load that has to be absorbed by parking lot
            int absorb = 0;
            // load that can be sent out
            int react = 0;
            Car c;
            RoadInfo ri;
            for (int i = 0; i < left.getLength() || i < right.getLength(); i++) {
                if (i < left.getLength()) {
                    c = left.getCar(-1, i);
                    if (c != null && c.dir == 1) {
                        lastCarl = i;
                        incomingLoad++;
                    }
                }
                if (i < right.getLength()) {
                    c = right.getCar(1, i);
                    if (c != null && c.dir == -1) {
                        lastCarr = i;
                        incomingLoad++;
                    }
                }
            }
            // no car in
            if (lastCarl == 0 && lastCarr == 0)
                continue;
            // one direction
            if (lastCarl == 0) {
                if (incomingLoad + load <= capacity)
                    continue;
                else {
                    react = incomingLoad - load - capacity;
                    ri = next.get(left);
                    if (ri.dir == 1)
                        return false;
                    ri.dir = -1;
                    ri.load += react;
                }
            }
            if (lastCarr == 0) {
                if (incomingLoad + load <= capacity)
                    continue;
                else {
                    react = incomingLoad - load - capacity;
                    ri = next.get(right);
                    if (ri.dir == -1)
                        return false;
                    ri.dir = 1;
                    ri.load += react;
                }
            }
            // both direction coming
            if (lastCarl != 0 && lastCarr != 0) {
                if (incomingLoad + load <= capacity)
                    continue;
                // can swap
                absorb = incomingLoad;
                if (lastCarl == lastCarr) {
                    // send one to the right
                    ri = next.get(right);
                    if (ri.dir == 0 || ri.dir == 1) {
                        ri.dir = 1;
                        ri.load += 1;
                        absorb--;
                        if (absorb + load <= capacity)
                            continue;
                    }
                    // send one to left
                    ri = next.get(left);
                    if (ri.dir == 0 || ri.dir == 1) {
                        ri.dir = -1;
                        ri.load += 1;
                        absorb--;
                        if (absorb + load <= capacity)
                            continue;
                    }
                    return false;
                }
                // first absorb cars when both sides are coming in
                absorb = 0;
                if (lastCarl < lastCarr) {
                    for (int i = 0; i < lastCarl; i++) {
                        c = left.getCar(-1, i);
                        if (c != null && c.dir == 1) {
                            absorb++;
                        }
                        c = right.getCar(1, i);
                        if (c != null && c.dir == -1) {
                            absorb++;
                        }
                    }
                    // some more cars on the right cannot be send right as soon as they come
                    c = right.getCar(1, lastCarl);
                    if (c != null && c.dir == -1) {
                        absorb++;
                    }
                    c = right.getCar(1, lastCarl+1);
                    if (c != null && c.dir == -1) {
                        absorb++;
                    }
                    if (absorb + load > capacity)
                        return false;
                    react = incomingLoad - absorb;
                    ri = next.get(left);
                    if (ri.dir == 1)
                        return false;
                    ri.dir = -1;
                    ri.load += react;
                }
                if (lastCarl > lastCarr) {
                    for (int i = 0; i < lastCarr; i++) {
                        c = left.getCar(-1, i);
                        if (c != null && c.dir == 1) {
                            absorb++;
                        }
                        c = right.getCar(1, i);
                        if (c != null && c.dir == -1) {
                            absorb++;
                        }
                    }
                    c = left.getCar(1, lastCarr);
                    if (c != null && c.dir == 1) {
                        absorb++;
                    }
                    c = left.getCar(1, lastCarr+1);
                    if (c != null && c.dir == 1) {
                        absorb++;
                    }
                    if (absorb + load > capacity)
                        return false;
                    react = incomingLoad - absorb;
                    ri = next.get(right);
                    if (ri.dir == -1)
                        return false;
                    ri.dir = 1;
                    ri.load += react;
                }
            }
        }
        return true;
    }
}