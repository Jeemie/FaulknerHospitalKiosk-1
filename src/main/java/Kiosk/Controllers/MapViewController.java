package Kiosk.Controllers;

import Kiosk.KioskApp;
import Map.Building;
import Map.Exceptions.FloorDoesNotExistException;
import Map.LocationNode;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class MapViewController{

    // Reference to the main application.

    private boolean okClicked = false;
    private KioskApp kioskApp;
    private Building mMainHost;
    private LocationNode startNode;
    private LocationNode destinationNode;
    private int numThreads = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(MapViewController.class);
    @FXML
    private Label currentFloorLabel;

    @FXML
    private TextField searchTextBox;

    @FXML
    private StackPane imageStackPane;

    @FXML
    private Button confirmButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button changeFloorButtonUp;

    @FXML
    private Button changeFloorButtonDown;

    @FXML

    private ListView Direction;


    public Timer timer;
    public Timer atimer;

    int counter = 0;
    int getCounterFloor=1;
    private volatile boolean running = true;

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            counter++;
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (running) {
                try {
                    if (counter == 60) {
                        System.out.println("Timed Out.");
                        running = false;
                        timer.cancel();
                        timer.purge();
                        atimer.cancel();
                        atimer.purge();
                        timerTask.cancel();
                        Platform.runLater(resetKiosk);
                        break;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    atimer.cancel();
                    timer.cancel();
                    timerTask.cancel();
                    running = false;
                    exception.printStackTrace();
                    break;
                }

            }
        }
    };

    Thread timerThread;

    Runnable resetKiosk = new Runnable() {

        @Override
        public void run() {
            handleBack();
        }
    };





    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {


        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);






        //destinationNode.getCurrentFloor().drawFloorAdmin(imageStackPane);
//        System.out.println(destinationNode.getLocation().getX());
//
//        System.out.println(destinationNode.getLocation().getY());

        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                counter = 0;

                destinationNode.getCurrentFloor().drawFloorAdmin(imageStackPane);
                scrollPane.setHvalue(destinationNode.getLocation().getX()/imageStackPane.getWidth());
//                scrollPane.setVvalue(destinationNode.getLocation().getY()/imageStackPane.getHeight());
//                //mMainHost.drawShortestPath(startNode, destinationNode);
//                System.out.println(destinationNode.getLocation().getX()/imageStackPane.getWidth());
//                currentFloorLabel.setText(String.valueOf(destinationNode.getCurrentFloor()));

                mMainHost.drawShortestPath(startNode, destinationNode);
                imageStackPane.setMaxHeight(mMainHost.getyMax());
                imageStackPane.setMinHeight(mMainHost.getyMin());
                imageStackPane.setMaxWidth(mMainHost.getxMax());
                imageStackPane.setMinWidth(mMainHost.getyMin());
                //System.out.println(destinationNode.getLocation().getX()/imageStackPane.getWidth());
                currentFloorLabel.setText(String.valueOf(destinationNode.getCurrentFloor()));

            }

        });





        searchTextBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().equals(KeyCode.ENTER)) {

                    timer.cancel();
                    running = false;
                    timerThread.interrupt();
                    LOGGER.info("Blah " + searchTextBox.getText());
                    kioskApp.showSearch(searchTextBox.getText());

                } else {

                    counter = 0;

                }
            }
        });
        changeFloorButtonDown.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                getCounterFloor--;
                if (getCounterFloor >= 1) {
                    currentFloorLabel.setText(String.valueOf(getCounterFloor));
                } else { getCounterFloor = 1;}
                try {
                    mMainHost.getFloor(getCounterFloor).drawFloorNormal(imageStackPane);
                    if(counter<1){
                        counter =1;
                    }
                } catch (FloorDoesNotExistException e) {
                    e.printStackTrace();
                }
                System.out.println(getCounterFloor);

            }
        });




        scrollPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(numThreads == 0) {
                    numThreads +=1;
                    running = true;
                    timer = new Timer("A Timer");
                    atimer = new Timer("A Timer2");
                    timerThread = new Thread(runnable);
                    timer.scheduleAtFixedRate(timerTask, 30, 1000);
                    timerThread.start();
                }
                counter = 0;
            }
        });





        //timer.scheduleAtFixedRate(timerTask, 30, 1000);

        //timerThread.start();

        changeFloorButtonUp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                getCounterFloor++;
                if (getCounterFloor <= 7) {
                    currentFloorLabel.setText(String.valueOf(getCounterFloor));
                } else {getCounterFloor = 7;}
                try {
                    mMainHost.getFloor(getCounterFloor).drawFloorNormal(imageStackPane);
                    if(counter>4){
                        counter =4;
                    }
                } catch (FloorDoesNotExistException e) {
                    e.printStackTrace();
                }
                System.out.println(getCounterFloor);
               // building.drawShortestPath(startNode, destinationNode);

            }

        });


    }




    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param kioskApp
     */
    public void setKioskApp(KioskApp kioskApp) {
        this.kioskApp = kioskApp;
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }


    /**
     * Called when the user clicks back.
     */
    // TODO: handleBack should have an if statement, which will go to
    // either userUI2 or userUI3 depending on which screen userUI4 was
    // accessed from
    @FXML
    private void handleBack() {

        handleCancel();
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {

        atimer.cancel();
        atimer.purge();
        timer.cancel();
        timer.purge();
        running = false;
        timerThread.interrupt();
        kioskApp.reset();
    }

    public void setBuilding(Building building) {
        this.mMainHost = building;
    }

    public void setDestinationNode(LocationNode destinationNode){

/*      atimer.cancel();
        atimer.purge();
        timer.cancel();
        timer.purge();*/
        running = false;
        //timerThread.interrupt();
        this.destinationNode = destinationNode;
        destinationNode.getCurrentFloor().drawFloorAdmin(this.imageStackPane);

    }

    public void setStartNode(LocationNode startNode) {

        this.startNode = startNode;
    }

}
