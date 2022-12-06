package com.example.pathfinder;


import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;


public class PathFinder extends Application {
    private Stage stage;
    private ImageView imageView = new ImageView();
    private BorderPane root;
    private Pane pane;

    private ListGraph<Place> lp = new ListGraph<>();
    private Place connectionOne;
    private Place connectionTwo;
    private Button newPlaceBtn = new Button("New Place");
    private Button newConnectionBtn = new Button("New Connection");
    private boolean changed;


    @Override
    public void start(Stage primaryStage) {

        this.stage = primaryStage;
        this.root = new BorderPane();
        this.pane = new Pane();
        pane.setId("outputArea");
        root.setCenter(pane);


        VBox vbox = new VBox();
        root.setTop(vbox);
        MenuBar menuBar = new MenuBar();
        vbox.getChildren().add(menuBar);
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);
        MenuItem newMap = new MenuItem("New Map");
        newMap.setOnAction(new NewMapHandler());
        newMap.setOnAction(new NewMapHandler());

        MenuItem open = new MenuItem("Open");
        open.setOnAction(new OpenHandler());
        MenuItem save = new MenuItem("Save");
        save.setOnAction(new SaveHandler());
        MenuItem saveImage = new MenuItem("Save Image");
        saveImage.setOnAction(new SaveImageHandler());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new ExitHandler());
        fileMenu.getItems().addAll(newMap, open, save, saveImage, exit);


        FlowPane flowPane = new FlowPane();

        flowPane.setAlignment(Pos.TOP_CENTER);
        Button findPathBtn = new Button("Find Path");
        findPathBtn.setOnAction(new FindPathHandler());
        Button showConnectionBtn = new Button("Show Connection");
        showConnectionBtn.setOnAction(new ShowConnectionHandler());

        newPlaceBtn.setOnAction(new NewPlaceHandler());


        newConnectionBtn.setOnAction(new NewConnectionHandler());

        Button changeConnectionBtn = new Button("Change Connection");
        changeConnectionBtn.setOnAction(new ChangeConnectionHandler());
        flowPane.getChildren().addAll(findPathBtn, showConnectionBtn, newPlaceBtn, newConnectionBtn,
                changeConnectionBtn);
        flowPane.setPadding(new Insets(15));
        flowPane.setHgap(10);
        vbox.getChildren().add(flowPane);

        fileMenu.setId("menuFile");
        menuBar.setId("menu");
        newMap.setId("menuNewMap");
        open.setId("menuOpenFile");
        save.setId("menuSaveFile");
        saveImage.setId("menuSaveImage");
        exit.setId("menuExit");
        findPathBtn.setId("btnFindPath");
        showConnectionBtn.setId("btnShowConnection");
        changeConnectionBtn.setId("btnChangeConnection");
        newPlaceBtn.setId("btnNewPlace");
        newConnectionBtn.setId("btnNewConnection");

        Scene scene = new Scene(root, 580, 100);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();

        /*     just to check that all files are in the right place
        File file = new File(".");
        for(String fileNames : file.list()) System.out.println(fileNames);

         */

    }

    private boolean changed() {
        if (changed) {
            return true;
        }
        return false;
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changed()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, continue anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    return;
                }
            }

            pane.getChildren().clear();
            lp = new ListGraph<>();
            connectionOne = null;
            connectionTwo = null;
            Image image = new Image("file:europa.gif");
            imageView.setImage(image);
            pane.getChildren().add(imageView);
            stage.setHeight(840);
            stage.setWidth(620);
        }
    }

    class OpenHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                if (changed()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Unsaved changes, continue anyway?");
                    Optional<ButtonType> res = alert.showAndWait();
                    if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                        return;
                    }
                }

                pane.getChildren().clear();
                lp = new ListGraph<>();
                connectionOne = null;
                connectionTwo = null;

                File file = new File("file:europa.graph");
                String fileName = file.getName();
                if (!fileName.equals("file:europa.graph")) {
                    Alert msgBox = new Alert(Alert.AlertType.ERROR);
                    msgBox.setTitle("Wrong format!");
                    msgBox.setHeaderText(null);
                    msgBox.setContentText("Wrong format!");
                    msgBox.showAndWait();
                    return;
                }


                Map<String, Place> graph = new HashMap<>();
                FileReader graphFile = new FileReader(file);
                BufferedReader in = new BufferedReader(graphFile);
                Image image = new Image(in.readLine());
                imageView.setImage(image);
                pane.getChildren().add(imageView);
                stage.setHeight(840);
                stage.setWidth(620);
                changed = true;

                String line = in.readLine();
                String[] spliters = line.split(";");
                for (int i = 0; i < spliters.length; i += 3) {
                    String name = spliters[i];
                    double x = Double.parseDouble(spliters[i + 1]);
                    double y = Double.parseDouble(spliters[i + 2]);
                    Place place = new Place(name, x, y);
                    place.setOnMouseClicked(new ClickHandlerConnection());
                    place.setFill(Color.BLUE);
                    Text text = new Text();
                    text.setText(name);
                    text.setY(y + 25);
                    text.setX(x + 2);
                    place.setFill(Color.BLUE);
                    lp.add(place);
                    graph.put(name, place);
                    pane.getChildren().addAll(place, text);
                    place.setId(name);
                    changed = true;
                }

                while ((line = in.readLine()) != null) {
                    String[] split = line.split(";");
                    String city1 = split[0];
                    String city2 = split[1];
                    String transport = split[2];
                    int weight = Integer.parseInt(split[3]);

                    Place node1 = graph.get(city1);
                    Place node2 = graph.get(city2);

                    if (lp.getEdgeBetween(node1, node2) == null) {
                        lp.connect(node1, node2, transport, weight);
                        Line line1 = new Line();

                        line1.startXProperty().bind(node1.centerXProperty().add(node1.translateXProperty()));
                        line1.startYProperty().bind(node1.centerYProperty().add(node1.translateYProperty()));
                        line1.endXProperty().bind(node2.centerXProperty().add(node2.translateXProperty()));
                        line1.endYProperty().bind(node2.centerYProperty().add(node2.translateYProperty()));

                        line1.setStrokeWidth(2);
                        pane.getChildren().add(line1);
                        line1.setDisable(true);
                        changed = true;
                    }
                }
                changed = true;
                graphFile.close();
                in.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                FileWriter writer = new FileWriter("file:europa.graph");
                PrintWriter out = new PrintWriter(writer);
                out.println("file:europa.gif");
                ArrayList<Place> places = new ArrayList<>(lp.getNodes());
                for (Place place : places) {
                    out.print(place.getCity() + ";" + place.getCenterX() + ";" + place.getCenterY() + ";");

                }
                out.println();
                for (Place place : places) {
                    ArrayList<Edge<Place>> edges = new ArrayList<>(lp.getEdgesFrom(place));
                    for (Edge<Place> edge : edges) {
                        out.println(place.getCity() + ";" + edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight());
                    }
                }

                out.close();
                writer.close();
                changed = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                WritableImage image = root.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));

            } catch (IOException e) {
                e.getMessage();
            }
        }
    }

    class ExitHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (changed()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, exit anyway?");
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    return;
                }
            }
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class NewPlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            pane.setOnMouseClicked(new ClickHandler());
            pane.setCursor(Cursor.CROSSHAIR);
        }
    }

    class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            double x = event.getX();
            double y = event.getY();

            pane.setCursor(Cursor.DEFAULT);
            newPlaceBtn.setDisable(false);
            pane.setOnMouseClicked(null);

            Alert msgBox = new Alert(Alert.AlertType.CONFIRMATION);
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));
            TextField name = new TextField();

            gridPane.add(new Label("Name of place: "), 0, 0);
            gridPane.add(name, 1, 0);
            msgBox.getDialogPane().setContent(gridPane);
            Optional<ButtonType> res = msgBox.showAndWait();

            if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                return;
            }

            String city = name.getText();
            Place place = new Place(city, x, y);
            place.setOnMouseClicked(new ClickHandlerConnection());
            Text text = new Text();
            text.setText(city);
            text.setY(y + 25);
            text.setX(x + 2);
            place.setFill(Color.BLUE);
            lp.add(place);
            place.setId(city);
            pane.getChildren().addAll(place, text);
            changed = true;
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (connectionOne == null || connectionTwo == null) {
                Alert alertOnlyOneSelected = new Alert(Alert.AlertType.ERROR, "Error! Two places must be selected!");
                alertOnlyOneSelected.setTitle("Error!");
                alertOnlyOneSelected.showAndWait();
                return;
            }

            if (lp.getEdgeBetween(connectionOne, connectionTwo) != null) {
                Alert alertAlreadyConnected = new Alert(Alert.AlertType.ERROR, "Error! There is already a connection between the selected places!");
                alertAlreadyConnected.setTitle("Error!");
                alertAlreadyConnected.showAndWait();
                return;
            }

            if (connectionOne == null || connectionTwo == null) {
                Alert alertOnlyOneSelected = new Alert(Alert.AlertType.ERROR, "Error! Two places must be selected!");
                alertOnlyOneSelected.setTitle("Error!");
                alertOnlyOneSelected.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connection");
            alert.setHeaderText("Connection from " + connectionOne.getCity() + " to " + connectionTwo.getCity());
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));
            TextField name = new TextField();
            TextField time = new TextField();

            gridPane.add(new Label("Name: "), 0, 0);
            gridPane.add(new Label("Time: "), 0, 1);
            gridPane.add(name, 1, 0);
            gridPane.add(time, 1, 1);

            alert.getDialogPane().setContent(gridPane);

            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                return;
            }

            if (name.getText().isEmpty()) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR, "Error! Wrong format, write the name of transport!");
                alert2.setTitle("Error!");
                alert2.showAndWait();
                return;
            } else {
                int weight;

                try {
                    weight = Integer.parseInt(time.getText());
                } catch (NumberFormatException e) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR, "Error! Wrong format, write an integer!");
                    alert1.setTitle("Error!");
                    alert1.showAndWait();
                    return;
                }

                if (lp.getEdgeBetween(connectionOne, connectionTwo) == null) {
                    lp.connect(connectionOne, connectionTwo, name.getText(), weight);
                    Line line = new Line();

                    line.startXProperty().bind(connectionOne.centerXProperty().add(connectionOne.translateXProperty()));
                    line.startYProperty().bind(connectionOne.centerYProperty().add(connectionOne.translateYProperty()));
                    line.endXProperty().bind(connectionTwo.centerXProperty().add(connectionTwo.translateXProperty()));
                    line.endYProperty().bind(connectionTwo.centerYProperty().add(connectionTwo.translateYProperty()));

                    line.setStrokeWidth(3);
                    pane.getChildren().add(line);
                    line.setDisable(true);
                    changed = true;
                }
            }

        }
    }

    class ClickHandlerConnection implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Place place = (Place) event.getSource();
            if (place.getFill().equals(Color.RED)) {
                place.setColor(Color.BLUE);
                if (place == connectionOne) {
                    connectionOne = null;
                } else if (place == connectionTwo) {
                    connectionTwo = null;
                }
            } else if (connectionOne == null) {
                connectionOne = place;
                connectionOne.setColor(Color.RED);
            } else if (connectionTwo == null && place != connectionTwo) {
                connectionTwo = place;
                connectionTwo.setColor(Color.RED);
            }
            pane.setCursor(Cursor.DEFAULT);
            pane.setOnMouseClicked(null);
        }
    }

    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (connectionOne == null || connectionTwo == null) {
                Alert alertOnlyOneSelected = new Alert(Alert.AlertType.ERROR);
                alertOnlyOneSelected.setTitle("Error!");
                alertOnlyOneSelected.setHeaderText("Two places must be selected!");
                alertOnlyOneSelected.showAndWait();
            } else {
                List<Edge<Place>> list = lp.getPath(connectionOne, connectionTwo);
                AlertPath alertPath = new AlertPath(connectionOne, connectionTwo, list);
                alertPath.showAndWait();

                if (lp.getPath(connectionOne, connectionTwo) == null) {
                    Alert noTimeAlert = new Alert(Alert.AlertType.ERROR, "There is no connection!");
                    noTimeAlert.showAndWait();
                    return;
                }
            }
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (connectionOne == null || connectionTwo == null) {
                Alert alertOnlyOneSelected = new Alert(Alert.AlertType.ERROR);
                alertOnlyOneSelected.setTitle("Error!");
                alertOnlyOneSelected.setHeaderText("Two places must be selected!");
                alertOnlyOneSelected.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Connection");
                alert.setHeaderText("Connection from " + connectionOne.getCity() + " to " + connectionTwo.getCity());
                GridPane gridPane = new GridPane();

                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.setPadding(new Insets(20, 150, 10, 10));
                TextField name = new TextField();
                TextField time = new TextField();

                gridPane.add(new Label("Name: "), 0, 0);
                gridPane.add(new Label("Time: "), 0, 1);
                gridPane.add(name, 1, 0);
                gridPane.add(time, 1, 1);
                Edge edge = lp.getEdgeBetween(connectionOne, connectionTwo);

                if (lp.getEdgeBetween(connectionOne, connectionTwo) == null) {
                    Alert noTimeAlert = new Alert(Alert.AlertType.ERROR, "There is no connection!");
                    noTimeAlert.showAndWait();
                    return;
                }

                name.setText(edge.getName());
                time.setText("" + edge.getWeight());
                name.setEditable(false);
                time.setEditable(false);

                alert.getDialogPane().setContent(gridPane);
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    return;
                }
            }
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (connectionOne == null || connectionTwo == null) {
                Alert alertOnlyOneSelected = new Alert(Alert.AlertType.ERROR);
                alertOnlyOneSelected.setTitle("Error!");
                alertOnlyOneSelected.setHeaderText("Two places must be selected!");
                alertOnlyOneSelected.showAndWait();
            } else {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Connection");
                alert.setHeaderText("Connection from " + connectionOne.getCity() + " to " + connectionTwo.getCity());
                GridPane gridPane = new GridPane();

                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.setPadding(new Insets(20, 150, 10, 10));
                Edge<Place> edge = lp.getEdgeBetween(connectionOne, connectionTwo);
                TextField name = new TextField(edge.getName());
                name.setEditable(false);
                TextField time = new TextField(edge.getWeight() + " ");

                gridPane.add(new Label("Name: "), 0, 0);
                gridPane.add(new Label("Time: "), 0, 1);
                gridPane.add(name, 1, 0);
                gridPane.add(time, 1, 1);

                alert.getDialogPane().setContent(gridPane);

                int weight;
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    return;
                }
                if (lp.getEdgeBetween(connectionOne, connectionTwo) == null) {
                    Alert noTimeAlert = new Alert(Alert.AlertType.ERROR, "There is no connection!");
                    noTimeAlert.showAndWait();
                    return;
                } else {
                    try {
                        weight = Integer.parseInt(time.getText());

                    } catch (NumberFormatException e) {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR, "Wrong format, write an integer!");
                        alert1.showAndWait();
                        return;
                    }
                }
                lp.setConnectionWeight(connectionOne, connectionTwo, weight);
                changed = true;
            }
        }
    }
}
