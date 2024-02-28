package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Stage {

    private static final HashMap<Integer, Stage> stages = new HashMap<Integer, Stage>();

    // If all of these are correct I get +1% and/or some guiness
    public static final int[] FLAT_POINTS = { 50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2 };
    public static final int[] MEDIUM_MOUNTAIN_POINTS = { 30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2 };
    public static final int[] HIGH_MOUNTAIN_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
    public static final int[] TT_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

    private int myId;
    private String name;
    private String description;
    private StageType type;
    private double length;
    private ArrayList<Integer> checkpoints;
    private boolean prepared = false;
    private ArrayList<Integer> riders; // riders are added to this list when their times are registered
    private HashMap<Integer, LocalTime> startTimes = new HashMap<Integer, LocalTime>();
    private HashMap<Integer, LocalTime> finishTimes = new HashMap<Integer, LocalTime>();

    /**
     * Constructor for the Stage class
     *
     * @param name        Name of the stage
     * @param description Description of the stage
     * @param type        The type of the stage
     * @param length      The length of the stage
     */
    public Stage(String name, String description, StageType type, double length) {
        // TODO Check there are no rules for stage naming

        // Set up attributes for the object
        this.myId = UniqueIdGenerator.calculateUniqueId(stages);
        this.name = name;
        this.description = description;
        this.type = type;
        this.length = length;
        this.checkpoints = new ArrayList<Integer>();
        this.prepared = false;

        // add the new object to the hashmap of all stages
        stages.put(this.myId, this);
    }

    /**
     * Getter for a stage by its ID
     *
     * @param id the Id to query
     * @return The stage object reference
     */
    public static Stage getStageById(int id) {
        return stages.get(id);
    }

    /**
     * Getter for all stage IDs
     */
    public static ArrayList<Integer> getIds() {
        return new ArrayList<Integer>(stages.keySet());
    }

    /**
     * Register a rider's results at each checkpoint
     *
     * @param riderId the ID of the rider
     * @param times   the start time, times at each checkpoint, and the finish time
     */
    public void registerResults(int riderId, LocalTime... times)
            throws DuplicatedResultException, InvalidCheckpointTimesException {
        if (riders.contains(riderId)) {
            throw new DuplicatedResultException("Rider ID already has a finish time");
        }

        // Must be done before any changes are made
        for (int i = 0; i < times.length - 1; i++) {
            if (times[i].isAfter(times[i + 1])) {
                throw new InvalidCheckpointTimesException("Checkpoint times are not in order");
            }
        }

        startTimes.put(riderId, times[0]);
        finishTimes.put(riderId, times[times.length - 1]);

        // Consider that the array is [start, checkpoint1, checkpoint2, ..., finish]
        // The nth checkpoint starts at index n and ends at index n+1
        for (int i = 0; i < times.length - 2; i++) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpoints.get(i));
            checkpoint.recordTime(riderId, times[i], times[i + 1]);
        }

        riders.add(riderId);
    }

    /**
     * Adds a checkpoint to the stage
     *
     * @param checkpointId the ID of the checkpoint to add
     */
    public void addCheckpoint(int checkpointId) {
        checkpoints.add(checkpointId);
    }

    /**
     * Gets the total time a rider took to complete the stage
     *
     * @param riderId the ID of the rider to calculate the time for
     */
    public LocalTime totalTime(int riderId) throws IDNotRecognisedException {
        if (!riders.contains(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        return LocalTime
                .ofSecondOfDay(finishTimes.get(riderId).toSecondOfDay() - startTimes.get(riderId).toSecondOfDay());
    }

    /**
     * Get the sprint points for a rider.
     * Sprint points are calculated based on the rider's position at the end of the
     * stage and the type of stage. Sprint points from checkpoints are added to
     * this.
     *
     * @param riderId the ID of the rider to calculate the sprint points for
     * @return the number of sprint points the rider gets for this stage
     */
    public int sprintPoints(int riderId) throws IDNotRecognisedException {
        if (!riders.contains(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        // Calculate the rider's position at the end of the stage by counting the number
        // of riders who finished before them
        int position = 1;
        for (int id : riders) {
            if (id != riderId && finishTimes.get(id).isBefore(finishTimes.get(riderId))) {
                position++;
            }
        }

        int[] pointsArray = null;

        switch (type) {
            case FLAT:
                pointsArray = FLAT_POINTS;
                break;
            case MEDIUM_MOUNTAIN:
                pointsArray = MEDIUM_MOUNTAIN_POINTS;
                break;
            case HIGH_MOUNTAIN:
                pointsArray = HIGH_MOUNTAIN_POINTS;
                break;
            case TT:
                pointsArray = TT_POINTS;
                break;
        }

        int points = pointsArray[position - 1]; // TODO Not sure what should happen if position is >15, possibly return
                                                // 1

        for (int checkpointId : checkpoints) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
            points += checkpoint.getSprintPoints(riderId);
        }

        return points;
    }

    /**
     * Get the mountain points for a rider.
     * Mountain points are calculated based on the rider's position at the end of
     * each mountain checkpoint.
     *
     * @param riderId the ID of the rider to calculate the mountain points for
     * @return the number of mountain points the rider gets for this stage
     */
    public int mountainPoints(int riderId) throws IDNotRecognisedException {
        if (!riders.contains(riderId)) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        int points = 0;

        for (int checkpointId : checkpoints) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
            points += checkpoint.getMountainPoints(riderId);
        }

        return points;
    }

    /**
     * Conclude the preparation of the stage.
     */
    public void prepare() {
        prepared = true;
    }

    /**
     * Removes all of a rider's results from the stage
     */
    public void removeRider(int riderId) {
        startTimes.remove(riderId);
        finishTimes.remove(riderId);
        riders.remove(riderId);

        for (int checkpointId : checkpoints) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
            checkpoint.removeRider(riderId);
        }
    }

    public int getId() {
        return myId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public StageType getType() {
        return type;
    }

    public double getLength() {
        return length;
    }

    public ArrayList<Integer> getCheckpoints() {
        return checkpoints;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public ArrayList<Integer> getRegisteredRiders() {
        return riders;
    }
}
