package Test.algorithm;

/**
 * Created by zuston on 17/5/21.
 */
public class quicksort {
    public static void main(String[] args) {
        int [] arr = {4,2,5,1,9,8,10,32,67,21,43,23,25,59};
        qqsort(arr,0,arr.length-1);
        for (int i:arr){
            System.out.println(i);
        }
    }

    public static  void qqsort(int arr[],int left,int right){
        if (left>right){
            return;
        }
        int l = left;
        int r = right;
        int basicValue = arr[left];
        int index = left;
        while (left<right){
            while (arr[right]>=basicValue&&right>left){
                right--;
            }
            if (arr[right]<basicValue){
                swap(arr,right,index);
                index = right;
            }
            while (arr[left]<=basicValue&&left<right){
                left++;
            }
            if (arr[left]>basicValue){
                swap(arr,left,index);
                index = left;
            }
        }
        qqsort(arr,l,index-1);
        qqsort(arr,index+1,r);
    }

    public static void swap(int[] arr, int right, int index) {
        int temp = arr[right];
        arr[right] = arr[index];
        arr[index] = temp;
    }
}
