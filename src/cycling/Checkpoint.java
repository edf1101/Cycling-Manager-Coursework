package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Checkpoint {

    private static final HashMap<Integer, Checkpoint> checkpoints = new HashMap<Integer, Checkpoint>();
    private int myId; // its unique ID for checkpoints
    protected CheckpointType myType; // the type of checkpoint it is
    private Double location; // where in the stage it is located

    // The times that riders passed the checkpoint format of: <riderId, time>
    protected final HashMap<Integer, LocalTime> passTimes = new HashMap<Integer, LocalTime>();

    /**
     * Constructor for the abstract superclass checkpoint.
     * This will be called via super() in the subclasses
     */
    public Checkpoint() {
        myId = UniqueIdGenerator.calculateUniqueId(checkpoints);
        checkpoints.put(myId, this);
    }


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
     * @param riderId the Id of the rider to record
     * @param passTime What time they crossed the checkpoint
     */
    public void recordTime(int riderId, LocalTime passTime) {
        passTimes.put(riderId, passTime);
    }

    public int getMyId() {
        return myId;
    }

    /**
     * Get time for a rider as they passed the checkpoint
     *
     * @param riderId the rider's ID
     * @return the time that the rider reached the checkpoint
     */
    public LocalTime getPassTime(int riderId) {
        return passTimes.get(riderId);
    }

    // Don't think we need this method
    ///**
    // * Get time for a rider to reach the checkpoint from the previous checkpoint
    // *
    // * @param riderId the rider's ID
    // * @return the time that the rider took to reach the checkpoint
    // */
    //public LocalTime splitTime(int riderId) {
    //    return LocalTime.ofSecondOfDay(finishTimes.get(riderId).toSecondOfDay() - startTimes.get(riderId).toSecondOfDay());
    //}

    /**
     * Remove a rider's time from the checkpoint
     *
     * @param riderId the Id of the rider to remove from the checkpoint
     */
    public void removeRider(int riderId) {
        passTimes.remove(riderId);
    }

    /**
     * Get sprint points for a rider
     *
     * @param riderId the rider's ID
     * @return the sprint points for the rider
     */
    public abstract int getIntermediateSprintPoints(int riderId);

    /**
     * Get mountain points for a rider
     *
     * @param riderId the rider's ID
     * @return the mountain points for the rider
     */
    public abstract int getMountainPoints(int riderId);

}
