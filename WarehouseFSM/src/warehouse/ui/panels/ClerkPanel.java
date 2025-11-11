package warehouse.ui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import warehouse.Client;
import warehouse.Product;
import warehouse.WarehouseContext;
import warehouse.ui.GuiController;
import warehouse.ui.StatePanel;

public class ClerkPanel extends JPanel implements StatePanel {
  private final GuiController controller;
  private final JTextArea outputArea;
  private final JPanel actionsPanel;

  public ClerkPanel(GuiController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Clerk");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    outputArea = new JTextArea();
    outputArea.setEditable(false);
    add(new JScrollPane(outputArea), BorderLayout.CENTER);

    actionsPanel = new JPanel();
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
    actionsPanel.add(createButton("Add Client", this::addClient));
    actionsPanel.add(createButton("List Products", this::listProducts));
    actionsPanel.add(createButton("List Clients", () -> listClients(false)));
    actionsPanel.add(createButton("List Clients with Balance", () -> listClients(true)));
    actionsPanel.add(createButton("Record Payment", this::recordPayment));
    actionsPanel.add(createButton("Become Client by ID", this::becomeClient));
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
    outputArea.setText("Clerk tools ready.");
  }

  private void addClient() {
    JTextField nameField = new JTextField();
    JTextField addressField = new JTextField();
    JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
    form.add(new JLabel("Full name:"));
    form.add(nameField);
    form.add(new JLabel("Address:"));
    form.add(addressField);

    int result = JOptionPane.showConfirmDialog(this, form, "Add Client", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    String name = nameField.getText().trim();
    String address = addressField.getText().trim();
    if (name.isEmpty() || address.isEmpty()) {
      warn("All fields are required.");
      return;
    }
    try {
      String id = controller.createClient(name, address);
      outputArea.setText("Client added: " + id + " (" + name + ")");
    } catch (Exception ex) {
      warn("Failed to add client: " + ex.getMessage());
    }
  }

  private void listClients(boolean onlyWithBalance) {
    Collection<Client> clients = controller.getContext().warehouse().getAllClients();
    if (clients.isEmpty()) {
      outputArea.setText("No clients found.");
      return;
    }
    StringBuilder sb = new StringBuilder(onlyWithBalance ? "== Clients with Balance ==\n" : "== All Clients ==\n");
    int shown = 0;
    for (Client c : clients) {
      if (!onlyWithBalance || c.getBalance() > 0.0) {
        sb.append(c).append('\n');
        shown++;
      }
    }
    outputArea.setText(shown == 0 ? "No matching clients." : sb.toString());
  }

  private void becomeClient() {
    Collection<Client> clients = controller.getContext().warehouse().getAllClients();
    if (clients.isEmpty()) {
      warn("No clients available.");
      return;
    }
    java.util.List<String> ids = new ArrayList<>();
    JComboBox<String> combo = new JComboBox<>();
    for (Client c : clients) {
      ids.add(c.getId());
      combo.addItem(c.getId() + " - " + c.getName());
    }
    JPanel form = new JPanel(new BorderLayout(5, 5));
    form.add(new JLabel("Select client to become:"), BorderLayout.NORTH);
    form.add(combo, BorderLayout.CENTER);
    int result = JOptionPane.showConfirmDialog(this, form, "Become Client", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;
    String cid = ids.get(combo.getSelectedIndex());
    if (!controller.loginAsClient(cid, WarehouseContext.CLERK_STATE)) {
      warn("Client not found: " + cid);
      return;
    }
    outputArea.setText("Switched to client: " + cid);
  }

  private void listProducts() {
    Collection<Product> products = controller.getContext().warehouse().getAllProducts();
    if (products.isEmpty()) {
      outputArea.setText("No products in catalog.");
      return;
    }
    StringBuilder sb = new StringBuilder("== Products ==\n");
    for (Product p : products) sb.append(p).append('\n');
    outputArea.setText(sb.toString());
  }

  private void recordPayment() {
    JTextField clientField = new JTextField();
    JTextField amountField = new JTextField();
    JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
    form.add(new JLabel("Client ID:"));
    form.add(clientField);
    form.add(new JLabel("Amount:"));
    form.add(amountField);

    int result = JOptionPane.showConfirmDialog(this, form, "Record Payment", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;

    String cid = clientField.getText().trim();
    String amtText = amountField.getText().trim();
    if (cid.isEmpty() || amtText.isEmpty()) {
      warn("Both fields are required.");
      return;
    }
    double amt;
    try {
      amt = Double.parseDouble(amtText);
    } catch (NumberFormatException ex) {
      warn("Enter a valid amount.");
      return;
    }
    try {
      controller.getContext().warehouse().recordPayment(cid, amt);
      outputArea.setText("Payment recorded: " + cid + " paid $" + String.format("%.2f", amt));
    } catch (Exception ex) {
      warn("Failed: " + ex.getMessage());
    }
  }

  private void warn(String message) {
    JOptionPane.showMessageDialog(this, message, "Clerk", JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public String getName() {
    return "Clerk";
  }
}
