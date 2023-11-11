import nz.sodium.*;
import swidgets.*;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GpsTrackerGUI {

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

        public String getTrackerId() { return trackerId; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getAltitude() { return altitude; }
    }

    
    private static Cell<String> simulateTrackerData(String trackerId) {
        // Simulate GPS data for a tracker (latitude, longitude, altitude)
        Random rand = new Random();
        double latitude = -90 + 180 * rand.nextDouble();  // Random latitude
        double longitude = -180 + 360 * rand.nextDouble(); // Random longitude
        double altitude = 1000 * rand.nextDouble(); // Random altitude
    
        // Create a GpsEvent and format it as a string
        GpsEvent event = new GpsEvent(trackerId, latitude, longitude, altitude);
        String dataString = "Tracker " + event.getTrackerId() +
                            ": Lat " + event.getLatitude() +
                            ", Lon " + event.getLongitude() +
                            ", Alt " + event.getAltitude();
    
        // Create a CellSink and immediately send the data string to it
        CellSink<String> trackerDataCell = new CellSink<>(dataString);
    
        return trackerDataCell;
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GPS Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel for tracker data
        JPanel trackerPanel = new JPanel(new GridLayout(0, 1));
        frame.add(trackerPanel, BorderLayout.NORTH);

        // Simulate and display data for multiple trackers
        int numberOfTrackers = 5; // Example number of trackers
        for (int i = 0; i < numberOfTrackers; i++) {
            Cell<String> trackerData = simulateTrackerData("Tracker " + (i + 1));
            SLabel trackerLabel = new SLabel(trackerData);
            trackerPanel.add(trackerLabel);
        }

        // Simulate tracker data
        Cell<String> trackerData = new Cell<>("Tracker 1: 0.0, 0.0, 0.0"); // Example tracker data
        SLabel trackerLabel = new SLabel(trackerData);
        trackerPanel.add(trackerLabel);

        // Input panel for latitude and longitude
        JPanel inputPanel = new JPanel(new FlowLayout());
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
        frame.add(combinedDataDisplay, BorderLayout.SOUTH);

        // Apply filter logic on button click
        applyButton.sClicked.listen(ignored -> {
            double lat = Double.parseDouble(latitudeField.text.sample());
            double lon = Double.parseDouble(longitudeField.text.sample());

            // Filter logic (assuming a method to get GPS event stream)
            // Stream<GpsEvent> filteredStream = getFilteredGpsEventStream(lat, lon);

            // Update display (assuming a method to format GPS event data)
            // Cell<String> displayData = getDisplayDataFromStream(filteredStream);
            // combinedDataDisplay.setText(displayData.sample());
        });

        frame.pack();
        frame.setVisible(true);
    }

    // Placeholder method to get filtered GPS event stream
    private static Stream<GpsEvent> getFilteredGpsEventStream(double latitude, double longitude) {
        // Implement actual filtering logic here
        return new Stream<>();
    }

    // Placeholder method to format GPS event data for display
    private static Cell<String> getDisplayDataFromStream(Stream<GpsEvent> stream) {
        // Implement logic to convert stream data to string for display
        return new Cell<>("");
    }
}
