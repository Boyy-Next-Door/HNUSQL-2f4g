package usersystem;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
    protected String id;  //用户id
    protected  String name;  //用户名
    protected String password;  //用户密码
    protected User parent;  //该用户的父结点
    protected int level;  //结点的层数
    protected List<User> children = new ArrayList<>();  //该用户的子节点
    protected byte permission;  //该用户的权限
    abstract public void addChild(User user);

    public User() {
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
}
