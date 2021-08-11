import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

public class Control {

	private Model model;
	private View view;
	private Pane[][] panes;
	
	public Control(Model model, View view) {
		this.model = model;
		this.view = view;
		this.panes = view.getPane();
		setCoordClick();
		setBackSpace();
		
	}
	
	public void setCoordClick() {
		view.getScene().setOnMousePressed(( e -> {
			if (e.isPrimaryButtonDown()) {
				int borderX = (int) (e.getSceneX() / model.getTileSize());
				int borderY = (int) (e.getSceneY() / model.getTileSize());
				int x = (int) ((e.getSceneX() -  borderX ) / model.getTileSize());					// subtract border width. Mouse needs to be further to hover over same field
				int y = (int) ((e.getSceneY() -  borderY ) / model.getTileSize());
				this.model.update(x, y);
				this.view.update(x, y);
				
			} else if(e.isSecondaryButtonDown()) {
				int borderX = (int) (e.getSceneX() / model.getTileSize());
				int borderY = (int) (e.getSceneY() / model.getTileSize());
				int x = (int) ((e.getSceneX() -  borderX ) / model.getTileSize());
				int y = (int) ((e.getSceneY() -  borderY ) / model.getTileSize());
				this.model.deleteTile(x, y);
				this.view.update(x, y);
			}
		}));
	}
	

	public void setBackSpace() {
		
		view.getScene().setOnKeyPressed(( e -> {
			if(e.getCode() == KeyCode.BACK_SPACE) {
				model.clearField();
				view.Initialize(model.getWidth(), model.getHeight());
			}
		}));
		
	}
	
	
	public void setPaneClick() {								// this sucks. Only available once
		for (int y = 0; y < model.getHeight() ; y++) {
			final int Y = y;
			for (int x = 0; x < model.getWidth() ; x++) {
				final int X = x;
				this.panes[x][y].setOnMouseClicked( e -> {
					if (e.getButton() == MouseButton.PRIMARY) {
						
						this.model.update(X, Y);
						this.view.update(X, Y);
					}
				});
				
			}
		}
	}
	
}
