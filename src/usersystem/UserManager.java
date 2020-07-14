package usersystem;

import java.io.*;

//�û�������
public class UserManager {
    //�û��洢�ļ�·��
    private static final String fileDir = "src/usersystem/user_file.dbuf";
    //�û���
    private static UserTree tree = null;
    //��������userTree
    static {
        readTreeFromFile();
    }

    //��dbuf�ļ��ж�ȡuserTree
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

    //���ڴ��е�treeд���ļ�
    private static void writeTreeToFile(){
        ObjectOutputStream objectOutputStream = null;
        try {
            //����֮ǰ��dbuf�ļ�
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
