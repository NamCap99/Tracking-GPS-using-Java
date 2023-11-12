public class GpsEvent {

    private final String trackerId; // The name of the GPS Tracker
    private final double latitude; // The Latitude of the GPS event
    private final double longitude; // The Longitude of the GPS event
    private final double altitude; // The Altitude of the GPS event in feet

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
        return trackerId + " | lat: " + latitude + " lon: " + longitude + " alt: " + altitude;
    }
}
