package Map.Enums;

/**
 * Enum to represent the CardinalDirection. Used for the method
 *   in the LocationNode class called getDirectionsTo
 */
public enum CardinalDirection {

    NORTH,
    EAST,
    SOUTH,
    WEST;

    //Get the opposites of the compas, used by the getTextualDirections function
    public CardinalDirection opposite() {

        return CardinalDirection.values()[(this.ordinal() + 2) % 4];
    }

    //Get the value to the right of the compass, used by getTextualDirections function
    public CardinalDirection right() {

        return CardinalDirection.values()[(this.ordinal() + 1) % 4];
    }

    //Get the value of the left of the compass, used by getTextualDirections function
    public CardinalDirection left() {

        return CardinalDirection.values()[(this.ordinal() + 3) % 4];
    }

}
