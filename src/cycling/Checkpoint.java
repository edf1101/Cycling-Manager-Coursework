package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class for a checkpoint
 * This class is the superclass for the subclasses IntermediateSprint and Climb
 *
 * @author 730003140 & 730002704
 * @version 1.0
 */
public abstract class Checkpoint implements java.io.Serializable{

    private static final HashMap<Integer, Checkpoint> checkpoints = new HashMap<Integer, Checkpoint>();
    private final int myId; // its unique ID for checkpoints
    protected CheckpointType myType; // the type of checkpoint it is
    private final Double location; // where in the stage it is located

    // The times that riders passed the checkpoint format of: <riderId, time>
    protected final HashMap<Integer, LocalTime> passTimes = new HashMap<Integer, LocalTime>();
    private final int parentStageId; // the stage that the checkpoint is in

    /**
     * Constructor for the abstract superclass checkpoint.
     * This will be called via super() in the subclasses
     *
     * @param type the type of checkpoint
     * @param location the location of the checkpoint
     * @param parentStageId the stage that the checkpoint is in
     * @throws InvalidLocationException if the location is out of range of the stage
     * @throws InvalidStageTypeException if the stage is not of the correct type
     * @throws InvalidStageStateException if the stage is already prepared
     */
    public Checkpoint(CheckpointType type, Double location, int parentStageId)
            throws InvalidLocationException, InvalidStageTypeException, InvalidStageStateException {
        // Check conditions are Ok for the checkpoint
        Stage parentStage = null;
        try {
            parentStage = Stage.getStageById(parentStageId);
        } catch (IDNotRecognisedException e) {
            assert false: "Parent stage ID not recognised, shouldn't happen";
        }

        if (location < 0 || location > parentStage.getLength()) {
            throw new InvalidLocationException("Location of checkpoint must be within the stage");
        }

        if (parentStage.getType() == StageType.TT) {
            throw new InvalidStageTypeException("Time trial stages cannot have checkpoints");
        }

        if (parentStage.isPrepared()) {
            throw new InvalidStageStateException("Stage already prepared");
        }

        this.myType = type;
        this.location = location;
        this.parentStageId = parentStageId;
        myId = UniqueIdGenerator.calculateUniqueId(checkpoints);
        checkpoints.put(myId, this);
    }

    /**
     * Pushes a checkpoint into the system.
     *
     * @param id the ID of the checkpoint to add
     * @param checkpoint the checkpoint object to add
     */
    public static void pushCheckpoint(int id, Checkpoint checkpoint) {
        checkpoints.put(id, checkpoint);
    }

    /**
     * Gives the stage object that this belongs to
     *
     * @return the stage object that this belongs to
     */
    public Stage getParentStage() {
        try{
            return Stage.getStageById(parentStageId);
        } catch (Exception e) {
            // this shouldn't happen ever since we have a valid stage ID checked before upon instantiation
            return null;
        }
    }

    /**
     * Getter for a checkpoint by its ID
     *
     * @param id the ID to query
     * @return the checkpoint with the given ID
     */
    public static Checkpoint getCheckpointById(int id) throws IDNotRecognisedException{
        if (!checkpoints.containsKey(id)) {
            throw new IDNotRecognisedException("Checkpoint " + id + " is not part of the system");
        }
        return checkpoints.get(id);
    }

    /**
     * Getter for all checkpoint IDs
     * @return an ArrayList of all checkpoint IDs
     */
    public static ArrayList<Integer> getIds() {
        return new ArrayList<Integer>(checkpoints.keySet());
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
     * @param id the ID of the checkpoint to remove
     * @throws IDNotRecognisedException if the id is not in the hashmap
     */
    public static void removeFromHashmap(int id) throws IDNotRecognisedException {
        // check if the checkpoint is in the hashmap
        if (!checkpoints.containsKey(id)) {
            throw new IDNotRecognisedException("Checkpoint " + id + " is not part of the system");
        }
        checkpoints.remove(id); // first delete it from the hashmap of all checkpoints
    }

    /**
     * Delete this checkpoint
     *
     * @throws IDNotRecognisedException if the checkpoint ID is not recognised
     * @throws InvalidStageStateException if the stage is already prepared
     */
    public void delete() throws IDNotRecognisedException, InvalidStageStateException {
        Stage.getStageById(parentStageId).removeCheckpoint(myId);
    }

    /**
     * Get the type of the checkpoint
     *
     * @return the type of the checkpoint
     */
    public Double getLocation() {
        return location;
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
