import java.util.*;
/**
 * A class that keeps account of a queue of packets, the number of collision for the current node, and the next
 * sense time.
 */
public class Node {
    double time;
    int numOfCollisions;
    int busyBuffer;
    Queue<Double> queue;

    public Node() {
        this.numOfCollisions = 0;
        this.busyBuffer = 0;
        this.queue = new LinkedList<Double>();
    }

    public void addToQueue(double generatedTime) {
        if (this.queue.isEmpty()) {
            this.time = generatedTime;
        }

        this.queue.add(generatedTime);
    }
}
