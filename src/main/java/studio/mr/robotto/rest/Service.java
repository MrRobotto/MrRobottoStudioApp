package studio.mr.robotto.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aaron on 24/08/2015.
 */
public class Service {
    private HashMap<String, Action> mActions;
    private String mUrl;
    private HashMap<String, String> mHeaders;
    private ActionConfig mConfig;

    public Service(String url, ActionConfig config) {
        mUrl = url;
        mActions = new HashMap<>();
        mHeaders = new HashMap<>();
        mConfig = config;
    }

    public Service(String url) {
        this(url, new ActionConfig());
    }

    public void addAction(String name, Action action) {
        mActions.put(name, action);
        configAction(action);
    }

    public void callAction(String name) {
        ActionCaller caller = new ActionCaller(mActions.get(name));
        caller.execute();
    }

    public void callActionWithBodyParams(String name, Map<String, String> bodyParams) {
        Action action = mActions.get(name);
        action.getRequestBody().clear();
        action.getRequestBody().putAll(bodyParams);
        ActionCaller caller = new ActionCaller(action);
        caller.execute();
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setConfig(ActionConfig config) {
        mConfig = config;
    }

    private void configAction(Action action) {
        if (action.getUrl() == null) {
            action.setUrl(mUrl);
        }
        if (action.getHeaders() == null) {
            action.setHeaders(mHeaders);
        }
        if (action.getConfig() == null) {
            action.setConfig(mConfig);
        }
    }
}
