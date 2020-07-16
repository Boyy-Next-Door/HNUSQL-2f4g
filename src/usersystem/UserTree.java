package usersystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        if(levelOrderTraverse(user.getName()) == null){
            root.addChild(user);
            ul.add(user);
            user.setLevel(root.getLevel() + 1);
            numOfUser = numOfUser + 1;
            height = updateHeight(root);
        }
        else{
            System.err.println("The user is exist!");
        }
    }

    /**
     * 删除用户（如何判断当前登录用户是管理员？）
     * @param name 要删除的用户的用户名
     */
    public boolean delUserFromTree(String name){
        //1.查找是否有这个用户
        User user = levelOrderTraverse(name);
        if (user == null){
            System.err.println("The user to delete does not exist!");
            return false;
        }
        else{
            //被删除结点的子结点上移
            List<User> children = user.getChildren();
            for(int i = 0; i < children.size(); ){
                shiftUser(children.get(i), user, user.getParent());
            }
            // for (User u: children){
            //     shiftUser(u, user, user.getParent());
            // }

            //将被删除的结点从其父亲的结点列表中删除
            user.getParent().getChildren().remove(user);

            //将被删除的结点从UserTree的ul中删除
            this.ul.remove(user);
            return true;
        }
    }
    /**
     * 碰到grant字句时，需要将用户移动到给它授权的结点下面
     * @param u1 需要移动的结点
     * @param p1 之前的父结点
     * @param p2 移动之后的父结点
     */
    public void shiftUser(User u1, User p1, User p2){

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
        //5.更改u1的孩子的level属性
        if (!u1.getChildren().isEmpty()){
            for(User child: u1.getChildren()){
                shiftUser(child, u1, u1);
            }
        }
        //6.更新树的高度
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
        //System.out.println("s1 = " + s1);
        char[] chars1 = s1.toCharArray();

        byte and1 = (byte) (xor1 & b1);
        String s2 = Integer.toBinaryString(and1);
        //System.out.println("s2 = " + s2);
        char[] chars2 = s2.toCharArray();

        chars1 = zeroize(chars1);
        chars2 = zeroize(chars2);

        // System.out.println(chars1);
        // System.out.println(chars2);

        for(int i = 0; i < 4; i++){
            if (chars1[i] =='1' && chars2[i] != '1'){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断u1是否是u2的直接父系（授权要用）
     * @param u1  要授权的用户
     * @param u2  被授权的用户
     */
    public boolean isDirectPaternal(User u1, User u2){
        User u = u2;
        while (u.getParent() != null){
            u = u.getParent();
            if (u1.equals(u)){
                return true;
            }
        }
        return false;
    }

    public boolean permDntExist(CommonUser u, byte give){
        byte have = u.getPermission();
        //获取二者的高四位：
        byte have_h = (byte) ((have & 0xf0) >>> 4);
        byte give_h = (byte) ((give & 0xf0) >>> 4);
        //做按位与，若不为0，说明存在要授予的权限已有
        byte compare = (byte) (have_h & give_h);
        if (compare == 0x00)
            return false;
        else
            return true;
    }
    /**
     * 判断授权是否合法
     * @param u1  授权者
     * @param u2  被授权者
     * @param permission  要授予的权限
     * @return true则授权合法，false则授权不合法
     */
    public boolean isPermissionLegal(User u1, CommonUser u2, byte permission){
        //1.获取授权用户的permission低四位
        byte p1_low4 = (byte) (u1.getPermission() & 0x0f);
        //2.获取被将要授予的permission的高四位和低四位
        byte p2_high4 = (byte) ((permission & 0xf0) >>> 4);
        //byte p2_low4 = (byte) (permission & 0x0f);

        //判断1，有没有权限授予别人这个权限？
        if (isValid(p1_low4, p2_high4)){
            //判断2，给别人的这个权限本身合不合法？（已删除）
            //判断3，给的这个权限是否已有？没有才可以
            if(!permDntExist(u2, permission)){
                //是否是父系用户授权？
                if(isDirectPaternal(u1, u2)){
                    return true;
                }
                else{
                    //第二层的结点如果没有被管理员授权，就可以被其他用户授权
                    if(u2.getLevel() == 2 && !u2.isGrantee())
                        return true;
                    System.err.println(u1.getName() + " is not a direct paternal user of " + u2.getName() + ", and they are not brothers to the second tier!");
                    return false;
                }
            }
            else{
                System.err.println(u1.getName() + " ==> " + u2.getName() + ": " +
                        "Permission already exists, please reauthorize after revoking!");
                return false;
            }
        }
        else{
            System.err.println("There is no permission granted for this permission!");
            return false;
        }
    }

    /**
     * 处理grant子句（不带with，非授权可授权）
     * @param u1 授权用户
     * @param u2 被授权用户
     * @param permission 要授予的权限，被调用到时，低4位一定是0
     * @return 是否成功授权
     */
    public boolean processGrant(User u1, CommonUser u2, byte permission){
        //.判断是否可以授权
        if (isPermissionLegal(u1, u2, permission)) {
            //可以授权
            //(1)更改u2的permission属性
            u2.setHighPermission(permission);
            //(2)挪动u2的结点
            shiftUser(u2, u2.getParent(), u1);
            //(3)更新树的高度
            height = updateHeight(root);
            //(4)更改isGrantee标志位
            u2.setGrantee(true);
            return true;
        }
        //不可以授权
        else{
            System.err.println("Unauthorized authorization!");
            return false;
        }
    }

    /**
     * 处理grant子句（带with，授权可授权）
     * @param u1 授权用户
     * @param u2 被授权用户
     * @param permission 要授予的权限
     */
    public boolean processGrantWith(User u1, CommonUser u2, byte permission){
        if (isPermissionLegal(u1, u2, permission)) {
            //可以授权
            //(1)更改u2的permission属性
            u2.setLowPermission(permission);
            //不需要移动结点等操作了，因为是其父亲再次给其授权
            return true;
        }
        //不可以授权
        else{
            System.err.println("Unauthorized authorization!");
            return false;
        }
    }

    /**
     * 撤销授权操作（还需要思考）
     * @param user 要撤销权利的用户
     */
    public void processRevoke(CommonUser user){

    }

    public void processRevokeWith(CommonUser user){

    }

    /**
     * 登录，并输出相关提示信息
     * @param name 用户名
     * @param password 密码
     * @return 返回true或false，表示是否登陆成功
     */
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
