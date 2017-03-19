package test;

import java.util.Scanner;

/**
 * Created by zuston on 17-3-17.
 */
public class one {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.next();
        char [] arr = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c:arr){
            int i = (int)c;
            if ((i>=65&&i<=90)||(i>=97&&i<=122)){
                sb.append(c);
            }
        }

        System.out.println(String.valueOf(sb).toLowerCase());
    }
}
