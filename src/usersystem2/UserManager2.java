package usersystem2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager2 {
    //�û��洢�ļ�·��
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
        //����û����Ƿ��Ѵ���
        if (users.containsKey(user.getUsername())) {
            System.err.println("�û�" + user.getUsername() + "�Ѵ���,���ʧ��");
            return;
        }
        users.put(user.getUsername(), user);
        writeUsersToFile();
    }

    public static void deleteUser(String username) {
        if (!users.containsKey(username)) {
            System.err.println("�û�" + username + "������,ɾ��ʧ��");
            return;
        }
        users.remove(username);
        writeUsersToFile();
    }

    /**
     * �޸��û���Ϣ
     * @param username ԭ�û���
     * @param newname ���û���
     * @param newPaswrd ������
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
            System.err.println("�����û�������");
            return false;
        }

        User granter = users.get(granterName);
        User grantee = users.get(granteeName);

        //�����Ȩ���Ƿ�ӵ��Ȩ��
        if (granter.isGrantable(database, table, permission)){

            //��鱻��Ȩ���Ƿ�û�д�Ȩ�ޣ�û�вſ��Ի�ã�
            if (grantee.canBeAuthorized(database, table, permission)) {
                grantee.acquirePermission(granterName, database, table, permission, grantType);
                return true;
            }
            else {
                System.err.println("����Ȩ����ӵ������Ȩ��");
                return false;
            }

        } else {
            //��Ȩ�߲����·�
            System.err.println("��Ȩ����Ȩ�·�Ŀ��Ȩ��");
            return false;
        }
    }

    public static boolean revoke(String revokerName, String revokeeName, String database, String table, byte permission, int revokeType) throws Exception{
        if (!(users.containsKey(revokerName) && users.containsKey(revokeeName))) {
            System.err.println("�����û�������");
            return false;
        }

        User revoker = users.get(revokerName);
        User revokee = users.get(revokerName);

        //���revoker�Ƿ�����ջ�Ȩ��
        if (revoker.isRevokable(database, table, permission) && revokee.canBeRevoked(database, table, permission)){
            //���Խ���Ȩ�޳���
            revokee.revokePermission(revokerName, database, table, permission, revokeType);

        }
        return false;
    }

    //��dbuf�ļ��ж�ȡuserList
    private static void readUsersFromFile()  {
        BufferedReader bfr = null;
        users = new HashMap<>();
        try {
            File file = new File(fileDir);
            if(!file.exists()){
                throw new IOException("�û��б��ļ�δ�ҵ���������һ���µ�");
            }
            bfr = new BufferedReader(new FileReader(file));
            while (bfr.ready()) {
                String[] split = bfr.readLine().split("<====>");
                if (split.length != 2) continue;
                users.put(split[0], JSONObject.parseObject(split[1], User.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            //���ļ���ȡusersʧ��  ����һ���µ�users���� ��д���ļ�
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

    //���ڴ��е�userListд���ļ�
    private static synchronized void writeUsersToFile() {
        BufferedWriter bfw = null;
        try {
            //����֮ǰ�洢��users����
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
