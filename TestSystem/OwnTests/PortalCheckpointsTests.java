package OwnTests;

import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
public class PortalCheckpointsTests {
    /**
     * Tests the portal class
     */
    public static void checkpointImplTests() throws IDNotRecognisedException, NameNotRecognisedException,
            IllegalNameException, InvalidNameException, InvalidLengthException, InvalidLocationException,
            InvalidStageStateException, InvalidStageTypeException, DuplicatedResultException,
            InvalidCheckpointTimesException {
        CyclingPortalImpl portal = new CyclingPortalImpl();

        int team1Id = portal.createTeam("Team1", "Test team");
        int team2Id = portal.createTeam("Team2", "Test team");

        int rider1Id = portal.createRider(team1Id, "Rider11", 2000);
        int rider2Id = portal.createRider(team1Id, "Rider12", 2000);
        int rider3Id = portal.createRider(team1Id, "Rider13", 2000);

        int rider4Id=portal.createRider(team2Id, "Rider21", 2000);
        int rider5Id=portal.createRider(team2Id, "Rider22", 2000);
        int rider6Id=portal.createRider(team2Id, "Rider23", 2000);

        int race1Id = portal.createRace("Race1", "Test race");
        int stage1Id = portal.addStageToRace(race1Id, "Stage1", "Test stage 1", 20.0,
                LocalDateTime.of(2021, 1, 1, 12, 0), StageType.FLAT);

        int check1Id = portal.addCategorizedClimbToStage(stage1Id, 0.0, CheckpointType.C1, 2.0,
                100.0);
        int check2Id = portal.addCategorizedClimbToStage(stage1Id, 100.0, CheckpointType.C2,
                3.0, 200.0);

        portal.concludeStagePreparation(stage1Id);

        //System.out.println(Arrays.toString(portal.getTeamRiders(team1Id)));
        for (int riderId : portal.getTeamRiders(team1Id)) {
            LocalTime start = LocalTime.of(0, 0);
            LocalTime mid1 = LocalTime.of(0, 1, riderId);
            LocalTime mid2 = LocalTime.of(0, 2, riderId);
            LocalTime fin = LocalTime.of(0, 3, riderId);

            portal.registerRiderResultsInStage(stage1Id, riderId, // note to self the riderId in time is so they are offset? nice
                    new LocalTime[] { start, mid1, mid2, fin });
        }

        // TODO write these with assertions
        //for (LocalTime time : portal.getRankedAdjustedElapsedTimesInStage(stage1Id)) {
        //    System.out.println(time);
        //}
        //
        //// Test getting ranked times
        //LocalTime[] rankedTimes = portal.getRankedAdjustedElapsedTimesInStage(stage1Id);
        //System.out.println(Arrays.toString(rankedTimes));
        //// test getting ranked riders
        //int[] rankedRiders = portal.getRidersRankInStage(stage1Id);
        //System.out.println(Arrays.toString(rankedRiders));
    }

}
