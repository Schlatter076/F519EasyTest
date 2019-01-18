package loyer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class F591ViceDataTools {

  private F591ViceDataTools() {} //不允许其他类创建本类实例
  /**
   * 获取副驾全部测试数据
   * @return
   */
  public static List<F591ViceData> getAllByDB() {
    List<F591ViceData> list = new ArrayList<>();
    String sql = "select * from f519_vice";
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
        
        list.add(new F591ViceData(action, items, upper, lower, value, unit, result));
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }
  
  public static class F591ViceData {
    private String action;
    private String items;
    private String upper;
    private String lower;
    private String value;
    private String unit;
    private String result;
    
    public F591ViceData() {
      super();
    }

    public F591ViceData(String action, String items, String upper, String lower, String value, String unit,
        String result) {
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
