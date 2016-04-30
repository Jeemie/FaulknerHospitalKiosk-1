package Map;

import Map.Enums.CardinalDirection;
import Map.Enums.ImageType;
import Map.Enums.RelativeDirection;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matt on 4/22/16.
 */
public class Path {

    private int currentIndex;

    private ArrayList<LocationNode> originalPath;

    private ImageView imageView;

    private Pane nodePane;

    private Pane edgePane;

    private LocationNode startLocationNode;

    private LocationNode destinationLocationNode;

    private ArrayList<ArrayList<LocationNode>> splitPath;

    private ArrayList<Direction> directions;

    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(Path.class);


    public Path(ImageView imageView, Pane nodePane, Pane edgePane, ArrayList<LocationNode> locationNodes) {

        this.currentIndex = -1;
        this.originalPath = locationNodes;
        this.imageView = imageView;
        this.nodePane = nodePane;
        this.edgePane = edgePane;
        this.startLocationNode = locationNodes.get(0);
        this.destinationLocationNode = locationNodes.get(locationNodes.size() - 1);
        this.splitPath = new ArrayList<>();
        this.directions = new ArrayList<>();

    }

    public void  setup() {

        ArrayList<LocationNode> tempPath = new ArrayList<>();

        for (int i = 0; i < this.originalPath.size() - 1; i++) {

            LOGGER.info("i: " + i + " size: " + this.originalPath.size());

            if (this.originalPath.get(i).isSameFloor(this.originalPath.get(i+1))) {

                tempPath.add(this.originalPath.get(i));

            } else {

                tempPath.add(this.originalPath.get(i));

                if (tempPath.size() > 1) {

                    this.splitPath.add(tempPath);

                }

                tempPath = new ArrayList<>();

            }

        }

        tempPath.add(this.originalPath.get(this.originalPath.size() - 1));

        if (tempPath.size() > 0) {

            this.splitPath.add(tempPath);

        }

        //Set the directions object according to the path given
        setupDirections(originalPath);

        drawNextFloor();
    }

    public void drawNextFloor() {

        if (!(this.currentIndex == (this.splitPath.size() - 1))) {

            this.currentIndex++;

        }

        drawFloorPath();

    }

    public void drawPreviousFloor() {

        if (!(this.currentIndex == 0)) {

            this.currentIndex--;

        }

        drawFloorPath();

    }

    private void drawFloorPath() {

        LOGGER.info(this.splitPath.toString());
        LOGGER.info("Path part: " + this.currentIndex);

        ArrayList<LocationNode> temp = this.splitPath.get(this.currentIndex);
        Floor tempFloor = temp.get(0).getCurrentFloor();

        tempFloor.drawFloor(this.imageView);

        this.edgePane.getChildren().clear();
        this.nodePane.getChildren().clear();

        if (this.startLocationNode.equals(temp.get(0))) {

            this.startLocationNode.drawNormal(this.nodePane, ImageType.STARTLOCATION, -10, 0);

        } else {

            temp.get(0).drawNormal(this.nodePane, -10, 0);

        }


        if (this.destinationLocationNode.equals(temp.get(temp.size() - 1))) {

            this.destinationLocationNode.drawNormal(this.nodePane, ImageType.DESTINATION, -10, 20);

        } else {

            temp.get(temp.size() - 1).drawNormal(this.nodePane, -10, 10);

        }

        drawEdgesNormal(this.edgePane, temp);

    }

    private void drawEdgesNormal(Pane pane, ArrayList<LocationNode> path) {

        LocationNode current;
        LocationNode next;

        // For each node in the path
        // Number of edges = number of nodes in path - 1
        for (int i = 0; i < path.size() - 1; i++) {

            current = path.get(i);
            next = path.get(i+1);

            // Find edge between specified nodes
            for (LocationNodeEdge edge : current.getEdges()) {

                if(edge.isEdgeBetweenNodes(current, next)) {

                    // Found edge between two specified nodes
                    edge.drawEdge(pane);
                    break;

                }

            }

        }

    }

    private void setupDirections(ArrayList<LocationNode> path) {
        // Set's the direction objects

        // Create three LocationNodes to create two CardinalDirections
        LocationNode firstLNode, secondLNode, thirdLNode;

        // Create two CardinalDirections to create one RelativeDirection
        CardinalDirection pastCDirection, currentCDirection;

        // A count to help calculate junctions
        int junctionCount = 0;

        // If there is only one node in the path or less, then return nothing.
        if (path.size() < 2) {

            return; //Return with empty array
        }

        // Set the direction to straight if path only has 2 nodes
        directions.add( new Direction(RelativeDirection.STRAIGHT,
                                "Go straight",
                                (int) path.get(0).getDistanceBetweenNodes(path.get(1))));

        // Will run if path.size() is 3
        // Go through the path, and create 1 RelativeDirection through 2 CardinalDirections
        // by using 3 LocationNodes each loop
        for (int i = 1; i < (path.size() - 1); i++) {

            //Declare first three nodes
            firstLNode = path.get(i - 1);
            secondLNode = path.get(i);
            thirdLNode = path.get(i + 1);

            if (secondLNode.onSameFloor(thirdLNode)) {

                //Create CardinalDirections
                pastCDirection = firstLNode.getDirectionsTo(secondLNode);
                currentCDirection = secondLNode.getDirectionsTo(thirdLNode);

                //Create a string to put into the textualDirections arraylist
                String currentTextDirection = "";

                //Create RelationalDirection based on the two cardinal direction
                //If the directions are the same, go forward
                LOGGER.info("pastCDirection: ", pastCDirection.toString());
                LOGGER.info("currentCDirection: ", currentCDirection.toString());
                if (pastCDirection == currentCDirection) {

                    LOGGER.info("Junction count added");
                    junctionCount++; //Increment junctionCount to know the number of junctions
                    if (i == path.size() - 2) { //If it's the last part of the path

                        directions.add(new Direction(   RelativeDirection.STRAIGHT,
                                                        "Go straight, you've reached destination",
                                                        0));

                    }

                } else {

                    //Change how sentence junction is structured depending on junctionCount number
                    currentTextDirection += "Take the ";
                    switch (junctionCount) {
                        case 0:
                            currentTextDirection += "next ";
                            break;
                        case 1:
                            currentTextDirection += (junctionCount + 1) + "nd "; //2nd
                            break;
                        case 2:
                            currentTextDirection += (junctionCount + 1) + "rd "; //3rd
                            break;
                        default:
                            currentTextDirection += (junctionCount + 1) + "th "; //nth
                            break;
                    }

                    //Check cardinalDirection relations, and output the right direction
                    if (pastCDirection.right() == currentCDirection) {

                        currentTextDirection += "Right";

                        directions.add(new Direction(   RelativeDirection.RIGHT,
                                                        currentTextDirection,
                                                        0));

                    } else if (pastCDirection.left() == currentCDirection) {
                        currentTextDirection += "Left";

                        directions.add(new Direction(   RelativeDirection.LEFT,
                                                        currentTextDirection,
                                                        0));

                    } else if (pastCDirection.opposite() == currentCDirection) {
                        currentTextDirection += "Back"; //Should actually not happen

                        directions.add(new Direction(   RelativeDirection.BACK,
                                                        currentTextDirection,
                                                        0));

                    }

                    //Reset junctionCount
                    junctionCount = 0;

                }

            } else { // Multi-level edge

                directions.add( new Direction(  RelativeDirection.ELEVATOR,
                                                "Take the elevator to " + thirdLNode.getCurrentFloor().getFloorName() + ".",
                                                0));

            }
        }
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }
}
