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
import warehouse.WarehouseContext;
import warehouse.ui.GuiController;
import warehouse.ui.StatePanel;

public class OpeningPanel extends JPanel implements StatePanel {
  private final GuiController controller;
  private final JTextArea outputArea;
  private final JPanel actionsPanel;

  public OpeningPanel(GuiController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(10, 10));

    JLabel title = new JLabel("Opening");
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);

    outputArea = new JTextArea();
    outputArea.setEditable(false);
    add(new JScrollPane(outputArea), BorderLayout.CENTER);

    actionsPanel = new JPanel();
    actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
    actionsPanel.add(createButton("Login as Client...", this::loginClient));
    actionsPanel.add(createButton("Create New Client...", this::createClient));
    actionsPanel.add(createButton("Login as Clerk", controller::loginAsClerk));
    actionsPanel.add(createButton("Login as Manager", controller::loginAsManager));
    actionsPanel.add(createButton("Quit", controller::quitApplication));
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
    outputArea.setText("Select a role to continue.");
  }

  @Override
  public String getName() {
    return "Opening";
  }

  private void loginClient() {
    Collection<Client> clients = controller.getContext().warehouse().getAllClients();
    if (clients.isEmpty()) {
      warn("No clients exist yet. Create one first.");
      return;
    }
    java.util.List<String> ids = new ArrayList<>();
    JComboBox<String> combo = new JComboBox<>();
    for (Client c : clients) {
      ids.add(c.getId());
      combo.addItem(c.getId() + " - " + c.getName());
    }
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.add(new JLabel("Select client to login:"), BorderLayout.NORTH);
    panel.add(combo, BorderLayout.CENTER);
    int result = JOptionPane.showConfirmDialog(this, panel, "Login as Client", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;
    String selection = (String) combo.getSelectedItem();
    if (selection == null) return;
    String clientId = ids.get(combo.getSelectedIndex());
    if (!controller.loginAsClient(clientId, WarehouseContext.OPENING_STATE)) {
      warn("Client ID not found: " + clientId);
      return;
    }
    outputArea.setText("Logged in as client " + selection + ".");
  }

  private void createClient() {
    JTextField nameField = new JTextField();
    JTextField addressField = new JTextField();
    JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
    panel.add(new JLabel("Full name:"));
    panel.add(nameField);
    panel.add(new JLabel("Address:"));
    panel.add(addressField);

    int result = JOptionPane.showConfirmDialog(this, panel, "Create Client", JOptionPane.OK_CANCEL_OPTION);
    if (result != JOptionPane.OK_OPTION) return;
    String name = nameField.getText().trim();
    String address = addressField.getText().trim();
    if (name.isEmpty() || address.isEmpty()) {
      warn("Both fields are required.");
      return;
    }
    String clientId;
    try {
      clientId = controller.createClient(name, address);
    } catch (Exception ex) {
      warn("Failed to create client: " + ex.getMessage());
      return;
    }
    outputArea.setText("Created client " + clientId + " (" + name + "). Logging in...");
    controller.loginAsClient(clientId, WarehouseContext.OPENING_STATE);
  }

  private void warn(String message) {
    JOptionPane.showMessageDialog(this, message, "Opening", JOptionPane.WARNING_MESSAGE);
  }
}
