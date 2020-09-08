package client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sqlmagic.tinysql.entities.BaseResponse;
import com.sqlmagic.tinysql.protocol.Request;
import com.sqlmagic.tinysql.utils.MyTableUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.lang.String.*;


public class tinyClient {
    private String host;                //主机ip地址
    private int port;                   //主机端口号
    private String username;            //登陆名
    private String password;            //登陆密码
    private Socket clientSocket;        //与服务端通信的socket
    private String cookie;              //服务器返回给客户端的cookie
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream obout;

    private static tinyClient client = null;

    public static tinyClient getClient() {
        if (client == null) {
            client = new tinyClient();
        }
        return client;

    }

    private tinyClient() {
    }


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
         //   System.out.println("Connection success");
            return true;
        }catch (Exception e){
        //    System.out.println("Connection failed");
            return false;
        }
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
        //Map<String,String> map=new HashMap<String,String>();
        //map.put("username",username);
        //map.put("password",password);
        //String str= JSON.toJSONString(map);
        Request request=new Request(null, Request.LOGIN,"login",username,password);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        //System.out.println(str);
        /*
        需要根据客户端发送的信息判断是否登陆
        暂时默认可以直接登陆
         */
        String resp=in.readLine();
        //System.out.println(resp);
        JSONObject jsonObject= JSONObject.parseObject(resp);
        int status=jsonObject.getInteger("status");
        if(status==0) {
            cookie = jsonObject.getString("cookie");
            //System.out.println("cookie=" + cookie);
            return true;
        }
        else if(status==1){
            return false;
        }
        return false;
    }

    /**
     *
     * @param databaseName
     * @return
     * @throws Exception
     */
    public BaseResponse useDatabase(String databaseName)throws Exception{
        String rawSQL="use "+databaseName;
        Request request=new Request(cookie, Request.USE_DATABASE,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        JSONObject jsonObject= JSONObject.parseObject(responseStr);
        BaseResponse baseResponse=jsonObject.toJavaObject(BaseResponse.class);
        return  baseResponse;
    }

    /**
     *
     *
     * @return
     * @throws Exception
     */
    public BaseResponse getDatabases()throws Exception{
        String rawSQL="show databases";
        String responseStr;
        Request request=new Request(cookie, Request.SHOW_DATABASES,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        responseStr=in.readLine();
        //System.out.println(responseStr);
        JSONObject jsonObject= JSONObject.parseObject(responseStr);
        BaseResponse baseResponse=jsonObject.toJavaObject(BaseResponse.class);
        return  baseResponse;
    }


    /**
     *
     *
     * @param databaseName
     * @return
     * @throws Exception
     */
    public BaseResponse getTables(String databaseName)throws Exception{
        String rawSQL="show tables;";
        String responseStr;
        Request request=new Request(cookie, Request.SHOW_TABLES,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        responseStr=in.readLine();
       // System.out.println(responseStr);
        //JSONObject jsonObject= JSONObject.parseObject(responseStr);
       // BaseResponse baseResponse=jsonObject.toJavaObject(BaseResponse.class);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
    }


    /**
     *
     *
     * @param rawSQL
     * @return
     * @throws Exception
     */
    public BaseResponse Select(String rawSQL)throws Exception{
        //BaseResponse baseResponse=new BaseResponse();
        Request request=new Request(cookie, Request.SELECT,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
       // System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        String tempStr=(String)baseResponse.getData();
       //System.out.println(tempStr);
       // System.out.println(tempStr.length());

        /*
        解析字符串
         */

        MyTableUtil newTable=new MyTableUtil();
        int flag1=0;
        int flag2=0;
        int flag3=0;
        int flag4=0;
        int len=0;
        for(int i=0;i<tempStr.length();i++){
            if(tempStr.charAt(i)!='\n'&&flag1==0){
                continue;
            }
            else if(tempStr.charAt(i)=='\n'&&flag1==0){
                flag1=1;
                i=i+2;
                continue;
            }
            else if(flag1==1&&flag2==0){
                String temp="";
                while(true){
                    if(tempStr.charAt(i)==' '){
                       i=i+1;
                       continue;
                    }
                    else if(tempStr.charAt(i)=='|'){
                        i=i+1;
                        newTable.addColumn(temp);
                        len=len+1;
                        //System.out.println("temp="+temp);
                        temp="";
                        continue;
                    }else if(tempStr.charAt(i)=='\n'){
                        flag2=1;
                        break;
                    }
                    else{
                        temp=temp+tempStr.charAt(i);
                        i=i+1;
                    }
                }
            }
            else if(flag2==1&&flag3==0){
                if(tempStr.charAt(i)=='|'){
                    flag3=1;
                    i=i+1;
                    continue;
                }
            }
            else if(flag4==0){
                String temp="";
                String[] strs=new String[len];
                int whichLen=0;
                while(true){
                    if(tempStr.charAt(i)==' '){
                        i=i+1;
                        continue;
                    }
                    else if(tempStr.charAt(i)=='|'){
                        i=i+1;
                        strs[whichLen++]=temp;
                     //   System.out.println("temp="+temp);
                        temp="";
                        continue;
                    }
                    else if(tempStr.charAt(i)=='\n'){
                        i=i+3;
                    //    for(int j=0;j<len;j++){
                     //       System.out.println(strs[j]);
                      //  }
                        newTable.addRow(strs);
                        whichLen=0;
                    }
                    else if(tempStr.charAt(i)=='-'){
                        flag4=1;
                        break;
                    }
                    else{
                        temp=temp+tempStr.charAt(i);
                        i=i+1;
                    }
                }
            }
            else if (flag4==1)break;
        }

        BaseResponse returnResponse=BaseResponse.ok(newTable);
        System.out.println(newTable.generate());
      //  BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return returnResponse;
    }

    /**
     *
     *
     * @param rawSQL
     * @return
     * @throws Exception
     */
    public BaseResponse Insert(String rawSQL)throws Exception{
        Request request=new Request(cookie, Request.INSERT,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
    }

    /*
    public static final int CREATE = 201;
    public static final int ALTER = 202;
    public static final int DROP = 203;
    public static final int GRANT = 204;
    public static final int REVOKE = 205;
     */

    public BaseResponse Create(String rawSQL)throws Exception{
        Request request=new Request(cookie, Request.CREATE,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        //System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
    }


    /**
     *
     * @param username
     * @param rawSQL
     * @return
     * @throws Exception
     */
    public BaseResponse Alter(String rawSQL)throws Exception{
        Request request=new Request(cookie, Request.ALTER,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        //System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
    }

    public BaseResponse Drop(String rawSQL)throws Exception{
        Request request=new Request(cookie, Request.DROP,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        //System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
    }

    public BaseResponse Grant(String rawSQL)throws Exception{
        Request request=new Request(cookie, Request.GRANT,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        //System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
    }

    public BaseResponse Revoke(String rawSQL)throws Exception{
        Request request=new Request(cookie, Request.REVOKE,rawSQL);
        String str= JSONObject.toJSONString(request);
        out.println(str);
        String responseStr=in.readLine();
        //System.out.println(responseStr);
        BaseResponse baseResponse=JSONObject.parseObject(responseStr,BaseResponse.class);
        return baseResponse;
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
    /*
    public List getTables(String username, String databaseName)throws Exception{
        List<String> respList=new ArrayList<>();
        String msg="show tables";
        //out.println(msg);
        respList=sendMessage(msg);
        return respList;
    }

     */

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
