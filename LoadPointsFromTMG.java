import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A class containing a method to read a METAL TMG graph file and
 * return an array of Waypoint objects representing the graph's
 * vertices.
 * 
 * @author Jim Teresco
 * @version Spring 2022
 */

public class LoadPointsFromTMG {

	/**
	 * Open and read a METAL TMG graph file with the given name,
	 * returning an array of Waypoint objects in the file.
	 * 
	 * @param filename the name of a METAL TMG graph file to read
	 * @return an array of Waypoint objects representing the graph vertices
	 * @throws IOException
	 */
	public static Waypoint[] getArray(String filename) throws IOException {

		Scanner in = new Scanner(new File(filename));
		Waypoint[] points = null;

		try {
			// check this is a valid TMG file
			String header = in.next();
			if (!header.equals("TMG")) {
				throw new IOException("Invalid TMG file header \"" + header + "\", must be \"TMG\"");
			}
			String version = in.next();
			if (!version.equals("1.0") && !version.equals("2.0")) {
				throw new IOException("Invalid TMG file version " + version + ", must be 1.0 or 2.0");
			}
			String format = in.next();
			if (!format.equals("simple") &&
					!format.equals("collapsed") &&
					!format.equals("traveled")) {
				throw new IOException("Invalid TMG file format type \"" + format
						+ "\", must be one of \"simple\", \"collapsed\", or \"traveled\"");
			}

			// next, get the number of vertices and edges
			// (we'll be ignoring edges)
			int v = in.nextInt();
			int e = in.nextInt();

			// construct our array now that we know how many entries it
			// should have
			points = new Waypoint[v];

			// loop over the next v lines and add entries to the array
			for (int i = 0; i < v; i++) {
				points[i] = new Waypoint(in.next(),
						in.nextDouble(), in.nextDouble());
			}
		} catch (NoSuchElementException e) {
			throw new IOException("Invalid TMG file contents");
		}
		in.close();
		return points;
	}

	/**
	 * A main method to test this.
	 * 
	 * @param args[0] file name to try to load
	 * 
	 */
	public static void main(String args[]) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java LoadPointsFromTMG filename");
			System.exit(1);
		}

		System.out.println("Using file " + args[0]);
		Waypoint[] points = getArray(args[0]);
		System.out.println("Loaded " + points.length + " waypoints.");
		for (Waypoint w : points) {
			System.out.println(w);
		}
	}
}
