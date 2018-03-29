package next.section9;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CellUtil {

    public static int[] initMaybeSet = {1,2,3,4,5,6,7,8,9};

    public static ComputeUnit initCell(Map<String,String> map){
        ComputeUnit cu = new ComputeUnit();
        Cell[][] cells = new Cell[9][9];
        cu.setCells(cells);
        int isSureNum = 0;
        for(int i = 0 ; i < 9 ; i ++){
            Cell[] cellx = new Cell[9];
            for(int j= 0 ; j < 9 ; j ++){
                Cell c = new Cell();
                c.setX(i);
                c.setY(j);
                String key = String.valueOf(i+1) + String.valueOf(j+1);
                if(map.keySet().contains(key)){
                    if(map.get(key).contains(",")){ //设置可能集合
                        String[] maybeSetString = map.get(key).split(",");
                        int[] maybeSetInt = new int[maybeSetString.length];
                        for(int k = 0 ; k < maybeSetString.length ; k++ ){
                            maybeSetInt[k] = Integer.parseInt(maybeSetString[k]);
                        }
                        c.setMaybeSet(maybeSetInt);
                    } else {
                        c.setValue(Integer.parseInt(map.get(key)));
                        c.setIssure(true);
                        isSureNum++;
                    }
                } else {
                    c.setMaybeSet(initMaybeSet);
                }
                cellx[j] = c;
            }
            cells[i] = cellx;
        }
        cu.setIsSureNum(isSureNum);
        return cu;
    }


    public static Map<String,String> cellToMap(Cell[][] cells){
        Map<String,String> resultMap = new HashMap<String, String>();
        for(int i = 0 ; i < cells.length ; i ++){
            for(int j =0 ; j < cells[i].length ; j ++){
                resultMap.put(String.valueOf(i+1) + String.valueOf(j+1),cells[i][j].toString());
            }
        }
        return resultMap;
    }

    /**
     * 深度克隆
     * @param obj
     * @return
     */
    public static Object deepClone(Object obj) {
        Object newObj = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bo);
            oos.writeObject(obj);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            newObj = oi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newObj;
    }

    public static boolean checkCells(Cell[][] cells){
        if(cells == null) return false;
        if(!checkRow(cells)) return false;
        if(!checkColumn(cells)) return false;
        if(!checkBlock(cells)) return false;
        return true;
    }


    private static boolean checkBlock(Cell[][] cell) {
        for(int b = 0 ; b < 9 ; b ++){
            int[] blockValue = new int[9];
            int num = 0;
            for(int i = (b%3)*3; i < (b%3)*3+3 ; i ++) {
                for(int j = b/3 *3  ; j < (b/3 + 1) * 3 ; j++ ){
                    blockValue[num] = cell[i][j].getValue();
                    num++;
                }
            }
            if(AlgorithmUtil.getRestSet(CellUtil.initMaybeSet,blockValue).length != 0){
                return false;
            }
        }
        return true;
    }

    private  static boolean checkColumn(Cell[][] cell) {
        for(int i= 0 ; i < 9 ; i++ ){
            int[] columnValue = new int[9];
            for(int j = 0 ;j < 9 ; j++){
                columnValue[j] = cell[i][j].getValue();
            }
            if(AlgorithmUtil.getRestSet(CellUtil.initMaybeSet,columnValue).length != 0){
                return false;
            }
        }
        return true;
    }

    private static boolean checkRow(Cell[][] cell) {
        for(int i = 0 ; i < 9 ; i ++ ){
            int[] rowValue = new int[9];
            for(int j = 0 ;j < 9 ; j++){
                rowValue[j] = cell[j][i].getValue();
            }
            if(AlgorithmUtil.getRestSet(CellUtil.initMaybeSet,rowValue).length != 0){
                return false;
            }
        }
        return true;
    }
}
