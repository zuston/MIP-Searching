package Test.algorithm;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zuston on 17/5/8.
 */
public class all {
    public static int n = 0;
    public static ArrayList<Integer> resList = new ArrayList<Integer>();
    public static ArrayList<Integer> value = new ArrayList<Integer>(Arrays.asList(1,2,2));
    public static void main(String[] args) {
        n = value.size();
        loop(0);
    }

    public static void loop(int step){
        if (step==n){
            for (Integer v:resList){
                System.out.print(value.get(v));
            }
            System.out.println();
            return;
        }
        loop(step+1);
        resList.add(step);
        loop(step+1);
        resList.remove(resList.size()-1);
    }
}
