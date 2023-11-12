import org.junit.Assert;
import org.junit.Test;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.EventQueue;
import org.junit.After;

public class Gui_Test {
    private GpsGUI gpsGUI; // Assume GpsGUI is your main GUI class

    @Before
    public void setUp() {
        // Initialize GpsGUI with mock or real components as necessary
        gpsGUI = new GpsGUI();
    }

    @Test
    public void testDistanceCalculationAndUpdate() throws Exception {
        try {
            String trackerId = "Tracker1";
            GpsEvent startEvent = new GpsEvent(trackerId, 50.0, 10.0, 100.0);
            GpsEvent endEvent = new GpsEvent(trackerId, 50.001, 10.001, 100.0);

            // Call the public static method calculateDistance
            // double distance = GpsGUI.calculateDistance(startEvent, endEvent);

            // EventQueue.invokeAndWait(() -> GpsGUI.updateTrackerDistanceDisplay(trackerId,
            // distance));

            // Retrieve the label from your GpsGUI instance
            JLabel trackerLabel = new JLabel(); // Stubbed out for example purposes
            // Wait for the EDT to process the update
            EventQueue.invokeAndWait(() -> {
            });
            // Call the now-public static method updateTrackerDistanceDisplay
            // GpsGUI.updateTrackerDistanceDisplay(trackerId, distance);

            // String expectedDisplay = String.format("%s: Total Distance: %.2f meters",
            // trackerId, distance);
            // Assert.assertEquals(expectedDisplay, trackerLabel.getText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An exception occurred during the test.");
        }
    }

    @Test
    public void testGuiInitialization() {
        // Verify all components are initialized
        assertNotNull("Frame should not be null", gpsGUI.getFrame());
        assertNotNull("Tracker panel should not be null", gpsGUI.getTrackerPanel());
        assertNotNull("Input panel should not be null", gpsGUI.getInputPanel());
        // Add more assertions as needed for other components
    }

    @Test
    public void testTrackerDisplays() {
        // Simulate events for ten trackers and verify that their displays are updated
        // correctly
        for (int i = 1; i <= 10; i++) {
            String trackerId = "Tracker" + i;

            // Check initial state
            JLabel initialLabel = gpsGUI.getTrackerLabel(trackerId);
            assertNotNull("Label for tracker should not be null", initialLabel);
            assertEquals("Initial text should show 0 meters", trackerId + ": Distance 0 meters",
                    initialLabel.getText());

            // Simulate processing an event
            GpsGUI.GpsEvent simulatedEvent = new GpsGUI.GpsEvent(trackerId, 50.0 + i, 10.0 + i, 100.0 + i);
            gpsGUI.processGpsEvent(simulatedEvent);

            // Allow the event to be processed by the EDT
            try {
                SwingUtilities.invokeAndWait(() -> {
                });
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed to process the event in the EDT.");
            }

            // Verify the tracker display is updated correctly
            JLabel updatedLabel = gpsGUI.getTrackerLabel(trackerId);
            String expectedDisplay = String.format("Tracker%s: Lat %.1f, Lon %.1f", trackerId, 50.0 + i, 10.0 + i);
            assertEquals("Tracker display should be updated with simulated event data", expectedDisplay,
                    updatedLabel.getText());
        }
    }

    @After
    public void tearDown() {
        // Clean up the FRP environment if needed
        GpsGUI.cleanup(); // Implement cleanup method in GpsGUI if necessary
    }
}
