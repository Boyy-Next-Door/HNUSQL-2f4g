package usersystem2;

import java.io.Serializable;
import java.util.ArrayList;

public class Database  implements Serializable {
    private static final long serialVersionUID = 3L;
    private String databaseName;

    @Override
    public int hashCode() {
        return databaseName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Database))
            return false;

        Database dbObj = (Database)obj;
        if (this == dbObj)
            return true;
        if (dbObj.databaseName.equals(this.databaseName)){
            return true;
        }else{
            return false;
        }
    }

    public Database(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
