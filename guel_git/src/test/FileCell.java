package test;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import test.Cell;

public class FileCell extends Cell {

	private static String name;
	Label label;
	String path;

	
    public FileCell() {
    	
        super(name);

        label = new Label("");
        Background background = new Background(new BackgroundFill(Color.AQUAMARINE, new CornerRadii(5.0), new Insets(-5.0)));
        label.setBackground(background);

        setView(label);
    }
    public Label getLabel() {
    	return label;
    }
    public void setLabel(String n_name) {
    	label.setText(n_name);
    }


}
