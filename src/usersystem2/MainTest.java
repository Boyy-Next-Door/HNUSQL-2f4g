package usersystem2;

import java.util.ArrayList;
import java.util.HashMap;

public class MainTest {
    public static void main(String[] args) {
        //test1();
        test2();
        // test3();
        // test4();
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
    //测试授权
    public static void test2(){
        User u1 = new User("u1", "123456");
        User u2 = new User("u2", "123456");
        User u3 = new User("u3", "123456");


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
        boolean grant1 = false;
        boolean grant2 = false;
        boolean grant3 = false;
        boolean grant4 = false;
        boolean grant5 = false;
        boolean grant6 = false;

        try {
            // grant1 = UserManager2.grant("u1", "u2", "db1", "table1", (byte) 0x80, 0);
            grant2 = UserManager2.grant("u1", "u2", "db1","table1",(byte) 0x40, 0);
            grant3 = UserManager2.grant("u1", "u3", "db1","table1",(byte) 0x40, 0);
            grant4 = UserManager2.grant("u1", "u3", "db1","table1",(byte) 0x80, 0);
            grant1 = UserManager2.grant("u1", "u2", "db1", "table1", (byte) 0x80, 0);

            //为什么给两个用户先授权0x40后授权0x80时grantTo就没有问题，而反过来就有问题呢（grant1在前，u1的grantTo就有4个记录，而grant1在最后，u1的grantTo只有2个记录（正确的）？

            // grant5 = UserManager2.grant("u1", "u2", "db1","table1",(byte)0x60, 0);
            // grant5 = UserManager2.grant("u1", "u2", "db1", "table2", (byte)0x10, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(grant1);
        System.out.println(grant2);
        System.out.println(grant3);
        System.out.println(grant4);
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
