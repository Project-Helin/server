package commons.gis;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by martin on 09.04.16.
 */
public class WGS84HelperTest {

    private int EARTH_RADIUS = 6371;

    @Test
    public void testGetDistanceFromLatInKM() {
        double halfEarthDistance = 0;
        try {
            halfEarthDistance = WGS84Helper.getDistanceFromLatInKM(90, -90);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(EARTH_RADIUS * Math.PI, halfEarthDistance, 0.000001);
    }

    @Test
    public void testGetDistanceFromLatInM() {
        double halfEarthDistance = 0;
        try {
            halfEarthDistance = WGS84Helper.getDistanceFromLatInM(90, -90);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(EARTH_RADIUS * Math.PI * 1000, halfEarthDistance, 0.000001);
    }

    @Test
    public void testGetDistanceFromLatLonInKm() {

        // Pole Test
        double lonDistanceOnPole = Double.MAX_VALUE;
        try {
            lonDistanceOnPole = WGS84Helper.getDistanceFromLatLonInKm(90, 0, 90, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(0.0, lonDistanceOnPole, 0.000001);

        // Aequator Test
        double lonDistanceOnEquator = 0;
        try {
            lonDistanceOnEquator = WGS84Helper.getDistanceFromLatLonInKm(0, 0, 0, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(EARTH_RADIUS * Math.PI, lonDistanceOnEquator, 0.000001);

        //Pole To Pole Test
        double halfEarthDistance = 0;
        try {
            halfEarthDistance = WGS84Helper.getDistanceFromLatLonInKm(90, 0, -90, 0);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(EARTH_RADIUS * Math.PI, halfEarthDistance, 0.000001);
    }

    @Test
    public void testGetDistanceFromLatLonInM() {
        double lonDistanceOnEquatorKM = 0;
        try {
            lonDistanceOnEquatorKM = WGS84Helper.getDistanceFromLatLonInKm(0, 0, 0, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }

        double lonDistanceOnEquatorM = 0;
        try {
            lonDistanceOnEquatorM = WGS84Helper.getDistanceFromLatLonInM(0, 0, 0, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }

        assertEquals(lonDistanceOnEquatorKM * 1000, lonDistanceOnEquatorM, 0.000001);
    }

    @Test
    public void testGetDistanceFromLonInKM() {
        double lonDistanceOnPole = Double.MAX_VALUE;
        try {
            lonDistanceOnPole = WGS84Helper.getDistanceFromLonInKM(90, 0, 90, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(0.0, lonDistanceOnPole, 0.000001);

        double lonDistanceOnEquator = 0;
        try {
            lonDistanceOnEquator = WGS84Helper.getDistanceFromLonInKM(0, 0, 0, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(EARTH_RADIUS * Math.PI, lonDistanceOnEquator, 0.000001);

    }

    @Test
    public void testGetDistanceFromLonInM() {
        double lonDistanceOnPole = Double.MAX_VALUE;
        try {
            lonDistanceOnPole = WGS84Helper.getDistanceFromLonInM(90, 0, 90, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(0.0 * 1000, lonDistanceOnPole, 0.000001);

        double lonDistanceOnEquator = 0;
        try {
            lonDistanceOnEquator = WGS84Helper.getDistanceFromLonInM(0, 0, 0, 180);
        } catch (WGS84CoordinateException e) {
            e.printStackTrace();
        }
        assertEquals(EARTH_RADIUS * Math.PI * 1000, lonDistanceOnEquator, 0.000001);

    }

    @Test(expected = WGS84CoordinateException.class)
    public void expectSingaporeToLosAngelesInverseFailure() throws WGS84CoordinateException {
            double distanceSingaporeToLosAngeles = WGS84Helper.getDistanceFromLonInM(103, 1, -118, 34);
    }
}