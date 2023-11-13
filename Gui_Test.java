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
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import org.junit.After;

public class Gui_Test {
    private GpsGUI gpsGUI; // Assume GpsGUI is your main GUI class
    private boolean isHeadless;

    @Before
    public void setUp() {
        isHeadless = GraphicsEnvironment.isHeadless();
        if (!isHeadless) {
            SwingUtilities.invokeLater(() -> {
                gpsGUI = new GpsGUI();
                gpsGUI.initializeComponents();
            });
        } else {
            // Setup for headless mode
            System.setProperty("java.awt.headless", "true");
            gpsGUI = new GpsGUI(); // Create a GpsGUI instance without GUI initialization
            gpsGUI.mockComponents(); // Make sure this method does not instantiate actual GUI components
        }
    }

    // private void runTest(Runnable testLogic) {
    //     if (isHeadless) {
    //         testLogic.run();
    //     } else {
    //         try {
    //             SwingUtilities.invokeAndWait(() -> {
    //                 try {
    //                     testLogic.run();
    //                 } catch (RuntimeException e) {
    //                     throw e; // Re-throw runtime exceptions
    //                 } catch (Exception e) {
    //                     // Handle or wrap other exceptions as runtime exceptions
    //                     throw new RuntimeException(e);
    //                 }
    //             });
    //         } catch (InterruptedException | InvocationTargetException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    private void runTest(Runnable testLogic) {
        if (isHeadless) {
            // Run test logic without involving GUI in headless mode
            testLogic.run();
        } else {
            // Run test logic normally in GUI mode
            try {
                SwingUtilities.invokeAndWait(testLogic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    

    private void runInAppropriateEnvironment(Runnable testLogic) {
        if (GraphicsEnvironment.isHeadless()) {
            System.setProperty("java.awt.headless", "true");
            GpsGUI.setTestMode(true);
            gpsGUI.mockComponents(); // Assuming mockComponents method exists in GpsGUI
        } else {
            GpsGUI.setTestMode(false);
            // Setup for non-headless environment
        }

        testLogic.run();
    }

    @Test
    public void testDistanceCalculationAndUpdate() {
        runTest(() -> {
            String trackerId = "Tracker1";
            GpsGUI.GpsEvent startEvent = new GpsGUI.GpsEvent(trackerId, 50.0, 10.0, 100.0);
            GpsGUI.GpsEvent endEvent = new GpsGUI.GpsEvent(trackerId, 50.001, 10.001, 100.0);
    
            double distance = GpsGUI.calculateDistance(startEvent, endEvent);
            gpsGUI.updateTrackerDistanceDisplay(trackerId, distance);
    
            JLabel trackerLabel = gpsGUI.getTrackerLabel(trackerId);
            String expectedDisplay = String.format("%s: Total Distance: %.2f meters", trackerId, distance);
            assertEquals(expectedDisplay, trackerLabel.getText());
        });
    }
    
    @Test
    public void testGuiInitialization() {
        if (!isHeadless) {
        runTest(() ->{
            runInAppropriateEnvironment(() -> {
                assertNotNull("Frame should not be null", gpsGUI.getFrame());
                assertNotNull("Tracker panel should not be null", gpsGUI.getTrackerPanel());
                assertNotNull("Input panel should not be null", gpsGUI.getInputPanel());
            });
        });
    }
    }
    
    @Test
    public void testTrackerDisplays() {
        if (!isHeadless) {
            runTest(() -> {
                for (int i = 1; i <= 10; i++) {
                    String trackerId = "Tracker" + i;
                    JLabel initialLabel = gpsGUI.getTrackerLabel(trackerId);
                    assertNotNull("Label for tracker should not be null", initialLabel);
                    assertEquals("Initial text should show 0 meters", trackerId + ": Distance 0 meters", initialLabel.getText());
            
                    GpsGUI.GpsEvent simulatedEvent = new GpsGUI.GpsEvent(trackerId, 50.0 + i, 10.0 + i, 100.0 + i);
                    processGpsEventInSwingThread(simulatedEvent);
            
                    JLabel updatedLabel = gpsGUI.getTrackerLabel(trackerId);
                    String expectedDisplay = String.format("Tracker%s: Lat %.1f, Lon %.1f", trackerId, 50.0 + i, 10.0 + i);
                    assertEquals("Tracker display should be updated with simulated event data", expectedDisplay, updatedLabel.getText());
                }
            });
        }
    }
    
    private void processGpsEventInSwingThread(GpsGUI.GpsEvent event) {
        try {
            SwingUtilities.invokeAndWait(() -> gpsGUI.processGpsEvent(event));
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
            // Optionally, handle or rethrow as a runtime exception
        }
    }
    

    @Test
    public void testTrackerDisplayUpdate() {
        if (!isHeadless) {
        runTest(() -> {
            for (int i = 1; i <= 10; i++) {
                String trackerId = "Tracker" + i;
                // Directly create a GpsEvent with deterministic data.
                GpsGUI.GpsEvent simulatedEvent = new GpsGUI.GpsEvent(trackerId, 50.0 + i, 10.0 + i, 100.0 + i);
    
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
        });
    }
    }

    @After
    public void tearDown() {
        // Clean up the FRP environment if needed
        GpsGUI.cleanup(); // Implement cleanup method in GpsGUI if necessary
    }
}
