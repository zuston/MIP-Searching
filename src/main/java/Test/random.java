package Test;

/**
 * Created by zuston on 17/6/6.
 */
public class random {
    public static void main(String[] args) {
        int value = ((int) (Math.random()*1000))%2;
        System.out.println(value);
    }
}
