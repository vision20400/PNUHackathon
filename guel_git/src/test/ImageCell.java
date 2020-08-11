package test;

import javafx.scene.image.ImageView;

import  test.Cell;

public class ImageCell extends Cell {

    public ImageCell(String id) {
        super(id);

        ImageView view = new ImageView("");
        view.setFitWidth(100);
        view.setFitHeight(80);

        setView(view);

    }

}
