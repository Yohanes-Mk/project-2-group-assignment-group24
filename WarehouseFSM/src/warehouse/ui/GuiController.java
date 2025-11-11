package warehouse.ui;

import warehouse.Client;
import warehouse.WarehouseContext;

public class GuiController {
  private final WarehouseContext context;
  private final WarehouseFrame frame;

  public GuiController(WarehouseFrame frame) {
    this.frame = frame;
    this.context = WarehouseContext.instance();
  }

  public WarehouseContext getContext() {
    return context;
  }

  public WarehouseFrame getFrame() {
    return frame;
  }

  public String createClient(String name, String address) {
    String id = context.warehouse().addClient(name, address);
    return id;
  }

  public boolean clientExists(String clientId) {
    if (clientId == null || clientId.isBlank()) return false;
    for (Client c : context.warehouse().getAllClients()) {
      if (c.getId().equalsIgnoreCase(clientId.trim())) return true;
    }
    return false;
  }

  public boolean loginAsClient(String clientId, int previousState) {
    if (!clientExists(clientId)) return false;
    context.setCurrentClient(clientId.trim());
    context.setPreviousState(previousState);
    changeTo(WarehouseContext.CLIENT_STATE);
    return true;
  }

  public void loginAsClerk() {
    context.setCurrentClient(null);
    changeTo(WarehouseContext.CLERK_STATE);
  }

  public void loginAsManager() {
    context.setCurrentClient(null);
    changeTo(WarehouseContext.MANAGER_STATE);
  }

  public void logout() {
    context.logout();
    int next = context.getCurrentStateIndex();
    if (next == WarehouseContext.EXIT_STATE) frame.dispose();
    else frame.showState(next);
  }

  public void changeTo(int state) {
    context.setState(state);
    frame.showState(state);
  }

  public void quitApplication() {
    frame.dispose();
  }
}
