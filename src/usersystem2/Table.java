package usersystem2;


import java.io.Serializable;

public class Table implements Serializable {
    private static final long serialVersionUID = 1L;
    private Database db;
    private String tableName;
    private byte permission;

    public Table(Database db, String tableName, byte permission) {
        this.db = db;
        this.tableName = tableName;
        this.permission = permission;
    }


    @Override
    public int hashCode() {
        int result = db.hashCode();
        result = 17 * result + (tableName == null ? 0 : tableName.hashCode());
        result = 17 * result + (int) permission;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Table))
            return false;

        Table tableObj = (Table) obj;
        if (this == tableObj)
            return true;
        if(db==null || tableName==null){
            return false;
        }
        else if ((tableObj.db.equals(this.db)) && (tableObj.tableName.equals(this.tableName)) &&
                (tableObj.permission == this.permission)) {
            return true;
        } else {
            return false;
        }
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
