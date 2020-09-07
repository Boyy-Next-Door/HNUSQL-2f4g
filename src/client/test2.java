package client;

import com.sqlmagic.tinysql.entities.BaseResponse;

import java.util.Scanner;

public class test2 {
    public static void  main(String [] args){
        tinyClient client = tinyClient.getClient();
        try {

            client.startConnection("127.0.0.1",6666);
            String uname, pword;
            Scanner sc = new Scanner(System.in);
            uname = sc.nextLine();
            pword = sc.nextLine();

            if (client.login("127.0.0.1", 6666, uname, pword) == false) {
                System.out.println("login failed");
            } else{
                System.out.println("login succeed");

              // client.getDatabases("haha");
                while (true) {
                    System.out.print("tinySql>");
                    String msg = null;
                    msg = sc.nextLine();
//                    client.Select("aa","select * from Course;");
                 //   client.getTables("aaa","bbb");
                    BaseResponse haha = client.getDatabases();
                    //    client.send(msg);
                }

            }
            client.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
