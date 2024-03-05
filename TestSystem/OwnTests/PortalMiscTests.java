package OwnTests;
import cycling.*;

import java.time.LocalDateTime;

public class PortalMiscTests {
    public static void miscImplTests(){
        // Test Erase portal works
        // Will do this by creating a portal, adding all the objects to it, then erasing it.
        // Check that the objects are no longer in the portal in a basic way. Then Check that ids are reused.

        CyclingPortalImpl portal = new CyclingPortalImpl(); // Create a portal

        int oldTeamId; // add team
        try {
            oldTeamId = portal.createTeam("Team1","desc1");
        } catch (IllegalNameException | InvalidNameException e) {
            throw new RuntimeException(e);
        }

        int oldRiderId; // add rider
        try {
            oldRiderId = portal.createRider(oldTeamId,"Rider1",2000);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        int oldRaceId; // add race
        try {
            oldRaceId = portal.createRace("Race","racedesc");
        } catch (InvalidNameException | IllegalNameException e) {
            throw new RuntimeException(e);
        }

        int oldStageId; // add stage
        try {
            oldStageId = portal.addStageToRace(oldRaceId,"Stage1","stagedesc",10.0,
                    LocalDateTime.of(2021, 1, 1, 0, 0),StageType.FLAT);
        } catch (InvalidNameException | IllegalNameException | IDNotRecognisedException | InvalidLengthException e) {
            throw new RuntimeException(e);
        }

        int oldCheckId; // add stage
        try {
            oldCheckId = portal.addCategorizedClimbToStage(oldStageId,10.0,CheckpointType.C1,
                    10.0,10.0);
        } catch (IDNotRecognisedException | InvalidStageStateException | InvalidStageTypeException |
                 InvalidLocationException e) {
            throw new RuntimeException(e);
        }

        portal.eraseCyclingPortal(); // erase portal

        // Do the basic checks
        assert portal.getTeams().length == 0 : "Teams not erased";
        assert portal.getRaceIds().length == 0 : "Races not erased";

        // Now to check everything went, we will create all new objects and check Ids have been reused
        int newTeamId; // add team
        try {
            newTeamId = portal.createTeam("Team1","desc1");
        } catch (IllegalNameException | InvalidNameException e) {
            throw new RuntimeException(e);
        }

        int newRiderId; // add rider
        try {
            newRiderId = portal.createRider(newTeamId,"Rider1",2000);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        int newRaceId; // add race
        try {
            newRaceId = portal.createRace("Race","racedesc");
        } catch (InvalidNameException | IllegalNameException e) {
            throw new RuntimeException(e);
        }

        int newStageId; // add stage
        try {
            newStageId = portal.addStageToRace(newRaceId,"Stage1","stagedesc",10.0,
                    LocalDateTime.of(2021, 1, 1, 0, 0),StageType.FLAT);
        } catch (InvalidNameException | IllegalNameException | IDNotRecognisedException | InvalidLengthException e) {
            throw new RuntimeException(e);
        }

        int newCheckId; // add stage
        try {
            newCheckId = portal.addCategorizedClimbToStage(newStageId,10.0,CheckpointType.C1,
                    10.0,10.0);
        } catch (IDNotRecognisedException | InvalidStageStateException | InvalidStageTypeException |
                 InvalidLocationException e) {
            throw new RuntimeException(e);
        }

        assert oldTeamId == newTeamId : "Team Ids not reused";
        assert oldRiderId == newRiderId : "Rider Ids not reused";
        assert oldRaceId == newRaceId: "Race Ids not reused";
        assert oldStageId == newStageId : "Stage Ids not reused";
        assert oldCheckId == newCheckId : "Check Ids not reused";
    }
}
