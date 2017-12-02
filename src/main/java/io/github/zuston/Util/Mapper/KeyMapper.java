package io.github.zuston.Util.Mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by zuston on 17/5/2.
 */
public class KeyMapper {
    public static HashMap<String,String> mapper = new HashMap<String, String>();

    static {
        mapper.put("es","atomic_numbers_ratio");
        mapper.put("ve","valence_electrons_sum");
        mapper.put("sg","space_group_type_number");
        mapper.put("en","element_type_numbers");
        mapper.put("st","special_tags");
    }

    public static ArrayList<String> DoubleList = new ArrayList<String>(
            Arrays.asList("valence_electrons_sum","space_group_type_number","element_type_numbers")
    );

    public static ArrayList<String> StringList = new ArrayList<String>(
        Arrays.asList(mapper.get("st"))
    );
}
