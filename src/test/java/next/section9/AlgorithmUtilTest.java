package next.section9;

import org.junit.Test;

public class AlgorithmUtilTest {

    @Test
    public void getComplementarySetTest(){
        int[] U = new int[]{3,4,5,6,7,8};
        int[] A = new int[]{1,2,3};
        int[] B = AlgorithmUtil.getComplementarySet(U,A);
        for(int i = 0 ; i < B.length ; i++){
            System.out.println(B[i]);
        }
    }

    @Test
    public void getIntersectionSetTest(){
        int[] A = new int[]{1,2,3,4,8,9};
        int[] B = new int[]{3,8,9};
        int[] C = AlgorithmUtil.getIntersectionSet(A,B);
        for(int i = 0 ; i < C.length ; i++){
            System.out.println(C[i]);
        }
    }

    @Test
    public void getUnionSetTest(){
        int[] A = new int[]{3,4,5,6,8,9};
        int[] B = new int[]{1,2,3,8};
        int[] C = AlgorithmUtil.getUnionSet(A,B);
        for(int i = 0 ; i < C.length ; i++){
            System.out.println(C[i]);
        }
    }

    @Test
    public void getRestSet(){
        int[] A = new int[]{3,4,5,6,8,9};
        int[] C = AlgorithmUtil.getRestSet(A,2,1,9,8,5,4);
        for(int i = 0 ; i < C.length ; i++){
            System.out.println(C[i]);
        }
    }
}
