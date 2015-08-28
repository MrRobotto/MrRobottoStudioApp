package studio.mr.robotto.services.models;

/**
 * Created by aaron on 28/08/2015.
 */
public class SessionData {
    private String token;
    private String url;

    public SessionData(String url, String token) {
        this.token = token;
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SessionData{" +
                "token='" + token + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
