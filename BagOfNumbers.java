/**
 * Implementation of a "bag of numbers". Each number from 0 to
 * last-1 will be returned exactly once by calls to the getNext
 * method.
 * 
 * @author Jim Teresco
 * @version Spring 2022
 */
public class BagOfNumbers {

    protected int next;
    protected int last;

    /**
     * Construct a new "bag of numbers" that will return numbers
     * between 0 and last-1.
     * 
     * @param last the number of numbers to be returned (0 to last-1)
     */
    public BagOfNumbers(int last) {

        this.next = 0;
        this.last = last;
    }

    /**
     * Return the next value in the range 0 to last-1 that has not yet
     * been returned by a call to this method. If no additional numbers
     * remain, returns -1.
     * 
     * @return the next unreturned value in the range 0 to last-1, or -1
     *         if all such values have been previously returned.
     */
    synchronized int getNext() {

        int retval = next;
        next++;
        if (next <= last + 1)
            return retval;
        else
            return -1;
    }
}
