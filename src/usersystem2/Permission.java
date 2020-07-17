package usersystem2;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;

public class Permission {
    private int target = 1;     //0--database  1--table（默认）
    private Database database;
    private Table table;
    private byte permission;    //该用户的权限
    private User grantedBy;
    private int grantType;      // 0-link 1-admin

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

    /**
     * 校验当前权限能否下发目标权限
     *
     * @param currentPerm
     * @param targetPerm
     */
    public boolean isGrantable(Permission currentPerm, byte targetPerm) {
        //TODO 需要实现
        return false;
    }
}
