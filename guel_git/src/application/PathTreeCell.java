package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import test.CellType;
import test.FileCell;
import test.LabelCell;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.jfoenix.controls.JFXTabPane;

public class PathTreeCell extends TreeCell<PathItem>{
    private TextField textField;
    private Path editingPath;
    private StringProperty messageProp;
    private ContextMenu dirMenu = new ContextMenu();
    private ContextMenu fileMenu = new ContextMenu();
    private ExecutorService service;
    private static List<String> imageList;
    private VBox vbox;
    private JFXTabPane mainTab;
    private Map <String, Tab> openTabs = new HashMap<>();
    static {
        imageList = Arrays.asList("BMP", "DIB", "RLE", "JPG", "JPEG" , "JPE", "JFIF"
        , "GIF", "EMF", "WMF", "TIF", "TIFF", "PNG", "ICO");
    }

    public PathTreeCell(final StringProperty messageProp , VBox vbox, JFXTabPane mainTab) {
        this.messageProp = messageProp;
        this.vbox = vbox;
        this.mainTab = mainTab;
        MenuItem expandMenu = new MenuItem("Expand");
        expandMenu.setOnAction((ActionEvent event) -> {
            getTreeItem().setExpanded(true);
        });
        
        MenuItem expandAllMenu = new MenuItem("Expand All");
        expandAllMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                expandTreeItem(getTreeItem());
            }
            private void expandTreeItem(TreeItem<PathItem> item) {
                if (item.isLeaf()){
                    return;
                }
                item.setExpanded(true);
                ObservableList<TreeItem<PathItem>> children = item.getChildren();
                children.stream().filter(child -> (!child.isLeaf()))
                    .forEach(child -> expandTreeItem(child));
            }
        });
        
        MenuItem addMenu = new MenuItem("Add Directory");
        addMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Path newDir = createNewDirectory();
                if (newDir != null) {
                    TreeItem<PathItem> addItem = PathTreeItem.createNode(new PathItem(newDir));
                    getTreeItem().getChildren().add(addItem);
                }
            }
            private Path createNewDirectory() {
                Path newDir = null;
                while (true) {
                    Path path = getTreeItem().getValue().getPath();
                    newDir = Paths.get(path.toAbsolutePath().toString(), "newDirectory" + String.valueOf(getItem().getCountNewDir()));
                    try {
                        Files.createDirectory(newDir);
                        break;
                    } catch (FileAlreadyExistsException ex) {
                        continue;
                    } catch (IOException ex) {
                        cancelEdit();
                        messageProp.setValue(String.format("Creating directory(%s) failed", newDir.getFileName()));
                        break;
                    }
                }
                    return newDir;
            }
        });
        //create new file(txt) ���ο� txt���� ����
        MenuItem addtxtfile = new MenuItem("AddTXTFile");
        addtxtfile.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		Path newfile = createnewfile();
            	if(newfile != null) {
            		TreeItem<PathItem> addthetxtfile = PathTreeItem.createNode(new PathItem(newfile));
            		getTreeItem().getChildren().add(addthetxtfile);
            	}
        	}
        	private Path createnewfile() {
        		 Path newtxt = null;
                 while (true) {
                     Path path = getTreeItem().getValue().getPath();
                     newtxt = Paths.get(path.toAbsolutePath().toString(), "new.txt");
                     try {
                         Files.createFile(newtxt);
                         break;
                     } catch (FileAlreadyExistsException ex) {
                         continue;
                     } catch (IOException ex) {
                         cancelEdit();
                         messageProp.setValue(String.format("Creating file(%s) failed", newtxt.getFileName()));
                         break;
                     }
                 }
                     return newtxt;
        		
        	}
        });
        //���� �ҷ�����(������ ������ ��������, ���� ���丮���� �ҷ����� ���̱� ������)
        MenuItem addfile = new MenuItem("AddFile");
        addfile.setOnAction(new EventHandler<ActionEvent>() {
        	public void handle(ActionEvent t) {
        		FileChooser filechooser = new FileChooser();
        		filechooser.setInitialDirectory(new File(System.getProperty("user.home")));
                File choice = filechooser.showSaveDialog(null);
                if(choice == null || ! choice.isDirectory()) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("The file is invalid.");

                    alert.showAndWait();
                } else {
                	String LoadPath = choice.getPath();
                	Path RootPath = Paths.get(LoadPath);
                	System.out.println(RootPath);
                	PathItem pathItem = new PathItem(RootPath);
                	TreeItem<PathItem> addthefile = PathTreeItem.createNode(pathItem);
            		getTreeItem().getChildren().add(addthefile);
                }
        	}
        });
        
        MenuItem deleteMenu =new MenuItem("Delete");
        deleteMenu.setOnAction((ActionEvent event) -> {
            ObjectProperty<TreeItem<PathItem>> prop = new SimpleObjectProperty<>();
            new ModalDialog(getTreeItem(), prop);
            prop.addListener((ObservableValue<? extends TreeItem<PathItem>> ov, TreeItem<PathItem> oldItem, TreeItem<PathItem> newItem) -> {
                try {
                    Files.walkFileTree(newItem.getValue().getPath(), new VisitorForDelete());
                    if (getTreeItem().getParent() == null){
                        // when the root is deleted how to clear the TreeView???
                    } else {
                        getTreeItem().getParent().getChildren().remove(newItem);
                    }
                } catch (IOException ex) {
                    messageProp.setValue(String.format("Deleting %s failed", newItem.getValue().getPath().getFileName()));
                }
            });
        });
        //���� ���丮�� �ִ� ���� �̸� �ٲٱ�(������)
        MenuItem rename = new MenuItem("rename");
        rename.setOnAction(event -> {
        	//rename the file
        	Thread thread = new Thread() {
        		public void run() {
        			Platform.runLater(() -> {
        				startEdit();
        			});
        		}
        	};
        	thread.start();
        });
        MenuItem copy = new MenuItem("copy");
        copy.setOnAction(event ->{
        	Thread thread = new Thread() {
        		public void run() {
        			Platform.runLater(() -> {
        				int num = 1;
        	        	try {
        	        		Path source = getTreeItem().getValue().getPath();
            	        	String copyfile = getTreeItem().getValue().toString();
            	        	System.out.println(copyfile);
            	        	String copiedfile = copyfile.replaceFirst("(\\.[^\\.]*)?$", "-copy");
            	        	Path target = source.resolveSibling(copiedfile);
							Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
							TreeItem<PathItem> item = PathTreeItem.createNode(new PathItem(target));
	                        getTreeItem().getParent().getChildren().add(item);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			});
        		}
        	};
        	
        });
        
        MenuItem addtimeline = new MenuItem("timeline");
        addtimeline.setOnAction(new EventHandler<ActionEvent>() {
      		 
            @Override
            public void handle(ActionEvent event) {
            	String path = getTreeItem().getValue().getPath().toString();
            	
            	
        		
        		String separator = "\\";
    	    	String[] arr=path.replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
    	        FileCell cell = new FileCell(arr[arr.length - 1]);
    	        
    	        cell.setPath(path);
    	        cell.setOnMouseClicked(new EventHandler<MouseEvent>()
    			{
    			    @Override
    			    public void handle(MouseEvent mouseEvent)
    			    {
    			        if(mouseEvent.getClickCount() == 2){
    				       // openNewTab(cell.getPath());
    			        	System.out.println(cell.getPath());
    			        }
    			    }
    			 });
    	        
        		vbox.getChildren().add(cell);
         	           	
            }
        });	
        	
            
        dirMenu.getItems().addAll(expandMenu, expandAllMenu, addMenu, addtxtfile, addfile);
        fileMenu.getItems().addAll(deleteMenu, rename, copy, addtimeline);
    } 

    @Override
    protected void updateItem(PathItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            final String itemString = getString();
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(itemString);
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(itemString);
                final Node image = getImage(itemString);
                if (image != null) {
                    setGraphic(image); // <-- *** add image
                } else {
                    setGraphic(null);
                }
                if (!getTreeItem().isLeaf()) {
                    setContextMenu(dirMenu);
                } else {
                    setContextMenu(fileMenu);
                }
            }
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null){
            createTextField();
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();
        if (getItem() == null) {
            editingPath = null;
        } else {
            editingPath =getItem().getPath();
        }
        System.out.println("5");
    }

    @Override
    public void commitEdit(PathItem pathItem) {
        // rename the file or directory
    	System.out.println("3");
        if (editingPath != null) {
            try {
                Files.move(editingPath, pathItem.getPath());
                System.out.println("4");
            } catch (IOException ex) {
                cancelEdit();
                messageProp.setValue(String.format("Renaming %s filed", editingPath.getFileName()));
            }
        }
        super.commitEdit(pathItem);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getString());
        setGraphic(null);
    }

    public String getString() {
        return getItem().toString();
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased((KeyEvent t) -> {
        	System.out.println("1");
            if (t.getCode() == KeyCode.ENTER){
                Path path = Paths.get(getItem().getPath().getParent().toAbsolutePath().toString(), textField.getText());
                commitEdit(new PathItem(path));
                System.out.println("2");
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    private Node getImage(String itemPath) {
        String parentPath = getItem().getPath().getParent().toAbsolutePath().toString();
        Image image = new Image("file:" + parentPath + "/" + itemPath);
        if (image.isError()) {
            return null; // <-- if not image
        }
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitWidth(10); // <-- set size of image
        imageView.setPreserveRatio(true);
        return imageView;
    }
    
    public void openNewTab(String path){
		File txtFile = new File(path);
		final HTMLEditor htmlEditor = new HTMLEditor();
        htmlEditor.setPrefHeight(245);
        
		TabSetText n_tab = new TabSetText();
		Tab tab = n_tab.createEditableTab(txtFile.getName());
	    
	    try {
		       // 바이트 단위로 파일읽기
		        String filePath = path; // 대상 파일
		        FileInputStream fileStream = null; // 파일 스트림
		        
		        fileStream = new FileInputStream( filePath );// 파일 스트림 생성
		        //버퍼 선언
		        byte[ ] readBuffer = new byte[fileStream.available()];
		        while (fileStream.read( readBuffer ) != -1){}
		       
		        htmlEditor.setHtmlText(new String(readBuffer));
		        fileStream.close(); //스트림 닫기
		    } catch (Exception e) {
		    	
			e.getStackTrace();
		    }
	    
	    
	    tab.setContent(htmlEditor);
	    //tabpane 새로 추가했을때 원래 눌러져있었으면 자동으로 그 tab으로 가도록 만들어야 됨(미완성)
	    if(openTabs.containsKey(path)) {
	    	mainTab.getSelectionModel().select(openTabs.get(tab));
	    }
	    else {
	    	mainTab.getTabs().add(tab);
	    	openTabs.put(path, tab);
	    	tab.setOnClosed(e -> openTabs.remove(path));
	    }
	   
	    //System.out.println(mainTab.getContextMenu().getItems().toString());
	    /*if(mainTab.getTabs().contains(tab)) {
	    	System.out.println("1");
	    	
	    }
	    else {
	    	mainTab.getTabs().add(tab);
	    }*/
	 }
    
}
