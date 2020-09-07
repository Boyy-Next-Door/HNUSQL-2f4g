package client;

import java.util.Scanner;

public class ClientTest {
    public static void  main(String [] args){
        tinyClient client =tinyClient.getClient();
        try {

            //client.startConnection("127.0.0.1",6666);
            String uname, pword;
            Scanner sc = new Scanner(System.in);
            uname = sc.nextLine();
            pword = sc.nextLine();

            if (client.login("127.0.0.1", 6666, uname, pword) == false) {
                System.out.println("login failed");
            } else{
                System.out.println("login succeed");


                while (true) {
                    System.out.print("tinySql>");
                    String msg = null;
                    msg = sc.nextLine();
                    client.sendWithCookie("login",msg);
                    //client.send(msg);
                }
                /*
                while(true){
                    System.out.print("tinySql>");
                    String msg = null;
                    msg = sc.nextLine();
                    client.getOut().println(msg);
                    //ResultSet rs=(ResultSet)(client.getIn().readLine());
                    String resp=client.getIn().readLine();
                    System.out.println(resp);
                }
                 */


/*
                while (true) {
                    System.out.print("tinySql>");
                    String msg = null;
                    msg = sc.nextLine();
                    List<String> resp=client.sendMessage(msg);
                    Iterator<String> stringIterator = resp.iterator();
                    System.out.println("=============================================");
                    while (stringIterator.hasNext()) {
                        System.out.println(stringIterator.next());
                    }
                    System.out.println("=============================================");
                }
*/


                /*
                System.out.println("=============================================");
                List<String[]> resp=client.getTableContent("aa","aa","Course");
                Iterator<String[]> stringIterator = resp.iterator();
                while (stringIterator.hasNext()) {
                    for(String ss : stringIterator.next()){
                        System.out.println(ss);
                    }
                }
                System.out.println("=============================================");
                 */


                /*
                System.out.println("=============================================");
                List<String[]> resp=client.getTableField("aa","aa","Course");
                Iterator<String[]> stringIterator = resp.iterator();
                while (stringIterator.hasNext()) {
                    for(String ss : stringIterator.next()){
                        System.out.println(ss);
                    }
                }
                System.out.println("=============================================");
                 */

            }
            client.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
