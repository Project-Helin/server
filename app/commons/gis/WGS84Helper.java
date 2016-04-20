package commons.gis;

public class WGS84Helper {

    private final static int EARTH_RADIUS = 6378; // 6378137m ist der Radius gem. WGS84 Standart

    //assert that coordinates are in boundary of the world!
    private static void assertLatCoordinate(double lat) throws WGS84CoordinateException {
        if(lat < -90.0000 || lat > 90.0000){
            throw new WGS84CoordinateException("Lat Coordinate out of bounds: " + lat);
        }
    }

    private static void assertLonCoordinate(double lon) throws WGS84CoordinateException {
        if(lon < -180.000 || lon > 180.000){
            throw new WGS84CoordinateException("Lon Coordinates out of bounds: " + lon);
        }
    }

    // Haversin Formula
    // Approx! -> Berlin to Tokio is 0.5% off to the real distance!
    public static double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) throws WGS84CoordinateException {
        assertLatCoordinate(lat1);
        assertLatCoordinate(lat2);
        assertLonCoordinate(lon1);
        assertLonCoordinate(lon2);

        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RADIUS * c; // Distance in km
        return d;
    }

    // This function is approx! - ACCURACY IS LIMITED!
    public static double getDistanceFromLatInKM(double lat1, double lat2) throws WGS84CoordinateException {
        assertLatCoordinate(lat1);
        assertLatCoordinate(lat2);

        double dLat = deg2rad(Math.abs(lat2 - lat1));
        return dLat * EARTH_RADIUS;
    }

    // This function is approx! - ACCURACY IS LIMITED!
    public static double getDistanceFromLonInKM(double lat1, double lon1, double lat2, double lon2) throws WGS84CoordinateException {
        assertLatCoordinate(lat1);
        assertLatCoordinate(lat2);
        assertLonCoordinate(lon1);
        assertLonCoordinate(lon2);

        double dLon = deg2rad(Math.abs(lon2 - lon1));
        double midLat = deg2rad(Math.abs(lat1 + lat2) / 2);

        //System.out.println("Lon: " + dLon);
        //System.out.println("midLat:" + midLat);

        return dLon * Math.cos(midLat) * EARTH_RADIUS;
    }

    public static double getDistanceFromLatLonInM(double lat1, double lon1, double lat2, double lon2) throws WGS84CoordinateException {
        return getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) * 1000;
    }

    // This function is approx! - ACCURACY IS LIMITED!
    public static double getDistanceFromLatInM(double lat1, double lat2) throws WGS84CoordinateException {


        return getDistanceFromLatInKM(lat1, lat2) * 1000;
    }

    // This function is approx! - ACCURACY IS LIMITED!
    public static double getDistanceFromLonInM(double lat1, double lon1, double lat2, double lon2) throws WGS84CoordinateException {
        return getDistanceFromLonInKM(lat1, lon1, lat2, lon2) * 1000;
    }

    private static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public static void calculateDegreeFromMeter(double meter) {

    }
}