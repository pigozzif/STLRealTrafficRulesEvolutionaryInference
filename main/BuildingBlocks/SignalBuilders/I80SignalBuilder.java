package BuildingBlocks.SignalBuilders;

import BuildingBlocks.TrajectoryRecord;
import eu.quanticol.moonlight.signal.Signal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class I80SignalBuilder implements SignalBuilder<Signal<TrajectoryRecord>[]> {

    private final int windowSize = 200;
    private HashMap<String, double[]> varsBounds = new HashMap<>();
    private double[] temporalBounds;

    // TODO: maybe fix Long Method
    public List<Signal<TrajectoryRecord>[]> parseSignals(String fileName, List<Integer> boolIndexes,
                                                              List<Integer> doubleIndexes) throws IOException {
        List<Signal<TrajectoryRecord>[]> signals = new ArrayList<>();
        BufferedReader reader = this.createReaderFromFile(fileName);
        String[] header = reader.readLine().split(",");
        for (Integer idx : doubleIndexes) {
            this.varsBounds.put(header[idx], new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
        }
        this.temporalBounds = new double[]{0, 99};
        int vehicleIdx = 1;
        boolean isFinished = false;
        String[] line = new String[boolIndexes.size() + doubleIndexes.size() + 1];
        List<TrajectoryRecord> trajectory = new ArrayList<>();
        List<Long> times = new ArrayList<>();
        boolean[] boolVars = new boolean[boolIndexes.size()];
        double[] doubleVars = new double[doubleIndexes.size()];
        while (!isFinished) {
            while (true) {
                try {
                    line = reader.readLine().split(",");
                }
                catch (NullPointerException | IOException e) {
                    isFinished = true;
                    break;
                }
                for (int idx=0; idx < boolIndexes.size(); ++idx) {
                    boolVars[idx] = line[boolIndexes.get(idx)].equals("1");
                }
                for (int idx=0; idx < doubleIndexes.size(); ++idx) {
                    double val = Double.parseDouble(line[doubleIndexes.get(idx)]);
                    double[] temp = this.varsBounds.get(header[doubleIndexes.get(idx)]);
                    temp[0] = Math.min(val, temp[0]);
                    temp[1] = Math.max(val, temp[1]);
                    doubleVars[idx] = val;
                }
                if (vehicleIdx != Integer.parseInt(line[14])) {
                    break;
                }
                trajectory.add(new TrajectoryRecord(boolVars, doubleVars));
                times.add(Long.parseLong(line[15].trim()));
            }
            //if (line[14].equals("3")) {
            //    break;
            //}
            createSignalAndUpdateWithSlidingWindow(trajectory, times, signals);
            vehicleIdx = Integer.parseInt(line[14]);
            trajectory.add(new TrajectoryRecord(boolVars, doubleVars));
            times.add(Long.parseLong(line[15]));
        }
        reader.close();
        return signals;
    }

    @Override
    public HashMap<String, double[]> getVarsBounds() {
        return this.varsBounds;
    }

    @Override
    public double[] getTemporalBounds() {
        return this.temporalBounds;
    }

    private void createSignalAndUpdateWithSlidingWindow(List<TrajectoryRecord> trajectory, List<Long> times,
                                              List<Signal<TrajectoryRecord>[]> signals) {
        if (times.size() == 0) { // TODO: maybe more complete check
            return;
        }
        Signal<?>[] innerSignal = new Signal<?>[(trajectory.size() * 2) / this.windowSize];
        int length = times.size();
        int j = 0;
        int i;
        int t = 0;
        double time = 0.0;
        while (j < length) {
            Signal<TrajectoryRecord> currSignal = new Signal<>();
            for (i = 0; i < this.windowSize && j < length; ++i, ++j) {
                currSignal.add(time, trajectory.get(j));
                ++time;
            }
            currSignal.endAt(time);
            ++time;
            innerSignal[t] = currSignal;
            j -= this.windowSize / 2;
            if (currSignal.size() != this.windowSize) {
                break;
            }
            ++t;
        }
        signals.add((Signal<TrajectoryRecord>[]) innerSignal);
        trajectory.clear();
        times.clear();
    }

    private static void createSignalAndUpdate(List<TrajectoryRecord> trajectory, List<Long> times,
                                              List<Signal<TrajectoryRecord>> signals) {
        if (times.size() == 0) { // TODO: maybe more complete check
            return;
        }
        Signal<TrajectoryRecord> currSignal = new Signal<>();
        int length = times.size();
        double time = 0.0;
        for (int i=0; i < length; ++i) {
            currSignal.add(time, trajectory.get(i));
            ++time;
        }
        currSignal.endAt(time);
        ++time;
        signals.add(currSignal);
        trajectory.clear();
        times.clear();
    }

}
