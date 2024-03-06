package OwnTests;

import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
public class PortalStagesTests {

    /**
     * Tests the stage implementation functions
     */
    @SuppressWarnings("unused")
    public static void stageImplTests() throws IDNotRecognisedException, NameNotRecognisedException {
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
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (IDNotRecognisedException e) {
            // This is expected
        }
        // test error thrown when giving invalid Ids to getStages
        try {
            portal.getRaceStages(-10);
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (IDNotRecognisedException e) {
            // This is expected
        }
        // test error thrown when giving invalid Ids to getStageLength
        try {
            portal.getStageLength(-10);
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (IDNotRecognisedException e) {
            // This is expected
        }
        // test error thrown when giving invalid Ids to removeStageById
        try {
            portal.removeStageById(-10);
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (IDNotRecognisedException e) {
            // This is expected
        }
        // test error thrown when giving invalid Ids to registerRiderResultsInStage
        try {
            portal.registerRiderResultsInStage(-10, rider1Id, new LocalTime[] { LocalTime.of(12, 0) });
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (IDNotRecognisedException e) {
            // This is expected
        } catch (DuplicatedResultException | InvalidCheckpointTimesException | InvalidStageStateException ignored) {
            assert (false) : "not expecting this exception";
        }
        try {
            portal.registerRiderResultsInStage(stage1Id, -10, new LocalTime[] { LocalTime.of(12, 0) });
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (IDNotRecognisedException e) {
            // This is expected
        } catch (DuplicatedResultException | InvalidCheckpointTimesException | InvalidStageStateException ignored) {
            assert (false) : "not expecting this exception";
        }
        // now conclude the stage
        try {
            portal.concludeStagePreparation(stage1Id);
        } catch (InvalidStageStateException e) {
            throw new RuntimeException(e);
        }

         //Check it throws error when trying to conclude a stage thats already concluded
        try {
            portal.concludeStagePreparation(stage1Id);
            assert (false) : "Should have thrown an exception for invalid id";
        } catch (InvalidStageStateException e) {
            // Expected
        }

    }
}
