package loyer.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class F591RecordDataTools {

  
  private F591RecordDataTools() {} //不允许其他类创建本类实例
  /**
   * 通过日期获取表中记录的数据
   * @param date
   * @return
   */
  public static F591RecordData getByDate(String date) {
    F591RecordData data = null;
    String sql = "select * from vice_recordtd where recordtime='"+date+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      if(rs.next()) {
        String name = rs.getString(1);
        String sum = rs.getString(2);
        String ok = rs.getString(3);
        String ng = rs.getString(4);
        String times = rs.getString(5);
        
        data = new F591RecordData(name, sum, ok, ng, times, date);
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "不良记录表获取失败:" + e.getLocalizedMessage());
    }
    return data;
  }
  /**
   * 向数据库中插入一条当天的测试数据
   * @param datas
   * @return
   */
  public static int insert(String[] datas) {
    if(datas == null || datas.length != 6) {
      JOptionPane.showMessageDialog(null, "数据格式有误，请重试!");
      return -1;
    } else {
      String sql = "insert into vice_recordtd values(?, ?, ?, ?, ?, ?)";
      return DBHelper.AddU(sql, datas);
    }
  }
  /**
   * 更新库里数据
   * @param datas
   * @return
   */
  public static int update(String[] datas) {
    if(datas == null || datas.length != 6) {
      JOptionPane.showMessageDialog(null, "数据格式有误，请重试!");
      return -1;
    } else {
      String sql = "update vice_recordtd set recordsum='"+datas[1]+"', recordok='"+datas[2]+"', recordng='"+datas[3]+"',"
          + "recordts='"+datas[4]+"' where recordtime='"+datas[5]+"'";
      return DBHelper.AddU(sql, null);
    }
  }
  
  public static class F591RecordData {
    private String name;
    private String sum;
    private String ok;
    private String ng;
    private String times;
    private String date;
    public F591RecordData() {
      super();
    }
    public F591RecordData(String name, String sum, String ok, String ng, String times, String date) {
      super();
      this.name = name;
      this.sum = sum;
      this.ok = ok;
      this.ng = ng;
      this.times = times;
      this.date = date;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getSum() {
      return sum;
    }
    public void setSum(String sum) {
      this.sum = sum;
    }
    public String getOk() {
      return ok;
    }
    public void setOk(String ok) {
      this.ok = ok;
    }
    public String getNg() {
      return ng;
    }
    public void setNg(String ng) {
      this.ng = ng;
    }
    public String getTimes() {
      return times;
    }
    public void setTimes(String times) {
      this.times = times;
    }
    public String getDate() {
      return date;
    }
    public void setDate(String date) {
      this.date = date;
    }
  }
}
