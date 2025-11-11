package warehouse.ui.panels;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import warehouse.Client;
import warehouse.Product;
import warehouse.Transaction;
import warehouse.WarehouseContext;
import warehouse.ui.GuiController;
import warehouse.ui.StatePanel;

public class ClientPanel extends JPanel implements StatePanel {
  private final GuiController controller;
  private final JTextArea outputArea;
  private final JPanel actionsPanel;

  public ClientPanel(GuiController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Client");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    outputArea = new JTextArea();
    outputArea.setEditable(false);
    add(new JScrollPane(outputArea), BorderLayout.CENTER);

    actionsPanel = new JPanel();
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
    actionsPanel.add(createButton("Show Client Details", this::showClientDetails));
    actionsPanel.add(createButton("List Products", this::listProducts));
    actionsPanel.add(createButton("Show Transactions", this::showTransactions));
    actionsPanel.add(createButton("Open Wishlist", this::openWishlist));
    actionsPanel.add(createButton("Place Order", this::placeOrder));
    actionsPanel.add(createButton("Logout", controller::logout));
    add(actionsPanel, BorderLayout.EAST);
  }

  private JButton createButton(String label, Runnable action) {
    JButton button = new JButton(label);
    button.addActionListener(e -> action.run());
    return button;
  }

  @Override
  public JPanel getPanel() {
    return this;
  }

  @Override
  public void onShow() {
    String cid = controller.getContext().getCurrentClient();
    outputArea.setText(cid == null ? "No client selected." : "Client home for " + cid + ".");
  }

  private void showClientDetails() {
    String clientId = requireClient(); if (clientId == null) return;
    for (Client c : controller.getContext().warehouse().getAllClients()) {
      if (c.getId().equalsIgnoreCase(clientId)) {
        outputArea.setText(c.toString());
        return;
      }
    }
    showDialog("Client not found: " + clientId);
  }

  private void listProducts() {
    Collection<Product> products = controller.getContext().warehouse().getAllProducts();
    if (products == null || products.isEmpty()) {
      outputArea.setText("No products available.");
      return;
    }
    StringBuilder sb = new StringBuilder("== Products ==\n");
    for (Product p : products) sb.append(p).append('\n');
    outputArea.setText(sb.toString());
  }

  private void showTransactions() {
    String clientId = requireClient(); if (clientId == null) return;
    List<Transaction> txns = controller.getContext().warehouse().getTransactionsForClient(clientId);
    if (txns == null || txns.isEmpty()) {
      outputArea.setText("No transactions for " + clientId + ".");
      return;
    }
    StringBuilder sb = new StringBuilder("== Transactions ==\n");
    for (Transaction t : txns) sb.append(t).append('\n');
    outputArea.setText(sb.toString());
  }

  private void openWishlist() {
    if (requireClient() == null) return;
    controller.changeTo(WarehouseContext.WISHLIST_STATE);
  }

  private void placeOrder() {
    WarehouseContext ctx = controller.getContext();
    String clientId = ctx.getCurrentClient();
    if (clientId == null || clientId.isBlank()) {
      showDialog("No client selected. Please log in first.");
      return;
    }

    List<String> wishlist;
    try {
      wishlist = ctx.warehouse().getWishlistForClient(clientId);
    } catch (Exception ex) {
      showDialog("Cannot access wishlist: " + ex.getMessage());
      return;
    }
    if (wishlist == null || wishlist.isEmpty()) {
      showDialog("Wishlist is empty. Add items before placing an order.");
      return;
    }

    List<Transaction> before = new ArrayList<>(ctx.warehouse().getTransactionsForClient(clientId));
    try {
      ctx.warehouse().placeOrder(clientId);
    } catch (Exception ex) {
      showDialog("Order failed: " + ex.getMessage());
      return;
    }

    List<Transaction> after = ctx.warehouse().getTransactionsForClient(clientId);
    if (after.size() == before.size()) {
      showDialog("Order processed, but no items could be fulfilled (possibly out of stock).");
      return;
    }

    double total = 0;
    StringBuilder receipt = new StringBuilder("== Receipt for ").append(clientId).append(" ==\n");
    for (int i = before.size(); i < after.size(); i++) {
      Transaction t = after.get(i);
      receipt.append(t).append('\n');
      total += t.getTotal();
    }
    receipt.append("\nTotal: $").append(String.format("%.2f", total));
    outputArea.setText(receipt.toString());
  }

  private String requireClient() {
    String clientId = controller.getContext().getCurrentClient();
    if (clientId == null || clientId.isBlank()) {
      showDialog("No client selected. Please log in first.");
      return null;
    }
    return clientId;
  }

  private void showDialog(String message) {
    JOptionPane.showMessageDialog(this, message, "Client", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public String getName() {
    return "Client";
  }
}
