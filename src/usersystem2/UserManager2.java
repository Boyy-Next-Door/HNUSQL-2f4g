package usersystem2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import usersystem.UserTree;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager2 {

    //用户存储文件路径
    private static final String fileDir = "src/usersystem2/user_file.dbuf";
    private static final String fileDir2 = "src/usersystem2/user_file2.dbuf";

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
     *
     * @param username  原用户名
     * @param newname   新用户名
     * @param newPaswrd 新密码
     */
    public static void modifyName(String username, String newname, String newPaswrd) {
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
        if (granter.isGrantable(database, table, permission)) {

            //检查被授权者是否没有此权限（没有才可以获得）
            if (grantee.canBeAuthorized(database, table, permission)) {
                grantee.acquirePermission(granterName, database, table, permission, grantType);
                writeUsersToFile();

                return true;
            } else {
                System.err.println("被授权者已拥有其中权限");
                return false;
            }

        } else {
            //授权者不能下发
            System.err.println("授权者无权下发目标权限");
            return false;
        }
    }

    public static boolean revoke(String revokerName, String revokeeName, String database, String table, byte permission) throws Exception {
        if (!(users.containsKey(revokerName) && users.containsKey(revokeeName))) {
            System.err.println("参数用户不存在");
            return false;
        }

        User revoker = users.get(revokerName);
        User revokee = users.get(revokeeName);

        if (revoker.isRevokable(database, table, permission)) {

            if (revokee.canBeRevoked(database, table, permission)) {
                revokee.revokePermission(revokerName, database, table, permission, 0);
                writeUsersToFile();

                return true;
            } else {
                System.err.println("被撤销用户没有要撤销的权限");
                return false;
            }
        } else {
            System.err.println("撤销用户无权撤销此权限");
            return false;
        }
    }

    //从dbuf文件中读取userList
    private static void readUsersFromFile() {
//        BufferedReader bfr = null;
//        users = new HashMap<>();
//        try {
//            File file = new File(fileDir);
//            if(!file.exists()){
//                throw new IOException("用户列表文件未找到，将创建一个新的");
//            }
//            bfr = new BufferedReader(new FileReader(file));
//            while (bfr.ready()) {
//                String[] split = bfr.readLine().split("<====>");
//                if (split.length != 2) continue;
//                User value = JSONObject.parseObject(split[1],User.class);
//                users.put(split[0], value);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            //从文件读取users失败  创建一个新的users集合 并写入文件
//            users = new HashMap<String, User>();
//            writeUsersToFile();
//        } finally {
//            try {
//                if (bfr != null)
//                    bfr.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            File file = new File(fileDir2);
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            try {
                Object object = objectInputStream.readObject();
                users = (HashMap<String, User>) object;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
//            e.printStackTrace();
            //用户文件尚未初始化过 这里初始化一个空的集合写进去
            users = new HashMap<String, User>();
            writeUsersToFile();
        }
    }

    //将内存中的userList写入文件
    private static synchronized void writeUsersToFile() {
//        BufferedWriter bfw = null;
//        try {
//            //覆盖之前存储的users内容
//            bfw = new BufferedWriter(new FileWriter(new File(fileDir), false));
//            for (Map.Entry<String, User> entry : users.entrySet()) {
//                bfw.write(entry.getKey());
//                bfw.write("<====>");
//                bfw.write(JSON.toJSONString(entry.getValue()));
//                bfw.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                bfw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        ObjectOutputStream objectOutputStream = null;
        try {
            //覆盖之前的dbuf文件
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(fileDir2), false));
            objectOutputStream.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //登陆
    public static boolean login(String username, String password) {
        User userByName = getUserByName(username);
        if (userByName == null || !userByName.getPassword().equals(password)) {
            return false;
        }

        return true;
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
