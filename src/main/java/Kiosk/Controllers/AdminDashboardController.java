package Kiosk.Controllers;

//import Kiosk.Controllers.EventHandlers.ChangeMapStateEventHandler;

import Kiosk.Controllers.EventHandlers.AddTabEventHandler;
import Kiosk.Controllers.EventHandlers.ChangeMapStateEventHandler;
import Kiosk.KioskApp;
import Map.Enums.ImageType;
import Map.Enums.MapState;
import Map.Map;
import Map.Floor;
import Map.Destination;
import Map.LocationNode;
import Map.Location;
import Map.LocationNodeEdge;
import Utils.FixedSizedStack;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Controller for the admin dashboard view
 */
public class AdminDashboardController {

    private Map faulknerHospitalMap;

    private KioskApp kioskApp;

    private ObservableList icons = FXCollections.observableArrayList();

    private boolean lockTabPane;

    private Location clickedLocation;


    // TODO possibly rethink
    private FixedSizedStack<Destination> previouslyClickedDestinations = new FixedSizedStack<>(10);

    private FixedSizedStack<LocationNodeEdge> previouslyClickedEdges = new FixedSizedStack<>(10);

    private FixedSizedStack<LocationNode> previouslyClickedLocationNodes = new FixedSizedStack<>(10);

    private FixedSizedStack<Destination> previouslyClickedFloors = new FixedSizedStack<>(10);


    // Logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDashboardController.class);

    @FXML
    private ScrollPane mapScrollPane;

    // TODO find out use and rename
    @FXML
    private Label selectedButtonLabel;

    @FXML
    private StackPane mapStackPane;

    @FXML
    private Button zoomOutButton;

    @FXML
    private Button zoomInButton;

    @FXML
    private Slider zoomSlider;

    @FXML
    private TabPane mapTabPane;


    //\\ Building Tab //\\
    @FXML
    private Tab buildingTab;

    @FXML
    private Accordion buildingAccordion;


    // Floors Titled Pane //
    @FXML
    private TitledPane buildingFloorsTitledPane;

    @FXML
    private ListView buildingFloorsListView;

    @FXML
    private Button buildingFloorsAddButton;

    @FXML
    private Button buildingFloorsModifyButton;

    @FXML
    private Button buildingFloorsDeleteButton;


    // Destinations Titled Pane //
    @FXML
    private TitledPane buildingDestinationsTitledPane;

    @FXML
    private ListView buildingDestinationsListView;


    // Destinations Titled Pane //
    @FXML
    private TitledPane buildingMiscTitledPane;

    @FXML
    private Button setStartNode;

    @FXML
    private Button astarButton;

    @FXML
    private Button dijkstrasButton;


    //\\ Floor Tab //\\
    @FXML
    private Tab floorTab;

    @FXML
    private Accordion floorAccordion;


    // Locations Titled Pane //
    @FXML
    private TitledPane floorLocationsTitledPane;

    @FXML
    private ListView floorLocationsListView;

    @FXML
    private Button floorLocationsAddButton;

    @FXML
    private Button floorLocationsModifyButton;

    @FXML
    private Button floorLocationsDeleteButton;


    // Floor Destinations Titled Pane //
    @FXML
    private TitledPane floorDestinationsTitledPane;

    @FXML
    private ListView floorDestinationsListView;


    // Floor Information Titled Pane //
    @FXML
    private TitledPane floorInformationTitledPane;


    //\\ Location Tab //\\
    @FXML
    private Tab locationTab;

    @FXML
    private Accordion locationAccordion;


    // Connected Locations Titled Pane //
    @FXML
    private TitledPane locationConnectedLocationsTitledPane;

    @FXML
    private Button locationConnectedLocationsAddButton;

    @FXML
    private Button locationConnectedLocationsDeleteButton;
    @FXML
    private ListView locationConnectedLocationListView;
    // Destinations Titled Pane //
    @FXML
    private TitledPane locationDestinationsTitledPane;

    @FXML
    private ListView locationDestinationsListView;

    @FXML
    private Button locationDestinationsAddButton;

    @FXML
    private Button locationDestinationsModifyButton;

    @FXML
    private Button locationDestinationsDeleteButton;


    // Information Titled Pane //
    @FXML
    private TitledPane locationInformationTitledPane;


    @FXML
    private Tab addFloorTab;


    @FXML
    private Tab addLocationTab;

    @FXML
    private ListView addLocationIconsListView;

    @FXML
    private TextField addLocationNameTextField;

    @FXML
    private Button addLocationAddButton;

    @FXML
    private Button addLocationDiscardButton;


    @FXML
    private Button discardChangesButton;

    @FXML
    private Button saveChangesButton;

    @FXML
    private Button logoutButton;


    private int counter = 0;

    final double SCALE_DELTA = 1.1;


    public void setListeners() {

        this.icons.setAll(ImageType.values());
        this.lockTabPane = false;
        this.clickedLocation = new Location(0, 0);


        // Setup Listeners
        setCoreFunctionalityListeners();
        setBuildingTabListeners();
        setFloorTabListeners();
        setLocationTabListeners();
        setAddFloorTabListeners();
        setAddLocationTabListeners();


    }


    private void setCoreFunctionalityListeners() {


        this.faulknerHospitalMap.setupAdminStackPane(this.mapStackPane);

        final Group scrollContent = new Group(mapStackPane);
        mapScrollPane.setContent(scrollContent);

        // Setup Logout Button
        this.logoutButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                faulknerHospitalMap.setCurrentMapState(MapState.NORMAL);

                // TODO Add saving prompt if changes have been made

                LOGGER.info("Logging out of the Admin Dashboard");

                kioskApp.reset();

            }

        });

        // Setup Discard Changes Button
        this.discardChangesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                // TODO Reload building from file

                LOGGER.info("Discarding changes to the map");

            }

        });

        // Setup Save Changes Button
        this.saveChangesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            public URL saveFilePath;

            @Override
            public void handle(MouseEvent event) {

                LOGGER.info("Attempting to save changes to the map");


                try {

                    this.saveFilePath = new URL("file:///" + System.getProperty("user.dir") + "/resources/" + "default.json");

                } catch (MalformedURLException e) {

                    e.printStackTrace();
                }

                try {

                    File saveFile = new File(saveFilePath.toURI());

                    faulknerHospitalMap.saveToFile(saveFile);

                    LOGGER.info("Changes were saved");

                } catch (IOException e) {

                    LOGGER.error("An error occurred while saving changes to the map", e);

                } catch (URISyntaxException e) {

                    LOGGER.error("An error occurred while saving changes to the map", e);

                }

            }

        });

        // Setup Zoom Slider
        this.zoomSlider.setMax(1.5);
        this.zoomSlider.setMin(0.5);
        this.zoomSlider.setValue(0.5);
        this.zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                LOGGER.info("Zoom slider has been moved from " + oldValue + " to " + newValue);

                // TODO fix zooming

//                double scrollH = mapScrollPane.getHvalue();
//                double scrollV = mapScrollPane.getVvalue();
//
//                LOGGER.info("" + scrollH);
//                LOGGER.info("" + scrollV);
//                zoomSlider.setScaleX(newValue.doubleValue());
//                zoomSlider.setScaleY(newValue.doubleValue());
//                mapScrollPane.setHvalue(scrollH);
//                mapScrollPane.setVvalue(scrollV);

            }

        });


        // Setup Zoom In Button
        this.zoomInButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                LOGGER.info("Zooming In");

                zoomSlider.setValue(zoomSlider.getValue() + 0.1);

                double scaleFactor = 1.1;

                scaleFactor = SCALE_DELTA;
                if (counter < 10) {
                    counter += 1;
                }

                if (counter > 0 && counter < 10) {
                    Point2D scrollOffset = figureScrollOffset(scrollContent, mapScrollPane);

                    mapStackPane.setScaleX(mapStackPane.getScaleX() * scaleFactor);
                    mapStackPane.setScaleY(mapStackPane.getScaleY() * scaleFactor);

                    // move viewport so that old center remains in the center after the
                    // scaling
                    repositionScroller(scrollContent, mapScrollPane, scaleFactor, scrollOffset);
                } else {
                    return;
                }
            }

        });

        // Setup Zoom Out Button
        this.zoomOutButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                LOGGER.info("Zooming out");

                zoomSlider.setValue(zoomSlider.getValue() - 0.1);

                double scaleFactor = 1.1;

                scaleFactor = 1 / SCALE_DELTA;
                if (counter > 0) {
                    counter -= 1;
                }

                if (counter > 0 && counter < 10) {
                    Point2D scrollOffset = figureScrollOffset(scrollContent, mapScrollPane);

                    mapStackPane.setScaleX(mapStackPane.getScaleX() * scaleFactor);
                    mapStackPane.setScaleY(mapStackPane.getScaleY() * scaleFactor);

                    // move viewport so that old center remains in the center after the
                    // scaling
                    repositionScroller(scrollContent, mapScrollPane, scaleFactor, scrollOffset);
                } else {
                    return;
                }

            }

        });


        // Setup Tab Pane
        this.mapTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {


                if (lockTabPane && (oldValue.equals(addFloorTab) || oldValue.equals(addLocationTab))) {

                    mapTabPane.getSelectionModel().select(oldValue);

                    return;
                }

                if (newValue.equals(addFloorTab) || newValue.equals(addLocationTab)) {

                    lockTabPane = true;

                    return;
                }

                LOGGER.info("The selected tab is currently " + newValue.getText());

            }

        });

        this.mapStackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                clickedLocation = new Location(event.getX(), event.getY());

                LOGGER.info("The current clicked location is " + clickedLocation.getX() + ", " + clickedLocation.getY());

            }

        });


        this.mapStackPane.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
                event.consume();

                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor = 1.1;
                if (event.getDeltaY() > 0) {
                    scaleFactor = SCALE_DELTA;
                    if (counter < 10) {
                        zoomSlider.setValue(zoomSlider.getValue() + 0.1);
                        counter += 1;
                    }
                } else {
                    scaleFactor = 1 / SCALE_DELTA;
                    if (counter > 0) {
                        zoomSlider.setValue(zoomSlider.getValue() - 0.1);
                        counter -= 1;
                    }
                }


                // amount of scrolling in each direction in scrollContent coordinate
                // units

                if (counter > 0 && counter < 10) {
                    Point2D scrollOffset = figureScrollOffset(scrollContent, mapScrollPane);

                    mapStackPane.setScaleX(mapStackPane.getScaleX() * scaleFactor);
                    mapStackPane.setScaleY(mapStackPane.getScaleY() * scaleFactor);

                    // move viewport so that old center remains in the center after the
                    // scaling
                    repositionScroller(scrollContent, mapScrollPane, scaleFactor, scrollOffset);
                } else {
                    return;
                }
            }

        });
    }


    private Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }


    private void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            double newScrollXOffset = (scaleFactor - 1) * halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }


    private void setBuildingTabListeners() {

        // TODO do something with the building tab


        // Setup Building Accordion
        this.buildingAccordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

            @Override
            public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) {

                if (newValue != null) {

                    LOGGER.info("In the building tab the " + newValue.getText() + " Titled Pane has been expanded");

                } else {

                    LOGGER.info("In the building tab the " + oldValue.getText() + " Titled Pane has been closed");

                }

            }

        });

        this.buildingFloorsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {

                    LOGGER.info("Building Floors Titled Pane Opened");

                }

            }

        });

        this.buildingFloorsListView.setItems(this.faulknerHospitalMap.getCurrentBuildingFloors());
        this.buildingFloorsListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Floor currentFloor = ((Floor) buildingFloorsListView.getSelectionModel().getSelectedItem());

                faulknerHospitalMap.setCurrentFloor(currentFloor);

            }

        });

        this.buildingFloorsAddButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

//                AdminSubControllerLoader loader = new AdminSubControllerLoader();
//
//                loader.setStackPane(mapStackPane);
//                loader.setCurrentBuilding(building);
//                loader.loadAddFloor();
//
//
//                SubViewLoader<AdminDashboardAddFloorController> subViewLoader =
//                        new SubViewLoader<>("Views/AdminDashboardSubViews/AdminDashboardAddFloor.fxml", mapStackPane);
//
//                AdminDashboardAddFloorController adminDashboardAddFloorController = subViewLoader.loadView();
//
//
//                adminDashboardAddFloorController.setCurrentBuilding(building);
//                adminDashboardAddFloorController.setSubViewLoader(subViewLoader);
//                adminDashboardAddFloorController.setListeners();

            }

        });


        this.buildingDestinationsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {

                    LOGGER.info("Building Destinations Titled Pane Opened");

//                    building.addBuildingDestinationsToListView(buildingDestinationsListView);

                }

            }

        });


        this.buildingDestinationsListView.setItems(this.faulknerHospitalMap.getCurrentBuildingDestinations());
        this.buildingDestinationsListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Destination currentDestination = ((Destination) buildingDestinationsListView.getSelectionModel().getSelectedItem());

                faulknerHospitalMap.setCurrentDestination(currentDestination);

            }

        });

        this.astarButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                LOGGER.info("Switching to AStart Algorithm");

                faulknerHospitalMap.useAStar();

            }

        });

        this.dijkstrasButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                LOGGER.info("Switching to Diskstra's Algorithm");

                faulknerHospitalMap.useDijkstras();

            }

        });


    }


    private void setFloorTabListeners() {

        // Setup Building Accordion
        this.floorAccordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

            @Override
            public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) {

                if (newValue != null) {

                    LOGGER.info("In the Floor tab the " + newValue.getText() + " Titled Pane has been expanded");

                } else {

                    LOGGER.info("In the Floor tab the " + oldValue.getText() + " Titled Pane has been closed");

                }

            }

        });

        this.floorLocationsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {

                    LOGGER.info("Building Floors Titled Pane Opened");

//                    if (building.getCurrentFloor() != null) {
//
//                        building.getCurrentFloor().addLocationToListView(floorLocationsListView);
//
//                    }


                }

            }

        });

        this.floorLocationsListView.setItems(this.faulknerHospitalMap.getCurrentFloorLocationNodes());
        this.floorLocationsListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                LocationNode currentLocationNode = ((LocationNode) floorLocationsListView.getSelectionModel().getSelectedItem());

                faulknerHospitalMap.setCurrentLocationNode(currentLocationNode);

                switch (faulknerHospitalMap.getCurrentMapState()) {

                    case REMOVENODE:

                        faulknerHospitalMap.removeLocationNode();

                        break;

                    case MOVENODE:

                        break;

                    default:

                        break;

                }

            }

        });

        this.floorLocationsAddButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new AddTabEventHandler(MapState.ADDNODE,
                this.faulknerHospitalMap, "Add Location", this.selectedButtonLabel, this.mapTabPane,
                this.addLocationTab));

        this.floorLocationsModifyButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new ChangeMapStateEventHandler(this.faulknerHospitalMap, MapState.MOVENODE, "Modify Button",
                        this.selectedButtonLabel));

        this.floorLocationsDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new ChangeMapStateEventHandler(this.faulknerHospitalMap, MapState.REMOVENODE, "Delete Button",
                        this.selectedButtonLabel));


        this.floorDestinationsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                LOGGER.info("Building Floors Titled Pane Opened");

            }

        });

        this.floorDestinationsListView.setItems(this.faulknerHospitalMap.getCurrentFloorDestinations());
        this.floorDestinationsListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Destination currentDestination = ((Destination) floorDestinationsListView.getSelectionModel().getSelectedItem());

                faulknerHospitalMap.setCurrentDestination(currentDestination);

            }

        });

    }

    private void setLocationTabListeners() {


        // Setup Building Accordion
        this.locationAccordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {

            @Override
            public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) {

                if (newValue != null) {

                    LOGGER.info("In the Location tab the " + newValue.getText() + " Titled Pane has been expanded");

                } else {

                    LOGGER.info("In the Location tab the " + oldValue.getText() + " Titled Pane has been closed");

                }

            }

        });

        this.locationConnectedLocationsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {

                    LOGGER.info("Building Floors Titled Pane Opened");

//                    building.getCurrentNodes().addAdjacentsToListView(locationConnectedLocationListView);


                }

            }


        });


        this.locationConnectedLocationListView.setItems(this.faulknerHospitalMap.getCurrentAdjacentLocationNodes());
        this.locationConnectedLocationListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                // TODO

            }

        });

        this.locationDestinationsTitledPane.expandedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                if (newValue) {

                    LOGGER.info("Building Floors Titled Pane Opened");

//                    if (building.getCurrentNodes() != null) {
//
//                        building.getCurrentNodes().addDestinationsToListView(locationDestinationsListView);
//
//                    }


                }

            }


        });

        this.locationDestinationsListView.setItems(this.faulknerHospitalMap.getCurrentLocationNodeDestinations());
        this.locationDestinationsListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Destination currentDestination = ((Destination) locationDestinationsListView.getSelectionModel().getSelectedItem());

                faulknerHospitalMap.setCurrentDestination(currentDestination);

            }

        });


        locationConnectedLocationsAddButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new ChangeMapStateEventHandler(this.faulknerHospitalMap, MapState.ADDADJACENTNODE, "Add Connected " +
                        "Button", this.selectedButtonLabel));

        locationDestinationsAddButton.setOnAction(event -> {
            selectedButtonLabel.setText("Add Destination Button");
//            this.locationDestinationsAddButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new ChangeMapStateEventHandler(building, MapState.MODIFYDESTINATIONS));
        });

        locationConnectedLocationsDeleteButton.setOnAction(event -> {

            selectedButtonLabel.setText("Delete Destination Button");
//            this.locationConnectedLocationsDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new ChangeMapStateEventHandler(building, MapState.REMOVENODE));
        });


        this.setStartNode.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                faulknerHospitalMap.setStartLocationNode(faulknerHospitalMap.getCurrentLocationNode());

            }

        });

    }

    private void setAddFloorTabListeners() {

        this.mapTabPane.getTabs().remove(this.addFloorTab);


    }

    private void setAddLocationTabListeners() {

        this.mapTabPane.getTabs().remove(this.addLocationTab);

        this.addLocationIconsListView.setCellFactory(listView -> new ListCell<ImageType>() {

            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(80);
                imageView.setFitWidth(160);
                imageView.setPreserveRatio(true);
            }

            @Override
            public void updateItem(ImageType imageType, boolean empty) {
                super.updateItem(imageType, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(imageType.toString());

                    try {

                        Image icon = new Image(new URL("file:///" + System.getProperty("user.dir") + "/resources/" +
                                imageType.getResourceFileName()).toString(), true);

                        imageView.setImage(icon);

                    } catch (MalformedURLException e) {

                        LOGGER.error("Unable to show the icon  in the addLocationIconsListView", e);

                    }

                    setGraphic(imageView);
                }
            }
        });

        this.addLocationIconsListView.setItems(this.icons);


        this.addLocationNameTextField.getText();

        this.addLocationAddButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (addLocationNameTextField.getText().length() < 1) {

                    return;
                }

                if (addLocationIconsListView.getSelectionModel().getSelectedItem() == null) {

                    return;
                }

                ImageType selectedImageType = (ImageType) addLocationIconsListView.getSelectionModel().getSelectedItem();

                faulknerHospitalMap.addLocationNode(addLocationNameTextField.getText(), clickedLocation, selectedImageType);

                lockTabPane = false;

                mapTabPane.getTabs().remove(addLocationTab);

                mapTabPane.getSelectionModel().select(floorTab);

                addLocationNameTextField.setText("");

            }

        });

        this.addLocationDiscardButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                lockTabPane = false;

                mapTabPane.getTabs().remove(addLocationTab);

                mapTabPane.getSelectionModel().select(floorTab);

                addLocationNameTextField.setText("");

            }

        });

    }


    public void setFaulknerHospitalMap(Map faulknerHospitalMap) {

        this.faulknerHospitalMap = faulknerHospitalMap;

    }

    public void setKioskApp(KioskApp kioskApp) {

        this.kioskApp = kioskApp;

    }

}