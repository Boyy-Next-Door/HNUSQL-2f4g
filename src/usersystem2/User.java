package usersystem2;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String password;
    //用户的权限
    HashMap<Table, Permission> permissions;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        permissions = new HashMap<Table, Permission>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HashMap<Table, Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashMap<Table, Permission> permissions) {
        this.permissions = permissions;
    }
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * 能给别人的不能多于本身已有的。对p1低四位p2高四位关系的判断，
     * 和对p2高四位和p2低四位关系的判断方法相同，抽出方法
     * @param b1  本身已有的
     * @param b2  给别人的
     * @return  true或false，表示能不能给
     */
    public boolean isValid(byte b1, byte b2){
        byte xor1 = (byte) (b1 ^ b2);
        String s1 = Integer.toBinaryString(xor1);
        //System.out.println("s1 = " + s1);
        char[] chars1 = s1.toCharArray();

        byte and1 = (byte) (xor1 & b1);
        String s2 = Integer.toBinaryString(and1);
        //System.out.println("s2 = " + s2);
        char[] chars2 = s2.toCharArray();

        chars1 = zeroize(chars1);
        chars2 = zeroize(chars2);

        // System.out.println(chars1);
        // System.out.println(chars2);

        for(int i = 0; i < 4; i++){
            if (chars1[i] =='1' && chars2[i] != '1'){
                return false;
            }
        }
        return true;
    }

    /**
     * 给传入的字符数组高位补0，返回长度为4的字符数组（为不满4位的二进制数高位补0）
     * @param ch 传进的数组
     * @return  补0后的数组
     */
    public char[] zeroize(char[] ch){
        if (ch.length == 4){
            return ch;
        }
        else{
            char[] chars = new char[4];
            int len = ch.length;
            for(int i = len - 1; i >= 0; i--){
                chars[i+1] = ch[i];
            }
            len = len + 1;
            chars[0] = '0';
            if (len == 4)
                return chars;
            while(len < 4){
                for(int i = len - 1; i >= 0; i--){
                    chars[i+1] = chars[i];
                }
                len = len + 1;
                chars[0] = '0';
            }
            return chars;
        }
    }

    public boolean isGrantable(String database, String table, byte permission) {
        boolean isGrantable=false;
        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            //本用户关于这个表的某一组权限
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)){
                //TODO 检查一下这个权限能否下发 如果不行 还可能有针对该表的其他permission可以下发
                byte permission1 = entry.getKey().getPermission();
                //获取授权用户的permission低四位
                byte p1_low4 = (byte) (permission1 & 0x0f);
                //获取被将要授予的tagetPerm的高四位
                byte p2_high4 = (byte) ((permission & 0xf0) >>> 4);

                //判断：有没有权限授予别人这个权限？
                if (isValid(p1_low4, p2_high4)){
                    isGrantable = true;
                    break;
                }
                else{
                    //检查下一个
                    continue;
                }
            }
        }
        return  isGrantable;
    }

    public boolean canBeAuthorized(String database, String table, byte permission){
        boolean canBeGiven=false;
        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            //本用户关于这个表的某一组权限
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)){
                //TODO 检查一下这个权限与要接受的权限是否有重复的部分
                byte permission1 = entry.getKey().getPermission();
                //获取授权用户的permission高四位
                byte p1_high4 = (byte) ((permission1 & 0xf0) >>> 4);
                //获取被将要授予的tagetPerm的高四位
                byte p2_high4 = (byte) ((permission & 0xf0) >>> 4);

                byte and = (byte) (p1_high4 & p2_high4);
                //判断：有没有权限授予别人这个权限？
                if (and == 0x00){
                    continue;
                }
                else{
                    canBeGiven = false;
                    break;
                }
            }
        }
        return  canBeGiven;
    }

    public void acquirePermission(String granterName, String database, String table, byte permission, int grantType) {
        Table table1 = new Table(new Database(database), table,permission);
        Permission newPerm = new Permission();
        newPerm.setGrantedBy(UserManager2.getUserByName(granterName));
        newPerm.setTarget(1);                   //默认目标为表
        newPerm.setTable(table1);               //记录目标表
        newPerm.setDatabase(table1.getDb());    //记录目标数据库
        newPerm.setPermission(permission);      //设置权限位
        newPerm.setGrantType(grantType);        //记录权限下发形式
        permissions.put(table1, newPerm);
    }
}
