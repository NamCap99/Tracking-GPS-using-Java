import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import swidgets.SLabel;  // Import your custom SLabel
import nz.sodium.Cell;   // Import from Sodium library

public class GpsTrackerGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GPS Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create the tracker panel
        JPanel trackerPanel = new JPanel(new GridLayout(0, 3)); // Adjust layout as needed
        frame.add(trackerPanel, BorderLayout.NORTH);

        // Simulate tracker data
        Cell<String> trackerData = new Cell<>("Tracker Data Here");

        // Create and add an SLabel to the panel
        SLabel trackerLabel = new SLabel(trackerData);
        trackerPanel.add(trackerLabel);

        // TODO: Add more components and logic

        frame.pack();
        frame.setVisible(true);
    }
}
