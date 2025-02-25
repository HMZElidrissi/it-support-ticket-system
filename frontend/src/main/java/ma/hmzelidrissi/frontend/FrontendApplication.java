package ma.hmzelidrissi.frontend;

import javax.swing.*;
import ma.hmzelidrissi.frontend.ui.AuthFrame;

public class FrontendApplication {
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      System.setProperty("awt.useSystemAAFontSettings", "on");
      System.setProperty("swing.aatext", "true");

    } catch (Exception e) {
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {
      AuthFrame authFrame = new AuthFrame();
      authFrame.setVisible(true);
    });
  }
}