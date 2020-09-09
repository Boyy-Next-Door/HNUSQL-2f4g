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

        String row = new String(" 1         霍霍      ");
        String subString = getSubString(row, 11, 10);
        System.out.println(subString);
//        char[] toCharArray = new char[5000]; //这就是拥有该行标准长度的一个char数组  其中内容可能由于中文缩短
//        char[] toCharArray1 = row.toCharArray();
//        for (int i = 0; i < toCharArray1.length; i++) {
//            toCharArray[i] = toCharArray1[i];
//        }
//
//        for (int j = 0; j < toCharArray.length; j++) {
//            if (isChinese(toCharArray[j])) {
//                //如果这个字符是中文  把后面所有char都往后挪  插一个标记进来0
//                for (int k = toCharArray.length - 1; k >= j + 2; k--) {
//                    toCharArray[k] = toCharArray[k - 1];
//                }
//                toCharArray[j + 1] = 127;
//            }
//        }
//
//        char[] chars = new char[10];
//        //截取字段
//        for (int i = 11; i < 21; i++) {
//            chars[i - 11] = toCharArray[i];
//        }
//
//        //去掉特殊字符
//        for (int i = 0; i < chars.length; i++) {
//            if (chars[i] == 127) {
//                //如果这个字符是中文  把后面所有char都往前挪  插一个标记进来0
//                for (int k = i; k < chars.length - 1; k++) {
//                    chars[k] = chars[k + 1];
//                }
//                chars[chars.length - 1] = '\0';
//            }
//        }
//        System.out.println(chars);
//
//
//        row.toCharArray(); //这个的原始长度可以由coldef得到
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
        char[] toCharArray = new char[toCharArray1.length + countChineseChar]; //这就是拥有该行标准长度的一个char数组  其中内容可能由于中文缩短
        for (int i = 0; i < toCharArray1.length; i++) {
            toCharArray[i] = toCharArray1[i];
        }

        for (int j = 0; j < toCharArray.length; j++) {
            if (isChinese(toCharArray[j])) {
                //如果这个字符是中文  把后面所有char都往后挪  插一个标记进来0
                for (int k = toCharArray.length - 1; k >= j + 2; k--) {
                    toCharArray[k] = toCharArray[k - 1];
                }
                toCharArray[j + 1] = 127;
            }
        }

        char[] chars = new char[10];
        //截取字段
        for (int i = start; i < start + size; i++) {
            chars[i - start] = toCharArray[i];
        }

        //去掉特殊字符
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 127) {
                //如果这个字符是中文  把后面所有char都往前挪  插一个标记进来0
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

    //测试树结构、层次遍历与授权（存疑）
    public static void test1() {
        //创建admin用户
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
        System.out.println("-----------挪动结点------------");
        tree.shiftUser(u1, u1.getParent(), u3);
        tree.shiftUser(u4, u4.getParent(), u3);
        tree.shiftUser(u2, u2.getParent(), u5);
        tree.shiftUser(u7, u7.getParent(), u5);
        tree.shiftUser(u8, u8.getParent(), u1);
        tree.shiftUser(u9, u9.getParent(), u8);
        tree.levelOrderTraversePrint(admin);
        System.out.println("共有" + tree.getNumOfUser() + "个用户");

        printTreeInfo(tree);

        System.out.println("-----------授权test1------------");
        if (tree.processGrant(admin, u4, (byte) 0x10))
            tree.processGrantWith(admin, u4, (byte) 0x01);
        tree.levelOrderTraversePrint(admin);
        printTreeInfo(tree);

        System.out.println("-----------授权test2------------");
        if (tree.processGrant(u4, u5, (byte) 0x10))
            tree.processGrantWith(u4, u5, (byte) 0x01);
        tree.levelOrderTraversePrint(admin);
        System.out.println("树的高度为：" + tree.getHeight());
        printTreeInfo(tree);
    }

    //测试单例模式（成功）
    public static void test2() {
        Admin admin1 = Admin.getAdmin();
        Admin admin2 = Admin.getAdmin();
        System.out.println("---------------下面是“admin1”--------------------");
        System.out.println(admin1.getId());
        System.out.println(admin1.getName());
        System.out.println(admin1.getPassword());
        System.out.println(admin1.getParent());
        System.out.println(Integer.toBinaryString(admin1.getPermission()));
        System.out.println("---------------下面是“admin2”--------------------");
        System.out.println(admin2.getId());
        System.out.println(admin2.getName());
        System.out.println(admin2.getPassword());
        System.out.println(admin2.getParent());
        System.out.println(Integer.toBinaryString(admin1.getPermission()));
    }

    //测试登录（成功）
    public static void test3() {
        Admin admin = Admin.getAdmin();
        UserTree tree = new UserTree(admin);

        String name1 = "admin";
        String password1 = "123456";
        String name2 = "bayanwen";
        String password2 = "1234567";
        //1.可以登陆成功用例：
        System.out.println(tree.logIn(name1, password1));
        //2.用户不存在用例：
        System.out.println(tree.logIn(name2, password1));
        //3.用户密码错误用例：
        System.out.println(tree.logIn(name1, password2));

    }

    //测试维护树高度的bug（bug已订正）
    public static void test4() {
        //创建admin用户
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
        System.out.println("-----------挪动结点------------");
        tree.shiftUser(u1, u1.getParent(), u3);
        tree.shiftUser(u4, u4.getParent(), u3);
        tree.shiftUser(u2, u2.getParent(), u5);
        tree.shiftUser(u7, u7.getParent(), u5);
        tree.shiftUser(u8, u8.getParent(), u1);
        tree.shiftUser(u9, u9.getParent(), u8);
        tree.levelOrderTraversePrint(admin);
        System.out.println("共有" + tree.getNumOfUser() + "个用户");
        System.out.println("树的高度为：" + tree.getHeight());
        System.out.println("-----------添加用户------------");
        CommonUser u10 = new CommonUser("10");
        tree.addUserToTree(u10);
        tree.shiftUser(u10, u10.getParent(), u9);
        tree.levelOrderTraversePrint(admin);
        System.out.println("共有" + tree.getNumOfUser() + "个用户");
        System.out.println("树的高度为：" + tree.getHeight());
    }

    //测试授权时相关结点的移动（此测试作废，不要用）
    public static void test5() {
        //创建admin用户
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
        System.out.println("-----------开始授权------------");
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
        //授权失败用例1，失败原因：授权者低4位全0，不可授权（测试成功）
        // tree.processGrant(u9, u10, (byte)0x10);

        //授权失败用例2，失败原因：授权者要授予自己无权授给别人的权利（同1）（测试成功）
        // tree.processGrant(u2, u10, (byte) 0x10);

        //授权失败用例3，失败原因：要授予的权限的低四位与高四位不相匹配（测试成功）
        // tree.processGrant(u6, u10, (byte) 0x35);

        //授权成功用例（测试成功）
        tree.processGrant(u8, u10, (byte) 0x10);

        tree.levelOrderTraversePrint(admin);

        System.out.println("-----------连带子树一块挪------------");
        //授权失败用例4：测试非父系用户的授权
        // tree.processGrant(u5, u7, (byte) 0x33);

        //授权成功用例：直接父系重新授权
        tree.processGrant(u3, u7, (byte) 0x31);
        tree.levelOrderTraversePrint(admin);

        System.out.println("树的高度为：" + tree.getHeight());
        System.out.println("遍历树中的用户list：");
        for (User u : tree.getUl()) {
            System.out.print("user id:" + u.getId());
            if (u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.print(" isGrantee:" + u.isGrantee());
            System.out.println();
        }
    }

    //测试0xff在内存中存为ffffffff是否影响结果（有疑问）
    public static void test6() {
        byte b1 = (byte) 0x47;
        byte b2 = (byte) 0xad;

        byte p1_high4 = (byte) ((b1 & 0xf0) >> 4);
        byte p1_low4 = (byte) (b1 & 0x0f);
        //2.获取被将要授予的permission的高四位和低四位
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

    //测试如何只改变byte的高4位
    public static void test7() {
        byte b1 = (byte) 0xe0;
        byte b2 = 0x33;
        byte b1_h = (byte) (b1 & 0xf0);
        byte b2_l = (byte) (b2 & 0x0f);
        //把b1_h和b2_l组合在一起
        byte haha = (byte) (b1_h + b2_l);
        System.out.println(Byte.toUnsignedInt(haha));
        System.out.println(Integer.toBinaryString(haha));
    }

    //测试已有权限的话，再授权请求是否会被驳回、测试删除用户对用户树的影响
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

        System.out.println("-----------开始授权------------");
        //admin 给 u1 1001 1001（分成2步操作，调用processGrant与processGrantWith）
        if (tree.processGrant(admin, u1, (byte) 0x90))
            tree.processGrantWith(admin, u1, (byte) 0x09);

        //admin 给 u2 0011 0011（同样分成两步操作）
        if (tree.processGrant(admin, u2, (byte) 0x30))
            tree.processGrantWith(admin, u2, (byte) 0x03);

        //u1 给 u3 0001 0000（只调用processGrant）
        tree.processGrant(u1, u3, (byte) 0x10);

        //u2 给 u4 0011 0011（分成2步操作，调用processGrant与processGrantWith）
        if (tree.processGrant(u2, u4, (byte) 0x30))
            tree.processGrantWith(u2, u4, (byte) 0x03);

        //u4 给 u5 0001 0001（同样分成两步操作）
        if (tree.processGrant(u4, u5, (byte) 0x10))
            tree.processGrantWith(u4, u5, (byte) 0x01);

        tree.levelOrderTraversePrint(admin);
        System.out.println("-----------以上测试成功------------");

        //再用u4给u5授权0x33，理论上会失败（确实失败了，测试成功）
        // if (tree.processGrant(u4, u5, (byte) 0x30))
        //     tree.processGrantWith(u4, u5, (byte) 0x03);
        //
        // System.out.println(Integer.toBinaryString(admin.getPermission()));
        // System.out.println(Integer.toBinaryString(u1.getPermission()));
        // System.out.println(Integer.toBinaryString(u2.getPermission()));
        // System.out.println(Integer.toBinaryString(u3.getPermission()));
        // System.out.println(Integer.toBinaryString(u4.getPermission()));
        // System.out.println(Integer.toBinaryString(u5.getPermission()));

        //用u4给u5授权0x20，理论上会成功(测试成功）
        // tree.processGrant(u4, u5, (byte) 0x20);
        // tree.levelOrderTraversePrint(admin);
        // //原来的0001 0001与0010 0000组合，就是0011 0001 = 49，结果正确
        // System.out.println(Byte.toUnsignedInt(u5.getPermission()));
        // System.out.println("树的高度为：" + tree.getHeight());
        System.out.println("-----------以上测试成功------------");

        // System.out.println("-----------删除用户u5（叶子）------------");
        // //测试删除用户u5
        // tree.delUserFromTree("u5");
        // tree.levelOrderTraversePrint(admin);
        // printTreeInfo(tree);

        // System.out.println("-----------删除用户u4（中间）------------");
        // //测试删除用户u4
        // tree.delUserFromTree("u4");
        // tree.levelOrderTraversePrint(admin);
        // printTreeInfo(tree);

        System.out.println("-----------删除用户u2（中间，子树有两层）------------");
        //测试删除用户u4
        tree.delUserFromTree("u2");
        tree.levelOrderTraversePrint(admin);
        printTreeInfo(tree);

        // System.out.println("-----------删除用户u6（不存在）------------");
        // //测试删除用户u4
        // tree.delUserFromTree("u6");
        // tree.levelOrderTraversePrint(admin);
        // printTreeInfo(tree);
        System.out.println("-----------以上测试成功------------");

    }

    public static void printTreeInfo(UserTree tree) {
        System.out.println("树的高度为：" + tree.getHeight());
        System.out.println("遍历树中的用户list：");
        for (User u : tree.getUl()) {
            System.out.print("user id:" + u.getId());
            if (u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.println();
        }
    }
}
