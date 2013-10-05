package oneway.g3;

public class MyParking extends PlaceNode
{

	public add(MyCar m, int dir){
		if (dir==-1) left.add(m);
		if (dir==1) right.add(m);
		capacity++;
	}
	public remove(int dir){
		if (dir==-1) left.remove();
		if (dir==1) right.remove();
	}
    private Queue<MyCar> left;
	private Queue<MyCar> right;
	private int capacity;
}

