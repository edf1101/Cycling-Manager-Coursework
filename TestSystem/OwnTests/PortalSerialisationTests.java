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
        // Then we will check that the objects are in the new portal.
        // There's a slight potential issue that if another portal is instantiated seperately it may
        // share ids with the serialised portal and then overwrite data in use elsewhere.
        // we will test this creating two portals with seperate data but shared ids and then serialising both
        // and then deserialising them into new portals and checking that the data is correct and not overwritten.

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
            portal1RaceId = portal1.createRace("Race1","racedesc123");
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

        // get some data from portal1 to compare
        LocalTime[] portal1Times;
        String portal1RaceDetails;
        try {
            portal1Times= portal1.getGeneralClassificationTimesInRace(portal1RaceId);
            portal1RaceDetails = portal1.viewRaceDetails(portal1RaceId);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // serialise the portal
        try {
            portal1.saveCyclingPortal("portal1.ser");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // start over again
        // the idea is that we create a new races etc that share these ids as the previous serialised portal
        // and then we will deserialise the portal and check that both can be used
        portal1.eraseCyclingPortal(); // erase the portal
        int portal3TeamId; // add team
        try {
            portal3TeamId = portal1.createTeam("Team1","desc1");
        } catch (IllegalNameException | InvalidNameException e) {
            throw new RuntimeException(e);
        }

        int portal3RiderId; // add rider
        try {
            portal3RiderId = portal1.createRider(portal3TeamId,"Rider1",2000);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        int portal3RaceId; // add race
        try {
            portal3RaceId = portal1.createRace("Race1","racedesc");
        } catch (InvalidNameException | IllegalNameException e) {
            throw new RuntimeException(e);
        }

        int portal3StageId; // add stage
        try {
            portal3StageId = portal1.addStageToRace(portal3RaceId,"Stage1","stagedesc",10.0,
                    LocalDateTime.of(2021, 1, 1, 0, 0),StageType.FLAT);
        } catch (InvalidNameException | IllegalNameException | IDNotRecognisedException | InvalidLengthException e) {
            throw new RuntimeException(e);
        }

        int portal3CheckId; // add checkpoint
        try {
            portal3CheckId = portal1.addCategorizedClimbToStage(portal3StageId,10.0,CheckpointType.C1,
                    10.0,10.0);
        } catch (IDNotRecognisedException | InvalidStageStateException | InvalidStageTypeException |
                 InvalidLocationException e) {
            throw new RuntimeException(e);
        }

        // conclude stage prep
        try {
            portal1.concludeStagePreparation(portal3StageId);
        } catch (IDNotRecognisedException | InvalidStageStateException e) {
            throw new RuntimeException(e);
        }

        // add some times for the stage
        try {
            LocalTime[] results = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(1, 0, 0),LocalTime.of(4, 0, 0)};
            portal1.registerRiderResultsInStage(portal3StageId,portal3RiderId,results);
        } catch (IDNotRecognisedException | InvalidStageStateException | DuplicatedResultException |
                 InvalidCheckpointTimesException e) {
            throw new RuntimeException(e);
        }

        // get some data from portal1 to compare
        LocalTime[] portal3Times;
        String portal3RaceDetails;
        try {
            portal3Times= portal1.getGeneralClassificationTimesInRace(portal3RaceId);
            portal3RaceDetails = portal1.viewRaceDetails(portal3RaceId);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // serialise the portal
        try {
            portal1.saveCyclingPortal("portal3.ser");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // check portal1 ids are same as portal3
        assert portal1TeamId == portal3TeamId : "Team ids not equal";
        assert portal1RiderId == portal3RiderId : "Rider ids not equal";
        assert portal1RaceId == portal3RaceId : "Race ids not equal";
        assert portal1StageId == portal3StageId : "Stage ids not equal";
        assert portal1CheckId == portal3CheckId : "Check ids not equal";

        // create a new portal and deserialise the data into it
        CyclingPortalImpl portal4 = new CyclingPortalImpl();
        try {
            portal4.loadCyclingPortal("portal3.ser");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // get the same data from portal4
        String portal4RaceDetails;
        String expectedDetails4 = "Name: Race1, Description: racedesc, Number of stages: 1, Total length: 0.00";
        LocalTime[] expectedTimes4 = new LocalTime[]{LocalTime.of(4, 0, 0)};
        LocalTime[] portal4Times;
        try {
            portal4Times= portal4.getGeneralClassificationTimesInRace(portal1RaceId);
            portal4RaceDetails = portal4.viewRaceDetails(portal1RaceId);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }
        assert Arrays.equals(expectedTimes4,portal4Times) : "Times not equal";
        assert expectedDetails4.equals(portal4RaceDetails) : "Details not equal";

        // create a new portal and deserialise the data into it
        CyclingPortalImpl portal2 = new CyclingPortalImpl();
        try {
            portal2.loadCyclingPortal("portal1.ser");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // get the same data from portal2
        String portal2RaceDetails;
        String expectedDetails = "Name: Race1, Description: racedesc123, Number of stages: 1, Total length: 0.00";
        LocalTime[] expectedTimes = new LocalTime[]{LocalTime.of(2, 0, 0)};
        LocalTime[] portal2Times;
        try {
            portal2Times= portal2.getGeneralClassificationTimesInRace(portal1RaceId);
            portal2RaceDetails = portal2.viewRaceDetails(portal1RaceId);
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }
        assert Arrays.equals(expectedTimes,portal2Times) : "Times not equal";
        assert expectedDetails.equals(portal2RaceDetails) : "Details not equal";

        //// get the same data from portal4
        // expectedDetails4 = "Name: Race1, Description: racedesc, Number of stages: 1, Total length: 0.00";
        // expectedTimes4 = new LocalTime[]{LocalTime.of(4, 0, 0)};
        //try {
        //    portal4Times= portal4.getGeneralClassificationTimesInRace(portal1RaceId);
        //    portal4RaceDetails = portal4.viewRaceDetails(portal1RaceId);
        //} catch (IDNotRecognisedException e) {
        //    throw new RuntimeException(e);
        //}
        //assert Arrays.equals(expectedTimes4,portal4Times) : "Times not equal";
        //assert expectedDetails4.equals(portal4RaceDetails) : "Details not equal";
    }
}
