package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

// TODO list:
//  - put assertions into the src code
//  - given time trial is explicitly said that its the elapsed time not mass start,
//  for non TT should we use elapsed time
//  as starting from stage start time not rider start?
//  - Javadoc incorrect symbols > ...

/**
 * This class is the implementation of the CyclingPortal interface.
 * It is the only backend class that the frontend application can interact with.
 *
 * @author 730003140 & 730002704
 * @version 1.0
 */
public class CyclingPortalImpl implements CyclingPortal {

    // Lists of the various IDs that belong to this instance of CyclingPortalImpl
    private final HashMap<Integer, Race> myRaces = new HashMap<>();
    private final HashMap<Integer, Team> myTeams = new HashMap<>();

    // Class to encapsulate error checking functions
    private final ErrorChecker errorChecker = new ErrorChecker(this);

    /**
     * Method to get all the race IDs in the system.
     *
     * @return int array of race IDs
     */
    @Override
    public int[] getRaceIds() {
        // Convert the ArrayList of Integers to an array of ints and return it
        return myRaces.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Creates a Race Instance
     *
     * @param name        Race's name.
     * @param description Race's description (can be null).
     * @return The ID of the race created
     * @throws IllegalNameException When the name is already taken
     * @throws InvalidNameException When the name is null, empty, >30 chars, or
     *                              contains whitespace
     */
    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
        errorChecker.checkNameUnused(name, ErrorChecker.nameUnusedType.RACE); // Check the name is unused

        Race newRace = new Race(name, description); // Create instance
        myRaces.put(newRace.getId(), newRace); //

        return newRace.getId();
    }

    /**
     * Get the details from a race.
     * <p>
     * The state of this MiniCyclingPortal must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A string describing the race including:
     *         name, description, stage count, and total length
     * @throws IDNotRecognisedException If the ID does not match to any race in the
     *                                  system.
     */
    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race belongs to this system
        return getRaceById(raceId).getDetails();
    }

    /**
     * Removes a race by its ID
     *
     * @param raceId The ID of the race to be removed.
     * @throws IDNotRecognisedException Thrown if the ID is not recognised
     */
    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race belongs to this system

        // Delete the race and remove it from the list of races
        getRaceById(raceId).delete();
        myRaces.remove(Integer.valueOf(raceId));
    }

    /**
     * Get the number of stages in a queried race
     *
     * @param raceId The ID of the race being queried.
     * @return The number of stages in the race
     * @throws IDNotRecognisedException When the race ID is not in the system
     */
    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race belongs to this system
        return getRaceStages(raceId).length;
    }

    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
            StageType type)
            throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

        errorChecker.checkNameUnused(stageName, ErrorChecker.nameUnusedType.STAGE); // Check Stage name is Unique
        errorChecker.checkRaceBelongsToSystem(raceId); // Check Race matches system list of races

        // Create the stage and add it to the list of stage Ids and return Id.
        Stage newStage = new Stage(stageName, description, type, length, getRaceById(raceId));
        getRaceById(raceId).addStage(newStage);

        return newStage.getId();
    }

    /**
     * Get the stages present in a race
     *
     * @param raceId The ID of the race being queried.
     * @return The array of integer IDs in the race
     * @throws IDNotRecognisedException When the race ID is not in the system
     */
    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check the race exists in this system

        // Get the race instance
        Race myRace = getRaceById(raceId);
        // Convert its list of stage IDs to an array of ints and return it
        return myRace.getStages().keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Get the length of a stage
     *
     * @param stageId The ID of the stage being queried.
     * @return the length of a stage
     * @throws IDNotRecognisedException If the stage ID is not in the system
     */
    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        // Check the stage exists in this system
        errorChecker.checkStageBelongsToSystem(stageId);

        return getStage(stageId).getLength();
    }

    /**
     * Removes a stage by its ID
     *
     * @param stageId The ID of the stage to be removed.
     * @throws IDNotRecognisedException Thrown if the ID is not recognised
     */
    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        // Throw error if invalid stage id
        errorChecker.checkStageBelongsToSystem(stageId);

        getStage(stageId).delete();
    }

    /**
     * Add a categorized climb to a stage
     * <p>
     *
     * @param stageId         The ID of the stage to which the climb checkpoint is
     *                        being added.
     * @param location        The kilometre location where the climb finishes within
     *                        the stage.
     * @param type            The category of the climb - {@link CheckpointType#C4},
     *                        {@link CheckpointType#C3}, {@link CheckpointType#C2},
     *                        {@link CheckpointType#C1}, or
     *                        {@link CheckpointType#HC}.
     * @param averageGradient The average gradient for the climb.
     * @param length          The length of the climb in kilometre.
     * @return The ID of the newly created checkpoint
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidLocationException   If the location is out of bounds of the
     *                                    stage length.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     * @throws InvalidStageTypeException  Time-trial stages cannot contain any
     *                                    checkpoint.
     */
    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
            Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
            InvalidStageTypeException {
        errorChecker.checkStageBelongsToSystem(stageId); // Check if the system contains this stage

        Checkpoint newClimb = new Climb(type, location, length, averageGradient, getStage(stageId)); // Create the new
                                                                                                     // climb
        getStage(stageId).addCheckpoint(newClimb); // Add it to the parent stage's list of checkpoints
        return newClimb.getId();
    }

    /**
     * Add an intermediate sprint to a stage.
     *
     * @param stageId  The ID of the stage to which the intermediate sprint
     *                 checkpoint
     *                 is being added.
     * @param location The kilometre location where the intermediate sprint finishes
     *                 within the stage.
     * @return the Id of the newly created checkpoint
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidLocationException   If the location is out of bounds of the
     *                                    stage length.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     * @throws InvalidStageTypeException  Time-trial stages cannot contain any
     *                                    checkpoint.
     */
    @Override
    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
            InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        errorChecker.checkStageBelongsToSystem(stageId); // Check if the system contains this stage

        Checkpoint newInterSprint = new IntermediateSprint(location, getStage(stageId)); // Create the new climb
        getStage(stageId).addCheckpoint(newInterSprint); // Add it to the parent stage's list of
        // Checkpoints
        return newInterSprint.getId();
    }

    /**
     * Remove a checkpoint from a stage
     *
     * @param checkpointId The ID of the checkpoint to be removed.
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     */
    @Override
    public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
        Checkpoint check = getCheckpoint(checkpointId);
        check.getParentStage().removeCheckpoint(checkpointId);
    }

    /**
     * Concludes the preparation of a stage. After conclusion, the stage's state
     * should be "waiting for results".
     *
     * @param stageId The ID of the stage to be concluded.
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     */
    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
        errorChecker.checkStageBelongsToSystem(stageId);

        getStage(stageId).concludePreparation();
    }

    /**
     * Get the checkpoints present in a stage
     *
     * @param stageId The ID of the stage being queried.
     * @return The array of integer IDs in the stage
     * @throws IDNotRecognisedException If the stage Id is not part of this system
     */
    @Override
    public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
        errorChecker.checkStageBelongsToSystem(stageId); // Check if the system contains this stage
        // Convert the ArrayList of Integers to an array of ints and return it
        // return
        // Stage.getStageById(stageId).getCheckpointIds().stream().mapToInt(Integer::intValue).toArray();
        int[] ids = new int[getStage(stageId).getCheckpoints().size()];
        for (Checkpoint checkpoint : getStage(stageId).getCheckpoints()) {
            ids[checkpoint.getId()] = checkpoint.getId();
        }
        return ids;
    }

    /**
     * This function creates a team
     *
     * @param name        The identifier name of the team.
     * @param description A description of the team.
     * @return The ID of the newly created team
     * @throws IllegalNameException When you input a name that is already taken
     * @throws InvalidNameException When you input a name that breaks naming
     *                              convention rules
     */
    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
        errorChecker.checkNameUnused(name, ErrorChecker.nameUnusedType.TEAM); // Check for illegal name, already in use

        Team newTeam = new Team(name, description); // Create instance of the team
        int newId = newTeam.getId(); // The new ID for the created team
        myTeams.put(newTeam.getId(), newTeam);
        return newId;
    }

    /**
     * Remove a team from the system
     *
     * @param teamId The ID of the team to be removed.
     * @throws IDNotRecognisedException When teamID is not an ID for any of the
     *                                  systems teams
     */
    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {
        errorChecker.checkTeamBelongsToSystem(teamId); // Check the teamID exists in this system

        myTeams.get(teamId).remove(); // Remove the team from its own class
        myTeams.remove(Integer.valueOf(teamId)); // Remove it from the cycling portals list of associated teams
    }

    /**
     * Getter for the team IDs that belong to this instance of CyclingPortalImpl
     *
     * @return an array of the team IDs
     */
    @Override
    public int[] getTeams() {
        // Convert the ArrayList of Integers to an array of ints and return it
        return myTeams.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Get the riders (as IDs) that are a part of the queried team
     *
     * @param teamId The ID of the team being queried.
     * @return The IDs of the riders
     * @throws IDNotRecognisedException If the team ID is not part of the system.
     */
    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {

        errorChecker.checkTeamBelongsToSystem(teamId); // Check the teamID exists in this system
        return myTeams.get(teamId).getRiders().keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Create a rider and add them to a team
     *
     * @param teamId      The ID rider's team.
     * @param name        The name of the rider.
     * @param yearOfBirth The year of birth of the rider.
     * @return The ID of the newly created rider
     * @throws IDNotRecognisedException When teamID is not an ID for any of the
     *                                  systems teams
     * @throws IllegalArgumentException When the name is empty or null, or the year
     *                                  of birth is less than 1900
     */
    @Override
    public int createRider(int teamId, String name, int yearOfBirth)
            throws IDNotRecognisedException, IllegalArgumentException {
        // errorChecker.checkTeamBelongsToSystem(teamId); // Check the teamID exists in
        // this system
        Rider newRider = new Rider(name, yearOfBirth, myTeams.get(teamId)); // Create the rider
        getTeam(teamId).addRider(newRider); // Add the rider to the team
        return newRider.getId(); // Return the new rider's ID
    }

    /**
     * Remove a rider from the system
     *
     * @param riderId The ID of the rider to be removed.
     * @throws IDNotRecognisedException When riderID is not an ID for any of the
     *                                  systems riders
     */

    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        Rider rider = getRider(riderId);
        rider.getMyTeam().removeRider(riderId); // Remove the rider using its own object's remove function
        for (int stageId : rider.getRegisteredStages()) {
            getStage(stageId).removeRider(riderId);
        }
    }

    /**
     * Record the times of a rider in a stage.
     *
     * @param stageId     The ID of the stage the result refers to.
     * @param riderId     The ID of the rider.
     * @param checkpoints An array of times at which the rider reached each of the
     *                    checkpoints of the stage, including the start time and the
     *                    finish line.
     * @throws IDNotRecognisedException        If the ID does not match to any rider
     *                                         or
     *                                         stage in the system.
     * @throws DuplicatedResultException       Thrown if the rider has already a
     *                                         result
     *                                         for the stage. Each rider can have
     *                                         only
     *                                         one result per stage.
     * @throws InvalidCheckpointTimesException Thrown if the length of
     *                                         checkpointTimes is
     *                                         not equal to n+2, where n is the
     *                                         number
     *                                         of checkpoints in the stage; +2
     *                                         represents
     *                                         the start time and the finish time of
     *                                         the
     *                                         stage.
     * @throws InvalidStageStateException      Thrown if the stage is not "waiting
     *                                         for
     *                                         results". Results can only be added
     *                                         to a
     *                                         stage while it is "waiting for
     *                                         results".
     */

    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
            throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
            InvalidStageStateException {
        // errorChecker.checkRiderBelongsToSystem(riderId); // Check stageId and riderId
        // exists in this system
        errorChecker.checkStageBelongsToSystem(stageId);
        getRider(riderId).registerForStage(stageId); // Check rider is in system and register for stage
        getStage(stageId).registerResults(riderId, checkpoints);
    }

    /**
     * Get the times of a rider in a stage.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @return The array of times at which the rider reached each of the checkpoints
     *         of the stage and the total elapsed time. Or empty array if the rider
     *         has no result for the stage.
     * @throws IDNotRecognisedException If the ID does not match to any rider or
     *                                  stage in the system.
     */
    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        errorChecker.checkStageBelongsToSystem(stageId);
        getRider(riderId); // Check rider is in system (will throw error if not

        Stage stage = getStage(stageId);
        try {
            return stage.getResults(riderId);
        } catch (IDNotRecognisedException e) {
            return new LocalTime[0];
        }
    }

    /**
     * Get the adjusted elapsed time of a rider in a stage.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @return The adjusted time taken in a stage
     * @throws IDNotRecognisedException If the stage ID is not part of the
     *                                  system or the RiderID is not part of the
     *                                  system
     */
    @Override
    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
        getRider(riderId); // Check rider is in system (will throw error if not
        errorChecker.checkStageBelongsToSystem(stageId);
        return getStage(stageId).getAdjustedElapsedTime(riderId);
    }

    /**
     * Remove the results of a rider in a stage.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @throws IDNotRecognisedException If the ID does not match to any rider or
     *                                  stage in the system.
     */
    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        getStage(stageId).removeRider(riderId);
    }

    /**
     * Get the riders (as IDs) that are a part of the queried stage ordered by their
     * GC time
     *
     * @param stageId The ID of the stage being queried.
     * @return The IDs of the riders sorted by elapsed time
     * @throws IDNotRecognisedException If the stage ID is not part of the system.
     */
    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
        // Check stage ID is part of this system
        errorChecker.checkStageBelongsToSystem(stageId);
        return getStage(stageId).getRidersRankInStage();
    }

    /**
     * Get the adjusted elapsed times of riders in a stage.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of adjusted elapsed times sorted by their finish
     *         time. An empty list if there is no result for the stage. These times
     *         should match the riders returned by
     *         {@link #getRidersRankInStage(int)}
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        errorChecker.checkStageBelongsToSystem(stageId); // Check stage in system
        return getStage(stageId).getRankedAdjustedElapsedTimesInStage();
    }

    /**
     * Get the number of points obtained by each rider in a stage.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of points each rider received in the stage, sorted
     *         by their elapsed time. An empty list if there is no result for the
     *         stage. These points should match the riders returned by
     *         {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
        errorChecker.checkStageBelongsToSystem(stageId); // Check stage is in our system
        return getStage(stageId).getSprintPointsInStage(getRidersRankInStage(stageId));
    }

    /**
     * Get the number of mountain points obtained by each rider in a stage.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of mountain points each rider received in the stage,
     *         sorted by their finish time. An empty list if there is no result for
     *         the stage. These points should match the riders returned by
     *         {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        errorChecker.checkStageBelongsToSystem(stageId); // Check stage is in our system
        return getStage(stageId).getRidersMountainPointsInStage(getRidersRankInStage(stageId));
    }

    /**
     * Erase all the data from the system, so no races,stages, teams, riders,
     * or checkpoint references exist.
     */
    @Override
    public void eraseCyclingPortal() {
        // Use while loops as for loops will not work with concurrent modification
        while (!myTeams.isEmpty()) {
            try {
                removeTeam(new ArrayList<Integer>(myTeams.keySet()).get(0));
            } catch (IDNotRecognisedException e) {
                throw new RuntimeException(e);
            }
        }

        while (!myRaces.isEmpty()) {
            try {
                removeRaceById(myRaces.keySet().stream().toList().get(0));
            } catch (IDNotRecognisedException e) {
                throw new RuntimeException(e);
            }
        }

        assert getTeams().length == 0 : "Teams not erased";
        assert getRaceIds().length == 0 : "Races not erased";
    }

    /**
     * Method saves this MiniCyclingPortal contents into a serialised file,
     * with the filename given in the argument.
     *
     * @param filename Location of the file to be saved.
     * @throws IOException If there is a problem experienced when trying to save the
     *                     store contents to the file.
     */
    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        SerializedData.saveData(filename, this);
    }

    /**
     * Method should load and replace this MiniCyclingPortal contents with the
     * serialised contents stored in the file given in the argument.
     *
     * @param filename Location of the file to be loaded.
     * @throws IOException            If there is a problem experienced when trying
     *                                to load the store contents from the file.
     * @throws ClassNotFoundException If required class files cannot be found when
     *                                loading.
     */
    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
        SerializedData.loadData(filename, this);
    }

    /**
     * Removes a race by its name instead of by its ID.
     *
     * @param name The name of the race to be removed.
     * @throws NameNotRecognisedException When the name has not been found in the
     *                                    system
     */
    @Override
    public void removeRaceByName(String name) throws NameNotRecognisedException {
        // Go through all races check their names
        for (Race race : myRaces.values()) {
            if (race.getName().equals(name)) {
                try {
                    removeRaceById(race.getId());
                } catch (IDNotRecognisedException e) {
                    // This should never happen as we are giving it an ID that we have found in the
                    // system
                    assert false : "Race ID not found in the system";
                }
                return;
            }
        }
        throw new NameNotRecognisedException("The name " + name + " has not been found in the system");
    }

    /**
     * Get the general classification times of riders in a race.
     *
     * @param raceId The ID of the race being queried.
     * @return A list of riders' times sorted by the sum of their adjusted elapsed
     *         times in all stages of the race. An empty list if there is no result
     *         for any stage in the race. These times should match the riders
     *         returned by {@link #getRidersGeneralClassificationRank(int)}.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race is in this system
        return getRaceById(raceId).getRidersGeneralClassificationTimes();
    }

    /**
     * Get the overall points of riders in a race.
     *
     * @param raceId The ID of the race being queried.
     * @return An array of riders' points (i.e., the sum of their points in all
     *         stages
     *         of the race), sorted by the total adjusted elapsed time. An empty
     *         array if
     *         there is no result for any stage in the race. These points should
     *         match the riders returned by
     *         {@link #getRidersGeneralClassificationRank(int)}.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race is in this system

        return getRaceById(raceId).getRidersSprintPoints();
    }

    /**
     * Get the overall mountain points of riders in a race.
     *
     * @param raceId The ID of the race being queried.
     * @return An array of riders' mountain points (i.e., the sum of their mountain
     *         points in all stages of the race), sorted by the total adjusted
     *         elapsed time.
     *         An empty array if there is no result for any stage in the race. These
     *         points should match the riders returned by
     *         {@link #getRidersGeneralClassificationRank(int)}.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race is in this system

        return getRaceById(raceId).getRidersMountainPoints();
    }

    /**
     * Get the general classification rank of riders in a race.
     *
     * @param raceId The ID of the race being queried.
     * @return A ranked list of riders' IDs sorted ascending by the sum of their
     *         adjusted elapsed times in all stages of the race. That is, the first
     *         in this list is the winner (least time). An empty list if there is no
     *         result for any stage in the race.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race is in this system
        return getRaceById(raceId).getRidersGeneralClassificationRanks();

    }

    /**
     * Get the ranked list of riders based on the points classification in a race.
     *
     * @param raceId The ID of the race being queried.
     * @return A ranked list of riders' IDs sorted descending by the sum of their
     *         points in all stages of the race. That is, the first in this list is
     *         the winner (more points). An empty list if there is no result for any
     *         stage in the race.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race is in this system
        return getRaceById(raceId).getRidersSprintPointsRankings();
    }

    /**
     * Get the ranked list of riders based on the mountain classification in a race.
     *
     * @param raceId The ID of the race being queried.
     * @return A ranked list of riders' IDs sorted descending by the sum of their
     *         mountain points in all stages of the race.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
        errorChecker.checkRaceBelongsToSystem(raceId); // Check race is in this system

        return getRaceById(raceId).getRidersMountainPointsRankings();
    }

    /**
     * Get all race IDs in the system as an ArrayList.
     *
     * @return ArrayList of race IDs
     */
    public HashMap<Integer, Race> getMyRacesMap() {
        return myRaces;
    }

    /**
     * Get all team IDs in the system as an ArrayList.
     *
     * @return ArrayList of team IDs
     */
    public HashMap<Integer, Team> getMyTeamsMap() {
        return myTeams;
    }

    /**
     * Get all stage IDs in the system as an ArrayList.
     *
     * @return ArrayList of stage IDs
     */
    protected ArrayList<Integer> getMyStageIds() {
        ArrayList<Integer> myStageIds = new ArrayList<Integer>();
        for (int raceId : getMyRacesMap().keySet()) {
            try {
                myStageIds.addAll(getRaceById(raceId).getStages().keySet());
            } catch (IDNotRecognisedException e) {
                // Should never happen
            }
        }
        return myStageIds;
    }

    protected Rider getRider(int riderId) throws IDNotRecognisedException {
        for (Team team : myTeams.values()) {
            for (Rider rider : team.getRiders().values()) {
                if (rider.getId() == riderId) {
                    return rider;
                }
            }
        }
        throw new IDNotRecognisedException("Rider " + riderId + " is not part of the system");
    }

    protected Team getTeam(int teamId) throws IDNotRecognisedException {
        for (Team team : myTeams.values()) {
            if (team.getId() == teamId) {
                return team;
            }
        }
        throw new IDNotRecognisedException("Team " + teamId + " is not part of the system");
    }

    protected Checkpoint getCheckpoint(int checkId) throws IDNotRecognisedException {
        for (Race race : myRaces.values()) {
            for (Stage stage : race.getStages().values()) {
                for (Checkpoint checkpoint : stage.getCheckpoints()) {
                    if (checkpoint.getId() == checkId) {
                        return checkpoint;
                    }
                }
            }
        }
        throw new IDNotRecognisedException("Checkpoint " + checkId + " is not part of the system");
    }

    protected Stage getStage(int stageId) throws IDNotRecognisedException {
        for (Race race : myRaces.values()) {
            for (Stage stage : race.getStages().values()) {
                if (stage.getId() == stageId) {
                    return stage;
                }
            }
        }
        throw new IDNotRecognisedException("Stage " + stageId + " is not part of the system");
    }

    protected Race getRaceById(int raceId) throws IDNotRecognisedException {
        if (myRaces.containsKey(raceId)) {
            return myRaces.get(raceId);
        }
        throw new IDNotRecognisedException("Race " + raceId + " is not part of the system");
    }
}
