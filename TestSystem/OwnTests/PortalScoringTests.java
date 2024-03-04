package OwnTests;
import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class PortalScoringTests {
    public static void scoringImplTests(){
        CyclingPortalImpl portal = new CyclingPortalImpl();

        // create a race
        int raceId = -1;
        try {
            raceId = portal.createRace("Tour-de-France", "France");
        } catch (IllegalNameException | InvalidNameException e) {
            e.printStackTrace();
        }

        // create a team
        int teamId = -1;
        try {
            teamId = portal.createTeam("TeamGB", "UK Team");
        } catch (IllegalNameException | InvalidNameException e) {
            e.printStackTrace();
        }
        // Create 4 riders
        int riderId1 = -1;
        int riderId2 = -1;
        int riderId3 = -1;
        int riderId4 = -1;
        try {
            riderId1 = portal.createRider(teamId,"Chris Froome", 1985);
            riderId2 = portal.createRider(teamId,"Geraint Thomas", 1986);
            riderId3 = portal.createRider(teamId,"Egan Bernal", 1997);
            riderId4 = portal.createRider(teamId,"Tom Pidcock", 1999);
        } catch (IDNotRecognisedException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // create 3 stages
        int stageId1 = -1;
        int stageId2 = -1;
        int stageId3 = -1;
        try {
            stageId1 = portal.addStageToRace(raceId, "Stage1",
                    "Test stage 1",25.0,
                    LocalDateTime.of(2024, 1, 1, 0, 0),
                    StageType.FLAT);
            stageId2 = portal.addStageToRace(raceId, "Stage2",
                    "Test stage 2",10.0,
                    LocalDateTime.of(2024, 1, 2, 0, 0),
                    StageType.MEDIUM_MOUNTAIN);
            stageId3 = portal.addStageToRace(raceId, "Stage3",
                    "Test stage 3",10.0,
                    LocalDateTime.of(2024, 1, 2, 12, 0),
                    StageType.FLAT);
        }
        catch (InvalidNameException | IllegalNameException | IDNotRecognisedException | InvalidLengthException e) {
            throw new RuntimeException(e);
        }

        // Add checkpoints to stages
        int stage1Check1 = -1;
        int stage1Check2 = -1;
        int stage2Check1 = -1;
        int stage2Check2 = -1;
        int stage3Check1 = -1;
        int stage3Check2 = -1;
        try {
            stage1Check1 = portal.addCategorizedClimbToStage(stageId1, 10.0, CheckpointType.C1,
                    5.5,12.0);
            stage1Check2 = portal.addCategorizedClimbToStage(stageId1, 22.0, CheckpointType.C3,
                    2.5,3.0);
            stage2Check1 = portal.addIntermediateSprintToStage(stageId2, 2.0);
            stage2Check2 = portal.addIntermediateSprintToStage(stageId2, 6.0);
            stage3Check1 = portal.addIntermediateSprintToStage(stageId3, 5.0);
            stage3Check2 = portal.addCategorizedClimbToStage(stageId3, 8.0, CheckpointType.HC,
                    5.5,2.0);

        } catch (IDNotRecognisedException | InvalidLocationException |
                 InvalidStageStateException | InvalidStageTypeException e) {
            throw new RuntimeException(e);
        }

        // set stages as prepared
        try {
            portal.concludeStagePreparation(stageId1);
            portal.concludeStagePreparation(stageId2);
            portal.concludeStagePreparation(stageId3);
        } catch (IDNotRecognisedException | InvalidStageStateException e) {
            throw new RuntimeException(e);
        }

        // Add riders times to stages
        try {
            //int temp = riderId1;
            //riderId1 = riderId4;
            //riderId4 = temp;
            // Stage 1
            // Rider 1 times
            LocalTime[] rider1Times = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 5, 0), LocalTime.of(1, 0, 2),
                    LocalTime.of(1, 56, 0)};
            portal.registerRiderResultsInStage(stageId1,riderId1,rider1Times);
            // Rider 2 times
            LocalTime[] rider2Times = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 3, 0), LocalTime.of(1, 5, 0),
                    LocalTime.of(1, 57, 0)};
            portal.registerRiderResultsInStage(stageId1,riderId2,rider2Times);
            // Rider 3 times
            LocalTime[] rider3Times = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 10, 0), LocalTime.of(1, 30, 0),
                    LocalTime.of(2, 0, 0)};
            portal.registerRiderResultsInStage(stageId1,riderId3,rider3Times);
            // Rider 4 times
            LocalTime[] rider4Times = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 3, 2), LocalTime.of(0, 59, 0),
                    LocalTime.of(1, 55, 0)};
            portal.registerRiderResultsInStage(stageId1,riderId4,rider4Times);

            // Stage 2
            // Rider 1 times
            LocalTime[] rider1TimesS2 = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 15, 0), LocalTime.of(0, 20, 0),
                    LocalTime.of(0, 59, 0)};
            portal.registerRiderResultsInStage(stageId2,riderId1,rider1TimesS2);
            // Rider 2 times
            LocalTime[] rider2TimesS2 = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 3, 2), LocalTime.of(0, 59, 0),
                    LocalTime.of(1, 20, 0)};
            portal.registerRiderResultsInStage(stageId2,riderId2,rider2TimesS2);
            // Rider 3 times
            LocalTime[] rider3TimesS2 = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 20, 0), LocalTime.of(0, 40, 0),
                    LocalTime.of(1, 25, 0)};
            portal.registerRiderResultsInStage(stageId2,riderId3,rider3TimesS2);
            // Rider 4 times
            LocalTime[] rider4TimesS2 = new LocalTime[]{LocalTime.of(0, 0, 0),
                    LocalTime.of(0, 30, 0), LocalTime.of(1, 0, 0),
                    LocalTime.of(1, 40, 0)};
            portal.registerRiderResultsInStage(stageId2,riderId4,rider4TimesS2);

            // Stage 3
            // Rider 1 times
            LocalTime[] rider1TimesS3 = new LocalTime[]{LocalTime.of(12, 0, 0),
                    LocalTime.of(12, 5, 0), LocalTime.of(12, 30, 0),
                    LocalTime.of(13, 2, 4)};
            portal.registerRiderResultsInStage(stageId3,riderId1,rider1TimesS3);
            // Rider 2 times
            LocalTime[] rider2TimesS3 = new LocalTime[]{LocalTime.of(12, 0, 0),
                    LocalTime.of(12, 16, 0), LocalTime.of(12, 39, 0),
                    LocalTime.of(13, 10, 45)};
            portal.registerRiderResultsInStage(stageId3,riderId2,rider2TimesS3);
            // Rider 3 times
            LocalTime[] rider3TimesS3 = new LocalTime[]{LocalTime.of(12, 0, 0),
                    LocalTime.of(12, 6, 0), LocalTime.of(12, 45, 43),
                    LocalTime.of(13, 20, 33)};
            portal.registerRiderResultsInStage(stageId3,riderId3,rider3TimesS3);
            // Rider 4 times
            LocalTime[] rider4TimesS3 = new LocalTime[]{LocalTime.of(12, 0, 0),
                    LocalTime.of(12, 14, 0), LocalTime.of(12, 35, 0),
                    LocalTime.of(13, 22, 1)};
            portal.registerRiderResultsInStage(stageId3,riderId4,rider4TimesS3);
        } catch (IDNotRecognisedException | InvalidStageStateException | InvalidCheckpointTimesException |
                 DuplicatedResultException e) {
            throw new RuntimeException(e);
        }

        // Get the GC scores + Ranking
        int[] rankings;
        try {
            rankings = portal.getRidersGeneralClassificationRank(raceId);
            LocalTime[] GC = portal.getGeneralClassificationTimesInRace(raceId);

            assert (Arrays.equals(rankings, new int[]{riderId1, riderId2, riderId3, riderId4})):
                    "GC Rankings are not working";

            LocalTime[] expectedGTimes = new LocalTime[]{LocalTime.of(3, 57, 4),
                    LocalTime.of(4, 27, 45), LocalTime.of(4, 45, 33),
                    LocalTime.of(4, 57, 1)};
            assert (Arrays.equals(GC, expectedGTimes)): "GC Times are not working";

        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // Get the times for each stage
        try {
            // stage 1
            LocalTime[] should = new LocalTime[]{
                    LocalTime.of(1, 55, 0), LocalTime.of(1, 56, 0),
                    LocalTime.of(1, 57, 0), LocalTime.of(2, 0, 0)};
            assert Arrays.equals(portal.getRankedAdjustedElapsedTimesInStage(stageId1),should) :
                    "Stage elapsed times are not working";
            assert Arrays.equals(portal.getRidersRankInStage(stageId1),
                    new int[]{riderId4, riderId1, riderId2, riderId3}) : "Stage rankings are not working";

            // stage 2
            should = new LocalTime[]{
                    LocalTime.of(0, 59, 0), LocalTime.of(1, 20, 0),
                    LocalTime.of(1, 25, 0), LocalTime.of(1, 40, 0)};
            assert Arrays.equals(portal.getRankedAdjustedElapsedTimesInStage(stageId2),should) :
                    "Stage elapsed times are not working";
            assert Arrays.equals(portal.getRidersRankInStage(stageId2),
                    new int[]{riderId1, riderId2, riderId3, riderId4}) : "Stage rankings are not working";

        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // Test getting mountain points per stage
        try {
            assert Arrays.equals(portal.getRidersMountainPointsInStage(stageId3),
                    new int[]{20, 12, 10, 15}) : "Mountain points for stage are not working";
            assert Arrays.equals(portal.getRidersMountainPointsInStage(stageId1),
                    new int[]{10, 7, 10, 4}) : "Mountain points for stage are not working";

        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // Test getting sprint points per stage
        try {
            assert Arrays.equals(portal.getRidersPointsInStage(stageId2),
                    new int[]{67, 60, 54, 45}) : "Sprint points for stage are not working";
            assert Arrays.equals(portal.getRidersPointsInStage(stageId3),
                    new int[]{70, 43, 37, 33}) : "Sprint points for stage are not working";
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // Test getting the race mountain points
        try {
            assert Arrays.equals(portal.getRidersMountainPointsInRace(raceId),
                    new int[]{27, 22, 14, 25}) : "Mountain points for race isn't working";
            assert Arrays.equals(portal.getRidersMountainPointClassificationRank(raceId),
                    new int[]{riderId1, riderId4, riderId2, riderId3}) : "Mountain points for race isn't working";
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

        // Test getting the sprint points
        try {
            assert Arrays.equals(portal.getRidersPointsInRace(raceId),
                    new int[]{167, 123, 109, 128}) : "Sprint points for race failed";
            assert Arrays.equals(portal.getRidersPointClassificationRank(raceId),
                    new int[]{20, 23, 21, 22}) : "Sprint points for race failed";
        } catch (IDNotRecognisedException e) {
            throw new RuntimeException(e);
        }

    }
}
