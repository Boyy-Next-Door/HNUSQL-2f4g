package usersystem2;

import java.util.HashMap;

public class User {
    private String username;
    private String password;
    //用户的权限
    HashMap<Table, Permission> permissions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<Table, Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashMap<Table, Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean isGrantable(String database, String table, byte permission) {

        return false;
    }

    public void acquirePermission(String granterName, String database, String table, byte permission, int grantType) {

    }
}
