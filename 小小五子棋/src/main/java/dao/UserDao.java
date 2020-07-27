package dao;

import model.User;
import util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private UserDao(){

    }
    private static UserDao dao = new UserDao();
    public void addUser(User user) throws SQLException {              //添加用户
        //获取连接
        Connection conn = DbUtil.getConnection();
        //sql语句
        String sql = "INSERT INTO user(account, password, name, level)" +
                " values(?,?,?,?)";
        //预编译
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        ptmt.setString(1,user.getAccount());
        ptmt.setString(2,user.getPassword());
        ptmt.setString(3,user.getName());
        ptmt.setInt(4,user.getLevel());

        ptmt.execute();       //执行添加语句
    }
    public void delUser(String account) throws SQLException {            //删除用户
        //获取连接
        Connection conn = DbUtil.getConnection();
        //sql语句
        String sql = "delete from user where account=?";
        //预编译
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        ptmt.setString(1,account);

        ptmt.execute();
    }
    public User findUser(String account) throws SQLException {            //删除用户
        User user = null;

        //获取连接
        Connection conn = DbUtil.getConnection();
        //sql语句
        String sql = "select * from user where account=?";
        //预编译
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        ptmt.setString(1,account);

        ResultSet set = ptmt.executeQuery();
        while(set.next()){
            user = new User();
            user.setAccount(set.getString("account"));
            user.setPassword(set.getString("password"));
            user.setName(set.getString("name"));
            user.setLevel(set.getInt("level"));
        }
        return user;
    }
    public User logIn(User user) throws SQLException {            //用户登录
        User u = null;
        //获取连接
        Connection conn = DbUtil.getConnection();
        //sql语句
        String sql = "select * from user where account=? and password=?";
        //预编译
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        ptmt.setString(1,user.getAccount());
        ptmt.setString(2,user.getPassword());
        ResultSet set = ptmt.executeQuery();
        while(set.next()){
            u = new User();
            u.setAccount(set.getString("account"));
            u.setPassword(set.getString("password"));
            u.setName(set.getString("name"));
            u.setLevel(set.getInt("level"));
        }
        return u;
    }
    public static UserDao getDao(){
        return dao;
    }
}
