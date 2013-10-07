package oneway.g3;

public class Road extends PlaceNode
{
    private int length;
	private Car[] cars;
	private int segmentId;

	public Road(PlaceNode left, int length, int segmentId) {
        super(left);
		this.length = length;
		this.segmentId = segmentId;
		cars = new Car[length];
		for (int i = 0; i < length; i++)
			cars[i] = null;
	}

    public void add(Car c) {
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

    public void pop(int dir) {
        int index = (dir == 1)? 0: (length-1);
        assert(cars[index] != null);
        cars[index] = null;
    }

	// moves cars along the road, return false if crash
	public boolean step() {
        // check that every car in this road has the same dir
        int dir = 0;
		for (int i = 1; i < length; i++) {
            if (cars[i] != null) {
                //general dir and car dir are not the same
                if (dir * cars[i].dir == -1)
                    return false;
                //overwrite dir in case it's 0
                dir = cars[i].dir;                
            }
		}
        // move into parking lot, capacity check later
        if (dir == 1 && cars[length-1] != null) {
            Car c = cars[length-1];
            right.add(c);
            c.moveForward();
            cars[length-1] = null;
        }
        if (dir == -1 && cars[0] != null) {
            Car c = cars[0];
            right.add(c);
            c.moveForward();
            cars[0] = null;
        }
        // move within the road, no need for crash check
		for (int i = 1; i < length; i++) {
            if (cars[i] != null) {
                cars[i].moveForward();
                cars[i+dir] = cars[i];
                cars[i] = null;
            }
        }
        return true;
	}
}

