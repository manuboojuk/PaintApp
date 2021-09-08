package ca.utoronto.utm.paint;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart = Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd = Pattern.compile("^EndPaintSaveFile$");
	
	//general patterns, can be used by more than one shape
	private Pattern pColor = Pattern.compile("^color:-?[0-9]+,-?[0-9]+,-?[0-9]+$");
	private Pattern pFilled = Pattern.compile("^filled:((true)|(false))$");
	private Pattern pPoints = Pattern.compile("^points$");
	private Pattern pPoint = Pattern.compile("^point:\\([0-9]+,[0-9]+\\)$");
	private Pattern pEndPoints = Pattern.compile("^endpoints$");

	//circle specific patterns
	private Pattern pCircleStart = Pattern.compile("^Circle$");
	private Pattern pCenter = Pattern.compile("^center:\\([0-9]+,[0-9]+\\)$");
	private Pattern pRadius = Pattern.compile("^radius:[0-9]+$");
	private Pattern pCircleEnd = Pattern.compile("^EndCircle$");
	
	//rectangle specific patterns
	private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
	private Pattern pP1 = Pattern.compile("^p1:\\([0-9]+,[0-9]+\\)$");
	private Pattern pP2 = Pattern.compile("^p2:\\([0-9]+,[0-9]+\\)$");
	private Pattern pRectangleEnd = Pattern.compile("^EndRectangle$");
	
	//squiggle specific patterns
	private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");
	
	//polyline specific patterns
	private Pattern pPolylineStart = Pattern.compile("^Polyline$");
	private Pattern pPolylineEnd = Pattern.compile("^EndPolyline$");
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		//general shape attributes, can be used by more than one shape
		Color color = null;
		boolean filled = false;
		ArrayList<Point> points = new ArrayList<Point>();
		
		//circle specific attributes
		CircleCommand circleCommand = null; 
		int radius = 0;
		Point center = new Point(0, 0);
		
		//rectangle specific attributes
		RectangleCommand rectangleCommand = null;
		Point p1 = new Point(0, 0);
		Point p2 = new Point(0, 0);
		
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;
	
		try {	
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				
				//delete empty line
				if (l.matches("^$")) {
					continue;
				}
				
				//delete whitespaces
				l = l.replaceAll("\\s+", "");
				l = l.replaceAll("\\t+", "");
								
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					
					//looking for start of file
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
						
					//checks if line matches the start of new object line or 
					//matches
					case 1: 
						
						//matches start of circle object
						m=pCircleStart.matcher(l);
						if(m.matches()){
							state=2; 
							break;
						}
						
						//matches start of rectangle object
						m=pRectangleStart.matcher(l);
						if(m.matches()){
							state=7; 
							break;
						}
						
						//matches start of squiggle object
						m=pSquiggleStart.matcher(l);
						if(m.matches()){
							state=12; 
							break;
						}
						
						//matches start of polyline object
						m=pPolylineStart.matcher(l);
						if(m.matches()){
							state=18; 
							break;
						}
						
						//matches end of file
						m=pFileEnd.matcher(l);
						if (m.matches()) {
							return true;
						}
						return false;
					
					//CIRCLE CASES, CASE 2-7
					//checks if line matches format for color line
					case 2:
						m=pColor.matcher(l);
						if (m.matches()) {
							
							//gets the integer values from the string 
							String[] rgbList = l.replace("color:", "").split(",");
							int r = Integer.parseInt(rgbList[0]);
							int g = Integer.parseInt(rgbList[1]);
							int b = Integer.parseInt(rgbList[2]);
							
							//check if the integer rgb values are in the right range
							if (0 <= r && r <= 255 && 0 <= g && g <= 255 && 0 <= b && b <= 255) {
								color = Color.rgb(r, g, b);
							}
							
							else {
								return false;
							}
							
							state=3;
							break;
						}
						return false;
					
					//checks if line matches format for filled line
					case 3:
						m=pFilled.matcher(l);

						if (m.matches()) {
							
							//strips un-needed chars and gets boolean value from the string
							filled = Boolean.parseBoolean(l.replace("filled:", ""));
							state=4;
							break;
						}
						return false;
					
					//checks if line matches format for the center of circle line
					case 4:
						m=pCenter.matcher(l);
						if (m.matches()) {
							
							//stips the un-needed chars and gets the two integer values from the string
							String[] pointCoords = l.replace("center:", "").replace("(", "").replace(")", "").split(",");
							int tempCenterX = Integer.parseInt(pointCoords[0]);
							int tempCenterY = Integer.parseInt(pointCoords[1]);
							
							//checks if the points are in the right range
							if (0 <= tempCenterX && tempCenterX <= 500 && 0 <= tempCenterY && tempCenterY <= 500) {
								center.x = tempCenterX;
								center.y = tempCenterY;
							} 
							
							else {
								return false;
							}
							
							state=5;
							break;
						}
						return false;
					
					//checks if line matches format for the radius of circle line
					case 5:
						m=pRadius.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the integer value from the string
							int tempRadius = Integer.parseInt(l.replace("radius:", ""));
							
							//checks if radius is in correct range
							if (tempRadius > 0) {radius = tempRadius;}
							else {return false;}
							
							state=6;
							break;
						}
						return false;
						
					//checks if line matches format of the end of circle line
					case 6:
						m=pCircleEnd.matcher(l);
						if (m.matches()) {
							
							//set circleCommand's attributes and pass it to paintModel
							circleCommand = new CircleCommand(center, radius);
							circleCommand.setFill(filled);
							circleCommand.setColor(color);
							
							this.paintModel.addCommand(circleCommand);
							
							state=1;
							break;
						}
						return false;
					
					//RECTANGLE CASES, CASE 7-12
					//checks if line matches format for the color line
					case 7:
						m=pColor.matcher(l);
						if (m.matches()) {
							
							//gets the integer values from the string 
							String[] rgbList = l.replace("color:", "").split(",");
							int r = Integer.parseInt(rgbList[0]);
							int g = Integer.parseInt(rgbList[1]);
							int b = Integer.parseInt(rgbList[2]);
							
							//check if the integer rgb values are in the right range
							if (0 <= r && r <= 255 && 0 <= g && g <= 255 && 0 <= b && b <= 255) {
								color = Color.rgb(r, g, b);
							}
							
							else {
								return false;
							}
							
							state=8;
							break;
						}
						return false;
					
					//checks if line matches format for the filled line
					case 8:
						m=pFilled.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the boolean value from the string
							filled = Boolean.parseBoolean(l.replace("filled:", ""));
							state=9;
							break;
						}
						return false;
					
					//checks if line matches format for the point 1 line
					case 9:
						m=pP1.matcher(l);
						if (m.matches()) {

							//stips the un-needed chars and gets the two integer values from the string
							String[] pointCoords = l.replace("p1:", "").replace("(", "").replace(")", "").split(",");
							p1.x = Integer.parseInt(pointCoords[0]);
							p1.y = Integer.parseInt(pointCoords[1]);

							state=10;
							break;
						}
						return false;
					
					//checks if line matches format for the point 2 line
					case 10:
						m=pP2.matcher(l);
						if (m.matches()) {
							
							//stips the un-needed chars and gets the two integer values from the string
							String[] pointCoords = l.replace("p2:", "").replace("(", "").replace(")", "").split(",");
							p2.x = Integer.parseInt(pointCoords[0]);
							p2.y = Integer.parseInt(pointCoords[1]);
							
							state=11;
							break;
						}
						return false;
						
					//checks if line matches rectangle end line
					case 11:
						m=pRectangleEnd.matcher(l);
						if (m.matches()) {
							
							//sets rectangle command's attributes and passes it to the paintModel
							rectangleCommand = new RectangleCommand(p1, p2);
							rectangleCommand.setFill(filled);
							rectangleCommand.setColor(color);
							this.paintModel.addCommand(rectangleCommand);
							
							state=1;
							break;
						}
						return false;
					
					//SQUIGGLE CASE 12-18
					//checks if line matches format for the color line
					case 12:
						m=pColor.matcher(l);
						if (m.matches()) {
							
							//gets the integer values from the string 
							String[] rgbList = l.replace("color:", "").split(",");
							int r = Integer.parseInt(rgbList[0]);
							int g = Integer.parseInt(rgbList[1]);
							int b = Integer.parseInt(rgbList[2]);
							
							//checks if the integer rgb values are in the right range
							if (0 <= r && r <= 255 && 0 <= g && g <= 255 && 0 <= b && b <= 255) {
								color = Color.rgb(r, g, b);
							}
							
							else {
								return false;
							}
							
							state=13;
							break;
						}
						return false;
					
					//checks if line matches format for the filled line
					case 13:
						m=pFilled.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the boolean value from the string
							filled = Boolean.parseBoolean(l.replace("filled:", ""));
							state=14;
							break;
						}
						return false;
						
					//checks if line matches the header for the points section; matches the points line
					case 14:
						m=pPoints.matcher(l);
						if (m.matches()) {
							state=15;
							break;
						}
						return false;
						
					//checks if line matches the first point under the points line
					//need this case because we need at least 1 point	
					case 15:
						m=pPoint.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the integer coordinate values from the string
							String[] pointCoords = l.replace("point:", "").replace("(", "").replace(")", "").split(",");
							int x = Integer.parseInt(pointCoords[0]);
							int y = Integer.parseInt(pointCoords[1]);
							
							//add point to arraylist
							points.add(new Point(x, y));
							
							state=16;
							break;
						}
						return false;
					
					//checks if line matches the first point under the points line and also matches
					//the end of the points section i.e matches the endpoints line
					case 16:
						m=pPoint.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the integer coordinate values from the string
							String[] pointCoords = l.replace("point:", "").replace("(", "").replace(")", "").split(",");
							int x = Integer.parseInt(pointCoords[0]);
							int y = Integer.parseInt(pointCoords[1]);
							
							//add point to arraylist
							points.add(new Point(x, y));
							
							state=16;
							break;
						}
						
						//checks if line matches the endpoints line
						m=pEndPoints.matcher(l);
						if (m.matches()) {
							state=17;
							break;
						}
						return false;
					
					//checks if line matches the squiggle end line
					case 17:
						m=pSquiggleEnd.matcher(l);
						if (m.matches()){
							squiggleCommand = new SquiggleCommand();

							//adds all the points to squiggle command
							for (Point point: points) {
								squiggleCommand.add(point);
							}
							
							//sets rectangle command's attributes and passes it to the paintModel
							squiggleCommand.setFill(filled);
							squiggleCommand.setColor(color);
							this.paintModel.addCommand(squiggleCommand);
							
							//resets points arraylist so it can be used again
							points = new ArrayList<Point>();
							
							state=1;
							break;
						}
						return false;
					
					//POLYLINE CASE 18-23
					case 18:
						m=pColor.matcher(l);
						if (m.matches()) {
							
							//gets the integer values from the string 
							String[] rgbList = l.replace("color:", "").split(",");
							int r = Integer.parseInt(rgbList[0]);
							int g = Integer.parseInt(rgbList[1]);
							int b = Integer.parseInt(rgbList[2]);
							
							//checks if the integer rgb values are in the right range
							if (0 <= r && r <= 255 && 0 <= g && g <= 255 && 0 <= b && b <= 255) {
								color = Color.rgb(r, g, b);
							}
							
							else {
								return false;						
							}
							
							state=19;
							break;
						}
						return false;
					
					//checks if line matches format for the filled line
					case 19:
						m=pFilled.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the boolean value from the string
							filled = Boolean.parseBoolean(l.replace("filled:", ""));
							state=20;
							break;
						}
						return false;
					
					//checks if line matches the header for the points section; matches the points line
					case 20:
						m=pPoints.matcher(l);
						if (m.matches()) {
							state=21;
							break;
						}
						return false;
						
					//checks if line matches the first point under the points line
					//need this case because we need at least 1 point
					case 21:
						m=pPoint.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the integer coordinate values from the string
							String[] pointCoords = l.replace("point:", "").replace("(", "").replace(")", "").split(",");
							int x = Integer.parseInt(pointCoords[0]);
							int y = Integer.parseInt(pointCoords[1]);
							
							//add point to arraylist
							points.add(new Point(x, y));
							
							state=22;
							break;
						}
						return false;
					
					//checks if line matches the first point under the points line and also matches
					//the end of the points section i.e matches the endpoints line	
					case 22:
						m=pPoint.matcher(l);
						if (m.matches()) {
							
							//strips un-needed chars and gets the integer coordinate values from the string
							String[] pointCoords = l.replace("point:", "").replace("(", "").replace(")", "").split(",");
							int x = Integer.parseInt(pointCoords[0]);
							int y = Integer.parseInt(pointCoords[1]);
						
							//add point to arraylist
							points.add(new Point(x, y));
							
							state=22;
							break;
						}
						
						//matches the endpoints line
						m=pEndPoints.matcher(l);
						if (m.matches()) {
							state=23;
							break;
						}
						return false;
					
					//checks if line matches the end polyline line
					case 23:
						m=pPolylineEnd.matcher(l);
						if (m.matches()){
							
							//adds in the points
							polylineCommand = new PolylineCommand();
							for (Point point: points) {
								polylineCommand.addAnchoredPoint(point);
							}
							
							//sets polyline command's attributes and passes it to the paintModel
							polylineCommand.setFill(filled);
							polylineCommand.setColor(color);
							this.paintModel.addCommand(polylineCommand);

							//resets points arraylist so it can be used again
							points = new ArrayList<Point>();

							//set polyline back to null so it can be used again
							polylineCommand = null;
							state=1;
							break;
						}
						return false;
				}
			}
		}  catch (Exception e){
			
		}
		return true;
	}
	
}
