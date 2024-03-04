package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

// TODO we haven't handled time trials yet, oops

public class Stage {
    private static final HashMap<Integer, Stage> stages = new HashMap<Integer, Stage>(); // Hashmap of all stages

    private static final HashMap<StageType, int[]> POINTS = new HashMap<StageType, int[]>(); // Points for each stage
                                                                                             // type
    static {
        POINTS.put(StageType.FLAT, new int[] { 50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2 });
        POINTS.put(StageType.MEDIUM_MOUNTAIN, new int[] { 30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2 });
        POINTS.put(StageType.HIGH_MOUNTAIN, new int[] { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 });
        POINTS.put(StageType.TT, new int[] { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 });
    }

    private final int myId;
    private final String name;
    private final String description;
    private final StageType type;
    private final double length;
    private final ArrayList<Integer> checkpoints = new ArrayList<Integer>();
    private boolean prepared = false;

    private final int parentRaceId;

    // Hashmaps to store the start and finish times for each rider format of
    // <riderId, time>
    private final HashMap<Integer, LocalTime> startTimes = new HashMap<Integer, LocalTime>();
    private final HashMap<Integer, LocalTime> finishTimes = new HashMap<Integer, LocalTime>();

    /**
     * Constructor for the Stage class.
     *
     * @param name        Name of the stage
     * @param description Description of the stage
     * @param type        The type of the stage
     * @param length      The length of the stage
     * @param parentRaceId The ID of the parent race that this stage belongs to
     * @throws InvalidNameException   if the name is not 0<characters<=30 or
     *                                contains whitespace
     * @throws InvalidLengthException if the length is less than 5km
     */
    public Stage(String name, String description, StageType type, double length, int parentRaceId)
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
        this.parentRaceId = parentRaceId;

        // add the new object to the hashmap of all stages
        stages.put(this.myId, this);
    }

    /**
     * Getter for the parent race this stage belongs to
     * @return the race Id
     */
    public int getRaceId() {
        return parentRaceId;
    }

    /**
     * Getter for a stage by its ID.
     *
     * @param id the ID to query
     * @return The stage object reference
     * @throws IDNotRecognisedException if the ID is not recognised
     */
    public static Stage getStageById(int id) throws IDNotRecognisedException {
        if (!stages.containsKey(id)) {
            throw new IDNotRecognisedException("Stage ID not recognised");
        }

        return stages.get(id);
    }

    /**
     * Getter for all stage IDs.
     *
     * @return an array of all stage IDs
     */
    public static ArrayList<Integer> getIds() {
        return new ArrayList<Integer>(stages.keySet());
    }

    /**
     * Register a rider's results at each checkpoint.
     *
     * @param riderId the ID of the rider
     * @param times   an array of times in the form [start, checkpoint1,
     *                checkpoint2, ..., finish]
     * @throws DuplicatedResultException       if the rider has already registered a
     *                                         result
     * @throws InvalidCheckpointTimesException if the number of times given ≠ the
     *                                         number of checkpoints + 2
     * @throws InvalidStageStateException      if the stage is not fully set up
     */
    public void registerResults(int riderId, LocalTime... times)
            throws DuplicatedResultException, InvalidCheckpointTimesException, InvalidStageStateException {

        // Check stage is fully set up
        if (!prepared) {
            throw new InvalidStageStateException("Stage not prepared");
        }

        // Check if the rider has already registered a results
        if (startTimes.containsKey(riderId) || finishTimes.containsKey(riderId)) {
            throw new DuplicatedResultException("Rider ID already has a finish time");
        }
        //System.out.println("Checkpoint size: " + checkpoints.size()+ " times size: " + times.length);
        // Check the number of times is correct
        if (times.length != checkpoints.size() + 2) {
            throw new InvalidCheckpointTimesException("Number of times given is not number of checkpoints + 2");
        }

        // Must be done before any changes are made
        for (int i = 0; i < times.length - 1; i++) {
            if (times[i].isAfter(times[i + 1])) {
                throw new InvalidCheckpointTimesException("Checkpoint times are not in order");
            }
        }

        startTimes.put(riderId, times[0]);
        finishTimes.put(riderId, times[times.length - 1]);

        // TODO this code for adding times will fail if its a time trial type (i think),
        // will add a check for that when we add them

        // Consider that the array is [start, checkpoint1, checkpoint2, ..., finish]
        // The nth checkpoint starts at index n and ends at index n+1
        for (int i = 1; i < times.length - 2; i++) {
            try {
                Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpoints.get(i));
                checkpoint.recordTime(riderId, times[i]);

            }
            catch (IDNotRecognisedException e) {
                // will never happen as we are iterating through a list of already validated checkpoints
            }
        }

    }

    /**
     * Adds a checkpoint to the stage.
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
     * Get the sprint points for a rider.
     * Sprint points are calculated based on the rider's position at the end of the
     * stage and the type of stage. Sprint points from checkpoints are added to
     * this.
     *
     * @param riderId the ID of the rider to calculate the sprint points for
     * @return the number of sprint points the rider gets for this stage
     * @throws IDNotRecognisedException if the rider ID is not recognised
     */
    public int getSprintPoints(int riderId) throws IDNotRecognisedException {
        // TODO this should throw an error if the rider isn't in the system,
        // If its only not in the stage then it should return empty array.
        // check if the rider has a start and finish time recorded
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        // Calculate the rider's position at the end of the stage by counting the number
        // of riders who finished before them
        int position = 1;
        for (int id : finishTimes.keySet()) {
            if (id != riderId && getElapsedTime(id).isBefore(getElapsedTime(riderId))) {
                position++;
            }
        }

        int[] pointsArray = POINTS.get(type);

        // Assume the rider gets 0 points if they finish outside the top 15
        // TODO check this with diogo
        int points = (position >= pointsArray.length) ? 0 : pointsArray[position - 1];

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
     * @throws IDNotRecognisedException if the rider ID is not recognised
     */
    public int getMountainPoints(int riderId) throws IDNotRecognisedException {

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
     *
     * @throws InvalidStageStateException if the stage is already prepared
     */
    public void concludePreparation() throws InvalidStageStateException {
        if (prepared) {
            throw new InvalidStageStateException("Stage already prepared");
        }

        prepared = true;
    }

    /**
     * Remove a checkpoint from the stage's list of checkpoints.
     *
     * @param checkpointId the ID of the checkpoint to remove
     */
    public void removeCheckpoint(int checkpointId) throws InvalidStageStateException {
        if (prepared)
            throw new InvalidStageStateException("Stage already prepared");
        checkpoints.remove(checkpointId);
    }


    /**
     * Removes all of a rider's results from the stage and its checkpoints.
     *
     * @param riderId the ID of the rider to remove
     * @throws IDNotRecognisedException if the rider ID is not recognised
     */
    public void removeRider(int riderId) throws IDNotRecognisedException {
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        startTimes.remove(riderId);
        finishTimes.remove(riderId);

        for (int checkpointId : checkpoints) {
            Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
            checkpoint.removeRider(riderId);
        }
    }

    /**
     * Deletes this stage and its associated checkpoints.
     *
     * @throws IDNotRecognisedException if the stage ID is not recognised
     */
    public void delete() throws IDNotRecognisedException {
        if (!stages.containsKey(myId)) {
            throw new IDNotRecognisedException("Stage ID not recognised");
        }

        stages.remove(myId); // Remove from the dictionary of stages

        for (int checkpointId : checkpoints) {
            checkpoints.remove(checkpointId);
            Checkpoint.removeFromHashmap(checkpointId);
        }

        // remove this stage from the parent
        Race.getRaceById(parentRaceId).removeStage(myId);
    }

    /**
     * Deletes a checkpoint from the stage.
     *
     * @param checkpointId the ID of the checkpoint to delete
     * @throws InvalidStageStateException if the stage is already prepared
     * @throws IDNotRecognisedException  if the checkpoint ID is not recognised
     */
    public void deleteCheckpoint(int checkpointId) throws InvalidStageStateException, IDNotRecognisedException {
        if (prepared) {
            throw new InvalidStageStateException("Stage already prepared");
        }

        if (!checkpoints.contains(checkpointId)) {
            throw new IDNotRecognisedException("Checkpoint ID not recognised");
        }

        checkpoints.remove(checkpointId);
        Checkpoint.removeFromHashmap(checkpointId);
    }

    /**
     * Gets a rider's elapsed time for the stage.
     *
     * @param riderId the ID of the rider to get the elapsed time for
     * @return the elapsed time
     * @throws IDNotRecognisedException if the rider ID is not recognised
     */
    private LocalTime getElapsedTime(int riderId)  {

        return LocalTime
                .ofNanoOfDay(finishTimes.get(riderId).toNanoOfDay() - startTimes.get(riderId).toNanoOfDay());
    }

    /**
     * Gets a rider's adjusted elapsed time for the stage.
     * Adjusted time recursively gives riders that finish within 1 second of each other the same time.
     *
     * @param riderId the ID of the rider to get the adjusted time for
     * @return the adjusted time
     * @throws IDNotRecognisedException if the rider ID is not recognised
     */
    public LocalTime getAdjustedElapsedTime(int riderId) throws IDNotRecognisedException {
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        LocalTime adjustedElapsedTime = getElapsedTime(riderId);

        // Repeat until no more adjustments are made
        // There might be a better way to do this by decreasing the elapsed time by 1 second at a time until it's outside the range
        boolean adjusted = true;
		while (adjusted) {
			adjusted = false;

			// Iterate through all registered riders in the stage
			for (int otherRiderID : getRegisteredRiders()) {
				if (otherRiderID != riderId) {
					// Calculate the time difference between the current rider and other riders

                    // TODO check if spec says to combine times <= or < 1 second
					float difference = (float) (getElapsedTime(otherRiderID).toNanoOfDay() -
                            adjustedElapsedTime.toNanoOfDay()) /1000000000; // nanoseconds to seconds

					// Adjust the elapsed time if the difference is within the range [-1, 0)
					if (-1 < difference && difference < 0) {
						adjustedElapsedTime = getElapsedTime(otherRiderID);
						adjusted = true;
					}
				}
			}
		}

        return adjustedElapsedTime;
    }

    /**
     * Gets a rider's results for the stage, that is, an array of finish times for
     * each checkpoint and elapsed time.
     *
     * @param riderId the ID of the rider to get the results for
     * @return an array in the form [checkpoint1, checkpoint2, ..., finish]
     * @throws IDNotRecognisedException if the rider ID is not recognised
     */
    public LocalTime[] getResults(int riderId) throws IDNotRecognisedException {
        if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
            throw new IDNotRecognisedException("Rider ID not recognised");
        }

        LocalTime[] results = new LocalTime[checkpoints.size() + 1];

        // Set the last element to the elapsed time
        results[results.length - 1] = getElapsedTime(riderId);

        for (int i = 0; i < results.length - 2; i++) {
            results[i] = Checkpoint.getCheckpointById(checkpoints.get(i)).getPassTime(riderId);
        }

        return results;
    }

    /**
     * Getter for the Stage ID.
     *
     * @return the Id of the parent Race
     */
    public int getId() {
        return myId;
    }

    /**
     * Getter for the name of this stage.
     *
     * @return the Stage name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the description of this stage.
     *
     * @return the stage description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the name of this stage.
     *
     * @return the Stage name
     */
    public StageType getType() {
        return type;
    }

    /**
     * Getter for the length of this stage.
     *
     * @return the stage length
     */
    public double getLength() {
        return length;
    }

    /**
     * Getter for the checkpoints of this stage.
     *
     * @return the stage checkpoints
     */
    public ArrayList<Integer> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Getter for whether the stage is prepared.
     *
     * @return boolean indicating whether or not the stage is prepared
     */
    public boolean isPrepared() {
        return prepared;
    }

    /**
     * Getter for registered riders in the stage.
     *
     * @return the registered riders
     */
    public ArrayList<Integer> getRegisteredRiders() {
        return new ArrayList<Integer>(finishTimes.keySet());
    }

    /**
     * Get the riders (as IDs) that are a part of the queried stage ordered by their
     * GC time
     *
     * @return The IDs of the riders sorted by elapsed time
     */
    public int[] getRidersRankInStage(){
        Function<Integer,LocalTime> func = this::getElapsedTime;
        PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false,
                new ArrayList<>(List.of(myId)));
        return pointsHandler.getRiderRanks();

    }

    /**
     * Get the adjusted elapsed times of riders in a stage.
     *
     * @return The ranked list of adjusted elapsed times sorted by their finish
     *         time.
     */
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(){
        Function<Integer,LocalTime> func = this::getElapsedTime;
        PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false,
                new ArrayList<>(List.of(myId)));
        return pointsHandler.getRiderTimes();
    }
}
