package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;

public class CircleCommand extends PaintCommand {
	private Point centre;
	private int radius;
	
	public CircleCommand(Point centre, int radius){
		this.centre = centre;
		this.radius = radius;
	}
	public Point getCentre() { return centre; }
	public void setCentre(Point centre) { 
		this.centre = centre; 
		this.setChanged();
		this.notifyObservers();
	}
	public int getRadius() { return radius; }
	public void setRadius(int radius) { 
		this.radius = radius; 
		this.setChanged();
		this.notifyObservers();
	}
	public void execute(GraphicsContext g){
		int x = this.getCentre().x;
		int y = this.getCentre().y;
		int radius = this.getRadius();
		if(this.isFill()){
			g.setFill(this.getColor());
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g.setStroke(this.getColor());
			g.strokeOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}
	
	/**
	 * Returns all the save information described in paintSaveFileFormat.txt
	 * The string returned is also formatted as described in paintSaveFileFormat.txt
	 */
	@Override
	public String getAllSaveInfo() {
		String circleInfo = "Circle\n";
		
		//adds basic shape information (rgb and fill) to the return string
		String basicShapeInfo = this.getBasicSaveInfo();
		circleInfo += basicShapeInfo;
		
		//adds center point to string
		String center = this.getCentre().getString();
		circleInfo += "\tcenter:" + center + "\n";
		
		//adds radius to string
		int radius = this.getRadius();
		circleInfo += "\tradius:" + radius + "\n";
		
		//end string and return
		circleInfo += "End Circle";
		return circleInfo;
	}
}
