package usersystem2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 2L;

    private String username;
    private String password;
    //用户的权限
    HashMap<Table, Permission> permissions;
    HashMap<Permission, List<User>> grantTo;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        permissions = new HashMap<Table, Permission>();
        grantTo = new HashMap<Permission, List<User>>();
    }

    @Override
    public int hashCode() {
        return 17*username.hashCode()+password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User))
            return false;

        User userObj = (User) obj;
        if (this == userObj)
            return true;
        if ((userObj.username.equals(this.username)) && (userObj.password.equals(this.password))){
            return true;
        }else{
            return false;
        }
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
                ", permissions=" + permissions +
                ", grantTo=" + grantTo +
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

        chars1 = zeroize(chars1,4);
        chars2 = zeroize(chars2,4);

        for(int i = 0; i < 4; i++){
            if (chars1[i] =='1' && chars2[i] != '1'){
                return false;
            }
        }
        return true;
    }

    /**
     * 给传入的字符数组高位补0，返回长度为lenth的字符数组（为不满lenth位的二进制数高位补0）
     * @param ch 传进的数组
     * @return  补0后的数组
     */
    public char[] zeroize(char[] ch, int lenth){
        if (ch.length == lenth){
            return ch;
        }
        else{
            char[] chars = new char[lenth];
            int len = ch.length;
            for(int i = len - 1; i >= 0; i--){
                chars[i+1] = ch[i];
            }
            len = len + 1;
            chars[0] = '0';
            if (len == lenth)
                return chars;
            while(len < lenth){
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
                byte permission1 = key.getPermission();
                //获取授权用户的permission低四位
                byte p1_low4 = (byte) (permission1 & 0x0f);
                //获取将要被授予的tagetPerm的高四位
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
        boolean canBeGiven = true;
        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            //本用户关于这个表的某一组权限
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)){
                //TODO 检查一下这个权限与要接受的权限是否有重复的部分
                byte permission1 = key.getPermission();
                //获取授权用户的permission高四位
                byte p1_high4 = (byte) ((permission1 & 0xf0) >>> 4);
                //获取将要被授予的tagetPerm的高四位
                byte p2_high4 = (byte) ((permission & 0xf0) >>> 4);

                byte and = (byte) (p1_high4 & p2_high4);
                //判断：要接受的权限是否与自己已有的重复？
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

    public boolean isRevokable(String database, String table, byte permission){
        boolean isRevokable = false;
        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            //要去撤销别人的权限的用户关于这个表的某一组权限
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)){
                //TODO 检查一下撤销者的这个权限是否是从别人那里收回的 如果不行 还可能有针对该表的其他permission可以用来撤销
                byte permission1 = key.getPermission();
                //获取撤销用户的permission低四位
                byte p1_low4 = (byte) (permission1 & 0x0f);
                //获取将要被撤销的tagetPerm的高四位
                byte p2_high4 = (byte) ((permission & 0xf0) >>> 4);

                //判断：有没有权限撤销别人这个权限？
                if (isValid(p1_low4, p2_high4)){
                    isRevokable= true;
                    break;
                }
                else{
                    //检查下一个
                    continue;
                }
            }
        }
        return  isRevokable;
    }

    public boolean canBeRevoked(String database, String table, byte permission){
        boolean canBeRevoke = false;
        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            //被撤销者关于这个表的某一组权限
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)){
                //TODO 检查一下要被撤销的是不是自己已有的权限
                byte permission1 = key.getPermission();
                //获取授权用户的permission高四位
                byte p1_high4 = (byte) ((permission1 & 0xf0) >>> 4);
                //获取将要被授予的tagetPerm的高四位
                byte p2_high4 = (byte) ((permission & 0xf0) >>> 4);

                byte and = (byte) (p1_high4 & p2_high4);
                //判断：自己是不是有要被撤销的权限
                if (and == 0x00){
                    continue;
                }
                else{
                    canBeRevoke = true;
                    break;
                }
            }
        }
        return  canBeRevoke;
    }

    public ArrayList<Byte> split(byte old){
        char[] chars = byteToBit(old);
        ArrayList<Byte> bytes = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            if(chars[i] == '1'){
                byte byte1 = 0;
                if(i == 0){
                    byte1 -= 128;
                    Byte aByte = new Byte((byte) byte1);
                    bytes.add(aByte);
                }
                else{
                    byte1 += Math.pow(2,7-i);
                    Byte aByte = new Byte((byte)byte1);
                    bytes.add(aByte);
                }
            }
        }
        return bytes;
    }


    public void acquirePermission(String granterName, String database, String table, byte permission, int grantType) {

        User granter = UserManager2.getUserByName(granterName);
        Permission newPerm = new Permission();

        boolean add = true;   //是合并还是添加？
        for(Map.Entry<Table, Permission> entry: permissions.entrySet()){
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)) {
                //可合并情况（授权人相同，且授权类型相同）

                Permission perm1 = permissions.get(key);

                if((perm1.getGrantedBy().equals(granter)) && (perm1.getGrantType() == grantType)){
                    byte former = perm1.getPermission();
                    byte latter = (byte)(former | permission);
                    Permission temp = new Permission();
                    temp.setDatabase(perm1.getDatabase());
                    temp.setTable(perm1.getTable());
                    temp.setTarget(perm1.getTarget());
                    temp.setPermission(latter);
                    temp.setGrantedBy(perm1.getGrantedBy());
                    temp.setGrantType(perm1.getGrantType());

                    // perm1.setPermission(latter);
                    // newPerm = temp;

                    //保证HashMap<Table,Permission>中Table对象中的permission与Permission中的permission一致
                    Table newKey = new Table(key.getDb(), key.getTableName(), latter);
                    permissions.put(newKey, temp);
                    permissions.remove(key);

                    newPerm.setDatabase(perm1.getDatabase());
                    newPerm.setTable(perm1.getTable());
                    newPerm.setTarget(perm1.getTarget());
                    newPerm.setPermission(permission);
                    newPerm.setGrantedBy(perm1.getGrantedBy());
                    newPerm.setGrantType(perm1.getGrantType());
                    add = false;
                }
            }
        }

        if(add){
            Table table1 = new Table(new Database(database), table, permission);

            newPerm.setGrantedBy(granter);
            newPerm.setTarget(1);                   //默认目标为表
            newPerm.setTable(table1);               //记录目标表
            newPerm.setDatabase(table1.getDb());    //记录目标数据库
            newPerm.setPermission(permission);      //设置权限位
            newPerm.setGrantType(grantType);        //记录权限下发形式
            permissions.put(table1, newPerm);
        }

        //将传入的permission拆分，构建只有一位为1、其他与newPerm相同的Permission对象
        ArrayList<Byte> split = split(newPerm.getPermission());

        for(int i = 0; i < split.size(); i++){
            // System.out.println(it.next());
            Permission Perm2 = new Permission();
            Perm2.setTarget(newPerm.getTarget());
            Perm2.setDatabase(newPerm.getDatabase());
            Perm2.setTable(newPerm.getTable());
            Perm2.setGrantType(newPerm.getGrantType());
            Perm2.setGrantedBy(newPerm.getGrantedBy());
            Perm2.setPermission(split.get(i).byteValue());

            // System.out.println("Perm2:" + Perm2.hashCode());
            // for(Map.Entry<Permission, List<User>> entry: granter.grantTo.entrySet()) {
            //     Permission Perm1 = entry.getKey();
            //     System.out.println("Perm1:"+Perm1.getPermission() + Perm1.hashCode());
            // }
            //为什么不进入第一个if？？
            if(granter.grantTo.containsKey(Perm2)){
                if(!granter.grantTo.get(Perm2).contains(this))
                    granter.grantTo.get(Perm2).add(this);
            }
            else{
                List users = new ArrayList<User>();
                users.add(this);
                granter.grantTo.put(Perm2,users);
            }
        }
    }

    //这里参数中的permission要保证高4位为1的是要撤销的权限，低4位为0（没有作用）
    public void revokePermission(String revokerName, String database, String table, byte permission, int revokeType){

        User revoker = UserManager2.getUserByName(revokerName);

        for(Map.Entry<Table, Permission> entry:permissions.entrySet()){
            Table key = entry.getKey();
            if(key.getDb().getDatabaseName().equals(database) && key.getTableName().equals(table)) {
                Permission perm1 = permissions.get(key);

                //撤销者的校验
                if((perm1.getGrantedBy().equals(revoker)) || revokerName.equals("admin")){
                    byte and = (byte)(perm1.getPermission() & permission);

                    //找到了应修改的Permission
                    if(and != 0x00){
                        byte permission2 = modify(perm1.getPermission(), and);
                        perm1.setPermission(permission2);

                        //这里Permission改了，Table要改吗？
                        Table newKey = key;
                        newKey.setPermission(permission2);
                        permissions.put(newKey, perm1);
                        permissions.remove(key);

                        //级联撤销权限
                        // if(perm1.getGrantType() == 1){
                        //
                        //     ArrayList<Byte> split = split(permission);
                        //
                        //     for(int i = 0; i < split.size(); i++) {
                        //         // System.out.println(it.next());
                        //         Permission Perm2 = perm1;
                        //         Perm2.setPermission(split.get(i).byteValue());
                        //         for(int j = 0; j < this.grantTo.get(Perm2).size(); j++){
                        //             User next = this.grantTo.get(Perm2).get(j);
                        //             Permission permission1 = next.permissions.get(key);
                        //             //修改对应位的权限
                        //
                        //         }
                        //     }
                        // }
                    }
                }
                else{
                    System.err.println("您无权撤销此权限！");
                }
            }
        }
    }

    public char[] byteToBit(byte b){
        String s1 = Integer.toBinaryString(b);
        int len = s1.length();
        char[] chars = new char[8];
        if(len >= 8){
            s1 = s1.substring(len-8, len);
            chars = s1.toCharArray();
        }
        else {
            chars = s1.toCharArray();
            chars = zeroize(chars, 8);
        }
        return chars;
    }

    public byte modify(byte old, byte token){
        char[] chars1 = byteToBit(old);
        char[] chars2 = byteToBit(token);
        for(int i = 0; i < 8; i++){
            if (chars2[i] == '1'){
                chars1[i] = '0';
                if(i+4 < 8){
                    chars1[i+4] = '0';
                }
            }
        }
        byte newperm = 0;
        if(chars1[0] == '1'){
            newperm -=128;
        }
        for(int i = 1; i < 8; i++){
            if(chars1[i] == '1'){
                newperm += (byte)Math.pow(2,7-i);
            }
        }

        char[] chars3 = byteToBit(newperm);
        for(int i = 0; i < chars3.length; i++){
            System.out.print(chars3[i]);
        }

        return newperm;
    }
}
