package usersystem2;


public class Table {
    private Database db;
    private String tableName;
    private byte permission;

    public Table(Database db, String tableName, byte permission) {
        this.db = db;
        this.tableName = tableName;
        this.permission = permission;
    }

    public byte getPermission() {
        return permission;
    }

    public void setPermission(byte permission) {
        this.permission = permission;
    }

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
