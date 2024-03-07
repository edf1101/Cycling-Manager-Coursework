package OwnTests;

import cycling.*;

public class PortalRidersTests {

    /**
     * Tests the rider implementation functions
     */
    public static void riderImplTests() throws IDNotRecognisedException {
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
        //System.out.println(portal.getTeamRiders(team1Id).length);
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


}
