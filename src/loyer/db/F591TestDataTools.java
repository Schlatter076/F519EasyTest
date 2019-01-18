package loyer.db;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class F591TestDataTools {

  private F591TestDataTools() {} //不允许其他类创建本类实例
  /**
   * 获取全部追溯表内容
   * @return
   */
  public static List<F591TestData> getAllByDB() {
    List<F591TestData> list = new ArrayList<>();
    String sql = "select * from vice_record";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      while(rs.next()) {
        int num = rs.getInt(1);
        String action = rs.getString(2);
        String items = rs.getString(3);
        String upper = rs.getString(4);
        String lower = rs.getString(5);
        String value = rs.getString(6);
        String unit = rs.getString(7);
        String result = rs.getString(8);
        String date = rs.getString(9);
        
        list.add(new F591TestData(num, action, items, upper, lower, value, unit, result, date));
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "测试数据追溯表加载失败：" + e.getLocalizedMessage());
    }
    return list;
  }
  /**
   * 获取指定日期的测试数据
   * @param date
   * @return
   */
  public static List<F591TestData> getByDate(String date) {
    List<F591TestData> list = new ArrayList<>();
    String sql = "select * from vice_record where date='"+date+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      while(rs.next()) {
        int num = rs.getInt(1);
        String action = rs.getString(2);
        String items = rs.getString(3);
        String upper = rs.getString(4);
        String lower = rs.getString(5);
        String value = rs.getString(6);
        String unit = rs.getString(7);
        String result = rs.getString(8);
        
        list.add(new F591TestData(num, action, items, upper, lower, value, unit, result, date));
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "测试数据追溯表加载失败：" + e.getLocalizedMessage());
    }
    return list;
  }
  /**
   * 向表中插入一条数据
   * @param datas
   * @return
   */
  public static int insert(String[] datas) {
    if(datas == null || datas.length != 8) {
      JOptionPane.showMessageDialog(null, "数据格式有误，请检查后重试！");
      return -1;
    } else {
      String sql = "insert into vice_record(action, items, upper, lower, value, unit, result, date) values(?, ?, ?, ?, ?, ?, ?, ?)";
      return DBHelper.AddU(sql, datas);
    }
  }
  /**
   * 将测试数据记录导出到本地
   */
  public static void outExcl() {
    WritableWorkbook wwb = null;
    try {
      String path = "excl/";
      File pathFile = new File(path);
      if(!pathFile.isDirectory()) {
        pathFile.mkdirs();
      }
      //创建可写入的Excel工作簿
      String fileName = "副驾测试数据" + LocalDate.now()+".xls";
      File file = new File(pathFile, fileName);
      if(!file.exists()) {
        file.createNewFile();
      }
      //以fileName为文件名来创建一个Workbook
      wwb = Workbook.createWorkbook(file);
      
      //创建工作表
      WritableSheet ws = wwb.createSheet("测试数据表", 0);
      
      //查询数据库中所有的数据
      List<F591TestData> list = getByDate(LocalDate.now().toString());
      //要插入到的excl表格的行号，默认从0开始
      Label label1 = new Label(0, 0, "产品编号");
      Label label2 = new Label(1, 0, "测试内容");
      Label label3 = new Label(2, 0, "对应引脚");
      Label label4 = new Label(3, 0, "上限值");
      Label label5 = new Label(4, 0, "下限值");
      Label label6 = new Label(5, 0, "实测值");
      Label label7 = new Label(6, 0, "单位");
      Label label8 = new Label(7, 0, "结果判定");
      Label label9 = new Label(8, 0, "日期");
      ws.addCell(label1);
      ws.addCell(label2);
      ws.addCell(label3);
      ws.addCell(label4);
      ws.addCell(label5);
      ws.addCell(label6);
      ws.addCell(label7);
      ws.addCell(label8);
      ws.addCell(label9);
      for(int i = 0; i < list.size(); i++) {
        Label label1_ = new Label(0, i+1, list.get(i).getNum() + "");
        Label label2_ = new Label(1, i+1, list.get(i).getAction());
        Label label3_ = new Label(2, i+1, list.get(i).getItems());
        Label label4_ = new Label(3, i+1, list.get(i).getUpper());
        Label label5_ = new Label(4, i+1, list.get(i).getLower());
        Label label6_ = new Label(5, i+1, list.get(i).getValue());
        Label label7_ = new Label(6, i+1, list.get(i).getUnit());
        Label label8_ = new Label(7, i+1, list.get(i).getResult());
        Label label9_ = new Label(8, i+1, list.get(i).getDate());
        ws.addCell(label1_);
        ws.addCell(label2_);
        ws.addCell(label3_);
        ws.addCell(label4_);
        ws.addCell(label5_);
        ws.addCell(label6_);
        ws.addCell(label7_);
        ws.addCell(label8_);
        ws.addCell(label9_);
      }
      //写进文档
      wwb.write();
      
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, "excl写入失败:" + e.getLocalizedMessage());
    } finally {
      //关闭Excel工作簿对象
      try {
        wwb.close();
      } catch (WriteException | IOException e) {
        JOptionPane.showMessageDialog(null, "excl导出失败:" + e.getLocalizedMessage());
      }
    }
  }
  /////////////////////////////////////////////
  public static class F591TestData {
    
    private int num;
    private String action;
    private String items;
    private String upper;
    private String lower;
    private String value;
    private String unit;
    private String result;
    private String date;
    
    
    public F591TestData() {
      super();
    }
    public F591TestData(int num, String action, String items, String upper, String lower, String value, String unit,
        String result, String date) {
      super();
      this.num = num;
      this.action = action;
      this.items = items;
      this.upper = upper;
      this.lower = lower;
      this.value = value;
      this.unit = unit;
      this.result = result;
      this.date = date;
    }
    public int getNum() {
      return num;
    }
    public void setNum(int num) {
      this.num = num;
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
    public String getDate() {
      return date;
    }
    public void setDate(String date) {
      this.date = date;
    }
  }
}
