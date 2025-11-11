package warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import warehouse.Product;
import warehouse.WarehouseContext;
import warehouse.ui.GuiController;
import warehouse.ui.StatePanel;

public class ManagerPanel extends JPanel implements StatePanel {
  private final GuiController controller;
  private final JTextArea outputArea;
  private final JPanel actionsPanel;

  public ManagerPanel(GuiController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Manager");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    outputArea = new JTextArea();
    outputArea.setEditable(false);
    add(new JScrollPane(outputArea), BorderLayout.CENTER);

    actionsPanel = new JPanel();
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
    actionsPanel.add(createButton("Add Product", this::addProduct));
    actionsPanel.add(createButton("List Products", this::listProducts));
    actionsPanel.add(createButton("View Product Waitlist", this::viewWaitlist));
    actionsPanel.add(createButton("Receive Shipment (optional)", this::receiveShipment));
    actionsPanel.add(createButton("Become Clerk", () -> controller.changeTo(WarehouseContext.CLERK_STATE)));
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
    outputArea.setText("Manager tools ready.");
  }

  private void addProduct() {
    JTextField codeField = new JTextField();
    JTextField titleField = new JTextField();
    JTextField priceField = new JTextField();
    JTextField qtyField = new JTextField();
    JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
    form.add(new JLabel("Code (label):"));
    form.add(codeField);
    form.add(new JLabel("Title:"));
    form.add(titleField);
    form.add(new JLabel("Price:"));
    form.add(priceField);
    form.add(new JLabel("Quantity:"));
    form.add(qtyField);

    int result = JOptionPane.showConfirmDialog(this, form, "Add Product", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    String title = titleField.getText().trim();
    String priceText = priceField.getText().trim();
    String qtyText = qtyField.getText().trim();
    if (title.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()) {
      warn("All fields except code are required.");
      return;
    }

    double price;
    int qty;
    try {
      price = Double.parseDouble(priceText);
      qty = Integer.parseInt(qtyText);
    } catch (NumberFormatException ex) {
      warn("Enter numeric values for price and quantity.");
      return;
    }
    if (qty < 0 || price < 0) {
      warn("Price and quantity must be positive.");
      return;
    }

    try {
      String id = controller.getContext().warehouse().addProduct(title, qty, price);
      String label = codeField.getText().trim();
      outputArea.setText("Product added: " + id + " ("
          + (label.isEmpty() ? title : label + " / " + title) + ")");
    } catch (Exception ex) {
      warn("Failed to add product: " + ex.getMessage());
    }
  }

  private void listProducts() {
    Collection<Product> products = controller.getContext().warehouse().getAllProducts();
    if (products.isEmpty()) {
      outputArea.setText("No products available.");
      return;
    }
    StringBuilder sb = new StringBuilder("== Products ==\n");
    for (Product p : products) sb.append(p).append('\n');
    outputArea.setText(sb.toString());
  }

  private void receiveShipment() {
    JTextField productField = new JTextField();
    JTextField qtyField = new JTextField();
    JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
    form.add(new JLabel("Product ID:"));
    form.add(productField);
    form.add(new JLabel("Quantity:"));
    form.add(qtyField);

    int result = JOptionPane.showConfirmDialog(this, form, "Receive Shipment", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    String pid = productField.getText().trim();
    String qtyText = qtyField.getText().trim();
    if (pid.isEmpty() || qtyText.isEmpty()) {
      warn("Both fields are required.");
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

    try {
      controller.getContext().warehouse().receiveShipment(pid, qty);
      outputArea.setText("Shipment processed for " + pid + " (+ " + qty + ")");
    } catch (Exception ex) {
      warn("Failed: " + ex.getMessage());
    }
  }

  private void warn(String message) {
    JOptionPane.showMessageDialog(this, message, "Manager", JOptionPane.WARNING_MESSAGE);
  }

  private void viewWaitlist() {
    String pid = JOptionPane.showInputDialog(this, "Product ID:", "View Waitlist", JOptionPane.PLAIN_MESSAGE);
    if (pid == null || pid.trim().isEmpty()) return;
    pid = pid.trim();
    java.util.List<String> rows;
    try {
      rows = controller.getContext().warehouse().getWaitlistView(pid);
    } catch (Exception ex) {
      warn("Failed: " + ex.getMessage());
      return;
    }
    if (rows.isEmpty()) {
      outputArea.setText("Waitlist empty for " + pid + ".");
      return;
    }
    StringBuilder sb = new StringBuilder("== Waitlist for ").append(pid).append(" ==\n");
    for (String r : rows) sb.append(r).append('\n');
    outputArea.setText(sb.toString());
  }

  @Override
  public String getName() {
    return "Manager";
  }
}
