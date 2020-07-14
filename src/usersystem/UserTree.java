package usersystem;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class UserTree implements Serializable {

    private User root;
    private int height;
    private int numOfUser;
    private ArrayList<User> ul = new ArrayList<>();

    public UserTree(User root) {
        this.root = root;
        ul.add(root);
        numOfUser = 1;
        height = 1;
    }
    public UserTree(){

    }

    public User getRoot() {
        return root;
    }

    public void setRoot(User root) {
        this.root = root;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNumOfUser() {
        return numOfUser;
    }

    public void setNumOfUser(int numOfUser) {
        this.numOfUser = numOfUser;
    }

    public void setUl(ArrayList<User> ul) {
        this.ul = ul;
    }

    public ArrayList<User> getUl() {
        return ul;
    }

    /**
     * 借助队列层次遍历树，并打印id
     * @param tmp 遍历开始的结点
     */
    public void levelOrderTraverse(User tmp){
        if (tmp == null) return;
        LinkedList<User> queue = new LinkedList<>();
        queue.add(tmp);
        User curr;
        while (!queue.isEmpty()){
            curr = queue.element();
            queue.remove();
            System.out.print(curr.getId() + " ");

            if (!curr.children.isEmpty()){
                queue.addAll(curr.children);
            }
        }
        System.out.println();
    }

    /**
     * 维护树的height属性
     */
    public int updateHeight(User user){
        if (user.children.isEmpty()) {
            return user.getLevel();
        }
        else{
            int max = user.children.get(0).getLevel();
            for(User child: user.children){
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
    public void addUserToTree(User user){
        root.addChild(user);
        ul.add(user);
        user.setLevel(root.getLevel() + 1);
        numOfUser = numOfUser + 1;
        height = 2;
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
        Iterator iterator = p1.children.iterator();
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
     * 处理grant子句
     * @param u1 授权用户
     * @param u2 被授权用户
     * @param permission 要授予的权限
     */
    public void processGrant(User u1, User u2, byte permission){
        //1.如果要授给别人的权限，有不在【u1已有的权限】这之中的权限，则拒绝授权
        if (permission > u1.getPermission()) {
            System.err.println("Unauthorized authorization!");
        }

        else{
            //2.更改u2的permission属性
            u2.setPermission(permission);
            //3.挪动u2的结点
            shiftUser(u2, u2.getParent(), u1);
            //4.更新树的高度
            height = updateHeight(root);
        }
    }

    public static void main(String[] args) throws IOException {
        //创建admin用户
        User admin = new User("0", 1);
        User u1 = new User("1");
        User u2 = new User("2");
        User u3 = new User("3");
        User u4 = new User("4");
        User u5 = new User("5");
        User u6 = new User("6");
        User u7 = new User("7");
        User u8 = new User("8");
        User u9 = new User("9");
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

        tree.levelOrderTraverse(admin);
        System.out.println("---------------挪动结点-----------------");
        tree.shiftUser(u1,u1.getParent(), u3);
        tree.shiftUser(u4,u4.getParent(), u3);
        tree.shiftUser(u2,u2.getParent(), u5);
        tree.shiftUser(u7,u7.getParent(), u5);
        tree.shiftUser(u8,u8.getParent(), u1);
        tree.shiftUser(u9,u9.getParent(), u8);
        tree.levelOrderTraverse(admin);
        System.out.println("共有" + tree.getNumOfUser() + "个用户");
        //System.out.println("树的高度为：" + tree.updateHeight(admin));
        System.out.println("树的高度为：" + tree.getHeight());
        System.out.println("遍历树中的用户list：");
        for(User u: tree.getUl()){
            System.out.print("user id:" + u.getId());
            if(u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.println();
        }
        System.out.println("-----------授权test------------");
        tree.processGrant(admin,u4, (byte) 0x00);
        tree.levelOrderTraverse(admin);
        System.out.println("树的高度为：" + tree.getHeight());
        System.out.println("遍历树中的用户list：");
        for(User u: tree.getUl()){
            System.out.print("user id:" + u.getId());
            if(u.getParent() != null)
                System.out.print(" parent id:" + u.getParent().getId());
            System.out.print(" level:" + u.getLevel());
            System.out.println();
        }
        //tree.levelOrderTraverse(tree.getRoot());

        String string = JSON.toJSONString(tree);
        FileWriter fileWriter = new FileWriter(new File("src/usersystem/user_file.dbuf"));
        fileWriter.write(string);
        fileWriter.flush();
        System.out.println(string);
        System.out.println(tree.getNumOfUser());
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
}
