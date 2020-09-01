package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.ObjectOutputStream;



public class tinyClient {
    private String host;
    private int port;
    private String username;
    private String password;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream obout;

    private String cookie;


    public void setHost(String host){this.host=host;}
    public void setPort(int port){this.port=port;}
    public void setUsername(String username){this.username=username;}
    public void setPassword(String password){this.password=password;}
    public String getHost(){return host;}
    public int getPort(){return port;}
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public PrintWriter getOut(){return  out;}
    public BufferedReader getIn(){return  in;}






    /*
     * 与服务器进行连接
     * @param ip 服务器的ip地址
     * @param port 服务器的端口号
     * @return：
     * true--客户端与服务器端建立连接成功
     * false--客户端与服务器端建立连接失败
     */
    public boolean startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            obout= new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Connection success");
            return true;
        }catch (Exception e){
            System.out.println("Connection failed");
            return false;
        }
    }

    /*
     * 向服务器发送msg，
     * @param msg 需要发送的字符串
     * @return：返回一个list代表数据库执行msg的结果，list每一行都是一行string
     * 因为数据库返回的结果可能包含多行，例如select语句
     */
    public List sendMessage(String msg) throws Exception {
        out.println(msg);
        List<String> respList=new ArrayList<>();
        while(true) {
            String resp = in.readLine();
            // System.out.println(resp);
            if("DONE".equals(resp))break;
            respList.add(resp);
        }
        return respList;
        //return resp;
    }

    public void send(String msg) throws Exception {
        out.println(msg);
        //return resp;
    }

    public void sendWithCookie(String cookie,String msg)throws Exception{
        obout.writeObject(new Info(cookie,msg));
    }

    /*
     * 登陆功能
     * @param ip 服务器的ip地址
     * @param port 服务器的端口号
     * @param username 客户的用户名
     * @param password 客户的登陆密码
     * @return:true登陆成功，false登陆失败
     */
    public boolean login(String ip,int port,String username,String password)throws Exception{
        if(startConnection(ip,port)==false)return false;
        StringBuilder sb=new StringBuilder();
        String msg=sb.append(username).append(":").append(password).toString();
        //String resp=sendMessage(msg);
        out.println(msg);
        try {
            String resp=in.readLine();
            if("success".equals(resp)) return true;
            else if("fail".equals(resp))return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * 测试服务器连通性
     * @param host
     * @param port
     * @return true客户端成功和服务器连接，false客户端和服务器未连接
     */
    public boolean testConnect(String host,int port){
        return clientSocket.isConnected();
    }


    /*
     * 由于user和database之间的联系还没有完全实现，
     * 所以接下来的的函数中暂且省略username和databasename，待以后补全
     */


    /*
     * 返回数据库清单
     * @param 用户名
     * @return 用list的格式返回数据库清单
     */
    /*
    public List getDatabases(String username){

    }
     */


    /**
     * 获取指定数据库的表清单
     * @param username 暂且省略
     * @param databaseName 暂且省略
     * @return  用list的格式返回指定数据库的表清单，list中的每一项都代表一个表
     */
    public List getTables(String username, String databaseName)throws Exception{
        List<String> respList=new ArrayList<>();
        String msg="show tables";
        //out.println(msg);
        respList=sendMessage(msg);
        return respList;
    }

    /**
     * 获取指定表的内容
     * @param username 暂且省略
     * @param databaseName 暂且省略
     * @param tableName 暂且省略
     * @return
     */
    public List getTableContent(String username, String databaseName, String tableName)throws Exception{
        List<String> respList=new ArrayList<>();
        List<String[]> listStr=new ArrayList<>();
        String msg="select * from "+tableName;
        respList=sendMessage(msg);
        if(respList.size()==0){
            return respList;
        }
        else {
            Iterator<String> stringIterator = respList.iterator();
            String[] colName;
            int col=0;
            while (stringIterator.hasNext()) {
                if(col==0){
                    colName=stringIterator.next().split("\\s+");
                    listStr.add(colName);
                    col=col+1;
                    continue;
                }
                else if(col==1){
                    String[] arr=stringIterator.next().split("\\s+");
                    col=col+1;
                    continue;
                }
                else{
                    String[] arr=stringIterator.next().split("\\s+");
                    listStr.add(arr);
                }
            }
        }
        return listStr;
    }

    /**
     * 获取指定表的字段
     * @param username 暂且省略
     * @param databaseName 暂且省略
     * @param tableName 暂且省略
     * @return
     */
    public List getTableField(String username, String databaseName, String tableName)throws Exception{
        List<String> respList=new ArrayList<>();
        List<String[]> listStr=new ArrayList<>();
        String msg="describe "+tableName;
        respList=sendMessage(msg);
        Iterator<String> stringIterator = respList.iterator();
        String[] arr;
        while (stringIterator.hasNext()) {
            arr=stringIterator.next().split("\\s+");
            listStr.add(arr);
        }
        return listStr;
    }

    public void stopConnection() throws Exception {
        in.close();
        out.close();
        clientSocket.close();
    }


}
