package next.section9;

import java.util.Arrays;

public class AlgorithmUtil {

    /**
     * 取补集
     * @param U  需要有序
     * @param A  需要有序
     * @return
     */
    public static int[] getComplementarySet(int[] U , int[] A){
        int num = 0 ; //补集的集合数量
        int[] resultOrignal = new int[U.length]; //最初的补集数组
        int i = 0 ;
        int j = 0 ;
        while(i < U.length && j < A.length){
            if(U[i] < A[j]){
                resultOrignal[num] = U[i];
                num++;
                i++;
                continue;
            }
            if(U[i] == A[j]){
                i ++;
                j ++;
                continue;
            }
            if(U[i] > A[j]){
                j ++;
                continue;
            }
        }
        if(j == A.length && i < U.length){
            System.arraycopy(U, i, resultOrignal,num,U.length-i);
            num+=U.length-i;
        }
        int[] result = new int[num];
        if(num > 0){
           System.arraycopy(resultOrignal, 0, result,0,num);
        }
        return result;
    }

    /**
     * 取交集
     * @param A 需要有序
     * @param B 需要有序
     */
    public static int[]  getIntersectionSet(int[] A , int[] B){
        int num = 0 ; //交集的集合数量
        int[] resultOrignal = new int[A.length];
        int i = 0 ;
        int j = 0 ;
        while(i < A.length && j < B.length){
            if(A[i] < B[j]){
                i++;
                continue;
            }
            if(A[i] == B[j]){
                resultOrignal[num] = A[i];
                num++;
                i ++;
                j ++;
                continue;
            }
            if(A[i] > B[j]){
                j ++;
                continue;
            }
        }
        int[] result = new int[num];
        if(num > 0){
            System.arraycopy(resultOrignal, 0, result,0,num);
        }
        return result;
    }

    /**
     * 取并集
     * @param A 需要有序
     * @param B 需要有序
     * @return
     */
    public static int[]  getUnionSet(int[] A , int[] B){
        int num = 0 ; //交集的集合数量
        int[] resultOrignal = new int[A.length + B.length];
        int i = 0 ;
        int j = 0 ;
        while(i < A.length && j < B.length){
            if(A[i] < B[j]){
                resultOrignal[num] = A[i];
                i++;
                num++;
                continue;
            }
            if(A[i] == B[j]){
                resultOrignal[num] = A[i];
                i ++;
                j ++;
                num++;
                continue;
            }
            if(A[i] > B[j]){
                resultOrignal[num] = B[j];
                j ++;
                num++;
                continue;
            }
        }
        if( i != A.length || j != B.length ){
            if( i == A.length){
                System.arraycopy(B, j, resultOrignal,num,B.length-j);
                num+=B.length-j;
            }
            if( j == B.length){
                System.arraycopy(A, i, resultOrignal,num,A.length-i);
                num+=A.length-i;
            }
        }

        int[] result = new int[num];
        if(num > 0){
            System.arraycopy(resultOrignal, 0, result,0,num);
        }
        return result;
    }

    /**
     * 取并集
     * @param A
     * @param i
     * @return
     */
    public static int[]  getUnionSet(int[] A, int i){
        int[] B = new int[]{i};
        int[] result = getUnionSet(A,B);
        return result;
    }

        /**
         * 获取剩余元素
         * @param U 需要有序
         * @param intArray
         * @return
         */
    public static int[]  getRestSet(int[] U,int... intArray){
        Arrays.sort(intArray);
        int[] result = getComplementarySet(U,intArray);
        return result;
    }

    /**
     * 比较是否是包含关系
     * @param U
     * @param A
     * @return
     */
    public static boolean isContains(int[] U,int[] A){
        int[] allResult = getUnionSet(U,A);
        return allResult.length == U.length;
    }
    /**
     * 是否包含或者是否被包含
     * @param UA
     * @param UB
     * @return
     */
    public static boolean isContainsOrBeContrained(int[] UA,int[] UB){
        int[] allResult = getUnionSet(UA,UB);
        return allResult.length == UA.length || allResult.length == UB.length;

    }
}
