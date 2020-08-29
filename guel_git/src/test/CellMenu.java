package test;


import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
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
        MenuItem menuItem3 = new MenuItem("more"); 
  
        // add menu items to menu 
        contextMenu.getItems().add(menuItem1); 
        contextMenu.getItems().add(menuItem2); 
        contextMenu.getItems().add(menuItem3); 
        
       
        
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
   		 
            @Override
            public void handle(ActionEvent event) {
            	
            	
            	TextInputDialog td;
            	
            	
                    LabelCell labelcell = (LabelCell)parent;
                    // create a text input dialog 
                    td = new TextInputDialog(labelcell.cellName); 
                    // setHeaderText 
                    td.setHeaderText("rename");              
                    td.showAndWait(); 
                	
                	graph.beginUpdate();
                	labelcell.setCellName(td.getEditor().getText());
                	labelcell.setLabel(td.getEditor().getText());
                	graph.endUpdate();
              
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
 
        
        menuItem3.setOnAction(new EventHandler<ActionEvent>() {
        	
        	@Override
            public void handle(ActionEvent event) {
            	 
            }
    	});
	}
	
	
	
 	public ContextMenu getContextMenu() {
 		return contextMenu;
 	} 
	
	
	
}
