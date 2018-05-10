package next.section9;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CalculateService {

    public Map<String, String> calculate(Map<String, String> condition) throws Exception {

        ComputeUnit cu = CellUtil.initCell(condition);
        long a1 = System.currentTimeMillis();
        int times = 0 ;
        while (cu.getIsSureNum() < 81) {
            CalResult cr = new CalResult();
            times++;
            for (int i = 0; i < 9; i++) {
                Cell[] cellx = cu.getCells()[i];
                for (int j = 0; j < 9; j++) {
                    Cell c = cellx[j];
                    if (!c.getIssure()) {
                        if (calRowMybeSet(c, i, j, cu , cr) || calColMybeSet(c, i, j, cu, cr) || calBlockMybeSet(c, i, j, cu,cr)) {
                        }
                    }
                }
            }
            squeezeMaybeSet(cu,cr); //进行挤压操作
            doLinkageCompute(cu,cr); //进行联动操作
            if(!cr.getChanged()){
                break;
            }
            cr = new CalResult();
        }
        long a2 = System.currentTimeMillis();
        System.out.println(  " First cost :" + (a2 - a1) + "  times: " + times);
        if (cu.getIsSureNum() < 81) {
            doExhaustionCompute(cu); //穷举操作
        }
        long a3 = System.currentTimeMillis();
        System.out.println(  " Second cost :" + (a3 - a2));
        return CellUtil.cellToMap(cu.getCells());
    }

    /**
     * 穷举计算
     * @param cu
     * @throws Exception
     */
    private void doExhaustionCompute(ComputeUnit cu) throws Exception {
        Cell exhaustCell_orign = getExhaustionCell(cu.getCells());
        for (int i = 0; i < exhaustCell_orign.getMaybeSet().length; i++) {
            ComputeUnit cu_sub = (ComputeUnit) CellUtil.deepClone(cu);
            Cell exhaustCell = cu_sub.getCells()[exhaustCell_orign.getX()][exhaustCell_orign.getY()];
            int value = exhaustCell_orign.getMaybeSet()[i];
            exhaustCell.setValue(value);
            exhaustCell.setIssure(true);
            exhaustCell.setMaybeSet(null);
            cu_sub.setIsSureNum(cu_sub.getIsSureNum() + 1);
            try {
                if (exhaustionCalculate(cu_sub)) {
                    cu.setIsSureNum(cu_sub.getIsSureNum());
                    cu.setCells(cu_sub.getCells());
                    return;
                }
            } catch (CalErrorException e) {

            }
        }
        throw new CalErrorException();
    }

    private Cell getExhaustionCell(Cell[][] cells) {
        int minMayBenum = 10;
        Cell exhaustionCell = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!cells[i][j].getIssure()) {
                    if (cells[i][j].getMaybeSet().length < minMayBenum) {
                        exhaustionCell = cells[i][j];
                        minMayBenum = exhaustionCell.getMaybeSet().length;
                    }
                }
            }
        }
        return exhaustionCell;
    }

    /**
     * @param cu
     * @return 是否运算完成
     */
    private boolean exhaustionCalculate(ComputeUnit cu) throws Exception {
        while (cu.getIsSureNum() < 81) {
            CalResult cr = new CalResult();
            for (int i = 0; i < 9; i++) {
                Cell[] cellx = cu.getCells()[i];
                for (int j = 0; j < 9; j++) {
                    Cell c = cellx[j];
                    if (!c.getIssure()) {
                        if (calRowMybeSet(c, i, j, cu , cr) || calColMybeSet(c, i, j, cu, cr) || calBlockMybeSet(c, i, j, cu,cr)) {
                        }
                    }
                }
            }
            squeezeMaybeSet(cu,cr); //进行挤压操作，有bug
            doLinkageCompute(cu,cr); //进行联动操作
            if(!cr.getChanged()){
                break;
            }
            cr = new CalResult();
        }

        if (cu.getIsSureNum() < 81) {
            Cell exhaustCell_orign = getExhaustionCell(cu.getCells());
            for (int i = 0; i < exhaustCell_orign.getMaybeSet().length; i++) {
                ComputeUnit cu_sub = (ComputeUnit) CellUtil.deepClone(cu);
                Cell exhaustCell = cu_sub.getCells()[exhaustCell_orign.getX()][exhaustCell_orign.getY()];
                int value = exhaustCell_orign.getMaybeSet()[i];
                exhaustCell.setValue(value);
                exhaustCell.setIssure(true);
                cu_sub.setIsSureNum(cu_sub.getIsSureNum() + 1);
                try {
                    if (exhaustionCalculate(cu_sub)) {
                        cu.setIsSureNum(cu_sub.getIsSureNum());
                        cu.setCells(cu_sub.getCells());
                        return true;
                    }
                } catch (CalErrorException aa) {
                }
            }
        } else {
            return CellUtil.checkCells(cu.getCells());
        }
        return false;
    }

    /**
     *  关联计算
     * @param cu
     * @param cr
     */
    private void doLinkageCompute(ComputeUnit cu, CalResult cr) {
        rowLinkageCompute(cu,cr);
        colLinkageCompute(cu,cr);
        blockLinkageCompute(cu,cr);
    }

    /**
     * 块关联计算
     * @param cu
     * @param cr
     */
    private void blockLinkageCompute(ComputeUnit cu, CalResult cr) {
        for (int b = 0; b < 9; b++) {
            Cell[] blockSet = new Cell[9];
            int num = 0;
            for (int i = (b % 3) * 3; i < (b % 3) * 3 + 3; i++) {
                for (int j = b / 3 * 3; j < (b / 3 + 1) * 3; j++) {
                    blockSet[num] = cu.getCells()[i][j];
                    num++;
                }
            }
            blockLinkageComputeRow(blockSet,cu,cr);
            blockLinkageComputeCol(blockSet,cu,cr);
        }
    }

    /**
     * 块关联 对列计算
     * @param blockSet
     * @param cu
     * @param cr
     */
    private void blockLinkageComputeCol(Cell[] blockSet,ComputeUnit cu, CalResult cr) {
        Cell[] col1 = new Cell[3];
        System.arraycopy(blockSet, 0, col1, 0, 3);
        Cell[] col2 = new Cell[3];
        System.arraycopy(blockSet, 3, col2, 0, 3);
        Cell[] col3 = new Cell[3];
        System.arraycopy(blockSet, 6, col3, 0, 3);
        int[] maybeCol1 = getMaybeSet(col1);
        int[] maybeCol2 = getMaybeSet(col2);
        int[] maybeCol3 = getMaybeSet(col3);
        int[] complementarySetCol1 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol1, AlgorithmUtil.getUnionSet(maybeCol2, maybeCol3)), getMaybeSet(col1));
        if (complementarySetCol1.length > 1) {
            linkageComputeBlock2Col(col1[0].getX(), col1[0].getY(), complementarySetCol1, cu,cr);
        }
        int[] complementarySetCol2 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol2, AlgorithmUtil.getUnionSet(maybeCol1, maybeCol3)), getMaybeSet(col2));
        if (complementarySetCol2.length > 1) {
            linkageComputeBlock2Col(col2[0].getX(), col2[0].getY(), complementarySetCol2, cu,cr);
        }
        int[] complementarySetCol3 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol3, AlgorithmUtil.getUnionSet(maybeCol1, maybeCol2)), getMaybeSet(col3));
        if (complementarySetCol3.length > 1) {
            linkageComputeBlock2Col(col3[0].getX(), col3[0].getY(), complementarySetCol3, cu,cr);
        }


    }
    /**
     * 块关联 对行计算
     * @param blockSet
     * @param cu
     * @param cr
     */
    private void blockLinkageComputeRow(Cell[] blockSet,ComputeUnit cu, CalResult cr) {
        Cell[] row1 = new Cell[3];
        Cell[] row2 = new Cell[3];
        Cell[] row3 = new Cell[3];
        for(int i= 0 ; i < 3 ; i ++ ){
            row1[i] = blockSet[i*3+0];
            row2[i] = blockSet[i*3+1];
            row3[i] = blockSet[i*3+2];
        }
        int[] maybeRow1 = getMaybeSet(row1);
        int[] maybeRow2 = getMaybeSet(row2);
        int[] maybeRow3 = getMaybeSet(row3);
        int[] complementarySetRow1 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow1, AlgorithmUtil.getUnionSet(maybeRow2, maybeRow3)), getMustBeSet(row1));
        if (complementarySetRow1.length > 0) {
            linkageComputeBlock2Row(row1[0].getX(), row1[0].getY(), complementarySetRow1, cu,cr);
        }
        int[] complementarySetRow2 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow2, AlgorithmUtil.getUnionSet(maybeRow1, maybeRow3)), getMustBeSet(row2));
        if (complementarySetRow2.length > 0) {
            linkageComputeBlock2Row(row2[0].getX(), row2[0].getY(), complementarySetRow2, cu,cr);
        }
        int[] complementarySetRow3 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow3, AlgorithmUtil.getUnionSet(maybeRow1, maybeRow2)), getMustBeSet(row3));
        if (complementarySetRow3.length > 0) {
            linkageComputeBlock2Row(row3[0].getX(), row3[0].getY(), complementarySetRow3, cu,cr);
        }


    }
    /**
     * 列关联计算
     * @param cu
     * @param cr
     */
    private void colLinkageCompute(ComputeUnit cu, CalResult cr) {
        for (int i = 0; i < 9; i++) {
            Cell[] colSet = cu.getCells()[i];
            Cell[] col1 = new Cell[3];
            System.arraycopy(colSet, 0, col1, 0, 3);
            Cell[] col2 = new Cell[3];
            System.arraycopy(colSet, 3, col2, 0, 3);
            Cell[] col3 = new Cell[3];
            System.arraycopy(colSet, 6, col3, 0, 3);
            int[] maybeCol1 = getMaybeSet(col1);
            int[] maybeCol2 = getMaybeSet(col2);
            int[] maybeCol3 = getMaybeSet(col3);
            int[] complementarySetCol1 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol1, AlgorithmUtil.getUnionSet(maybeCol2, maybeCol3)), getMaybeSet(col1));
            if (complementarySetCol1.length > 1) {
                linkageComputeCol2Block(i, 0, complementarySetCol1, cu,cr);
            }
            int[] complementarySetCol2 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol2, AlgorithmUtil.getUnionSet(maybeCol1, maybeCol3)), getMaybeSet(col2));
            if (complementarySetCol2.length > 1) {
                linkageComputeCol2Block(i, 3, complementarySetCol2, cu,cr);
            }
            int[] complementarySetCol3 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol3, AlgorithmUtil.getUnionSet(maybeCol1, maybeCol2)), getMaybeSet(col3));
            if (complementarySetCol3.length > 1) {
                linkageComputeCol2Block(i, 6, complementarySetCol3, cu,cr);
            }
        }
    }

    /**
     * 行关联计算
     * @param cu
     * @param cr
     */
    private void rowLinkageCompute(ComputeUnit cu, CalResult cr) {
        for (int i = 0; i < 9; i++) {
            Cell[] rowSet = new Cell[9];
            for (int j = 0; j < 9; j++) {
                rowSet[j] = cu.getCells()[j][i];
            }
            Cell[] row1 = new Cell[3];
            System.arraycopy(rowSet, 0, row1, 0, 3);
            Cell[] row2 = new Cell[3];
            System.arraycopy(rowSet, 3, row2, 0, 3);
            Cell[] row3 = new Cell[3];
            System.arraycopy(rowSet, 6, row3, 0, 3);
            int[] maybeRow1 = getMaybeSet(row1);
            int[] maybeRow2 = getMaybeSet(row2);
            int[] maybeRow3 = getMaybeSet(row3);
            int[] complementarySetRow1 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow1, AlgorithmUtil.getUnionSet(maybeRow2, maybeRow3)), getMustBeSet(row1));
            if (complementarySetRow1.length > 0) {
                linkageComputeRow2Block(0, i, complementarySetRow1, cu,cr);
            }
            int[] complementarySetRow2 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow2, AlgorithmUtil.getUnionSet(maybeRow1, maybeRow3)), getMustBeSet(row2));
            if (complementarySetRow2.length > 0) {
                linkageComputeRow2Block(3, i, complementarySetRow2, cu,cr);
            }
            int[] complementarySetRow3 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow3, AlgorithmUtil.getUnionSet(maybeRow1, maybeRow2)), getMustBeSet(row3));
            if (complementarySetRow3.length > 0) {
                linkageComputeRow2Block(6, i, complementarySetRow3, cu,cr);
            }
        }
    }
    /**
     * 根据行关联出的结果集 对相关块进行修改
     * @param x
     * @param y
     * @param mustNotBeSet
     * @param cu
     * @param cr
     */
    private void linkageComputeRow2Block(int x, int y, int[] mustNotBeSet, ComputeUnit cu, CalResult cr) {
        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (j != y) {
                    Cell c = cu.getCells()[i][j];
                    if (!c.getIssure()) {
                        int[] newMaybeSet = AlgorithmUtil.getComplementarySet(c.getMaybeSet(), mustNotBeSet);
                        if(newMaybeSet.length != c.getMaybeSet().length){
                            cr.setChanged(true);
                            c.setMaybeSet(newMaybeSet);
                        }
                        if (c.checkIssure()) {
                            cu.setIsSureNum(cu.getIsSureNum() + 1);
                            notifyXYBlock(i, j, cu,cr);
                        }
                    }
                }
            }
        }
    }
    /**
     * 根据块关联出的结果集 对相关行进行修改
     * @param x
     * @param y
     * @param mustNotBeSet
     * @param cu
     * @param cr
     */
    private void linkageComputeBlock2Row(int x, int y, int[] mustNotBeSet, ComputeUnit cu, CalResult cr) {
        for (int i = 0 ; i < 9 ; i++) {
                if (i < x || i >= x+3) {
                    Cell c = cu.getCells()[i][y];
                    if (!c.getIssure()) {
                        int[] newMaybeSet = AlgorithmUtil.getComplementarySet(c.getMaybeSet(), mustNotBeSet);
                        if(newMaybeSet.length != c.getMaybeSet().length){
                            cr.setChanged(true);
                            c.setMaybeSet(newMaybeSet);
                        }
                        if (c.checkIssure()) {
                            cu.setIsSureNum(cu.getIsSureNum() + 1);
                            notifyXYBlock(i, y, cu,cr);
                        }
                    }
                }
        }
    }
    /**
     * 根据块关联出的结果集 对相关列进行修改
     * @param x
     * @param y
     * @param mustNotBeSet
     * @param cu
     * @param cr
     */
    private void linkageComputeBlock2Col(int x, int y, int[] mustNotBeSet, ComputeUnit cu, CalResult cr) {
            for (int j = 0 ; j < 9 ; j ++) {
                if (j < y || j >= y+3  ) {
                    Cell c = cu.getCells()[x][j];
                    if (!c.getIssure()) {
                        int[] newMaybeSet = AlgorithmUtil.getComplementarySet(c.getMaybeSet(), mustNotBeSet);
                        if(newMaybeSet.length != c.getMaybeSet().length){
                            cr.setChanged(true);
                            c.setMaybeSet(newMaybeSet);
                        }
                        if (c.checkIssure()) {
                            cu.setIsSureNum(cu.getIsSureNum() + 1);
                            notifyXYBlock(x, j, cu,cr);
                        }
                    }
                }
            }
    }
    /**
     * 根据列关联出的结果集 对相关块进行修改
     * @param x
     * @param y
     * @param mustNotBeSet
     * @param cu
     * @param cr
     */
    private void linkageComputeCol2Block(int x, int y, int[] mustNotBeSet, ComputeUnit cu, CalResult cr) {
        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (i != x) {
                    Cell c = cu.getCells()[i][j];
                    if (!c.getIssure()) {
                        int[] newMaybeSet = AlgorithmUtil.getComplementarySet(c.getMaybeSet(), mustNotBeSet);
                        if(newMaybeSet.length != c.getMaybeSet().length){
                            cr.setChanged(true);
                            c.setMaybeSet(newMaybeSet);
                        }
                        if (c.checkIssure()) {
                            cu.setIsSureNum(cu.getIsSureNum() + 1);
                            notifyXYBlock(i, j, cu,cr);
                        }
                    }
                }
            }
        }
    }

    /**
     * 进行事件通知
     * @param x
     * @param y
     * @param cu
     * @param cr
     */
    private void notifyXYBlock(int x, int y, ComputeUnit cu, CalResult cr) {
        notifyX(x, y, cu,cr);
        notifyY(x, y, cu,cr);
        notifyBlock(x, y, cu,cr);
    }
    /**
     * 进行事件通知
     * @param x
     * @param y
     * @param cu
     * @param cr
     */
    private void notifyX(int x, int y, ComputeUnit cu, CalResult cr) {
        for (int i = 0; i < 9; i++) {
            if (!cu.getCells()[i][y].getIssure()) {
                calRowMybeSet(cu.getCells()[i][y], i, y, cu,cr);
            }
        }
    }
    /**
     * 进行事件通知
     * @param x
     * @param y
     * @param cu
     * @param cr
     */
    private void notifyY(int x, int y, ComputeUnit cu, CalResult cr) {
        for (int i = 0; i < 9; i++) {
            if (!cu.getCells()[x][i].getIssure()) {
                calColMybeSet(cu.getCells()[x][i], x, i, cu,cr);
            }
        }
    }

    private void notifyBlock(int x, int y, ComputeUnit cu, CalResult cr) {
        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (!cu.getCells()[i][j].getIssure()) {
                    calBlockMybeSet(cu.getCells()[i][j], i, j, cu,cr);
                }
            }
        }
    }

    private int[] getMaybeSet(Cell[] cells) {
        int[] result = new int[0];
        for (Cell c : cells) {
            if (!c.getIssure()) {
                result = AlgorithmUtil.getUnionSet(result, c.getMaybeSet());
            } else {
                result = AlgorithmUtil.getUnionSet(result, c.getValue());
            }
        }
        return result;
    }

    private int[] getMustBeSet(Cell[] cells) {
        int[] result = new int[0];
        for (Cell c : cells) {
            if (c.getIssure()) {
                result = AlgorithmUtil.getUnionSet(result, c.getValue());
            }
        }
        return result;
    }

    /**
     * 挤压操作
     * @param cu
     * @param cr
     */
    private void squeezeMaybeSet(ComputeUnit cu, CalResult cr) {
        squeezeRowMaybeSet(cu,cr);
        squeezeColMaybeSet(cu,cr);
        squeezeBlockMaybeSet(cu,cr);
    }

    /**
     * 行挤压
     */
    private void squeezeRowMaybeSet(ComputeUnit cu, CalResult cr) {
        for (int i = 0; i < 9; i++) {
            Cell[] squeezeSet = new Cell[9];
            for (int j = 0; j < 9; j++) {
                squeezeSet[j] = cu.getCells()[j][i];
            }
            doSqueezeNew(squeezeSet, cu,  cr);
        }
    }

    /**
     * 列挤压
     */
    private void squeezeColMaybeSet(ComputeUnit cu, CalResult cr) {
        for (int i = 0; i < 9; i++) {
            doSqueezeNew(cu.getCells()[i], cu,  cr);
        }
    }

    /**
     * 块挤压
     */
    private void squeezeBlockMaybeSet(ComputeUnit cu, CalResult cr) {
        for (int b = 0; b < 9; b++) {
            Cell[] squeezeSet = new Cell[9];
            int num = 0;
            for (int i = (b % 3) * 3; i < (b % 3) * 3 + 3; i++) {
                for (int j = b / 3 * 3; j < (b / 3 + 1) * 3; j++) {
                    squeezeSet[num] = cu.getCells()[i][j];
                    num++;
                }
            }
            doSqueezeNew(squeezeSet, cu,cr);
        }
    }

    /**
     * 进行挤压 正向挤压和逆向挤压
     *
     * @param cells
     */
    private void doSqueeze(Cell[] cells, ComputeUnit cu, CalResult cr) {
        int num = 0;
        Cell[] squeezeInitSet = new Cell[9];
        for (Cell c : cells) {
            if (!c.getIssure()) {
                squeezeInitSet[num] = c;
                num++;
            }
        }
        if (num != 9) {
            Cell[] squeezeSet = new Cell[num];
            System.arraycopy(squeezeInitSet, 0, squeezeSet, 0, num);
            List<SqueezeBox> allboxes = new LinkedList<SqueezeBox>();
            for (int i = 0; i < squeezeSet.length; i++) {
                Cell c = squeezeSet[i];
                if (c.getMaybeSet().length < num) {
                    SqueezeBox sb = new SqueezeBox();
                    sb.setFirstCell(c);
                    sb.setFirstCell_length(c.getMaybeSet().length);
                    sb.getAllCells().add(c);
                    Iterator<SqueezeBox> it = allboxes.iterator();
                    while (it.hasNext()) {
                        SqueezeBox sb_last = it.next();
                        if (sb_last.getFirstCell().getMaybeSet().length < c.getMaybeSet().length) continue;
                        if (AlgorithmUtil.isContainsOrBeContrained(sb_last.getFirstCell().getMaybeSet(), c.getMaybeSet())) {
                            if (sb_last.getFirstCell().getMaybeSet().length >= c.getMaybeSet().length) {
                                sb_last.getAllCells().add(c);
/*
                                if (sb_last.getAllCells().size() == sb_last.getFirstCell().getMaybeSet().length) {
*/
                                if (sb_last.getAllCells().size() == sb_last.getFirstCell_length()) {
                                    for (Cell c_reCal : squeezeSet) {
                                        if (!sb_last.getAllCells().contains(c_reCal)) {
                                            c_reCal.setMaybeSet(AlgorithmUtil.getComplementarySet(c_reCal.getMaybeSet(), sb_last.getFirstCell().getMaybeSet()));
                                            if (c_reCal.checkIssure()) {
                                                cu.setIsSureNum(cu.getIsSureNum() + 1);
                                            }
                                        }
                                    }
                                    break;
                                }
                            } else {
                                sb.getAllCells().add(sb_last.getFirstCell());
/*
                                if (sb.getAllCells().size() == sb.getFirstCell().getMaybeSet().length) {
*/
                                if (sb.getAllCells().size() == sb.getFirstCell_length()) {
                                    for (Cell c_reCal : squeezeSet) {
                                        if (!sb.getAllCells().contains(c_reCal)) {
                                            c_reCal.setMaybeSet(AlgorithmUtil.getComplementarySet(c_reCal.getMaybeSet(), sb.getFirstCell().getMaybeSet()));
                                            if (c_reCal.checkIssure()) {
                                                cu.setIsSureNum(cu.getIsSureNum() + 1);
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    allboxes.add(sb);
                }
            }
        }
    }

    /**
     * 新的挤压方法，使用分治策略
     * @param cells
     * @param cu
     * @param cr
     */
    private void doSqueezeNew(Cell[] cells, ComputeUnit cu ,CalResult cr) {
        int num = 0;
        Cell[] squeezeInitSet = new Cell[9];
        for (Cell c : cells) {
            if (!c.getIssure()) {
                squeezeInitSet[num] = c;
                num++;
            }
        }
        CalResult calResult = new CalResult();
        Cell[] squeezeSet = new Cell[num];
        System.arraycopy(squeezeInitSet, 0, squeezeSet, 0, num);
        squeezeRecursively(squeezeSet,cu,calResult);
    }

    /**
     * 递归使用分治策略挤压
     * @param cells
     * @param cu
     * @param calResult
     */
    public void squeezeRecursively(Cell[] cells, ComputeUnit cu, CalResult calResult) {
        int num = cells.length;
        if(num > 1){
            Cell[] innerCells = new Cell[9];
            Cell[] outerCells = new Cell[9];
            List<SqueezeBox> allboxes = new LinkedList<SqueezeBox>();
            for (int i = 0; i < num; i++) {
                Cell c = cells[i];
                if(c == null){
                    break;
                }
                SqueezeBox sb = new SqueezeBox();
                sb.setFirstCell(c);
                sb.getAllCells().add(c);
                if(c.getIssure()){
                    int outlength = modifyAndSplitCells(cells, sb, outerCells, innerCells, cu, calResult);
                    Cell[] newOutCells = new Cell[outlength];
                    System.arraycopy(outerCells,0,newOutCells,0,outlength);
                    squeezeRecursively(newOutCells, cu, calResult);
                    return;
                } else if (c.getMaybeSet().length < num) {
                    Iterator<SqueezeBox> it = allboxes.iterator();
                    while (it.hasNext()) {
                        SqueezeBox sb_last = it.next();
                        if (sb_last.getFirstCell().getMaybeSet().length < c.getMaybeSet().length) continue;
                        if (AlgorithmUtil.isContainsOrBeContrained(sb_last.getFirstCell().getMaybeSet(), c.getMaybeSet())) {
                            if (sb_last.getFirstCell().getMaybeSet().length >= c.getMaybeSet().length) {
                                sb_last.getAllCells().add(c);
                                if (sb_last.getAllCells().size() == sb_last.getFirstCell().getMaybeSet().length) {
                                    int outlength = modifyAndSplitCells(cells, sb_last, outerCells, innerCells, cu, calResult);
                                    Cell[] newOutCells = new Cell[outlength];
                                    System.arraycopy(outerCells,0,newOutCells,0,outlength);
                                    Cell[] newInCells = new Cell[num - outlength];
                                    System.arraycopy(innerCells,0,newInCells,0,num - outlength);
                                    squeezeRecursively(newOutCells, cu, calResult);
                                    squeezeRecursively(newInCells, cu, calResult);
                                    return;
                                }
                            } else {
                                sb.getAllCells().add(sb_last.getFirstCell());
                                if (sb.getAllCells().size() == sb.getFirstCell().getMaybeSet().length) {
                                    int outlength = modifyAndSplitCells(cells, sb, outerCells, innerCells, cu, calResult);
                                    Cell[] newOutCells = new Cell[outlength];
                                    System.arraycopy(outerCells,0,newOutCells,0,outlength);
                                    Cell[] newInCells = new Cell[num - outlength];
                                    System.arraycopy(innerCells,0,newInCells,0,num - outlength);
                                    squeezeRecursively(newOutCells, cu, calResult);
                                    squeezeRecursively(newInCells, cu, calResult);
                                    return;
                                }
                            }
                        }
                    }
                    allboxes.add(sb);
                }
            }
        }

    }

    /**
     * 针对挤压后的结果，进行分治，并修改结果
     * @param cells
     * @param sb_last
     * @param outerCells
     * @param innerCells
     * @param cu
     * @param calResult
     * @return
     */
    private int modifyAndSplitCells(Cell[] cells, SqueezeBox sb_last, Cell[] outerCells, Cell[] innerCells, ComputeUnit cu,  CalResult calResult) {
        int ic = 0;
        int oc = 0;
        for (Cell c_reCal : cells) {
            if(c_reCal == null){
                break;
            }
            if (!sb_last.getAllCells().contains(c_reCal)) {
                int[] new_maybeSet = AlgorithmUtil.getComplementarySet(c_reCal.getMaybeSet(), sb_last.getFirstCell().getMaybeSet());
                if (c_reCal.getMaybeSet().length != new_maybeSet.length) {
                    calResult.setChanged(true);
                    c_reCal.setMaybeSet(new_maybeSet);
                }
                if (c_reCal.checkIssure()) {
                    cu.setIsSureNum(cu.getIsSureNum() + 1);
                }
                outerCells[oc++] = c_reCal;
            } else {
                innerCells[ic++] = c_reCal;
            }
        }
        return oc;
    }

    /**
     * @param c
     * @param x
     * @param y
     * @return
     */
    private boolean calRowMybeSet(Cell c, int x, int y, ComputeUnit cu,CalResult cr) {
        int[] excludedInitSet = new int[9];
        int[] excludedMaybeSet = new int[0];
        int excluededNum = 0;
        for (int i = 0; i < 9; i++) {
            if (i != x) {
                Cell c_other = cu.getCells()[i][y];
                if (c_other.getIssure()) {
                    excludedInitSet[excluededNum] = c_other.getValue();
                    excluededNum++;
                }
                if (excludedMaybeSet.length < 9) {
                    if (c_other.getIssure()) {
                        excludedMaybeSet = AlgorithmUtil.getUnionSet(excludedMaybeSet, c_other.getValue());
                    } else {
                        excludedMaybeSet = AlgorithmUtil.getUnionSet(excludedMaybeSet, c_other.getMaybeSet());
                    }
                }
            }
        }
        if (excludedMaybeSet.length < 8) {
            throw new CalErrorException();
        }
        if (excludedMaybeSet.length < 9) {
            int[] includedMaybeSet = AlgorithmUtil.getRestSet(CellUtil.initMaybeSet, excludedMaybeSet);
            if (includedMaybeSet.length == 1) {
                cr.setChanged(true);
                c.setIssure(true);
                c.setValue(includedMaybeSet[0]);
                cu.setIsSureNum(cu.getIsSureNum() + 1);
                return true;
            }
        }
        int[] excludedSet = new int[excluededNum];
        System.arraycopy(excludedInitSet, 0, excludedSet, 0, excluededNum);
        int[] newMayBeSet = AlgorithmUtil.getRestSet(c.getMaybeSet(), excludedSet);
        if(newMayBeSet.length != c.getMaybeSet().length){
            cr.setChanged(true);
            c.setMaybeSet(newMayBeSet);
        }
        if (c.checkIssure()) {
            cu.setIsSureNum(cu.getIsSureNum() + 1);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算列可能数值，排除可能值
     * @param c
     * @param x
     * @param y
     * @param cu
     * @param cr
     * @return
     */
    private boolean calColMybeSet(Cell c, int x, int y, ComputeUnit cu,CalResult cr) {
        int[] excludedInitSet = new int[9];
        int[] excludedMaybeSet = new int[0];
        int excluededNum = 0;
        for (int j = 0; j < 9; j++) {
            if (j != y) {
                Cell c_other = cu.getCells()[x][j];
                if (c_other.getIssure()) {
                    excludedInitSet[excluededNum] = c_other.getValue();
                    excluededNum++;
                }
                if (excludedMaybeSet.length < 9) {
                    if (c_other.getIssure()) {
                        excludedMaybeSet = AlgorithmUtil.getUnionSet(excludedMaybeSet, c_other.getValue());
                    } else {
                        excludedMaybeSet = AlgorithmUtil.getUnionSet(excludedMaybeSet, c_other.getMaybeSet());
                    }
                }
            }
        }
        if (excludedMaybeSet.length < 9) {
            int[] includedMaybeSet = AlgorithmUtil.getRestSet(CellUtil.initMaybeSet, excludedMaybeSet);
            if (includedMaybeSet.length == 1) {
                cr.setChanged(true);
                c.setIssure(true);
                c.setValue(includedMaybeSet[0]);
                cu.setIsSureNum(cu.getIsSureNum() + 1);
                return true;
            }
            if (includedMaybeSet.length < 1) {
                throw new CalErrorException();
            }
        }
        int[] excludedSet = new int[excluededNum];
        System.arraycopy(excludedInitSet, 0, excludedSet, 0, excluededNum);
        int[] newMayBeSet = AlgorithmUtil.getRestSet(c.getMaybeSet(), excludedSet);
        if(newMayBeSet.length != c.getMaybeSet().length){
            cr.setChanged(true);
            c.setMaybeSet(newMayBeSet);
        }
        if (c.checkIssure()) {
            cu.setIsSureNum(cu.getIsSureNum() + 1);
            return true;
        } else {
            return false;
        }
    }
    /**
     * 计算块可能数值，排除可能值
     * @param c
     * @param x
     * @param y
     * @param cu
     * @param cr
     * @return
     */
    private boolean calBlockMybeSet(Cell c, int x, int y, ComputeUnit cu,CalResult cr) {
        int[] excludedInitSet = new int[9];
        int[] excludedMaybeSet = new int[0];
        int excluededNum = 0;
        for (int i = x / 3 * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (i != x || j != y) {
                    Cell c_other = cu.getCells()[i][j];
                    if (c_other.getIssure()) {
                        excludedInitSet[excluededNum] = c_other.getValue();
                        excluededNum++;
                    }
                    if (excludedMaybeSet.length < 9) {
                        if (c_other.getIssure()) {
                            excludedMaybeSet = AlgorithmUtil.getUnionSet(excludedMaybeSet, c_other.getValue());
                        } else {
                            excludedMaybeSet = AlgorithmUtil.getUnionSet(excludedMaybeSet, c_other.getMaybeSet());
                        }
                    }
                }
            }
        }
        if (excludedMaybeSet.length < 9) {
            int[] includedMaybeSet = AlgorithmUtil.getRestSet(CellUtil.initMaybeSet, excludedMaybeSet);
            if (includedMaybeSet.length == 1) {
                cr.setChanged(true);
                c.setIssure(true);
                c.setValue(includedMaybeSet[0]);
                cu.setIsSureNum(cu.getIsSureNum() + 1);
                return true;
            }
            if (includedMaybeSet.length < 1) {
                throw new CalErrorException();
            }
        }
        int[] excludedSet = new int[excluededNum];
        System.arraycopy(excludedInitSet, 0, excludedSet, 0, excluededNum);
        int[] newMayBeSet = AlgorithmUtil.getRestSet(c.getMaybeSet(), excludedSet);
        if(newMayBeSet.length != c.getMaybeSet().length){
            cr.setChanged(true);
            c.setMaybeSet(newMayBeSet);
        }
        if (c.checkIssure()) {
            cu.setIsSureNum(cu.getIsSureNum() + 1);
            return true;
        } else {
            return false;
        }
    }

    public void printCells(Cell[][] cells, String mark) {
        System.out.println(mark + "   ");
        System.out.println("");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(String.format("%1$20s", cells[i][j] + "  "));
            }
            System.out.println("");
        }
    }


}
