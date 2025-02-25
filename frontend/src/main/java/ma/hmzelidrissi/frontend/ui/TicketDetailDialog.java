package ma.hmzelidrissi.frontend.ui;

import lombok.Getter;
import ma.hmzelidrissi.frontend.api.TicketApi;
import ma.hmzelidrissi.frontend.dto.AddCommentRequest;
import ma.hmzelidrissi.frontend.dto.UpdateStatusRequest;
import ma.hmzelidrissi.frontend.model.*;
import ma.hmzelidrissi.frontend.utils.UIUtils;
import net.miginfocom.swing.MigLayout;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TicketDetailDialog extends JDialog {
    private final Ticket ticket;
    private final boolean isITSupport;
    private final TicketApi ticketApi = new TicketApi();
    @Getter
    private boolean ticketUpdated = false;

    private final JTextArea commentTextArea = new JTextArea(4, 30);
    private final JPanel commentsPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow]"));

    public TicketDetailDialog(Frame owner, Ticket ticket, boolean isITSupport) {
        super(owner, "Ticket Details: #" + ticket.getId(), true);
        this.ticket = ticket;
        this.isITSupport = isITSupport;

        setSize(700, 800);
        setLocationRelativeTo(owner);

        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]"));

        // Ticket information panel
        createTicketInfoPanel(mainPanel);

        // Ticket comments section
        createCommentsSection(mainPanel);

        // IT Support actions panel
        if (isITSupport) {
            createITSupportPanel(mainPanel);
        }

        // Bottom buttons
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(closeButton, "tag right, gaptop 20");

        add(mainPanel);

        // Load comments
        loadComments();
    }

    private void createTicketInfoPanel(JPanel mainPanel) {
        JPanel infoPanel = new JPanel(new MigLayout("fillx, insets 10", "[][grow]"));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Ticket Information"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Add ticket details
        addInfoField(infoPanel, "ID:", ticket.getId().toString());
        addInfoField(infoPanel, "Title:", ticket.getTitle());
        addInfoField(infoPanel, "Status:", ticket.getStatus().toString());
        addInfoField(infoPanel, "Priority:", ticket.getPriority().toString());
        addInfoField(infoPanel, "Category:", ticket.getCategory().toString());
        addInfoField(infoPanel, "Created:", ticket.getCreationDate().format(formatter));
        addInfoField(infoPanel, "Created By:", ticket.getCreatedByName());

        // Description
        infoPanel.add(new JLabel("Description:"), "cell 0 7");

        JTextArea descriptionArea = new JTextArea(ticket.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(infoPanel.getBackground());
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(300, 100));
        infoPanel.add(descriptionScroll, "cell 1 7, growx, gapbottom 10");

        mainPanel.add(infoPanel, "growx, wrap");
    }

    private void addInfoField(JPanel panel, String label, String value) {
        panel.add(new JLabel(label), "");
        panel.add(new JLabel(value), "growx, wrap");
    }

    private void createCommentsSection(JPanel mainPanel) {
        JPanel commentsContainer = new JPanel(new MigLayout("fillx, insets 0", "[grow]"));
        commentsContainer.setBorder(BorderFactory.createTitledBorder("Comments"));

        // Scrollable comments list
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        JScrollPane commentsScroll = new JScrollPane(commentsPanel);
        commentsScroll.setPreferredSize(new Dimension(650, 150));
        commentsContainer.add(commentsScroll, "growx, wrap, gapbottom 10");

        // Add comment section (only for IT Support)
        if (isITSupport) {
            JPanel addCommentPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]"));

            commentTextArea.setLineWrap(true);
            commentTextArea.setWrapStyleWord(true);
            JScrollPane commentScroll = new JScrollPane(commentTextArea);
            addCommentPanel.add(commentScroll, "growx");

            JButton addButton = new JButton("Add Comment");
            addButton.addActionListener(e -> addComment());
            addCommentPanel.add(addButton, "");

            commentsContainer.add(addCommentPanel, "growx");
        }

        mainPanel.add(commentsContainer, "growx, wrap, gaptop 20");
    }

    private void createITSupportPanel(JPanel mainPanel) {
        JPanel actionsPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow]"));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("IT Support Actions"));

        JLabel statusLabel = new JLabel("Change Status:");
        actionsPanel.add(statusLabel, "");

        // Only show status options that make sense for current status
        JPanel statusButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        if (ticket.getStatus() == Status.NEW) {
            JButton startButton = new JButton("Start Working");
            startButton.addActionListener(e -> updateTicketStatus(Status.IN_PROGRESS));
            statusButtonsPanel.add(startButton);
        }

        if (ticket.getStatus() == Status.IN_PROGRESS) {
            JButton resolveButton = new JButton("Mark as Resolved");
            resolveButton.addActionListener(e -> updateTicketStatus(Status.RESOLVED));
            statusButtonsPanel.add(resolveButton);
        }

        if (ticket.getStatus() == Status.RESOLVED) {
            JButton reopenButton = new JButton("Reopen Ticket");
            reopenButton.addActionListener(e -> updateTicketStatus(Status.IN_PROGRESS));
            statusButtonsPanel.add(reopenButton);
        }

        actionsPanel.add(statusButtonsPanel, "growx, wrap");

        mainPanel.add(actionsPanel, "growx, wrap, gaptop 20");
    }

    private void loadComments() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            List<Comment> comments = ticketApi.getTicketComments(ticket.getId());

            // Clear existing comments
            commentsPanel.removeAll();

            if (comments.isEmpty()) {
                JLabel noCommentsLabel = new JLabel("No comments yet");
                noCommentsLabel.setForeground(Color.GRAY);
                noCommentsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                commentsPanel.add(noCommentsLabel);
            } else {
                // Add comments to panel
                for (Comment comment : comments) {
                    addCommentToPanel(comment);
                }
            }

            // Refresh panel
            commentsPanel.revalidate();
            commentsPanel.repaint();

        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error loading comments: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void addCommentToPanel(Comment comment) {
        JPanel commentPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow]"));
        commentPanel.setBorder(BorderFactory.createEtchedBorder());
        commentPanel.setBackground(new Color(250, 250, 250));

        // Header with user and date
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]"));
        headerPanel.setOpaque(false);

        JLabel userLabel = new JLabel(comment.getCreatedBy());
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(userLabel, "");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JLabel dateLabel = new JLabel(comment.getCreatedAt().format(formatter));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);
        headerPanel.add(dateLabel, "right");

        commentPanel.add(headerPanel, "growx, wrap");

        // Comment content
        JTextArea contentArea = new JTextArea(comment.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setBackground(commentPanel.getBackground());
        contentArea.setBorder(null);

        commentPanel.add(contentArea, "growx");

        // Add to comments panel
        commentsPanel.add(commentPanel);
        commentsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addComment() {
        String content = commentTextArea.getText().trim();

        if (content.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please enter a comment");
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            AddCommentRequest request = new AddCommentRequest(content);
            Comment comment = ticketApi.addComment(ticket.getId(), request);

            if (comment != null) {
                // Clear comment text area
                commentTextArea.setText("");

                // Add new comment to panel
                commentsPanel.removeAll();
                loadComments();

                // Set ticket as updated
                ticketUpdated = true;
            }

        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error adding comment: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateTicketStatus(Status newStatus) {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            UpdateStatusRequest request = new UpdateStatusRequest(newStatus);
            boolean success = ticketApi.updateTicketStatus(ticket.getId(), request);

            if (success) {
                UIUtils.showInfoMessage(this, "Ticket status updated to " + newStatus);

                // Set ticket as updated
                ticketUpdated = true;

                // Close dialog
                dispose();
            }

        } catch (Exception e) {
            UIUtils.showErrorMessage(this, "Error updating ticket status: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}