package client;

import java.util.Scanner;

public class test2 {
    public static void  main(String [] args){
        tinyClient client = tinyClient.getClient();
        try {

            //client.startConnection("127.0.0.1",6666);
            String uname, pword;
            Scanner sc = new Scanner(System.in);
            uname = "123";
            pword = "123";

            if (client.login("127.0.0.1", 6666, uname, pword) == false) {
                System.out.println("login failed");
            } else{
                System.out.println("login succeed");

              // client.getDatabases("haha");
                while (true) {
                    System.out.print("tinySql>");
                    String msg = null;
                    msg = sc.nextLine();
                 //   client.Revoke("ddd","aa");
                 //   client.Insert("aa","insert into Student ( Sno ) values ( 2 )");
                 //   client.Select("aa","select * from Course;");
                    client.getTables("bbb");
                //    client.getDatabases("haha");
                //    client.send(msg);
                }

            }
            client.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
