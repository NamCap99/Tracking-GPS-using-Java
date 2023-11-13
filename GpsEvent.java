public class GpsEvent {
    private String trackerId;
    private double latitude;
    private double longitude;
    private double altitude;

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
