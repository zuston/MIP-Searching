package io.github.zuston.Tools.Notify.Impl;

import io.github.zuston.Helper.HttpHelper;
import io.github.zuston.Tools.Notify.NotifyTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zuston on 9/28/17.
 */
@Service
public class WechatNotifyTool implements NotifyTool{

    @Autowired
    private HttpHelper httpHelper;

    @Override
    public boolean send(Map<String, String> dataMapper) {
        httpHelper.doGet("",new HashMap<>());
        return false;
    }
}
