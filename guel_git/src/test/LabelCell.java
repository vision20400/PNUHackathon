package test;

import javafx.geometry.Insets;
import javafx.scene.control.Label;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import test.Cell;

public class LabelCell extends Cell {

	Label label;
	
    public LabelCell(String name) {
        super(name);

        label = new Label(name);
        Background background = new Background(new BackgroundFill(Color.rgb(200,230,250,1), new CornerRadii(10.0), new Insets(-8.0)));
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
