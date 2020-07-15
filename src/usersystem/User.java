package usersystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;  //用户id
    private  String name;  //用户名
    private String password;  //用户密码
    private User parent;  //该用户的父结点
    private int level;  //结点的层数
    private List<User> children = new ArrayList<>();  //该用户的子节点
    private byte permission;  //该用户的权限
    private boolean isGrantee;  //已被授权
    abstract public void addChild(User user);

    public User() {
        this.isGrantee = false;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String id) {
        this.id = id;
        this.isGrantee = false;
    }

    public User(String id, String name, String password, byte permission) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.permission = permission;
        this.isGrantee = false;
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

    public boolean isGrantee() {
        return isGrantee;
    }

    public void setGrantee(boolean grantee) {
        isGrantee = grantee;
    }

    public void setHighPermission(byte permission){
        //本函数目标：仅改动permission的高4位（把新的权限加进来，而不是覆盖，所以要做按位或运算）
        byte high = (byte) ((permission & 0xf0) | (this.permission & 0xf0));
        byte low = (byte) (this.permission & 0x0f);
        this.permission = (byte) (high + low);
    }

    public byte getHighPermission() {
        return (byte) (permission & 0b11110000);
    }

    public void setLowPermission(byte permission){
        //本函数目标：仅改动permission的低4位（把新的权限加进来，而不是覆盖，所以要做按位或运算）
        byte high = (byte) (this.permission & 0xf0);
        byte low = (byte) ((permission & 0x0f) | (this.permission & 0x0f));
        this.permission = (byte) (high + low);
    }

    public byte getLowPermission() {
        return (byte) (permission & 0b00001111);
    }
}
