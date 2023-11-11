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

    
    private static Cell<GpsEvent> simulateTrackerData(String trackerId) {
        Random rand = new Random();
    
        // Generate latitude and longitude with 8 decimal places
        double latitude = -90 + 180 * rand.nextDouble();  // Range: -90 to 90
        latitude = Math.round(latitude * 1e8) / 1e8;      // Round to 8 decimal places
    
        double longitude = -180 + 360 * rand.nextDouble(); // Range: -180 to 180
        longitude = Math.round(longitude * 1e8) / 1e8;    // Round to 8 decimal places
    
        double altitude = 1000 * rand.nextDouble(); // Random altitude
    
        GpsEvent event = new GpsEvent(trackerId, latitude, longitude, altitude);
    
        // Create a CellSink and set its value to the generated event
        CellSink<GpsEvent> trackerDataCell = new CellSink<>(event);
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
// Simulate and display data for multiple trackers
int numberOfTrackers = 10; // Example number of trackers
// Inside createAndShowGUI method
for (int i = 0; i < numberOfTrackers; i++) {
    Cell<GpsEvent> trackerData = simulateTrackerData("Tracker " + (i + 1));

    // Map the GpsEvent data to a String format, excluding altitude
    Cell<String> displayData = trackerData.map(event ->
        "Tracker " + event.getTrackerId() + ": Lat " + event.getLatitude() + ", Lon " + event.getLongitude()
    );

    SLabel trackerLabel = new SLabel(displayData);
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

    // ... [Previous code]

// Apply filter logic on button click
// applyButton.sClicked.listen(ignored -> {
//     try {
//         double lat = Double.parseDouble(latitudeField.text.sample());
//         double lon = Double.parseDouble(longitudeField.text.sample());

//         // Validate the latitude and longitude values
//         if (isValidLatitude(lat) && isValidLongitude(lon)) {
//             Stream<GpsEvent> filteredStream = getFilteredGpsEventStream(lat, lon);
//             Cell<String> displayData = getDisplayDataFromStream(filteredStream);
//             combinedDataDisplay.setText(displayData.sample());  // Update display
//         } else {
//             // Handle invalid input values
//             JOptionPane.showMessageDialog(frame, "Invalid latitude or longitude values.");
//         }
//     } catch (NumberFormatException e) {
//         // Handle parsing errors
//         JOptionPane.showMessageDialog(frame, "Error in parsing latitude or longitude.");
//     }
// });


}
