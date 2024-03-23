package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Class to represent an entire Race in the system
 *
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
public class Race extends Entity {
    private final String name;
    private final String description;
    private final HashMap<Integer, Stage> stages = new HashMap<>(); // Hashmap of the stages belonging to this race

    /**
     * Constructor for the Race class.
     *
     * @param name        Name of the race
     * @param description Description of the race
     * @throws InvalidNameException When the name is empty/null, too long/short, or
     *                              contains whitespace
     */
    public Race(String name, String description) throws InvalidNameException {
        super(); // Call the entity constructor

        // Check for invalid (rule breaking) name
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            freeId(); // as super() has been called, we need to free the ID
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up attributes for the object
        this.name = name;
        this.description = description;
    }

    /**
     * Getter for the Race's name.
     *
     * @return The name of the Race instance
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a short text description of the race.
     * Format: "Name: [name], Description: [description], Number of stages: [number
     * of
     * stages], Total length: [total length]".
     *
     * @return The formatted descriptor string
     */
    public String getDetails() {
        int numStages = stages.size();

        double length = 0.0; // sum up the lengths of all stages
        for (Stage stage : stages.values()) {
            length += stage.getLength();
        }

        return String.format("Name: %s, Description: %s, Number of stages: %d, Total length: %.2f",
                name, description, numStages, length);
    }

    /**
     * Gets a short description of the race class.
     *
     * @return The descriptor string
     */
    @Override
    public String toString() {
        return "Race Class " + getDetails();
    }

    @Override
    public void remove() {
        // This cannot be done with a for each loop as it throws concurrent modification
        // exception

        while (!stages.isEmpty()) {
              new ArrayList<>(stages.values()).get(0).remove();
        }

        freeId();
    }

    /**
     * Adds a stage to the list of stages that belong to this race.
     *
     * @param stage The stage to add to the list
     */
    public void addStage(Stage stage) {
        int stagesBefore = stages.size();
        stages.put(stage.getId(), stage);
        // Check that the stage was added to the list
        assert stages.size() == stagesBefore + 1 : "Stage was not added to the list";
    }

    /**
     * Removes a stage from the list of stages that belong to this race.
     * Does not delete the stage, just removes it from the list.
     *
     * @param stageId The stage ID to remove from the list
     */
    public void removeStage(int stageId) {
        int stagesBefore = stages.size();
        stages.remove((Integer) stageId);
        // assert that the stage was removed from the list
        assert stages.size() == stagesBefore - 1 : "Stage was not removed from the list";
    }

    /**
     * Getter for the list of stage IDs that belong to this Race.
     *
     * @return The stage IDs belonging to this race
     */
    public HashMap<Integer, Stage> getStages() {
        return stages;
    }

    /**
     * Gets the general classification times for all riders in the race ordered by
     * time.
     *
     * @return An array of Localtimes of the riders' general classification times
     */
    public LocalTime[] getRidersGeneralClassificationTimes() {
        Function<Integer, LocalTime> func = this::getRiderGeneralClassificationTime;
        PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false,
                new ArrayList<Stage>(stages.values()));
        return pointsHandler.getRiderTimes();
    }

    /**
     * Gets the general classification ranks for all riders in the race.
     *
     * @return The ordered riderIds of who came 1st 2nd etc
     */
    public int[] getRidersGeneralClassificationRanks() {
        Function<Integer, LocalTime> func = this::getRiderGeneralClassificationTime;
        PointsHandler<LocalTime> pointsHandler = new PointsHandler<LocalTime>(func, false,
                new ArrayList<Stage>(stages.values()));

        return pointsHandler.getRiderRanks();
    }

    /**
     * Gets the general classification time for a rider across all race stages.
     *
     * @param riderId The riderID to check
     * @return The total time for the rider across all stages
     */
    private LocalTime getRiderGeneralClassificationTime(int riderId) {
        LocalTime summedTime = LocalTime.of(0, 0, 0);

        for (Stage stage : stages.values()) {
            if (!stage.getPrepared()) {
                continue;
            }

            try {
                LocalTime stageTime = stage.getAdjustedElapsedTime(riderId);
                summedTime = summedTime.plusHours(stageTime.getHour())
                        .plusMinutes(stageTime.getMinute())
                        .plusSeconds(stageTime.getSecond())
                        .plusNanos(stageTime.getNano());
            } catch (IDNotRecognisedException e) {
                // This comes up if a rider isn't in the stage so just do nothing
            }
        }

        return summedTime;
    }

    /**
     * Gets the mountain points for all riders ordered by their GC time.
     *
     * @return An array of the mountain points for each rider ordered by their GC
     *         time
     */
    public int[] getRidersMountainPoints() {
        int[] ridersGeneralClassificationRanks = getRidersGeneralClassificationRanks();
        int[] ridersMountainPoints = new int[ridersGeneralClassificationRanks.length];

        for (int i = 0; i < ridersGeneralClassificationRanks.length; i++) {
            ridersMountainPoints[i] = getRiderMountainPoints(ridersGeneralClassificationRanks[i]);
        }

        return ridersMountainPoints;
    }

    /**
     * Gets the rankings of the riders considering their mountain points.
     *
     * @return An int array of the riderIds ordered by their mountain points
     */
    public int[] getRidersMountainPointsRankings() {
        Function<Integer, Integer> func = this::getRiderMountainPoints;
        PointsHandler<Integer> pointsHandler = new PointsHandler<Integer>(func, true,
                new ArrayList<Stage>(stages.values()));

        return pointsHandler.getRiderRanks();
    }

    /**
     * Gets the rankings of the riders considering their sprint points.
     *
     * @return An int array of the riderIds ordered by their sprint points
     */
    public int[] getRidersSprintPointsRankings() {
        Function<Integer, Integer> func = this::getRiderSprintPoints;
        PointsHandler<Integer> pointsHandler = new PointsHandler<Integer>(func, true,
                new ArrayList<Stage>(stages.values()));

        return pointsHandler.getRiderRanks();
    }

    /**
     * Gets the sprint points for all riders ordered by their GC time.
     *
     * @return An int array of the sprint points for each rider ordered by their GC time
     */
    public int[] getRidersSprintPoints() {
        int[] ridersGeneralClassificationRanks = getRidersGeneralClassificationRanks();
        int[] ridersSprintPoints = new int[ridersGeneralClassificationRanks.length];

        for (int i = 0; i < ridersGeneralClassificationRanks.length; i++) {
            ridersSprintPoints[i] = getRiderSprintPoints(ridersGeneralClassificationRanks[i]);
        }

        return ridersSprintPoints;
    }

    /**
     * Get the mountain points for a rider across all stages.
     *
     * @param riderId The rider to get mountain points for
     * @return The sum of the mountain points for the rider
     */
    private int getRiderMountainPoints(int riderId) {
        int pointSum = 0;
        for (Stage stage : stages.values()) {
            try {
                pointSum += stage.getMountainPoints(riderId);
            } catch (IDNotRecognisedException e) {
                // Should never happen since we are iterating through the list of stageIds
                // Already validated
                assert false : "Stage ID not recognised";
            }
        }
        return pointSum;
    }

    /**
     * Get the sprint points for a rider across all stages.
     *
     * @param riderId The rider to get sprint points for
     * @return The sum of the sprint points for the rider
     */
    private int getRiderSprintPoints(int riderId) {
        int pointSum = 0;

        for (Stage stage : stages.values()) {
            try {
                pointSum += stage.getSprintPoints(riderId);
            } catch (IDNotRecognisedException e) {
                // Should never happen since we are iterating through the list of stageIds
                // Already validated
                assert false : "Stage ID not recognised";
            }
        }
        
        return pointSum;
    }
}
