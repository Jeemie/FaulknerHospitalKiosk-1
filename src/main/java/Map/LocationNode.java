package Map;

import Kiosk.Controllers.AdminDepartmentPanelController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * A class the represents node(point) on a floor
 */
public class LocationNode extends Observable implements Comparable<LocationNode>{

    private double heuristicCost; // heuristic cost for AStar algorithm
    private final UUID uniqueID; // A randomly generated UUID associated with the current node
    private Location location; // The pixel location of the node on the map
    private ArrayList<LocationNode> adjacentLocationNodes; // A list of nodes that are connected to the current node
    private EnumMap<Destination, ArrayList<String>> destinations; // A map  of the destinations at the current node
    private Floor currentFloor; // The floor that the node is associated with
    public double minDistance = Double.POSITIVE_INFINITY;
    private static NodeObserver observer = new NodeObserver(); // Observer Object watching all LocationNode objects
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationNode.class); // Logger for this class
    private Circle nodeCircle;
    private ArrayList<Line> adjacentLines;
    public Neighbors[] neighbors;
    public LocationNode previous;



    /**
     * TODO
     *
     * @param heuristicCost
     * @param location
     * @param currentFloor
     */
    public LocationNode(double heuristicCost, Location location, Floor currentFloor) {

        this.heuristicCost = heuristicCost;
        this.uniqueID = UUID.randomUUID();
        this.location = location;
        this.adjacentLocationNodes = new ArrayList<>();
        this.destinations = new EnumMap<Destination, ArrayList<String>>(Destination.class);
        this.currentFloor = currentFloor;
        this.adjacentLines = new ArrayList<>();
        this.nodeCircle = new Circle(this.location.getX(), this.location.getY(), 5.0);

        observer.observeNode(this);  //starts observing new LocationNode object

    }




    /**
     * TODO
     *  @param heuristicCost
     * @param uniqueID
     * @param location
     * @param currentFloor
     * @param destinations
     */
    public LocationNode(double heuristicCost, UUID uniqueID, Location location, Floor currentFloor, EnumMap<Destination, ArrayList<String>> destinations) {

        this.heuristicCost = heuristicCost;
        this.uniqueID = uniqueID;
        this.location = location;
        this.adjacentLocationNodes = new ArrayList<>();
        this.destinations = destinations;
        this.currentFloor = currentFloor;
        this.adjacentLines = new ArrayList<>();
        this.nodeCircle = new Circle(this.location.getX(), this.location.getY(), 5.0);

        observer.observeNode(this); //starts observing new LocationNode object

    }

    /**
     * TODO
     *
     * @param destination
     * @param name
     */
    public void addDestination(Destination destination, String name) {

        ArrayList<String> temp;

        if(destinations.containsKey(destination)) {

            temp = destinations.get(destination);
            temp.add(name);

        } else {

            temp = new ArrayList<String>();
            temp.add(name);

            destinations.put(destination,temp);

        }

        setChanged();

        notifyObservers();


    }

    public NodeObserver getNodeObserver(){
        return this.observer;
    }

    /**
     * TODO
     *
     * @param destination
     */
    public void removeDestination(Destination destination, String name) {

        if (destinations.containsKey(destination)) {

            ArrayList<String> temp = destinations.get(destination);

            temp.remove(name);
            setChanged();

        }



        notifyObservers();

    }

    /**
     * TODO
     *
     * @param destinationType
     * @return
     */
    public ArrayList<String> getDestinations(Destination destinationType) {

        ArrayList<String> nodeDestinations = new ArrayList<>();

        if (destinations.containsKey(destinationType)) {

            nodeDestinations.addAll(destinations.get(destinationType));

        }

        return nodeDestinations;
    }

    /**
     * TODO
     *
     * @return
     */
    public ArrayList<String> getDestinations() {

        Set<Destination> entries = destinations.keySet();

        ArrayList<String> nodeDestinations = new ArrayList<>();

        for (Destination d : entries) {

            nodeDestinations.addAll(destinations.get(d));

        }

        return nodeDestinations;
    }

    /**
     * TODO
     *
     * @param adjacentLocationNode
     */
    public void addAdjacentNode(LocationNode adjacentLocationNode) {

        if (this.equals(adjacentLocationNode)) {

            return;
        }

        if (!adjacentLocationNodes.contains(adjacentLocationNode)) {

            adjacentLocationNodes.add(adjacentLocationNode);

            adjacentLocationNode.addAdjacentNode(this);

            setChanged();
            notifyObservers();



        }

    }

    /**
     * The straight line distance between two nodes, ignoring the floor they are on.
     *
     * @param destinationLocationNode The node you want to get the distance to.
     * @return The distance between two nodes.
     */
    public double getDistanceBetweenNodes(LocationNode destinationLocationNode) {

        // location of destination node
        Location destinationLocation = destinationLocationNode.getLocation();

        // return the distance between the nodes
        return this.location.getDistanceBetween(destinationLocation);


//        int x = (int)abs((destinationLocationNode.location.getX())-(neighborsNode.getTempGoal().location.getX()));
//        int y = (int)abs((destinationLocationNode.location.getY())-(neighborsNode.getTempGoal().location.getY()));
//        double distance =  Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
//        return  distance;
    }

    /**
     * TODO
     *
     * @return
     */
    public double getHueristicCost() {

        return heuristicCost;
    }

    /**
     * TODO
     *
     * @return
     */
    public Location getLocation() {

        return location;
    }

    /**
     * TODO
     *
     * @param adjacentLocationNode
     */
    public void removeAdjacentNode(LocationNode adjacentLocationNode) {

        //if the node exists
        if (adjacentLocationNodes.contains(adjacentLocationNode)) {

            LOGGER.info("Deleting adjacent LocationNode: " + adjacentLocationNode.toString());

            //removes the node from list of adjacent Nodes
            adjacentLocationNodes.remove(adjacentLocationNode);

//            //removes this node from the other node's list of adjacent nodes
//            adjacentLocationNode.removeAdjacentNode(this);

            setChanged();

        }

        //call to observer to check if AdjacentNodes has changed
        notifyObservers();

    }

    /**
     * TODO
     *
     * @param cost
     */
    public void setHeuristicCost(double cost) {

        if (this.heuristicCost != cost) {

            this.heuristicCost = cost;
            setChanged();

        }

        notifyObservers();

    }

    public void drawNormalNode(Pane pane) {

        if (pane.getChildren().contains(nodeCircle)) {
            return;
        }

        pane.getChildren().add(this.nodeCircle);

    }

    public void drawAdminNode(Pane pane) {

        if (pane.getChildren().contains(nodeCircle)) {
            return;
        }

        pane.getChildren().add(this.nodeCircle);

        nodeCircle.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (getState() == BuildingState.ADDADJACENTNODE) {

                    if (currentFloor.getOtherLocationNode() == null) {

                        currentFloor.setOtherLocationNode(getCurrentNode());
                        LOGGER.info("NULLLSLDASDASDASD");

                    } else {

                        LOGGER.info("Adding adjacent node");

                        currentFloor.getOtherLocationNode().addAdjacentNode(getCurrentNode());

                        currentFloor.setOtherLocationNode(null);

                    }

                } else if (getState() == BuildingState.MODIFYDESTINATIONS) {

                    modifyNodeView();

                } else if (getState() == BuildingState.REMOVENODE) {

                    LOGGER.info("Deleting Node: " + toString());

                    deleteNode(pane);

                }

            }
        });

    }

    /**
     *
     *
     * @param pane
     */
    public void drawAdjacentNodes(Pane pane) {

        LOGGER.info("Drawing Adjacent Nodes");

        if (adjacentLines.size() != 0) {

            for (Line line : adjacentLines) {

                pane.getChildren().remove(line);

            }

            this.adjacentLines = new ArrayList<>();

        }

        for (LocationNode locationNode : this.adjacentLocationNodes) {

            drawAdjacentNode(pane, locationNode);

        }

    }

    public void drawAdjacentNode(Pane pane, LocationNode adjacentNode) {

        Line newLine  = new Line(this.location.getX(), this.location.getY(),
                adjacentNode.getLocation().getX(), adjacentNode.getLocation().getY());

        pane.getChildren().add(newLine);

        this.adjacentLines.add(newLine);

    }

    /**
     * Getter for the building's current state.
     *
     * @return The state of the building.
     */
    public BuildingState getState() {

        return this.currentFloor.getState();
    }

    /**
     * Sets the state of the building
     *
     * @param state The state you want to set the building to
     */
    public void setState(BuildingState state) {

        this.currentFloor.setState(state);

    }


    public void modifyNodeView() {

        try {

            Stage stage;
            stage = new Stage();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Kiosk/Views/AdminDepartmentPanel.fxml"));
            Parent root = (Parent)loader.load();
            AdminDepartmentPanelController controller = loader.<AdminDepartmentPanelController>getController();
            controller.setNode(this);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {

        }

    }

    public void deleteNode(Pane pane) {

        for (Line line : adjacentLines) {

            if (pane.getChildren().contains(line)) {

                pane.getChildren().remove(line);

            }

        }

        for (LocationNode node : this.adjacentLocationNodes) {

            node.removeAdjacentNode(getCurrentNode());

        }

        if (pane.getChildren().contains(nodeCircle)) {

            pane.getChildren().remove(nodeCircle);

        }

        notifyObservers();
        setChanged();

        this.currentFloor.removeLocationNode(this);

    }

    @Override
    public String toString() {

      //  return uniqueID.toString();
        return "" + this.location.getX();
    }

    @Override
    protected void finalize() throws Throwable {

        LOGGER.info("Deleting Node: " + toString());
        getCurrentFloor().getNodePane().getChildren().remove(this.nodeCircle);

        super.finalize();
    }

    public LocationNode getCurrentNode() {
        return this;
    }

    public Floor getCurrentFloor() {
        return currentFloor;
    }

    @Override
    public int compareTo(LocationNode o) {
        return Double.compare(minDistance,o.minDistance);
    }

    public Neighbors[] getNeighbors() {
        return neighbors;
    }

    public LocationNode getPrevious() {
        return previous;
    }

    public void setPrevious(LocationNode previous) {
        this.previous = previous;
    }
}

