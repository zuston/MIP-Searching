package io.github.zuston.Tools.Notify;

import java.util.Map;

/**
 * Created by zuston on 9/28/17.
 */
public interface NotifyTool {
    public boolean send(Map<String,String> dataMapper);
}
