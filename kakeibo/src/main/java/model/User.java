package model;

public class User {
    private int id;
    private String username;

    // 引数ありコンストラクター
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    // 引数なしコンストラクター
    public User() {
    }

    // getter / setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
