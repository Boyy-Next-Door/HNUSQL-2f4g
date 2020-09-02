package usersystem2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager2 {
    //用户存储文件路径
    private static final String fileDir = "src/usersystem2/user_file.dbuf";

    private static HashMap<String, User> users;

    static {
        readUsersFromFile();
    }

    public static HashMap<String, User> getUsers() {
        return users;
    }

    public static void setUsers(HashMap<String, User> users) {
        UserManager2.users = users;
    }

    public static void addUser(User user) {
        //检查用户名是否已存在
        if (users.containsKey(user.getUsername())) {
            System.err.println("用户" + user.getUsername() + "已存在,添加失败");
            return;
        }
        users.put(user.getUsername(), user);
        writeUsersToFile();
    }

    public static void deleteUser(String username) {
        if (!users.containsKey(username)) {
            System.err.println("用户" + username + "不存在,删除失败");
            return;
        }
        users.remove(username);
        writeUsersToFile();
    }

    /**
     * 修改用户信息
     * @param username 原用户名
     * @param newname 新用户名
     * @param newPaswrd 新密码
     */
    public static void modifyName(String username, String newname, String newPaswrd){
        User user = getUserByName(username);
        user.setUsername(newname);
        user.setPassword(newPaswrd);
        writeUsersToFile();
    }

    public static User getUserByName(String username) {
        return users.get(username);
    }

    public static boolean grant(String granterName, String granteeName, String database, String table, byte permission, int grantType) throws Exception {
        if (!(users.containsKey(granterName) && users.containsKey(granteeName))) {
            System.err.println("参数用户不存在");
            return false;
        }

        User granter = users.get(granterName);
        User grantee = users.get(granteeName);

        //检查授权者是否拥有权限
        if (granter.isGrantable(database, table, permission)){

            //检查被授权者是否没有此权限（没有才可以获得）
            if (grantee.canBeAuthorized(database, table, permission)) {
                grantee.acquirePermission(granterName, database, table, permission, grantType);
                return true;
            }
            else {
                System.err.println("被授权者已拥有其中权限");
                return false;
            }

        } else {
            //授权者不能下发
            System.err.println("授权者无权下发目标权限");
            return false;
        }
    }

    public static boolean revoke(String revokerName, String revokeeName, String database, String table, byte permission, int revokeType) throws Exception{
        if (!(users.containsKey(revokerName) && users.containsKey(revokeeName))) {
            System.err.println("参数用户不存在");
            return false;
        }

        User revoker = users.get(revokerName);
        User revokee = users.get(revokerName);

        //检查revoker是否可以收回权限
        if (revoker.isRevokable(database, table, permission) && revokee.canBeRevoked(database, table, permission)){
            //可以进行权限撤销
            revokee.revokePermission(revokerName, database, table, permission, revokeType);

        }
        return false;
    }

    //从dbuf文件中读取userList
    private static void readUsersFromFile()  {
        BufferedReader bfr = null;
        users = new HashMap<>();
        try {
            File file = new File(fileDir);
            if(!file.exists()){
                throw new IOException("用户列表文件未找到，将创建一个新的");
            }
            bfr = new BufferedReader(new FileReader(file));
            while (bfr.ready()) {
                String[] split = bfr.readLine().split("<====>");
                if (split.length != 2) continue;
                users.put(split[0], JSONObject.parseObject(split[1], User.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            //从文件读取users失败  创建一个新的users集合 并写入文件
            users = new HashMap<String, User>();
            writeUsersToFile();
        } finally {
            try {
                if (bfr != null)
                    bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //将内存中的userList写入文件
    private static synchronized void writeUsersToFile() {
        BufferedWriter bfw = null;
        try {
            //覆盖之前存储的users内容
            bfw = new BufferedWriter(new FileWriter(new File(fileDir), false));
            for (Map.Entry<String, User> entry : users.entrySet()) {
                bfw.write(entry.getKey());
                bfw.write("<====>");
                bfw.write(JSON.toJSONString(entry.getValue()));
                bfw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bfw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //
    // public static void main(String[] args) {
    //     User user = new User();
    //     user.setUsername("yjz");
    //     user.setPassword("123");
    //     user.setPermissions(new HashMap<>());
    //     addUser(user);
    //
    // }
}
