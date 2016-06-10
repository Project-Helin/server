package service.gis;

import org.junit.Test;

import static org.junit.Assert.*;

public class Wgs84HelperTest {

    private final int EARTH_RADIUS = Wgs84Helper.EARTH_RADIUS_IN_KM;

    @Test
    public void testGetDistanceFromLatInKM() {
        double halfEarthDistance = Wgs84Helper.getDistanceFromLatInKm(90, -90);
        assertEquals(EARTH_RADIUS * Math.PI, halfEarthDistance, 0.000001);
    }

    @Test
    public void testGetDistanceFromLatInM() {
        double halfEarthDistance = Wgs84Helper.getDistanceFromLatInM(90, -90);
        assertEquals(EARTH_RADIUS * Math.PI * 1000, halfEarthDistance, 0.000001);
    }

    @Test
    public void shouldGetDistanceOnPole() {
        double lonDistanceOnPole = Wgs84Helper.getDistanceFromLatLonInKm(90, 0, 90, 180);
        assertEquals(0.0, lonDistanceOnPole, 0.000001);
    }

    @Test
    public void shouldGetDistanceOnEquator() {
        double lonDistanceOnEquator = Wgs84Helper.getDistanceFromLatLonInKm(0, 0, 0, 180);
        assertEquals(EARTH_RADIUS * Math.PI, lonDistanceOnEquator, 0.000001);
    }

    @Test
    public void shouldGetDistanceFromNorthPoleToSouthPole() {
        double halfEarthDistance = Wgs84Helper.getDistanceFromLatLonInKm(90, 0, -90, 0);
        assertEquals(EARTH_RADIUS * Math.PI, halfEarthDistance, 0.000001);
    }

    @Test
    public void testGetDistanceFromLatLonInM() {
        double lonDistanceOnEquatorInKm  = Wgs84Helper.getDistanceFromLatLonInKm(0, 0, 0, 180);
        double lonDistanceOnEquatorInMeter = Wgs84Helper.getDistanceFromLatLonInM(0, 0, 0, 180);

        assertEquals(lonDistanceOnEquatorInKm * 1000, lonDistanceOnEquatorInMeter, 0.000001);
    }

    @Test
    public void testGetDistanceFromLonInKM() {
        double lonDistanceOnPole = Wgs84Helper.getDistanceFromLonInKm(90, 0, 90, 180);
        assertEquals(0.0, lonDistanceOnPole, 0.000001);

        double lonDistanceOnEquator = Wgs84Helper.getDistanceFromLonInKm(0, 0, 0, 180);
        assertEquals(EARTH_RADIUS * Math.PI, lonDistanceOnEquator, 0.000001);
    }

    @Test
    public void testGetDistanceFromLonInM() {
        double lonDistanceOnPole  = Wgs84Helper.getDistanceFromLonInM(90, 0, 90, 180);
        assertEquals(0.0 * 1000, lonDistanceOnPole, 0.000001);

        double lonDistanceOnEquator = Wgs84Helper.getDistanceFromLonInM(0, 0, 0, 180);
        assertEquals(EARTH_RADIUS * Math.PI * 1000, lonDistanceOnEquator, 0.000001);
    }

    @Test(expected = Wgs84CoordinateException.class)
    public void expectSingaporeToLosAngelesInverseFailure() throws Wgs84CoordinateException {
        int lat1 = 103; // is out of range
        int lon1 = 1;
        Wgs84Helper.getDistanceFromLonInM(lat1, lon1, -118, 34);
    }

}