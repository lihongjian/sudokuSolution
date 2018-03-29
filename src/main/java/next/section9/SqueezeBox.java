package next.section9;

import java.util.LinkedList;
import java.util.List;

public class SqueezeBox {

    Cell firstCell;

    List<Cell> allCells = new LinkedList<Cell>();

    int firstCell_length;

    public int getFirstCell_length() {
        return firstCell_length;
    }

    public void setFirstCell_length(int firstCell_length) {
        this.firstCell_length = firstCell_length;
    }

    public Cell getFirstCell() {
        return firstCell;
    }

    public void setFirstCell(Cell firstCell) {
        this.firstCell = firstCell;
    }

    public List<Cell> getAllCells() {
        return allCells;
    }

    public void setAllCells(List<Cell> allCells) {
        this.allCells = allCells;
    }
}
