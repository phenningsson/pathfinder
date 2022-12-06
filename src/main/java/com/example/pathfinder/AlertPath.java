package com.example.pathfinder;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.util.List;

public class AlertPath extends Alert {

    private TextArea textArea = new TextArea();

    public AlertPath(Place from, Place to, List<Edge<Place>> list) {
        super(AlertType.INFORMATION);

        int total = 0;
        for(Edge<Place> placeEdge : list) {
            textArea.appendText(placeEdge.toString() + "\n");
            total += placeEdge.getWeight();
        }

        textArea.appendText("Total " + total);
        setHeaderText("The path from " + from.getCity() + " to " + to.getCity() + ":");
        textArea.setEditable(false);
        getDialogPane().setContent(textArea);

    }
}