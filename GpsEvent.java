public class GpsEvent {

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

    // @Override
    // public String toString() {
    // return trackerId + " | lat: " + latitude + " lon: " + longitude + " alt: " +
    // altitude;
    // }
}
