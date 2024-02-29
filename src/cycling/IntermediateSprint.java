package cycling;

public class IntermediateSprint extends Checkpoint {

    // The points distribution for crossing the intermediate sprint checkpoint
    private static final int[] POINTS = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};


    /**
     * Get sprint points for a rider
     *
     * @param riderId the rider's ID
     * @return the sprint points for the rider
     */
    @Override
    public int getIntermediateSprintPoints(int riderId) {
        // This is according to the order that the riders crossed the line,
        // not how long the checkpoint took them

        // We will calculate position by going through the list of times going through the checkpoint
        // If there is a smaller time then we will increment the position
        int position = 1;
        for (int searchRiderId : passTimes.keySet()) {
            if (searchRiderId != riderId && passTimes.get(searchRiderId).isBefore(passTimes.get(riderId))){
                position++;
            }
        }

        // return the points for position if its within the 15 point scoring positions else 0
        return (position <= 15) ? POINTS[position - 1] : 0;
    }

    /**
     * Get mountain points for a rider
     *
     * @param riderId the rider's ID
     * @return 0 as there are no mountain points for an intermediate sprint
     */
    @Override
    public int getMountainPoints(int riderId) {
        return 0;
    }
}
