package cycling;

import java.io.*;
import java.util.HashMap;

/**
 * This class is used to serialize the data of the CyclingPortalImpl portal
 * instance.
 *
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
class SerializedData implements java.io.Serializable {

    private final HashMap<Integer, Race> RACES = new HashMap<>();
    private final HashMap<Integer, Team> TEAMS = new HashMap<>();

    /**
     * This method is used to save the data of the CyclingPortalImpl portal instance
     * to a file.
     *
     * @param filename the filename to save the data to
     * @param portal   the portal to save
     * @throws IOException if an I/O error occurs
     */
    protected static void saveData(String filename, CyclingPortalImpl portal) throws IOException {
        // Convert the portal to a serialised data object and write it to a file
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));

        try {
            SerializedData data = new SerializedData(portal);
            out.writeObject(data);
            out.close();
        } catch (IDNotRecognisedException e) {
            // This should never happen as we are giving it an ID that we have found in the
            // system
            assert false : "ID not recognised when saving data to file";
        }
    }

    /**
     * This method is used to load the data of the CyclingPortalImpl portal instance
     * from a file.
     *
     * @param filename  the filename to load the data from
     * @param newPortal the portal to load the data into
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be
     *                                found
     */
    protected static void loadData(String filename, CyclingPortalImpl newPortal)
            throws IOException, ClassNotFoundException {

        // Open up the serialised data file and read the data into a new instance of
        // SerializedData
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
        SerializedData loadedPortal = (SerializedData) in.readObject();
        in.close();

        // Remove the old portal
        newPortal.eraseCyclingPortal();

        // These are the references to the new portal's hashmaps
        // Since java is pass by value, we can't just assign the new hashmaps
        // To the old ones, we have to modify the old ones
        HashMap<Integer, Race> raceMap = newPortal.getMyRacesMap();
        HashMap<Integer, Team> teamMap = newPortal.getMyTeamsMap();

        // Add in all the new data
        for (Race race : loadedPortal.getRaces().values()) {
            raceMap.put(race.getId(), race);
        }

        for (int teamId : loadedPortal.getTeams().keySet()) {
            teamMap.put(teamId, loadedPortal.getTeams().get(teamId));
        }
    }

    /**
     * Constructor for the SerializedData class.
     *
     * @param portal the portal to be serialized
     * @throws IDNotRecognisedException if an ID is not recognised
     */
    protected SerializedData(CyclingPortalImpl portal) throws IDNotRecognisedException {
        for (int raceId : portal.getRaceIds()) {
            Race race = portal.getRaceById(raceId);
            RACES.put(raceId, race);
        }

        for (int teamId : portal.getTeams()) {
            Team team = portal.getTeamById(teamId);
            TEAMS.put(teamId, team);
        }
    }

    /**
     * Returns the map of races.
     *
     * @return The map of races
     */
    protected HashMap<Integer, Race> getRaces() {
        return RACES;
    }

    /**
     * Returns the map of teams.
     *
     * @return The map of teams
     */
    protected HashMap<Integer, Team> getTeams() {
        return TEAMS;
    }
}
