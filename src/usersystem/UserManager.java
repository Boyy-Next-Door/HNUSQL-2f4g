package usersystem;

import java.io.*;

//用户中心类
public class UserManager {
    //用户存储文件路径
    private static final String fileDir = "src/usersystem/user_file.dbuf";
    //用户树
    private static UserTree tree = null;
    //开机加载userTree
    static {
        readTreeFromFile();
    }

    //从dbuf文件中读取userTree
    private static void readTreeFromFile() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileDir));
            try {
                Object object = objectInputStream.readObject();
                tree = (UserTree) object;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将内存中的tree写入文件
    private static void writeTreeToFile(){
        ObjectOutputStream objectOutputStream = null;
        try {
            //覆盖之前的dbuf文件
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/usersystem/user_file.dbuf",false));
            objectOutputStream.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                objectOutputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(tree);
        tree.addUserToTree(new CommonUser("100"));
        writeTreeToFile();
        System.out.println(tree);
    }
}
