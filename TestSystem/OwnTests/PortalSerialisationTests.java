package OwnTests;
import cycling.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class PortalSerialisationTests {

    public static void serialisationImplTests(){
        // Test serialisation of portal works
        // Will do this by creating a portal, adding all the objects to it, then serialising it.
        // Then we will create a new portal and deserialise the data into it.
        // Then we will check that the objects are in the new portal in a basic way by checking

        CyclingPortalImpl portal1 = new CyclingPortalImpl(); // Create a portal

        int portal1TeamId; // add team
        try {
            portal1TeamId = portal1.createTeam("Team1","desc1");
        } catch (IllegalNameException | InvalidNameException e) {
            throw new RuntimeException(e);
        }

        int portal1RiderId; // add rider
        try {
            portal1RiderId = portal1.createRider(portal1TeamId,"Rider1",2000);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        int portal1RaceId; // add race
        try {
            portal1RaceId = portal1.createRace("Race1","racedesc");
        } catch (InvalidNameException | IllegalNameException e) {
            throw new RuntimeException(e);
        }

        int portal1StageId; // add stage
        try {
            portal1StageId = portal1.addStageToRace(portal1RaceId,"Stage1","stagedesc",10.0,
                    LocalDateTime.of(2021, 1, 1, 0, 0),StageType.FLAT);
        } catch (InvalidNameException | IllegalNameException | IDNotRecognisedException | InvalidLengthException e) {
            throw new RuntimeException(e);
        }

        int portal1CheckId; // add checkpoint
        try {
            portal1CheckId = portal1.addCategorizedClimbToStage(portal1StageId,10.0,CheckpointType.C1,
                    10.0,10.0);
        } catch (IDNotRecognisedException | InvalidStageStateException | InvalidStageTypeException |
                 InvalidLocationException e) {
            throw new RuntimeException(e);
        }

        // conclude stage prep
        try {
            portal1.concludeStagePreparation(portal1StageId);
        } catch (IDNotRecognisedException | InvalidStageStateException e) {
            throw new RuntimeException(e);
        }

        // add some times for the stage
        try {
            LocalTime[] results = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(1, 0, 0),LocalTime.of(2, 0, 0)};
            portal1.registerRiderResultsInStage(portal1StageId,portal1RiderId,results);
        } catch (IDNotRecognisedException | InvalidStageStateException | DuplicatedResultException |
                 InvalidCheckpointTimesException e) {
            throw new RuntimeException(e);
        }

        // output these times
        LocalTime[] portal1Times;
        try {
            portal1Times= portal1.getGeneralClassificationTimesInRace(portal1RaceId);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // serialise the portal
        try {
            portal1.saveCyclingPortal("portal1.ser");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create a new portal and deserialise the data into it
        CyclingPortalImpl portal2 = new CyclingPortalImpl();
        try {
            portal2.loadCyclingPortal("portal1.ser");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // get portal2 times
        LocalTime[] portal2Times;
        try {
            portal2Times= portal2.getGeneralClassificationTimesInRace(portal1RaceId);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }
        assert Arrays.equals(portal1Times,portal2Times) : "Times not equal";

    }
}
