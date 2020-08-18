package test;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import test.Cell;

public class TitledPaneCell extends Cell {

    Cell relatedCell;
    Label label;
    Graph graph;

    public TitledPaneCell(String name) {
        super(name);

        label = new Label(name);
        Background background = new Background(new BackgroundFill(Color.rgb(200,230,250,1), new CornerRadii(10.0), new Insets(-8.0)));
        label.setBackground(background);

        setView(label);
        setCellType(CellType.TITLEDPANE);

        label.setOnMouseClicked(event -> {
            graph.beginUpdate();
            Cell thisCell = graph.getModel().addCell(label.getText(), CellType.LABEL);
            thisCell.relocate(relatedCell.getLayoutX(), relatedCell.getLayoutY() + 100);
            graph.getModel().addEdge(String.valueOf(relatedCell.getCellID()), String.valueOf(thisCell.getCellID()));
            graph.endUpdate();
            label.setDisable(true);
        });
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
    public void setRelatedCell(Cell relatedCell) {
        this.relatedCell = relatedCell;
    }
    public Label getLabel() {
        return label;
    }
    public void setLabel(String n_name) {
        label.setText(n_name);
    }
}