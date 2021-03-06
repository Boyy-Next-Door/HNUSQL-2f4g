package usersystem2;

import usersystem.UserManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainTest {
    public static void main(String[] args) throws Exception {
        // test1();
        test2();
        // test3();
        // test4();
        // test5();
        // test6();
        // test7();
    }

    private static void test7(){
        User u1 = new User("admin", "123456");
        User u2 = new User("u2", "123456");
        User u3 = new User("u3", "123456");
        User u4 = new User("u4", "123456");
        User u5 = new User("u5", "123456");
        User u6 = new User("u6","123456");
        User u7 = new User("u7","123456");

        Database db1 = new Database("db1");
        Table table1 = new Table(db1,"table1", (byte)0xff);
        HashMap<Table, Permission> map = new HashMap<>();
        Permission permission = new Permission();

        permission.setDatabase(db1);
        permission.setTable(table1);
        permission.setGrantedBy(u1);
        permission.setGrantType(2);
        permission.setTarget(1);
        permission.setPermission((byte)0xff);
        map.put(table1,permission);

        u1.setPermissions(map);

        UserManager2.addUser(u1);
        UserManager2.addUser(u2);
        UserManager2.addUser(u3);
        UserManager2.addUser(u4);
        UserManager2.addUser(u5);
        UserManager2.addUser(u6);
        UserManager2.addUser(u7);

        // User admin = UserManager2.getUserByName("admin");
        // User u2 = UserManager2.getUserByName("u2");
        // User u3 = UserManager2.getUserByName("u3");
        // User u4 = UserManager2.getUserByName("u4");
        // User u5 = UserManager2.getUserByName("u5");
        // User u6 = UserManager2.getUserByName("u6");
        // User u7 = UserManager2.getUserByName("u7");

        boolean grant1 = false;
        boolean grant2 = false;
        boolean grant3 = false;
        boolean grant4 = false;
        boolean grant5 = false;
        boolean grant6 = false;
        boolean grant7 = false;
        boolean grant8 = false;
        boolean revoke1 = false;
        boolean revoke2 = false;
        boolean revoke3 = false;

        try {
            grant1 = UserManager2.grant("admin","u2","db1","table1",(byte)0x77,1);
            grant2 = UserManager2.grant("admin","u3","db1","table1",(byte)0x44, 2);
            grant3 = UserManager2.grant("u2", "u4","db1","table1",(byte)0x44, 2);
            grant4 = UserManager2.grant("u4","u6","db1","table1",(byte)0x40, 0);
            grant5 = UserManager2.grant("u2", "u5", "db1", "table1", (byte)0x40, 0);
            grant6 = UserManager2.grant("u2", "u7", "db1", "table1", (byte)0x44, 1);


            revoke1 = UserManager2.revoke("admin","u2","db1","table1",(byte)0x70);
            revoke2 = UserManager2.revoke("admin","u3","db1","table1",(byte)0x40);
            System.out.println("mission complete");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void test6() {
        try {
            File file = new File("src/usersystem2/user_file2.dbuf");
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            try {
                Object object = objectInputStream.readObject();
                HashMap<String, User> users = (HashMap<String, User>) object;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    public static void test5() throws Exception {
//        User u1 = new User("admin", "123456");
//        User u2 = new User("u2", "123456");
//
//        Database db1 = new Database("db1");
//        Table table1 = new Table(db1,"table1", (byte)0xff);
//        HashMap<Table, Permission> map = new HashMap<>();
//        Permission permission = new Permission();
//
//        permission.setDatabase(db1);
//        permission.setTable(table1);
//        permission.setGrantedBy(u1);
//        permission.setGrantType(2);
//        permission.setTarget(1);
//        permission.setPermission((byte)0xff);
//        map.put(table1,permission);
//
//        u1.setPermissions(map);
//
//        UserManager2.addUser(u1);
//        UserManager2.addUser(u2);


//
//        User u22 = UserManager2.getUserByName("admin");
//        boolean grant1 = UserManager2.grant("admin", "u2", "db1", "table1", (byte) 0x80, 0);
//        System.out.println(grant1);
        User admin = UserManager2.getUserByName("admin");
        User u2 = UserManager2.getUserByName("u2");
        System.out.println(admin);
        System.out.println(u2);

    }
    //测试用户的创建和删除、用户信息的修改（成功）
    public static void test1(){
        UserManager2.deleteUser("u1");
        UserManager2.deleteUser("u2");
        UserManager2.deleteUser("u3");

        User u1 = new User("u1", "123456");
        User u2 = new User("u2", "123456");
        User u3 = new User("u3", "123456");
        UserManager2.addUser(u1);
        UserManager2.addUser(u2);
        UserManager2.addUser(u3);
        System.out.println(u1.toString());
        System.out.println(u2.toString());
        System.out.println(u3.toString());

        System.out.println("----------------------------");

        UserManager2.modifyName("u1","u","123456");
        System.out.println(u1.toString());

        UserManager2.modifyName("u2", "uu", "1234");
        System.out.println(u2.toString());

        UserManager2.modifyName("u3", "uuu", "123");
        System.out.println(u3.toString());



    }
    //测试授权、撤销权限
    public static void test2(){
        User u1 = new User("admin", "123456");
        User u2 = new User("u2", "123456");
        User u3 = new User("u3", "123456");
        User u4 = new User("u4", "123456");
        User u5 = new User("u5", "123456");

        Database db1 = new Database("db1");
        Table table1 = new Table(db1,"table1", (byte)0xff);
        HashMap<Table, Permission> map = new HashMap<>();
        Permission permission = new Permission();

        permission.setDatabase(db1);
        permission.setTable(table1);
        permission.setGrantedBy(u1);
        permission.setGrantType(2);
        permission.setTarget(1);
        permission.setPermission((byte)0xff);
        map.put(table1,permission);

        u1.setPermissions(map);

        UserManager2.addUser(u1);
        UserManager2.addUser(u2);
        UserManager2.addUser(u3);
        UserManager2.addUser(u4);
        UserManager2.addUser(u5);

        // User admin = UserManager2.getUserByName("admin");
        // User u2 = UserManager2.getUserByName("u2");
        // User u3 = UserManager2.getUserByName("u3");
        // User u4 = UserManager2.getUserByName("u4");
        // User u5 = UserManager2.getUserByName("u5");


        boolean grant1 = false;
        boolean grant2 = false;
        boolean grant3 = false;
        boolean grant4 = false;
        boolean grant5 = false;
        boolean grant6 = false;
        boolean grant7 = false;
        boolean grant8 = false;
        boolean revoke1 = false;
        boolean revoke2 = false;
        boolean revoke3 = false;

        try {
            // grant1 = UserManager2.grant("u1", "u2", "db1", "table1", (byte) 0x80, 0);
            // grant2 = UserManager2.grant("u1", "u2", "db1","table1",(byte) 0x40, 0);
            // grant3 = UserManager2.grant("u1", "u3", "db1","table1",(byte) 0x40, 0);
            // grant4 = UserManager2.grant("u1", "u3", "db1","table1",(byte) 0x80, 0);
            // grant5 = UserManager2.grant("u1", "u2", "db1","table1",(byte) 0x20, 0);
            // grant6 = UserManager2.grant("u1", "u3", "db1","table1",(byte) 0x20, 0);
            // grant7 = UserManager2.grant("u1", "u3", "db1","table1",(byte) 0x10, 0);
            // grant8 = UserManager2.grant("u1", "u2", "db1","table1",(byte) 0x10, 0);
            //为什么给两个用户先授权0x40后授权0x80时grantTo就没有问题，而反过来就有问题呢（grant1在前，u1的grantTo就有4个记录，而grant1在最后，u1的grantTo只有2个记录（正确的）？
            //上面的已解决

            grant1 = UserManager2.grant("admin", "u2", "db1", "table1", (byte) 0x80, 0);
            grant2 = UserManager2.grant("admin", "u2", "db1","table1",(byte) 0x66, 1);
            grant3 = UserManager2.grant("u2", "u3", "db1","table1",(byte) 0x22, 2);
            grant4 = UserManager2.grant("u3", "u4", "db1","table1",(byte) 0x22, 1);
            grant5 = UserManager2.grant("u4", "u5", "db1","table1",(byte) 0x20, 0);
            // revoke1 = UserManager2.revoke("admin","u2","db1","table1",(byte) 0x20);
            revoke2 = UserManager2.revoke("u2","u3","db1","table1",(byte) 0x20);//成功
            revoke3 = UserManager2.revoke("admin","u4","db1","table1",(byte) 0x20);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(grant1);
        // System.out.println(grant2);
        // System.out.println(grant3);
        // System.out.println(grant4);
        // System.out.println(grant5);
        // System.out.println(grant6);
        // System.out.println(grant7);
        // System.out.println(grant8);
        // System.out.println(revoke1);
        // System.out.println(revoke2);
        // System.out.println(revoke3);
    }

    //测试modify函数（成功）
    public static void test3(){
        User u1 = new User("u1", "123456");
        UserManager2.addUser(u1);
        byte a = (byte)0xcc;
        byte b = (byte)0x80;
        byte c = (byte)0x40;
        byte b1 = u1.modify(a,b);
        byte b2 = u1.modify(b1,c);
    }

    //测试split函数（成功）
    public static void test4(){
        User u1 = new User("u1", "123456");
        UserManager2.addUser(u1);
        byte a = (byte)0xf2;
        ArrayList<Byte> split = u1.split(a);
        for(int i = 0; i < split.size(); i++){
            System.out.println(split.get(i).byteValue());
        }
    }
}
