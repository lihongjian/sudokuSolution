package next.section9;

import org.junit.Test;

import java.text.Format;

public class DevelopTest {

    @Test
    public void testCal(){
/*        Integer i = 20 ;
        int j = 18%9 ;
        doAdd(i);
        System.out.println(i);*/
       int a = 0;
       System.out.println(a++);
       System.out.println(a++);


/*
        System.out.println(20/3*3);
*/

    }

    public  void doAdd(Integer i){
        i = i + 50;
        System.out.println(i);
    }

    public static void main(String[] args){
        Integer i = 20 ;
        int j = 18%9 ;
        DevelopTest dt = new DevelopTest();
        dt.doAdd(i);
        System.out.println(i);
    }

    @Test
    public void deepCloneTest(){

        ComputeUnit cu = new ComputeUnit();
        Cell[][] a = new Cell[3][2];
        a[0][1] = new Cell();
        a[0][1].setMaybeSet(new int[]{1,2});
        a[0][0] = new Cell();
        a[0][0].setMaybeSet(new int[]{1,2});
        cu.setCells(a);

        ComputeUnit cu_copy = (ComputeUnit) CellUtil.deepClone(cu);
        cu_copy.getCells()[0][0].setMaybeSet(new int[]{5,6});
        System.out.println(cu.getCells()[0][0].getMaybeSet());
        System.out.println(cu_copy.getCells()[0][0].getMaybeSet());

    }

    @Test
    public void stringFormat(){
        /*String newString = String.format("% 18d","1");
        System.out.println(newString);*/
        String a = "33333";
        System.out.println(String.format("%1$7s", a));
    }
}
