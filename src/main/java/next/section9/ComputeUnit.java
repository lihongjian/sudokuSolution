package next.section9;

import java.io.Serializable;
import java.util.Arrays;

public class ComputeUnit implements Cloneable,Serializable,Comparable{

    Cell[][] cells;
    int isSureNum;

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public int getIsSureNum() {
        return isSureNum;
    }

    public void setIsSureNum(int isSureNum) {
        this.isSureNum = isSureNum;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int compareTo(Object o) {
        if(o == null) return -1;
        ComputeUnit b = (ComputeUnit)o;
        if(this.isSureNum != b.getIsSureNum()){
            return -1;
        } else {
            for(int i = 0 ; i < 9 ; i++){
                for(int j = 0 ; j < 9 ; j ++){
                    if(!this.getCells()[i][j].getIssure()){
                        if(!Arrays.equals(this.getCells()[i][j].getMaybeSet(),b.getCells()[i][j].getMaybeSet())){
                            return -1;
                        }
                    }
                }
            }
        }
        return 0;
    }
}
