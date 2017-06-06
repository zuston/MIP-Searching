package Test.algorithm;

import java.util.LinkedHashMap;

/**
 * Created by zuston on 17/5/21.
 */
public class lru {

    public int maxSize;
    public LinkedHashMap<Integer,Integer> container;

    public lru(int maxSize){
        this.maxSize = maxSize;
        this.container = new LinkedHashMap<Integer, Integer>();
    }

    public int get(int key){
        if (container.containsKey(key)){
            int value = container.get(key);
            remove(key);
            put(key,value);
            return value;
        }
        return -1;
    }

    public void put(int key,int value){
        if (this.container.containsKey(key)){
            remove(key);
            this.container.put(key,value);
            return;
        }
        if (this.container.size()>=this.maxSize){
            int kkey = this.container.entrySet().iterator().next().getKey();
            this.container.remove(kkey);
        }
        this.container.put(key,value);

    }

    private void remove(int key){
        this.container.remove(key);
    }

    public static void main(String[] args) {
        lru l = new lru(3);
        l.put(1,1);
        l.put(2,2);
        l.put(3,3);
        l.get(2);
        l.put(4,1);
        l.get(3);
        System.out.println(l.container);
    }
}
