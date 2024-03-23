package cycling;

import java.util.HashMap;

/**
 * The Climb class is a subclass of the Checkpoint class and is used to represent
 * a climb in a stage
 *
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
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
     * Constructor for the Climb class.
     *
     * @param type          the type of checkpoint it is
     * @param location      the location of the checkpoint
     * @param length        the length of the climb
     * @param avgGradient   the average gradient of the climb
     * @param parentStage the stage that the checkpoint is in
     * @throws InvalidLocationException if the location is out of range of the stage
     * @throws InvalidStageStateException if the stage is not in the correct state
     * @throws InvalidStageTypeException if the stage is not of the correct type
     */
    public Climb(CheckpointType type, Double location, Double length, Double avgGradient, Stage parentStage)
            throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {

        super(type, location, parentStage);
        this.averageGradient = avgGradient;
        this.length = length;
        if (location + length > parentStage.getLength()) {
            throw new InvalidLocationException("Climb is out of range of the stage");
        }
    }
    
    /**
     * Get mountain points for a rider.
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

}
