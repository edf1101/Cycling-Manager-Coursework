package cycling;

import java.time.LocalTime;
import java.util.HashMap;

/**
 * Abstract class for a checkpoint
 * This class is the superclass for the subclasses IntermediateSprint and Climb
 *
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
class Checkpoint extends Entity {
    // The times that riders passed the checkpoint format of: <riderId, time>
    protected final HashMap<Integer, LocalTime> passTimes = new HashMap<Integer, LocalTime>();
    protected CheckpointType myType; // the type of checkpoint it is
    private final Double location; // where in the stage it is located

    private final Stage parentStage; // the stage that the checkpoint is in

    /**
     * Constructor for the abstract superclass checkpoint.
     * This will be called via super() in the subclasses
     *
     * @param type        the type of checkpoint
     * @param location    the location of the checkpoint
     * @param parentStage the stage that the checkpoint is in
     * @throws InvalidLocationException   if the location is out of range of the
     *                                    stage
     * @throws InvalidStageTypeException  if the stage is not of the correct type
     * @throws InvalidStageStateException if the stage is already prepared
     */
    protected Checkpoint(CheckpointType type, Double location, Stage parentStage)
            throws InvalidLocationException, InvalidStageTypeException, InvalidStageStateException {
        super(); // Call the entity constructor

        // Check conditions are Ok for the checkpoint
        this.parentStage = parentStage;

        if (location <= 0 || location > parentStage.getLength()) {
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
    }

    /**
     * Gives the stage object that this belongs to
     *
     * @return the stage object that this belongs to
     */
    protected Stage getParentStage() {
        return parentStage;
    }

    /**
     * Record a rider's time at the checkpoint
     *
     * @param riderId  the Id of the rider to record
     * @param passTime What time they crossed the checkpoint
     */
    protected void recordTime(int riderId, LocalTime passTime) {
        int passTimesBefore = passTimes.size(); // Get the number of passTimes before the rider is added
        passTimes.put(riderId, passTime);
        assert passTimes.size() == passTimesBefore + 1; // Check that the rider has been added
    }

    /**
     * Get time for a rider as they passed the checkpoint
     *
     * @param riderId the rider's ID
     * @return the time that the rider reached the checkpoint
     */
    protected LocalTime getPassTime(int riderId) {
        return passTimes.get(riderId);
    }

    /**
     * Remove a rider's time from the checkpoint
     *
     * @param riderId the Id of the rider to remove from the checkpoint
     */
    protected void removeRider(int riderId) {
        int passTimesBefore = passTimes.size(); // Get the number of passTimes before the rider is removed
        passTimes.remove(riderId);
        assert passTimes.size() == passTimesBefore - 1; // Check that the rider has been removed
    }

    @Override
    protected void remove() {
        int idsBefore = usedIds.size(); // Get the number of usedIds before the checkpoint is removed
        freeId(); // Remove the checkpoint from the usedIds list
        assert idsBefore == usedIds.size() + 1 : "Number of IDs incorrect after removal";
    }

    /**
     * Get sprint points for a rider.
     *
     * @param riderId the rider's ID
     * @return the sprint points for the rider, 0 by default.
     */
    protected int getIntermediateSprintPoints(int riderId) {
        return 0;
    }

    /**
     * Get mountain points for a rider.
     *
     * @param riderId the rider's ID
     * @return the mountain points for the rider, 0 by default.
     */
    protected int getMountainPoints(int riderId) {
        return 0;
    }

    /**
     * Get the details of the checkpoint in a string form.
     *
     * @return A string describing the checkpoint
     */
    @Override
    public String toString() {
        return "Checkpoint belonging to: " + parentStage.getName() + " Type:" + myType +
                " location: " + location + "km";
    }
}
