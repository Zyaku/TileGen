
import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class View {

	private Model model;
	private Stage stage;
	private GridPane gridpane;
	private Scene scene ;
	private Pane [][] panes;
	private double tileSize;
	private double sceneWidth;
	private double sceneHeight;
	private HashMap <String, Image> possibleImages;

	public View(Model model, Stage primaryStage) {
		
		this.model = model;
		this.stage = primaryStage;
		this.panes = new Pane [(int) model.getWidth()][(int) model.getHeight()];
		
		this.gridpane = new GridPane();
		this.tileSize = model.getTileSize();
		this.possibleImages = model.getPossibleImages();
		
		Initialize(model.getWidth(), model.getHeight());
		
		this.sceneWidth = model.getWidth() * model.getTileSize() + model.getWidth() ;
		this.sceneHeight = model.getHeight() * model.getTileSize() + model.getHeight();
		scene = new Scene (this.gridpane, this.sceneWidth, this.sceneHeight ); // add more width and height for borders
		primaryStage.setScene(scene);
		
	}
	
	
	public void Initialize(double width , double height) {
		for (int y = 0; y < height ; y++) {
			for (int x = 0; x < width ; x++) {
				StackPane pane = setUpPane(this.tileSize, Color.WHITE, Color.BLACK);
				if (this.panes[x][y] != null) {
					delOldSetNewPane(x, y, pane);
				} else {
					this.panes[x][y] = pane;
					this.gridpane.add(pane, x, y);
				}
			}
		}
	}
	
	
	public void update( int x , int y) {
		
		Tile tile = model.getTile(x, y);
		String id = tile != null ? tile.getId() : null;
		StackPane pane = id == "zero" 
				? setUpPane(this.tileSize, Color.BLACK,Color.BLACK) 
				: id == null
				? setUpPane(this.tileSize, null ,Color.BLACK)
				: setUpPane(this.tileSize,null,Color.BLACK, id);
		delOldSetNewPane(x, y, pane);
		
	}
	
	
	public void delOldSetNewPane(int x, int y, StackPane pane) {
		
		this.gridpane.getChildren().remove(this.panes[x][y]);									// remove old pane
		this.panes[x][y] = pane;																// save new pane in array for later deleting
		gridpane.add(pane, x, y);	
		
	}
	
	
	public StackPane setUpPane(double tileSize, Color fill, Color border, String id) {
		
		StackPane pane = new StackPane();
		pane.setPrefSize(tileSize, tileSize);
		Rectangle rect = new Rectangle(tileSize,tileSize);
		rect.setFill(fill);
		rect.setStroke(border);
		
		ImageView image = new ImageView(this.possibleImages.get(id));
		image.setFitHeight(this.tileSize);
		image.setFitWidth(this.tileSize );
	
		pane.getChildren().add(image);
		pane.getChildren().add(rect);
		
		return pane;
	}
	
	
	public StackPane setUpPane(double tileSize, Color fill, Color border) {
		
		StackPane pane = new StackPane();
		pane.setPrefSize(tileSize, tileSize);
		Rectangle rect = new Rectangle(tileSize,tileSize);
		rect.setFill(fill);
		rect.setStroke(border);
		pane.getChildren().add(rect);
		
		return pane;
	}
	
	public GridPane getGridPane () {
		return this.gridpane;
	}
	
	public Pane [][] getPane() {
		return this.panes;
	}
	
	public Scene getScene() {
		return this.scene;
	}

}
