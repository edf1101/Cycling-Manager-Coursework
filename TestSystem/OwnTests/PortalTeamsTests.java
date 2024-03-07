package OwnTests;

import cycling.*;

public class PortalTeamsTests {
    /**
     * Tests the team related methods of the CyclingPortal interface.
     */
    @SuppressWarnings("unused")
    public static void teamImplTests() throws IDNotRecognisedException {
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

        } catch (IllegalNameException | InvalidNameException e) {
            e.printStackTrace();
        }

        assert (portal1.getTeams().length == 1)
                : "Portal1 should have one team.";

        assert (portal2.getTeams().length == 1)
                : "Portal2 should have one team.";

        // Test printing the description

        //String supposedMessage = "Name: TeamOne  Description: My favourite";
        //assert (actualMessage.equals(supposedMessage)) : "The get Description / ToString is broken";

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
}
