package cycling;

import java.util.ArrayList;

/**
 * Represents an entity with a unique ID
 *
 * @author 730002704
 * @author 730003140
 * @version 1.0
 */
public abstract class Entity implements java.io.Serializable {
    protected static final ArrayList<Integer> usedIds = new ArrayList<Integer>();
    protected final int id;

    /**
     * Constructor for the Entity class. Generates a unique ID for the entity.
     */
    public Entity() {

        int idsUsedBefore = usedIds.size();
        this.id = UniqueIdGenerator.calculateUniqueId(usedIds);
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
}
