package Map;

import Map.Enums.ImageType;
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

    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(Path.class);


    public Path(ImageView imageView, Pane nodePane, Pane edgePane, ArrayList<LocationNode> locationNodes) {

        this.currentIndex = 0;
        this.originalPath = locationNodes;
        this.imageView = imageView;
        this.nodePane = nodePane;
        this.edgePane = edgePane;
        this.startLocationNode = locationNodes.get(0);
        this.destinationLocationNode = locationNodes.get(locationNodes.size() - 1);
        this.splitPath = new ArrayList<>();

    }

    public void  setup() {

        ArrayList<LocationNode> tempPath = new ArrayList<>();
        tempPath.add(this.originalPath.get(0));

        for (LocationNode l : this.originalPath) {

            LOGGER.info(l.toString());

        }

        for (int i = 0; i < this.originalPath.size() - 1; i++) {

            LOGGER.info("i: " + i + " size: " + this.originalPath.size());

            if (this.originalPath.get(i).isSameFloor(this.originalPath.get(i+1))) {

                tempPath.add(this.originalPath.get(i));

            } else {

                this.splitPath.add(tempPath);
                tempPath = new ArrayList<>();
                tempPath.add(this.originalPath.get(i));

            }

        }

        tempPath.add(this.originalPath.get(this.originalPath.size() - 1));

        if (tempPath.size() > 0) {

            this.splitPath.add(tempPath);

        }

        drawNextFloor();
    }

    public void drawNextFloor() {

        LOGGER.info("Drawing floor " + this.currentIndex);

        if (!(this.currentIndex == (this.splitPath.size() - 1))) {

            this.currentIndex++;

        }

        LOGGER.info("Drawing floor " + this.currentIndex);

        ArrayList<LocationNode> temp = this.splitPath.get(this.currentIndex);
        Floor tempFloor = temp.get(0).getCurrentFloor();

        LOGGER.info(temp.toString());

        tempFloor.drawFloor(this.imageView);

        this.edgePane.getChildren().clear();
        this.nodePane.getChildren().clear();

        this.startLocationNode.drawStartNode(this.nodePane);

        if (this.startLocationNode.equals(temp.get(0))) {

            this.startLocationNode.drawNormal(this.nodePane, ImageType.STARTLOCATION, 2, 2);

        } else {

            temp.get(0).drawNormal(this.nodePane, temp.get(0).getAssociatedImage(), 2, 2);

        }


        if (this.destinationLocationNode.equals(temp.get(temp.size() - 1))) {

            this.destinationLocationNode.drawNormal(this.nodePane, ImageType.DESTINATION, 2, 1);

        } else {

            temp.get(temp.size() - 1).drawNormal(this.nodePane, temp.get(temp.size() - 1).getAssociatedImage(), 2, 2);

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


}