package ma.hmzelidrissi.frontend.ui;

import lombok.Getter;
import ma.hmzelidrissi.frontend.api.TicketApi;
import ma.hmzelidrissi.frontend.model.Category;
import ma.hmzelidrissi.frontend.model.Priority;
import ma.hmzelidrissi.frontend.dto.CreateTicketRequest;
import ma.hmzelidrissi.frontend.utils.UIUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CreateTicketDialog extends JDialog {
    private final JTextField titleField = new JTextField(20);
    private final JTextArea descriptionArea = new JTextArea(10, 20);
    private final JComboBox<Priority> priorityComboBox = new JComboBox<>(Priority.values());
    private final JComboBox<Category> categoryComboBox = new JComboBox<>(Category.values());

    private final TicketApi ticketApi = new TicketApi();
    @Getter
    private boolean ticketCreated = false;

    public CreateTicketDialog(Frame owner) {
        super(owner, "Create New Ticket", true);
        setSize(500, 600);
        setLocationRelativeTo(owner);

        JPanel mainPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow]"));

        // Title section
        JLabel titleLabel = new JLabel("Create New Support Ticket");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, "wrap, center, gapbottom 20");

        // Title field
        mainPanel.add(new JLabel("Title:"), "wrap");
        mainPanel.add(titleField, "growx, wrap, gapbottom 10");

        // Priority dropdown
        mainPanel.add(new JLabel("Priority:"), "wrap");
        mainPanel.add(priorityComboBox, "growx, wrap, gapbottom 10");

        // Category dropdown
        mainPanel.add(new JLabel("Category:"), "wrap");
        mainPanel.add(categoryComboBox, "growx, wrap, gapbottom 10");

        // Description area
        mainPanel.add(new JLabel("Description:"), "wrap");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, "growx, wrap, gapbottom 20, height 200:250:300");

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JButton submitButton = new JButton("Submit Ticket");
        submitButton.addActionListener(this::handleCreateTicket);

        buttonsPanel.add(cancelButton, "growx");
        buttonsPanel.add(submitButton, "growx");

        mainPanel.add(buttonsPanel, "growx");

        add(mainPanel);
    }

    private void handleCreateTicket(ActionEvent e) {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Priority priority = (Priority) priorityComboBox.getSelectedItem();
        Category category = (Category) categoryComboBox.getSelectedItem();

        // Validate input
        if (title.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please enter a title");
            return;
        }

        if (description.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please enter a description");
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            CreateTicketRequest request = new CreateTicketRequest(
                    title, description, priority, category);

            boolean success = ticketApi.createTicket(request);

            if (success) {
                ticketCreated = true;
                UIUtils.showInfoMessage(this, "Ticket created successfully!");
                dispose();
            }

        } catch (Exception ex) {
            UIUtils.showErrorMessage(this, "Error creating ticket: " + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}