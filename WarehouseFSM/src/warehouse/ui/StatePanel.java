package warehouse.ui;

import javax.swing.JPanel;

public interface StatePanel {
  JPanel getPanel();
  void onShow();
  String getName();
}
