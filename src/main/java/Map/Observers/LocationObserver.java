package Map.Observers;

import Map.Location;
import Map.LocationNodeRefactored;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by matt on 4/18/16.
 */
public class LocationObserver implements Observer {

    private final Location observedLocation;
    private final LocationNodeRefactored associatedLocationNode;

    public LocationObserver(Location observedLocation, LocationNodeRefactored associatedLocationNode) {

        this.observedLocation = observedLocation;
        this.associatedLocationNode = associatedLocationNode;

    }

    @Override
    public void update(Observable o, Object arg) {

        // TODO redraw things

    }

}
