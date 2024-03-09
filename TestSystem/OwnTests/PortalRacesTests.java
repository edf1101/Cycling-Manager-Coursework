package OwnTests;

import cycling.*;

public class PortalRacesTests {
    /**
     * Tests for the race implementation
     *
     * @throws IDNotRecognisedException
     * @throws NameNotRecognisedException
     */
    public static void raceImplTests() throws IDNotRecognisedException, NameNotRecognisedException {
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
            assert false : "Should have thrown an exception for empty name";
        } catch (InvalidNameException | IllegalNameException e) {
            // This is expected
        }
        try {
            portal.createRace(null, "TestRidecdesc");
            assert false : "Should have thrown an exception for null name";
        } catch (InvalidNameException | IllegalNameException e) {
            // This is expected
        }
        try {
            portal.createRace("hfjyguydtgkfytuydirttutdutfufufuygyfuyfyfyufuyyyfjfy", "TestRidecdesc");
            assert false : "Should have thrown an exception for too long name";
        } catch (InvalidNameException | IllegalNameException e) {
            // This is expected
        }

        // test adding a race with the same name as we had before
        try {
            portal.createRace("Race1", "test2");
            assert false : "Should have thrown an exception for same name";
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
