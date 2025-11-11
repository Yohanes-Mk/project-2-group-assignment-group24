package warehouse;

import java.util.List;

public class ClientMenuState implements WarehouseState {
  private final WarehouseContext ctx;

  public ClientMenuState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "ClientMenuState"; }

  @Override
  public void run() {
    while (ctx.getCurrentStateIndex() == WarehouseContext.CLIENT_STATE) {
      String cid = ctx.getCurrentClient();
      System.out.println();
      System.out.println("== Client Menu (Client: " + (cid == null ? "-" : cid) + ") ==");
      System.out.println("1) Show client details");
      System.out.println("2) Show list of products (with price)");
      System.out.println("3) Show client transactions");
      System.out.println("4) Wishlist operations");
      System.out.println("0) Logout");

      int choice = ctx.promptInt("> ");
      switch (choice) {
        case 0: ctx.logout(); break;
        case 1: showClientDetails(); break;
        case 2: showProducts(); break;
        case 3: showTransactions(); break;
        case 4: openWishlistState(); break;
        default: System.out.println("Invalid option.");
      }
    }
  }

  private String currentClientIdOrWarn() {
    String cid = ctx.getCurrentClient();
    if (cid == null) System.out.println("No active client selected.");
    return cid;
  }

  private void showClientDetails() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    for (Client c : ctx.warehouse().getAllClients()) {
      if (c.getId().equalsIgnoreCase(cid)) { System.out.println(c); return; }
    }
    System.out.println("Client not found.");
  }

  private void showProducts() {
    System.out.println("== All products ==");
    for (Product p : ctx.warehouse().getAllProducts()) System.out.println(p);
  }

  private void showTransactions() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    System.out.println("== Transactions for " + cid + " ==");
    for (Transaction t : ctx.warehouse().getTransactionsForClient(cid)) {
      System.out.println(t);
    }
  }

  private void openWishlistState() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    int ns = ctx.getNextState(WarehouseContext.CLIENT_STATE, WarehouseContext.CMD_WISHLIST);
    if (ns >= 0) ctx.setState(ns);
  }
}
