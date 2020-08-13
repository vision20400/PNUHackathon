package application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfoenix.controls.JFXTabPane;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import test.CellType;
import test.Graph;
import test.Model;
import javafx.scene.control.Alert.AlertType;


public class MainController implements Initializable {
	@FXML
	private TreeView<PathItem> treeV;
	@FXML
	private MenuBar menu;
	@FXML
	private MenuItem newFile;
	@FXML
	private MenuItem openFile;
	@FXML
	private MenuItem newWindow;
	@FXML
	private MenuItem save;
	@FXML
	private MenuItem print;
	@FXML
	private JFXTabPane mainTab;
	@FXML
	private TextField txtMsg;
	@FXML
	private Button labelCell;
	@FXML
	private Button loadbtn;
	@FXML
	private Button binderbtn;
	@FXML
	private Button newFilebtn;
	@FXML
	private Button mappingbtn;
	@FXML
	private SplitPane split;
	
	private String LoadPath;
	
	private Path rootPath;
	
	@FXML
	private BorderPane mapping;
	
	private Graph graph ;
	

	
	private StringProperty messageProp= new SimpleStringProperty();
	private ExecutorService service;
	public MainController() {
        treeV = new TreeView<>();
        treeV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        service = Executors.newFixedThreadPool(3);
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	 
		graph = new Graph();
	    mapping.setCenter(graph.getScrollPane());
		
		
		//���丮 �ε� ��ư �׼�
		loadbtn.setOnAction((event) -> {
			DirectoryChooser directorychooser = new DirectoryChooser();
			directorychooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File choice = directorychooser.showDialog(null);
            if(choice == null || ! choice.isDirectory()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Could not open directory");
                alert.setContentText("The file is invalid.");

                alert.showAndWait();
            } else {
            	LoadPath = choice.getPath();
            	rootPath = Paths.get(LoadPath);
            	PathItem pathItem = new PathItem(rootPath);
                treeV.setRoot(createNode(pathItem));
                treeV.setEditable(true);
                treeV.setCellFactory((TreeView<PathItem> p) -> {
                	final PathTreeCell cell = new PathTreeCell(messageProp);
                    setDragDropEvent(cell);
                    return cell;
                });
            }
        
	   });
		
		//����Ʈ�� �����,���̱�
		binderbtn.setOnAction((event) -> {
			if(split.getDividerPositions()[0] <= 0.1)
				split.setDividerPositions(0.2);
			else
				split.setDividerPositions(0.0);
		});
		
		//������ �߰�
		newFilebtn.setOnAction((event) -> {
			Tab tab = new Tab();
		    tab.setText("untitled"); //*.txt
		    TextArea textArea = new TextArea();
		    textArea.appendText("");
		    tab.setContent(textArea);
		    mainTab.getTabs().add(tab);
		});
		
		//���� �����,���̱�
		mappingbtn.setOnAction((event) -> {
			if(split.getDividerPositions()[1] >= 0.9)
				split.setDividerPositions(split.getDividerPositions()[0], 0.8);
			else
				split.setDividerPositions(split.getDividerPositions()[0], 0.9764705882352941);
		});
		
		//���� ����
		openFile.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent event) {
		        FileChooser newFileChooser = new FileChooser();
		        File newFile = newFileChooser.showOpenDialog(null);
		        if (newFile != null) {
		            openNewTab(newFile.getPath());
		        }
		    }
		});
		//������ �߰�
		newFile.setOnAction((event) -> {
			Tab tab = new Tab();
		    tab.setText("untitled"); //*.txt
		    TextArea textArea = new TextArea();
		    textArea.appendText("");
		    tab.setContent(textArea);
		    mainTab.getTabs().add(tab);
		});
		//����
		save.setOnAction((event) -> {
			if (isTabExist()) {
	            FileChooser saveFileChooser = new FileChooser();
	            File saveFile = saveFileChooser.showSaveDialog(null);
	            if (saveFile != null) {
	              saveFile(saveFile);
	            }
	        }
		});
		//����Ʈ
		print.setOnAction((event) -> {
			if (isTabExist()) {
		        PrinterJob printerJob = PrinterJob.createPrinterJob();
		        if (printerJob.showPrintDialog(null)){
		            boolean success = printerJob.printPage(mainTab.getSelectionModel().getSelectedItem().getContent());
		            if (success) {
		                printerJob.endJob();
		            }
		        }
		    }
        });
		//�Ű椤��
		newWindow.setOnAction((event) -> {
	        System.out.println("newWindow clicked");
	        /*
			Stage stage = new Stage();
			stage.setTitle(mainTab.getSelectionModel().getSelectedItem().getText());
			TextArea textArea = (TextArea)mainTab.getSelectionModel().getSelectedItem().getContent();
			TextArea newArea = new TextArea(textArea.getText());
			newArea.setEditable(false);
			stage.setScene(new Scene(newArea, 1000, 800));
			stage.show();      
			
			*/
		});
		//Ʈ�� ������ �ι�  Ŭ���� -���� ���� ��
		treeV.setOnMouseClicked(new EventHandler<MouseEvent>()
		{
		    @Override
		    public void handle(MouseEvent mouseEvent)
		    {
		        if(mouseEvent.getClickCount() == 1)
		        {
		            TreeItem<PathItem> item = treeV.getSelectionModel().getSelectedItem();
		            if(item.isLeaf()) {
		            	String clickpath = getTreePath(item);
			            System.out.println("Selected Text : " + clickpath);
			            openNewTab(clickpath);
		            }
		        }
		    }
		 });

 	}

	//���� ����
		 
	private void saveFile(File saveFile) {
		
		TextArea textArea = (TextArea) mainTab.getSelectionModel().getSelectedItem().getContent();
	    try{
	      FileWriter writer = null;
	      writer = new FileWriter(saveFile);
	      writer.write(textArea.getText().replaceAll("\n", "\r\n"));
	      writer.close();
	      
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	
	}

	//�� ��������
	private boolean isTabExist() {
		return mainTab.getSelectionModel().getSelectedItem() != null;
	}


	//������ ���� �ǿ� �߰�
	public void openNewTab(String path){
		File txtFile = new File(path);
		
		Tab tab = new Tab();
	    tab.setText(txtFile.getName()); //*.txt
	    TextArea textArea = new TextArea();
    		 
	    BufferedReader br = null;
	    
	    try{
	      br = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile)));
	      String line;
	      while((line = br.readLine()) != null){
	        textArea.appendText(line + "\n");
	      }
	      
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	      
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  
	    tab.setContent(textArea);
	    //tabpane ���� �߰������� ���� �������־����� �ڵ����� �� tab���� ������ ������ ��(�̿ϼ�)
	    mainTab.getTabs().add(tab);
	 }

		
		
	//���丮�� Ʈ�� �����	
	public TreeItem<String> getNodesForDirectory(File directory) {
		   //Returns a TreeItem representation of the specified directory
           TreeItem<String> root = new TreeItem<String>(directory.getName());
           for(File f : directory.listFiles()) {
               System.out.println("Loading " + f.getName());
               if(f.isDirectory()) { //Then we call the function recursively
                   root.getChildren().add(getNodesForDirectory(f));
               } else {
            	   if(FileExtension(f.getName())) {
            		   System.out.println(f.getName());
	                   root.getChildren().add(new TreeItem<String>(f.getName()));
            	   }
               }
           }
           return root;
	 }
	
	// ������ Ʈ�������� ��� ���ϱ�
	public String getTreePath(TreeItem<PathItem> item) {
		TreeItem<PathItem> cur_item = item;
		String path = "";
		
		while(true) {
			if(cur_item.getParent() == null)
				break;
			else {
				path = "\\"+cur_item.getValue()+ path;
				cur_item = cur_item.getParent();
			}
		}
		path = LoadPath + path ;
		
		return path;
	}

	
	

	public void clickHandler() {
		String msg = txtMsg.getText();
		
		addGraphComponents();
		/*Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Gondr �˸�â");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.show();*/
		
	}
	
	private void addGraphComponents() {
        Model model = graph.getModel();

        graph.beginUpdate();
        model.addCell(txtMsg.getText(), CellType.LABEL);
        graph.endUpdate();
    }

// ���� Ȯ���� ���� �ϴ°� �ٵ� filefilter�� �̷� ��� �ִµ�
	public static boolean FileExtension(String name) {
        String fileName = name;
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length());
        final String[] extension = { "txt","jpg"};// Ȯ���� ���� 
 
        int len = extension.length;
        for (int i = 0; i < len; i++) {
            if (ext.equalsIgnoreCase(extension[i])) {
                return true; 
            }
        }
        return false;
    }
	//drag and drop ����
	private void setDragDropEvent(final PathTreeCell cell) {
        // The drag starts on a gesture source
        cell.setOnDragDetected(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if (item != null && item.isLeaf()) {
                Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                List<File> files = Arrays.asList(cell.getTreeItem().getValue().getPath().toFile());
                content.putFiles(files);
                db.setContent(content);
                event.consume();
            }
        });
        // on a Target
        cell.setOnDragOver(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if ((item != null && !item.isLeaf()) &&
                    event.getGestureSource() != cell &&
                    event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();
                final Path sourceParentPath = sourceCell.getTreeItem().getValue().getPath().getParent();
                if (sourceParentPath.compareTo(targetPath) != 0) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });
        // on a Target
        cell.setOnDragEntered(event -> {
            TreeItem<PathItem> item = cell.getTreeItem();
            if ((item != null && !item.isLeaf()) &&
                    event.getGestureSource() != cell &&
                    event.getDragboard().hasFiles()) {
                Path targetPath = cell.getTreeItem().getValue().getPath();
                PathTreeCell sourceCell = (PathTreeCell) event.getGestureSource();
                final Path sourceParentPath = sourceCell.getTreeItem().getValue().getPath().getParent();
                if (sourceParentPath.compareTo(targetPath) != 0) {
                    cell.setStyle("-fx-background-color: powderblue;");
                }
            }
            event.consume();
        });
        // on a Target
        cell.setOnDragExited(event -> {
        	TreeItem<PathItem> item = cell.getTreeItem();
        	ObjectProperty<TreeItem<PathItem>> prop = new SimpleObjectProperty<>();
        	prop.addListener((ObservableValue<? extends TreeItem<PathItem>> ov, TreeItem<PathItem> oldItem, TreeItem<PathItem> newItem) -> {
        		try {
        			Files.walkFileTree(newItem.getValue().getPath(), new VisitorForDelete());
        			item.getParent().getChildren().remove(newItem);
        		} catch (IOException ex) {
        			messageProp.setValue(String.format("Deleting %s failed", newItem.getValue().getPath().getFileName()));
        		}
        	});
            event.consume();
        });
        // on a Target
        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                final Path source = db.getFiles().get(0).toPath();
                final Path target = Paths.get(
                        cell.getTreeItem().getValue().getPath().toAbsolutePath().toString(),
                        source.getFileName().toString());
                if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                    Platform.runLater(() -> {
                        BooleanProperty replaceProp = new SimpleBooleanProperty();
                        //CopyModalDialog dialog = new CopyModalDialog(stage, replaceProp);
                        replaceProp.addListener((ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) -> {
                            if (newValue) {
                                FileCopyTask task = new FileCopyTask(source, target);
                                service.submit(task);
                            }
                        });
                    });
                } else {
                    FileCopyTask task = new FileCopyTask(source, target);
                    service.submit(task);
                    task.setOnSucceeded(value -> {
                        Platform.runLater(() -> {
                            TreeItem<PathItem> item = PathTreeItem.createNode(new PathItem(target));
                            cell.getTreeItem().getChildren().add(item);
                        });
                    });
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        // on a Source
        cell.setOnDragDone(event -> {
            ;
        });
    }
	
    private TreeItem<PathItem> createNode(PathItem pathItem) {
        return PathTreeItem.createNode(pathItem);
    }

}
