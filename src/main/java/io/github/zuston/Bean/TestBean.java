package io.github.zuston.Bean;

/**
 * Created by zuston on 17-2-20.
 */
public class TestBean {
    public String name;
    public int age;


    public TestBean(){
        this.name = "zuston";
        this.age = 20;
    }
    public TestBean(String name,int age){
        this.name = name;
        this.age = age;
    }
    public String getName(){
        return this.name;
    }

    public int age(){
        return this.age;
    }


}
