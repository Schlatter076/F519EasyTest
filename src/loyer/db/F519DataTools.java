package loyer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class F519DataTools {

  private F519DataTools() {} //不允许其他类创建本类实例
  /**
   * 获取全部测试数据
   * @return
   */
  public static List<F519Data> getAllByDB() {
    List<F519Data> list = new ArrayList<>();
    String sql = "select * from f519";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      while(rs.next()) {
        String action = rs.getString(1);
        String items = rs.getString(2);
        String upper = rs.getString(3);
        String lower = rs.getString(4);
        String value = rs.getString(5);
        String unit = rs.getString(6);
        String result = rs.getString(7);
        
        list.add(new F519Data(action, items, upper, lower, value, unit, result));
      } 
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "测试数据表加载失败" + e.getLocalizedMessage());
    }
    return list;
  }
  
  /**
   * F519表的实体
   * @author hw076
   *
   */
  public static class F519Data {
    
    private String action;
    private String items;
    private String upper;
    private String lower;
    private String value;
    private String unit;
    private String result;
    
    public F519Data() {
      super();
    }
    public F519Data(String action, String items, String upper, String lower, String value, String unit, String result) {
      super();
      this.action = action;
      this.items = items;
      this.upper = upper;
      this.lower = lower;
      this.value = value;
      this.unit = unit;
      this.result = result;
    }
    public String getAction() {
      return action;
    }
    public void setAction(String action) {
      this.action = action;
    }
    public String getItems() {
      return items;
    }
    public void setItems(String items) {
      this.items = items;
    }
    public String getUpper() {
      return upper;
    }
    public void setUpper(String upper) {
      this.upper = upper;
    }
    public String getLower() {
      return lower;
    }
    public void setLower(String lower) {
      this.lower = lower;
    }
    public String getValue() {
      return value;
    }
    public void setValue(String value) {
      this.value = value;
    }
    public String getUnit() {
      return unit;
    }
    public void setUnit(String unit) {
      this.unit = unit;
    }
    public String getResult() {
      return result;
    }
    public void setResult(String result) {
      this.result = result;
    }
  }
}
