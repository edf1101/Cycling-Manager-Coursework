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
public class ErrorChecker implements java.io.Serializable {
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
     * Checks if a team is part of the system
     *
     * @param teamId the teamID to check
     * @throws IDNotRecognisedException thrown if the team is not part of the system
     */
    public void checkTeamBelongsToSystem(int teamId) throws IDNotRecognisedException {
        if (!portal.getMyTeamsMap().containsKey(teamId)) {
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
        if (!portal.getMyRacesMap().containsKey(raceId)) {
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
        portal.getStage(stageId); // if it's not in any system this throws error

        Race stageRace = portal.getStage(stageId).getParentRace();

        if (!portal.getMyRacesMap().containsKey(stageRace.getId())) {
            // Throw if it's not in our specific system
            throw new IDNotRecognisedException("Stage " + stageId + " is not part of the system");
        }

        // Return otherwise
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
                ids = new ArrayList<>(portal.getMyTeamsMap().keySet());
                break;
            case RACE:
                ids = new ArrayList<>(portal.getMyRacesMap().keySet());
                break;
            default:
                assert (false) : "Invalid nameUnusedType";
        }

        for (int id : ids) {
            String name = null;
            switch (type) { // use the specific method to get the name
                case STAGE:
                    try {
                        name = portal.getStage(id).getName();
                    } catch (IDNotRecognisedException e) {
                        // Will never happen
                    }
                    break;
                case TEAM:
                    try{
                    name = portal.getTeam(id).getName();}
                    catch (IDNotRecognisedException e){
                        // Will never happen as iterating through already valid ids so ignore
                    }
                    break;
                case RACE:
                    try {
                        name = portal.getRaceById(id).getName();
                    } catch (IDNotRecognisedException e) {
                        throw new RuntimeException(e);
                    }
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
