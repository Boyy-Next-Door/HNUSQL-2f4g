package usersystem;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;  //用户id
    private String name;  //用户名
    private String password;  //用户密码
    private User parent;  //该用户的父结点
    private int level;  //结点的层数
    public List<User> children = new ArrayList<>();  //该用户的子节点
    protected byte permission = 0x00;  //该用户的权限

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public User(String id, String name, String password, int level) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public byte getPermission() {
        return permission;
    }

    public void setPermission(byte permission) {
        this.permission = permission;
    }

    public void addChild(User child){
        //1.设置chile的parent属性
        child.setParent(this);
        //2.在当前结点的children列表中添加child
        children.add(child);

    }

}
