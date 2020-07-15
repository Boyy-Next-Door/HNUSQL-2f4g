package usersystem;

import java.util.List;

public class CommonUser extends User{

    public CommonUser(String id) {
        super(id);
    }

    public void addChild(User child) {
        //1.设置chile的parent属性
        child.setParent(this);
        //2.在当前结点的children列表中添加child
        getChildren().add(child);
    }
}
