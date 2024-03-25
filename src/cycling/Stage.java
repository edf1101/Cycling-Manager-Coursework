package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Class to represent a stage in the staged bike race.
 *
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
class Stage extends Entity {

  // Points for each stage type
  private static final HashMap<StageType, int[]> POINTS = new HashMap<StageType, int[]>();

  static {
    POINTS.put(StageType.FLAT,
        new int[]{50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2});
    POINTS.put(StageType.MEDIUM_MOUNTAIN,
        new int[]{30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2});
    POINTS.put(StageType.HIGH_MOUNTAIN,
        new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1});
    POINTS.put(StageType.TT,
        new int[]{20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1});
  }

  private final String name;
  private final String description;

  private final StageType type;
  private final double length;
  private final ArrayList<Integer> checkpointOrder = new ArrayList<Integer>();
  private final HashMap<Integer, Checkpoint> myCheckpoints = new HashMap<Integer, Checkpoint>();
  private boolean prepared = false;
  private final LocalDateTime startStageTime; // The time the stage starts
  private final Race parentRace;

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
   * @param startTime   The time the stage starts
   * @param parentRace  The parent race that this stage belongs to
   * @throws InvalidNameException   if the name is not between 0 and 30 chars or
   *                                contains whitespace
   * @throws InvalidLengthException if the length is less than 5km
   */
  protected Stage(String name, String description, StageType type, double length,
                  LocalDateTime startTime, Race parentRace)
      throws InvalidNameException, InvalidLengthException {
    super(); // Call the entity constructor

    // Check name is not null, empty or >30 chars
    if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
      freeId(); // as super() has been called, we need to free the ID
      throw new InvalidNameException(" name broke naming rules. Length must be between 0 and 30,"
          + " and no whitespace");
    }

    // Check length is not less than 5km
    if (length < 5) {
      freeId(); // as super() has been called, we need to free the ID
      throw new InvalidLengthException(" length broke rules. Length must be >= 5");
    }

    // Set up attributes for the object
    this.name = name;
    this.description = description;
    this.type = type;
    this.length = length;
    this.startStageTime = startTime;
    this.prepared = false;
    this.parentRace = parentRace;
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
  protected void registerResults(int riderId, LocalTime... times)
      throws DuplicatedResultException, InvalidCheckpointTimesException,
      InvalidStageStateException {

    // Check stage is fully set up
    if (!prepared) {

      throw new InvalidStageStateException("Stage not prepared");
    }

    // Check if the rider has already registered a results
    if (startTimes.containsKey(riderId) || finishTimes.containsKey(riderId)) {
      throw new DuplicatedResultException("Rider ID already has a finish time");
    }

    // Check the number of times is correct
    if (times.length != myCheckpoints.size() + 2) {
      throw new InvalidCheckpointTimesException("Number of times given is not"
          + " number of checkpoints + 2");
    }

    // Must be done before any changes are made
    for (int i = 0; i < times.length - 1; i++) {
      if (times[i].isAfter(times[i + 1])) {
        throw new InvalidCheckpointTimesException("Checkpoint times are not in order");
      }
    }

    startTimes.put(riderId, times[0]);
    finishTimes.put(riderId, times[times.length - 1]);
    assert startTimes.size() == finishTimes.size() : "Start & finish times should be same size";

    if (type == StageType.TT) {
      // If the stage is a time trial, then the rider's time is the time they
      // finished the stage
      return;
    }

    // Consider that the array is [start, checkpoint1, checkpoint2, ..., finish]
    // The nth checkpoint starts at index n and ends at index n+1
    for (int i = 0; i < times.length - 2; i++) {
      Checkpoint checkpoint = myCheckpoints.get(checkpointOrder.get(i));
      checkpoint.recordTime(riderId, times[i + 1]);

    }

  }

  /**
   * Adds a checkpoint to the stage.
   *
   * @param checkpoint the checkpoint object to add
   */
  protected void addCheckpoint(Checkpoint checkpoint) {

    // assert that the stage is not prepared
    assert !prepared : "Stage should not be prepared when adding a checkpoint";
    int checkpointCount = myCheckpoints.size();

    checkpointOrder.add(checkpoint.getId());
    myCheckpoints.put(checkpoint.getId(), checkpoint);
    // assert that the checkpoint was added to the list
    assert myCheckpoints.size() == checkpointCount + 1 : "Checkpoint was not added to the list";
    assert checkpointOrder.size() == checkpointCount + 1 : "Checkpoint was not added to the list";

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
  protected int getSprintPoints(int riderId) throws IDNotRecognisedException {
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
    int points = (position >= pointsArray.length) ? 0 : pointsArray[position - 1];

    // Add the points from the checkpoints for intermediate sprints
    for (Checkpoint checkpoint : myCheckpoints.values()) {
      assert type != StageType.TT : "TT stage should not have intermediate sprints";
      points += checkpoint.getIntermediateSprintPoints(riderId);
    }

    return points;
  }

  /**
   * Get the sprint points for an array of riders.
   *
   * @param orderedRiders the array of riders to calculate the sprint points for.
   * @return an array of the number of sprint points each rider gets for this
   *        stage.
   * @throws IDNotRecognisedException if a rider ID is not recognised.
   */
  protected int[] getRidersSprintPoints(int[] orderedRiders) throws IDNotRecognisedException {
    int[] sprintPoints = new int[orderedRiders.length];
    for (int index = 0; index < orderedRiders.length; index++) {
      sprintPoints[index] = getSprintPoints(orderedRiders[index]);
    }

    assert sprintPoints.length == orderedRiders.length
        : "Sprint points array should be the same length as the riders array";
    return sprintPoints;
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
  protected int getMountainPoints(int riderId) throws IDNotRecognisedException {

    // check if the rider has a start and finish time recorded
    if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
      throw new IDNotRecognisedException("Rider ID not recognised");
    }

    int points = 0;

    for (Checkpoint checkpoint : myCheckpoints.values()) {
      points += checkpoint.getMountainPoints(riderId);
    }

    return points;
  }

  /**
   * Conclude the preparation of the stage.
   *
   * @throws InvalidStageStateException if the stage is already prepared
   */
  protected void concludePreparation() throws InvalidStageStateException {
    if (prepared) {
      throw new InvalidStageStateException("Stage already prepared");
    }

    prepared = true;
  }

  /**
   * Remove a checkpoint from the stage's list of checkpoints.
   *
   * @param checkpointId the ID of the checkpoint to remove
   * @throws InvalidStageStateException if the stage is already prepared
   */
  protected void removeCheckpoint(int checkpointId) throws InvalidStageStateException {
    if (prepared) {
      throw new InvalidStageStateException("Stage already prepared");
    }

    int checkpointCount = myCheckpoints.size();
    myCheckpoints.remove(checkpointId);
    checkpointOrder.remove(Integer.valueOf(checkpointId));

    assert myCheckpoints.size() == checkpointCount - 1 : "Checkpoint was not removed from list";
    assert checkpointOrder.size() == checkpointCount - 1 : "Checkpoint was not removed from  list";
  }

  /**
   * Removes all of a rider's results from the stage and its checkpoints.
   *
   * @param riderId the ID of the rider to remove
   * @throws IDNotRecognisedException if the rider ID is not recognised
   */
  protected void removeRider(int riderId) throws IDNotRecognisedException {
    if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
      throw new IDNotRecognisedException("Rider ID not recognised");
    }

    assert startTimes.containsKey(riderId) : "Rider ID not found in start times";

    int startTimesBefore = startTimes.size();
    startTimes.remove(riderId);
    finishTimes.remove(riderId);

    assert startTimes.size() == startTimesBefore - 1 : "Rider not removed from start times";
    assert startTimes.size() == finishTimes.size() : "Start and finish times should be same size";

    for (Checkpoint checkpoint : myCheckpoints.values()) {
      checkpoint.removeRider(riderId);
    }
  }

  @Override
  protected void remove() {
    freeId(); // Remove the ID from the list of used IDs

    // has to be like this so concurrent modification exception is not thrown
    while (!myCheckpoints.isEmpty()) {
      // and the checkpoints need to be fetched like this so Java 8 compiles.
      myCheckpoints.get(myCheckpoints.keySet().toArray()[0]).remove();
      myCheckpoints.remove(myCheckpoints.keySet().toArray()[0]);
    }

    // assert all checkpoints removal
    assert myCheckpoints.isEmpty() : "stage's checkpoints should be empty after removing stage";
    parentRace.removeStage(id); // Remove this stage from the parent
  }

  /**
   * Gets a rider's elapsed time for the stage.
   *
   * @param riderId the ID of the rider to get the elapsed time for
   * @return the elapsed time
   */
  private LocalTime getElapsedTime(int riderId) {
    return LocalTime
        .ofNanoOfDay(finishTimes.get(riderId).toNanoOfDay()
            - startTimes.get(riderId).toNanoOfDay());
  }

  /**
   * Gets a rider's adjusted elapsed time for the stage.
   * Adjusted time recursively gives riders that finish within 1 second of each
   * other the same time.
   *
   * @param riderId the ID of the rider to get the adjusted time for
   * @return the adjusted time
   * @throws IDNotRecognisedException if the rider ID is not recognised
   */
  protected LocalTime getAdjustedElapsedTime(int riderId) throws IDNotRecognisedException {
    if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
      throw new IDNotRecognisedException("Rider ID not recognised");
    }

    LocalTime adjustedElapsedTime = getElapsedTime(riderId);

    // Repeat until no more adjustments are made
    boolean adjusted = true;
    while (adjusted) {
      adjusted = false;

      // Iterate through all registered riders in the stage
      for (int otherRiderId : getRegisteredRiders()) {
        if (otherRiderId != riderId) {
          // Calculate the time difference between the current rider and other riders

          float difference = (float) (getElapsedTime(otherRiderId).toNanoOfDay()
              - adjustedElapsedTime.toNanoOfDay()) / 1000000000; // nanoseconds to seconds

          // Adjust the elapsed time if the difference is within the range [-1, 0)
          if (-1 < difference && difference < 0) {
            adjustedElapsedTime = getElapsedTime(otherRiderId);
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
  protected LocalTime[] getResults(int riderId) throws IDNotRecognisedException {
    if (!(startTimes.containsKey(riderId) && finishTimes.containsKey(riderId))) {
      throw new IDNotRecognisedException("Rider ID not recognised");
    }

    LocalTime[] results = new LocalTime[myCheckpoints.size() + 1];

    // Set the last element to the elapsed time
    results[results.length - 1] = getElapsedTime(riderId);

    for (int i = 0; i < results.length - 2; i++) {
      Checkpoint checkpoint = myCheckpoints.get(checkpointOrder.get(i));
      results[i] = checkpoint.getPassTime(riderId);
    }

    return results;
  }

  /**
   * Getter for the name of this stage.
   *
   * @return the Stage name
   */
  protected String getName() {
    return name;
  }

  /**
   * Getter for the name of this stage.
   *
   * @return the Stage name
   */
  protected StageType getType() {
    return type;
  }

  /**
   * Getter for the length of this stage.
   *
   * @return the stage length
   */
  protected double getLength() {
    // assert length was checked in the constructor
    assert length >= 5 : "Length should be greater than 5";

    return length;
  }

  /**
   * Getter for the checkpoints of this stage.
   *
   * @return the stage checkpoints
   */
  protected ArrayList<Checkpoint> getCheckpoints() {
    ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

    for (int checkpointId : checkpointOrder) {
      checkpoints.add(myCheckpoints.get(checkpointId));
    }

    return checkpoints;
  }

  /**
   * Getter for the checkpoint ids of this stage.
   *
   * @return the stage checkpoint ids
   */
  protected int[] getCheckpointIds() {
    int[] ids = new int[myCheckpoints.size()];
    int i = 0;
    for (int checkpointId : checkpointOrder) {
      ids[i] = myCheckpoints.get(checkpointId).getId();
      i += 1;
    }
    return ids;
  }

  /**
   * Getter for whether the stage is prepared.
   *
   * @return boolean indicating whether the stage is prepared
   */
  protected boolean getPrepared() {
    return prepared;
  }

  /**
   * Getter for registered riders in the stage.
   *
   * @return the registered riders
   */
  protected ArrayList<Integer> getRegisteredRiders() {
    return new ArrayList<Integer>(finishTimes.keySet());
  }

  /**
   * Get the riders (as IDs) that are a part of the queried stage ordered by their
   * GC time.
   *
   * @return The IDs of the riders sorted by elapsed time
   */
  protected int[] getRidersRankInStage() {
    Function<Integer, LocalTime> func = this::getElapsedTime;
    ArrayList<Stage> pointsStages = new ArrayList<Stage>();
    pointsStages.add(this);
    PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false,
        pointsStages);

    int[] riderRanks = pointsHandler.getRiderRanks();
    assert riderRanks.length == getRegisteredRiders().size()
        : "Rider ranks array should be the same length as the riders array";
    return riderRanks;

  }

  /**
   * Get the adjusted elapsed times of riders in a stage.
   *
   * @return The ranked list of adjusted elapsed times sorted by their finish
   *        time.
   */
  protected LocalTime[] getRankedAdjustedElapsedTimesInStage() {
    Function<Integer, LocalTime> func = this::getElapsedTime;
    ArrayList<Stage> pointsStages = new ArrayList<Stage>();
    pointsStages.add(this);
    PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false,
        pointsStages);

    LocalTime[] riderTimes = pointsHandler.getRiderTimes();
    assert riderTimes.length == getRegisteredRiders().size()
        : "Rider times array should be the same length as the riders array";
    return riderTimes;
  }

  /**
   * This function returns the mountain points for each rider a given array of
   * riderIds.
   *
   * @param orderedRiders a list of riderIds which will determine the order of the
   *                      output array
   * @return an array of mountain points for each rider.
   * @throws IDNotRecognisedException If any of the riders in the input array are
   *                                  not recognised
   */
  protected int[] getRidersMountainPoints(int[] orderedRiders) throws IDNotRecognisedException {
    // Get the ordered list of riders
    int[] mountainPoints = new int[orderedRiders.length];
    for (int index = 0; index < orderedRiders.length; index++) {
      mountainPoints[index] = getMountainPoints(orderedRiders[index]);
    }
    assert mountainPoints.length == orderedRiders.length
        : "Sprint points array should be the same length as the riders array";
    return mountainPoints;
  }

  /**
   * Get the details of the stage in a string form.
   *
   * @return A string describing the stage
   */
  @Override
  public String toString() {
    return "Stage: " + name + " Description: " + description + " Type: " + type
        + " Start time:" + startStageTime + " Length " + length + "km";
  }
}
