package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Stage {

    // Hashmap to store all stages
    private static final HashMap<Integer, Stage> stages = new HashMap<Integer, Stage>();

    // Hashmap to store the points for each stage type
    // TODO not sure if all caps name is correct given its a dict not an int[] any more, will check
    private static final HashMap<StageType,int[]> POINTS = new HashMap<StageType,int[]>();
    static {
        POINTS.put(StageType.FLAT, new int[]{ 50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2 });
        POINTS.put(StageType.MEDIUM_MOUNTAIN, new int[]{ 30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2 });
        POINTS.put(StageType.HIGH_MOUNTAIN, new int[]{ 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 });
        POINTS.put(StageType.TT, new int[]{ 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 });
    }

    private final int myId;
    private final String name;
    private final String description;
    private final StageType type;
    private final double length;
    private final int parentRace; // the race that this belongs to.
    private final ArrayList<Integer> checkpoints = new ArrayList<Integer>();
    private boolean prepared = false;

    // Hashmaps to store the start and finish times for each rider format of <riderId, time>
    private final HashMap<Integer, LocalTime> startTimes = new HashMap<Integer, LocalTime>();
    private final HashMap<Integer, LocalTime> finishTimes = new HashMap<Integer, LocalTime>();

    /**
     * Constructor for the Stage class
     *
     * @param name        Name of the stage
     * @param description Description of the stage
     * @param type        The type of the stage
     * @param length      The length of the stage
     * @param raceId    The ID of the race this stage is a part of
     * @throws InvalidNameException   if the name is not 0<characters<=30 or contains whitespace
     * @throws InvalidLengthException if the length is less than 5km
     */
    public Stage(String name, String description, StageType type, double length, int raceId)
            throws InvalidNameException, InvalidLengthException {

        // Check name is not null, empty or >30 chars
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Check length is not less than 5km
        if (length < 5) {
            throw new InvalidLengthException(" length broke rules. Length must be >= 5");
        }

        // Set up attributes for the object
        this.myId = UniqueIdGenerator.calculateUniqueId(stages);
        this.name = name;
        this.description = description;
        this.type = type;
        this.length = length;
        this.prepared = false;
        this.parentRace = raceId;

        // add the new object to the hashmap of all stages
        stages.put(this.myId, this);

        // add this stage id to its parent race's list of stages
        Race.getRaceById(raceId).addStage(this.myId);
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
     * @throws DuplicatedResultException        if the rider has already registered a result
     * @throws InvalidCheckpointTimesException if the number of times given ≠ the number of checkpoints + 2
     * @throws InvalidStageStateException       if the stage is not fully set up
     */
    public void registerResults(int riderId, LocalTime... times)
            throws DuplicatedResultException, InvalidCheckpointTimesException, InvalidStageStateException {

        // check stage is fully set up
        if (!prepared) {
            throw new InvalidStageStateException("Stage not prepared");
        }

        // Check if the rider has already registered a results
        if (startTimes.containsKey(riderId) || finishTimes.containsKey(riderId)){
            throw new DuplicatedResultException("Rider ID already has a finish time");
        }

        // Check the number of times is correct
        if (times.length != checkpoints.size() + 2) {
            throw new InvalidCheckpointTimesException("Number of times given is not number of checkpoints + 2");
        }

        // TODO: Check if we need to sort this first as the exception is thrown
        //  the number of times given ≠ the number of checkpoints + 2

        // Must be done before any changes are made
        //for (int i = 0; i < times.length - 1; i++) {
        //    if (times[i].isAfter(times[i + 1])) {
        //        throw new InvalidCheckpointTimesException("Checkpoint times are not in order");
        //    }
        //}

        startTimes.put(riderId, times[0]);
        finishTimes.put(riderId, times[times.length - 1]);

        // TODO this code for adding times will fail if its a time trial type (i think),
        //  will add a check for that when we add them
        // Consider that the array is [start, checkpoint1, checkpoint2, ..., finish]
        // The nth checkpoint starts at index n and ends at index n+1
        for (int i = 1; i < times.length - 1; i++) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpoints.get(i));
            checkpoint.recordTime(riderId, times[i]);
        }

    }

    /**
     * Adds a checkpoint to the stage
     *
     * @param checkpointId the ID of the checkpoint to add
     * @throws InvalidStageTypeException if the stage is a time trial stage
     */
    public void addCheckpoint(int checkpointId) throws InvalidStageTypeException {
        if (type == StageType.TT) {
            throw new InvalidStageTypeException("Time trial stages cannot have checkpoints");
        }
        checkpoints.add(checkpointId);
    }

    /**
     * Gets the total time a rider took to complete the stage
     *
     * @param riderId the ID of the rider to calculate the time for
     */
    public LocalTime totalTime(int riderId) throws IDNotRecognisedException {
        // check if the rider has a start and finish time recorded
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
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
        // TODO this should throw an error if the rider isn't in the system,
        //  If its only not in the stage then it should return empty array.
        // check if the rider has a start and finish time recorded
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        // Calculate the rider's position at the end of the stage by counting the number
        // of riders who finished before them
        int position = 1;
        for (int id : finishTimes.keySet()) {
            if (id != riderId && totalTime(id).isBefore(totalTime(riderId))) {
                position++;
            }
        }

        int[] pointsArray = POINTS.get(type);


        // Assume the rider gets 0 points if they finish outside the top 15
        // TODO check this with diogo
        int points = (position > 15) ? 0 : pointsArray[position - 1];

        // Add the points from the checkpoints for intermediate sprints
        for (int checkpointId : checkpoints) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
            points += checkpoint.getIntermediateSprintPoints(riderId);
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

        // check if the rider has a start and finish time recorded
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
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

        for (int checkpointId : checkpoints) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
            checkpoint.removeRider(riderId);
        }
    }

    public void remove() {

        stages.remove(myId); // remove from the dictionary of stages
        Race.getRaceById(parentRace).removeStage(myId);  // remove from race object

        // TODO: remove all checkpoints in this stage

    }

    /**
     * Getter for the Stage Id
     *
     * @return the Id of the parent Race
     */
    public int getId() {
        return myId;
    }

    /**
     * Getter for the race that this stage belongs to
     *
     * @return the Id of the parent Race
     */
    public int getParentRace() {
        return parentRace;
    }

    /**
     * Getter for the name of this stage
     *
     * @return the Stage name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the description of this stage
     *
     * @return the stage description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the name of this stage
     *
     * @return the Stage name
     */
    public StageType getType() {
        return type;
    }

    /**
     * Getter for the length of this stage
     *
     * @return the stage length
     */
    public double getLength() {
        return length;
    }

    /**
     * Getter for the checkpoints of this stage
     *
     * @return the stage checkpoints
     */
    public ArrayList<Integer> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Getter for whether the stage is prepared
     *
     * @return boolean indicating whether the stage is prepared
     */
    public boolean isPrepared() {
        return prepared;
    }

    /**
     * Getter for registered riders in the stage
     *
     * @return the registered riders
     */
    public ArrayList<Integer> getRegisteredRiders() {
        return new ArrayList<Integer>(finishTimes.keySet());
    }
}
