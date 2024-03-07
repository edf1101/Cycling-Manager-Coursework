//package cycling;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * This class is used to serialize the data of the CyclingPortalImpl portal instance.
// *
// * @author 730003140 & 730002704
// * @version 1.0
// */
//public class SerializedData implements java.io.Serializable {
//
//    /**
//     * This method is used to save the data of the CyclingPortalImpl portal instance to a file.
//     *
//     * @param filename the filename to save the data to
//     * @param portal the portal to save
//     * @throws IOException if an I/O error occurs
//     */
//    public static void saveData(String filename,CyclingPortalImpl portal) throws IOException {
//        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
//
//        try {
//            SerializedData data = new SerializedData(portal);
//            out.writeObject(data);
//            out.close();
//        } catch (IDNotRecognisedException e) {
//            // This should never happen as we are giving it an ID that we have found in the system
//        }
//    }
//
//    /**
//     * This method is used to load the data of the CyclingPortalImpl portal instance from a file.
//     *
//     * @param filename the filename to load the data from
//     * @param newPortal the portal to load the data into
//     * @throws IOException if an I/O error occurs
//     * @throws ClassNotFoundException if the class of a serialized object cannot be found
//     */
//    public static void loadData(String filename, CyclingPortalImpl newPortal) throws IOException, ClassNotFoundException {
//
//        // open up the serialised data file and read the data into a new instance of SerializedData
//        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
//        SerializedData loadedPortal = (SerializedData) in.readObject();
//        in.close();
//
//        // Copy the loaded data into this instance
//        newPortal.eraseCyclingPortal();
//
//        // these are the references to the new portal's lists
//        // since java is pass by value, we can't just assign the new lists
//        // to the old ones, we have to modify the old ones
//        ArrayList<Integer> myRaceIds = newPortal.getMyRaceIds();
//        ArrayList<Integer> myTeamIds = newPortal.getMyTeamIds();
//
//        // add in all the new data
//        for (int raceId : loadedPortal.getRaces().keySet()) {
//            myRaceIds.add(raceId);
//            Race.pushRace(raceId, loadedPortal.getRaces().get(raceId));
//        }
//
//        for (int stageId : loadedPortal.getStages().keySet()) {
//            Stage.pushStage(stageId, loadedPortal.getStages().get(stageId));
//        }
//
//        for (int checkpointId : loadedPortal.getCheckpoints().keySet()) {
//            Checkpoint.pushCheckpoint(checkpointId, loadedPortal.getCheckpoints().get(checkpointId));
//        }
//
//        for (int riderId : loadedPortal.getRiders().keySet()) {
//            //Rider.pushRider(riderId, loadedPortal.getRiders().get(riderId));
//        }
//
//        for (int teamId : loadedPortal.getTeams().keySet()) {
//            myTeamIds.add(teamId);
//            //Team.pushTeam(teamId, loadedPortal.getTeams().get(teamId));
//        }
//    }
//    private final CyclingPortalImpl portal;
//
//    private final HashMap<Integer, Race> races = new HashMap<>();
//    private final HashMap<Integer, Stage> stages = new HashMap<>();
//    private final HashMap<Integer, Checkpoint> checkpoints = new HashMap<>();
//    private final HashMap<Integer, Rider> riders = new HashMap<>();
//    private final HashMap<Integer, Team> teams = new HashMap<>();
//
//    /**
//     * Constructor for the SerializedData class
//     *
//     * @param portal the portal to be serialized
//     * @throws IDNotRecognisedException if an ID is not recognised
//     */
//    public SerializedData(CyclingPortalImpl portal) throws IDNotRecognisedException {
//        this.portal = portal;
//
//        for (int raceId : portal.getRaceIds()) {
//            Race race = Race.getRaceById(raceId);
//            races.put(raceId, race);
//
//            for (int stageId : race.getStageIds()) {
//                Stage stage = Stage.getStageById(stageId);
//                stages.put(stageId, stage);
//
//                for (int checkpointId : stage.getCheckpointIds()) {
//                    Checkpoint checkpoint = Checkpoint.getCheckpointById(checkpointId);
//                    checkpoints.put(checkpointId, checkpoint);
//                }
//            }
//        }
//
//        for (int teamId : portal.getTeams()) {
//            Team team = Team.getTeamById(teamId);
//            teams.put(teamId, team);
//
//            for (int riderId : team.getRiders()) {
//                Rider rider = Rider.getRiderById(riderId);
//                riders.put(riderId, rider);
//            }
//        }
//    }
//
//    /**
//     * Returns the portal object.
//     *
//     * @return the portal object
//     */
//    public CyclingPortalImpl getPortal() {
//        return portal;
//    }
//
//    /**
//     * Returns the map of races.
//     *
//     * @return the map of races
//     */
//    public HashMap<Integer, Race> getRaces() {
//        return races;
//    }
//
//    /**
//     * Returns the map of stages.
//     *
//     * @return the map of stages
//     */
//    public HashMap<Integer, Stage> getStages() {
//        return stages;
//    }
//
//    /**
//     * Returns the map of checkpoints.
//     *
//     * @return the map of checkpoints
//     */
//    public HashMap<Integer, Checkpoint> getCheckpoints() {
//        return checkpoints;
//    }
//
//    /**
//     * Returns the map of riders.
//     *
//     * @return the map of riders
//     */
//    public HashMap<Integer, Rider> getRiders() {
//        return riders;
//    }
//
//    /**
//     * Returns the map of teams.
//     *
//     * @return the map of teams
//     */
//    public HashMap<Integer, Team> getTeams() {
//        return teams;
//    }
//}
