package io.github.zuston.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by zuston on 17/3/25.
 */
public class RaceMapper {
    // 族系列表
    public static ArrayList<String> race = getRace();
    // 元素周期表所有元素
    public static HashMap<String,ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
    static {
        hm.put("1A",get1A());
        hm.put("2A",get2A());
        hm.put("3B",get3B());
        hm.put("4B",get4B());
        hm.put("5B",get5B());
        hm.put("6B",get6B());
        hm.put("7B",get7B());
        hm.put("8B1",get8B1());
        hm.put("8B2",get8B2());
        hm.put("8B3",get8B3());
        hm.put("1B",get1B());
        hm.put("2B",get2B());
        hm.put("3A",get3A());
        hm.put("4A",get4A());
        hm.put("5A",get5A());
        hm.put("6A",get6A());
        hm.put("7A",get7A());
        hm.put("8A",get8A());
    }
    public static ArrayList<String> allElements = getAllElements();


    private static ArrayList<String> get8A(){
        return new ArrayList<String>(Arrays.asList(
            "He",
            "Ne",
                "Ar",
                "Kr",
                "Xe",
                "Rn"
        ));
    }


    private static ArrayList<String> get6A() {
        return new ArrayList<String>(Arrays.asList(
                "O",
                "S",
                "Se",
                "Te",
                "Po"
        ));
    }

    private static ArrayList<String> get5A() {
        return new ArrayList<String>(Arrays.asList(
                "N",
                "P",
                "As",
                "Sb",
                "Bi"
        ));
    }

    private static ArrayList<String> get4A() {
        return new ArrayList<String>(Arrays.asList(
                "C",
                "Si",
                "Ge",
                "Sn",
                "Pb"
        ));
    }

    private static ArrayList<String> get3A() {
        return new ArrayList<String>(Arrays.asList(
                "B",
                "Al",
                "Ga",
                "In",
                "Tl"
        ));
    }

    private static ArrayList<String> get2B() {
        return new ArrayList<String>(Arrays.asList(
                "Zn",
                "Cd",
                "Hg"
        ));
    }

    private static ArrayList<String> get1B() {
        return new ArrayList<String>(Arrays.asList(
                "Cu",
                "Ag",
                "Au"

        ));
    }

    private static ArrayList<String> get8B1() {
        return new ArrayList<String>(Arrays.asList(
                "Fe",
                "Ru",
                "Os",
                "Hs"
        ));
    }

    private static ArrayList<String> get8B2(){
        return new ArrayList<String>(Arrays.asList(
                "Co",
                "Rh",
                "Ir",
                "Mt"
        ));
    }

    private static ArrayList<String> get8B3(){
        return new ArrayList<String>(Arrays.asList(
                "Ni",
                "Pd",
                "Pt"
        ));
    }

    private static ArrayList<String> get8B() {
        return new ArrayList<String>(Arrays.asList(
                "Fe",
                "Ru",
                "Os",


                "Co",
                "Rh",

                "Ir",

                "Ni",
                "Pd",
                "Pt"
        ));
    }

    private static ArrayList<String> get7B() {
        return new ArrayList<String>(Arrays.asList(
                "Mn",
                "Tc",
                "Re"
        ));
    }

    private static ArrayList<String> get6B() {
        return new ArrayList<String>(Arrays.asList(
                "Cr",
                "Mo",
                "W"

        ));
    }

    private static ArrayList<String> get5B() {
        return new ArrayList<String>(
            Arrays.asList(
                    "V",
                    "Nb",
                    "Ta"
            )
        );
    }

    private static ArrayList<String> get4B() {
        return new ArrayList<String>(Arrays.asList(
                "Ti",
                "Zr",
                "Hf"
        ));
    }

    private static ArrayList<String> get3B() {
        return new ArrayList<String>(
                Arrays.asList(
                        "Sc",
                        "Y",
                        "La",
                        "Ce",
                        "Pr",
                        "Nd",
                        "Pm",
                        "Sm",
                        "Eu",
                        "Gd",
                        "Tb",
                        "Dy",
                        "Ho",
                        "Er",
                        "Tm",
                        "Yb",
                        "Lu",
                        "Ac",
                        "Th",
                        "Pa",
                        "U",
                        "Np",
                        "Pu",
                        "Am",
                        "Cm",
                        "Bk",
                        "Cf",
                        "Es",
                        "Fm",
                        "Md",
                        "No",
                        "Lr"
                )
        );
    }

    private static ArrayList<String> get2A() {
        return new ArrayList<String>(
                Arrays.asList(
                        "Be",
                        "Mg",
                        "Ca",
                        "Sr",
                        "Ba",
                        "Ra")
        );
    }

    private static ArrayList<String> get1A() {
        return new ArrayList<String>(
                Arrays.asList(
                        "H",
                        "Li",
                        "Na",
                        "K",
                        "Rb",
                        "Cs",
                        "Fr"
                )
        );
    }

    private static ArrayList<String> get7A() {
        return new ArrayList<String>(Arrays.asList(
                "F",
                "Cl",
                "Br",
                "I",
                "At"
        ));
    }

    private static ArrayList<String> getRace() {
        ArrayList<String> raceArr = new ArrayList<String>();
        raceArr.add("1A");
        raceArr.add("2A");
        raceArr.add("3B");
        raceArr.add("4B");
        raceArr.add("5B");
        raceArr.add("6B");
        raceArr.add("7B");
        raceArr.add("8B1");
        raceArr.add("8B2");
        raceArr.add("8B3");
        raceArr.add("1B");
        raceArr.add("2B");
        raceArr.add("3A");
        raceArr.add("4A");
        raceArr.add("5A");
        raceArr.add("6A");
        raceArr.add("7A");
        raceArr.add("8A");
        return raceArr;
    }


    public static ArrayList<String> getAllElements(){
        ArrayList<String> allElementsList = new ArrayList<String>();
        for (String value:race){
            allElementsList.addAll(hm.get(value));
        }
        return allElementsList;
    }


}
