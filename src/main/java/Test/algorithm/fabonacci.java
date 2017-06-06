package Test.algorithm;

/**
 * Created by zuston on 17/5/21.
 */
public class fabonacci {
    public static void main(String [] args){
        System.out.println(loop3(1,0,1));
    }

    public static  long loop(int i){
        if (i<=1){
            return i;
        }

        return loop(i-1)+loop(i-2);
    }

    public static int loop2(int i){
        int result [] = {0,1};
        if (i<2){
            return result[i];
        }
        int left = 0;
        int right = 1;
        int now = 0;
        for (int k=2;k<=i;k++){
            int temp = left;
            left = right;
            right = temp+right;
        }
        return right;
    }

    public static long loop3(int i,long left,long right){
        if (i==0){
            return left;
        }
        return loop3(i-1,right,left+right);
    }
}
