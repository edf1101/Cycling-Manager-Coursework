package cycling;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The portal does certain things many times, ie check a stage is part of its system.
 * This class holds those functions to avoid repetition.
 */
public class ErrorChecker {

    /**
     * Checks if a rider is part of the system
     *
     * @param  riderId the rider ID to check
     * @param  teamIds the list of teamIds that are contained by this portal
     */
    public static void checkRiderBelongsToSystem(int riderId,ArrayList<Integer> teamIds) throws IDNotRecognisedException {
        int riderTeam = Rider.getRiderById(riderId).getMyTeam(); // check if the ID is in any system


        if (!teamIds.contains(riderTeam)) { // if it's not in our specific system throw error
            {
                throw new IDNotRecognisedException("Rider " + riderId + " is not part of the system");
            }
        }
        // else return
    }

    /**
     * Checks if a stage is part of the system
     *
     * @param  stageId the stage ID to check
     * @param  raceIds the list of raceIds that are contained by this portal
     */
    public static void checkStageBelongsToSystem(int stageId,ArrayList<Integer> raceIds) throws IDNotRecognisedException {
        Stage.getStageById(stageId); // if it's not in any system this throws error

        int stageRace = Stage.getStageById(stageId).getRaceId();


        if (!raceIds.contains(stageRace)) { // if it's not in our specific system throw error
            {
                throw new IDNotRecognisedException("Stage " + stageId + " is not part of the system");
            }
        }
        // else return
    }

    /**
     * Checks if a checkpoint is part of the system
     *
     * @param  checkpointId the checkpoint ID to check
     * @param  raceIds the list of raceIds that are contained by this portal
     */
    public static void checkCheckpointBelongsToSystem(int checkpointId,ArrayList<Integer> raceIds) throws IDNotRecognisedException {

        // if it's not in any system this throws error
        int parentRace = Checkpoint.getCheckpointById(checkpointId).getParentStage().getRaceId();

        if (!raceIds.contains(parentRace)) { // if it's not in our specific system throw error
            {
                throw new IDNotRecognisedException("Checkpoint " + checkpointId + " is not part of the system");
            }
        }
        // else return
    }

    public static void checkStageNameUnused(String trialName,ArrayList<Integer> raceIds) throws IllegalNameException {

        // Go through all races in this system
        for (int raceId : raceIds) {

            // go through each stage in the current race
            for (int stageId : Race.getRaceById(raceId).getStageIds())
                {
                    String stageName = null; // get the name of the stage
                    try {
                        stageName = Stage.getStageById(stageId).getName();
                    } catch (IDNotRecognisedException e) {
                        // Should never occur
                    }

                    if (stageName.equals(trialName)) { // if the name is already taken
                        throw new IllegalNameException("The name " + stageName + " has already been taken");
                    }
                }

        }

    }

}
