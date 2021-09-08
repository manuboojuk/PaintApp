package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class SquiggleCommand extends PaintCommand {
	private ArrayList<Point> points = new ArrayList<Point>();
	
	public void add(Point p){ 
		this.points.add(p); 
		this.setChanged();
		this.notifyObservers();
	}
	
	public ArrayList<Point> getPoints() { 
		return this.points; 
	}
		
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		for(int i = 0; i < points.size() - 1; i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i + 1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
	}
	
	/**
	 * Returns all the save information described in paintSaveFileFormat.txt
	 * The string returned is also formatted as described in paintSaveFileFormat.txt
	 */
	@Override
	public String getAllSaveInfo() {
		String squiggleInfo = "Squiggle\n";
		
		//adds basic shape information (rgb and fill) to the return string
		String basicShapeInfo = this.getBasicSaveInfo();
		squiggleInfo += basicShapeInfo;
		
		//loops through each point that this squiggle spans and adds it to the return string
		ArrayList<Point> points = this.getPoints();
		squiggleInfo += "\tpoints\n";
		for (Point point: points) {
			String pointString = point.getString();
			squiggleInfo += "\t\tpoint:" + pointString + "\n";
		}
		squiggleInfo += "\tend points\n";
		
		//end string and return
		squiggleInfo += "End Squiggle";
		return squiggleInfo;
	}
}
