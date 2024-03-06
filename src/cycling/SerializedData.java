package cycling;

import java.util.HashMap;

public class SerializedData implements java.io.Serializable {
    private CyclingPortalImpl portal;

    private HashMap<Integer, Race> races;
    private HashMap<Integer, Stage> stages;
    private HashMap<Integer, Checkpoint> checkpoints;
    private HashMap<Integer, Rider> riders;
    private HashMap<Integer, Team> teams;

    /**
     * Constructor for the SerializedData class
     *
     * @param portal the portal to be serialized
     * @throws IDNotRecognisedException if an ID is not recognised
     */
    public SerializedData(CyclingPortalImpl portal) throws IDNotRecognisedException {
        this.portal = portal;

        for (int raceId : portal.getRaceIds()) {
            Race race = Race.getRaceById(raceId);
            races.put(raceId, race);

            for (int stageId : race.getStageIds()) {
                Stage stage = Stage.getStageById(stageId);
                stages.put(stageId, stage);

                for (int checkpointId : stage.getCheckpointIds()) {
                    Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
                    checkpoints.put(checkpointId, checkpoint);
                }
            }
        }

        for (int teamId : portal.getTeams()) {
            Team team = Team.getTeamById(teamId);
            teams.put(teamId, team);

            for (int riderId : team.getRiders()) {
                Rider rider = Rider.getRiderById(riderId);
                riders.put(riderId, rider);
            }
        }
    }

    /**
     * Returns the portal object.
     *
     * @return the portal object
     */
    public CyclingPortalImpl getPortal() {
        return portal;
    }

    /**
     * Returns the map of races.
     *
     * @return the map of races
     */
    public HashMap<Integer, Race> getRaces() {
        return races;
    }

    /**
     * Returns the map of stages.
     *
     * @return the map of stages
     */
    public HashMap<Integer, Stage> getStages() {
        return stages;
    }

    /**
     * Returns the map of checkpoints.
     *
     * @return the map of checkpoints
     */
    public HashMap<Integer, Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Returns the map of riders.
     *
     * @return the map of riders
     */
    public HashMap<Integer, Rider> getRiders() {
        return riders;
    }

    /**
     * Returns the map of teams.
     *
     * @return the map of teams
     */
    public HashMap<Integer, Team> getTeams() {
        return teams;
    }
}
