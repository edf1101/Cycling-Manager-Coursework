package cycling;

import java.util.ArrayList;
import java.util.HashMap;

public class Stage {

    private static final HashMap<Integer, Stage> stages = new HashMap<Integer, Stage>();

    private int myId;
    private String name;
    private String description;
    private StageType type;
    private double length;
    private ArrayList<Integer> checkpoints;
    private boolean prepared = false;

    /**
     * Constructor for the Stage class
     *
     * @param name        Name of the stage
     * @param description Description of the stage
     * @param type        The type of the stage
     * @param length      The length of the stage
     */
    public Stage(String name, String description, StageType type, double length) {
        // TODO Check there are no rules for stage naming

        // Set up attributes for the object
        this.myId = UniqueIdGenerator.calculateUniqueId(stages);
        this.name = name;
        this.description = description;
        this.type = type;
        this.length = length;
        this.checkpoints = new ArrayList<Integer>();
        this.prepared = false;

        // add the new object to the hashmap of all stages
        stages.put(this.myId, this);
    }

    /**
     * Getter for a stage by its Id
     *
     * @param id the Id to query
     * @return The stage object reference
     */
    public static Stage getStageById(int id) {
        return stages.get(id);
    }

    /**
     * Getter for all stage Ids
     */
    public static ArrayList<Integer> getIds() {
        return new ArrayList<Integer>(stages.keySet());
    }

    /**
     * Adds a checkpoint to the stage
     *
     * @param checkpointId the Id of the checkpoint to add
     */
    public void addCheckpoint(int checkpointId) {
        checkpoints.add(checkpointId);
    }

    /**
     * Calculates the total time a rider took to complete the stage
     *
     * @param riderId the Id of the rider to calculate the time for
     */
    public void calculateTime(int riderId) {
        Rider rider = Rider.getRiderById(riderId);
    }
}
