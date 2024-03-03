package cycling;

import java.util.ArrayList;
import java.util.HashMap;

public class Race {

    private static final HashMap<Integer, Race> races = new HashMap<Integer, Race>();
    private final int myId;
    private final String name;
    private final String description;
    private final ArrayList<Integer> stageIds = new ArrayList<Integer>(); // list of the stages belonging to this race

    /**
     * Constructor for the Race class
     *
     * @param name        Name of the race
     * @param description Description of the race
     */
    public Race(String name, String description) throws InvalidNameException {
        // Check for invalid (rule breaking) name
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up attributes for the object

        this.myId = UniqueIdGenerator.calculateUniqueId(races);
        this.name = name;
        this.description = description;

        // add the new object to the hashmap of all races

        races.put(this.myId, this);
    }

    /**
     * Getter for a race by its Id
     *
     * @param id the Id to query
     * @return The race object reference
     */
    public static Race getRaceById(int id) {
        return races.get(id);
    }

    /**
     * Getter for the Race's name
     *
     * @return The name of the Race instance
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the Id attribute on this Race instance
     *
     * @return the Id
     */
    public int getId() {
        return myId;
    }

    /**
     * Gets a short text description of the race including:
     * race Id, name, description, the number of stages, and the total length
     *
     * @return The formatted descriptor string
     */
    public String getDetails() {
        int numStages = stageIds.size();
        double length = 0.0;
        return String.format("Name: %s, Description: %s, Number of stages: %d, Total length: %.2f",
                name, description, numStages, length);
    }

    /**
     * Gets a short description of the race class
     *
     * @return The descriptor string
     */
    @Override
    public String toString() {
        return "Race Class " + getDetails();
    }

    /**
     * Deletes this Race, cascading down to delete all stages and checkpoints
     */
    public void delete() {
        races.remove(myId);

        for (int stageId : stageIds) {
            try {
                Stage.getStageById(stageId).delete();
            } catch (IDNotRecognisedException e) {
                // Should never happen as we are iterating through the list of stageIds
                // which are valid and checked
            }
        }

        // No need to remove stages from list as they are already deleted
    }

    /**
     * Adds a stage to the list of stages that belong to this race
     *
     * @param stageId the stage ID to add to the list
     */
    public void addStage(int stageId) {
        stageIds.add(stageId);
    }

    /**
     * Deletes a stage
     *
     * @param stageId the stage ID to delete
     */
    public void deleteStage(int stageId) throws IDNotRecognisedException {
        if (!stageIds.contains(stageId)) {
            throw new IDNotRecognisedException("The stage ID: " + stageId + ", does not exist in this system");
        }

        Stage.getStageById(stageId).delete();
        stageIds.remove(Integer.valueOf(stageId));
    }

    /**
     * Removes a stage to the list of stages that belong to this race
     * Does not delete the stage, just removes it from the list
     *
     * @param stageId the stage ID to remove from the list
     */
    // TODO Check if this is ever needed, or if deleteStage is enough
    public void removeStage(int stageId) {
        stageIds.remove(Integer.valueOf(stageId));
    }

    /**
     * Getter for the list of stage IDs that belong to this Race
     *
     * @return The stage IDs belonging to this race
     */
    public ArrayList<Integer> getStageIds() {
        return stageIds;
    }

    /**
     * Gets the Id of a Race by its name
     *
     * @param name       The name of the race to find an Id for.
     * @param portalsIds the list of the raceIds that are contained by this portal
     *                   instance, so we don't mix up this
     *                   with another portals race with a same name
     * @return The id of the race with the given name (in the context of the portal)
     * @throws NameNotRecognisedException When the name has not been found in the
     *                                    system
     */
    public static int getIdByName(String name, ArrayList<Integer> portalsIds) throws NameNotRecognisedException {
        // Check the name exists in this system by iterating through
        // all raceIds in system and checking name
        boolean foundName = false;
        int foundId = 0;
        for (int raceId : portalsIds) {
            if (Race.getRaceById(raceId).getName().equals(name)) {
                foundName = true;
                foundId = raceId;
                break;
            }
        }
        if (!foundName) {
            throw new NameNotRecognisedException("The Race name: " + name + ", does not exist in this system");
        }
        return foundId;
    }

}
