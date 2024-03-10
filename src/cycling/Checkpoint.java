package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class for a checkpoint
 * This class is the superclass for the subclasses IntermediateSprint and Climb
 *
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
public abstract class Checkpoint implements java.io.Serializable{
    private static final ArrayList<Integer> idsUsed = new ArrayList<>(); // list of the ids used
    // The times that riders passed the checkpoint format of: <riderId, time>
    protected final HashMap<Integer, LocalTime> passTimes = new HashMap<Integer, LocalTime>();
    private final int myId; // its unique ID for checkpoints
    protected CheckpointType myType; // the type of checkpoint it is
    private final Double location; // where in the stage it is located

    private final Stage parentStage; // the stage that the checkpoint is in

    /**
     * Constructor for the abstract superclass checkpoint.
     * This will be called via super() in the subclasses
     *
     * @param type the type of checkpoint
     * @param location the location of the checkpoint
     * @param parentStage the stage that the checkpoint is in
     * @throws InvalidLocationException if the location is out of range of the stage
     * @throws InvalidStageTypeException if the stage is not of the correct type
     * @throws InvalidStageStateException if the stage is already prepared
     */
    public Checkpoint(CheckpointType type, Double location, Stage parentStage)
            throws InvalidLocationException, InvalidStageTypeException, InvalidStageStateException {
        // Check conditions are Ok for the checkpoint
        this.parentStage = parentStage;

        if (location < 0 || location > parentStage.getLength()) {
            throw new InvalidLocationException("Location of checkpoint must be within the stage");
        }

        if (parentStage.getType() == StageType.TT) {
            throw new InvalidStageTypeException("Time trial stages cannot have checkpoints");
        }

        if (parentStage.getPrepared()) {
            throw new InvalidStageStateException("Stage already prepared");
        }

        this.myType = type;
        this.location = location;
        myId = UniqueIdGenerator.calculateUniqueId(idsUsed);
        idsUsed.add(myId);
    }

    /**
     * Gives the stage object that this belongs to
     *
     * @return the stage object that this belongs to
     */
    public Stage getParentStage() {
        return parentStage;
        }

    /**
     * Record a rider's time at the checkpoint
     *
     * @param riderId  the Id of the rider to record
     * @param passTime What time they crossed the checkpoint
     */
    public void recordTime(int riderId, LocalTime passTime) {
        passTimes.put(riderId, passTime);
    }

    /**
     * Get the Id of the checkpoint
     *
     * @return the checkpoint id
     */
    public int getId() {
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

    /**
     * Remove a rider's time from the checkpoint
     *
     * @param riderId the Id of the rider to remove from the checkpoint
     */
    public void removeRider(int riderId) {
        passTimes.remove(riderId);
    }

    /**
     * Remove a checkpoint from the hashmap
     *
     * @throws IDNotRecognisedException if the id is not in the hashmap
     */
    public void delete() throws IDNotRecognisedException {
        // check if the checkpoint is in the hashmap
        idsUsed.remove(Integer.valueOf(myId));
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
