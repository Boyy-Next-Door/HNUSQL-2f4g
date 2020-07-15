package usersystem;

import java.util.List;

public class Admin extends User{

    private static final Admin admin = new Admin();

    Admin(){
        setId("0");
        setName("admin");
        setPassword("123456");
        setParent(null);
        setLevel(1);
        setPermission((byte)0x00);
    }

    public static Admin getAdmin(){
        return admin;
    }

    /**
     * 增加2层用户
     * @param child  要增加的2层用户
     */
    public void addChild(User child) {
        //1.设置chile的parent属性
        child.setParent(this);
        //2.在当前结点的children列表中添加child
        getChildren().add(child);
    }
}
