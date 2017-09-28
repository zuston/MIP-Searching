package io.github.zuston.Helper;

import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zuston on 9/28/17.
 */
@Service
public class HttpHelper {
    private final String DEFAULT_CHARSET = "utf-8";

    public HttpResponse doPost(String url, Map<String,String> map){
        return doPost(url, map, DEFAULT_CHARSET);
    }
    public HttpResponse doPost(String url, Map<String,String> map, String charset){
        return null;
    }

    public HttpResponse doGet(String url, Map<String,String> map){
        return doGet(url, map, DEFAULT_CHARSET);
    }

    public HttpResponse doGet(String url, Map<String,String> map, String charset){
        return null;
    }
}
