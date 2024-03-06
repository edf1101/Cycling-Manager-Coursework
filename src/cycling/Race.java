package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Class to represent an entire Race in the system
 *
 * @author 730003140 & 730002704
 * @version 1.0
 */
public class Race {

    private static final HashMap<Integer, Race> races = new HashMap<Integer, Race>();
    private final int myId;
    private final String name;
    private final String description;
    private final ArrayList<Integer> stageIds = new ArrayList<Integer>(); // list of the stages belonging to this race

    /**
     * Constructor for the Race class
     *
     * @param name        Name of the race
     * @param description Description of the race
     * @throws InvalidNameException When the name is empty/null, too long/short, or contains whitespace
     */
    public Race(String name, String description) throws InvalidNameException {
        // Check for invalid (rule breaking) name
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up attributes for the object
        this.myId = UniqueIdGenerator.calculateUniqueId(races);
        this.name = name;
        this.description = description;

        // add the new object to the hashmap of all races
        races.put(this.myId, this);
    }

    /**
     * Pushes a race into the system.
     *
     * @param id the ID of the race to add
     * @param race the race object to add
     */
    public static void pushRace(int id, Race race) {
        races.put(id, race);
    }

    /**
     * Getter for a race by its ID
     *
     * @param id the ID to query
     * @return The race object reference
     */
    public static Race getRaceById(int id) {
        return races.get(id);
    }

    /**
     * Getter for the Race's name
     *
     * @return The name of the Race instance
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the Id attribute on this Race instance
     *
     * @return the Id
     */
    public int getId() {
        return myId;
    }

    /**
     * Gets a short text description of the race including:
     * race Id, name, description, the number of stages, and the total length
     *
     * @return The formatted descriptor string
     */
    public String getDetails() {
        int numStages = stageIds.size();
        double length = 0.0;
        return String.format("Name: %s, Description: %s, Number of stages: %d, Total length: %.2f",
                name, description, numStages, length);
    }

    /**
     * Gets a short description of the race class
     *
     * @return The descriptor string
     */
    @Override
    public String toString() {
        return "Race Class " + getDetails();
    }

    /**
     * Deletes this Race, cascading down to delete all stages and checkpoints
     */
    public void delete() {
        // NOTE: this cannot be done with a for each loop as it throws concurrent modification exception
        for (int stageIndex =0; stageIndex < stageIds.size(); stageIndex++) {
            try {
                Stage.getStageById(stageIds.get(stageIndex)).delete();
            } catch (IDNotRecognisedException e) {
                // Should never happen as we are iterating through the list of stageIds
                // which are valid and checked
            }
        }

        // No need to remove stages from list as they are already deleted
        races.remove(myId);
    }

    /**
     * Adds a stage to the list of stages that belong to this race
     *
     * @param stageId the stage ID to add to the list
     */
    public void addStage(int stageId) {
        stageIds.add(stageId);
    }

    /**
     * Deletes a stage
     *
     * @param stageId the stage ID to delete
     * @throws IDNotRecognisedException When the stage ID does not exist in the system
     */
    public void deleteStage(int stageId) throws IDNotRecognisedException {
        if (!stageIds.contains(stageId)) {
            throw new IDNotRecognisedException("The stage ID: " + stageId + ", does not exist in this system");
        }

        Stage.getStageById(stageId).delete();
        stageIds.remove(Integer.valueOf(stageId));
    }

    /**
     * Removes a stage to the list of stages that belong to this race
     * Does not delete the stage, just removes it from the list
     *
     * @param stageId the stage ID to remove from the list
     */
    public void removeStage(int stageId) {
        stageIds.remove(Integer.valueOf(stageId));
    }

    /**
     * Getter for the list of stage IDs that belong to this Race
     *
     * @return The stage IDs belonging to this race
     */
    public ArrayList<Integer> getStageIds() {
        return stageIds;
    }

    /**
     * Gets the Id of a Race by its name
     *
     * @param name       The name of the race to find an Id for.
     * @param portalsIds the list of the raceIds that are contained by this portal
     *                   instance, so we don't mix up this
     *                   with another portals race with a same name
     * @return The id of the race with the given name (in the context of the portal)
     * @throws NameNotRecognisedException When the name has not been found in the
     *                                    system
     */
    public static int getIdByName(String name, ArrayList<Integer> portalsIds) throws NameNotRecognisedException {
        // Check the name exists in this system by iterating through
        // all raceIds in system and checking name
        boolean foundName = false;
        int foundId = 0;
        for (int raceId : portalsIds) {
            if (Race.getRaceById(raceId).getName().equals(name)) {
                foundName = true;
                foundId = raceId;
                break;
            }
        }
        if (!foundName) {
            throw new NameNotRecognisedException("The Race name: " + name + ", does not exist in this system");
        }
        return foundId;
    }

    /**
     * Gets the general classification times for all riders in the race ordered by time
     *
     * @return an array of Localtimes of the riders' general classification times
     */
    public LocalTime[] getRidersGeneralClassificationTimes() {
        Function<Integer,LocalTime> func = this::getRiderGeneralClassificationTime;
        PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false, stageIds);
        return pointsHandler.getRiderTimes();
    }

    /**
     * Gets the general classification ranks for all riders in the race
     *
     * @return the ordered riderIds of who came 1st 2nd etc
     */
    public int[] getRidersGeneralClassificationRanks() {
        Function<Integer,LocalTime> func = this::getRiderGeneralClassificationTime;
        PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false, stageIds);
        return pointsHandler.getRiderRanks();

    }

    /**
     * Gets the general classification time for a rider across all race stages
     *
     * @param riderId the riderID to check
     * @return the total time for the rider across all stages
     */
    private LocalTime getRiderGeneralClassificationTime(int riderId) {
        LocalTime summedTime = LocalTime.of(0, 0, 0);
        for(int stageId : stageIds){
            try {
                LocalTime stageTime = Stage.getStageById(stageId).getAdjustedElapsedTime(riderId);
                summedTime = summedTime.plusHours(stageTime.getHour())
                        .plusMinutes(stageTime.getMinute())
                        .plusSeconds(stageTime.getSecond())
                        .plusNanos(stageTime.getNano());

            } catch (IDNotRecognisedException e) {
                // Should never happen since we are iterating through the list of stageIds already validated
                assert(false) : "Stage ID not recognised"; // assertion so we can see if it happened
            }
        }
        return summedTime;
    }

    /**
     * Gets the mountain points for all riders ordered by their GC time
     *
     * @return an array of the mountain points for each rider ordered by their GC time
     */
    public int[] getRidersMountainPoints() {
        int[] ridersGeneralClassificationRanks = getRidersGeneralClassificationRanks();
        int[] ridersMountainPoints = new int[ridersGeneralClassificationRanks.length];
        for(int i = 0; i < ridersGeneralClassificationRanks.length; i++){
            ridersMountainPoints[i] = getRiderMountainPoints(ridersGeneralClassificationRanks[i]);
        }
        return ridersMountainPoints;
    }

    /**
     * Gets the rankings of the riders considering their mountain points
     *
     * @return an array of the riderIds ordered by their mountain points
     */
    public int[] getRidersMountainPointsRankings() {
        Function<Integer,Integer> func = this::getRiderMountainPoints;
        PointsHandler<Integer> pointsHandler = new PointsHandler<Integer>(func, true, stageIds);
        return pointsHandler.getRiderRanks();
    }

    /**
     * Gets the rankings of the riders considering their sprint points
     *
     * @return an array of the riderIds ordered by their sprint points
     */
    public int[] getRidersSprintPointsRankings() {
        Function<Integer,Integer> func = this::getRiderSprintPoints;
        PointsHandler<Integer> pointsHandler = new PointsHandler<Integer>(func, true, stageIds);
        return pointsHandler.getRiderRanks();
    }

    /**
     * Gets the sprint points for all riders ordered by their GC time
     *
     * @return an array of the sprint points for each rider ordered by their GC time
     */
    public int[] getRidersSprintPoints() {
        int[] ridersGeneralClassificationRanks = getRidersGeneralClassificationRanks();
        int[] ridersSprintPoints = new int[ridersGeneralClassificationRanks.length];
        for(int i = 0; i < ridersGeneralClassificationRanks.length; i++){
            ridersSprintPoints[i] = getRiderSprintPoints(ridersGeneralClassificationRanks[i]);
        }
        return ridersSprintPoints;
    }

    /**
     * Get the mountain points for a rider across all stages
     *
     * @param riderId the rider to get mountain points for
     * @return the sum of the mountain points for the rider
     */
    private int getRiderMountainPoints(int riderId) {
        int pointSum = 0;
        for(int stageId:stageIds){
            try {
                pointSum += Stage.getStageById(stageId).getMountainPoints(riderId);
            }
            catch (IDNotRecognisedException e){
                // Should never happen since we are iterating through the list of stageIds already validated
                assert(false) : "Stage ID not recognised"; // assertion so we can see if it happened
            }
        }
        return pointSum;
    }

    /**
     * Get the sprint points for a rider across all stages
     *
     * @param riderId the rider to get sprint points for
     * @return the sum of the sprint points for the rider
     */
    private int getRiderSprintPoints(int riderId) {
        int pointSum = 0;
        for(int stageId:stageIds){
            try {
                pointSum += Stage.getStageById(stageId).getSprintPoints(riderId);
            }
            catch (IDNotRecognisedException e){
                // Should never happen since we are iterating through the list of stageIds already validated
                assert(false) : "Stage ID not recognised"; // assertion so we can see if it happened
            }
        }
        return pointSum;
    }

}
