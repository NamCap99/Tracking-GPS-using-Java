import org.junit.Assert;
import org.junit.Test;

import nz.sodium.Cell;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;

public class Gui_Test {
    private GpsGUI gpsGUI; // Assume GpsGUI is your main GUI class

    @Before
    public void setUp() {
        // Initialize GpsGUI with mock or real components as necessary
        GpsGUI.setTestMode(true);
        gpsGUI = new GpsGUI();
    }

    @Test
    public void testDistanceCalculationAndUpdate() {
        String trackerId = "Tracker1";
        GpsEvent startEvent = new GpsEvent(trackerId, 50.0, 10.0, 100.0);
        GpsEvent endEvent = new GpsEvent(trackerId, 50.001, 10.001, 100.0);

        // Call the method to calculate the distance between two events.
        double distance = GpsGUI.calculateDistance(startEvent, endEvent);

        // Call the method to update the tracker's distance display.
        gpsGUI.updateTrackerDistanceDisplay(trackerId, distance);

        // Retrieve the label for this tracker.
        JLabel trackerLabel = gpsGUI.getTrackerLabel(trackerId);

        // Check if the label's text includes the calculated distance.
        String expectedDisplay = String.format("%s: Total Distance: %.2f meters", trackerId, distance);
        assertEquals(expectedDisplay, trackerLabel.getText());
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
    public void testTrackerDisplays() throws InvocationTargetException, InterruptedException {
        for (int i = 1; i <= 10; i++) {
            String trackerId = "Tracker" + i;
            JLabel initialLabel = gpsGUI.getTrackerLabel(trackerId);
            assertNotNull("Label for tracker should not be null", initialLabel);
            assertEquals("Initial text should show 0 meters", trackerId + ": Distance 0 meters",
                    initialLabel.getText());

            GpsEvent simulatedEvent = new GpsEvent(trackerId, 50.0 + i, 10.0 + i, 100.0 + i);
            SwingUtilities.invokeAndWait(() -> gpsGUI.processGpsEvent(simulatedEvent));

            JLabel updatedLabel = gpsGUI.getTrackerLabel(trackerId);
            String expectedDisplay = String.format("Tracker%s: Lat %.1f, Lon %.1f", trackerId, 50.0 + i, 10.0 + i);
            assertEquals("Tracker display should be updated with simulated event data", expectedDisplay,
                    updatedLabel.getText());
        }
    }

    // Simulate the tracker data synchronously for testing purposes.
    private Cell<GpsEvent> simulateTrackerData(String trackerId) {
        // Synchronously generate a GpsEvent with test data.
        double latitude = Math.random() * 180 - 90; // Random latitude between -90 and 90
        double longitude = Math.random() * 360 - 180; // Random longitude between -180 and 180
        double altitude = Math.random() * 1000; // Random altitude up to 1000
        GpsEvent simulatedEvent = new GpsEvent(trackerId, latitude, longitude, altitude);
        return new Cell<>(simulatedEvent);
    }

    @Test
    public void testTrackerDisplayUpdate() {
        for (int i = 1; i <= 10; i++) {
            String trackerId = "Tracker" + i;
            // Directly create a GpsEvent with deterministic data.
            GpsEvent simulatedEvent = new GpsEvent(trackerId, 50.0 + i, 10.0 + i, 100.0 + i);
            
            // Invoke the method that updates the GUI with the simulated event.
            // This method needs to be implemented in GpsGUI.
            gpsGUI.updateTrackerDisplay(simulatedEvent);
            
            // Retrieve the label for this tracker.
            JLabel trackerLabel = gpsGUI.getTrackerLabel(trackerId);
            
            // Check if the tracker label was updated with the simulated data.
            // The format here should match the format used in updateTrackerDisplay.
            String expectedText = String.format("Tracker%s: Lat %.1f, Lon %.1f, Alt %.1f meters",
                                 trackerId, simulatedEvent.getLatitude(), simulatedEvent.getLongitude(),
                                 simulatedEvent.getAltitude());
            assertEquals("Tracker label should display the correct coordinates.",
                         expectedText, trackerLabel.getText());
        }
    }
    
    @After
    public void tearDown() {
        // Clean up the FRP environment if needed
        GpsGUI.cleanup(); // Implement cleanup method in GpsGUI if necessary
        GpsGUI.setTestMode(false); // Reset the test mode.
    }
}
