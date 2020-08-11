package test;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import test.Cell;

public class RectangleCell extends Cell {

    public RectangleCell( String id) {
        super(id);

        Rectangle view = new Rectangle( 50,50);

        view.setStroke(Color.DODGERBLUE);
        view.setFill(Color.DODGERBLUE);

        setView( view);

    }

}