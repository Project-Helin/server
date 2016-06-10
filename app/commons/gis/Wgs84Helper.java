package service.gis;

public class Wgs84Helper {

    // 6378137m is the radius of the world, according to the EPSG:4326 Standart (WGS84)
    public final static int EARTH_RADIUS_IN_KM = 6378;

    //assert that coordinates are in boundary of the world!
    private static void assertLatCoordinate(double lat) throws Wgs84CoordinateException {
        if (lat < -90.0000 || lat > 90.0000) {
            throw new Wgs84CoordinateException("Lat Coordinate out of bounds: " + lat);
        }
    }

    private static void assertLonCoordinate(double lon) throws Wgs84CoordinateException {
        if (lon < -180.000 || lon > 180.000) {
            throw new Wgs84CoordinateException("Lon Coordinates out of bounds: " + lon);
        }
    }

    /**
     * Uses Haversine Formula to calculate the distance from two given LatLon values.
     * This is only an approximation. E.g Berlin to Tokyo is 0.5% off to the real distance!
     */
    public static double getDistanceFromLatLonInKm(double lat1, double lon1,
                                                   double lat2, double lon2) throws Wgs84CoordinateException {
        assertLatCoordinate(lat1);
        assertLatCoordinate(lat2);
        assertLonCoordinate(lon1);
        assertLonCoordinate(lon2);

        double deltaLat = degreeToRad(lat2 - lat1);  // degreeToRad below
        double deltaLon = degreeToRad(lon2 - lon1);

        double cumulatedAngle =
                Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                        Math.cos(degreeToRad(lat1)) * Math.cos(degreeToRad(lat2)) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double segmentPiece = 2 * Math.atan2(Math.sqrt(cumulatedAngle), Math.sqrt(1 - cumulatedAngle));
        double distance = EARTH_RADIUS_IN_KM * segmentPiece; // Distance in km
        return distance;
    }

    /**
     * This function is approx! - ACCURACY IS LIMITED!
     */
    public static double getDistanceFromLatInKm(double lat1, double lat2) throws Wgs84CoordinateException {
        assertLatCoordinate(lat1);
        assertLatCoordinate(lat2);

        double deltaLat = degreeToRad(Math.abs(lat2 - lat1));
        return deltaLat * EARTH_RADIUS_IN_KM;
    }

    /**
     * This function is approx! - ACCURACY IS LIMITED!
     */
    public static double getDistanceFromLonInKm(double lat1, double lon1,
                                                double lat2, double lon2) throws Wgs84CoordinateException {

        assertLatCoordinate(lat1);
        assertLatCoordinate(lat2);
        assertLonCoordinate(lon1);
        assertLonCoordinate(lon2);

        double deltaLon = degreeToRad(Math.abs(lon2 - lon1));
        double midLat = degreeToRad(Math.abs(lat1 + lat2) / 2);

        return deltaLon * Math.cos(midLat) * EARTH_RADIUS_IN_KM;
    }

    public static double getDistanceFromLatLonInM(double lat1, double lon1,
                                                  double lat2, double lon2) throws Wgs84CoordinateException {
        return getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) * 1000;
    }

    /**
     * This function is approx! - ACCURACY IS LIMITED!
     */
    public static double getDistanceFromLatInM(double lat1, double lat2) throws Wgs84CoordinateException {
        return getDistanceFromLatInKm(lat1, lat2) * 1000;
    }

    /**
     * This function is approx! - ACCURACY IS LIMITED!
     */
    public static double getDistanceFromLonInM(double lat1, double lon1,
                                               double lat2, double lon2) throws Wgs84CoordinateException {
        return getDistanceFromLonInKm(lat1, lon1, lat2, lon2) * 1000;
    }

    private static double degreeToRad(double deg) {
        return deg * (Math.PI / 180);
    }
}