package Map;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by maryannoconnell on 4/16/16.
 */
public class Dijkstras {

    private final Building building; // The building that will be associated with the AStar search
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Dijkstras.class); // Logger for this class

    /**
     * TODO
     *
     * @param building
     */
    public Dijkstras(Building building) {

        this.building = building;

    }

    public static void constructPath(LocationNode node) {
        //distance from start to start is 0
        node.minDistance = 0.;
        //create node set nodeQueue
        PriorityQueue<LocationNode> nodeQueue = new PriorityQueue<>();
        nodeQueue.add(node);
        //while nodeQueue is not empty
        while (!nodeQueue.isEmpty()) {
            //start node will be selected first
            LocationNode u = nodeQueue.poll();
            //for each neighbor e of u
            for (LocationNode e : u.getAdjacentLocationNodes()) {
                double cost = e.getHeuristicCost();
                //calculate the distance to that neighbor
                double distanceThroughNeighbor = u.minDistance + cost + u.getDistanceBetweenNodes(e);
                if (distanceThroughNeighbor < e.minDistance) {
                    //replace path with a shorter path and remove node u to the node set
                    nodeQueue.remove(u);
                    e.minDistance = distanceThroughNeighbor;
                    e.previous = u;
                    nodeQueue.add(e);
                }
            }

        }
    }

    public static List<LocationNode> getShortestPathTo(LocationNode target)
    {
        List<LocationNode> path = new ArrayList<LocationNode>();

        for (LocationNode node = target; node != null; node=node.previous) {
            path.add(node);
        }

        Collections.reverse(path);
        return path;
    }



}
