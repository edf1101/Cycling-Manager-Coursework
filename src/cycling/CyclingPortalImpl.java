package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CyclingPortalImpl implements CyclingPortal {

	// Lists of the various IDs that belong to this instance of CyclingPortalImpl
	private final ArrayList<Integer> myRaceIDs = new ArrayList<>();
	private final ArrayList<Integer> myStageIDs = new ArrayList<>();
	private final ArrayList<Integer> myCheckpointIDs = new ArrayList<>();
	private final ArrayList<Integer> myTeamIDs = new ArrayList<>();
	private final ArrayList<Integer> myRiderIDs = new ArrayList<>();


	/**
	 * Method to get all the race IDs in the system.
	 *
	 * @return int array of race IDs
	 */
	@Override
	public int[] getRaceIds() {
		// convert the ArrayList of Integers to an array of ints and return it
        return myRaceIDs.stream().mapToInt(Integer::intValue).toArray();
	}

	/**
	 * Creates a Race Instance
	 *
	 * @param name        Race's name.
	 * @param description Race's description (can be null).
	 * @return The ID of the race created
	 * @throws IllegalNameException When the name is already taken
	 * @throws InvalidNameException When the name is null, empty, >30 chars, or contains whitespace
	 */
	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		// Check for illegal name, already in use has to be done in this
		// class as we store our list of races here
		for(int raceID : myRaceIDs){
			if(Race.getRaceByID(raceID).getName().equals(name)){
				throw new IllegalNameException("The name "+ name+ " has already been taken");
			}
		}

		Race newRace = new Race(name,description); // Create instance
		int newID = newRace.getId(); // The new ID for the created
		myRaceIDs.add(newRace.getId());

		return newID;

	}

	/**
	 * Get the details from a race.
	 * <p>
	 * The state of this MiniCyclingPortal must be unchanged if any
	 * exceptions are thrown.
	 *
	 * @param raceId The ID of the race being queried.
	 * @return A string describing the race including:
	 * name, description, stage count, and total length
	 * @throws IDNotRecognisedException If the ID does not match to any race in the
	 *                                  system.
	 */
	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		// Check the race exists in this system
		if (!myRaceIDs.contains(raceId)){
			throw new IDNotRecognisedException("The ID "+ raceId+ " does not exist in this system");
		}
		return Race.getRaceByID(raceId).getDetails();
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
		if (!myRaceIDs.contains(raceId)){
			throw new IDNotRecognisedException("The Race ID "+ raceId + " Was not found in this CyclingPortalImpl");
		}

		Race.getRaceByID(raceId).remove(); // Remove the race using its own object's remove function
		myRaceIDs.remove(Integer.valueOf(raceId)); // remove it from the cycling portals list of associated teams
	}

	/**
	 * Get the number of stages in a queried race
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
		// TODO Auto-generated method stub
		return 0;
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
		if (!myRaceIDs.contains(raceId)){
			throw new IDNotRecognisedException("The Race ID "+ raceId + " Was not found in this CyclingPortalImpl");
		}

		// Get the race instance
		Race myRace = Race.getRaceByID(raceId);
		// convert its list of stage IDs to an array of ints and return it
        return myRace.getStageIDs().stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	/** This function creates a team
	 *
	 * @param name        The identifier name of the team.
	 * @param description A description of the team.
	 * @return The ID of the newly created team
	 * @throws IllegalNameException When you input a name that is already taken
	 * @throws InvalidNameException When you input a name that breaks naming convention rules
	 */
	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {

		// Check for illegal name, already in use
		for(int teamID : getTeams()){
			if(Team.getTeamById(teamID).getName().equals(name)){
				throw new IllegalNameException("The name "+ name+ " has already been taken");
			}
		}

		Team newTeam = new Team(name,description); // Create instance of the team
		int newID = newTeam.getId(); // The new ID for the created team
		myTeamIDs.add(newTeam.getId());

		return newID;
	}

	/**
	 * Remove a team from the system
	 *
	 * @param teamId The ID of the team to be removed.
	 * @throws IDNotRecognisedException When teamID is not an ID for any of the systems teams
	 */
	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {

		// Throw error if invalid team id
		if (!myTeamIDs.contains(teamId)){
			throw new IDNotRecognisedException("The ID "+ teamId + " Was not found in this CyclingPortalImpl");
		}

		Team.getTeamById(teamId).remove(); // remove the team from its own class
		myTeamIDs.remove(Integer.valueOf(teamId)); // remove it from the cycling portals list of associated teams

	}

	/**
	 * Getter for the team IDs that belong to this instance of CyclingPortalImpl
	 *
	 * @return an array of the team IDs
	 */
	@Override
	public int[] getTeams() {
		// convert the ArrayList of Integers to an array of ints and return it
		return myTeamIDs.stream().mapToInt(Integer::intValue).toArray();
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
		if (!myTeamIDs.contains(teamId)){
			throw new IDNotRecognisedException("The ID "+ teamId+ " does not exist in this system");
		}

		return Team.getTeamById(teamId).getRiders();
	}

	/**
	 * Create a rider and add them to a team
	 *
	 * @param teamID      The ID rider's team.
	 * @param name        The name of the rider.
	 * @param yearOfBirth The year of birth of the rider.
	 * @return The ID of the newly created rider
	 * @throws IDNotRecognisedException When teamID is not an ID for any of the systems teams
	 * @throws IllegalArgumentException When the name is empty or null, or the year of birth is less than 1900
	 */
	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
		// Check the teamID exists in this system
		if (!myTeamIDs.contains(teamID)){
			throw new IDNotRecognisedException("The ID "+ teamID+ " does not exist in this system");
		}

		// Create the rider
		Rider newRider = new Rider(name,yearOfBirth,teamID);
		myRiderIDs.add(newRider.getID()); // add the new rider to the cycling portal's list of riders
		return newRider.getID();

	}

	/**
	 * Remove a rider from the system
	 *
	 * @param riderId The ID of the rider to be removed.
	 * @throws IDNotRecognisedException When riderID is not an ID for any of the systems riders
	 */

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {

        if (!myRiderIDs.contains(riderId)){ // check the riderID exists in this system
			throw new IDNotRecognisedException("The rider ID "+ riderId+ " does not exist in this system");
		}

		Rider.getRiderById(riderId).remove(); // remove the rider using its own object's remove function
		myRiderIDs.remove(Integer.valueOf(riderId)); // Remove the rider ID from our list of rider IDs
	}

	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
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
	 * @throws NameNotRecognisedException When the name has not been found in the system
	 */
	@Override
	public void removeRaceByName(String name) throws NameNotRecognisedException {
		// Pass in list of this portal's race IDs so it doesn't get mixed
		// up with another portal's races which may share names
		int raceIDWithName = Race.getIDByName(name, myRaceIDs);
		try {
			removeRaceById(raceIDWithName);
		}
		catch (IDNotRecognisedException e){
			// This should never happen as we are giving it an ID that we have found in the system
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
