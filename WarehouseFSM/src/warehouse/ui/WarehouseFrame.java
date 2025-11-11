package warehouse.ui;

import java.awt.CardLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WarehouseFrame extends JFrame {
  private final CardLayout cards = new CardLayout();
  private final JPanel cardPanel = new JPanel(cards);
  private final Map<Integer, StatePanel> panelRegistry = new LinkedHashMap<>();

  public WarehouseFrame() {
    super("Warehouse GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(cardPanel);
    setSize(900, 600);
    setLocationRelativeTo(null);
  }

  public void register(int state, StatePanel panel) {
    panelRegistry.put(state, panel);
    cardPanel.add(panel.getPanel(), stateKey(state));
  }

  public void showState(int state) {
    StatePanel panel = panelRegistry.get(state);
    if (panel == null) return;
    cards.show(cardPanel, stateKey(state));
    panel.onShow();
  }

  private String stateKey(int state) {
    return "STATE_" + state;
  }
}
