package usersystem;

import java.util.List;

public class CommonUser extends User{

    public CommonUser(String id) {
        this.id = id;
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
    public void addChild(User child) {
        //1.设置chile的parent属性
        child.setParent(this);
        //2.在当前结点的children列表中添加child
        children.add(child);
    }
}
