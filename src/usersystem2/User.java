package usersystem2;

import java.util.HashMap;
import java.util.Map;

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
        boolean isGrantable=false;
        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            //本用户关于这个表的某一组权限
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)){
                //TODO 检查一下这个权限能否下发 如果不行 还可能有针对该表的其他permission可以下发
                byte permission1 = entry.getKey().getPermission();
//                if(可以下发)
//                  isGrantable=true;
//                  break;
//                else(不能下发 找下一个)
//                  continue;
            }
        }

        //检查是否能够下发权限
        return  isGrantable;
    }

    public void acquirePermission(String granterName, String database, String table, byte permission, int grantType) {
        Table table1 = new Table(new Database(database), table,permission);
        Permission permission1 = new Permission();
        permission1.setGrantedBy(UserManager2.getUserByName(granterName));
        permission1.setTarget(1);                   //默认目标为表
        permission1.setTable(table1);               //记录目标表
        permission1.setDatabase(table1.getDb());    //记录目标数据库
        permission1.setPermission(permission);      //设置权限位
        permission1.setGrantType(grantType);        //记录权限下发形式
    }
}
