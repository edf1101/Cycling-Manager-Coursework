package cycling;

import java.util.ArrayList;

/**
 * Represents an entity with a unique ID
 *
 * @author 730002704
 * @author 730003140
 * @version 1.0
 */
abstract class Entity implements java.io.Serializable {
    protected static final ArrayList<Integer> usedIds = new ArrayList<Integer>();
    protected final int id;

    /**
     * Constructor for the Entity class. Generates a unique ID for the entity.
     */
    protected Entity() {
        int idsUsedBefore = usedIds.size();
        this.id = calculateUniqueId();
        usedIds.add(this.id);
        assert usedIds.size() == idsUsedBefore + 1; // assert id was added to usedIds
    }

    /**
     * Getter for the ID attribute on the entity class.
     *
     * @return This instance of an entity's ID
     */
    protected int getId() {
        return id;
    }

    /**
     * Abstract method to be implemented by subclasses to delete the entity.
     * Should remove the entity from any parents and delete any children.
     */
    protected abstract void remove();

    /**
     * Remove this entity's ID from the usedIds list.
     */
    protected void freeId() {
        usedIds.remove((Integer) id);
    }

    /**
     * Calculate a unique ID for the entity.
     * This includes filling gaps from deleted entities.
     *
     * @return A unique ID for the entity
     */
    private static int calculateUniqueId() {

        // Go through all the teams and store their IDs in an array
        // also store the maximum ID
        ArrayList<Integer> genericIds = new ArrayList<Integer>();
        int maxId = -1;

        for (Integer currentId : usedIds) {
            genericIds.add(currentId);
            if (currentId > maxId) {
                maxId = currentId;
            }
        }

        // If there are gaps in the IDs ie `ids = [0, 1, 3, 4]` then return the first gap
        // (in this case 2)
        for (int i = 0; i < maxId; i++) {
            if (!genericIds.contains(i)) {
                return i;
            }
        }
        // If there are no gaps then return the next number in the sequence
        return maxId + 1;
    }
}
