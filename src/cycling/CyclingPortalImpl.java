package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CyclingPortalImpl implements CyclingPortal {

	// Lists of the various IDs that belong to this instance of CyclingPortalImpl
	private final ArrayList<Integer> myRaceIds = new ArrayList<>();
	private final ArrayList<Integer> myStageIds = new ArrayList<>();
	private final ArrayList<Integer> myCheckpointIds = new ArrayList<>();
	private final ArrayList<Integer> myTeamIds = new ArrayList<>();
	private final ArrayList<Integer> myRiderIds = new ArrayList<>();

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
		// Check for illegal name, already in use has to be done in this
		// class as we store our list of races here
		for (int raceId : myRaceIds) {
			if (Race.getRaceById(raceId).getName().equals(name)) {
				throw new IllegalNameException("The name " + name + " has already been taken");
			}
		}

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
		// Check the race exists in this system
		if (!myRaceIds.contains(raceId)) {
			throw new IDNotRecognisedException("The ID " + raceId + " does not exist in this system");
		}
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
		// Throw error if invalid race id
		if (!myRaceIds.contains(raceId)) {
			throw new IDNotRecognisedException("The Race ID " + raceId + " Was not found in this CyclingPortalImpl");
		}

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
		return getRaceStages(raceId).length;
	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

		// Check Race matches system list of races
		if (!myRaceIds.contains(raceId)) {
			throw new IDNotRecognisedException("The id " + raceId + " does not exist in this system");
		}

		// TODO check with diogo if stages are allowed to share names between races.
		// Check for illegal name (already in use)
		for (int stageId : myStageIds) {
			if (Stage.getStageById(stageId).getName().equals(stageName)) {
				throw new IllegalNameException("The name " + stageName + " has already been taken");
			}
		}

		// Create the stage and add it to the list of stage Ids and return Id.
		Stage newStage = new Stage(stageName, description, type, length, raceId);
		Race.getRaceById(raceId).addStage(newStage.getId());
		myStageIds.add(newStage.getId());

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
		// Throw error if invalid race id
		if (!myRaceIds.contains(raceId)) {
			throw new IDNotRecognisedException("The Race ID " + raceId + " Was not found in this CyclingPortalImpl");
		}

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
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}
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
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}

		Stage.getStageById(stageId).delete();
		// Remove the stage from the list of stages
		myStageIds.remove(Integer.valueOf(stageId)); // remove it from the cycling portals list of associated stages
	}

	/**
	 * Add a categorized climb to a stage
	 * <p>
	 * @param stageId         The ID of the stage to which the climb checkpoint is
	 *                        being added.
	 * @param location        The kilometre location where the climb finishes within
	 *                        the stage.
	 * @param type            The category of the climb - {@link CheckpointType#C4},
	 *                        {@link CheckpointType#C3}, {@link CheckpointType#C2},
	 *                        {@link CheckpointType#C1}, or {@link CheckpointType#HC}.
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

		// Check if the system contains this stage
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}

		Checkpoint newClimb = new Climb(type,location, length, averageGradient, stageId); // create the new climb
		Stage.getStageById(stageId).addCheckpoint(newClimb.getMyId()); // add it to the parent stage's list of checkpoints
		myCheckpointIds.add(newClimb.getMyId()); // add it to the cycling portal's list of checkpoints
		return newClimb.getMyId();
	}

	/**
	 * Add an intermediate sprint to a stage
	 *
	 * @param stageId  The ID of the stage to which the intermediate sprint checkpoint
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
		// Check if the system contains this stage
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}
		Checkpoint newInterSprint = new IntermediateSprint(location,stageId); // create the new climb
		Stage.getStageById(stageId).addCheckpoint(newInterSprint.getMyId()); // add it to the parent stage's list of checkpoints
		myCheckpointIds.add(newInterSprint.getMyId()); // add it to the cycling portal's list of checkpoints
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
		// Check checkpoint id exists in this system
		if (!myCheckpointIds.contains(checkpointId)) {
			throw new IDNotRecognisedException("The Checkpoint ID " + checkpointId + " Was not found in this CyclingPortalImpl");
		}

		// TODO abstract this away
		Checkpoint deletingCheckpoint = Checkpoint.getCheckpointById(checkpointId);
		Stage parentStage = deletingCheckpoint.getParentStage();

		if(parentStage.isPrepared()){
			throw new InvalidStageStateException("The stage has already been prepared");
		}

		// Delete the checkpoint its stage
		parentStage.removeCheckpoint(checkpointId);
		// Remove the checkpoint from the list of checkpoints
		myCheckpointIds.remove(Integer.valueOf(checkpointId)); // remove it from the cycling portal's list of checkpoints
		deletingCheckpoint.delete();
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
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}

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
		// Check if the system contains this stage
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}

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

		// Check for illegal name, already in use
		for (int teamId : getTeams()) {
			if (Team.getTeamById(teamId).getName().equals(name)) {
				throw new IllegalNameException("The name " + name + " has already been taken");
			}
		}

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

		// Throw error if invalid team id
		if (!myTeamIds.contains(teamId)) {
			throw new IDNotRecognisedException("The ID " + teamId + " Was not found in this CyclingPortalImpl");
		}

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
		// Check the teamID exists in this system
		if (!myTeamIds.contains(teamId)) {
			throw new IDNotRecognisedException("The ID " + teamId + " does not exist in this system");
		}

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
		// Check the teamID exists in this system
		if (!myTeamIds.contains(teamId)) {
			throw new IDNotRecognisedException("The ID " + teamId + " does not exist in this system");
		}

		// Create the rider
		Rider newRider = new Rider(name, yearOfBirth, teamId);
		myRiderIds.add(newRider.getId()); // add the new rider to the cycling portal's list of riders
		return newRider.getId();

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

		if (!myRiderIds.contains(riderId)) { // check the riderID exists in this system
			throw new IDNotRecognisedException("The rider ID " + riderId + " does not exist in this system");
		}

		Rider.getRiderById(riderId).remove(); // remove the rider using its own object's remove function
		myRiderIds.remove(Integer.valueOf(riderId)); // Remove the rider ID from our list of rider IDs
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
	 * @throws IDNotRecognisedException    If the ID does not match to any rider or
	 *                                     stage in the system.
	 * @throws DuplicatedResultException   Thrown if the rider has already a result
	 *                                     for the stage. Each rider can have only
	 *                                     one result per stage.
	 * @throws InvalidCheckpointTimesException Thrown if the length of checkpointTimes is
	 *                                     not equal to n+2, where n is the number
	 *                                     of checkpoints in the stage; +2 represents
	 *                                     the start time and the finish time of the
	 *                                     stage.
	 * @throws InvalidStageStateException  Thrown if the stage is not "waiting for
	 *                                     results". Results can only be added to a
	 *                                     stage while it is "waiting for results".
	 */

	// TODO ask diogo if its ok that the parameter name is checkpoints here and checkpointTimes in the interface
	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {

		// Check stageId and riderId exists in this system
		if (!myStageIds.contains(stageId) || !myRiderIds.contains(riderId)) {
			throw new IDNotRecognisedException("The ID " + stageId + " does not exist in this system");
		}

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
		// check rider and stage are in the system
		if (!myStageIds.contains(stageId) || !myRiderIds.contains(riderId)) {
			throw new IDNotRecognisedException("The stage ID " + stageId + " or riderId "+riderId+" do not exist in" +
					" this system");
		}

		Stage stage = Stage.getStageById(stageId);
		try {
			return stage.getResults(riderId);
		} catch (IDNotRecognisedException e) {
			return new LocalTime[0];
		}
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = Stage.getStageById(stageId);
		return stage.getAdjustedElapsedTime(riderId);
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage.getStageById(stageId).removeRider(riderId);
	}

	/**
	 * Get the riders (as IDs) that are a part of the queried stage ordered by their GC time
	 *
	 * @param stageId The ID of the stage being queried.
	 * @return The IDs of the riders sorted by elapsed time
	 * @throws IDNotRecognisedException If the stage ID is not part of the system.
	 */
	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		// check stage ID is part of this system
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The stage ID " + stageId + " Was not found in this portal");
		}
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
	 *         should match the riders returned by {@link #getRidersRankInStage(int)}
	 * @throws IDNotRecognisedException If the ID does not match any stage in the
	 *                                  system.
	 */
	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		// Check stage in system
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The stage ID " + stageId + " Was not found in this portal");
		}
		// TODO abstract away

		int[] order = getRidersRankInStage(stageId);
		LocalTime[] times = new LocalTime[order.length];

		for (int i = 0; i < order.length; i++) {
			times[i] = getRiderAdjustedElapsedTimeInStage(stageId, order[i]);
		}

		return times;
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eraseCyclingPortal() {
		// TODO Auto-generated method stub

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

}
