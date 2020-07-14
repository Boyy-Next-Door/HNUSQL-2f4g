package usersystem;

import com.alibaba.fastjson.JSON;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainTest {
    public static void main(String[] args) throws IOException {
        test1();
        //test2();
        //test3();
    }
    //�������ṹ����α�������Ȩ���ɹ���
    public static void test1() throws IOException {
        //����admin�û�
        Admin admin = Admin.getAdmin();
        CommonUser u1 = new CommonUser("1");
        CommonUser u2 = new CommonUser("2");
        CommonUser u3 = new CommonUser("3");
        CommonUser u4 = new CommonUser("4");
        CommonUser u5 = new CommonUser("5");
        CommonUser u6 = new CommonUser("6");
        CommonUser u7 = new CommonUser("7");
        CommonUser u8 = new CommonUser("8");
        CommonUser u9 = new CommonUser("9");
        UserTree tree = new UserTree(admin);
        tree.addUserToTree(u1);
        tree.addUserToTree(u2);
        tree.addUserToTree(u3);
        tree.addUserToTree(u4);
        tree.addUserToTree(u5);
        tree.addUserToTree(u6);
        tree.addUserToTree(u7);
        tree.addUserToTree(u8);
        tree.addUserToTree(u9);

        tree.levelOrderTraversePrint(admin);
        System.out.println("-----------Ų�����------------");
        tree.shiftUser(u1,u1.getParent(), u3);
        tree.shiftUser(u4,u4.getParent(), u3);
        tree.shiftUser(u2,u2.getParent(), u5);
        tree.shiftUser(u7,u7.getParent(), u5);
        tree.shiftUser(u8,u8.getParent(), u1);
        tree.shiftUser(u9,u9.getParent(), u8);
        tree.levelOrderTraversePrint(admin);
        System.out.println("����" + tree.getNumOfUser() + "���û�");
        //System.out.println("���ĸ߶�Ϊ��" + tree.updateHeight(admin));
        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        System.out.println("�������е��û�list��");
        for(User u: tree.getUl()){
            System.out.print("user id:" + u.getId());
            if(u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.println();
        }
        System.out.println("-----------��Ȩtest------------");
        tree.processGrant(admin,u4, (byte) 0x00);
        tree.levelOrderTraversePrint(admin);
        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        System.out.println("�������е��û�list��");
        for(User u: tree.getUl()){
            System.out.print("user id:" + u.getId());
            if(u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.println();
        }
        //tree.levelOrderTraverse(tree.getRoot());
    }
    //���Ե���ģʽ���ɹ���
    public static void test2(){
        Admin admin1 = Admin.getAdmin();
        Admin admin2 = Admin.getAdmin();
        System.out.println("---------------�����ǡ�admin1��--------------------");
        System.out.println(admin1.getId());
        System.out.println(admin1.getName());
        System.out.println(admin1.getPassword());
        System.out.println(admin1.getParent());
        System.out.println(Integer.toBinaryString(admin1.getPermission()));
        System.out.println("---------------�����ǡ�admin2��--------------------");
        System.out.println(admin2.getId());
        System.out.println(admin2.getName());
        System.out.println(admin2.getPassword());
        System.out.println(admin2.getParent());
        System.out.println(Integer.toBinaryString(admin1.getPermission()));
    }
    //���Ե�¼
    public static void test3(){
        Admin admin = Admin.getAdmin();
        UserTree tree = new UserTree(admin);

        String name1 = "admin";
        String password1 = "123456";
        String name2 = "bayanwen";
        String password2 = "1234567";
        //1.���Ե�½�ɹ�������
        System.out.println(tree.logIn(name1, password1));
        //2.�û�������������
        System.out.println(tree.logIn(name2, password1));
        //3.�û��������������
        System.out.println(tree.logIn(name1, password2));

    }
}
