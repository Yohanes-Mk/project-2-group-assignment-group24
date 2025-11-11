package warehouse;

import java.util.List;

public class ManagerMenuState implements WarehouseState {
  private final WarehouseContext ctx;

  public ManagerMenuState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "ManagerMenuState"; }

  @Override
  public void run() {
    while (ctx.getCurrentStateIndex() == WarehouseContext.MANAGER_STATE) {
      System.out.println();
      System.out.println("== Manager Menu ==");
      System.out.println("1) Add product");
      System.out.println("2) Display waitlist for a product");
      System.out.println("3) Receive a shipment");
      System.out.println("4) Become clerk");
      System.out.println("0) Logout");

      int choice = ctx.promptInt("> ");
      switch (choice) {
        case 0: ctx.logout(); break;
        case 1: addProduct(); break;
        case 2: displayWaitlist(); break;
        case 3: receiveShipment(); break;
        case 4: becomeClerk(); break;
        default: System.out.println("Invalid option.");
      }
    }
  }

  private void addProduct() {
    String name = ctx.promptLine("Product name: ");
    int qty = ctx.promptInt("Initial quantity: ");
    double price = ctx.promptDouble("Unit price: ");
    try {
      String id = ctx.warehouse().addProduct(name, qty, price);
      System.out.println("Added: " + id);
    } catch (Exception e) {
      System.out.println("Failed to add product: " + e.getMessage());
    }
  }

  private void displayWaitlist() {
    String pid = ctx.promptLine("Product ID: ");
    System.out.println("== Waitlist for " + pid + " ==");
    try {
      List<String> rows = ctx.warehouse().getWaitlistView(pid);
      if (rows == null || rows.isEmpty()) { System.out.println("(empty)"); return; }
      for (String row : rows) System.out.println(row);
    } catch (Exception e) {
      System.out.println("Failed: " + e.getMessage());
    }
  }

  private void receiveShipment() {
    String pid = ctx.promptLine("Product ID: ");
    int qty = ctx.promptInt("Quantity received: ");
    try {
      ctx.warehouse().receiveShipment(pid, qty);
      System.out.println("Shipment processed");
    } catch (Exception e) {
      System.out.println("Shipment failed: " + e.getMessage());
    }
  }

  private void becomeClerk() {
    int ns = ctx.getNextState(WarehouseContext.MANAGER_STATE, WarehouseContext.CMD_CLERK);
    ctx.setState(ns);
  }
}
