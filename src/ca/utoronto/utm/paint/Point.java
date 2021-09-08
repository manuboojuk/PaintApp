package ca.utoronto.utm.paint;

public class Point {
	int x, y; // Available to our package
	Point(int x, int y){
		this.x=x; this.y=y;
	}
	/**
	 * returns the string representation of this point in 
	 * the format (x,y)
	 * 
	 * @return the string representation of this point
	 */
	public String getString() {
		return "(" + this.x + "," + this.y + ")";
	}
}
