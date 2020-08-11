package test;

import javafx.scene.control.TitledPane;

import test.Cell;

public class TitledPaneCell extends Cell {

    public TitledPaneCell(String id) {
        super(id);

        TitledPane view = new TitledPane();
        view.setPrefSize(100, 80);

        setView(view);

    }

}