package ca.utoronto.utm.paint;
import java.util.Observable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class PaintCommand extends Observable {
	private Color color;
	private boolean fill;
	
	PaintCommand(){
		// Pick a random color for this
		int r = (int)(Math.random()*256);
		int g = (int)(Math.random()*256);
		int b= (int)(Math.random()*256);
		this.color = Color.rgb(r, g, b);
		
		this.fill = (1==(int)(Math.random()*2));
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public boolean isFill() {
		return fill;
	}
	public void setFill(boolean fill) {
		this.fill = fill;
	}
	public String toString(){
		double r = this.color.getRed();
		double g = this.color.getGreen();
		double b = this.color.getBlue();

		String s = "";
		s+="\tcolor:"+r+","+g+","+b+"\n";
		s+="\tfilled:"+this.fill+"\n";
		return s;
	}
	/**
	 * Returns the basic information for this shape; only meant to be used
	 * by subclasses for their "getAllSaveInfo" method
	 * 
	 * @return string of the basic information for this shape formatted as
	 * described in paintSaveFileFormat.txt
	 */
	protected String getBasicSaveInfo() {
		String shapeInfo = "";
		
		//gets RGB color and adds to string
		double r = this.getColor().getRed();
		double g = this.getColor().getGreen();
		double b = this.getColor().getBlue();
		shapeInfo += "\tcolor:" + (int) (r * 255) + "," + (int) (g * 255) + "," + (int) (b * 255) + "\n";
				
		//gets whether this squiggle is filled or not and adds to string
		boolean isFill = this.isFill();
		shapeInfo += "\tfilled:" + isFill + "\n";
		
		return shapeInfo;
	}
	
	public abstract String getAllSaveInfo();
	public abstract void execute(GraphicsContext g);
}
