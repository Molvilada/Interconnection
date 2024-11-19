package utils;

public class DistanceCalculator {
    private static final double EARTH_RADIUS = 6371; // km

    public static float calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double sinLat = Math.sin(dlat / 2);
        double sinLon = Math.sin(dlon / 2);

        double a = sinLat * sinLat + Math.cos(lat1) * Math.cos(lat2) * sinLon * sinLon;
        double c = 2 * Math.asin(Math.sqrt(a));

        return (float) (EARTH_RADIUS * c);
    }
}
