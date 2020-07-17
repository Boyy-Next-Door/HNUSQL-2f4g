package usersystem2;

import java.util.ArrayList;
import java.util.HashMap;

public class UserManager2 {
    private static HashMap<String, User> users;

    public static HashMap<String, User> getUsers() {
        return users;
    }

    public static void setUsers(HashMap<String, User> users) {
        UserManager2.users = users;
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static void deleteUser(String username) {
        users.remove(username);
    }

    public static boolean grant(String granterName, String granteeName, String database, String table, byte permission, int grantType) throws Exception {
        if (!(users.containsKey(granterName) && users.containsKey(granteeName))) {
            System.err.println("参数用户不存在");
            return false;
        }

        User granter = users.get(granterName);
        User grantee = users.get(granteeName);

        //检查授权者是否拥有权限且可以下发
        if (granter.isGrantable(database, table, permission)) {
            //授权者拥有对该表的权限，那么可以下发
            grantee.acquirePermission(granterName, database, table, permission, grantType);
            return true;
        } else {
            //授权者不能下发
            System.err.println("授权者无权下发目标权限");
            return false;
        }


    }

}
