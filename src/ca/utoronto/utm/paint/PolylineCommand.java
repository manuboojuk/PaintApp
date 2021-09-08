package ca.utoronto.utm.paint;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;

public class PolylineCommand extends PaintCommand {
	private Point nonAnchoredPoint;
	private ArrayList<Point> anchoredPoints = new ArrayList<Point>();
	private boolean isPolylineFinished = false; 
	
	public boolean isPolylineFinished() {
		return this.isPolylineFinished;
	}
	
	public void setPolyLineFinished(boolean b) {
		this.isPolylineFinished = b;
	}
	
	public Point getNonAnchoredPoint() { 
		return this.nonAnchoredPoint; 
	}
	
	public void addNonAnchoredPoint(Point p) { 
		this.nonAnchoredPoint = p;
		this.setChanged();
		this.notifyObservers();
	}
	
	public ArrayList<Point> getAnchoredPoints() {
		return this.anchoredPoints;
	}
	
	public void addAnchoredPoint(Point p) {
		this.anchoredPoints.add(p);
	}
	
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> anchoredPoints = this.getAnchoredPoints();
		int anchoredPointsSize = this.anchoredPoints.size();
		g.setStroke(this.getColor());
		
		//new lists to hold x and y coordinates, made to pass to the polyline constructor
		double x[] = new double [anchoredPoints.size() + 1];
		double y[] = new double [anchoredPoints.size() + 1];
		
		//put all x and y coords of anchor points into their respective lists
		for (int i = 0; i < anchoredPoints.size(); i++) {
			x[i] = anchoredPoints.get(i).x;
			y[i] = anchoredPoints.get(i).y;
		}
		
		//add the second point of the current line, (its the point that moves with the mouse)
		if (this.getNonAnchoredPoint() != null) {
			x[anchoredPoints.size()] = this.getNonAnchoredPoint().x;
			y[anchoredPoints.size()] = this.getNonAnchoredPoint().y;
			anchoredPointsSize += 1;
		}
		
		g.strokePolyline(x, y, anchoredPointsSize);
		
	}

	/**
	 * Returns all the save information described in paintSaveFileFormat.txt
	 * The string returned is also formatted as described in paintSaveFileFormat.txt
	 */
	@Override
	public String getAllSaveInfo() {
		String polylineInfo = "Polyline\n";
		
		//adds basic shape information (rgb and fill) to the return string
		String basicShapeInfo = this.getBasicSaveInfo();
		polylineInfo += basicShapeInfo;
		
		//loops through each anchored point that this polyline has and adds it to the return string
		ArrayList<Point> anchoredPoints = this.getAnchoredPoints();
		polylineInfo += "\tpoints\n";
		for (Point point: anchoredPoints) {
			String pointString = point.getString();
			polylineInfo += "\t\tpoint:" + pointString + "\n";
		}
		polylineInfo += "\tend points\n";
		
		//end string and return
		polylineInfo += "End Polyline";
		return polylineInfo;
	}
}
