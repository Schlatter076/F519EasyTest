package loyer.client;

import java.awt.EventQueue;

import loyer.gui.LogInFrame;

public class LogIn extends LogInFrame {
  
  private String[] productType = {"F591正驾", "F591副驾"};
  
  public LogIn() {
    super();
    textField.setText(productType[0]);
  }

  @Override
  public void logInEvent() {
    if(!isDataView) {
      if(textField.getText().equals(productType[0])) {
        isDataView = true;
        frame.dispose();
        DataView.getView();
      } else {
        isDataView = true;
        frame.dispose();
        F591DataView.getView();
      }
    }
  }

  @Override
  public void chooseEvent() {
    if(textField.getText().equals(productType[0])) {
      textField.setText(productType[1]);
    } else {
      textField.setText(productType[0]);
    }
  }

  public static void main(String[] args) {
    
    EventQueue.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        LogIn win = new LogIn();
        win.frame.setVisible(true);
      }
    });
  }

}
