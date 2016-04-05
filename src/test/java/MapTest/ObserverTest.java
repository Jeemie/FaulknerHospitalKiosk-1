package MapTest;

import Map.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.UUID;

/**
 * Created by mharris382 on 4/5/2016.
 */
public class ObserverTest {
    private Building mMainHospital;
    private Floor mFloor;
    private Location mLocation1, mLocation2, mLocation3;
    private Node mTestNode;
    private Node mAdjNode1, mAdjNode2;

    private NodeObserver mObserver = new NodeObserver();

    private EnumMap<Destination, ArrayList<String>> mDestinations;
    @Test
    public void initialize(){
        mMainHospital = new Building();

        mFloor = mMainHospital.getFloor(0);

        mLocation1 = new Location(10, 10);
        mLocation2 = new Location(23, 6);
        mLocation3 = new Location(8, 21);

        mTestNode = new Node(420.69, UUID.randomUUID(), mLocation1, mFloor, mDestinations, mObserver);
        mAdjNode1 = new Node(10.25, UUID.randomUUID(), mLocation2, mFloor, mDestinations, mObserver);
        mAdjNode2 = new Node(12.32, UUID.randomUUID(), mLocation3, mFloor, mDestinations, mObserver);




    }

    @Test
    public void testAddAdjacentNode(){
        mTestNode.addAdjacentNode(mAdjNode1);
        mTestNode.addAdjacentNode(mAdjNode2);
    }

    @Test
    public void testRemoveAdjacentNode(){

        mTestNode.removeAdjacentNode(mAdjNode2);
    }

    @Test
    public void testAddDestination(){
        mTestNode.addDestination(Destination.PHYSICIAN, "Dr. Phil");
    }
/*
    @Test void testRemoveDestination(){
        mTestNode.removeDestination(Destination.PHYSICIAN, "Dr. Phil");

    }
*/

    @Test
    public void run(){
        initialize();
        testAddAdjacentNode();
    }
}
