/**
 * A class created to store all results neatly.
 */
public class Result {
    int N;
    int arrivalRate;
    double efficiency;
    double throughput;

    public Result(int arrivalRate,int N,  double efficiency, double throughput) {
        this.N = N;
        this.arrivalRate = arrivalRate;
        this.efficiency = efficiency;
        this.throughput = throughput;

        System.out.println("Result");
        System.out.println("Number of Nodes " + N);
        System.out.println("Arrival Rate " + arrivalRate);
        System.out.println("Efficiency " + efficiency);
        System.out.println("Throughput " + throughput);
    }
}
