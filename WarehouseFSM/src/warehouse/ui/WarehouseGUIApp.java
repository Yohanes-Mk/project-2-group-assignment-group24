package warehouse.ui;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import warehouse.WarehouseContext;
import warehouse.WarehouseFSMApp;
import warehouse.ui.panels.ClientPanel;
import warehouse.ui.panels.ClerkPanel;
import warehouse.ui.panels.ManagerPanel;
import warehouse.ui.panels.OpeningPanel;
import warehouse.ui.panels.WishlistPanel;

public class WarehouseGUIApp {
  public static void main(String[] args) {
    if (GraphicsEnvironment.isHeadless()) {
      System.out.println("[INFO] Headless environment detected. Falling back to console mode.");
      WarehouseFSMApp.main(args);
      return;
    }

    SwingUtilities.invokeLater(() -> {
      WarehouseFrame frame = new WarehouseFrame();
      GuiController controller = new GuiController(frame);

      frame.register(WarehouseContext.OPENING_STATE, new OpeningPanel(controller));
      frame.register(WarehouseContext.CLIENT_STATE, new ClientPanel(controller));
      frame.register(WarehouseContext.CLERK_STATE, new ClerkPanel(controller));
      frame.register(WarehouseContext.MANAGER_STATE, new ManagerPanel(controller));
      frame.register(WarehouseContext.WISHLIST_STATE, new WishlistPanel(controller));

      frame.showState(WarehouseContext.OPENING_STATE);
      frame.setVisible(true);
    });
  }
}
