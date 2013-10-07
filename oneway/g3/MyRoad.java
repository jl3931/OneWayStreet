package oneway.g3;

//one object of MyRoad represents one segment
public class MyRoad extends PlaceNode
{
  private int length; //length of this segment
	private MyCar[] cars;
	private int segment; //segment id

	public MyRoad(int l, int s, PlaceNode leftNode, PlaceNode rightNode) {
		this.length = l;
		this.segment = s;
		this.setLeft(leftNode);
		this.setRight(rightNode);
		cars = new MyCar[l];
		for (int i=0; i<l; i++)
			cars[i] = null;
	}

	// moves cars along the road
	public void step(int d) {
		for (int i=1; i<length; i++) {
			if (d == 1) {
				cars[length-i] = cars[length-i-1];
			}
			if ( d== -1) {
				cars[i-1] = cars[i];
			}
		}
	}

}

