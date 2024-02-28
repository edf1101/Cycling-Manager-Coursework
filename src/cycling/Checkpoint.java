package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

// TODO

public abstract class Checkpoint {

    private static final HashMap<Integer, Checkpoint> checkpoints = new HashMap<Integer, Checkpoint>();
    private int myId;

    private HashMap<Integer, LocalTime> startTimes = new HashMap<Integer, LocalTime>();
    private HashMap<Integer, LocalTime> finishTimes = new HashMap<Integer, LocalTime>();

    /**
     * Getter for a checkpoint by its ID
     *
     * @param id the ID to query
     */
    public static Checkpoint getCheckpointById(int id) {
        return checkpoints.get(id);
    }

    /**
     * Getter for all checkpoint IDs
     */
    public static ArrayList<Integer> getIds() {
        return new ArrayList<Integer>(checkpoints.keySet());
    }

    /**
     * Record a rider's time at the checkpoint
     *
     * @param riderId
     * @param startTime
     * @param finishTime
     */
    public void recordTime(int riderId, LocalTime startTime, LocalTime finishTime) {
        startTimes.put(riderId, startTime);
        finishTimes.put(riderId, finishTime);
    }

    public int getMyId() {
        return myId;
    }

    /**
     * Get time for a rider at a checkpoint
     *
     * @param riderId the rider's ID
     * @return the time that the rider reached the checkpoint
     */
    public LocalTime getTime(int riderId) {
        return finishTimes.get(riderId);
    }

    /**
     * Get time for a rider to reach the checkpoint from the previous checkpoint
     *
     * @param riderId the rider's ID
     * @return the time that the rider took to reach the checkpoint
     */
    public LocalTime splitTime(int riderId) {
        return LocalTime.ofSecondOfDay(finishTimes.get(riderId).toSecondOfDay() - startTimes.get(riderId).toSecondOfDay());
    }

    /**
     * Remove a rider's time from the checkpoint
     *
     * @param riderId
     */
    public void removeRider(int riderId) {
        startTimes.remove(riderId);
        finishTimes.remove(riderId);
    }

    /**
     * Get sprint points for a rider
     *
     * @param riderId the rider's ID
     * @return the sprint points for the rider
     */
    public abstract int getSprintPoints(int riderId);

    /**
     * Get mountain points for a rider
     *
     * @param riderId the rider's ID
     * @return the mountain points for the rider
     */
    public abstract int getMountainPoints(int riderId);
}
