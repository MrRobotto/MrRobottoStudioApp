package studio.mr.robotto.rest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by aaron on 24/08/2015.
 */
public class Action {
    private String mUrl;
    private HashMap<String, String> mHeaders;
    private HashMap<String, String> mRequestBody = new HashMap<>();
    private HTTPMethod mMethod;
    private ActionConfig mConfig;

    public Action(String url, HTTPMethod method) {
        mUrl = url;
        mMethod = method;
    }

    public Action(String url, HTTPMethod method, ActionConfig config) {
        mUrl = url;
        mMethod = method;
        mConfig = config;
    }

    public Action(HTTPMethod method) {
        mMethod = method;
    }

    public Action(HTTPMethod method, ActionConfig config) {
        mMethod = method;
        mConfig = config;
    }

    private Action(String url, HashMap<String, String> headers, HTTPMethod method, ActionConfig config) {
        mUrl = url;
        mHeaders = headers;
        mMethod = method;
        mConfig = config;
    }

    public void addBodyParam(String key, String value) {
        mRequestBody.put(key, value);
    }

    public HashMap<String, String> getRequestBody() {
        return mRequestBody;
    }

    public String getRequestBodyAsJson() {
        JSONObject object = new JSONObject(mRequestBody);
        String r = object.toString();
        return r;
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public HashMap<String, String> getHeaders() {
        return mHeaders;
    }

    public void setHeaders(HashMap<String, String> headers) {
        mHeaders = headers;
    }

    public HTTPMethod getMethod() {
        return mMethod;
    }

    public void setMethod(HTTPMethod method) {
        mMethod = method;
    }

    public ActionConfig getConfig() {
        return mConfig;
    }

    public void setConfig(ActionConfig config) {
        mConfig = config;
    }

    public enum HTTPMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    public static class Builder {
        private String mUrl = null;
        private HashMap<String, String> mHeaders = null;
        private HTTPMethod mMethod = HTTPMethod.GET;
        private ActionConfig mConfig = null;

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public Builder setHeaders(HashMap<String, String> headers) {
            mHeaders = headers;
            return this;
        }

        public Builder setMethod(HTTPMethod method) {
            mMethod = method;
            return this;
        }

        public Builder setConfigurer(ActionConfig config) {
            mConfig = config;
            return this;
        }

        public Action createAction() {
            return new Action(mUrl, mHeaders, mMethod, mConfig);
        }
    }
}
