package test;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class toggle {
	
	private HBox root;
	private ToggleGroup group;
	
	public toggle() {
		
		root = new HBox(); 
        root.setPadding(new Insets(10)); 
        root.setSpacing(5); 
  
        // Gender 
        root.getChildren().add(new Label("edge ")); 
  
        // Creating a ToggleGroup 
        group = new ToggleGroup(); 
  
        // Creating new Toggle buttons. 
        ToggleButton maleButton = new ToggleButton("line"); 
        ToggleButton femaleButton = new ToggleButton("dotted line"); 
  
        // Set toggle group 
        // In a group, maximum only 
        // one button is selected 
        maleButton.setToggleGroup(group); 
        femaleButton.setToggleGroup(group); 
  
        maleButton.setUserData("line"); 
        femaleButton.setUserData("dotted line"); 
  
        // male button is selected at first by default 
        maleButton.setSelected(true); 
  
        root.getChildren().addAll(maleButton, femaleButton); 
	}
	
	public HBox getToggle() {
		return this.root;
	}
	
	public String getSelectedToggle() {
		return (String) group.getSelectedToggle().getUserData();
	}
	
	

}
