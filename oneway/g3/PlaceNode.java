package oneway.g3;

import java.util.LinkedList;
//abstract class
//used to create MyRoad, MyParking
public abstract class PlaceNode
{
	private PlaceNode left;
	private PlaceNode right;

	public void setLeft(PlaceNode leftNode) {
		left = leftNode;
	}
	public void setRight(PlaceNode rightNode) {
		right = rightNode;
	}
}

