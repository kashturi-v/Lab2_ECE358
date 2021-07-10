import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        FileWriter csvWriter;
        FileWriter csvWriter2;
        try {
            csvWriter = new FileWriter("performance.csv");
            csvWriter.append("A");
            csvWriter.append(",");
            csvWriter.append("N");
            csvWriter.append(",");
            csvWriter.append("Efficiency");
            csvWriter.append(",");
            csvWriter.append("Throughput");
            csvWriter.append("\n");

            ArrayList<Integer> arrivalRates = new ArrayList<>(Arrays.asList(7,10,20));
            ArrayList<Integer> numOfNodes = new ArrayList<>(Arrays.asList(20, 40, 60, 80,100));
            for(int i: numOfNodes){
                for(int j: arrivalRates){
                    Simulator sim = new Simulator(1000, i, j, true);
                    Result r = sim.startSim();
                    List<String> value = Arrays.asList("" + r.arrivalRate, "" + r.N, "" + r.efficiency,""+r.throughput);
                    csvWriter.append(String.join(",", value));
                    csvWriter.append("\n");
                }
            }

            csvWriter2 = new FileWriter("nonpperformance.csv");
            csvWriter2.append("A");
            csvWriter2.append(",");
            csvWriter2.append("N");
            csvWriter2.append(",");
            csvWriter2.append("Efficiency");
            csvWriter2.append(",");
            csvWriter2.append("Throughput");
            csvWriter2.append("\n");
            
            for(int i: numOfNodes){
                for(int j: arrivalRates){
                    Simulator sim = new Simulator(1000, i, j, false);
                    Result r = sim.startSim();
                    List<String> value = Arrays.asList("" + r.arrivalRate, "" + r.N, "" + r.efficiency,""+r.throughput);
                    csvWriter2.append(String.join(",", value));
                    csvWriter2.append("\n");
                }
            }

            csvWriter.append("\n");
            csvWriter.flush();
            csvWriter.close();
            csvWriter2.append("\n");
            csvWriter2.flush();
            csvWriter2.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
