import nz.sodium.*;
import swidgets.*;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;

public class GpsGUI {
    private static Map<String, JLabel> trackerDistanceLabels = new HashMap<>();
    private static Map<String, GpsEvent> lastKnownPositions = new HashMap<>();
    private static Map<String, Double> trackerDistances = new HashMap<>();
    private static final Map<String, Cell<GpsEvent>> trackerCells = new HashMap<>();
    private static final Map<String, StreamSink<GpsEvent>> trackerStreams = new HashMap<>();
    protected static JFrame frame;
    protected static JPanel trackerPanel;
    protected static JPanel inputPanel;
    private static JLabel eventDisplayLabel;
    private static Cell<Optional<Pair<Double, Double>>> currentFilter = new Cell<>(Optional.empty());

    private static boolean isTestMode = false; // Default to not being in test mode
    private static final double LATITUDE_THRESHOLD = 0.01; // Example threshold value
    private static final double LONGITUDE_THRESHOLD = 0.01; // Example threshold value
    static final int numberOfTrackers = 10; // Example number of trackers
    private static STextArea combinedDataDisplay;
    private static JLabel filterStatusLabel;

    // Assuming you have a class GpsEvent with the required methods
    public static class GpsEvent {
        private final String trackerId;
        private final double latitude;
        private final double longitude;
        private final double altitude;

        public GpsEvent(String trackerId, double latitude, double longitude, double altitude) {
            this.trackerId = trackerId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }

        public String getTrackerId() {
            return trackerId;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getAltitude() {
            return altitude;
        }

        @Override
        public String toString() {
            return String.format("Tracker ID: %s, Latitude: %.6f, Longitude: %.6f, Altitude: %.2f",
                    trackerId, latitude, longitude, altitude);
        }
    }

    // In your GpsGUI constructor or initialization block

    public static void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    public static void processNewData(String trackerId, double latitude, double longitude, double altitude) {
        // Create a new GpsEvent object with the provided details
        GpsEvent event = new GpsEvent(trackerId, latitude, longitude, altitude);

        // Pass the new event to the method that will handle it
        processNewGpsEvent(event);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getTrackerPanel() {
        return trackerPanel;
    }

    public JPanel getInputPanel() {
        return inputPanel;
    }

    private static Stream<GpsEvent> combineAllTrackerStreams() {
        StreamSink<GpsEvent> allEventsSink = new StreamSink<>();
        // Stream<GpsEvent> filteredStream = allEventsStream.filter(event ->
        // currentFilter.sample().map(filter ->
        // Math.abs(event.getLatitude() - filter.getFirst()) < LATITUDE_THRESHOLD &&
        // Math.abs(event.getLongitude() - filter.getSecond()) < LONGITUDE_THRESHOLD
        // ).orElse(true)
        // );
        Timer timer = new Timer(true); // true to make it a daemon thread
        timer.schedule(new TimerTask() {
            int counter = 0;

            public void run() {
                // Simulating a new event for a different tracker
                String trackerId = "Tracker " + (counter % 10 + 1);
                simulateTrackerData(trackerId, allEventsSink); // Pass the stream sink to the method
                counter++;
            }
        }, 0, 1000); // Emit an event every second
        return allEventsSink;
    }

    public static double dmsToDecimal(int degrees, int minutes, double seconds, boolean isNegative) {
        // Convert DMS to decimal degrees
        double decimalDegrees = degrees + minutes / 60.0 + seconds / 3600.0;

        // Round to 8 decimal places
        decimalDegrees = Math.round(decimalDegrees * 1e8) / 1e8;

        // Apply negative sign for South latitudes and West longitudes
        if (isNegative) {
            decimalDegrees = -decimalDegrees;
        }

        return decimalDegrees;
    }

    public static double feetToMeters(double feet) {
        // Conversion factor from feet to meters
        final double conversionFactor = 0.3048;

        // Convert feet to meters and round to the nearest millimeter
        return Math.round(feet * conversionFactor * 1000) / 1000.0;
    }

    private static void simulateTrackerData(String trackerId, StreamSink<GpsEvent> sink) {
        Random rand = new Random();
        Timer timer = new Timer(true); // Create a daemon thread that will not prevent the application from exiting
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // Generate random latitude and longitude with 8 decimal places
                double latitude = -90 + 180 * rand.nextDouble(); // Range: -90 to 90
                double longitude = -180 + 360 * rand.nextDouble(); // Range: -180 to 180
                double altitude = 1000 * rand.nextDouble(); // Random altitude up to 1000 meters

                // Round to 8 decimal places for latitude and longitude
                latitude = Math.round(latitude * 1e8) / 1e8;
                longitude = Math.round(longitude * 1e8) / 1e8;

                // Create a new GpsEvent with randomized data
                GpsEvent event = new GpsEvent(trackerId, latitude, longitude, altitude);

                // Print the generated event for debugging
                System.out.println("Generated event for " + trackerId + ": " + event);

                // Send the new event through the StreamSink
                sink.send(event);
            }
        }, 0, 1000); // Schedule to run every second
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(); // Assemble the GUI components
            setupTrackerStreams(); // Set up the FRP streams for the trackers
            setupPeriodicTasks(); // Set up any periodic tasks or timers

            if (!isTestMode) {
                showGUI(); // Make the GUI visible if not in test mode
            }
        });
    }

    public GpsGUI() {
        // Only initialize components if not in headless mode
        if (!GraphicsEnvironment.isHeadless()) {
            initializeComponents();
            if (!isTestMode) {
                showGUI(); // Only display the GUI if not in test mode
            }
        }
    }

    public void initializeComponents() {
        if (!GraphicsEnvironment.isHeadless()) {
            frame = new JFrame("GPS Tracker");
            eventDisplayLabel = new JLabel("No data");
            currentFilter = new CellLoop<>();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            trackerPanel = createTrackerPanel();
            inputPanel = createInputPanel();
            combinedDataDisplay = createCombinedDataDisplay();

            frame.add(trackerPanel, BorderLayout.NORTH);
            frame.add(inputPanel, BorderLayout.CENTER);
            frame.add(combinedDataDisplay, BorderLayout.SOUTH);

            frame.pack();
            frame.setSize(600, 600);
        }
    }

    private static void showGUI() {
        // Make the frame visible, should be called outside of testing context
        frame.setVisible(true);
    }

    public static void createAndShowGUI() {
        frame = new JFrame("GPS Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        trackerPanel = createTrackerPanel();
        inputPanel = createInputPanel();
        combinedDataDisplay = createCombinedDataDisplay();

        frame.add(trackerPanel, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(combinedDataDisplay, BorderLayout.SOUTH);

        frame.pack();
        frame.setSize(600, 600);
        // frame.setVisible(true);
    }

    public static void setupTrackerStreams() {
        for (int i = 1; i <= numberOfTrackers; i++) {
            String trackerId = "Tracker" + i;
            StreamSink<GpsEvent> streamSink = new StreamSink<>();
            trackerStreams.put(trackerId, streamSink);
            Cell<GpsEvent> cell = streamSink.hold(new GpsEvent(trackerId, 0, 0, 0));
            trackerCells.put(trackerId, cell);

            // Set up the GUI to react to changes in the cell.
            cell.listen(event -> {
                System.out.println("Received update for " + event.getTrackerId() + ": " + event); // Add this line to
                                                                                                  // debug
                SwingUtilities.invokeLater(() -> updateTrackerDisplay(event));
                eventDisplayLabel.setText(event.toString()); // Make sure to implement toString in GpsEvent
                // Clear the event display label after 3 seconds
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> eventDisplayLabel.setText("No data"));
                    }
                }, 3000);
            });
            simulateTrackerData(trackerId, streamSink);
        }
    }

    private static JPanel createTrackerPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Trackers"));

        for (int i = 1; i <= numberOfTrackers; i++) {
            String trackerId = "Tracker" + i;
            // Ensure that trackerCells contains a Cell for each trackerId
            SLabel trackerLabel = createTrackerLabel(trackerCells.get(trackerId));
            panel.add(trackerLabel); // This label should now reflect live data changes
            JLabel distanceLabel = createDistanceLabel(trackerId);
            panel.add(distanceLabel); // This label is for displaying the distance
        }
        return panel;
    }

    private static SLabel createTrackerLabel(Cell<GpsEvent> trackerData) {
        // Ensure that trackerData is not null and is a valid Cell instance
        if (trackerData != null) {
            Cell<String> displayData = trackerData.map(event -> "Tracker " + event.getTrackerId() + ": Lat "
                    + event.getLatitude() + ", Lon " + event.getLongitude());
            return new SLabel(displayData);
        } else {
            // Handle the case where trackerData is null, perhaps by returning a default
            // label or logging an error
            return new SLabel(new Cell<>("No Data"));
        }
    }

    private static JLabel createDistanceLabel(String trackerId) {
        JLabel distanceLabel = new JLabel(trackerId + ": Distance 0 meters");
        trackerDistanceLabels.put(trackerId, distanceLabel);
        return distanceLabel;
    }

    private static JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filter Controls"));

        STextField latitudeField = new STextField("Latitude");
        STextField longitudeField = new STextField("Longitude");
        SButton applyButton = new SButton("Apply Filter");

        panel.add(new JLabel("Latitude:"));
        panel.add(latitudeField);
        panel.add(new JLabel("Longitude:"));
        panel.add(longitudeField);
        panel.add(applyButton);

        filterStatusLabel = new JLabel("Current filter: None");
        panel.add(filterStatusLabel);

        setupFilterButtonListener(latitudeField, longitudeField, applyButton, filterStatusLabel);

        return panel;
    }

    private static STextArea createCombinedDataDisplay() {
        STextArea display = new STextArea("");
        display.setBorder(BorderFactory.createTitledBorder("Combined Data"));
        return display;
    }

    private static void setupFilterButtonListener(STextField latitudeField, STextField longitudeField,
            SButton applyButton, JLabel statusLabel) {
        // Apply filter logic on button click
        applyButton.sClicked.listen(ignored -> {
            String latStr = latitudeField.text.sample().trim();
            String lonStr = longitudeField.text.sample().trim();
            filterStatusLabel.setText("Current filter: Lat " + latStr + ", Lon " + lonStr);
            try {
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);

                // Validate latitude and longitude values
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    JOptionPane.showMessageDialog(frame,
                            "Latitude must be between -90 and 90 and longitude between -180 and 180.");
                    statusLabel.setText("Current filter: Invalid");
                    return; // Exit the method if the input is invalid
                }

                filterStatusLabel.setText("Current filter: Lat " + lat + ", Lon " + lon);
                currentFilter = new Cell<>(Optional.of(new Pair<>(lat, lon)));

                // Filter the stream for events within the specified latitude and longitude
                Stream<GpsEvent> allEventsStream = combineAllTrackerStreams();
                Stream<GpsEvent> filteredStream = allEventsStream
                        .filter(event -> Math.abs(event.getLatitude() - lat) < LATITUDE_THRESHOLD &&
                                Math.abs(event.getLongitude() - lon) < LONGITUDE_THRESHOLD);

                // Update the combined data display with the filtered data
                filteredStream
                        .map(event -> "Tracker " + event.getTrackerId() + ": Lat " + event.getLatitude() + ", Lon "
                                + event.getLongitude())
                        .hold("No data")
                        .listen(filteredData -> combinedDataDisplay.setText(filteredData));
            } catch (NumberFormatException e) {
                // If parsing the double fails, show an error message
                JOptionPane.showMessageDialog(frame, "Invalid input for latitude or longitude.");
                statusLabel.setText("Current filter: Invalid");
            }
        });
    }

    private static void simulateTestCases() {
        // Test cases (latitude, longitude)
        double[][] testCases = {
                { 0.0, 0.0 }, // Equator and Prime Meridian
                { 90.0, 0.0 }, // North Pole
                { -90.0, 0.0 }, // South Pole
                { 0.0, 180.0 }, // International Date Line, Equator
                { 51.477928, -0.001545 }, // Greenwich Observatory
                { 38.897676, -77.036530 }, // The White House, Washington D.C.
                { 48.858844, 2.294351 }, // Eiffel Tower, Paris
                { 35.689487, 139.691706 }, // Tokyo, Japan
                { -22.906847, -43.172896 }, // Rio de Janeiro, Brazil
                { 55.755826, 37.617300 } // Moscow, Russia
        };

        for (double[] testCase : testCases) {
            String trackerId = "TestTracker";
            double latitude = testCase[0];
            double longitude = testCase[1];
            double altitude = 0; // Assume sea level for testing

            // Simulate a delay between receiving each event
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Create a new GPS event with the test data
                    GpsEvent testEvent = new GpsEvent(trackerId, latitude, longitude, altitude);
                    // Process the event as if it were received from the GPS service
                    processGpsEvent(testEvent);
                }
            }, 1000 * Arrays.asList(testCases).indexOf(testCase)); // Delays for each test case
        }
    }

    private static void setupPeriodicTasks() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            // For each tracker, update its distance display
            trackerDistances.forEach((trackerId, distance) -> {
                updateTrackerDistanceDisplay(trackerId, distance);
            });
        }, 0, 5, TimeUnit.MINUTES); // Schedules the task to run every 5 minutes
    }

    public static double calculateDistance(GpsEvent startEvent, GpsEvent endEvent) {
        final int R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(endEvent.getLatitude() - startEvent.getLatitude());
        double lonDistance = Math.toRadians(endEvent.getLongitude() - startEvent.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(startEvent.getLatitude())) * Math.cos(Math.toRadians(endEvent.getLatitude()))
                        *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c; // convert to meters
        distance *= 1000; // convert to meters

        return Math.round(distance);
    }

    // Example of processing a new GPS event
    public static void processGpsEvent(GpsEvent simulatedEvent) {
        SwingUtilities.invokeLater(() -> {
            String trackerId = simulatedEvent.getTrackerId();

            // If there's a last known position, calculate the distance and update it
            if (lastKnownPositions.containsKey(trackerId)) {
                GpsEvent lastEvent = lastKnownPositions.get(trackerId);
                double distanceIncrement = calculateDistance(lastEvent, simulatedEvent);

                // Update the total distance traveled for the tracker
                double newTotalDistance = trackerDistances.getOrDefault(trackerId, 0.0) + distanceIncrement;
                trackerDistances.put(trackerId, newTotalDistance);

                // Update the distance display for the tracker
                updateTrackerDistanceDisplay(trackerId, newTotalDistance);
            } else {
                // If there's no last known position, initialize the distance and set the
                // current event as the last known position
                trackerDistances.put(trackerId, 0.0);
                lastKnownPositions.put(trackerId, simulatedEvent);
            }

            // Update the tracker display label
            updateTrackerDisplay(simulatedEvent);
        });
    }

    public static void updateTrackerDistanceDisplay(String trackerId, double newDistance) {
        SwingUtilities.invokeLater(() -> {
            String distanceStr = String.format("Tracker %s: Total Distance: %.2f meters", trackerId, newDistance);
            JLabel distanceLabel = trackerDistanceLabels.get(trackerId);
            if (distanceLabel != null) {
                distanceLabel.setText(distanceStr);
            }
        });
    }

    public static void processNewGpsEvent(GpsEvent newEvent) {
        StreamSink<GpsEvent> streamSink = trackerStreams.get(newEvent.getTrackerId());
        if (streamSink != null) {
            streamSink.send(newEvent);
        } else {
            // Handle the case where there is no StreamSink for this tracker ID
            System.err.println("No StreamSink found for tracker ID: " + newEvent);
        }
    }

    public static void updateTrackerDisplay(GpsEvent event) {
        SwingUtilities.invokeLater(() -> {
            JLabel trackerLabel = getTrackerLabel(event.getTrackerId());
            if (trackerLabel != null) {
                // Update the label with the latest event data.
                String labelText = String.format("Tracker %s: Lat %.8f, Lon %.8f",
                        event.getTrackerId(), event.getLatitude(), event.getLongitude());
                trackerLabel.setText(labelText);

                // Debug print
                System.out.println("Updating display for " + event.getTrackerId() + ": " + labelText);
            } else {
                // Debug print
                System.out.println("Tracker label not found for " + event.getTrackerId());
            }
        });
    }

    // Method to clean up after tests
    public static void cleanup() {
        // Dispose of the frame
        if (frame != null) {
            frame.dispose();
        }
    }

    // Getter for tracker labels if needed for tests
    public static JLabel getTrackerLabel(String trackerId) {
        return trackerDistanceLabels.get(trackerId);
    }
}
