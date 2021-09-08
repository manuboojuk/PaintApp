package ca.utoronto.utm.paint;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy{
	PolylineManipulatorStrategy(PaintModel paintModel) {
		super(paintModel);
	}
	
	PolylineCommand polylineCommand;
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		//if there isnt already an "active" polyline
		if (this.polylineCommand == null || this.polylineCommand.isPolylineFinished() == true) {
			this.polylineCommand = new PolylineCommand();
			this.addCommand(polylineCommand);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseButton.PRIMARY) {
			this.polylineCommand.addAnchoredPoint(new Point((int) e.getX(), (int) e.getY()));
		}
		
		//if secondary button is pressed
		else {
			this.polylineCommand.addAnchoredPoint(new Point((int) e.getX(), (int) e.getY()));	
			this.polylineCommand.setPolyLineFinished(true);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
		//if there is already a polyline made, check to avoid adding anchor point to null object
		if (this.polylineCommand != null && this.polylineCommand.isPolylineFinished() != true) {
			this.polylineCommand.addNonAnchoredPoint(new Point((int) e.getX(), (int) e.getY()));
		}
	}
}
