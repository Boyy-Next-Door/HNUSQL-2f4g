package usersystem;

import sun.security.util.Length;

import java.io.ByteArrayInputStream;

public class MainTest {
    public static void main(String[] args) {
        //test1();
        //test2();
        //test3();
        //test4();
        //test5();
        //test6();
        //test7();
//        test8();
        test9();
    }

    public static void test9() {

        String row = new String(" 1         ����      ");
        String subString = getSubString(row, 11, 10);
        System.out.println(subString);
//        char[] toCharArray = new char[5000]; //�����ӵ�и��б�׼���ȵ�һ��char����  �������ݿ���������������
//        char[] toCharArray1 = row.toCharArray();
//        for (int i = 0; i < toCharArray1.length; i++) {
//            toCharArray[i] = toCharArray1[i];
//        }
//
//        for (int j = 0; j < toCharArray.length; j++) {
//            if (isChinese(toCharArray[j])) {
//                //�������ַ�������  �Ѻ�������char������Ų  ��һ����ǽ���0
//                for (int k = toCharArray.length - 1; k >= j + 2; k--) {
//                    toCharArray[k] = toCharArray[k - 1];
//                }
//                toCharArray[j + 1] = 127;
//            }
//        }
//
//        char[] chars = new char[10];
//        //��ȡ�ֶ�
//        for (int i = 11; i < 21; i++) {
//            chars[i - 11] = toCharArray[i];
//        }
//
//        //ȥ�������ַ�
//        for (int i = 0; i < chars.length; i++) {
//            if (chars[i] == 127) {
//                //�������ַ�������  �Ѻ�������char����ǰŲ  ��һ����ǽ���0
//                for (int k = i; k < chars.length - 1; k++) {
//                    chars[k] = chars[k + 1];
//                }
//                chars[chars.length - 1] = '\0';
//            }
//        }
//        System.out.println(chars);
//
//
//        row.toCharArray(); //�����ԭʼ���ȿ�����coldef�õ�
//        for (int i = 1; i < toCharArray.length; i++) {
//
//        }
//        byte[] bytes = row.getBytes();
//        System.out.println(row);


    }

    public static String getSubString(String row, int start, int size) {
        char[] toCharArray1 = row.toCharArray();
        int countChineseChar = 0;
        for (int i = 0; i < toCharArray1.length; i++) {
            if (isChinese(toCharArray1[i])) {
                countChineseChar++;
            }
        }
        char[] toCharArray = new char[toCharArray1.length + countChineseChar]; //�����ӵ�и��б�׼���ȵ�һ��char����  �������ݿ���������������
        for (int i = 0; i < toCharArray1.length; i++) {
            toCharArray[i] = toCharArray1[i];
        }

        for (int j = 0; j < toCharArray.length; j++) {
            if (isChinese(toCharArray[j])) {
                //�������ַ�������  �Ѻ�������char������Ų  ��һ����ǽ���0
                for (int k = toCharArray.length - 1; k >= j + 2; k--) {
                    toCharArray[k] = toCharArray[k - 1];
                }
                toCharArray[j + 1] = 127;
            }
        }

        char[] chars = new char[10];
        //��ȡ�ֶ�
        for (int i = start; i < start + size; i++) {
            chars[i - start] = toCharArray[i];
        }

        //ȥ�������ַ�
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 127) {
                //�������ַ�������  �Ѻ�������char����ǰŲ  ��һ����ǽ���0
                for (int k = i; k < chars.length - 1; k++) {
                    chars[k] = chars[k + 1];
                }
                chars[chars.length - 1] = '\0';
            }
        }
        return new String(chars);
    }


    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    //�������ṹ����α�������Ȩ�����ɣ�
    public static void test1() {
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
        tree.shiftUser(u1, u1.getParent(), u3);
        tree.shiftUser(u4, u4.getParent(), u3);
        tree.shiftUser(u2, u2.getParent(), u5);
        tree.shiftUser(u7, u7.getParent(), u5);
        tree.shiftUser(u8, u8.getParent(), u1);
        tree.shiftUser(u9, u9.getParent(), u8);
        tree.levelOrderTraversePrint(admin);
        System.out.println("����" + tree.getNumOfUser() + "���û�");

        printTreeInfo(tree);

        System.out.println("-----------��Ȩtest1------------");
        if (tree.processGrant(admin, u4, (byte) 0x10))
            tree.processGrantWith(admin, u4, (byte) 0x01);
        tree.levelOrderTraversePrint(admin);
        printTreeInfo(tree);

        System.out.println("-----------��Ȩtest2------------");
        if (tree.processGrant(u4, u5, (byte) 0x10))
            tree.processGrantWith(u4, u5, (byte) 0x01);
        tree.levelOrderTraversePrint(admin);
        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        printTreeInfo(tree);
    }

    //���Ե���ģʽ���ɹ���
    public static void test2() {
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

    //���Ե�¼���ɹ���
    public static void test3() {
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

    //����ά�����߶ȵ�bug��bug�Ѷ�����
    public static void test4() {
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
        tree.shiftUser(u1, u1.getParent(), u3);
        tree.shiftUser(u4, u4.getParent(), u3);
        tree.shiftUser(u2, u2.getParent(), u5);
        tree.shiftUser(u7, u7.getParent(), u5);
        tree.shiftUser(u8, u8.getParent(), u1);
        tree.shiftUser(u9, u9.getParent(), u8);
        tree.levelOrderTraversePrint(admin);
        System.out.println("����" + tree.getNumOfUser() + "���û�");
        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        System.out.println("-----------����û�------------");
        CommonUser u10 = new CommonUser("10");
        tree.addUserToTree(u10);
        tree.shiftUser(u10, u10.getParent(), u9);
        tree.levelOrderTraversePrint(admin);
        System.out.println("����" + tree.getNumOfUser() + "���û�");
        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
    }

    //������Ȩʱ��ؽ����ƶ����˲������ϣ���Ҫ�ã�
    public static void test5() {
        //����admin�û�
        Admin admin = Admin.getAdmin();
        CommonUser u1 = new CommonUser("1", "u1", "123456", (byte) 0x10);
        CommonUser u2 = new CommonUser("2", "u2", "123456", (byte) 0x10);
        CommonUser u3 = new CommonUser("3", "u3", "123456", (byte) 0x10);
        CommonUser u4 = new CommonUser("4", "u4", "123456", (byte) 0x10);
        CommonUser u5 = new CommonUser("5", "u5", "123456", (byte) 0x10);
        CommonUser u6 = new CommonUser("6", "u6", "123456", (byte) 0x10);
        CommonUser u7 = new CommonUser("7", "u7", "123456", (byte) 0x10);
        CommonUser u8 = new CommonUser("8", "u8", "123456", (byte) 0x10);
        CommonUser u9 = new CommonUser("9", "u9", "123456", (byte) 0x10);
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
        System.out.println("-----------��ʼ��Ȩ------------");
        tree.processGrant(admin, u3, (byte) 0x77);
        tree.processGrant(admin, u5, (byte) 0x73);
        tree.processGrant(admin, u6, (byte) 0xf3);
        tree.processGrant(u3, u1, (byte) 0x73);
        tree.processGrant(u3, u4, (byte) 0x33);
        tree.processGrant(u5, u2, (byte) 0x22);
        tree.processGrant(u5, u8, (byte) 0x11);
        tree.processGrant(u1, u7, (byte) 0x31);
        tree.processGrant(u7, u9, (byte) 0x10);

        CommonUser u10 = new CommonUser("10", "u10", "123456", (byte) 0x00);
        tree.addUserToTree(u10);
        //��Ȩʧ������1��ʧ��ԭ����Ȩ�ߵ�4λȫ0��������Ȩ�����Գɹ���
        // tree.processGrant(u9, u10, (byte)0x10);

        //��Ȩʧ������2��ʧ��ԭ����Ȩ��Ҫ�����Լ���Ȩ�ڸ����˵�Ȩ����ͬ1�������Գɹ���
        // tree.processGrant(u2, u10, (byte) 0x10);

        //��Ȩʧ������3��ʧ��ԭ��Ҫ�����Ȩ�޵ĵ���λ�����λ����ƥ�䣨���Գɹ���
        // tree.processGrant(u6, u10, (byte) 0x35);

        //��Ȩ�ɹ����������Գɹ���
        tree.processGrant(u8, u10, (byte) 0x10);

        tree.levelOrderTraversePrint(admin);

        System.out.println("-----------��������һ��Ų------------");
        //��Ȩʧ������4�����ԷǸ�ϵ�û�����Ȩ
        // tree.processGrant(u5, u7, (byte) 0x33);

        //��Ȩ�ɹ�������ֱ�Ӹ�ϵ������Ȩ
        tree.processGrant(u3, u7, (byte) 0x31);
        tree.levelOrderTraversePrint(admin);

        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        System.out.println("�������е��û�list��");
        for (User u : tree.getUl()) {
            System.out.print("user id:" + u.getId());
            if (u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.print(" isGrantee:" + u.isGrantee());
            System.out.println();
        }
    }

    //����0xff���ڴ��д�Ϊffffffff�Ƿ�Ӱ�����������ʣ�
    public static void test6() {
        byte b1 = (byte) 0x47;
        byte b2 = (byte) 0xad;

        byte p1_high4 = (byte) ((b1 & 0xf0) >> 4);
        byte p1_low4 = (byte) (b1 & 0x0f);
        //2.��ȡ����Ҫ�����permission�ĸ���λ�͵���λ
        byte p2_high4 = (byte) ((b2 & 0xf0) >> 4);
        byte p2_low4 = (byte) (b2 & 0x0f);

        String s1 = Integer.toBinaryString(p1_high4);
        String s2 = Integer.toBinaryString(p1_low4);
        String s3 = Integer.toBinaryString(p2_high4);
        String s4 = Integer.toBinaryString(p2_low4);

        char[] chars1 = s1.toCharArray();
        char[] chars2 = s2.toCharArray();
        char[] chars3 = s3.toCharArray();
        char[] chars4 = s4.toCharArray();

        chars1 = zero(chars1);
        chars2 = zero(chars2);
        chars3 = zero(chars3);
        chars4 = zero(chars4);

        System.out.println(chars1);
        System.out.println(chars2);
        System.out.println(chars3);
        System.out.println(chars4);
        System.out.println(Integer.toHexString(0xf3));
        System.out.println(Integer.toHexString(0x0f));
    }

    public static char[] zero(char[] ch) {
        if (ch.length == 4) {
            return ch;
        } else {
            char[] chars = new char[4];
            int len = ch.length;
            for (int i = len - 1; i >= 0; i--) {
                chars[i + 1] = ch[i];
            }
            len = len + 1;
            chars[0] = '0';
            if (len == 4)
                return chars;
            while (len < 4) {
                for (int i = len - 1; i >= 0; i--) {
                    chars[i + 1] = chars[i];
                }
                len = len + 1;
                chars[0] = '0';
            }
            return chars;
        }
    }

    //�������ֻ�ı�byte�ĸ�4λ
    public static void test7() {
        byte b1 = (byte) 0xe0;
        byte b2 = 0x33;
        byte b1_h = (byte) (b1 & 0xf0);
        byte b2_l = (byte) (b2 & 0x0f);
        //��b1_h��b2_l�����һ��
        byte haha = (byte) (b1_h + b2_l);
        System.out.println(Byte.toUnsignedInt(haha));
        System.out.println(Integer.toBinaryString(haha));
    }

    //��������Ȩ�޵Ļ�������Ȩ�����Ƿ�ᱻ���ء�����ɾ���û����û�����Ӱ��
    public static void test8() {
        Admin admin = Admin.getAdmin();
        CommonUser u1 = new CommonUser("1", "u1", "123456", (byte) 0x00);
        CommonUser u2 = new CommonUser("2", "u2", "123456", (byte) 0x00);
        CommonUser u3 = new CommonUser("3", "u3", "123456", (byte) 0x00);
        CommonUser u4 = new CommonUser("4", "u4", "123456", (byte) 0x00);
        CommonUser u5 = new CommonUser("5", "u5", "123456", (byte) 0x00);
        UserTree tree = new UserTree(admin);
        tree.addUserToTree(u1);
        tree.addUserToTree(u2);
        tree.addUserToTree(u3);
        tree.addUserToTree(u4);
        tree.addUserToTree(u5);
        tree.levelOrderTraversePrint(admin);

        System.out.println("-----------��ʼ��Ȩ------------");
        //admin �� u1 1001 1001���ֳ�2������������processGrant��processGrantWith��
        if (tree.processGrant(admin, u1, (byte) 0x90))
            tree.processGrantWith(admin, u1, (byte) 0x09);

        //admin �� u2 0011 0011��ͬ���ֳ�����������
        if (tree.processGrant(admin, u2, (byte) 0x30))
            tree.processGrantWith(admin, u2, (byte) 0x03);

        //u1 �� u3 0001 0000��ֻ����processGrant��
        tree.processGrant(u1, u3, (byte) 0x10);

        //u2 �� u4 0011 0011���ֳ�2������������processGrant��processGrantWith��
        if (tree.processGrant(u2, u4, (byte) 0x30))
            tree.processGrantWith(u2, u4, (byte) 0x03);

        //u4 �� u5 0001 0001��ͬ���ֳ�����������
        if (tree.processGrant(u4, u5, (byte) 0x10))
            tree.processGrantWith(u4, u5, (byte) 0x01);

        tree.levelOrderTraversePrint(admin);
        System.out.println("-----------���ϲ��Գɹ�------------");

        //����u4��u5��Ȩ0x33�������ϻ�ʧ�ܣ�ȷʵʧ���ˣ����Գɹ���
        // if (tree.processGrant(u4, u5, (byte) 0x30))
        //     tree.processGrantWith(u4, u5, (byte) 0x03);
        //
        // System.out.println(Integer.toBinaryString(admin.getPermission()));
        // System.out.println(Integer.toBinaryString(u1.getPermission()));
        // System.out.println(Integer.toBinaryString(u2.getPermission()));
        // System.out.println(Integer.toBinaryString(u3.getPermission()));
        // System.out.println(Integer.toBinaryString(u4.getPermission()));
        // System.out.println(Integer.toBinaryString(u5.getPermission()));

        //��u4��u5��Ȩ0x20�������ϻ�ɹ�(���Գɹ���
        // tree.processGrant(u4, u5, (byte) 0x20);
        // tree.levelOrderTraversePrint(admin);
        // //ԭ����0001 0001��0010 0000��ϣ�����0011 0001 = 49�������ȷ
        // System.out.println(Byte.toUnsignedInt(u5.getPermission()));
        // System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        System.out.println("-----------���ϲ��Գɹ�------------");

        // System.out.println("-----------ɾ���û�u5��Ҷ�ӣ�------------");
        // //����ɾ���û�u5
        // tree.delUserFromTree("u5");
        // tree.levelOrderTraversePrint(admin);
        // printTreeInfo(tree);

        // System.out.println("-----------ɾ���û�u4���м䣩------------");
        // //����ɾ���û�u4
        // tree.delUserFromTree("u4");
        // tree.levelOrderTraversePrint(admin);
        // printTreeInfo(tree);

        System.out.println("-----------ɾ���û�u2���м䣬���������㣩------------");
        //����ɾ���û�u4
        tree.delUserFromTree("u2");
        tree.levelOrderTraversePrint(admin);
        printTreeInfo(tree);

        // System.out.println("-----------ɾ���û�u6�������ڣ�------------");
        // //����ɾ���û�u4
        // tree.delUserFromTree("u6");
        // tree.levelOrderTraversePrint(admin);
        // printTreeInfo(tree);
        System.out.println("-----------���ϲ��Գɹ�------------");

    }

    public static void printTreeInfo(UserTree tree) {
        System.out.println("���ĸ߶�Ϊ��" + tree.getHeight());
        System.out.println("�������е��û�list��");
        for (User u : tree.getUl()) {
            System.out.print("user id:" + u.getId());
            if (u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.println();
        }
    }
}
