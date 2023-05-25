import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * From a list of Waypoint objects representing places read from a
 * METAL TMG file, find the closest other point to every point.
 * 
 * @author Jim Teresco (starter)
 * @author Tyler Streithorst and Saif Ullah
 * @version Spring 2022
 * 
 */
public class ClosestToAll {

    // the array of Waypoint objects as read from a TMG file
    protected Waypoint[] points;

    // we will fill this array with the index of the closest other
    protected int[] closest;

    // the closest overall pair of points
    protected double globalMinD = Double.MAX_VALUE;

    // computing the subset of entries of closest
    protected double numThreads;
    protected Object lock = new Object();
    protected BagOfNumbers bag;

    /**
     * Construct a new ClosestToAll object with its points array
     * populated by the waypoints found in the given file, which
     * should be a path to a METAL TMG file version 1.0 or 2.0.
     * 
     * @param filename a path to a METAL TMG file version 1.0 or 2.0
     * @throws IOException
     */
    public ClosestToAll(String filename, int numThreads) throws IOException {

        points = LoadPointsFromTMG.getArray(filename);
        closest = new int[points.length];
        this.numThreads = numThreads;

    }

    /**
     * The number of Waypoints currently stored in the points array.
     * 
     * @return the number of Waypoints currently stored in the points array
     */
    public int numPoints() {

        return points.length;
    }

    /**
     * Perform the computation that fills the "closest" array at
     * position i with the index of the closest other Waypoint
     * in the points array to the Waypoint in points[i].
     * 
     * Also set globalMinD to the smallest distance between any
     * pair of points in the whole set.
     */
    public void computeClosestPointsSerial() {

        globalMinD = Double.MAX_VALUE;

        for(int i = 0; i < points.length; i++) {
            double pointMin = Double.MAX_VALUE;
            for(int j = 0; j < points.length; j ++) {
                if(i!=j && points[i].distanceTo(points[j]) < pointMin) {
                    pointMin = points[i].distanceTo(points[j]);
                    closest[i] = j;
                    if(pointMin < globalMinD)
                        globalMinD = pointMin;
                }
            }
        }
    }

    /*
     * This creates a number of threaded objects equal to the number passed in the command line parameters
     */
    public void computeClosestPointsSplit() {
        globalMinD = Double.MAX_VALUE;

        WorkerThread[] workerThreads = new WorkerThread[(int)numThreads];
        for (int i = 0; i < numThreads; i++) {
            workerThreads[i] = new WorkerThread(i);
            workerThreads[i].start();
        }
        for(int i = 0; i < numThreads; i++) {
            try {
                workerThreads[i].join();
            }
            catch(InterruptedException e) {}
        }
    }

    /*
     * This creates a number of threaded objects, and initializes BagOfNumbers for each thread to use
     */
    public void computeClosestPoints() {
        bag = new BagOfNumbers(points.length - 1);
        globalMinD = Double.MAX_VALUE;

        WorkerThread[] workerThreads = new WorkerThread[(int)numThreads];
        for (int i = 0; i < numThreads; i++) {
            workerThreads[i] = new WorkerThread(i);
            workerThreads[i].start();
        }
        for(int i = 0; i < numThreads; i++) {
            try {
                workerThreads[i].join();
            }
            catch(InterruptedException e) {}
        }
    }

    /*
     * This threaded object will get the next point in the array that was not computed, and get its closest point
     */
    class WorkerThread extends Thread {

        protected int threadNum;
    
        public WorkerThread(int threadNum) {
                // call superclass constructor
                super();
                this.threadNum = threadNum;
        }

        @Override
        public void run(){
        int toCompute = bag.getNext();
        while (toCompute != -1) {
                    double pointMin = Double.MAX_VALUE;
                    for(int j = 0; j < points.length; j ++) {
                        if(toCompute!=j && points[toCompute].distanceTo(points[j]) < pointMin) {
                            pointMin = points[toCompute].distanceTo(points[j]);
                            closest[toCompute] = j;
                            synchronized (lock) {
                                if(pointMin < globalMinD)
                                    globalMinD = pointMin;
                            }
                        }
                    }
                    toCompute = bag.getNext();
                }
            }
        }

    

    /**
     * Write a METAL NMP format file listing each point in the points
     * array with its computed closest other point. The file is
     * suitable for loading into METAL's HDX program.
     * 
     * @param filename the path of a file to create containing the NMP
     *                 format output for the closest points.
     */
    public void writeNMPFile(String filename) {

        try {
            PrintWriter pw = new PrintWriter(new File(filename));
            for (int start = 0; start < points.length; start++) {
                pw.println(points[start].nmpString());
                pw.println(points[closest[start]].nmpString());
            }
            pw.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Get an array of Waypoint objects read from a METAL graph TMG file,
     * and find the closest other point to each point in the file.
     * 
     * @param args[0] TMG file name to read
     * @param args[1] filename for output results (use "-" if no file
     *                should be created)
     */
    public static void main(String args[]) throws IOException {

        if (args.length != 3) {
            System.err.println("Usage: java ClosestToAll filename outfile");
            System.exit(1);
            
        }

        int threadsInput = 0;
        try{
            threadsInput = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Usage: java ClosestToAll integer necessary");
            System.exit(1);
        }

        ClosestToAll worker = new ClosestToAll(args[0], threadsInput);

        double startTime = System.currentTimeMillis();

        worker.computeClosestPoints();

        double elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Found results for " + worker.numPoints() + " points in "
                + elapsedTime + " ms");
        if (!args[1].equals("-")) {
            worker.writeNMPFile(args[1]);
        }
    }
}
