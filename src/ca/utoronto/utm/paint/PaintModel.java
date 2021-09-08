package ca.utoronto.utm.paint;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;

public class PaintModel extends Observable implements Observer {
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();
	
	public void save(PrintWriter writer) {
		
		//write the header to the file
		writer.println("Paint Save File Version 1.0");
		
		//loop through all commands in order from (first drawn to last drawn) and 
		//write their info to the file according to format in paintSaveFileFormat.txt
		for (PaintCommand command : this.commands) {
			writer.println(command.getAllSaveInfo());
		}
		
		//write the ending line to the file and close file
		writer.println("End Paint Save File");
		writer.close();
	}
	
	public void reset(){
		for(PaintCommand c: this.commands){
			c.deleteObserver(this);
		}
		this.commands.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
		command.addObserver(this);
		this.setChanged();
		this.notifyObservers();
	}
	
	public void executeAll(GraphicsContext g) {
		for(PaintCommand c: this.commands){
			c.execute(g);
		}
	}
	
	/**
	 * We Observe our model components, the PaintCommands
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers();
	}
}
