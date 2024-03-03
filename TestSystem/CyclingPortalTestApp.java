import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortal interface -- note you
 * will want to increase these checks, and run it on your CyclingPortalImpl
 * class
 * (not the BadCyclingPortal class).
 *
 *
 * @author Diogo Pacheco
 * @version 2.0
 */
public class CyclingPortalTestApp {

	/**
	 * Main method to run all tests
	 *
	 * @param args not used
	 * @throws InvalidCheckpointTimesException
	 * @throws DuplicatedResultException
	 * @throws InvalidStageTypeException
	 * @throws InvalidStageStateException
	 * @throws InvalidLocationException
	 * @throws InvalidLengthException
	 * @throws InvalidNameException
	 * @throws IllegalNameException
	 */
	public static void main(String[] args) throws IDNotRecognisedException, NameNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointTimesException {
		System.out.println("The system compiled and started the execution...");

		// run each sets of tests twice to check they don't affect each other

		System.out.println("Starting team impl tests...");
		teamClassTests();
		teamClassTests();
		System.out.println("Passed.\n");

		System.out.println("Starting rider impl tests...");
		riderClassTests();
		riderClassTests();
		System.out.println("Passed.\n");

		System.out.println("Starting race impl tests...");
		raceClassTests();
		raceClassTests();
		System.out.println("Passed.\n");

		System.out.println("Starting stage impl tests...");
		stageClassTests();
		stageClassTests();
		System.out.println("Passed.\n");

		 //Will fail until checkpoint functions are implemented
		 System.out.println("Starting portal tests...");
		 portalClassTests();
		 portalClassTests();
		 System.out.println("Passed.\n");

		System.out.println("All tests passed.");
	}

	/**
	 * Tests the portal class
	 */
	private static void portalClassTests() throws IDNotRecognisedException, NameNotRecognisedException,
			IllegalNameException, InvalidNameException, InvalidLengthException, InvalidLocationException,
			InvalidStageStateException, InvalidStageTypeException, DuplicatedResultException,
			InvalidCheckpointTimesException {
		CyclingPortalImpl portal = new CyclingPortalImpl();

		int team1Id = portal.createTeam("Team1", "Test team");
		int team2Id = portal.createTeam("Team2", "Test team");

		int rider1Id = portal.createRider(team1Id, "Rider11", 2000);
		int rider2Id = portal.createRider(team1Id, "Rider12", 2000);
		int rider3Id = portal.createRider(team1Id, "Rider13", 2000);

		int rider4Id=portal.createRider(team2Id, "Rider21", 2000);
		int rider5Id=portal.createRider(team2Id, "Rider22", 2000);
		int rider6Id=portal.createRider(team2Id, "Rider23", 2000);

		int race1Id = portal.createRace("Race1", "Test race");
		int stage1Id = portal.addStageToRace(race1Id, "Stage1", "Test stage 1", 20.0,
				LocalDateTime.of(2021, 1, 1, 12, 0), StageType.FLAT);

		int check1Id = portal.addCategorizedClimbToStage(stage1Id, 0.0, CheckpointType.C1, 2.0,
				100.0);
		int check2Id = portal.addCategorizedClimbToStage(stage1Id, 100.0, CheckpointType.C2,
				3.0, 200.0);

		portal.concludeStagePreparation(stage1Id);

		System.out.println(Arrays.toString(portal.getTeamRiders(team1Id)));
		for (int riderId : portal.getTeamRiders(team1Id)) {
			LocalTime start = LocalTime.of(0, 0);
			LocalTime mid1 = LocalTime.of(0, 1, riderId);
			LocalTime mid2 = LocalTime.of(0, 2, riderId);
			LocalTime fin = LocalTime.of(0, 3, riderId);

			portal.registerRiderResultsInStage(stage1Id, riderId, // note to self the riderId in time is so they are offset? nice
					new LocalTime[] { start, mid1, mid2, fin });
		}

		for (LocalTime time : portal.getRankedAdjustedElapsedTimesInStage(stage1Id)) {
			System.out.println(time);
		}

		// Test getting ranked times
		LocalTime[] rankedTimes = portal.getRankedAdjustedElapsedTimesInStage(stage1Id);
		System.out.println(Arrays.toString(rankedTimes));
		// test getting ranked riders
		int[] rankedRiders = portal.getRidersRankInStage(stage1Id);
		System.out.println(Arrays.toString(rankedRiders));
	}

	/**
	 * Tests the team class
	 */
	private static void teamClassTests() throws IDNotRecognisedException {
		MiniCyclingPortal portal1 = new CyclingPortalImpl();
		MiniCyclingPortal portal2 = new CyclingPortalImpl();

		// Test initial portal starts with no teams
		assert (portal1.getRaceIds().length == 0)
				: "Initial Portal not empty as required or not returning an empty array.";
		assert (portal2.getTeams().length == 0)
				: "Initial Portal not empty as required or not returning an empty array.";

		// Try adding teams and check they were added properly
		int team1Id = -1;
		int team2Id = -1;
		try {
			team1Id = portal1.createTeam("TeamOne", "My favourite");
			team2Id = portal2.createTeam("TeamOnePortal2", "My next fav");

		} catch (IllegalNameException e) {
			e.printStackTrace();
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}

		assert (portal1.getTeams().length == 1)
				: "Portal1 should have one team.";

		assert (portal2.getTeams().length == 1)
				: "Portal2 should have one team.";

		// Test printing the description

		String actualMessage = Team.getTeamById(team1Id).getDetails();
		String supposedMessage = "Name: TeamOne  Description: My favourite";
		assert (actualMessage.equals(supposedMessage)) : "The get Description / ToString is broken";

		// Test removing a team
		portal1.removeTeam(team1Id);
		assert (portal1.getTeams().length == 0)
				: "Portal1 should have no teams after removing the only team.";

		// Test if we add a new team then the removed team's Id is reused.
		int oldId = team1Id;
		int replacedId = -1;
		try {
			replacedId = portal1.createTeam("Team2", "A test team");
		} catch (IllegalNameException e) {
			e.printStackTrace();
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}
		assert (portal1.getTeams().length == 1)
				: "Portal1 should have one team after adding a new team.";
		assert (oldId == replacedId)
				: "The removed team's Id should be reused.";

		// Test errors are thrown when trying to remove a team that doesn't exist
		try {
			portal1.removeTeam(-10);
			assert (false) : "Should have thrown an exception for removing a non-existent team.";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a team with invalid empty name
		try {
			portal1.createTeam("", "A test team");
			assert (false) : "Should have thrown an exception for invalid team name test: empty";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a team with invalid null name
		try {
			portal1.createTeam(null, "A test team");
			assert (false) : "Should have thrown an exception for invalid team name test: null";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a team with invalid whitespace name
		try {
			portal1.createTeam("Test test", "A test team");
			assert (false) : "Should have thrown an exception for invalid team name test: with whitespace";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}
	}

	/**
	 * Tests the rider implementation functions
	 */
	private static void riderClassTests() throws IDNotRecognisedException {
		CyclingPortalImpl portal = new CyclingPortalImpl();

		// Create two test teams
		int team1Id = -1;
		int team2Id = -1;
		try {
			team1Id = portal.createTeam("Team1", "Test team");
			team2Id = portal.createTeam("Team2", "Test team");
		} catch (IllegalNameException e) {
			e.printStackTrace();
		} catch (InvalidNameException e) {
			e.printStackTrace();
		}

		// Test both teams have no riders
		assert (portal.getTeamRiders(team1Id).length == 0) : "Team1 should have no riders.";
		assert (portal.getTeamRiders(team2Id).length == 0) : "Team1 should have no riders.";

		// Add a rider to team 1 only
		int rider1Id = -1;
		try {
			rider1Id = portal.createRider(team1Id, "Rider1", 2000);
		} catch (IDNotRecognisedException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		// Check team 1 has 1 rider and team 2 has 0 riders
		assert (portal.getTeamRiders(team1Id).length == 1) : "Team1 should have 1 rider.";
		assert (portal.getTeamRiders(team2Id).length == 0) : "Team2 should have no riders.";

		// Add a rider to team 2
		int rider2Id = -1;
		try {
			rider2Id = portal.createRider(team2Id, "Rider2", 2000);
		} catch (IDNotRecognisedException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		// Check the rider's Ids are different
		assert (rider1Id != rider2Id) : "The rider Ids should be different.";

		// check each rider is assigned to the correct team
		assert (portal.getTeamRiders(team1Id)[0] == rider1Id) : "Rider1 isn't matched to team1.";
		assert (portal.getTeamRiders(team2Id)[0] == rider2Id) : "Rider2 isn't matched to team1.";

		// Test removing rider from system
		portal.removeRider(rider1Id);
		assert (portal.getTeamRiders(team1Id).length == 0) : "Team1 should have no riders.";

		// Test that adding a new rider reuses the removed rider's Id
		int oldId = rider1Id;
		int replacedId = -1; // add a new rider
		try {
			replacedId = portal.createRider(team1Id, "Rider3", 2000);
		} catch (IDNotRecognisedException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		assert (oldId == replacedId) : "The removed rider's Id should be reused.";

		// Test errors are thrown when trying to remove a rider that doesn't exist
		try {
			portal.removeRider(-10);
			assert (false) : "Should have thrown an exception for removing a non-existent rider.";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a rider with invalid date
		try {
			portal.createRider(team1Id, "TestRiderName", 1800);
			assert (false) : "Should have thrown an exception for invalid rider date";
		} catch (IllegalArgumentException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a rider with invalid empty name
		try {
			portal.createRider(team1Id, "", 2000);
			assert (false) : "Should have thrown an exception for invalid rider name test: empty";
		} catch (IllegalArgumentException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a rider with invalid null name
		try {
			portal.createRider(team1Id, null, 2000);
			assert (false) : "Should have thrown an exception for invalid rider name test: null";
		} catch (IllegalArgumentException e) {
			// This is expected
		}

		// Test errors are thrown when trying to add a rider with not found team Id
		try {
			portal.createRider(-100, "Test", 2000);
			assert (false) : "Should have thrown an exception for non existent team Id";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}

	}

	private static void raceClassTests() throws IDNotRecognisedException, NameNotRecognisedException {
		CyclingPortalImpl portal = new CyclingPortalImpl();

		// Test initial portal has no races
		assert (portal.getRaceIds().length == 0) : "Initial Portal not empty on start";

		// Test creating a race
		int race1Id = -1;
		try {
			race1Id = portal.createRace("Race1", "test_dsc");
		} catch (IllegalNameException | InvalidNameException e) {
			e.printStackTrace();
		}

		// check the portal now has 1 race in it
		assert (portal.getRaceIds().length == 1) : "Portal should have 1 race in now";

		// this new race must have 0 stages upon instantiation
		assert (portal.getRaceStages(race1Id).length == 0) : "Race should have 0 stages on start";
		assert (portal.getNumberOfStages(race1Id) == 0) : "Race should have 0 stages on start";

		// Test the get details method works
		String actualOutput = portal.viewRaceDetails(race1Id);
		String expectedOutput = "Name: Race1, Description: test_dsc, Number of stages: 0, Total length: 0.00";
		assert (actualOutput.equals(expectedOutput)) : "The get details method is broken";

		// Test adding race with illegal name
		try {
			portal.createRace("", "TestRidecdesc");
			assert (false) : "Should have thrown an exception for empty name";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}
		try {
			portal.createRace(null, "TestRidecdesc");
			assert (false) : "Should have thrown an exception for null name";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}
		try {
			portal.createRace("hfjyguydtgkfytuydirttutdutfufufuygyfuyfyfyufuyyyfjfy", "TestRidecdesc");
			assert (false) : "Should have thrown an exception for too long name";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}

		// test adding a race with the same name as we had before
		try {
			portal.createRace("Race1", "test2");
			assert (false) : "Should have thrown an exception for same name";
		} catch (InvalidNameException | IllegalNameException e) {
			// This is expected
		}
		// Test removing the race
		portal.removeRaceById(race1Id);

		assert (portal.getRaceIds().length == 0) : "Portal should have no races in now";

		// test adding a new race reuses the removed one
		int oldId = race1Id;
		int replacedId = -1;
		try {
			replacedId = portal.createRace("Race2", "test_dsc");
		} catch (IllegalNameException | InvalidNameException e) {
			e.printStackTrace();
		}
		assert (oldId == replacedId) : "The new race did not reuse the Id";

		// Test removing by name
		portal.removeRaceByName("Race2");
		assert (portal.getRaceIds().length == 0) : "Portal should have no teams now";

	}

	private static void stageClassTests() throws IDNotRecognisedException, NameNotRecognisedException {
		CyclingPortalImpl portal = new CyclingPortalImpl();

		// Create 2 sets of Races, Teams and Riders for testing
		int rider1Id = -1;
		int team1Id = -1;
		int race1Id = -1;
		int rider2Id = -1;
		int team2Id = -1;
		int race2Id = -1;

		try {
			race1Id = portal.createRace("Race1", "test_dsc");
			team1Id = portal.createTeam("Team1", "Test team");
			rider1Id = portal.createRider(team1Id, "Rider1", 2000);

			race2Id = portal.createRace("Race2", "test_dsc");
			team2Id = portal.createTeam("Team2", "Test team");
			rider2Id = portal.createRider(team2Id, "Rider2", 2000);

		} catch (IllegalNameException | InvalidNameException e) {
			e.printStackTrace();
		}

		// Check the race has no stages initially
		assert (portal.getRaceStages(race1Id).length == 0) : "Race1 should have no stages initially";
		assert (portal.getRaceStages(race2Id).length == 0) : "Race2 should have no stages initially";

		// Add a stage to race 1
		int stage1Id = -1;
		// set up start time as midday on 1st Jan 2021
		LocalDateTime startTime = LocalDateTime.of(2021, 1, 1, 12, 0);
		try {
			stage1Id = portal.addStageToRace(race1Id, "Stage1", "Test stage 1",
					20.0, startTime, StageType.FLAT);
		} catch (IllegalNameException | InvalidNameException | InvalidLengthException e) {
			throw new RuntimeException(e);
		}

		// Check race1 has 1 stage now and race 2 still has 0 stages
		assert (portal.getRaceStages(race1Id).length == 1) : "Race1 should have 1 stage now";
		assert (portal.getRaceStages(race2Id).length == 0) : "Race2 should have 0 stage now";

		int stage2Id = -1;
		// set up start time as midday on 1st Jan 2021
		try {
			stage2Id = portal.addStageToRace(race2Id, "Stage2", "Test stage 2",
					25.0, startTime, StageType.FLAT);
		} catch (IllegalNameException | InvalidNameException | InvalidLengthException e) {
			throw new RuntimeException(e);
		}

		assert (portal.getRaceStages(race1Id).length == 1) : "Race1 should have 1 stage now";
		assert (portal.getRaceStages(race2Id).length == 1) : "Race2 should have 1 stage now";

		// test removing a stage then readding to check the Id is reused
		portal.removeStageById(stage2Id);
		assert (portal.getRaceStages(race1Id).length == 1) : "Race1 should have 1 stage now";
		assert (portal.getRaceStages(race2Id).length == 0) : "Race2 should have 0 stage now";

		int oldId = stage2Id;
		int replacedId = -1;
		// set up start time as midday on 1st Jan 2021
		try {
			replacedId = portal.addStageToRace(race2Id, "Stage3", "New stage 3",
					25.0, startTime, StageType.FLAT);
		} catch (IllegalNameException | InvalidNameException | InvalidLengthException e) {
			throw new RuntimeException(e);
		}
		assert (oldId == replacedId) : "The removed stage's Id should be reused.";

		// check getting length works
		assert (portal.getStageLength(stage1Id) == 20.0) : "The get length method is broken";
		assert (portal.getStageLength(replacedId) == 25.0) : "The get length method is broken";

		// test getting the race's stages return these
		// for stage 1
		assert (portal.getRaceStages(race1Id).length == 1 && portal.getRaceStages(race1Id)[0] == stage1Id)
				: "Race1 is matched to stage1Id only";
		// for stage 2
		assert (portal.getRaceStages(race2Id).length == 1 && portal.getRaceStages(race2Id)[0] == replacedId)
				: "Race2 is matched to replacedId only";

		// check stage has no checkpoints initially
		assert (portal.getStageCheckpoints(stage1Id).length == 0) : "Stage1 should have no checkpoints initially";

		// check it throws error when trying to create a stage with invalid name
		try {
			portal.addStageToRace(race1Id, "", "Test stage 1",
					20.0, startTime, StageType.FLAT);
			assert (false) : "Should have thrown an exception for no name";
		} catch (InvalidNameException e) {
			// This is expected
		} catch (InvalidLengthException | IllegalNameException e) {
			assert (false) : "not expecting this exception";
		}
		// try with null name
		try {
			portal.addStageToRace(race1Id, null, "Test stage 1",
					20.0, startTime, StageType.FLAT);
			assert (false) : "Should have thrown an exception for null name";
		} catch (InvalidNameException e) {
			// This is expected
		} catch (InvalidLengthException | IllegalNameException e) {
			assert (false) : "not expecting this exception";
		}
		// try with whitespace name
		try {
			portal.addStageToRace(race1Id, "t 1", "Test stage 1",
					20.0, startTime, StageType.FLAT);
			assert (false) : "Should have thrown an exception for whitespace name";
		} catch (InvalidNameException e) {
			// This is expected
		} catch (InvalidLengthException | IllegalNameException e) {
			assert (false) : "not expecting this exception";
		}
		// try with too long name
		try {
			portal.addStageToRace(race1Id, "t1fdishfjdabfjksdbfkjsdbfjksdbfkj", "Test stage 1",
					20.0, startTime, StageType.FLAT);
			assert (false) : "Should have thrown an exception for too long name";
		} catch (InvalidNameException e) {
			// This is expected
		} catch (InvalidLengthException | IllegalNameException e) {
			assert (false) : "not expecting this exception";
		}

		// Test it throws error when reusing name
		try {
			portal.addStageToRace(race1Id, "Stage1", "Test stage 1",
					20.0, startTime, StageType.FLAT);
			assert (false) : "Should have thrown an exception for reused name";
		} catch (IllegalNameException e) {
			// This is expected
		} catch (InvalidLengthException | InvalidNameException e) {
			assert (false) : "not expecting this exception";
		}

		// test it throws error when <5km length
		try {
			portal.addStageToRace(race1Id, "Stage8", "Test stage 1",
					2.0, startTime, StageType.FLAT);
			assert (false) : "Should have thrown an exception for too short length";
		} catch (InvalidLengthException e) {
			// This is expected
		} catch (IllegalNameException | InvalidNameException e) {
			assert (false) : "not expecting this exception";
		}

		// test error thrown when giving invalid Ids to getNumberOfStages
		try {
			portal.getNumberOfStages(-10);
			assert (false) : "Should have thrown an exception for too short length";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}
		// test error thrown when giving invalid Ids to getStages
		try {
			portal.getRaceStages(-10);
			assert (false) : "Should have thrown an exception for too short length";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}
		// test error thrown when giving invalid Ids to getStageLength
		try {
			portal.getStageLength(-10);
			assert (false) : "Should have thrown an exception for too short length";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}
		// test error thrown when giving invalid Ids to removeStageById
		try {
			portal.removeStageById(-10);
			assert (false) : "Should have thrown an exception for too short length";
		} catch (IDNotRecognisedException e) {
			// This is expected
		}
		// test error thrown when giving invalid Ids to registerRiderResultsInStage
		try {
			portal.registerRiderResultsInStage(-10, rider1Id, new LocalTime[] { LocalTime.of(12, 0) });
			assert (false) : "Should have thrown an exception for too short length";
		} catch (IDNotRecognisedException e) {
			// This is expected
		} catch (DuplicatedResultException | InvalidCheckpointTimesException | InvalidStageStateException ignored) {
			// Not expecting this exception
		}
		try {
			portal.registerRiderResultsInStage(stage1Id, -10, new LocalTime[] { LocalTime.of(12, 0) });
			assert (false) : "Should have thrown an exception for too short length";
		} catch (IDNotRecognisedException e) {
			// This is expected
		} catch (DuplicatedResultException | InvalidCheckpointTimesException | InvalidStageStateException ignored) {
			// Not expecting this exception
		}
	}
}
