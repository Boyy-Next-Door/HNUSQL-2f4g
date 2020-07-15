package usersystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class UserTree implements Serializable {
    private static final long serialVersionUID = 2L;

    private Admin root;
    private int height;
    private int numOfUser;
    private ArrayList<User> ul = new ArrayList<>();

    public UserTree(Admin admin) {
        this.root = admin;
        ul.add(root);
        numOfUser = 1;
        height = 1;
    }

    public UserTree(){

    }
    public User getRoot() {
        return root;
    }

    /**  树的根节点不需要修改
     public void setRoot(Admin root) {
     this.root = root;
     }*/

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNumOfUser() {
        return numOfUser;
    }

    public ArrayList<User> getUl() {
        return ul;
    }

    public void setRoot(Admin root) {
        this.root = root;
    }

    public void setNumOfUser(int numOfUser) {
        this.numOfUser = numOfUser;
    }

    public void setUl(ArrayList<User> ul) {
        this.ul = ul;
    }

    @Override
    public String toString() {
        return "UserTree{" +
                "root=" + root +
                ", height=" + height +
                ", numOfUser=" + numOfUser +
                ", ul=" + ul +
                '}';
    }

    /**
     * 借助队列层次遍历树，并打印id
     * @param tmp 遍历开始的结点
     */
    public void levelOrderTraversePrint(User tmp){
        if (tmp == null) return;
        LinkedList<User> queue = new LinkedList<>();
        queue.add(tmp);
        User curr;
        while (!queue.isEmpty()){
            curr = queue.element();
            queue.remove();
            System.out.print(curr.getId() + " ");

            if (!curr.getChildren().isEmpty()){
                queue.addAll(curr.getChildren());
            }
        }
        System.out.println();
    }

    public User levelOrderTraverse(String name){
        LinkedList<User> queue = new LinkedList<>();
        queue.add(root);
        User curr;
        while (!queue.isEmpty()){
            curr = queue.element();
            if (name.equals(curr.getName())){
                return curr;
            }
            else queue.remove();
            if (!curr.getChildren().isEmpty()){
                queue.addAll(curr.getChildren());
            }
        }
        return null;
    }
    /**
     * 维护树的height属性
     */
    public int updateHeight(User user){
        if (user.getChildren().isEmpty()) {
            return user.getLevel();
        }
        else{
            int max = user.getChildren().get(0).getLevel();
            for(User child: user.getChildren()){
                int h1 = updateHeight(child);
                if (max < h1)
                    max = h1;
            }
            return max;
        }
    }
    /**
     * 向用户树中添加结点
     * @param user 要添加的用户
     */
    public void addUserToTree(CommonUser user){
        root.addChild(user);
        ul.add(user);
        user.setLevel(root.getLevel() + 1);
        numOfUser = numOfUser + 1;
        height = updateHeight(root);
    }
    /**
     * 碰到grant字句时，需要将用户移动到给它授权的结点下面
     * @param u1 需要移动的结点
     * @param p1 之前的父结点
     * @param p2 移动之后的父结点
     */
    public void shiftUser(CommonUser u1, User p1, User p2){

        //1.更改u1的parent属性
        u1.setParent(p2);
        //2.在p1的children列表中删去u1
        Iterator iterator = p1.getChildren().iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals(u1)){
                iterator.remove();
                break;
            }
        }
        //3.在p2的children列表中加入u1
        p2.addChild(u1);
        //4.更改u1的level属性
        u1.setLevel(p2.getLevel() + 1);
        //5.更新树的高度
        height = updateHeight(root);
    }

    /**
     * 给传入的字符数组高位补0，返回长度为4的字符数组（为不满4位的二进制数高位补0）
     * @param ch 传进的数组
     * @return  补0后的数组
     */
    public char[] zeroize(char[] ch){
        if (ch.length == 4){
            return ch;
        }
        else{
            char[] chars = new char[4];
            int len = ch.length;
            for(int i = len - 1; i >= 0; i--){
                chars[i+1] = ch[i];
            }
            len = len + 1;
            chars[0] = '0';
            if (len == 4)
                return chars;
            while(len < 4){
                for(int i = len - 1; i >= 0; i--){
                    chars[i+1] = chars[i];
                }
                len = len + 1;
                chars[0] = '0';
            }
            return chars;
        }
    }

    /**
     * 能给别人的不能多于本身已有的。对p1低四位p2高四位关系的判断，
     * 和对p2高四位和p2低四位关系的判断方法相同，抽出方法
     * @param b1  本身已有的
     * @param b2  给别人的
     * @return  true或false，表示能不能给
     */
    public boolean isValid(byte b1, byte b2){
        byte xor1 = (byte) (b1 ^ b2);
        String s1 = Integer.toBinaryString(xor1);
        System.out.println("s1 = " + s1);
        char[] chars1 = s1.toCharArray();

        byte and1 = (byte) (xor1 & b1);
        String s2 = Integer.toBinaryString(and1);
        System.out.println("s2 = " + s2);
        char[] chars2 = s2.toCharArray();

        chars1 = zeroize(chars1);
        chars2 = zeroize(chars2);

        System.out.println(chars1);
        System.out.println(chars2);

        for(int i = 0; i < 4; i++){
            if (chars1[i] =='1' && chars2[i] != '1'){
                return false;
            }
        }
        return true;
    }
    /**
     * 判断授权是否合法
     * @param p1_low4  授权者权限的低四位
     * @param p2_high4  要授予的权限的高四位
     * @param p2_low4  要授予的权限的低四位
     * @return true则授权合法，false则授权不合法
     */
    public boolean isPermissionLegal(byte p1_low4, byte p2_high4, byte p2_low4){
        if (isValid(p1_low4, p2_high4) && isValid(p2_high4, p2_low4))
            return true;
        else return false;
    }

    /**
     * 处理grant子句（对于结点位置更新部分还有要思考更改的地方）
     * @param u1  授权用户
     * @param u2  被授权用户
     * @param permission  要授予的权限
     * @return  是否授权成功
     */
    public boolean processGrant(User u1, CommonUser u2, byte permission){
        //1.获取授权用户的permission低四位
        byte p1_low4 = (byte) (u1.getPermission() & 0x0f);
        //2.获取被将要授予的permission的高四位和低四位
        byte p2_high4 = (byte) ((permission & 0xf0) >> 4);
        byte p2_low4 = (byte) (permission & 0x0f);
        //3.判断是否可以授权
        if (isPermissionLegal(p1_low4, p2_high4, p2_low4)) {
            //可以授权
            //(1)更改u2的permission属性
            u2.setPermission(permission);
            //(2)挪动u2的结点
            shiftUser(u2, u2.getParent(), u1);
            //(3)更新树的高度
            height = updateHeight(root);
            return true;
        }
        //不可以授权
        else{
            System.err.println("Unauthorized authorization!");
            return false;
        }
    }

    public boolean logIn(String name, String password){
        User user = levelOrderTraverse(name);
        //1.返回null说明用户名找不到，用户不存在
        if (user == null){
            System.err.println("The user does not exist!");
            return false;
        }
        else{
            //2.用户名存在，密码也匹配，登陆成功
            if(password.equals(user.getPassword())){
                System.out.println("Log in successfully! " + "Welcome, " + user.getName());
                return true;
            }
            //3.用户名存在，但是密码不匹配
            else{
                System.err.println("Incorrect password!");
                return false;
            }
        }
    }


}
