package test;

import application.PathItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.*;
import test.Graph;


public class Model {

	Graph graph;
    Cell graphParent;

    List<Cell> allCells;
    List<Cell> addedCells;
    List<Cell> removedCells;

    List<Edge> allEdges;
    List<Edge> addedEdges;
    List<Edge> removedEdges;

    Map<String,Cell> cellMap; // <id,cell>

    public Model(Graph graph) {
         graphParent = new Cell( "_ROOT_");
         this.graph = graph;
         // clear model, create lists
         clear();
    }

    public void clear() {
        allCells = new ArrayList<>();
        addedCells = new ArrayList<>();
        removedCells = new ArrayList<>();

        allEdges = new ArrayList<>();
        addedEdges = new ArrayList<>();
        removedEdges = new ArrayList<>();

        cellMap = new HashMap<>(); // <id,cell>
    }

    public void clearAddedLists() {
        addedCells.clear();
        addedEdges.clear();
    }

    public List<Cell> getAddedCells() {
        return addedCells;
    }
    public List<Cell> getRemovedCells() {
        return removedCells;
    }
    public List<Cell> getAllCells() {
        return allCells;
    }
    public List<Edge> getAddedEdges() {
        return addedEdges;
    }
    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }
    public List<Edge> getAllEdges() {
        return allEdges;
    }
    public Map<String, Cell> getCellMap() {
    	return cellMap;
    }

    public Cell addCell(String id, CellType type) {
        Cell newcell;

        switch (type) {
        case RECTANGLE:
            newcell = new RectangleCell(id);
            break;
        case TRIANGLE:
            newcell = new TriangleCell(id);
            break;
        case LABEL:
            newcell = new LabelCell(id);
            break;
        case FILE:
            newcell = new FileCell(id);
            break;
        case IMAGE:
            newcell = new ImageCell(id);
            break;
        case BUTTON:
            newcell = new ButtonCell(id);
            break;
        case TITLEDPANE:
            newcell = new TitledPaneCell(id);
            break;
        default:
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
        addCell(newcell);
        
        return newcell;
    }





    private void addCell( Cell cell) {
        cell.setGraph(this.graph);
        cell.setCellMenu(new CellMenu(cell));

        addedCells.add(cell);
        cellMap.put(String.valueOf( cell.getCellID()), cell);
    }

    public void addEdge( String sourceId, String targetId) {
        Cell sourceCell = cellMap.get( sourceId);
        Cell targetCell = cellMap.get( targetId);

        for (Cell _cell : sourceCell.linkedCells) {
            if (targetCell.equals(_cell))
                return;
        }

        Edge edge = new Edge( sourceCell, targetCell);

        addedEdges.add(edge);
        sourceCell.linkedCells.add(targetCell);
        targetCell.linkedCells.add(sourceCell);
    }

    public void removeCell( Cell cell) {
        removedCells.add(cell);

        for (Cell _cell : allCells) {
            if (cell == _cell)
                continue;
            removeEdge(String.valueOf(cell.getCellID()), String.valueOf(_cell.getCellID()));
        }

        cellMap.remove(String.valueOf(cell.getCellID()));
    }

    public void removeEdge( String sourceId, String targetId) {
        Cell sourceCell = cellMap.get( sourceId);
        Cell targetCell = cellMap.get( targetId);

        if (!sourceCell.linkedCells.contains(targetCell))
            return;

        sourceCell.linkedCells.remove(targetCell);
        targetCell.linkedCells.remove(sourceCell);

        for (Edge _edge : allEdges) {
            if ((_edge.source == sourceCell && _edge.target == targetCell) ||
                    (_edge.source == targetCell && _edge.target == sourceCell)) {
                
                removedEdges.add(_edge);
                break;
            }
        }
    }
    /**
     * Attach all cells which don't have a parent to graphParent 
     * @param cellList
     */
    public void attachOrphansToGraphParent( List<Cell> cellList) {
        for( Cell cell: cellList) {
            if( cell.getCellParents().size() == 0) {
                graphParent.addCellChild( cell);
            }
        }
    }

    /**
     * Remove the graphParent reference if it is set
     * @param cellList
     */
    public void disconnectFromGraphParent( List<Cell> cellList) {
        for( Cell cell: cellList) {
            graphParent.removeCellChild( cell);
        }
    }

    public void merge() {
        // cells
        allCells.addAll( addedCells);
        allCells.removeAll( removedCells);

        addedCells.clear();
        removedCells.clear();

        // edges
        allEdges.addAll( addedEdges);
        allEdges.removeAll( removedEdges);

        addedEdges.clear();
        removedEdges.clear();
    }
}