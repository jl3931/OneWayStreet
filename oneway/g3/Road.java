package oneway.g3;

public class Road extends PlaceNode
{
    public static final int CRASH = 2;
    private int length;
	private Car[] cars;
	private int segmentId;

    public Road(PlaceNode left, Road r) {
        super(left);
        length = r.length;
		segmentId = r.segmentId;
		cars = new Car[length];
		for (int i = 0; i < length; i++) {
            Car c = r.cars[i];
            if (c == null)
                cars[i] = null;
            else {
                Car newC = new Car(c);
                cars[i] = newC;
            }
        }
    }

	public Road(PlaceNode left, int length, int segmentId) {
        super(left);
		this.length = length;
		this.segmentId = segmentId;
		cars = new Car[length];
		for (int i = 0; i < length; i++)
			cars[i] = null;
	}

    public void add(Car c) {
        System.out.println("new car: " + c.dir);
        if (c.dir == 1) cars[0] = c;
        if (c.dir == -1) cars[length-1] = c;
    }

    public Car getCar(int dir, int index) {
        if (dir == -1)
            index = length - 1 - index;
        return cars[index];
    }
    
    public int load() {
        int load = 0;
        for (int i = 0; i < length; i++)
            if (cars[i] != null)
                load++;
        return load;
    }

    public int getLength() {
        return length;
    }

    public void pop(int dir) {
        int index = (dir == 1)? 0: (length-1);
        assert(cars[index] != null);
        cars[index] = null;
    }

    public int getDir() {
        int dir = 0;
		for (int i = 0; i < length; i++) {
            if (cars[i] != null) {
                //general dir and car dir are not the same
                if (dir * cars[i].dir == -1)
                    return CRASH;
                //overwrite dir in case it's 0
                dir = cars[i].dir;                
            }
		}
        return dir;
    }

	// moves cars along the road, return false if crash
	public boolean step() {
        // check that every car in this road has the same dir
        int dir = getDir();
        if (dir == CRASH)
            return false;
        System.out.println("Road step "+ dir);        
        // move into parking lot, capacity check later
        if (dir == 1 && cars[length-1] != null) {
            System.out.println("move into parking: 1");
            Car c = cars[length-1];
            right.add(c);
            c.moveForward();
            cars[length-1] = null;
        }
        if (dir == -1 && cars[0] != null) {
            System.out.println("move into parking: -1");
            Car c = cars[0];
            left.add(c);
            c.moveForward();
            cars[0] = null;
        }
        // move within the road, no need for crash check
        Car[] newCars = new Car[length];
		for (int i = 0; i < length; i++) {
            if (cars[i] != null) {
                cars[i].moveForward();
                int d = cars[i].dir;
                if ((i+d < 0) || (i+d >= length))
                    continue;
                newCars[i+d] = cars[i];
            }
        }
        cars = newCars;
        return true;
	}
}

