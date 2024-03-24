package OwnTests;

import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class PortalCheckpointsTests {
        /**
         * Tests the portal class
         */
        @SuppressWarnings("unused")
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

                int rider4Id = portal.createRider(team2Id, "Rider21", 2000);
                int rider5Id = portal.createRider(team2Id, "Rider22", 2000);
                int rider6Id = portal.createRider(team2Id, "Rider23", 2000);

                int race1Id = portal.createRace("Race1", "Test race");
                int stage1Id = portal.addStageToRace(race1Id, "Stage1", "Test stage 1", 20.0,
                                LocalDateTime.of(2021, 1, 1, 12, 0), StageType.FLAT);

                int stage2Id = portal.addStageToRace(race1Id, "Stage2", "Test stage 2", 20.0,
                        LocalDateTime.of(2021, 1, 1, 12, 0), StageType.TT);

                int check1Id = portal.addCategorizedClimbToStage(stage1Id, 1.0, CheckpointType.C1, 2.0,
                                5.0);
                int check2Id = portal.addCategorizedClimbToStage(stage1Id, 10.0, CheckpointType.C2,
                                3.0, 10.0);

                // Test error thrown adding a new checkpoint stage with invalid id
                try {
                        portal.addCategorizedClimbToStage(-10, 5.0, CheckpointType.C3, 4.0, 20.0);
                        assert false: "Adding a new checkpoint to a invalid id stage did not throw an error";
                } catch (IDNotRecognisedException e) {
                        //expected
                }

                // Test error thrown adding a new checkpoint stage with invalid location
                try {
                        portal.addCategorizedClimbToStage(stage1Id, 500.0, CheckpointType.C3, 4.0, 20.0);
                        assert false: "Adding a new checkpoint to a stage  did not throw an error";
                } catch (InvalidLocationException e) {
                        //expected
                }
                // Test error thrown adding a new checkpoint stage with invalid location
                try {
                        portal.addCategorizedClimbToStage(stage1Id, -1.0, CheckpointType.C3, 4.0, 20.0);
                        assert false: "Adding a new checkpoint to a stage  did not throw an error";
                } catch (InvalidLocationException e) {
                        //expected
                }

                // Test error thrown adding a new checkpoint to a time trial;
                try {
                        portal.addCategorizedClimbToStage(stage2Id, 1.0, CheckpointType.C3, 4.0, 20.0);
                        assert false: "Adding a new checkpoint to a TT stage  did not throw an error";
                } catch (InvalidStageTypeException e) {
                        //expected
                }


                portal.concludeStagePreparation(stage1Id);

                // System.out.println(Arrays.toString(portal.getTeamRiders(team1Id)));
                for (int riderId : portal.getTeamRiders(team1Id)) {
                        // TODO After changing to entity, IDs went over 60? not sure why
                        LocalTime start = LocalTime.of(0, 0);
                        LocalTime mid1 = LocalTime.of(0, 1, riderId / 2);
                        LocalTime mid2 = LocalTime.of(0, 2, riderId / 2);
                        LocalTime fin = LocalTime.of(0, 3, riderId / 2);

                        portal.registerRiderResultsInStage(stage1Id, riderId, // note to self the riderId in time is so
                                                                              // they are offset? nice - yes
                                        new LocalTime[] { start, mid1, mid2, fin });
                }

                // Test error thrown adding a new checkpoint to a concluded stage
                try {
                        portal.addCategorizedClimbToStage(stage1Id, 5.0, CheckpointType.C3, 4.0, 20.0);
                        assert false: "Adding a new checkpoint to a concluded stage did not throw an error";
                } catch (InvalidStageStateException e) {
                        //expected
                }

                // Test error thrown removing checkpoint to a concluded stage
                try {
                        portal.removeCheckpoint(check1Id);
                        assert false: "removing checkpoint for a concluded stage did not throw an error";
                } catch (InvalidStageStateException e) {
                        //expected
                }

                // Test error thrown removing checkpoint with invalid id
                try {
                        portal.removeCheckpoint(-10);
                        assert false: "removing checkpoint for a concluded stage did not throw an error";
                } catch (IDNotRecognisedException e) {
                        //expected
                }

        }

}
