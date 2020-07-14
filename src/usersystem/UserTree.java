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
     * �������в�α�����������ӡid
     * @param tmp ������ʼ�Ľ��
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
     * ά������height����
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
     * ���û�������ӽ��
     * @param user Ҫ��ӵ��û�
     */
    public void addUserToTree(User user){
        root.addChild(user);
        ul.add(user);
        user.setLevel(root.getLevel() + 1);
        numOfUser = numOfUser + 1;
        height = 2;
    }
    /**
     * ����grant�־�ʱ����Ҫ���û��ƶ���������Ȩ�Ľ������
     * @param u1 ��Ҫ�ƶ��Ľ��
     * @param p1 ֮ǰ�ĸ����
     * @param p2 �ƶ�֮��ĸ����
     */
    public void shiftUser(User u1, User p1, User p2){

        //1.����u1��parent����
        u1.setParent(p2);
        //2.��p1��children�б���ɾȥu1
        Iterator iterator = p1.children.iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals(u1)){
                iterator.remove();
                break;
            }
        }
        //3.��p2��children�б��м���u1
        p2.addChild(u1);
        //4.����u1��level����
        u1.setLevel(p2.getLevel() + 1);
        //5.�������ĸ߶�
        height = updateHeight(root);
    }

    /**
     * ����grant�Ӿ�
     * @param u1 ��Ȩ�û�
     * @param u2 ����Ȩ�û�
     * @param permission Ҫ�����Ȩ��
     */
    public void processGrant(User u1, User u2, byte permission){
        //1.���Ҫ�ڸ����˵�Ȩ�ޣ��в��ڡ�u1���е�Ȩ�ޡ���֮�е�Ȩ�ޣ���ܾ���Ȩ
        if (permission > u1.getPermission()) {
            System.err.println("Unauthorized authorization!");
        }

        else{
            //2.����u2��permission����
            u2.setPermission(permission);
            //3.Ų��u2�Ľ��
            shiftUser(u2, u2.getParent(), u1);
            //4.�������ĸ߶�
            height = updateHeight(root);
        }
    }

    public static void main(String[] args) throws IOException {
        //����admin�û�
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
        System.out.println("---------------Ų�����-----------------");
        tree.shiftUser(u1,u1.getParent(), u3);
        tree.shiftUser(u4,u4.getParent(), u3);
        tree.shiftUser(u2,u2.getParent(), u5);
        tree.shiftUser(u7,u7.getParent(), u5);
        tree.shiftUser(u8,u8.getParent(), u1);
        tree.shiftUser(u9,u9.getParent(), u8);
        tree.levelOrderTraverse(admin);
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
        tree.levelOrderTraverse(admin);
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
