import cycling.*;

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
	 */
	public static void main(String[] args) throws IDNotRecognisedException, NameNotRecognisedException {
		System.out.println("The system compiled and started the execution...");

		System.out.println("Starting team impl tests");
		// run each sets of tests twice to check they don't affect each other
		teamClassTests();
		teamClassTests();

		System.out.println("Starting rider impl tests");
		riderClassTests();
		riderClassTests();

		System.out.println("Starting race impl tests");
		raceClassTests();
		raceClassTests();

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

}
