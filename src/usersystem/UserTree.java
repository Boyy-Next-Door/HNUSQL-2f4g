package usersystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class UserTree {

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
     * 处理grant子句
     * @param u1 授权用户
     * @param u2 被授权用户
     * @param permission 要授予的权限
     */
    public void processGrant(User u1, CommonUser u2, byte permission){
        //1.如果要授给别人的权限，有不在【u1已有的权限】这之中的权限，则拒绝授权
        //这部分if之内的判断还在实现，此版本不作数
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
