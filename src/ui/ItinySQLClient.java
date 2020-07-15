package ui;

/**
 * 客户端功能接口
 *
 * @author Shinelon
 */
public interface ItinySQLClient {

    /**
     * 登陆接口
     *
     * @param host 数据库的url
     * @param port 数据库端口
     * @param useranme 账号
     * @param password 密码
     * @return 是否登陆成功
     */
    public boolean login(String host, int port, String useranme, String password);

    /**
     * 测试服务器连通性
     *
     * @param host
     * @param port
     * @return
     */
    public boolean testConnect(String host, int port);

    /**
     * 获取数据库清单
     * @param username
     * @return  返回类型待定
     */
    public Object getDatabases(String username);

    /**
     * 获取指定数据库的表清单
     * @param username
     * @return  返回类型待定
     */
    public Object getTables(String username, String databaseName);

    /**
     * 获取指定表的内容
     * @param username
     * @return  返回类型待定
     */
    public Object getTableContent(String username, String databaseName, String tableName);

    /**
     * 获取指定表的字段
     * @param username
     * @return  返回类型待定
     */
    public Object getTableField(String username, String databaseName, String tableName);

    /**
     * 获取指定表的索引
     * @param username
     * @return  返回类型待定
     */
    public Object getTableIndex(String username, String databaseName, String tableName);

    /**
     * 执行SQL语句
     * @param username
     * @return  返回类型待定
     */
    public Object excuteSQL(String username, String sql);

}
