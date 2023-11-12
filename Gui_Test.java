import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import javax.swing.JLabel;
import org.junit.After;
import org.junit.Assert;

public class Gui_Test {

    private GpsGUI gpsGUI; // Assume GpsGUI is your main GUI class

    @Before
    public void setUp() {
        // Initialize GpsGUI with mock or real components as necessary
        gpsGUI = new GpsGUI();
        gpsGUI.initializeComponents(); // You need to write this method in GpsGUI
    }
    @Test
public void testDistanceCalculationAndUpdate() {
    GpsGUI gpsGUI = new GpsGUI(); // Create an instance of GpsGUI if necessary
    gpsGUI.initializeComponents(); // Initialize the components if necessary

    String trackerId = "Tracker1";
    GpsEvent startEvent = new GpsEvent(trackerId, 50.0, 10.0, 100.0);
    GpsEvent endEvent = new GpsEvent(trackerId, 50.001, 10.001, 100.0);

    // Assuming calculateDistance is a static utility method
    double distance = GpsGUI.calculateDistance(startEvent, endEvent);

    // Update the distance traveled for the tracker
    // Since updateTrackerDistanceDisplay is static, we can call it directly
    GpsGUI.updateTrackerDistanceDisplay(trackerId, distance);

    // Retrieve the label from the GUI and assert the distance is displayed correctly
    JLabel trackerLabel = GpsGUI.getTrackerLabel(trackerId);
    String expectedDisplay = "Tracker " + trackerId + ": Total Distance: " + String.format("%.2f meters", distance);
    Assert.assertEquals(expectedDisplay, trackerLabel.getText());
}

    

    // Add more tests for different parts of the GUI...

    @After
    public void tearDown() {
        // Clean up the FRP environment if needed
        gpsGUI.cleanup(); // Implement cleanup method in GpsGUI if necessary
    }
}
