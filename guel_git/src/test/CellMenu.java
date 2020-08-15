package test;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import test.Cell;
import test.Graph;

public class CellMenu {

	Graph graph;
	Cell parent;
	ContextMenu contextMenu;
	MenuItem menuItem1;
    MenuItem menuItem2 ; 
    MenuItem menuItem3 ; 
	
	public CellMenu(Cell parent) {
		this.parent = parent;
		this.graph = parent.graph;
		
        // create a menu 
        contextMenu = new ContextMenu(); 
  
        // create menuitems 
        MenuItem menuItem1 = new MenuItem("rename"); 
        MenuItem menuItem2 = new MenuItem("delete"); 
        MenuItem menuItem3 = new MenuItem(""); 
  
        // add menu items to menu 
        contextMenu.getItems().add(menuItem1); 
        contextMenu.getItems().add(menuItem2); 
        contextMenu.getItems().add(menuItem3); 
        
       
        
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
   		 
            @Override
            public void handle(ActionEvent event) {
            	
            	LabelCell curCell = (LabelCell)parent;

            	 // create a text input dialog 
                TextInputDialog td = new TextInputDialog(curCell.cellName); 
                // setHeaderText 
                td.setHeaderText("rename");              
                td.showAndWait(); 
            	
            	graph.beginUpdate();
            	curCell.setCellName(td.getEditor().getText());
            	curCell.setLabel(td.getEditor().getText());
            	graph.endUpdate();
            	System.out.println(parent.cellName);
            }
        });
        
        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
      		 
            @Override
            public void handle(ActionEvent event) {
            	 graph.beginUpdate();
            	 graph.getModel().removeCell(parent);
            	 graph.endUpdate();
            }
        });
  

	}
	
	
 	public ContextMenu getContextMenu() {
 		return contextMenu;
 	} 
	
	
	
}
