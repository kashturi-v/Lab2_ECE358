

/**
 * This class Simulator is used to run the simulation with the following
 * parameters: simT: simulationT N: number of nodes A: arrival rate
 * isPersistent: whether the simulation is persistent or non-persistent
 */
public class Simulator {
    double D = 10;
    double S = (2 / 3);
    double R = 1e6;
    int L = 1500;
    double simT;
    int N;
    int A;
    double Ttrans;
    double Tprop;
    boolean isPersistent;
    Node[] nodes;

    public Simulator(int simT, int N, int A, boolean isPersistent) {
        this.simT = simT;
        this.N = N;
        this.A = A;
        this.isPersistent = isPersistent;
        this.nodes = new Node[N];
        this.Ttrans = this.L / this.R;
        this.Tprop = 5e-8;

    }

    public Result startSim() {
        // Variables used to keep track of transmission attemps and number of successful
        // transmissions
        int attempts = 0;
        int success = 0;

        // Generate the nodes with a queue of frames/packets using a poison disturbution
        // all within the range of the simulation

        for (int i = 0; i < nodes.length; i++) {
            Node currNode = new Node();
            double time = 0;
            
            do {
                // poisson disturbution
                time += (double) (-(1.0 / this.A) * Math.log(1 - Math.random()));
                currNode.addToQueue(time);
            } while (time < this.simT);
            nodes[i] = currNode;
        }
        // Running the acutal simulation
        double simTime = 0;
        while (simTime < this.simT) {

            // Select which node should transmit next, this node should have the frame with
            // the lowest timestamp
            int index = -1;
            double limitTime = this.simT;
            boolean didCollide = false;

            for (int i = 0; i < nodes.length; i++) {
                index = this.nodes[i].time < limitTime ? i : index;
                limitTime = this.nodes[i].time < limitTime ? this.nodes[i].time : limitTime;
            }
            // If no nodes are able to transmit, the simulation is finished
            if (index == -1) {
                System.out.println("Simulation Finished");
                break;
            }
            this.nodes[index].busyBuffer = 0;
            attempts++;
            simTime = limitTime;

            // Check if any other nodes aside from the sending node(the one that
            // transmitted) experiences a collision
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].queue.isEmpty() || i == index)
                    continue;

                // The time it takes for the first bit to be received by the current node from
                // the sender
                double start = limitTime + (this.Tprop * Math.abs(index - i));
                // The time it takes for the last bit to be received by the current node from
                // the sender
                double end = limitTime + this.Ttrans + (this.Tprop * Math.abs(index - i));

                // Compare the current node's sense time to the transmitted frame's
                // arrival time at the current to check if there is a collision or not
                if (this.nodes[i].time < start) {
                    didCollide = true;
                    attempts++;
                    this.nodes[i].busyBuffer = 0;
                    this.nodes[i].numOfCollisions++;

                    // If the node's collision counter is greater than Kmax which is 10, then we
                    // drop the frame from the queue
                    // and reset the collision counter and reset our sense time
                    if (this.nodes[i].numOfCollisions > 10) {
                        this.nodes[i].numOfCollisions = 0;

                        this.nodes[i].queue.poll();

                        if (!this.nodes[i].queue.isEmpty()) {
                            this.nodes[i].time = this.nodes[i].queue.peek();
                        }
                        // Otherwise, we back off this node to the sending node for a certain amount of
                        // time
                    } else {
                        double exponentialBackoff = ((Math.random() * (Math.pow(2.0, this.nodes[i].numOfCollisions) - 1)))
                                * 512 / this.R;
                        this.nodes[i].time = end + exponentialBackoff;
                    }

                }

            }
            // On the current sending node, we check if there were any collisions
            if (!didCollide) {
                // Any node that sense between the the start and end times, the node will find
                // the bus busy, hence,
                // we readjust the sense time
                for (int i = 0; i < nodes.length; i++) {
                    double start = (limitTime + this.Tprop * Math.abs(index - i));
                    double end = (limitTime + this.Ttrans + (this.Tprop * Math.abs(index - i)));
                    if (start <= this.nodes[i].time && this.nodes[i].time <= end) {
                        // in the case of the simulation being persistent we schedule our next bus-sense
                        // right at time end and immediately try again
                        if (isPersistent) {
                            this.nodes[i].time = end;
                        }
                        // in the non persistent case, we add an exponential backoff to the current
                        // sensing time
                        else {
                            this.nodes[i].busyBuffer++;

                            if (this.nodes[i].busyBuffer > 10) {
                                this.nodes[i].busyBuffer = 0;
                                attempts++;
                                this.nodes[i].queue.poll();
                            } else {
                                double exponentialBackoff = (Math.random()
                                        * (Math.pow(2.0, this.nodes[i].busyBuffer) - 1)) * 512 / this.R;
                                this.nodes[i].time =  end + exponentialBackoff;
                            }
                        }
                    }
                }
                // Reset the collision counter, remove the frame from the queue, increment our
                // success and reset our sense time
                this.nodes[index].queue.poll();
                success++;
                this.nodes[index].numOfCollisions = 0;
                if (!this.nodes[index].queue.isEmpty()) {
                    this.nodes[index].time = this.nodes[index].queue.peek();
                }
                // In the case of a collision we back off the sending node by a given amount
            } else {
                this.nodes[index].busyBuffer = 0;
                this.nodes[index].numOfCollisions++;
                // if the number of collisions exceeds Kmax, we drop the frame and reset the
                // sense time for the sending node
                if (this.nodes[index].numOfCollisions > 10) {
                    this.nodes[index].numOfCollisions = 0;

                    this.nodes[index].queue.poll();

                    if (!this.nodes[index].queue.isEmpty()) {
                        this.nodes[index].time = this.nodes[index].queue.peek();
                    }
                }
                // Otherwise we just backoff exponentially
                else {
                    double exponentialBackoff = (Math.random() * (Math.pow(2.0, this.nodes[index].numOfCollisions) - 1))
                            * 512 / this.R;
                    this.nodes[index].time = limitTime + this.Ttrans + exponentialBackoff;
                }
            }

        }

        for(int i=0; i<nodes.length; i++){
            attempts+=nodes[i].queue.size();
        }
        System.out.println("Attempts "+ attempts);
        System.out.println("Success "+ success);
        // Return our appropriate results back
        return new Result(A,N, (success * 1.0 / (attempts)), Math.abs((success * L / simT)/1000000));
    }
}