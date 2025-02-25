package ma.hmzelidrissi.frontend.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Utility class for UI-related functions
 */
public class UIUtils {
    /**
     * Show an error message dialog
     */
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an information message dialog
     */
    public static void showInfoMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a confirmation dialog
     * @return true if user confirmed, false otherwise
     */
    public static boolean showConfirmDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Generate an avatar icon based on a name
     */
    public static ImageIcon createAvatar(String name, int size) {
        // Generate a consistent color based on the name
        Random random = new Random(name.hashCode());
        Color backgroundColor = new Color(
                random.nextInt(100) + 100,
                random.nextInt(100) + 100,
                random.nextInt(100) + 100);

        // Create a circular avatar with initials
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing for smoother shapes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circle background
        g2d.setColor(backgroundColor);
        g2d.fillOval(0, 0, size, size);

        // Draw initials
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
        FontMetrics metrics = g2d.getFontMetrics();

        // Get initials (up to 2 characters)
        String initials = getInitials(name);

        // Center text
        int x = (size - metrics.stringWidth(initials)) / 2;
        int y = ((size - metrics.getHeight()) / 2) + metrics.getAscent();

        g2d.drawString(initials, x, y);
        g2d.dispose();

        return new ImageIcon(image);
    }

    /**
     * Extract initials from a name (up to 2 characters)
     */
    private static String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "?";
        }

        StringBuilder initials = new StringBuilder();
        String[] parts = name.split("\\s+");

        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }

        // If we only got one initial but the name is long enough, use first two letters
        if (initials.length() == 1 && !parts[0].isEmpty() && parts[0].length() > 1) {
            initials.append(parts[0].charAt(1));
        }

        return initials.toString().toUpperCase();
    }
}