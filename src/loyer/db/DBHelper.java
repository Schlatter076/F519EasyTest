package loyer.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DBHelper {
  
  private final static String URL = "jdbc:mysql://localhost:3306/f519switch?useSSL=false&serverTimezone=UTC";
  private final static String USER = "root";
  private final static String PWD = "123456";
  private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
  private static PreparedStatement ptmt = null;
  private static Connection conn = null;
  private static ResultSet rs = null;
  
  private DBHelper() {}; //不允许其他类创建本类实例
  /**
   * 连接到数据库
   * @return
   */
  public static Connection getConnection() {
    Connection connection = null;
    try {
      Class.forName(DRIVER);
      connection = DriverManager.getConnection(URL, USER, PWD);
      
    } catch (ClassNotFoundException | SQLException e) {
      JOptionPane.showMessageDialog(null, "数据库连接失败：" + e.getLocalizedMessage());
    }
    return connection;
  }
  /**
   * 数据库查询操作
   * @param sql  sql语句
   * @param str
   * @return
   */
  public static ResultSet search(String sql, String[] str) {
    conn = getConnection();
    try {
      ptmt = conn.prepareStatement(sql);
      if(str != null) {
        for(int i = 0; i < str.length; i++) {
          ptmt.setString(i + 1, str[i]);
        }
      }
      rs = ptmt.executeQuery();
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "数据库操作失败：" + e.getLocalizedMessage());
    }
    return rs;
  }
  /**
   * 数据增删改方法
   * @param sql sql语句
   * @param str
   * @return
   */
  public static int AddU(String sql, String[] str) {
    int getBack = 0;
    conn = getConnection();
    try {
      ptmt = conn.prepareStatement(sql);
      if(str != null) {
        for(int i = 0; i < str.length; i++) {
          ptmt.setString(i + 1, str[i]);
        }
      }
      getBack = ptmt.executeUpdate();
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "数据库操作失败：" + e.getLocalizedMessage());
    }
    return getBack;
  }
  /**
   * 关闭数据库连接
   */
  public static void close() {
    
    if(ptmt != null) {
      try {
        ptmt.close();
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "数据库关闭失败：" + e.getLocalizedMessage());
      }
    }
    if(rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "数据库关闭失败：" + e.getLocalizedMessage());
      }
    }
    if(conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "数据库关闭失败：" + e.getLocalizedMessage());
      }
    }
  }
}
