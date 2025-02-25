package ma.hmzelidrissi.frontend.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import ma.hmzelidrissi.frontend.api.TicketApi;
import ma.hmzelidrissi.frontend.model.Status;
import ma.hmzelidrissi.frontend.model.Ticket;
import ma.hmzelidrissi.frontend.utils.AuthManager;
import ma.hmzelidrissi.frontend.utils.UIUtils;
import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {
  private final JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0"));
  private final JPanel sidebarPanel = new JPanel(new MigLayout("fillx, insets 15 10", "[grow]"));
  private final JPanel contentPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]"));

  private final JTable ticketsTable;
  private final DefaultTableModel tableModel;

  private final TicketApi ticketApi = new TicketApi();
  private List<Ticket> currentTickets;

  private final boolean isITSupport;

  public MainFrame() {
    setTitle("IT Support Ticket System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1100, 700);
    setLocationRelativeTo(null);

    // Check user role
    isITSupport = AuthManager.getInstance().getCurrentUser().role().equals("IT_SUPPORT");

    // Setup sidebar with user information and menu
    setupSidebar();

    // Create table model with columns
    String[] columns = {"ID", "Title", "Priority", "Category", "Status", "Created Date"};
    tableModel =
        new DefaultTableModel(columns, 0) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        };
    ticketsTable = new JTable(tableModel);

    ticketsTable.setRowHeight(40);
    ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ticketsTable.setAutoCreateRowSorter(true);
    ticketsTable.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              openTicketDetails();
            }
          }
        });

    setupContentPanel();

    mainPanel.add(sidebarPanel, "width 250::250, grow y");
    mainPanel.add(contentPanel, "grow");

    add(mainPanel);

    loadTickets();
  }

  private void setupSidebar() {
    sidebarPanel.setBackground(new Color(50, 50, 65));

    JPanel profilePanel = new JPanel(new MigLayout("fillx, insets 10", "[center]"));
    profilePanel.setOpaque(false);

    JLabel avatarLabel = new JLabel();
    avatarLabel.setIcon(
        UIUtils.createAvatar(AuthManager.getInstance().getCurrentUser().name(), 64));
    avatarLabel.setForeground(Color.WHITE);
    profilePanel.add(avatarLabel, "wrap");

    // User name
    JLabel nameLabel = new JLabel(AuthManager.getInstance().getCurrentUser().name());
    nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
    nameLabel.setForeground(Color.WHITE);
    profilePanel.add(nameLabel, "wrap");

    JLabel roleLabel = new JLabel(AuthManager.getInstance().getCurrentUser().role());
    roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    roleLabel.setForeground(new Color(200, 200, 200));
    profilePanel.add(roleLabel, "wrap, gapbottom 20");

    sidebarPanel.add(profilePanel, "growx, wrap, gapbottom 20");

    addMenuItem("All Tickets", e -> loadTickets());

    if (isITSupport) {
      addMenuItem("New Tickets", e -> loadTicketsByStatus(Status.NEW));
      addMenuItem("In Progress", e -> loadTicketsByStatus(Status.IN_PROGRESS));
      addMenuItem("Resolved", e -> loadTicketsByStatus(Status.RESOLVED));
    } else {
      addMenuItem("Create Ticket", e -> openNewTicketDialog());
    }

    JButton logoutButton = new JButton("Logout");
    logoutButton.addActionListener(e -> logout());

    sidebarPanel.add(logoutButton, "growx, gaptop 30, bottom");
  }

  private void addMenuItem(String text, java.awt.event.ActionListener listener) {
    JButton menuButton = new JButton(text);
    menuButton.setHorizontalAlignment(SwingConstants.LEFT);
    menuButton.setForeground(Color.WHITE);
    menuButton.setBackground(new Color(60, 60, 75));
    menuButton.setFocusPainted(false);
    menuButton.setBorderPainted(false);
    menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    menuButton.setFont(new Font("Arial", Font.BOLD, 14));
    menuButton.addActionListener(listener);

    sidebarPanel.add(menuButton, "growx, height 40, wrap, gaptop 5");
  }

  private void setupContentPanel() {
    JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 0 0 20 0", "[grow][]"));

    JLabel titleLabel = new JLabel("All Tickets");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    headerPanel.add(titleLabel, "cell 0 0");

    JTextField searchField = new JTextField(15);
    searchField.putClientProperty("JTextField.placeholderText", "Search by ID...");

    JButton searchButton = new JButton("Search");
    searchButton.addActionListener(
        e -> {
          try {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
              Long ticketId = Long.parseLong(query);
              searchTicketById(ticketId);
            } else {
              loadTickets();
            }
          } catch (NumberFormatException ex) {
            UIUtils.showErrorMessage(this, "Please enter a valid ticket ID");
          }
        });

    JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[][]"));
    searchPanel.add(searchField);
    searchPanel.add(searchButton);

    headerPanel.add(searchPanel, "cell 1 0");

    // Add create ticket button if regular employee
    if (!isITSupport) {
      JButton createButton = new JButton("Create New Ticket");
      createButton.addActionListener(e -> openNewTicketDialog());
      headerPanel.add(createButton, "cell 1 1, gaptop 10");
    }

    contentPanel.add(headerPanel, "growx, wrap");

    // Tickets table
    JScrollPane scrollPane = new JScrollPane(ticketsTable);
    contentPanel.add(scrollPane, "grow");
  }

  private void loadTickets() {
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      currentTickets = ticketApi.getAllTickets();

      updateTableWithTickets(currentTickets);

      JLabel titleLabel = (JLabel) ((JPanel) contentPanel.getComponent(0)).getComponent(0);
      titleLabel.setText("All Tickets");

    } catch (Exception e) {
      UIUtils.showErrorMessage(this, "Error loading tickets: " + e.getMessage());
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  private void loadTicketsByStatus(Status status) {
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      currentTickets = ticketApi.getTicketsByStatus(status);
      updateTableWithTickets(currentTickets);

      JLabel titleLabel = (JLabel) ((JPanel) contentPanel.getComponent(0)).getComponent(0);
      titleLabel.setText(status.toString() + " Tickets");

    } catch (Exception e) {
      UIUtils.showErrorMessage(this, "Error loading tickets: " + e.getMessage());
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  private void searchTicketById(Long id) {
    try {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      Ticket ticket = ticketApi.getTicketById(id);
      if (ticket != null) {
        currentTickets = List.of(ticket);
        updateTableWithTickets(currentTickets);

        JLabel titleLabel = (JLabel) ((JPanel) contentPanel.getComponent(0)).getComponent(0);
        titleLabel.setText("Search Results");
      } else {
        UIUtils.showInfoMessage(this, "No ticket found with ID: " + id);
      }

    } catch (Exception e) {
      UIUtils.showErrorMessage(this, "Error searching ticket: " + e.getMessage());
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  private void updateTableWithTickets(List<Ticket> tickets) {
    tableModel.setRowCount(0);

    for (Ticket ticket : tickets) {
      Object[] rowData = {
        ticket.getId(),
        ticket.getTitle(),
        ticket.getPriority(),
        ticket.getCategory(),
        ticket.getStatus(),
        ticket.getCreationDate().toString()
      };
      tableModel.addRow(rowData);
    }
  }

  private void openTicketDetails() {
    int selectedRow = ticketsTable.getSelectedRow();
    if (selectedRow >= 0) {
      int modelRow = ticketsTable.convertRowIndexToModel(selectedRow);
      Long ticketId = (Long) tableModel.getValueAt(modelRow, 0);

      try {
        Ticket ticket =
            currentTickets.stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElse(null);

        if (ticket != null) {
          TicketDetailDialog dialog = new TicketDetailDialog(this, ticket, isITSupport);
          dialog.setVisible(true);

          if (dialog.isTicketUpdated()) {
            loadTickets();
          }
        }
      } catch (Exception e) {
        UIUtils.showErrorMessage(this, "Error opening ticket details: " + e.getMessage());
      }
    }
  }

  private void openNewTicketDialog() {
    CreateTicketDialog dialog = new CreateTicketDialog(this);
    dialog.setVisible(true);

    if (dialog.isTicketCreated()) {
      loadTickets();
    }
  }

  private void logout() {
    // Clear auth token
    AuthManager.getInstance().logout();

    // Close this window and open login window
    dispose();
    SwingUtilities.invokeLater(
        () -> {
          AuthFrame authFrame = new AuthFrame();
          authFrame.setVisible(true);
        });
  }
}
