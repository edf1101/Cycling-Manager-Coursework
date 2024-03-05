package cycling;

import java.util.ArrayList;

/**
 * The portal does certain things many times, ie check a stage is part of its
 * system.
 * This class holds those functions to avoid repetition.
 *
 * @author 730003140 & 730002704
 * @version 1.0
 */
public class ErrorChecker {
    private final CyclingPortalImpl portal;

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
     * @throws IDNotRecognisedException thrown if the rider is not part of the system
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
     * Checks if a team is part of the system
     *
     * @param teamId the teamID to check
     * @throws IDNotRecognisedException thrown if the team is not part of the system
     */
    public void checkTeamBelongsToSystem(int teamId) throws IDNotRecognisedException {
        if (!portal.getMyTeamIds().contains(teamId)) {
            // Throw if it's not in our specific system
            throw new IDNotRecognisedException("Team " + teamId + " is not part of the system");
        }
    }

    /**
     * Checks if a race is part of the system
     *
     * @param raceId the raceId to check
     * @throws IDNotRecognisedException thrown if the race is not part of the system
     */
    public void checkRaceBelongsToSystem(int raceId) throws IDNotRecognisedException {
        if (!portal.getMyRaceIds().contains(raceId)) {
            // Throw if it's not in our specific system
            throw new IDNotRecognisedException("Race " + raceId + " is not part of the system");
        }
    }

    /**
     * Checks if a stage is part of the system
     *
     * @param stageId the stage ID to check
     * @throws IDNotRecognisedException thrown if the stage is not part of the system
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
     * @throws IDNotRecognisedException thrown if the checkpoint is not part of the system
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

    /**
     * enum type to determine what type of name to check
     */
    public enum nameUnusedType{
        /**
         * Whether to check a stage name
         */
        STAGE,
        /**
         * Whether to check a team name
         */
        TEAM,
        /**
         * Whether to check a race name
         */
        RACE;
    }

    /**
     * Checks if a name is unused - combined function for stages, teams and races
     *
     * @param trialName the name to try
     * @param type What type of name to check
     * @throws IllegalNameException if the name is already taken
     */
    public void checkNameUnused(String trialName, nameUnusedType type) throws IllegalNameException {

        // Get the list of ids for the type
        ArrayList<Integer> ids = null;
        switch (type) {
            case STAGE:
                ids = portal.getMyStageIds();
                break;
            case TEAM:
                ids = portal.getMyTeamIds();
                break;
            case RACE:
                ids = portal.getMyRaceIds();
                break;
            default:
                assert (false) : "Invalid nameUnusedType";
        }

        for (int id : ids) {
            String name = null;
            switch (type) { // use the specific method to get the name
                case STAGE:
                    try {
                        name = Stage.getStageById(id).getName();
                    } catch (IDNotRecognisedException e) {
                        // Will never happen
                    }
                    break;
                case TEAM:
                    name = Team.getTeamById(id).getName();
                    break;
                case RACE:
                    name = Race.getRaceById(id).getName();
                    break;
                default:
                    assert (false) : "Invalid nameUnusedType";
            }

            if (name.equals(trialName)) { // if the name is already taken
                throw new IllegalNameException("The name " + name + " has already been taken");
            }
        }
    }

}
