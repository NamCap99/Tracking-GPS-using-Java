import org.junit.Assert;
import org.junit.Test;
import javax.swing.JLabel;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import java.awt.EventQueue;
import org.junit.After;

public class Gui_Test {

    private GpsGUI gpsGUI; // Assume GpsGUI is your main GUI class

    @Before
    public void setUp() {
        // Initialize GpsGUI with mock or real components as necessary
        gpsGUI = new GpsGUI();
        gpsGUI.initializeComponents(); // You need to write this method in GpsGUI
    }

    @Test
    public void testDistanceCalculationAndUpdate() throws Exception {
        try {
            String trackerId = "Tracker1";
            GpsEvent startEvent = new GpsEvent(trackerId, 50.0, 10.0, 100.0);
            GpsEvent endEvent = new GpsEvent(trackerId, 50.001, 10.001, 100.0);

            // Call the public static method calculateDistance
            double distance = GpsGUI.calculateDistance(startEvent, endEvent);

            EventQueue.invokeAndWait(() -> GpsGUI.updateTrackerDistanceDisplay(trackerId, distance));

            // Retrieve the label from your GpsGUI instance
            JLabel trackerLabel = new JLabel(); // Stubbed out for example purposes
            // Wait for the EDT to process the update
            EventQueue.invokeAndWait(() -> {
            });
            // Call the now-public static method updateTrackerDistanceDisplay
            GpsGUI.updateTrackerDistanceDisplay(trackerId, distance);

            String expectedDisplay = String.format("%s: Total Distance: %.2f meters", trackerId, distance);
            Assert.assertEquals(expectedDisplay, trackerLabel.getText());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An exception occurred during the test.");
        }
    }

    // Add more tests for different parts of the GUI...

    @After
    public void tearDown() {
        // Clean up the FRP environment if needed
        gpsGUI.cleanup(); // Implement cleanup method in GpsGUI if necessary
    }
}
