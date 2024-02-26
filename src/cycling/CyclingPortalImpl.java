package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class CyclingPortalImpl implements CyclingPortal {

	// a list of the race IDs that belong to this instance of CyclingPortalImpl
	private final ArrayList<Integer> races = new ArrayList<>();

	// a list of the team IDs that belong to this instance of CyclingPortalImpl
	private final ArrayList<Integer> teams = new ArrayList<>();


	/**
	 * Method to get all the race IDs in the system.
	 *
	 * @return int array of race IDs
	 */
	@Override
	public int[] getRaceIds() {
		// convert the ArrayList of Integers to an array of ints and return it
        return races.stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		// TODO Auto-generated method stub
		return null;
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

		// Check for invalid name
		if (name == null || name.length()>30 || name.isEmpty() || name.contains(" "))
			throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");


		Team newTeam = new Team(name,description); // Create instance of the team
		int newID = newTeam.getId(); // The new ID for the created team
		teams.add(newTeam.getId());


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
		if (!teams.contains(teamId)){
			throw new IDNotRecognisedException("The ID "+ teamId + " Was not found in this CyclingPortalImpl");
		}

		// Get team instance
		Team removingTeam = Team.getTeamById(teamId);
		removingTeam.remove(); // call the team's removal function
		teams.remove(Integer.valueOf(teamId)); // remove it from the cycling portals list of associated teams

	}

	/**
	 * Getter for the team IDs that belong to this instance of CyclingPortalImpl
	 *
	 * @return an array of the team IDs
	 */
	@Override
	public int[] getTeams() {
		// convert the ArrayList of Integers to an array of ints and return it
		return teams.stream().mapToInt(Integer::intValue).toArray();
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
		if (!teams.contains(teamId)){
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
		if (!teams.contains(teamID)){
			throw new IDNotRecognisedException("The ID "+ teamID+ " does not exist in this system");
		}
		// Check the arguments are legal
		if (name == null || name.isEmpty() || yearOfBirth<1900){
			throw  new IllegalArgumentException("The name mustn't be empty or null, and the year must be >= 1900");
		}

		// Create the rider
		Rider newRider = new Rider(name,yearOfBirth,teamID);
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
		// NOTE this won't work properly in some cases will explain in person

        if (!Rider.getRiderIds().contains(riderId)){
			throw new IDNotRecognisedException("The rider ID "+ riderId+ " does not exist in this system");
		}

		// Get team instance
		Rider removingRider = Rider.getRiderById(riderId);
		removingRider.remove(); // call the team's removal function

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

	@Override
	public void removeRaceByName(String name) throws NameNotRecognisedException {
		// TODO Auto-generated method stub

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
