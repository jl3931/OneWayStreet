package oneway.g3;

//abstract class
//superclass of Road, Parking
public abstract class PlaceNode
{
	PlaceNode left;
	PlaceNode right;

    public PlaceNode(PlaceNode left) {
        this(left, null);
    }

    public PlaceNode(PlaceNode left, PlaceNode right) {
        this.left = left;
        this.right = right;
    }

    public void setRight(PlaceNode right) {
        this.right = right;
    }

    public PlaceNode getLeft() {
        return left;
    }

    public PlaceNode getRight() {
        return right;
    }
    
    public abstract boolean step();
 
    public abstract void add(Car c);

    public abstract int load();
    
    public abstract void pop(int dir);

    public Car getCar(int dir, int index) {
        assert(false);
        return null;
    }
}

