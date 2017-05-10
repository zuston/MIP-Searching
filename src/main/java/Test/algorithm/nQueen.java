package Test;

import java.util.ArrayList;

/**
 * Created by zuston on 17/5/8.
 */
public class nQueen {

    public static ArrayList<Integer> resList = new ArrayList<Integer>();
    public static int count = 0;


    public static void main(String[] args) {
        loop(0);
        System.out.println(count);
    }

    public static void loop(int step){
        if (step==8){
            System.out.println(resList);
            count ++;
            return;
        }
        for (int i=0;i<8;i++){
            if (isValidate(step,i)){
                resList.add(i);
                loop(step+1);
                resList.remove(resList.size()-1);
            }
        }
    }

    private static boolean isValidate(int step, int i) {
        for (int k=0;k<step;k++){
            if (resList.get(k)==i){
                return false;
            }
            if (Math.abs(step-k)==Math.abs(i-resList.get(k))){
                return false;
            }
        }
        return true;
    }
}
