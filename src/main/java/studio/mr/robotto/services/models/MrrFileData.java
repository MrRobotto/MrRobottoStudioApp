package studio.mr.robotto.services.models;

/**
 * Created by aaron on 28/08/2015.
 */
public class MrrFileData {

    private int id;
    private String filename;
    private boolean is_selected;
    private String user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean is_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "MrrFileData{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", is_selected=" + is_selected +
                ", user='" + user + '\'' +
                '}';
    }
}
