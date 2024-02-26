import cycling.*;

/**
 * A short program to illustrate an app testing some minimal functionality of a
 * concrete implementation of the CyclingPortal interface -- note you
 * will want to increase these checks, and run it on your CyclingPortalImpl class
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
	public static void main(String[] args) throws IDNotRecognisedException {
		System.out.println("The system compiled and started the execution...");

		System.out.println("Starting team class tests");
		teamClassTests();

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
		try {
			portal1.createTeam("TeamOne", "My favorite");
			portal2.createTeam("TeamOnePortal2", "My next fav");
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
		int team1Portal1Id = portal2.getTeams()[0];

		System.out.println(Team.getTeamById(team1Portal1Id));
	}

}
