package test;


import java.util.List;
import java.util.Random;



public class RandomLayout extends Layout {

    Graph graph;

    Random rnd = new Random();

    public RandomLayout(Graph graph) {

        this.graph = graph;

    }

    public void execute() {

        List<Cell> cells = graph.getModel().getAllCells();
        int i = 10;
        int j = 10;

        for (Cell cell : cells) {

            //double x = rnd.nextDouble() * 300;
            //double y = rnd.nextDouble() * 300;
        	double x = i;
        	double y = j;

            cell.relocate(x, y);
            i += 100;
            j += 30;

        }

    }

}