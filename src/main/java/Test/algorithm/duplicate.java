package Test.algorithm;

import java.util.ArrayList;

/**
 * Created by zuston on 17/5/21.
 */
public class duplicate {
    public static void main(String [] args){
        int [] arr = {1,2,3,4,4,5,6,6,7,7,7,8,9};
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(arr[0]);
        for (int i=1;i<arr.length;i++){
            if (arr[i]!=arr[i-1]){
                arrayList.add(arr[i]);
            }
        }
        System.out.println(arrayList.toString());
    }

}
