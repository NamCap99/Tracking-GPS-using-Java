import nz.sodium.*;
import swidgets.*;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;

public class GpsGUI {
    private static Map<String, JLabel> trackerLabels = new HashMap<>();
    private static Map<String, JLabel> trackerDistanceLabels = new HashMap<>();
    private static Map<String, GpsEvent> lastKnownPositions = new HashMap<>();
    private static Map<String, Double> trackerDistances = new HashMap<>();

    private static final double LATITUDE_THRESHOLD = 0.01; // Example threshold value
    private static final double LONGITUDE_THRESHOLD = 0.01; // Example threshold value
    // Initialize event display label in static context
    private static JLabel eventDisplayLabel = new JLabel("No data");

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
    }

    private static Stream<GpsEvent> combineAllTrackerStreams() {
        StreamSink<GpsEvent> allEventsSink = new StreamSink<>();
        Timer timer = new Timer(true); // true to make it a daemon thread
        timer.schedule(new TimerTask() {
            int counter = 0;

            public void run() {
                // Simulating a new event for a different tracker
                String trackerId = "Tracker " + (counter % 10 + 1);
                Cell<GpsEvent> simulatedData = simulateTrackerData(trackerId);
                allEventsSink.send(simulatedData.sample());
                counter++;
            }
        }, 0, 1000); // Emit an event every second
        return allEventsSink;
    }

    // // Assuming you have a method to update a tracker's label
    private static void updateTrackerLabel(String trackerId, String data) {
        // Retrieve the label for the tracker and update its text
        JLabel label = trackerLabels.get(trackerId); // trackerLabels is a Map<String, JLabel>
        if (label != null) {
            label.setText(data);
        }
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

    // Call this method whenever a tracker's data is updated

    private static Cell<GpsEvent> simulateTrackerData(String trackerId) {
        Random rand = new Random();

        // Generate latitude and longitude with 8 decimal places
        double latitude = -90 + 180 * rand.nextDouble(); // Range: -90 to 90
        latitude = Math.round(latitude * 1e8) / 1e8; // Round to 8 decimal places

        double longitude = -180 + 360 * rand.nextDouble(); // Range: -180 to 180
        longitude = Math.round(longitude * 1e8) / 1e8; // Round to 8 decimal places

        double altitude = 1000 * rand.nextDouble(); // Random altitude

        GpsEvent event = new GpsEvent(trackerId, latitude, longitude, altitude);

        // Create a CellSink and set its value to the generated event
        CellSink<GpsEvent> trackerDataCell = new CellSink<>(event);
        return trackerDataCell;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
            simulateTestCases();
            setupPeriodicTasks();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GPS Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize event display label and add to frame
        JLabel eventDisplayLabel = new JLabel("No data");
        frame.add(eventDisplayLabel, BorderLayout.PAGE_START);

        // Panel for tracker data
        JPanel trackerPanel = new JPanel(new GridLayout(0, 1));
        trackerPanel.setBorder(BorderFactory.createTitledBorder("Trackers"));
        frame.add(trackerPanel, BorderLayout.NORTH);

        // Simulate and display data for multiple trackers
        // Simulate and display data for multiple trackers
        int numberOfTrackers = 10; // Example number of trackers
        // Inside createAndShowGUI method
        for (int i = 0; i < numberOfTrackers; i++) {
            String trackerId = "Tracker " + (i + 1);
            Cell<GpsEvent> trackerData = simulateTrackerData(trackerId);

            // Map the GpsEvent data to a String format, excluding altitude
            Cell<String> displayData = trackerData.map(event -> "Tracker " + event.getTrackerId() + ": Lat "
                    + event.getLatitude() + ", Lon " + event.getLongitude());

            SLabel trackerLabel = new SLabel(displayData);
            trackerPanel.add(trackerLabel);

            // Create a label for displaying distance and add it to the map
            JLabel distanceLabel = new JLabel(trackerId + ": Distance 0 meters");
            trackerDistanceLabels.put(trackerId, distanceLabel);
            trackerPanel.add(distanceLabel); // Add the distance label to the panel
        }

        // Simulate tracker data
        Cell<String> trackerData = new Cell<>("Tracker 1: 0.0, 0.0, 0.0"); // Example tracker data
        SLabel trackerLabel = new SLabel(trackerData);
        trackerPanel.add(trackerLabel);

        // Input panel for latitude and longitude
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Filter Controls"));
        STextField latitudeField = new STextField("Latitude");
        STextField longitudeField = new STextField("Longitude");
        SButton applyButton = new SButton("Apply Filter");
        inputPanel.add(new JLabel("Latitude:"));
        inputPanel.add(latitudeField);
        inputPanel.add(new JLabel("Longitude:"));
        inputPanel.add(longitudeField);
        inputPanel.add(applyButton);
        frame.add(inputPanel, BorderLayout.CENTER);

        // Display for combined GPS data
        STextArea combinedDataDisplay = new STextArea("");
        combinedDataDisplay.setBorder(BorderFactory.createTitledBorder("Combined Data"));
        frame.add(combinedDataDisplay, BorderLayout.SOUTH);

        JLabel filterStatusLabel = new JLabel("Current filter: None");
        inputPanel.add(filterStatusLabel);

        // Apply filter logic on button click
        applyButton.sClicked.listen(ignored -> {
            String latStr = latitudeField.text.sample().trim();
            String lonStr = longitudeField.text.sample().trim();
            try {
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);

                // Validate latitude and longitude values
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    throw new IllegalArgumentException(
                            "Latitude must be between -90 and 90 and longitude between -180 and 180.");
                }
                filterStatusLabel.setText("Current filter: Lat " + lat + ", Lon " + lon);

                Stream<GpsEvent> allEventsStream = combineAllTrackerStreams();
                Stream<GpsEvent> filteredStream = allEventsStream
                        .filter(event -> Math.abs(event.getLatitude() - lat) < LATITUDE_THRESHOLD &&
                                Math.abs(event.getLongitude() - lon) < LONGITUDE_THRESHOLD);

                filteredStream
                        .map(event -> "Tracker " + event.getTrackerId() + ": Lat " + event.getLatitude() + ", Lon "
                                + event.getLongitude())
                        .hold("No data")
                        .listen(filteredData -> combinedDataDisplay.setText(filteredData));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input for latitude or longitude.");
                filterStatusLabel.setText("Current filter: Invalid");
            }
        });

        frame.pack();
        frame.setSize(600, 600);
        frame.setVisible(true);
    }

    // Add a method to update this label
    private static void updateEventDisplay(String data) {
        eventDisplayLabel.setText(data);
        // Set up a timer to clear the label after 3 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> eventDisplayLabel.setText("No data"));
            }
        }, 3000);
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

    private static double calculateDistance(GpsEvent lastEvent, GpsEvent currentEvent) {
        final int R = 6371; // Radius of the Earth in kilometers
    
        double latDistance = Math.toRadians(currentEvent.getLatitude() - lastEvent.getLatitude());
        double lonDistance = Math.toRadians(currentEvent.getLongitude() - lastEvent.getLongitude());
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(Math.toRadians(lastEvent.getLatitude())) * Math.cos(Math.toRadians(currentEvent.getLatitude())) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
        double distance = R * c; // convert to meters
        distance *= 1000; // convert to meters
    
        return Math.round(distance);
    }
    

    // Example of processing a new GPS event
    public static void processGpsEvent(GpsEvent newEvent) {
        SwingUtilities.invokeLater(() -> {
            String trackerId = newEvent.getTrackerId();
            double newDistance = 0.0;
            // Inside the processGpsEvent method
            if (lastKnownPositions.containsKey(trackerId)) {
                GpsEvent lastEvent = lastKnownPositions.get(trackerId);
                double distanceIncrement = calculateDistance(lastEvent, newEvent);

                // Update the total distance
                double newTotalDistance = trackerDistances.getOrDefault(trackerId, 0.0) + distanceIncrement;
                trackerDistances.put(trackerId, newTotalDistance);

                // Update the distance display for the tracker
                updateTrackerDistanceDisplay(trackerId, newTotalDistance);
            } else {
                // If there's no last known position, we just put the current event as the last
                // known position
                lastKnownPositions.put(trackerId, newEvent);
            }
            // Update the total distance
            trackerDistances.put(trackerId, trackerDistances.getOrDefault(trackerId, 0.0) + newDistance);
        });
    }

    private static void updateTrackerDistanceDisplay(String trackerId, double newDistance) {
        SwingUtilities.invokeLater(() -> {
            String distanceStr = String.format("Tracker %s: Total Distance: %.2f meters", trackerId, newDistance);
            JLabel distanceLabel = trackerDistanceLabels.get(trackerId);
            if (distanceLabel != null) {
                distanceLabel.setText(distanceStr);
            }
        });
    }
}
