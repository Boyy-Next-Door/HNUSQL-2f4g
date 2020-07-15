package usersystem;

public class CommonUser extends User {

    public CommonUser(String id) {
        super(id);
        setPermission((byte) 0x00);
    }

    public CommonUser(String name, String password){
        super(name, password);
        setPermission((byte) 0x00);
    }

    public CommonUser(String id, String name, String password, byte permission){
        super(id, name, password, permission);
        setPermission((byte) 0x00);
    }
    public void addChild(User child) {
        //1.设置chile的parent属性
        child.setParent(this);
        //2.在当前结点的children列表中添加child
        getChildren().add(child);
    }
}
