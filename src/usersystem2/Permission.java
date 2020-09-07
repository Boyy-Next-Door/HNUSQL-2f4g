package usersystem2;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;

import java.io.Serializable;

public class Permission implements Serializable {
    private static final long serialVersionUID = 4L;
    private int target = 1;     //0--database  1--table（默认）
    private Database database;
    private Table table;
    private byte permission;    //该用户的权限
    private User grantedBy;
    private int grantType;      // 0-link 1-admin

    @Override
    public int hashCode() {
        int result = target;
        result = 17 * result + database.hashCode();
        result = 17 * result + table.hashCode();
        result = 17 * result + (int) permission;
        result = 17 * result + grantedBy.hashCode();
        result = 17 * result + grantType;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Permission))
            return false;

        Permission permObj = (Permission) obj;
        if (this == permObj)
            return true;
        if ((permObj.database.equals(this.database)) && (permObj.table.equals(this.table)) &&
                (permObj.target == this.target) && (permObj.permission == this.permission) &&
                (permObj.grantedBy.equals(this.grantedBy)) && (permObj.grantType == this.grantType)) {
            return true;
        } else {
            return false;
        }
    }


    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public byte getPermission() {
        return permission;
    }

    public void setPermission(byte permission) {
        this.permission = permission;
    }

    public User getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(User grantedBy) {
        this.grantedBy = grantedBy;
    }

    public int getGrantType() {
        return grantType;
    }

    public void setGrantType(int grantType) {
        this.grantType = grantType;
    }

}
