package test;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

public class Edge extends Group {

    protected Cell source;
    protected Cell target;

    Line line;
    Label label;
    Graph graph;

    ContextMenu contextMenu;
	
	MenuItem menuItem1;
    MenuItem menuItem2 ;

    TextField textField = new TextField(); 

    public Edge(Cell source, Cell target , String text) {

        this.source = source;
        this.target = target;
        this.graph = source.graph;
        
        textField.setVisible(false);

        source.addCellChild(target);
        target.addCellParent(source);
        
        // create a menu 
        contextMenu = new ContextMenu(); 
  
        // create item 
        MenuItem menuItem1 = new MenuItem("rename"); 
        MenuItem menuItem2 = new MenuItem("delete"); 
  
        // add menu items to menu 
        contextMenu.getItems().add(menuItem1); 
        contextMenu.getItems().add(menuItem2); 


        line = new Line();
        

        line.startXProperty().bind( source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0));
        line.startYProperty().bind( source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0));

        line.endXProperty().bind( target.layoutXProperty().add( target.getBoundsInParent().getWidth() / 2.0));
        line.endYProperty().bind( target.layoutYProperty().add( target.getBoundsInParent().getHeight() / 2.0));
        
        label = new Label(text);
       
        label.layoutXProperty().bind(line.endXProperty().subtract(line.endXProperty().subtract(line.startXProperty()).divide(2)));
        label.layoutYProperty().bind(line.endYProperty().subtract(line.endYProperty().subtract(line.startYProperty()).divide(2)));

        textField.layoutXProperty().bind(line.endXProperty().subtract(line.endXProperty().subtract(line.startXProperty()).divide(2)));
        textField.layoutYProperty().bind(line.endYProperty().subtract(line.endYProperty().subtract(line.startYProperty()).divide(2)));

       
        
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
   		 
            @Override
            public void handle(ActionEvent event) {
            	
              textField.setVisible(true);
  		      textField.setText(label.getText());   
  		      textField.selectAll();  
  		      textField.requestFocus();
            	 	
            }
        });
        
        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
      		 
            @Override
            public void handle(ActionEvent event) {
            	graph.beginUpdate();
            	getChildren().removeAll( line, label, textField);
            	graph.endUpdate();
            	
            	graph.getModel().removeEdge(String.valueOf(source.getCellID()), String.valueOf(target.getCellID()));
            }
        });
        
        
		label.setOnMouseClicked(new EventHandler<MouseEvent>() {  
			
		  @Override  
		  public void handle(MouseEvent event) {  
		    if (event.getClickCount()==2) {  
		      textField.setVisible(true);
		      textField.setText(label.getText());   
		      textField.selectAll();  
		      textField.requestFocus();  
		    }  
		  }  
		  
		}); 
		
		line.setOnMouseClicked(new EventHandler<MouseEvent>() {  
			
			  @Override  
			  public void handle(MouseEvent event) {  
			    if (event.getClickCount()==2) {  
			      textField.setVisible(true);
			      textField.setText(label.getText());   
			      textField.selectAll();  
			      textField.requestFocus();  
			    }  
			  }  
			  
			}); 
		
		
		textField.setOnAction(new EventHandler<ActionEvent>() {  
		  @Override  
		  public void handle(ActionEvent event) {  
		    label.setText(textField.getText()); 
		    textField.setVisible(false);
		  }  
		});
		
		
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {  
		  @Override  
		  public void changed(ObservableValue<? extends Boolean> observable,  
		      Boolean oldValue, Boolean newValue) {  
		    if (! newValue) {  
		      label.setText(textField.getText());  
		      textField.setVisible(false);
		    }  
		  }  
		});  
		
        
		label.setContextMenu(contextMenu);
        getChildren().addAll( line, label, textField);
       
    }

    public Cell getSource() {
        return source;
    }

    public Cell getTarget() {
        return target;
    }

    public Label getEdgeLable() {
    	return label;
    }
    public void setEdgeLabel(String text) {
    	label.setText(text);
    }
    
    
}