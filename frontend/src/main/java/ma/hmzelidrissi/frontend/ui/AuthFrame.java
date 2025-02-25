package ma.hmzelidrissi.frontend.ui;

import ma.hmzelidrissi.frontend.api.AuthApi;
import ma.hmzelidrissi.frontend.dto.AuthResponse;
import ma.hmzelidrissi.frontend.dto.SigninRequest;
import ma.hmzelidrissi.frontend.dto.SignupRequest;
import ma.hmzelidrissi.frontend.utils.AuthManager;
import ma.hmzelidrissi.frontend.utils.UIUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AuthFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JPanel loginPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow]"));
    private final JPanel registerPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow]"));

    private final JTextField loginEmailField = new JTextField(20);
    private final JPasswordField loginPasswordField = new JPasswordField(20);
    private final JTextField registerNameField = new JTextField(20);
    private final JTextField registerEmailField = new JTextField(20);
    private final JPasswordField registerPasswordField = new JPasswordField(20);

    private final AuthApi authApi = new AuthApi();

    public AuthFrame() {
        setTitle("IT Support Ticket System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);

        setupLoginPanel();
        setupRegisterPanel();

        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        cardLayout.show(mainPanel, "login");

        add(mainPanel);
    }

    private void setupLoginPanel() {
        // Title
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginPanel.add(titleLabel, "wrap, center, gapbottom 20");

        // Email field
        JLabel emailLabel = new JLabel("Email");
        loginPanel.add(emailLabel, "wrap");
        loginPanel.add(loginEmailField, "growx, wrap, gapbottom 10");

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        loginPanel.add(passwordLabel, "wrap");
        loginPanel.add(loginPasswordField, "growx, wrap, gapbottom 20");

        // Login button
        JButton loginButton = new JButton("Sign In");
        loginButton.addActionListener(this::handleLogin);
        loginPanel.add(loginButton, "growx, gapbottom 20, height 40");

        // Register link
        JButton registerLink = new JButton("Don't have an account? Sign up!");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(Color.BLUE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        loginPanel.add(registerLink, "center");
    }

    private void setupRegisterPanel() {
        // Title
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        registerPanel.add(titleLabel, "wrap, center, gapbottom 20");

        // Name field
        JLabel nameLabel = new JLabel("Full Name");
        registerPanel.add(nameLabel, "wrap");
        registerPanel.add(registerNameField, "growx, wrap, gapbottom 10");

        // Email field
        JLabel emailLabel = new JLabel("Email");
        registerPanel.add(emailLabel, "wrap");
        registerPanel.add(registerEmailField, "growx, wrap, gapbottom 10");

        // Password field
        JLabel passwordLabel = new JLabel("Password (min. 6 characters)");
        registerPanel.add(passwordLabel, "wrap");
        registerPanel.add(registerPasswordField, "growx, wrap, gapbottom 20");

        // Register button
        JButton registerButton = new JButton("Sign Up");
        registerButton.addActionListener(this::handleRegistration);
        registerPanel.add(registerButton, "growx, gapbottom 20, height 40");

        // Login link
        JButton loginLink = new JButton("Already have an account? Sign in!");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        registerPanel.add(loginLink, "center");
    }

    private void handleLogin(ActionEvent e) {
        String email = loginEmailField.getText();
        String password = new String(loginPasswordField.getPassword());

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please enter both email and password");
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            SigninRequest request = new SigninRequest(email, password);
            AuthResponse response = authApi.signin(request);

            // Store the authentication token
            AuthManager.getInstance().setAuthToken(response.token());
            AuthManager.getInstance().setCurrentUser(response);

            // Open main application window
            openMainApplication();

        } catch (Exception ex) {
            UIUtils.showErrorMessage(this, "Login failed: " + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void handleRegistration(ActionEvent e) {
        String name = registerNameField.getText();
        String email = registerEmailField.getText();
        String password = new String(registerPasswordField.getPassword());

        // Validate input
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIUtils.showErrorMessage(this, "Please fill in all fields");
            return;
        }

        if (password.length() < 6) {
            UIUtils.showErrorMessage(this, "Password must be at least 6 characters");
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            SignupRequest request = new SignupRequest(name, email, password);
            AuthResponse response = authApi.signup(request);

            // Store the authentication token
            AuthManager.getInstance().setAuthToken(response.token());
            AuthManager.getInstance().setCurrentUser(response);

            // Open main application window
            openMainApplication();

        } catch (Exception ex) {
            UIUtils.showErrorMessage(this, "Registration failed: " + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void openMainApplication() {
        // Close this window
        dispose();

        // Open the main application window
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}