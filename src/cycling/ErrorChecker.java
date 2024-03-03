package cycling;

/**
 * The portal does certain things many times, ie check a stage is part of its
 * system.
 * This class holds those functions to avoid repetition.
 */
public class ErrorChecker {
    private CyclingPortalImpl portal;

    /**
     * Constructor for the ErrorChecker class
     *
     * @param portal the portal that this error checker is for
     */
    public ErrorChecker(CyclingPortalImpl portal) {
        this.portal = portal;
    }

    /**
     * Checks if a rider is part of the system
     *
     * @param riderId the rider ID to check
     */
    public void checkRiderBelongsToSystem(int riderId) throws IDNotRecognisedException {
        int riderTeam = Rider.getRiderById(riderId).getMyTeam(); // Check if the ID is in any system

        if (!portal.getMyTeamIds().contains(riderTeam)) {
            // Throw if it's not in our specific system
            throw new IDNotRecognisedException("Rider " + riderId + " is not part of the system");
        }

        // Return otherwise
    }

    /**
     * Checks if a stage is part of the system
     *
     * @param stageId the stage ID to check
     */
    public void checkStageBelongsToSystem(int stageId) throws IDNotRecognisedException {
        Stage.getStageById(stageId); // if it's not in any system this throws error

        int stageRace = Stage.getStageById(stageId).getRaceId();

        if (!portal.getMyRaceIds().contains(stageRace)) {
            // Throw if it's not in our specific system
            throw new IDNotRecognisedException("Stage " + stageId + " is not part of the system");
        }

        // Return otherwise
    }

    /**
     * Checks if a checkpoint is part of the system
     *
     * @param checkpointId the checkpoint ID to check
     */
    public void checkCheckpointBelongsToSystem(int checkpointId) throws IDNotRecognisedException {

        // if it's not in any system this throws error
        int parentRace = Checkpoint.getCheckpointById(checkpointId).getParentStage().getRaceId();

        if (!portal.getMyRaceIds().contains(parentRace)) { // if it's not in our specific system throw error
            {
                throw new IDNotRecognisedException("Checkpoint " + checkpointId + " is not part of the system");
            }
        }
        // else return
    }

    public void checkStageNameUnused(String trialName) throws IllegalNameException {
        // Go through all races in this system
        for (int raceId : portal.getMyRaceIds()) {

            // go through each stage in the current race
            for (int stageId : Race.getRaceById(raceId).getStageIds()) {
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
