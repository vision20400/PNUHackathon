package test;

import javafx.geometry.Insets;
import javafx.scene.control.Label;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import test.Cell;

public class FileCell extends Cell {

	Label label;
	String path;
	
    public FileCell(String name) {
        super(name);

        label = new Label(name);
        Background background = new Background(new BackgroundFill(Color.rgb(150,200,230,1), new CornerRadii(10.0), new Insets(-8.0)));
        label.setBackground(background);

        setView(label);
        setCellType(CellType.LABEL);
    }
    public Label getLabel() {
    	return label;
    }
    public void setLabel(String n_name) {
    	label.setText(n_name);
    }
    
    public void setPath(String _path) {
    	this.path = _path;
    }
    public String getPath() {
    	return this.path;
    }
}
