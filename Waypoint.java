import java.awt.geom.Point2D;

/**
 * A class to represent the Waypoints from METAL data.
 * 
 * These have a String label, and a latitude and longitude
 * encoded in the Point2D.Double.
 * 
 * @author Jim Teresco
 * @version Spring 2022
 */
public class Waypoint extends Point2D.Double {

   protected String label;

   /**
    * Construct a new Waypoint.
    * 
    * @param label point label
    * @param lat   latitude
    * @param lng   longitude
    */
   public Waypoint(String label, double lat, double lng) {

      super(lng, lat);
      this.label = label;
   }

   /**
    * Compute and return a distance on the earth's surface from
    * this Waypoint to another.
    * 
    * @param other the other point
    * @return the distance in miles between this point and the other
    */
   public double distanceTo(Waypoint other) {

      /* radius of the Earth in statute miles */
      final double EARTH_RADIUS = 3963.1;

      // coordinates in radians
      double rlat1 = Math.toRadians(y);
      double rlng1 = Math.toRadians(x);
      double rlat2 = Math.toRadians(other.y);
      double rlng2 = Math.toRadians(other.x);

      return Math.acos(Math.cos(rlat1) * Math.cos(rlng1) * Math.cos(rlat2) * Math.cos(rlng2) +
            Math.cos(rlat1) * Math.sin(rlng1) * Math.cos(rlat2) * Math.sin(rlng2) +
            Math.sin(rlat1) * Math.sin(rlat2)) * EARTH_RADIUS;

   }

   /**
    * Return a String representation of the Waypoint
    * 
    * @return a String representation of the Waypoint
    */
   public String toString() {

      return label + " (" + y + "," + x + ")";
   }

   /**
    * Return a METAL .wpt-format line for this Waypoint
    * 
    * @return a METAL .wpt-format line for this Waypoint
    */
   public String wptString() {

      return label + "  http://www.openstreetmap.org/?lat=" + y + "&lon=" + x;
   }

   /**
    * Return a METAL .nmp-format line for this Waypoint
    * 
    * @return a METAL .nmp-format line for this Waypoint
    */
   public String nmpString() {

      return label + " " + y + " " + x;
   }

}
