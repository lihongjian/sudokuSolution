package next.section9;

import java.util.Arrays;
import java.util.Map;

public class CheckService {

    public Cell[][] init(Map<String,String> map){
        Cell[][] cells = new Cell[9][9];
        for(int i = 0 ; i < 9 ; i ++){
            for(int j= 0 ; j < 9 ; j ++){
                Cell c = new Cell();
                String key = String.valueOf(i+1) + String.valueOf(j+1);
                if(map.keySet().contains(key)){
                    c.setValue(Integer.parseInt(map.get(key)));
                } else {
                    return null;
                }
                cells[i][j] = c;
            }
        }
        return cells;
    }


    public Boolean check(Map<String, String> condition) {
        Cell[][] cells = init(condition);
        return CellUtil.checkCells(cells);
    }

}
