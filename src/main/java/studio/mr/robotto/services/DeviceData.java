package studio.mr.robotto.services;

/**
 * Created by aaron on 27/08/2015.
 */
public class DeviceData {
    private int id;
    private String name;
    private String user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
