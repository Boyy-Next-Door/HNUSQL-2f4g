package usersystem;

import java.util.List;

public class Admin extends User{

    private static final Admin admin = new Admin();

    Admin(){
        this.id = "0";
        this.name = "admin";
        this.password = "123456";
        this.parent = null;
        this.level = 1;
        this.permission = (byte)0x00;

    }

    public static Admin getAdmin(){
        return admin;
    }

    public String getId() {
        return id;
    }

    /**  禁止管理员用户更改id
     public void setId(String id) {
     this.id = id;
     }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public User getParent() {
        return null;
    }

    /**
     * 空方法，管理员用户位于根节点，没有父结点
     * @param user 没用
     */
    @Override
    public void setParent(User user) {

    }

    public int getLevel() {
        return level;
    }

    /**  管理员用户没有必要更改所在层级，它一直在第一层
     public void setLevel(int level) {
     this.level = level;
     }*/

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public byte getPermission() {
        return permission;
    }

    /**
     * 空方法，理员用户的权限没有必要修改，一直拥有全部的权限
     * @param permission 没用
     */
    @Override
    public void setPermission(byte permission) {

    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", parent=" + parent +
                ", level=" + level +
                ", children=" + children +
                ", permission=" + permission +
                '}';
    }

    /**
     * 增加2层用户
     * @param child  要增加的2层用户
     */
    public void addChild(User child) {
        //1.设置chile的parent属性
        child.setParent(this);
        //2.在当前结点的children列表中添加child
        children.add(child);
    }
}
