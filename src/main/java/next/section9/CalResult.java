package next.section9;

public class CalResult {


    Boolean isChanged = Boolean.FALSE;

    Cell[] cells = new Cell[9];

    public Boolean getChanged() {
        return isChanged;
    }

    public void setChanged(Boolean changed) {
        isChanged = changed;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }
}
