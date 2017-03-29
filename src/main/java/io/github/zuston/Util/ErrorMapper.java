package io.github.zuston.Util;

import java.util.HashMap;

/**
 * Created by zuston on 17/3/29.
 */
public class ErrorMapper {


    public static HashMap<Integer,String> errorMapper = new HashMap<Integer, String>();
    static {
        errorMapper.put(-12,"no data");
        errorMapper.put(-11,"lack the main element");
        errorMapper.put(-10,"expression error");
    }

    public static String ErrorReturn(Integer errorValue){
        return "{\"error\":"+String.valueOf(errorValue)+",\"msg\":\""+errorMapper.get(errorValue)+"\"}";
    }


    public static String ElementLackError(){
        int errorValue = -11;
        return ErrorReturn(errorValue);
    }

    public static String FormatError(){
        int errorValue = -10;
        return ErrorReturn(errorValue);
    }

    public static String NoDataError(){
        int errorValue = -12;
        return ErrorReturn(errorValue);
    }


}
