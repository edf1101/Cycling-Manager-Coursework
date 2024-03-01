package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CyclingPortalImpl implements CyclingPortal {

	// Lists of the various IDs that belong to this instance of CyclingPortalImpl
	// TODO could just store raceIDs, and then get the stages and checkpoints from
	// that. Same for teams and riders.
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
		Stage newStage = new Stage(stageName, description, type, length);
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

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}
		return Stage.getStageById(stageId).getLength();
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		// Throw error if invalid stage id
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}

		// Delete the stage from all races
		for (int raceId : myRaceIds) {
			Race race = Race.getRaceById(raceId);

			try {
				race.deleteStage(stageId);
			} catch (IDNotRecognisedException e) {
				// Fine, race doesn't have this stage
			}
		}

		// Remove the stage from the list of stages
		myStageIds.remove(Integer.valueOf(stageId)); // remove it from the cycling portals list of associated stages
	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		if (!myStageIds.contains(stageId)) {
			throw new IDNotRecognisedException("The Stage ID " + stageId + " Was not found in this CyclingPortalImpl");
		}

		Stage.getStageById(stageId).concludePreparation();
	}

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

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
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

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = Stage.getStageById(stageId);

		ArrayList<Integer> riders = stage.getRegisteredRiders();

		// TODO Check this isn't sorting it into reverse order
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

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
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
