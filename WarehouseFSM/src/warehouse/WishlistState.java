package warehouse;

import java.util.List;

public class WishlistState implements WarehouseState {
  private final WarehouseContext ctx;

  public WishlistState(WarehouseContext ctx) { this.ctx = ctx; }

  @Override public String getName() { return "WishlistState"; }

  @Override
  public void run() {
    while (ctx.getCurrentStateIndex() == WarehouseContext.WISHLIST_STATE) {
      String cid = ctx.getCurrentClient();
      System.out.println();
      System.out.println("== Wishlist Menu (Client: " + (cid == null ? "-" : cid) + ") ==");
      System.out.println("1) View wishlist");
      System.out.println("2) Add or update item");
      System.out.println("3) Remove item");
      System.out.println("4) Place order");
      System.out.println("5) Logout");
      System.out.println("0) Back to client menu");

      int choice = ctx.promptInt("> ");
      switch (choice) {
        case 0: returnToClient(); break;
        case 1: displayWishlist(); break;
        case 2: addOrUpdateItem(); break;
        case 3: removeItem(); break;
        case 4: placeOrder(); break;
        case 5: ctx.logout(); break;
        default: System.out.println("Invalid option.");
      }
    }
  }

  private String currentClientIdOrWarn() {
    String cid = ctx.getCurrentClient();
    if (cid == null) System.out.println("No active client selected.");
    return cid;
  }

  private void displayWishlist() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    System.out.println("== Wishlist for " + cid + " ==");
    try {
      List<String> rows = ctx.warehouse().getWishlistForClient(cid);
      if (rows == null || rows.isEmpty()) { System.out.println("(empty)"); return; }
      for (String row : rows) System.out.println(row);
    } catch (Exception e) {
      System.out.println("Failed: " + e.getMessage());
    }
  }

  private void addOrUpdateItem() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    String pid = ctx.promptLine("Product ID: ");
    int qty = ctx.promptInt("Quantity: ");
    if (qty <= 0) {
      System.out.println("Quantity must be positive (use Remove for deletions).");
      return;
    }
    try {
      ctx.warehouse().addOrUpdateWishlistItem(cid, pid, qty);
      System.out.println("Wishlist updated.");
    } catch (Exception e) {
      System.out.println("Failed: " + e.getMessage());
    }
  }

  private void removeItem() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    String pid = ctx.promptLine("Product ID to remove: ");
    try {
      ctx.warehouse().addOrUpdateWishlistItem(cid, pid, 0);
      System.out.println("Removed (if it existed).");
    } catch (Exception e) {
      System.out.println("Failed: " + e.getMessage());
    }
  }

  private void placeOrder() {
    String cid = currentClientIdOrWarn(); if (cid == null) return;
    try {
      ctx.warehouse().placeOrder(cid);
      System.out.println("Order processed for " + cid);
    } catch (Exception e) {
      System.out.println("Failed: " + e.getMessage());
    }
  }

  private void returnToClient() {
    int ns = ctx.getNextState(WarehouseContext.WISHLIST_STATE, WarehouseContext.CMD_CLIENT);
    if (ns >= 0) ctx.setState(ns);
  }
}
