package test;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MouseGestures {
    final DragContext dragContext = new DragContext();

    Graph graph;
    Cell cellTarget;
    Cell cellSource;

    private class Position {
        double x;
        double y;

        public Position (double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    Map<Cell, Position> prevPositions = new HashMap<>();

    public MouseGestures(Graph graph) {
    	 this.graph = graph;
    }

	public void makeDraggable( final Node node) {
        node.setOnMousePressed(onMousePressedEventHandler);
        node.setOnMouseDragged(onMouseDraggedEventHandler);
        node.setOnMouseReleased(onMouseReleasedEventHandler);
        node.setOnMouseEntered(onMouseEnteredEventHandler);
        node.setOnMouseExited(onMouseExitedEventHandler);
    }
	
    EventHandler<MouseEvent> onMouseEnteredEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            // Node node = (Node) event.getTarget();
             cellTarget = (Cell) event.getTarget();
             cellSource = (Cell) event.getSource();

             //System.out.println(cellTarget.getCellId());
             //System.out.println(cellSource.getCellId());
        }
    };

    EventHandler<MouseEvent> onMouseExitedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
        	 //System.out.println("exited");
        }
    };	    	

    EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Cell cell = (Cell) event.getSource();

            double scale = graph.getScale();

            dragContext.x = cell.getBoundsInParent().getMinX() * scale - event.getScreenX();
            dragContext.y = cell.getBoundsInParent().getMinY()  * scale - event.getScreenY();

            prevPositions.put(cell, new Position(cell.getLayoutX(), cell.getLayoutY()));
        }
    };

    EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Node node = (Node) event.getSource();
   
            double offsetX = event.getScreenX() + dragContext.x;
            double offsetY = event.getScreenY() + dragContext.y;

            // adjust the offset in case we are zoomed
            double scale = graph.getScale();

            offsetX /= scale;
            offsetY /= scale;

            node.relocate(offsetX, offsetY);
            
           

        }
    };

    EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
        	
            Cell cell = (Cell) event.getSource();
        
            for (Cell _cell : graph.getModel().allCells) {
                if (cell.equals(_cell)) {
                    continue;
                }

                if (cell.intersects(cell.sceneToLocal(
                        _cell.localToScene(_cell.getBoundsInLocal())))) {
                    cell.setLayoutX(prevPositions.get(cell).x);
                    cell.setLayoutY(prevPositions.get(cell).y);

                    graph.beginUpdate();
                    graph.getModel().addEdge(String.valueOf(cell.cellID), String.valueOf(_cell.cellID),"");
                    graph.endUpdate();

                    break;
                }
            }

            prevPositions.remove(cell);
        }
    };

    class DragContext {

        double x;
        double y;

    }
    
    public void addComponentEdge(String sourceId, String targetId) {

        Model model = graph.getModel();

        graph.beginUpdate();

        
        model.addEdge(sourceId,targetId,"");
        

        graph.endUpdate();

    }
    
    
    
}