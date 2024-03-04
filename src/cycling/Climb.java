package cycling;

import java.util.HashMap;

public class Climb extends Checkpoint {

    private static final HashMap<CheckpointType, int[]> POINTS = new HashMap<CheckpointType, int[]>();
    static {
        POINTS.put(CheckpointType.C4, new int[] { 1 });
        POINTS.put(CheckpointType.C3, new int[] { 2, 1 });
        POINTS.put(CheckpointType.C2, new int[] { 5, 3, 2, 1 });
        POINTS.put(CheckpointType.C1, new int[] { 10, 8, 6, 4, 2, 1 });
        POINTS.put(CheckpointType.HC, new int[] { 20, 15, 12, 10, 8, 6, 4, 2 });
    }

    private final Double length;
    private final Double averageGradient;

    /**
     * Constructor for the Climb class
     *
     * @param type          the type of checkpoint it is
     * @param location      the location of the checkpoint
     * @param length        the length of the climb
     * @param avgGradient   the average gradient of the climb
     * @param parentStageId the stage that the checkpoint is in
     */
    public Climb(CheckpointType type, Double location, Double length, Double avgGradient, int parentStageId) {

        super(type, location, parentStageId);
        this.averageGradient = avgGradient;
        this.length = length;
    }

    /**
     * Get sprint points for a rider
     *
     * @param riderId the rider's ID
     * @return 0 as there are no sprint points for a climb
     */
    @Override
    public int getIntermediateSprintPoints(int riderId) {
        return 0;
    }

    /**
     * Get mountain points for a rider
     *
     * @param riderId the rider's ID
     * @return the mountain points for the rider
     */
    @Override
    public int getMountainPoints(int riderId) {
        // We will calculate position by going through the list of times going through
        // the checkpoint
        // If there is a smaller time then we will increment the position
        int position = 1;
        for (int searchRiderId : passTimes.keySet()) {
            if (searchRiderId != riderId && passTimes.get(searchRiderId).isBefore(passTimes.get(riderId))) {
                position++;
            }
        }
        int[] pointDistribution = POINTS.get(myType);

        // return the points for position if its within the point scoring positions else
        // 0
        return (position <= pointDistribution.length) ? pointDistribution[position - 1] : 0;
    }

    /**
     * Get the length of the climb
     *
     * @return the length of the climb
     */
    public Double getLength() {
        return length;
    }

    /**
     * Get the average gradient of the climb
     *
     * @return the average gradient of the climb
     */
    public Double getAverageGradient() {
        return averageGradient;
    }
}
