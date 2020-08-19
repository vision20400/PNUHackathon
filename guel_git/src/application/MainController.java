package application;


import java.awt.Event;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.json.*;
import javax.json.stream.JsonParser;

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
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import test.*;
import javafx.scene.control.Alert.AlertType;
import test.Cell;


public class MainController implements Initializable {
	@FXML
	private SplitPane split;
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
	private Button template1btn;
	@FXML
	private HBox mergeHBox;
	@FXML
	private ToggleButton mergeToggle;
	@FXML
	private Button mergebutton;
	@FXML
	private Button saveMap;
	@FXML
	private Button loadMap;
	@FXML
	private TitledPane associatedWords;
	@FXML
	private GridPane associatedGrid;
	
	private String LoadPath;
	private String filePath;
	private Path rootPath;
	private MyStack addallcontents = new MyStack(30);
	
	@FXML
	private BorderPane mapping;
	
	private Graph graph ;
	
	private static boolean fromTreeToMapping = false;
    final DragContext dragContext = new DragContext();

    class DragContext {

        double x;
        double y;

    }
	
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
	    
	    split.setOnDragEntered(event -> {
	    	if (treeV.getBoundsInParent().contains(event.getSceneX(), event.getSceneY()))
	    		fromTreeToMapping = true;
	    	
	    	
	    });
	    split.setOnDragOver(event -> {
	    	dragContext.x = event.getX() - mapping.getParent().getBoundsInParent().getMinX();
	    	dragContext.y = event.getY() - mapping.getParent().getParent().getBoundsInParent().getMinY()/2;
	    	

	    });
	    split.setOnDragDone(event -> {
	    	if (fromTreeToMapping == false)
	    		return;
	    	if (mapping.getBoundsInParent().contains(dragContext.x, dragContext.y) == false)
	    		return;
	    		
	    	Model model = graph.getModel();
	    	
	    	String separator = "\\";
	    	String[] arr=filePath.replaceAll(Pattern.quote(separator), "\\\\").split("\\\\");
	        graph.beginUpdate();
	        FileCell cell = (FileCell) model.addCell(arr[arr.length - 1], CellType.FILE);
	        
	        cell.setPath(filePath);
	        cell.setOnMouseClicked(new EventHandler<MouseEvent>()
			{
			    @Override
			    public void handle(MouseEvent mouseEvent)
			    {
			        if(mouseEvent.getClickCount() == 2){
				        openNewTab(cell.getPath());
			        }
			    }
			 });
	   
	        
	        double offsetX = event.getScreenX() + dragContext.x;
            double offsetY = event.getScreenY() + dragContext.y;

            // adjust the offset in case we are zoomed
            double scale = graph.getScale();

            offsetX /= scale;
            offsetY /= scale;

            cell.relocate(offsetX, offsetY);
	        graph.endUpdate();
	        
	        fromTreeToMapping = false;
	    });
		
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
			final HTMLEditor htmlEditor = new HTMLEditor();
	        htmlEditor.setPrefHeight(245);
			TabSetText n_tab = new TabSetText();
			Tab tab = n_tab.createEditableTab("�� ����");
			
		    //TextArea textArea = new TextArea();
		    //textArea.appendText("");
		    tab.setContent(htmlEditor);
		    mainTab.getTabs().add(tab);
		});
				
		//���� �����,���̱�
		mappingbtn.setOnAction((event) -> {
			if(split.getDividerPositions()[1] > 0.7)
				split.setDividerPositions(split.getDividerPositions()[0], 0.7);
			else
				split.setDividerPositions(split.getDividerPositions()[0], 1.0);
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
			final HTMLEditor htmlEditor = new HTMLEditor();
	        htmlEditor.setPrefHeight(245);
			TabSetText n_tab = new TabSetText();
			Tab tab = n_tab.createEditableTab("�� ����");

		    //TextArea textArea = new TextArea();
		    //textArea.appendText("");
		    tab.setContent(htmlEditor);
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
		            if (item == null)
		            	return;
		            
		            if(item.isLeaf()) {
		            	String clickpath = getTreePath(item);
			            System.out.println("Selected Text : " + clickpath);
			            openNewTab(clickpath);
		            }
		            else {
		            	String clickpath = getTreePath(item);
		            	System.out.println("Selected Text : " + clickpath);
		            	openallfileTab(clickpath);
		            }
		        }
		        
		        if(mouseEvent.getClickCount() == 2 && mergeToggle.isSelected())
		        {
		            TreeItem<PathItem> item = treeV.getSelectionModel().getSelectedItem();
		            if(mergeHBox.hasProperties()) {
		            	mergebutton.setDisable(true);
		            }
		            else {
		            	mergebutton.setDisable(false);
		            }
		            if (item == null)
		            	return;
		            
		            if(item.isLeaf()) {
		            	String clickpath = getTreePath(item);
			            System.out.println("Selected Text : " + clickpath);
			            addMergeFile(clickpath);
		            }
		        }
		    }
		 });
		
		mergebutton.setOnAction((event) ->{
			Tab tab = new Tab();
			TextArea textArea = new TextArea();
			if(mergeHBox.hasProperties()) {
				while(addallcontents.isEmpty()) {
					File txtfile = new File(addallcontents.pop());
					System.out.println(addallcontents.pop());
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(txtfile)));
						String line;
						textArea.appendText("file : " + txtfile.getName() + "\n");
					    while((line = br.readLine()) != null){
					      textArea.appendText(line + "\n");
					    }
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
					    e.printStackTrace();
					}
				}
				tab.setText("merge result");
				tab.setContent(textArea);
				mainTab.getTabs().add(tab);
			}
			mergeHBox.getChildren().clear();
			addallcontents.clear();
		});
		
		template1btn.setOnAction((event) -> {
			final HTMLEditor htmlEditor = new HTMLEditor();
	        htmlEditor.setPrefHeight(245);
			TabSetText n_tab = new TabSetText();
			Tab tab = n_tab.createEditableTab("untitled");
			Synopsis txt = new Synopsis();
			String Synopsis = txt.template();
		    //TextArea textArea = new TextArea();
		    //textArea.appendText("");
			htmlEditor.setHtmlText(Synopsis);
		    tab.setContent(htmlEditor);
		    mainTab.getTabs().add(tab);
		});
		
		txtMsg.setOnKeyPressed(new EventHandler<KeyEvent> () {
		    @Override
		      public void handle(KeyEvent event) {
		    	
		    	if(event.getCode().equals(KeyCode.ENTER)) {
		            clickHandler();
		    	}
		    }
		});
		


	      saveMap.setOnAction(new EventHandler<ActionEvent>() {
	         @Override
	         public void handle(ActionEvent event) {
	            FileChooser saveFileChooser = new FileChooser();
	            File saveFile = saveFileChooser.showSaveDialog(null);
	            if (saveFile == null)
	               return;
	            String filepath = saveFile.getPath();

	            JsonObjectBuilder graphJSONObjectBuilder = Json.createObjectBuilder()
	                  .add("filename", filepath);

	            JsonArrayBuilder cellJSONArrayBuilder = Json.createArrayBuilder();
	            for (Cell cell : graph.getModel().getAllCells()) {
	               JsonObjectBuilder cellInfoBuilder = Json.createObjectBuilder()
	                     .add("type", CellType.toInteger(cell.getCellType()))
	                     .add("name", cell.getCellName())
	                     .add("id", cell.getCellID())
	                     .add("x", cell.getLayoutX())
	                     .add("y", cell.getLayoutY());
	               if (cell.getCellType() == CellType.FILE)
	                  cellInfoBuilder.add("path", ((FileCell)cell).getPath());
	               cellJSONArrayBuilder.add(cellInfoBuilder.build());
	            }
	            JsonArrayBuilder edgeJSONArrayBuilder = Json.createArrayBuilder();
	            for (Edge edge : graph.getModel().getAllEdges()) {
	               JsonObjectBuilder edgeInfoBuilder = Json.createObjectBuilder()
	                     .add("src", edge.getSource().getCellID())
	                     .add("dst", edge.getTarget().getCellID());
	               edgeJSONArrayBuilder.add(edgeInfoBuilder.build());
	            }
	            JsonArray cellJSONArray = cellJSONArrayBuilder.build();
	            JsonArray edgeJSONArray = edgeJSONArrayBuilder.build();

	            graphJSONObjectBuilder.add("cells", cellJSONArray);
	            graphJSONObjectBuilder.add("edges", edgeJSONArray);

	            JsonObject graphJSONObject = graphJSONObjectBuilder.build();

	            try {
	               FileWriter fw = new FileWriter(filepath);
	               JsonWriter jsonWriter = Json.createWriter(fw);
	               jsonWriter.writeObject(graphJSONObject);
	               fw.close();
	               jsonWriter.close();
	            } catch (IOException e) {
	               e.printStackTrace();
	            }
	         }
	      });
	      loadMap.setOnAction(new EventHandler<ActionEvent>() {
	         @Override
	         public void handle(ActionEvent event) {
	            FileChooser newFileChooser = new FileChooser();
	            File newFile = newFileChooser.showOpenDialog(null);
	            if (newFile == null)
	               return;
	            String filepath = newFile.getPath();

	            Model model = graph.getModel();

	            graph.beginUpdate();
	            model.getRemovedCells().addAll(model.getAllCells());
	            model.getRemovedEdges().addAll(model.getAllEdges());
	            try {
	               FileReader fr = new FileReader(filepath);
	               JsonReader jsonReader = Json.createReader(fr);
	               JsonObject graphJSONObject = jsonReader.readObject();
	               JsonArray cellJSONArray = graphJSONObject.getJsonArray("cells");
	               JsonArray edgeJSONArray = graphJSONObject.getJsonArray("edges");

	               for (int i=0; i<cellJSONArray.size(); i++) {
	                  JsonObject cellJSONObj = cellJSONArray.getJsonObject(i);
	                  CellType type = CellType.fromInteger(cellJSONObj.getInt("type"));
	                  String name = cellJSONObj.getString("name");
	                  int id = cellJSONObj.getInt("id");
	                  double x = cellJSONObj.getJsonNumber("x").doubleValue();
	                  double y = cellJSONObj.getJsonNumber("y").doubleValue();

	                  Cell cell = addGraphComponents(name, type);
	                  model.getCellMap().remove(String.valueOf(cell.getCellID()));
	                  cell.setCellID(id);
	                  model.getCellMap().put(String.valueOf(id), cell);

	                  cell.setLayoutX(x);
	                  cell.setLayoutY(y);

	                  if (cellJSONObj.containsKey("path"))
	                     ((FileCell)cell).setPath(cellJSONObj.getString("path"));
	               }
	               for (int i=0; i<edgeJSONArray.size(); i++) {
	                  JsonObject edgeJSONObj = edgeJSONArray.getJsonObject(i);
	                  String srcCellID = String.valueOf(edgeJSONObj.getInt("src"));
	                  String dstCellID = String.valueOf(edgeJSONObj.getInt("dst"));

	                  model.addEdge(srcCellID, dstCellID);
	               }
	               fr.close();
	            } catch (FileNotFoundException e) {
	               e.printStackTrace();
	            } catch (IOException e) {
	               e.printStackTrace();
	            }

	            graph.endUpdate();
	         }
	      });
 	}
	
	private void addMergeFile(String path) {
		File txtfile = new File(path);
		Label text = new Label(txtfile.getName());
		text.setStyle("	-fx-pref-width: 70; -fx-pref-height: 30;");
		addallcontents.push(path);
		mergeHBox.getChildren().add(text);
	}

	//���� ����
		 
	private void saveFile(File saveFile) {
		final TextArea htmlCode = new TextArea();
		//TextArea textArea = (TextArea) mainTab.getSelectionModel().getSelectedItem().getContent();
		htmlCode.setText(((HTMLEditor) mainTab.getSelectionModel().getSelectedItem().getContent()).getHtmlText());
	    try{
	      FileWriter writer = null;
	      writer = new FileWriter(saveFile);
	      writer.write(htmlCode.getText().replaceAll("\n", "\r\n"));
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
			final HTMLEditor htmlEditor = new HTMLEditor();
	        htmlEditor.setPrefHeight(245);
	        
			TabSetText n_tab = new TabSetText();
			Tab tab = n_tab.createEditableTab(txtFile.getName());
		    
		    try {
			       // ����Ʈ ������ �����б�
			        String filePath = path; // ��� ����
			        FileInputStream fileStream = null; // ���� ��Ʈ��
			        
			        fileStream = new FileInputStream( filePath );// ���� ��Ʈ�� ����
			        //���� ����
			        byte[ ] readBuffer = new byte[fileStream.available()];
			        while (fileStream.read( readBuffer ) != -1){}
			       
			        htmlEditor.setHtmlText(new String(readBuffer));
			        fileStream.close(); //��Ʈ�� �ݱ�
			    } catch (Exception e) {
				e.getStackTrace();
			    }
		    
		    
		    tab.setContent(htmlEditor);
		    //tabpane ���� �߰������� ���� �������־����� �ڵ����� �� tab���� ������ ������ ��(�̿ϼ�)
		    mainTab.getTabs().add(tab);
		 }
		public void openallfileTab(String path) {
			File dir = new File(path);
			final HTMLEditor htmlEditor = new HTMLEditor();
	        htmlEditor.setPrefHeight(245);
			
			TabSetText n_tab = new TabSetText();
			Tab tab = n_tab.createEditableTab(dir.getName());
			String[] fileNames = dir.list();
			
			for(String fileName : fileNames) {
				System.out.println(fileName);
				File f = new File(dir, fileName);
				if(f.isDirectory()) {
					break;
				}
				try {
					String filePath = path;
			        FileInputStream fileStream = null;
			        fileStream = new FileInputStream( filePath );
			        htmlEditor.setHtmlText(f.getName());
					byte[ ] readBuffer = new byte[fileStream.available()];
					while (fileStream.read( readBuffer ) != -1){}
				    htmlEditor.setHtmlText(new String(readBuffer));
					htmlEditor.setHtmlText("--------------------------------------------------------------------------------------------------------------------------------------------------" + "\n");
			        fileStream.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
			      e.printStackTrace();
			    }
			}
			tab.setContent(htmlEditor);
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
		associatedGrid.getChildren().clear();

		String msg = txtMsg.getText();
		txtMsg.clear();
		Cell relatedCell = addGraphComponents(msg, CellType.LABEL);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//associatedGrid.getChildren().clear();
				try {
					String URL = "https://opendict.korean.go.kr/search/searchResult?focus_name=query&query=";
					URL += msg;

					Document doc = Jsoup.connect(URL).get();
					Elements elem = null;
					for (int i=1; i<5; i++) {
						Elements num = doc.select("#searchPaging > div.section.floatL > div.group.mt30 > ul.panel > li > div > div.search_result > dl:nth-child(" + i + ") > dd:nth-child(2) > a > span.word_no.mr5");
						if (num.text().equals("��001��") == false)
							continue;	
						elem = doc.select("#searchPaging > div.section.floatL > div.group.mt30 > ul.panel > li > div > div.search_result > dl:nth-child(" + i + ") > dd:nth-child(2) > a");
						URL = "https://opendict.korean.go.kr/";
						URL += elem.attr("href");
						break;
					}
					if (elem == null) {
						System.out.println("crawling error");
						return;
					}
						

					doc = Jsoup.connect(URL).get();
					elem = doc.select("div[id=\"wordmap_json_str\"]");

					JsonReader jsonReader = Json.createReader(new StringReader(elem.text()));
					JsonObject jsonObject = jsonReader.readObject();
					JsonArray root = jsonObject.getJsonArray("children");
					
					int gridIdx = 0;
					for (int i=0; i<root.size(); i++) {
						String name = root.getJsonObject(i).getString("name");
						if (name.length() == "����Ѹ�".length() && name.equals("����Ѹ�") == true) {
							JsonArray children = root.getJsonObject(i).getJsonArray("children");
							for (int j=0; j<children.size(); j++) {
								TitledPaneCell cell = new TitledPaneCell(children.getJsonObject(j).getString("name"));
								cell.setGraph(graph);
								cell.setRelatedCell(relatedCell);
								associatedGrid.add(cell, gridIdx++, 0);
								if (gridIdx > 5) break;
							}
							break;
						}
					}
				} catch (Exception e) {
					System.out.println("crawling error");
				}
			}
		});
	}
	
	private Cell addGraphComponents(String name, CellType type) {
        Model model = graph.getModel();

        graph.beginUpdate();
        Cell cell = model.addCell(name, type);
        cell.relocate(mapping.getWidth()/2, mapping.getHeight()/2);
        graph.endUpdate();
        
        return cell;
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
            	filePath = getTreePath(item);
            	
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
