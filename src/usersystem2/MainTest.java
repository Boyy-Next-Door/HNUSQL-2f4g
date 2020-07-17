package usersystem2;

public class MainTest {
    public static void main(String[] args) {
        //test1();
        test2();
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
        UserManager2.addUser(u1);
        UserManager2.addUser(u2);
        UserManager2.addUser(u3);
    }
}
