import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;

import java.io.File;
import java.net.MalformedURLException;

public class View extends ScrollPane {
    private double scaleValue = 1.0;
    private double delta = 1.05;
    private Canvas canvas;

    public View(Canvas canvas) throws MalformedURLException {
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        this.setStyle("-fx-background-color: #000000;");

        this.canvas = canvas;

//        this.setFitToWidth(true);
//        this.setFitToHeight(true);
        this.setPannable(true);

        Group contentGroup = new Group();
        Group zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(canvas);

        this.setContent(contentGroup);
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);

        Scale scaleTransform = new Scale(scaleValue, scaleValue, 0, 0);
        zoomGroup.getTransforms().add(scaleTransform);

        class ZoomHandler implements EventHandler<ScrollEvent> {

            /**
             * Handles zooming by mouse wheel/scrolling. Finds the new scaleValue and applies it.
             * @param scrollEvent Event object fired on Scroll
             */
            @Override
            public void handle(ScrollEvent scrollEvent) {
                if (scrollEvent.getDeltaY() < 0)
                    scaleValue /= delta;    //smooth zoom out
                else
                    scaleValue *= delta;    //smooth zoom in

                double centerPosX = (zoomGroup.getLayoutBounds().getWidth() - getViewportBounds().getWidth())  * getHvalue() + getViewportBounds().getWidth()  / 2;
                double centerPosY = (zoomGroup.getLayoutBounds().getHeight() - getViewportBounds().getHeight())  * getVvalue() + getViewportBounds().getHeight()  / 2;

                //When zooming out, zoomGroup should only be zoomed until the entire content fits in the Viewport.
                //This prevents the content from being zoomed out of existence
                double nscale = Math.max(scaleValue, Math.min(getViewportBounds().getWidth() / zoomGroup.getLayoutBounds().getWidth(),
                        getViewportBounds().getHeight() / zoomGroup.getLayoutBounds().getHeight()));


                double newCenterX = centerPosX * nscale;
                double newCenterY = centerPosY * nscale;

                setHvalue((newCenterX - getViewportBounds().getWidth()/2) / (zoomGroup.getLayoutBounds().getWidth() * scaleValue - getViewportBounds().getWidth()));
                setVvalue((newCenterY - getViewportBounds().getHeight()/2) / (zoomGroup.getLayoutBounds().getHeight() * scaleValue  -getViewportBounds().getHeight()));

                //apply new scale
                scaleTransform.setX(nscale);
                scaleTransform.setY(nscale);
                scrollEvent.consume();
            }
        }

        //make sure that ScrollPane doesn't use ScrollEvent to pan
        addEventFilter(ScrollEvent.ANY, new ZoomHandler());

        Image img = new Image(new File("src/space.png").toURI().toURL().toExternalForm());

        canvas.getGraphicsContext2D().drawImage(img, 0, 0);

    }

    public Canvas getCanvas() {
        return this.canvas;
    }
}
