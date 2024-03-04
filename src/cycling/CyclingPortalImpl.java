package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CyclingPortalImpl implements CyclingPortal {

	// Lists of the various IDs that belong to this instance of CyclingPortalImpl
	// We only need to have lists for race and Team ids since we can backtrack
	// through others
	// to get to one of these
	private final ArrayList<Integer> myRaceIds = new ArrayList<>();
	private final ArrayList<Integer> myTeamIds = new ArrayList<>();

	private final ErrorChecker errorChecker = new ErrorChecker(this);

	/**
	 * Method to get all the race IDs in the system.
	 *
	 * @return int array of race IDs
	 */
	@Override
	public int[] getRaceIds() {
		// convert the ArrayList of Integers to an array of ints and return it
		return myRaceIds.stream().mapToInt(Integer::intValue).toArray();
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
		int newId = newRace.getId(); // The new ID for the created
		myRaceIds.add(newRace.getId());

		return newId;

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
		errorChecker.checkRaceBelongsToSystem(raceId); // check race belongs to this system
		return Race.getRaceById(raceId).getDetails();
	}

	/**
	 * Removes a race by its ID
	 *
	 * @param raceId The ID of the race to be removed.
	 * @throws IDNotRecognisedException Thrown if the ID is not recognised
	 */
	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		errorChecker.checkRaceBelongsToSystem(raceId); // check race belongs to this system

		// Delete the race and remove it from the list of races
		Race.getRaceById(raceId).delete();
		myRaceIds.remove(Integer.valueOf(raceId));
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
		errorChecker.checkRaceBelongsToSystem(raceId); // check race belongs to this system
		return getRaceStages(raceId).length;
	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

		// TODO check with diogo if stages are allowed to share names between races.
		errorChecker.checkNameUnused(stageName, ErrorChecker.nameUnusedType.STAGE); // Check Stage name is Unique
		errorChecker.checkRaceBelongsToSystem(raceId); // Check Race matches system list of races

		// Create the stage and add it to the list of stage Ids and return Id.
		Stage newStage = new Stage(stageName, description, type, length, raceId);
		Race.getRaceById(raceId).addStage(newStage.getId());

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
		Race myRace = Race.getRaceById(raceId);
		// convert its list of stage IDs to an array of ints and return it
		return myRace.getStageIds().stream().mapToInt(Integer::intValue).toArray();
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

		return Stage.getStageById(stageId).getLength();
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

		Stage.getStageById(stageId).delete();
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

		Checkpoint newClimb = new Climb(type, location, length, averageGradient, stageId); // create the new climb
		Stage.getStageById(stageId).addCheckpoint(newClimb.getMyId()); // add it to the parent stage's list of
																		// checkpoints
		return newClimb.getMyId();
	}

	/**
	 * Add an intermediate sprint to a stage
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

		Checkpoint newInterSprint = new IntermediateSprint(location, stageId); // create the new climb
		Stage.getStageById(stageId).addCheckpoint(newInterSprint.getMyId()); // add it to the parent stage's list of
																				// checkpoints
		return newInterSprint.getMyId();
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
		errorChecker.checkCheckpointBelongsToSystem(checkpointId); // Check checkpoint id exists in this system
		Checkpoint.getCheckpointById(checkpointId).delete(); // delete it using its own object's delete function
	}

	// TODO ik its not actually used anywhere but the specicifity of stage states implies we should have "waiting for results"
	//  etc instead of bool
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

		Stage.getStageById(stageId).concludePreparation();
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
		// convert the ArrayList of Integers to an array of ints and return it
		return Stage.getStageById(stageId).getCheckpoints().stream().mapToInt(Integer::intValue).toArray();
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
		myTeamIds.add(newTeam.getId());
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

		Team.getTeamById(teamId).remove(); // remove the team from its own class
		myTeamIds.remove(Integer.valueOf(teamId)); // remove it from the cycling portals list of associated teams

	}

	/**
	 * Getter for the team IDs that belong to this instance of CyclingPortalImpl
	 *
	 * @return an array of the team IDs
	 */
	@Override
	public int[] getTeams() {
		// convert the ArrayList of Integers to an array of ints and return it
		return myTeamIds.stream().mapToInt(Integer::intValue).toArray();
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
		return Team.getTeamById(teamId).getRiders();
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
		errorChecker.checkTeamBelongsToSystem(teamId); // Check the teamID exists in this system
		return new Rider(name, yearOfBirth, teamId).getId(); // Create the rider and return its ID
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
		errorChecker.checkRiderBelongsToSystem(riderId); // check rider is in system
		Rider.getRiderById(riderId).remove(); // remove the rider using its own object's remove function
	}

	/**
	 * Record the times of a rider in a stage.
	 * <p>
	 * The state of this MiniCyclingPortal must be unchanged if any
	 * exceptions are thrown.
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
		errorChecker.checkRiderBelongsToSystem(riderId); // Check stageId and riderId exists in this system
		errorChecker.checkStageBelongsToSystem(stageId);

		Stage.getStageById(stageId).registerResults(riderId, checkpoints);
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
		errorChecker.checkRiderBelongsToSystem(riderId); // check rider and stage are in the system
		errorChecker.checkStageBelongsToSystem(stageId);

		Stage stage = Stage.getStageById(stageId);
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
	 * system or the RiderID is not part of the system
	 */
	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		errorChecker.checkRiderBelongsToSystem(riderId); // Do checks for stage and rider Ids
		errorChecker.checkStageBelongsToSystem(stageId);
		return Stage.getStageById(stageId).getAdjustedElapsedTime(riderId);
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage.getStageById(stageId).removeRider(riderId);
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
		// check stage ID is part of this system
		errorChecker.checkStageBelongsToSystem(stageId);

		// TODO abstract this away

		Stage stage = Stage.getStageById(stageId);

		ArrayList<Integer> riders = stage.getRegisteredRiders();

		riders.sort((rider1, rider2) -> {
			try {
				LocalTime rider1Time = stage.getElapsedTime(rider1);
				LocalTime rider2Time = stage.getElapsedTime(rider2);

				return rider1Time.compareTo(rider2Time);
			} catch (IDNotRecognisedException e) {
				System.err.println("Rider not found in stage"); // Should never happen
				return 0;
			}
		});

		return riders.stream().mapToInt(Integer::intValue).toArray();
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
		// Check stage in system
		errorChecker.checkStageBelongsToSystem(stageId);
		// TODO abstract away

		int[] order = getRidersRankInStage(stageId);
		LocalTime[] times = new LocalTime[order.length];

		for (int i = 0; i < order.length; i++) {
			times[i] = getRiderAdjustedElapsedTimeInStage(stageId, order[i]);
		}

		return times;
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

		// Get the ordered list of riders
		int[] orderedRiders = getRidersRankInStage(stageId);
		int[] sprintPoints = new int[orderedRiders.length];
		for (int riderId : orderedRiders) {
			sprintPoints[riderId] = Stage.getStageById(stageId).getSprintPoints(riderId);
		}

		return sprintPoints;
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

		// Get the ordered list of riders
		int[] orderedRiders = getRidersRankInStage(stageId);
		int[] mountainPoints = new int[orderedRiders.length];
		for (int riderId : orderedRiders) {
			mountainPoints[riderId] = Stage.getStageById(stageId).getMountainPoints(riderId);
		}
		return mountainPoints;
	}

	/**
	 * Erase all the data from the system, so no races,stages, teams, riders,
	 * or checkpoint references exist.
	 *
	 */
	@Override
	public void eraseCyclingPortal() {
		// TODO tests for this
		// Can be done by removing all races and teams, since that's all the portal keeps track of
		for (int raceId : myRaceIds) {
			try {
				removeRaceById(raceId);
			} catch (IDNotRecognisedException e) {
				// This should never happen as we are giving it an ID that we have found in the
				// system
			}
		}
		for(int teamId : myTeamIds){
			try {
				removeTeam(teamId);
			} catch (IDNotRecognisedException e) {
				// This should never happen as we are giving it an ID that we have found in the
				// system
			}
		}

	}

	@Override
	public void saveCyclingPortal(String filename) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	/**
	 * Removes a race by its name instead of by its ID
	 *
	 * @param name The name of the race to be removed.
	 * @throws NameNotRecognisedException When the name has not been found in the
	 *                                    system
	 */
	@Override
	public void removeRaceByName(String name) throws NameNotRecognisedException {
		// Pass in list of this portal's race IDs so it doesn't get mixed
		// up with another portal's races which may share names
		int raceIDWithName = Race.getIdByName(name, myRaceIds);
		try {
			removeRaceById(raceIDWithName);
		} catch (IDNotRecognisedException e) {
			// This should never happen as we are giving it an ID that we have found in the
			// system
		}
	}

	@Override
	public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get all race IDs in the system as an ArrayList.
	 *
	 * @return ArrayList of race IDs
	 */
	public ArrayList<Integer> getMyRaceIds() {
		return myRaceIds;
	}

	/**
	 * Get all team IDs in the system as an ArrayList.
	 *
	 * @return ArrayList of team IDs
	 */
	public ArrayList<Integer> getMyTeamIds() {
		return myTeamIds;
	}

	/**
	 * Get all stage IDs in the system as an ArrayList.
	 *
	 * @return ArrayList of stage IDs
	 */
	public ArrayList<Integer> getMyStageIds() {
		ArrayList<Integer> myStageIds = new ArrayList<Integer>();
		for (int raceId :getMyRaceIds()) {
            myStageIds.addAll(Race.getRaceById(raceId).getStageIds());
		}
		return myStageIds;
	}
}
