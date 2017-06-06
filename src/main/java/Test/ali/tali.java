package Test.ali;

/**
 * Created by zuston on 17/5/22.
 */
public class tali {
    public static int tempV;
    public static int tag = 1;
    public static void main(String[] args) {

    }

    public static void find(int [] arr){
        if (arr.length<=1){
            tag = 0;
            return;
        }
        if (arr.length==2){
            tempV=  arr[0]>arr[1]?arr[1]:arr[0];
            return;
        }
//        第一大
        int one = arr[0]>arr[1]?arr[0]:arr[1];
//        第二大
        int two = arr[0]>arr[1]?arr[1]:arr[0];
        for (int i=2;i<arr.length;i++){
            int tempValue = arr[i];
            if (tempValue>one){
                two = one;
                one = tempValue;
            }
            if (tempValue<one&&tempValue>two){
                two = tempValue;
            }
        }
        tempV = two;
        return;
    }
}
