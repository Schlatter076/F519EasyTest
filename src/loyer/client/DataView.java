package loyer.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import loyer.db.F519DataTools;
import loyer.db.F519DataTools.F519Data;
import loyer.db.RecordDataTools;
import loyer.db.RecordDataTools.RecordData;
import loyer.db.TestDataTools;
import loyer.exception.NoSuchPort;
import loyer.exception.NotASerialPort;
import loyer.exception.PortInUse;
import loyer.exception.SerialPortParamFail;
import loyer.exception.TooManyListeners;
import loyer.gui.LoyerFrame;
import loyer.serial.SerialPortTools;

public class DataView extends LoyerFrame {

  private JTable table;
  private MyTableCellRenderrer myTableCellRenderrer;
  private Timer timer1;
  //private Timer timer2;
  private int myCount = 0;
  private SerialPort COM1 = null;
  private SerialPort COM2 = null;
  /**串口列表*/
  private ArrayList<String> portList = SerialPortTools.findPort();
  /**单片机返回数据起始字节1*/
  private static final byte FIRST_TEXT = (byte) 0xf3;
  /**单片机返回数据起始字节2*/
  private static final byte SECOND_TEXT = (byte) 0xf4;
  /**单片机返回数据结束字节*/
  private static final byte END_TEXT = 0x0a;
  /**单片机接收缓冲区大小*/
  private static final int BUFFER_SIZE = 11;
  /**单片机接收字节数组*/
  private static byte[] rx_buffer = new byte[BUFFER_SIZE];
  /**单片机串口接收单个字节*/
  private static byte data = 0;
  /**单片机串口接收计数器*/
  private static int rxCounter = 0;
  /**单片机接收完整数据包标志位*/
  private static boolean hasData = false;
  //private JButton button;

  @Override
  public boolean pwdIsPassed(String command) {
    return false;
  }

  @Override
  public void usartMethod() {
  }

  @Override
  public void resultView() {
  }

  @Override
  public void reportView() {
  }

  @Override
  public void nayinMethod() {
  }

  @Override
  public void close() {
    addNull();
    TestDataTools.outExcl();
    timer1.stop();
    //timer2.stop();
    System.exit(0);
  }

  public DataView() {
    super();
    PRODUCT_NAME = "F591正驾车窗开关简易测试系统";
    productField.setText(PRODUCT_NAME);
    table = completedTable(getTestTable());
    dataPanel.setViewportView(table);
    
    timer1 = new Timer(20, e -> {
      if(hasData) {
        hasData = false;
        if(rx_buffer[2] == 0x11) {
          mcu_reset();
          addNull();
        } else {
          myCount = rx_buffer[9];
          if(!statuField.getText().equals("正在测试...")) {
            statuField.setText("正在测试...");
          }
          if(myCount == 0) {
            initTable();
          }
          SerialPortTools.writeString(COM1, "UTF-8", ":MEASure:RESistance?");
        }
      }
      if(statuField.getText().equals("正在测试...")) {
        if(progressValue >= 100) progressValue = 0;
        progressBar.setValue(progressValue);
        progressValue++;
        timeField.setText(calculate(timeCount));
        timeCount += 20;
      }
    });
    timer1.start();
    
    com1Butt.addActionListener(e -> {
      if(COM1 == null) {  //如果串口1被关闭了
        initCOM1();
      }
      else
        com1Butt.setSelected(true);
    });
    com2Butt.addActionListener(e -> {
      if(COM2 == null) {
        initCOM2();
      }
      else
        com2Butt.setSelected(true);
    });
  }
  //////////////////////////////////////////////////////////////
  /**
   * 获取正驾测试页面
   */
  public static void getView() {
    
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        DataView win = new DataView();
        win.frame.setVisible(true);
        win.initLoad();
        win.setTableCellRenderer();
      }
    });
  }
  
  /////////////////////////////////////////////////////////////
  /**
   * 创建JTable方法
   * 
   * @return
   */
  public JTable getTestTable() {
    Vector<Object> rowNum = null, colNum = null;
    // 创建列对象
    colNum = new Vector<>();
    colNum.add("persist");
    colNum.add("测试内容");
    colNum.add("对应引脚");
    colNum.add("上限");
    colNum.add("下限");
    colNum.add("测试值");
    colNum.add("单位");
    colNum.add("测试结果");
    colNum.add("备注");

    // 创建行对象
    rowNum = new Vector<>();
    List<F519Data> tableList = F519DataTools.getAllByDB(); // 从数据库中获取c211表的内容

    for (Iterator<F519Data> i = tableList.iterator(); i.hasNext();) {
      F519Data rd = i.next();
      Vector<String> vt = new Vector<>();
      vt.add("*");
      vt.add(rd.getAction());
      vt.add(rd.getItems());
      vt.add(rd.getUpper());
      vt.add(rd.getLower());
      vt.add(rd.getValue());
      vt.add(rd.getUnit());
      vt.add(rd.getResult());

      rowNum.add(vt);
    }

    DefaultTableModel model = new DefaultTableModel(rowNum, colNum) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    JTable table = new JTable(model);
    return table;
  }

  /**
   * 提供设置JTable方法
   * 
   * @param table
   * @return
   */
  public JTable completedTable(JTable table) {

    DefaultTableCellRenderer r = new DefaultTableCellRenderer(); // 设置
    r.setHorizontalAlignment(JLabel.CENTER); // 单元格内容居中
    // table.setOpaque(false); //设置表透明
    JTableHeader jTableHeader = table.getTableHeader(); // 获取表头
    // 设置表头名称字体样式
    jTableHeader.setFont(new Font("宋体", Font.PLAIN, 14));
    // 设置表头名称字体颜色
    jTableHeader.setForeground(Color.BLACK);
    jTableHeader.setDefaultRenderer(r);

    // 表头不可拖动
    jTableHeader.setReorderingAllowed(false);
    // 列大小不可改变
    jTableHeader.setResizingAllowed(false);

    // 设置列宽
    TableColumn col_0 = table.getColumnModel().getColumn(0);
    TableColumn col_1 = table.getColumnModel().getColumn(1);
    TableColumn col_2 = table.getColumnModel().getColumn(2);
    TableColumn col_3 = table.getColumnModel().getColumn(3);
    TableColumn col_4 = table.getColumnModel().getColumn(4);
    TableColumn col_5 = table.getColumnModel().getColumn(5);
    TableColumn col_7 = table.getColumnModel().getColumn(7);
    TableColumn col_8 = table.getColumnModel().getColumn(8);
    col_0.setPreferredWidth(80);
    col_1.setPreferredWidth(200);
    col_2.setPreferredWidth(150);
    col_3.setPreferredWidth(120);
    col_4.setPreferredWidth(120);
    col_5.setPreferredWidth(120);
    col_7.setPreferredWidth(120);
    col_8.setPreferredWidth(200);

    // table.setEnabled(false); // 内容不可编辑
    table.setDefaultRenderer(Object.class, r); // 居中显示

    table.setRowHeight(30); // 设置行高
    // 增加一行空白行
    // AbstractTableModel tableModel = (AbstractTableModel) table.getModel();
    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
    tableModel.addRow(new Object[] { "*", "", "", "", "", "", "", "", "", "", "", "", "" });
    table.setGridColor(new Color(245, 245, 245)); // 设置网格颜色
    table.setForeground(Color.BLACK); // 设置文字颜色
    table.setBackground(new Color(245, 245, 245));
    table.setFont(new Font("宋体", Font.PLAIN, 13));
    // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 关闭表格列自动调整
    return table;
  }

  /**
   * 初始化table值
   */
  public void initTable() {
    for (int i = 0; i < 19; i++) {
      table.setValueAt("?", i, 5); // 清空测试值
      table.setValueAt("?", i, 7); // 清空测试结果
    }
  }
  /**
   * 初始化界面计数值和饼图
   */
  public void initCountAndPieChart() {
    RecordData rd = RecordDataTools.getByDate(LocalDate.now().toString());
    if (rd != null) {
      okCount = Integer.parseInt(rd.getOk());
      ngCount = Integer.parseInt(rd.getNg());
      totalCount = Integer.parseInt(rd.getSum());
      timeCount = 0;
      okField.setText(okCount + "");
      ngField.setText(ngCount + "");
      totalField.setText(totalCount + "");
      timeField.setText(timeCount + "");
      setPieChart(okCount, ngCount);
    } 
    else {
      okCount = 0;
      ngCount = 0;
      totalCount = 0;
      timeCount = 0;
      okField.setText(okCount + "");
      ngField.setText(ngCount + "");
      totalField.setText(totalCount + "");
      timeField.setText(timeCount + "");
      setPieChart(okCount, ngCount);
    }
  }
  /**
   * 获取对应单元格的数值
   * @param row
   * @param col
   */
  public double getDoubleValue(int row, int col) {
    return Double.parseDouble(table.getValueAt(row, col).toString());
  }
  /**
   * 判断测试结果是否ok
   * @param row 行数，从0开始
   */
  public void ifResultPass(int row) {
    double val = getDoubleValue(row, 5);
    if(val <= getDoubleValue(row, 3) && val >= getDoubleValue(row, 4)) {
      table.setValueAt("PASS", row, 7);
    } else {
      table.setValueAt("NG", row, 7);
    }
  }
  /**
   * 初始化串口1
   */
  public void initCOM1() {
    if(portList.contains("COM1") && COM1 == null) {
      try {
        COM1 = SerialPortTools.getPort("COM1", 9600, 8, 1, 0);
      } catch (SerialPortParamFail | NotASerialPort | NoSuchPort | PortInUse e) {
        JOptionPane.showMessageDialog(null, "COM1:" + e.toString());
      }
      com1Butt.setSelected(true);
      try {
        SerialPortTools.add(COM1, event -> {
          switch (event.getEventType()) {
          case SerialPortEvent.BI:  //10 通讯中断
            JOptionPane.showMessageDialog(null, "COM1:" + "通讯中断!");
            break;
          case SerialPortEvent.OE:  // 7 溢位（溢出）错误
            JOptionPane.showMessageDialog(null, "COM1:" + "溢位（溢出）错误!");
            break;
          case SerialPortEvent.FE:  // 9 帧错误
            JOptionPane.showMessageDialog(null, "COM1:" + "帧错误!");
            break;
          case SerialPortEvent.PE:  // 8 奇偶校验错误
            JOptionPane.showMessageDialog(null, "COM1:" + "奇偶校验错误!");
            break;
          case SerialPortEvent.CD:  // 6 载波检测
            JOptionPane.showMessageDialog(null, "COM1:" + "载波检测!");
            break;
          case SerialPortEvent.CTS:  // 3 清除待发送数据
            JOptionPane.showMessageDialog(null, "COM1:" + "清除待发送数据!");
            break;
          case SerialPortEvent.DSR:  // 4 待发送数据准备好了
            JOptionPane.showMessageDialog(null, "COM1:" + "待发送数据准备好了!");
            break;
          case SerialPortEvent.RI:  // 5 振铃指示
            JOptionPane.showMessageDialog(null, "COM1:" + "振铃指示!");
            break;
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  // 2 输出缓冲区已清空
            JOptionPane.showMessageDialog(null, "COM1:" + "输出缓冲区已清空");
            break;
          case SerialPortEvent.DATA_AVAILABLE: {
            // 有数据到达-----可以开始处理
            COM1DatasArrived();
          }
            break;
          }
        });
        SerialPortTools.writeString(COM1, "UTF-8", "*RST");
        SerialPortTools.writeString(COM1, "UTF-8", ":RATE:RESistance");
      } catch (TooManyListeners e) {
        JOptionPane.showMessageDialog(null, "COM1:" + e.toString());
      }
    }
    else {
      JOptionPane.showMessageDialog(null, "未发现串口1！");
      com1Butt.setSelected(false);
    }
  }
  /**
   * 初始化串口2
   */
  public void initCOM2() {
    if(portList.contains("COM2") && COM2 == null) {
      try {
        COM2 = SerialPortTools.getPort("COM2", 9600, 8, 1, 0);
      } catch (SerialPortParamFail | NotASerialPort | NoSuchPort | PortInUse e) {
        JOptionPane.showMessageDialog(null, "COM2:" + e.toString());
      }
      com2Butt.setSelected(true);
      try {
        SerialPortTools.add(COM2, arg0 -> {
          switch (arg0.getEventType()) {
          case SerialPortEvent.BI:  //10 通讯中断
          case SerialPortEvent.OE:  // 7 溢位（溢出）错误
          case SerialPortEvent.FE:  // 9 帧错误
          case SerialPortEvent.PE:  // 8 奇偶校验错误
          case SerialPortEvent.CD:  // 6 载波检测
          case SerialPortEvent.CTS:  // 3 清除待发送数据
          case SerialPortEvent.DSR:  // 4 待发送数据准备好了
          case SerialPortEvent.RI:  // 5 振铃指示
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  // 2 输出缓冲区已清空
            JOptionPane.showMessageDialog(null, "COM2错误：" + arg0.toString());
            break;
          case SerialPortEvent.DATA_AVAILABLE: {
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            if(!hasData) {
              rx_buffer = SerialPortTools.readBytes(COM2);
              hasData = true;
            }
          }
            break;
          }
        });
        //发送主驾测试指令
        SerialPortTools.writeBytes(COM2, new byte[]{(byte) 0xf3, (byte) 0xf4, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a});
      } catch (TooManyListeners e) {
        JOptionPane.showMessageDialog(null, "COM2:" + e.toString());
      }
    }
    else {
      JOptionPane.showMessageDialog(null, "未发现串口2！");
      com2Butt.setSelected(false);
    }
  }
  /**
   * 初始化串口
   */
  public void initPort() {
    initCOM1();
    initCOM2();
  }
  /**
   * 串口1数据到达
   */
  public void COM1DatasArrived() {
    String value = SerialPortTools.readString(COM1, "UTF-8");
    table.setValueAt(new BigDecimal(value).toPlainString().substring(0, 6), myCount, 5);
    ifResultPass(myCount);
    /*
    String[] datas = new String[7];
    for(int i = 1; i <= 7; i++) {
      datas[i - 1] = table.getValueAt(myCount, i).toString();
    }
    TestDataTools.insert(datas);//*/
    addOneRecord(myCount);
    if(myCount == 18) {
      /*
      for(int i = 1; i <= 7; i++) {
        datas[i - 1] = "--";
      }
      TestDataTools.insert(datas);//*/
      addNull();
      totalCount++;
      totalField.setText(totalCount + "");
      mcu_reset();
    }
  }
  
  public void mcu_reset() {
    timeCount = 0;
    timeField.setText(timeCount + "");
    progressValue = 0;
    progressBar.setValue(progressValue);
    statuField.setText("STOP");
    initTable();
  }
  /**
   * 初始化
   */
  public void initLoad() {
    initCountAndPieChart();
    initTable();
    initPort();
  }
  /**
   * 获取表中某一行测试数据添加到追溯表中
   * @param row
   */
  public void addOneRecord(int row) {
    String[] datas = new String[8];
    for(int i = 1; i <= 7; i++) {
      datas[i - 1] = table.getValueAt(row, i).toString();
    }
    datas[7] = LocalDate.now().toString();
    TestDataTools.insert(datas);
  }
  
  public void addNull() {
    String[] datas = new String[8];
    for(int i = 1; i <= 7; i++) {
      datas[i - 1] = "--";
    }
    datas[7] = LocalDate.now().toString();
    TestDataTools.insert(datas);
  }
  
  /**
   * table渲染色，测试结果为"PASS"则设为绿色，"NG"为红色
   */
  public void setTableCellRenderer() {
    if (myTableCellRenderrer == null) {
      myTableCellRenderrer = new MyTableCellRenderrer();
      table.getColumnModel().getColumn(7).setCellRenderer(myTableCellRenderrer);
    } else
      table.getColumnModel().getColumn(7).setCellRenderer(myTableCellRenderrer);
  }
  ///////////////////////////////////////////////////////////////////////////
  /**
   * 定义一个类用来渲染某一单元格 用法：获取某一列值，其中单元格值为"PASS"则设为绿色，若为"NG"则设为红色
   */
  class MyTableCellRenderrer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

      super.setHorizontalAlignment(JLabel.CENTER); // 该列居中显示
      Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if ("PASS".equals(value + "")) {
        comp.setBackground(new Color(0, 204, 51));
      } else if ("NG".equals(value + "")) {
        comp.setBackground(Color.RED);
      } else {
        if(isSelected) {
          comp.setBackground(table.getSelectionBackground());// 这一行保证其他单元格颜色不变
        } else 
          comp.setBackground(new Color(245, 245, 245));// 这一行保证其他单元格颜色不变
      }
      return comp;
    }
  }
  //////////////////////////////////////////////////////////////////////////////

}
