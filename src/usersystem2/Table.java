package usersystem2;


public class Table {
    private Database db;
    private String tableName;

    public Table(Database db, String tableName) {
        this.db = db;
        this.tableName = tableName;
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
