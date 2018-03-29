package next.section9;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CalculateService {

    public Map<String, String> calculate(Map<String, String> condition) throws Exception {

        ComputeUnit cu = CellUtil.initCell(condition);
        ComputeUnit cu_last = null;
        while (cu.getIsSureNum() < 81 && (cu.compareTo(cu_last) != 0)) {
            cu_last = (ComputeUnit) CellUtil.deepClone(cu);
            for (int i = 0; i < 9; i++) {
                Cell[] cellx = cu.getCells()[i];
                for (int j = 0; j < 9; j++) {
                    Cell c = cellx[j];
                    if (!c.getIssure()) {
                        if (calRowMybeSet(c, i, j, cu) || calColMybeSet(c, i, j, cu) || calBlockMybeSet(c, i, j, cu)) {
                        }
                    }
                }
            }
            squeezeMaybeSet(cu); //进行挤压操作，有bug
            doLinkageCompute(cu); //进行联动操作
        }
        if (cu.getIsSureNum() < 81) {
            doExhaustionCompute(cu); //穷举操作
        }
        return CellUtil.cellToMap(cu.getCells());
    }

    private void doExhaustionCompute(ComputeUnit cu) throws Exception {
        Cell exhaustCell_orign = getExhaustionCell(cu.getCells());
        for (int i = 0; i < exhaustCell_orign.getMaybeSet().length; i++) {
            ComputeUnit cu_sub = (ComputeUnit) CellUtil.deepClone(cu);
            Cell exhaustCell = cu_sub.getCells()[exhaustCell_orign.getX()][exhaustCell_orign.getY()];
            int value = exhaustCell_orign.getMaybeSet()[i];
            exhaustCell.setValue(value);
            exhaustCell.setIssure(true);
            exhaustCell.setMaybeSet(null);
            cu_sub.setIsSureNum(cu_sub.getIsSureNum()+1);
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
        ComputeUnit cu_last = null;
        while (cu.getIsSureNum() < 81 && (cu.compareTo(cu_last) != 0)) {
            cu_last = (ComputeUnit) CellUtil.deepClone(cu);
            for (int i = 0; i < 9; i++) {
                Cell[] cellx = cu.getCells()[i];
                for (int j = 0; j < 9; j++) {
                    Cell c = cellx[j];
                    if (!c.getIssure()) {
                        if (calRowMybeSet(c, i, j, cu) || calColMybeSet(c, i, j, cu) || calBlockMybeSet(c, i, j, cu)) {
                        }
                    }
                }
            }
            squeezeMaybeSet(cu); //进行挤压操作，有bug
            doLinkageCompute(cu); //进行联动操作
        }
        if (cu.getIsSureNum() < 81) {
            Cell exhaustCell_orign = getExhaustionCell(cu.getCells());
            for (int i = 0; i < exhaustCell_orign.getMaybeSet().length; i++) {
                ComputeUnit cu_sub = (ComputeUnit) CellUtil.deepClone(cu);
                Cell exhaustCell = cu_sub.getCells()[exhaustCell_orign.getX()][exhaustCell_orign.getY()];
                int value = exhaustCell_orign.getMaybeSet()[i];
                exhaustCell.setValue(value);
                exhaustCell.setIssure(true);
                cu_sub.setIsSureNum(cu_sub.getIsSureNum()+1);
                try {
                    if (exhaustionCalculate(cu_sub)) {
                        printCells(cu.getCells(),"end1");
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

    private void doLinkageCompute(ComputeUnit cu) {
        rowLinkageCompute(cu);
        colLinkageCompute(cu);
        blockLinkageCompute(cu);
    }

    private void colLinkageCompute(ComputeUnit cu) {
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
                linkageComputeCol2Block(i, 0, complementarySetCol1, cu);
            }
            int[] complementarySetCol2 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol2, AlgorithmUtil.getUnionSet(maybeCol1, maybeCol3)), getMaybeSet(col2));
            if (complementarySetCol2.length > 1) {
                linkageComputeCol2Block(i, 3, complementarySetCol2, cu);
            }
            int[] complementarySetCol3 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeCol3, AlgorithmUtil.getUnionSet(maybeCol1, maybeCol2)), getMaybeSet(col3));
            if (complementarySetCol3.length > 1) {
                linkageComputeCol2Block(i, 6, complementarySetCol3, cu);
            }
        }
    }

    private void rowLinkageCompute(ComputeUnit cu) {
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
                linkageComputeRow2Block(0, i, complementarySetRow1, cu);
            }
            int[] complementarySetRow2 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow2, AlgorithmUtil.getUnionSet(maybeRow1, maybeRow3)), getMustBeSet(row2));
            if (complementarySetRow2.length > 0) {
                linkageComputeRow2Block(3, i, complementarySetRow2, cu);
            }
            int[] complementarySetRow3 = AlgorithmUtil.getComplementarySet(AlgorithmUtil.getComplementarySet(maybeRow3, AlgorithmUtil.getUnionSet(maybeRow1, maybeRow2)), getMustBeSet(row3));
            if (complementarySetRow3.length > 0) {
                linkageComputeRow2Block(6, i, complementarySetRow3, cu);
            }
        }
    }

    private void linkageComputeRow2Block(int x, int y, int[] mustNotBeSet, ComputeUnit cu) {
        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (j != y) {
                    Cell c = cu.getCells()[i][j];
                    if (!c.getIssure()) {
                        c.setMaybeSet(AlgorithmUtil.getComplementarySet(c.getMaybeSet(), mustNotBeSet));
                        if (c.checkIssure()) {
                            cu.setIsSureNum(cu.getIsSureNum() + 1);
                            notifyXYBlock(i, j, cu);
                        }
                    }
                }
            }
        }
    }

    private void linkageComputeCol2Block(int x, int y, int[] mustNotBeSet, ComputeUnit cu) {
        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (i != x) {
                    Cell c = cu.getCells()[i][j];
                    if (!c.getIssure()) {
                        c.setMaybeSet(AlgorithmUtil.getComplementarySet(c.getMaybeSet(), mustNotBeSet));
                        if (c.checkIssure()) {
                            cu.setIsSureNum(cu.getIsSureNum() + 1);
                            notifyXYBlock(i, j, cu);
                        }
                    }
                }
            }
        }
    }

    private void notifyXYBlock(int x, int y, ComputeUnit cu) {
        notifyX(x, y, cu);
        notifyY(x, y, cu);
        notifyBlock(x, y, cu);
    }

    private void notifyX(int x, int y, ComputeUnit cu) {
        for (int i = 0; i < 9; i++) {
            if (!cu.getCells()[i][y].getIssure()) {
                calRowMybeSet(cu.getCells()[i][y], i, y, cu);
            }
        }
    }

    private void notifyY(int x, int y, ComputeUnit cu) {
        for (int i = 0; i < 9; i++) {
            if (!cu.getCells()[x][i].getIssure()) {
                calColMybeSet(cu.getCells()[x][i], x, i, cu);
            }
        }
    }

    private void notifyBlock(int x, int y, ComputeUnit cu) {
        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = y / 3 * 3; j < (y / 3 + 1) * 3; j++) {
                if (!cu.getCells()[i][j].getIssure()) {
                    calBlockMybeSet(cu.getCells()[i][j], i, j, cu);
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

    private void blockLinkageCompute(ComputeUnit cu) {
     /*   for(int b = 0 ; b < 9 ; b ++){
            Cell[] squeezeSet = new Cell[9];
            int num = 0;
            for(int i = (b%3)*3; i < (b%3)*3+3 ; i ++) {
                for(int j = b/3 *3  ; j < (b/3 + 1) * 3 ; j++ ){
                    squeezeSet[num] = cells[i][j];
                    num++;
                }
            }
            doSqueeze(squeezeSet);
        }*/
    }

    private void squeezeMaybeSet(ComputeUnit cu) {
        squeezeRowMaybeSet(cu);
        squeezeColMaybeSet(cu);
        squeezeBlockMaybeSet(cu);
    }

    /**
     * 行挤压
     */
    private void squeezeRowMaybeSet(ComputeUnit cu) {
        for (int i = 0; i < 9; i++) {
            Cell[] squeezeSet = new Cell[9];
            for (int j = 0; j < 9; j++) {
                squeezeSet[j] = cu.getCells()[j][i];
            }
            doSqueeze(squeezeSet, cu);
        }
    }

    /**
     * 列挤压
     */
    private void squeezeColMaybeSet(ComputeUnit cu) {
        for (int i = 0; i < 9; i++) {
            doSqueeze(cu.getCells()[i], cu);
        }
    }

    /**
     * 块挤压
     */
    private void squeezeBlockMaybeSet(ComputeUnit cu) {
        for (int b = 0; b < 9; b++) {
            Cell[] squeezeSet = new Cell[9];
            int num = 0;
            for (int i = (b % 3) * 3; i < (b % 3) * 3 + 3; i++) {
                for (int j = b / 3 * 3; j < (b / 3 + 1) * 3; j++) {
                    squeezeSet[num] = cu.getCells()[i][j];
                    num++;
                }
            }
            doSqueeze(squeezeSet, cu);
        }
    }

    /**
     * 进行挤压 正向挤压和逆向挤压
     *
     * @param cells
     */
    private void doSqueeze(Cell[] cells, ComputeUnit cu) {
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
     * @param c
     * @param x
     * @param y
     * @return
     */
    private boolean calRowMybeSet(Cell c, int x, int y, ComputeUnit cu) {
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
        if(excludedMaybeSet.length < 8){
            throw new CalErrorException();
        }
        if (excludedMaybeSet.length < 9) {
            int[] includedMaybeSet = AlgorithmUtil.getRestSet(CellUtil.initMaybeSet, excludedMaybeSet);
            if (includedMaybeSet.length == 1) {
                c.setIssure(true);
                c.setValue(includedMaybeSet[0]);
                cu.setIsSureNum(cu.getIsSureNum() + 1);
                return true;
            }
         /*   if (includedMaybeSet.length < 1) {
                throw new CalErrorException();
            }*/
        }
        int[] excludedSet = new int[excluededNum];
        System.arraycopy(excludedInitSet, 0, excludedSet, 0, excluededNum);
        c.setMaybeSet(AlgorithmUtil.getRestSet(c.getMaybeSet(), excludedSet));
        if (c.checkIssure()) {
            cu.setIsSureNum(cu.getIsSureNum() + 1);
            return true;
        } else {
            return false;
        }
    }

    private boolean calColMybeSet(Cell c, int x, int y, ComputeUnit cu) {
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
        c.setMaybeSet(AlgorithmUtil.getRestSet(c.getMaybeSet(), excludedSet));
        if (c.checkIssure()) {
            cu.setIsSureNum(cu.getIsSureNum() + 1);
            return true;
        } else {
            return false;
        }
    }

    private boolean calBlockMybeSet(Cell c, int x, int y, ComputeUnit cu) {
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
        c.setMaybeSet(AlgorithmUtil.getRestSet(c.getMaybeSet(), excludedSet));
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
