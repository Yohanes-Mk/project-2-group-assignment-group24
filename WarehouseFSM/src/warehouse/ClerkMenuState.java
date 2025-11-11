package warehouse;

public class ClerkMenuState implements WarehouseState {
  private final WarehouseContext ctx;

  public ClerkMenuState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "ClerkMenuState"; }

  @Override
  public void run() {
    while (ctx.getCurrentStateIndex() == WarehouseContext.CLERK_STATE) {
      System.out.println();
      System.out.println("== Clerk Menu ==");
      System.out.println("1) Add client");
      System.out.println("2) Show list of products (qty + price)");
      System.out.println("3) Show list of clients");
      System.out.println("4) Show list of clients with outstanding balance");
      System.out.println("5) Record payment");
      System.out.println("6) Become client");
      System.out.println("0) Logout");

      int choice = ctx.promptInt("> ");
      switch (choice) {
        case 0: ctx.logout(); break;
        case 1: addClient(); break;
        case 2: showProducts(); break;
        case 3: showClients(false); break;
        case 4: showClients(true); break;
        case 5: recordPayment(); break;
        case 6: becomeClient(); break;
        default: System.out.println("Invalid option.");
      }
    }
  }

  private void addClient() {
    String name = ctx.promptLine("Client name: ");
    String addr = ctx.promptLine("Address: ");
    try {
      String id = ctx.warehouse().addClient(name, addr);
      System.out.println("Added: " + id);
    } catch (Exception e) {
      System.out.println("Failed to add client: " + e.getMessage());
    }
  }

  private void showProducts() {
    System.out.println("== All products ==");
    for (Product p : ctx.warehouse().getAllProducts()) {
      System.out.println(p); // rely on Product.toString()
    }
  }

  private void showClients(boolean onlyWithBalance) {
    System.out.println("== All clients ==");
    for (Client c : ctx.warehouse().getAllClients()) {
      if (!onlyWithBalance || c.getBalance() > 0.0) System.out.println(c);
    }
  }

  private void recordPayment() {
    String cid = ctx.promptLine("Client ID: ");
    double amt = ctx.promptDouble("Amount: ");
    try {
      ctx.warehouse().recordPayment(cid, amt);
      System.out.println("Payment recorded");
    } catch (Exception e) {
      System.out.println("Payment failed: " + e.getMessage());
    }
  }

  private void becomeClient() {
    String cid = ctx.promptLine("Client ID to become: ");
    boolean valid = false;
    for (Client c : ctx.warehouse().getAllClients()) {
      if (c.getId().equalsIgnoreCase(cid)) { valid = true; break; }
    }
    if (!valid) { System.out.println("Invalid client ID."); return; }
    ctx.setCurrentClient(cid);
    ctx.setPreviousState(WarehouseContext.CLERK_STATE);
    int ns = ctx.getNextState(WarehouseContext.CLERK_STATE, WarehouseContext.CMD_CLIENT);
    ctx.setState(ns);
  }
}
