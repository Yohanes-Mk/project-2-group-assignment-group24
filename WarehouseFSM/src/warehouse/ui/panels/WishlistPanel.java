package warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import warehouse.WarehouseContext;
import warehouse.ui.GuiController;
import warehouse.ui.StatePanel;

public class WishlistPanel extends JPanel implements StatePanel {
  private final GuiController controller;
  private final JTextArea outputArea;
  private final JPanel actionsPanel;
  private final JTextField productField;
  private final JTextField quantityField;

  public WishlistPanel(GuiController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Wishlist");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    outputArea = new JTextArea();
    outputArea.setEditable(false);
    add(new JScrollPane(outputArea), BorderLayout.CENTER);

    actionsPanel = new JPanel();
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));

    productField = new JTextField();
    productField.setMaximumSize(new Dimension(140, 24));
    quantityField = new JTextField();
    quantityField.setMaximumSize(new Dimension(140, 24));

    actionsPanel.add(new JLabel("Product Code"));
    actionsPanel.add(productField);
    actionsPanel.add(new JLabel("Quantity"));
    actionsPanel.add(quantityField);
    actionsPanel.add(createButton("Add", this::addItem));
    actionsPanel.add(createButton("Remove", this::removeItem));
    actionsPanel.add(createButton("View Wishlist", this::viewWishlist));
    actionsPanel.add(createButton("Place Order", this::placeOrder));
    actionsPanel.add(createButton("Back to Client", () -> controller.changeTo(WarehouseContext.CLIENT_STATE)));
    actionsPanel.add(createButton("Logout", controller::logout));
    add(actionsPanel, BorderLayout.EAST);
  }

  private JButton createButton(String label, Runnable action) {
    JButton button = new JButton(label);
    button.setAlignmentX(LEFT_ALIGNMENT);
    button.addActionListener(e -> action.run());
    return button;
  }

  @Override
  public JPanel getPanel() {
    return this;
  }

  @Override
  public void onShow() {
    viewWishlist();
  }

  private void addItem() {
    String productId = productField.getText().trim();
    String qtyText = quantityField.getText().trim();
    if (productId.isEmpty() || qtyText.isEmpty()) {
      warn("Please enter product code and quantity.");
      return;
    }

    int qty;
    try {
      qty = Integer.parseInt(qtyText);
    } catch (NumberFormatException ex) {
      warn("Quantity must be an integer.");
      return;
    }
    if (qty <= 0) {
      warn("Quantity must be positive.");
      return;
    }

    String clientId = requireClient(); if (clientId == null) return;

    try {
      controller.getContext().warehouse().addOrUpdateWishlistItem(clientId, productId, qty);
      outputArea.setText("Wishlist updated: " + productId + " x" + qty);
    } catch (Exception ex) {
      warn("Failed: " + ex.getMessage());
    }
  }

  private void removeItem() {
    String productId = productField.getText().trim();
    if (productId.isEmpty()) {
      warn("Enter product code to remove.");
      return;
    }

    String clientId = requireClient(); if (clientId == null) return;

    try {
      controller.getContext().warehouse().addOrUpdateWishlistItem(clientId, productId, 0);
      outputArea.setText("Removed " + productId + " from wishlist.");
    } catch (Exception ex) {
      warn("Failed: " + ex.getMessage());
    }
  }

  private void viewWishlist() {
    String clientId = requireClient(); if (clientId == null) return;

    java.util.List<String> rows;
    try {
      rows = controller.getContext().warehouse().getWishlistForClient(clientId);
    } catch (Exception ex) {
      warn("Failed: " + ex.getMessage());
      return;
    }
    if (rows == null || rows.isEmpty()) {
      outputArea.setText("Wishlist is empty.");
      return;
    }

    StringBuilder sb = new StringBuilder("== Wishlist for ").append(clientId).append(" ==\n");
    for (String row : rows) sb.append(row).append('\n');
    outputArea.setText(sb.toString());
  }

  private void placeOrder() {
    String clientId = requireClient(); if (clientId == null) return;
    try {
      controller.getContext().warehouse().placeOrder(clientId);
      outputArea.setText("Order placed for " + clientId + ".\nCheck transactions for details.");
    } catch (Exception ex) {
      warn("Order failed: " + ex.getMessage());
    }
  }

  private String requireClient() {
    String clientId = controller.getContext().getCurrentClient();
    if (clientId == null || clientId.isBlank()) {
      warn("No client selected.");
      return null;
    }
    return clientId;
  }

  private void warn(String message) {
    JOptionPane.showMessageDialog(this, message, "Wishlist", JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public String getName() {
    return "Wishlist";
  }
}
