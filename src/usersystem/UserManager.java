package usersystem;

import java.io.*;

//�û�������
public class UserManager {
    //�û��洢�ļ�·��
    private static final String fileDir = "src/usersystem/user_file.dbuf";

    //�û���
    private static UserTree tree = null;

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

    private static void writeTreeToFile(){
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("src/usersystem/user_file.dbuf"));
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
        readTreeFromFile();
        System.out.println(tree);
        tree.addUserToTree(new CommonUser("100"));
        writeTreeToFile();

        readTreeFromFile();
        System.out.println(tree);
    }
}
