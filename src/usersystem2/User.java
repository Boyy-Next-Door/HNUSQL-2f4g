package usersystem2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class User {
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
                    temp.setTable(new Table(perm1.getDatabase(), perm1.getTable().getTableName(),latter));
                    temp.setTarget(perm1.getTarget());
                    temp.setPermission(latter);
                    temp.setGrantedBy(perm1.getGrantedBy());
                    temp.setGrantType(perm1.getGrantType());

                    //保证HashMap<Table,Permission>中Table对象中的permission与Permission中的permission一致
                    Table newKey = new Table(key.getDb(), key.getTableName(), latter);
                    permissions.put(newKey, temp);
                    permissions.remove(key);

                    newPerm.setDatabase(perm1.getDatabase());
                    newPerm.setTable(new Table(perm1.getDatabase(),perm1.getTable().getTableName(),permission));
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
            Table table2 = new Table(newPerm.getDatabase(),newPerm.getTable().getTableName(),split.get(i).byteValue());
            Perm2.setTable(table2);
            Perm2.setGrantType(newPerm.getGrantType());
            Perm2.setGrantedBy(newPerm.getGrantedBy());
            Perm2.setPermission(split.get(i).byteValue());

            //查看hashCode所用的代码
            // System.out.println("Perm2:" + Perm2.hashCode());
            // for(Map.Entry<Permission, List<User>> entry: granter.grantTo.entrySet()) {
            //     Permission Perm1 = entry.getKey();
            //     System.out.println("Perm1:"+Perm1.getPermission() + Perm1.hashCode());
            // }


            //为什么不进入第一个if？？（已解决）
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
    public void revokePermission(String revokerName, String database, String table, byte permission, int deep){

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

                        //如果还有权限
                        if(permission2 != 0x00){
                            Permission temp = new Permission();
                            temp.setDatabase(perm1.getDatabase());
                            temp.setTable(new Table(perm1.getDatabase(), perm1.getTable().getTableName(),permission2));
                            temp.setTarget(perm1.getTarget());
                            temp.setPermission(permission2);
                            temp.setGrantedBy(perm1.getGrantedBy());
                            temp.setGrantType(perm1.getGrantType());

                            //这里Permission改了，Table要改
                            Table newKey = new Table(key.getDb(), key.getTableName(), permission2);
                            permissions.put(newKey, temp);
                            permissions.remove(key);
                        }
                        else{
                            permissions.remove(key);//反复remove会不会导致效率低下？
                        }

                        //修改revoker的grantTo
                        ArrayList<Byte> split = split(permission);
                        for(int i = 0; i < split.size(); i++){
                            Permission perm2 = new Permission();
                            perm2.setDatabase(key.getDb());
                            perm2.setTable(new Table(key.getDb(),table,split.get(i).byteValue()));
                            perm2.setGrantType(perm1.getGrantType());
                            perm2.setGrantedBy(perm1.getGrantedBy());
                            perm2.setPermission(split.get(i).byteValue());

                            if(revoker.grantTo.containsKey(perm2)){
                                if(revoker.grantTo.get(perm2).contains(this)){
                                    revoker.grantTo.get(perm2).remove(this);
                                    if(revoker.grantTo.get(perm2).size() == 0){
                                        revoker.grantTo.remove(perm2);//反复remove会不会导致效率低下？
                                    }
                                }else{
                                    System.err.println("error 1");
                                }
                            }
                            else{
                                System.err.println("error 2");
                            }
                        }


                        // 级联撤销权限
                        //deep>0的话，说明下面的都是级联撤销的范围，不管这一层的type是不是link，都撤销
                        if(perm1.getGrantType() == 1 || deep > 0){

                            ArrayList<Byte> split2 = split(permission);

                            for(int i = 0; i < split2.size(); i++) {
                                // System.out.println(it.next());
                                Permission perm3 = new Permission();
                                perm3.setDatabase(key.getDb());
                                perm3.setTable(new Table(key.getDb(),table,split2.get(i).byteValue()));
                                perm3.setGrantType(0);//grantType应该改为什么？
                                perm3.setGrantedBy(this);//grantBy应该改为什么？
                                perm3.setPermission(split2.get(i).byteValue());

                                Permission perm4 = new Permission();
                                perm4.setDatabase(key.getDb());
                                perm4.setTable(new Table(key.getDb(),table,split2.get(i).byteValue()));
                                perm4.setGrantType(1);//grantType应该改为什么？
                                perm4.setGrantedBy(this);//grantBy应该改为什么？
                                perm4.setPermission(split2.get(i).byteValue());

                                Permission perm5 = new Permission();
                                perm5.setDatabase(key.getDb());
                                perm5.setTable(new Table(key.getDb(),table,split2.get(i).byteValue()));
                                perm5.setGrantType(2);//grantType应该改为什么？
                                perm5.setGrantedBy(this);//grantBy应该改为什么？
                                perm5.setPermission(split2.get(i).byteValue());


                                //grantBy、grantType不同，导致无法进入if（已解决）
                                if(this.grantTo.containsKey(perm3)){
                                    for(int j = 0; j < this.grantTo.get(perm3).size(); j++){
                                        User next = this.grantTo.get(perm3).get(j);
                                        //修改对应位的权限
                                        next.revokePermission(this.username,database,table,perm3.getPermission(),deep+1);
                                        if(!this.grantTo.containsKey(perm3))
                                            break;
                                    }
                                }else if(this.grantTo.containsKey(perm4)){
                                    for(int j = 0; j < this.grantTo.get(perm4).size(); j++){
                                        User next = this.grantTo.get(perm4).get(j);
                                        //修改对应位的权限
                                        next.revokePermission(this.username,database,table,perm4.getPermission(), deep+1);
                                        if(!this.grantTo.containsKey(perm4))
                                            break;
                                    }
                                }else if(this.grantTo.containsKey(perm5)){
                                    for(int j = 0; j < this.grantTo.get(perm5).size(); j++){
                                        User next = this.grantTo.get(perm5).get(j);
                                        //修改对应位的权限
                                        next.revokePermission(this.username,database,table,perm5.getPermission(), deep+1);
                                        if(!this.grantTo.containsKey(perm5))
                                            break;
                                    }
                                }else{
                                    System.err.println("error 3");
                                }

                            }
                        }
                    }
                }
                else{
                    System.err.println("您无权撤销此权限！");
                }
            }
        }
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
