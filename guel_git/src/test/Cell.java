package test;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import test.CellMenu;
import test.Graph;
public class Cell extends Pane {

	static int global_cellID = 0;
    String cellName;
    int cellID;

    List<Cell> children = new ArrayList<>();
    List<Cell> parents = new ArrayList<>();
    List<Cell> linkedCells = new ArrayList<>();

    Graph graph;
    Node view;
    
    CellMenu menu ;
    ContextMenu contextMenu;

    public Cell(String name) {
    	this.cellName = name;
    	this.cellID = global_cellID;
    	global_cellID++;
    	this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
 
            @Override
            public void handle(ContextMenuEvent event) {
 
                contextMenu.show((Cell)event.getSource(), event.getScreenX(), event.getScreenY());
            }
        });
	}
    
    public void setGraph(Graph graph) {
    	this.graph = graph;
    }
    public void setCellMenu(CellMenu cellMenu) {
    	this.menu = cellMenu;
    	contextMenu = cellMenu.contextMenu;
    }

	public void addCellChild(Cell cell) {
        children.add(cell);
    }

    public List<Cell> getCellChildren() {
        return children;
    }

    public void addCellParent(Cell cell) {
        parents.add(cell);
    }

    public List<Cell> getCellParents() {
        return parents;
    }

    public void removeCellChild(Cell cell) {
        children.remove(cell);
    }

    public void setView(Node view) {

        this.view = view;
        getChildren().add(view);

    }

    public Node getView() {
        return this.view;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String new_name) {
    	this.cellName = new_name;
    	
    }
    
    public int getCellID() {
    	return cellID;
    }
}

