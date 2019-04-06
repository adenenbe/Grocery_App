/**
 * Filename: Main.java Project: p4 
 * Authors: Aron Denenberg, Ryan Ruenroeng, Nick Ferrentino, Jacob Bur 
 * Due Date: 12/16/18
 * 
 * Bugs or Other Notes: 
 * 
 * JavaFx file creating front end of the application
 * 
 */
package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *  Main class to build out user GUI and interface with back end of the application.
 */
public class Main extends Application {
	//create objects that we will need through out project
	FoodData foodMaster = new FoodData();
	GroceryData groceryMaster = new GroceryData();
	ObservableList<FoodItem> foodList = FXCollections.observableArrayList();
	ObservableList<FoodItem> groceryList = FXCollections.observableArrayList();
	VBox appliedFilters = new VBox(5);
	TextField searchBar = new TextField();
	//declare variables that will be used to keep track of nutrition totals
	int foodCount = 0;
	int menuCount = 0;
	double totalCals = 0;
	double totalFats = 0;
	double totalCarbs = 0;
	double totalFiber = 0;
	double totalProtein = 0;
	//initialize the labels to 0;
	Label totalCalsLabel = new Label(totalCals + "");
	Label totalFatsLabel = new Label(totalFats + "");
	Label totalCarbsLabel = new Label(totalCarbs + "");
	Label totalFiberLabel = new Label(totalFiber + "");
	Label totalProteinLabel = new Label(totalProtein + "");
	Label foodCountLabel = new Label("Count: " + foodCount);
	Label menuCountLabel = new Label("Count: " + menuCount);
	
	enum comparator {
		EQ,
		LT,
		GT,
	}

	@Override
	public void start(Stage primaryStage) {
		/**
		 * custom list cell to show food items in the food picker and menu
		 */
		class FoodListItem extends ListCell<FoodItem> {
			GridPane gPane = new GridPane();
			Label recommendedItemLabel = new Label();
			Label nameLabel = new Label();
			Label calsLabel = new Label();
			Label fatLabel = new Label();
			Label carbsLabel = new Label();
			Label fiberLabel = new Label();
			Label proteinLabel = new Label();
			Button button = new Button();
			String buttonText;
			public boolean isMeal;
			//constructor for new cells
			public FoodListItem(boolean isMeal) {
				super();
				//setting button text
				buttonText = (isMeal == true ? "Remove" : "Add");
				this.isMeal = isMeal;
				//spacing the columns 
				int numCols = 7;
				for (int col = 0; col < numCols; col++) {
					ColumnConstraints cc = new ColumnConstraints();
					cc.setPercentWidth(100 / (numCols * 1.0));
					cc.setMaxWidth(60);
					gPane.getColumnConstraints().add(cc);
				}
				//adding child objects to  the new cell
				gPane.add(nameLabel, 0, 0);
				gPane.add(calsLabel, 1, 0);
				gPane.add(fatLabel, 2, 0);
				gPane.add(carbsLabel, 3, 0);
				gPane.add(fiberLabel, 4, 0);
				gPane.add(proteinLabel, 5, 0);
				gPane.add(button, 6, 0);
			}
			/**
			 * Routine to update the food item in the list
			 * @param item food item for this card
			 * @param empty is the list item we are trying to display empty. if so, do nothing
			 */
			@Override
			protected void updateItem(FoodItem item, boolean empty) {
				super.updateItem(item, empty);
				setText(null);
				if (empty) { //if we don't need to show anything, show nothing.
					setGraphic(null);
				} else { //else set the object text to be the new food's nutrient info
					recommendedItemLabel.setWrapText(true);
					nameLabel.setWrapText(true);
					calsLabel.setWrapText(true);
					fatLabel.setWrapText(true);
					carbsLabel.setWrapText(true);
					fiberLabel.setWrapText(true);
					proteinLabel.setWrapText(true);
					recommendedItemLabel.setText(isItemReccomended(item) != null ? "Recommended based on " + isItemReccomended(item).toString() : ""); ;
					nameLabel.setText(item.getName() != null ? item.getName() : "<null>");
					calsLabel.setText(item.getNutrientValue("calories") + "");
					fatLabel.setText(item.getNutrientValue("fat") + "");
					carbsLabel.setText(item.getNutrientValue("carbohydrate") + "");
					fiberLabel.setText(item.getNutrientValue("fiber") + "");
					proteinLabel.setText(item.getNutrientValue("protein") + "");
					button.setText(this.buttonText);
					
					if (this.isMeal) { //handle case where remove was clicked from menuList
						button.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								subFromGroceryList(getItem());
							}
						});
					} else { //handle case where add was clicked from foodlist
						button.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								addToGroceryList(getItem());
							}
						});
					}
					setGraphic(gPane);
				}
			}
		}
	
		BorderPane bPane = new BorderPane();
		Scene scene = new Scene(bPane, 1600, 750);
		
		// stack pane
		StackPane stackPane = new StackPane();
		TextField foodSuggestion1 = new TextField("food 1");
		TextField foodSuggestion2 = new TextField("food 2");
		TextField foodSuggestion3 = new TextField("food 3");
		stackPane.getChildren().addAll(foodSuggestion1, foodSuggestion2, foodSuggestion3);
		

		// top pane
		GridPane gPaneTop = new GridPane();
		Label title = new Label("NomNom Meal Prep Program");
		title.setUnderline(true);
		title.setFont(new Font("Arial", 20));
		title.setMinWidth(350);
		Button loadFoodButton = new Button();
		loadFoodButton.setText("Load Food List");
		final FileChooser fileChooser = new FileChooser();
		loadFoodButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file == null) { //do nothing if the popup was dismissed
					return;
				}
				String fileName = file.getAbsolutePath();
				  try { Path path = FileSystems.getDefault().getPath(fileName); 
				  if (!Files.exists(path)) { 
					  Alert noFileAlert = new Alert(AlertType.ERROR,"File Doesn't exist."); 
					  noFileAlert.show(); 
					  return; 
					  }
				  Files.lines(file.toPath());
				 
				foodMaster.loadFoodItems(fileName);
				resetDisplay(foodMaster);
				
				 } catch(IOException e1) { Alert invalidFileAlert = new Alert(AlertType.ERROR,
				 "Invalid File"); invalidFileAlert.show(); }
			}

			// }
		});
		Button saveFoodButton = new Button();
		saveFoodButton.setText("Save Food List");
		saveFoodButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) { //do nothing if the popup was dismissed
          return;
        }
				String fileName = file.getAbsolutePath();
				fileName = fileName + ".txt";
				Path path = FileSystems.getDefault().getPath(fileName);
				try {
					Files.newBufferedWriter(path);
					if (!Files.isWritable(path)) {
						throw new Exception();
					}
					System.out.println(fileName);
					foodMaster.saveFoodItems(fileName);
				} catch (Exception e1) {
					Alert invalidFileAlert = new Alert(AlertType.ERROR, "Cannot write to file.");
					invalidFileAlert.show();
				}
			}
		});
		VBox fileButtons = new VBox();
		fileButtons.getChildren().addAll(loadFoodButton, saveFoodButton);
		fileButtons.setPadding(new Insets(0, 0, 0, 10));

		Button clearMenuButton = new Button();
		clearMenuButton.setText("Clear Grocery List");
		clearMenuButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				clearMenu();
			}
		});
		//aligning title bar and buttons
		HBox clearButtonBox = new HBox();
		clearButtonBox.getChildren().addAll(clearMenuButton);
		clearButtonBox.setPadding(new Insets(10, 10, 0, 0));
		clearButtonBox.setAlignment(Pos.TOP_RIGHT);

		gPaneTop.add(fileButtons, 0, 0);
		gPaneTop.add(title, 1, 0);
		gPaneTop.add(clearButtonBox, 2, 0);
		
		//aligning save/load buttons to the left
		ColumnConstraints lc = new ColumnConstraints();
		lc.setPercentWidth(100 / (3 * 1.0));
		lc.setHalignment(HPos.LEFT);
		gPaneTop.getColumnConstraints().add(lc);

		//centering title
		ColumnConstraints c = new ColumnConstraints();
		c.setPercentWidth(100 / (3 * 1.0));
		c.setHalignment(HPos.CENTER);
		gPaneTop.getColumnConstraints().add(c);

		//Aligning clear button to the right
		ColumnConstraints rc = new ColumnConstraints();
		rc.setPercentWidth(100 / (3 * 1.0));
		rc.setHalignment(HPos.RIGHT);
		gPaneTop.getColumnConstraints().add(rc);

		gPaneTop.setMinHeight(50);
		bPane.setTop(gPaneTop);

		// right pane
		//constructing foodlist view
		ListView<FoodItem> listViewRight = new ListView<>(groceryList);
		listViewRight.setCellFactory(param -> new FoodListItem(true));
		//label header for meal
		Label VBoxRightLabel = new Label("Grocery List");
		VBoxRightLabel.setFont(new Font("Arial", 18));
		HBox VBoxRightHeader = new HBox();
		VBoxRightHeader.setAlignment(Pos.CENTER);
		VBoxRightHeader.getChildren().add(VBoxRightLabel);
		//totals bar along the bottom
		GridPane menuTotals = new GridPane();
		for (int col = 0; col < 7; col++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100 / (7 * 1.0));
			menuTotals.getColumnConstraints().add(cc);
		}
		menuTotals.add(new Label("Totals"), 0, 0);
		menuTotals.add(totalCalsLabel, 1, 0);
		menuTotals.add(totalFatsLabel, 2, 0);
		menuTotals.add(totalCarbsLabel, 3, 0);
		menuTotals.add(totalFiberLabel, 4, 0);
		menuTotals.add(totalProteinLabel, 5, 0);
		menuTotals.add(menuCountLabel, 6, 0);
		VBox VBoxRight = new VBox(10, VBoxRightHeader);
		VBox.setMargin(VBoxRightHeader, new Insets(10, 10, 10, 10));
		//adding components to a VBox
		VBoxRight.getChildren().addAll(getHeader(), listViewRight, menuTotals);
		VBoxRight.setAlignment(Pos.TOP_CENTER);
		VBoxRight.setMinWidth(600);
		VBoxRight.setPadding(new Insets(0, 10, 0, 10));
		BorderPane.setAlignment(VBoxRight, Pos.CENTER_LEFT);

		// left food pane
		ListView<FoodItem> listViewLeft = new ListView<>(foodList);
		listViewLeft.setCellFactory(param -> new FoodListItem(false));
		
		// setup search bar
		// search bar was declared as a global since it needs to be visible to all objects in Main
		searchBar.setPromptText("Search Food");
		Label VBoxLeftLabel = new Label("Food List");
		VBoxLeftLabel.setFont(new Font("Arial", 18));
		HBox VBoxLeftHeader = new HBox(100, searchBar, VBoxLeftLabel);
		VBoxLeftHeader.setMaxHeight(1);
		VBoxLeftHeader.setAlignment(Pos.BOTTOM_LEFT);
		VBox VBoxLeft = new VBox(10, VBoxLeftHeader);
		VBoxLeft.getChildren().addAll(getHeader(), listViewLeft, foodCountLabel);
		VBoxLeft.setAlignment(Pos.BOTTOM_CENTER);
		VBoxLeft.setMinWidth(600);
		VBoxLeft.setPadding(new Insets(16, 0, 0, 10));

		// setup listener for food item search
		searchBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				searchBar.setOnKeyReleased(new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent e) {
						if (!appliedFilters.getChildren().isEmpty() && (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE)) {
							resetDisplay(foodMaster);
							try {
								applyFilters(appliedFilters);
							} catch (Exception e1) {
								Alert noFiltersAlert = new Alert(AlertType.INFORMATION, "No filters selected.");
								noFiltersAlert.show();
							}
							foodList.retainAll(foodMaster.filterByName(searchBar.getText().toUpperCase()));
						} else if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
							foodList.setAll(foodMaster.filterByName(searchBar.getText().toUpperCase()));
						} else {
							foodList.retainAll(foodMaster.filterByName(searchBar.getText().toUpperCase()));
						}
						updateFoodCount();
					}
				});
			}
		});
		//center of the activity is the two list views for food and menu, aligning those here
		GridPane centerPane = new GridPane();
		int numCols = 2;
		for (int col = 0; col < numCols; col++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100 / (numCols * 1.0));
			centerPane.getColumnConstraints().add(cc);
		}
		centerPane.add(VBoxLeft, 0, 0);
		centerPane.add(VBoxRight, 1, 0);
		bPane.setCenter(centerPane);

		/*
		 * bottom pane This will have 3 sections: 1) Add Item Box. 2) Filters Box. 3)
		 * Additional Resources
		 */

		HBox HBoxBottom = new HBox(200);

		// Begin Code on Item Details Box

		GridPane ItemDetailsBox = new GridPane();
		constructItemDetailsBox(ItemDetailsBox, foodMaster, foodList);
		HBoxBottom.getChildren().add(ItemDetailsBox);
		HBox.setMargin(ItemDetailsBox, new Insets(10, 10, 10, 10));

		// Filter Options
		Label filterLabel = new Label("Filter List Options");
		filterLabel.setFont(new Font("Arial", 18));
		HBox bottomCenter = new HBox(10); // Add to the bottom center of the screen
		HBox.setMargin(bottomCenter, new Insets(10, 10, 10, 10));
		bottomCenter.setAlignment(Pos.TOP_CENTER);

		// setup grid for filter area
		GridPane filterArea = new GridPane();

		// setup HBox for filter entries
		HBox filter = new HBox(10);

		// Drop down list for Macro field options to choose from
		ComboBox<FoodData.macros> macroSelect = new ComboBox<FoodData.macros>();
		for (FoodData.macros macro : FoodData.macros.values()) {
			macroSelect.getItems().add(macro);
		}
		macroSelect.setPromptText("Macro");
		macroSelect.setMinWidth(100);

		// Drop down list for Comparator field options to choose from
		ComboBox<String> comparatorSelect = new ComboBox<String>();
		comparatorSelect.getItems().addAll("=", ">=", "<=");
		comparatorSelect.setPromptText("Comparator");
		comparatorSelect.setMinWidth(100);

		// Text field for numeric value to compare to
		TextField value = new TextField();
		value.setPromptText("Value");

		// Nutrition Links Box
		VBox NutritionLinksBox = new VBox();
		Label NutritionLinksLabel = new Label("Nutrition Links");
		NutritionLinksLabel.setFont(new Font("Arial", 18));
		NutritionLinksBox.getChildren().add(NutritionLinksLabel);
		// all URLs stored in array
		String[] urls = { "https://tinyurl.com/yccjpbok", "https://www.nutrition.gov",
				"http://www.quickmeme.com/meme/361zsh", "https://nutrition.org",
				"https://giphy.com/gifs/hungry-chipmunk-om-nom-YmYemei6DDkrK" };
		// loop over URLs to create pop up when clicked
		for (int i = 0; i < urls.length; i++) {
			final String url = urls[i];
			final Hyperlink link = new Hyperlink(url);
			link.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					WebView webView = new WebView();
					WebEngine webEngine = webView.getEngine();
					webEngine.load(url);
					Stage webPopup = new Stage();
					webPopup.initModality(Modality.APPLICATION_MODAL);
					webPopup.initOwner(primaryStage);
					Scene popupScene = new Scene(webView, 1500, 700);
					webPopup.setScene(popupScene);
					webPopup.show();
				}
			});
			// add URLs to main stage
			NutritionLinksBox.getChildren().add(new HBox(link));
		}
		HBox.setMargin(NutritionLinksBox, new Insets(10, 10, 10, 10));
		filter.getChildren().addAll(macroSelect, comparatorSelect, value);

		// create filter buttons
		VBox buttons = new VBox(10);
		Button filterButton = new Button("Add Filter");
		filterButton.setMinWidth(100);
		Button clearButton = new Button("Clear Filters");
		clearButton.setMinWidth(100);
		Button applyButton = new Button("Apply Filters");
		applyButton.setMinWidth(100);
		buttons.getChildren().addAll(filterButton, clearButton, applyButton);

		// Add HBox and buttons to the filter area grid
		filterArea.add(filterLabel, 0, 0);
		filterArea.add(filter, 0, 1);
		GridPane.setHalignment(filterLabel, HPos.CENTER);
		filterArea.setHgap(10);
		filterArea.setVgap(10);
		bottomCenter.getChildren().addAll(filterArea, buttons);
		HBoxBottom.getChildren().addAll(bottomCenter, NutritionLinksBox);
		bPane.setBottom(HBoxBottom);

		// Setup VBox for list of currently applied filters. This was declared at the
		// class level since the variable needs to be visible to all parts of Main
		appliedFilters.setAlignment(Pos.CENTER);

		// create actions for when add filter button is pressed
		filterButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				addFilter(macroSelect, comparatorSelect, value, appliedFilters);
			}
		});

		// create action for when clear filters button is pressed
		clearButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				appliedFilters.getChildren().clear();
				if (!clearButton.getText().isEmpty()) {
					foodList.setAll(foodMaster.filterByName(searchBar.getText().toUpperCase()));
					updateFoodCount();
				} else {
					try {
						applyFilters(appliedFilters);
					} catch (Exception e1) {
					}
				}
			}
		});

		// create actions for when apply filters button is pressed
		applyButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				try {
					applyFilters(appliedFilters);
				} catch (Exception e1) {
					Alert noFiltersAlert = new Alert(AlertType.INFORMATION, "No filters selected.");
					noFiltersAlert.show();
				}
			}
		});
		filterArea.add(appliedFilters, 0, 2);

		// show stage
		primaryStage.setTitle("NomNomNom");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Sets up the buttons, fields, and handling for the add item box.
	 * 
	 * @param ItemDetailsBox - Box to allow a user to add an item to foodMaster and then display in app.
	 * @param foodMaster - Array List of food items used as the source of truth for the app. 
	 * @param foodList - Array List of items used to display in the application.
	 */
	private void constructItemDetailsBox(GridPane ItemDetailsBox, FoodData foodMaster,
			ObservableList<FoodItem> foodList) {
		ColumnConstraints cc1 = new ColumnConstraints();
		// Column 1 setup
		cc1.setMinWidth(50);
		cc1.setHalignment(HPos.LEFT);
		ItemDetailsBox.getColumnConstraints().add(cc1);
		

		// Column 2 setup
		ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setMinWidth(200);
		cc2.setFillWidth(true);
		cc2.setHalignment(HPos.CENTER);
		ItemDetailsBox.getColumnConstraints().add(cc2);

		// Overall Table Setup
		ItemDetailsBox.setVgap(10);
		GridPane.setMargin(ItemDetailsBox, new Insets(10, 10, 10, 10));

		// Instantiate Add button.
		Button AddItemButton = new Button("Add Item");
		ItemDetailsBox.add(AddItemButton, 0, 1);

		// page.add(Node, colIndex, rowIndex, colSpan, rowSpan):
		Label ItemDetailsBoxLabel = new Label("Add New Food Item ");
		ItemDetailsBoxLabel.setFont(new Font("Arial", 18));
		ItemDetailsBox.add(ItemDetailsBoxLabel, 0, 0, 2, 1);

		// Setup variables to construct the grid of fields and labels for user input.
		String LabelString;
		TextField LabelField;
		int row = 1;

		// UniqueID Field Build
		LabelString = "UniqueID";
		TextField UniqueIDField = new TextField();
		LabelField = UniqueIDField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Name Field Build
		LabelString = "Name";
		TextField NameField = new TextField();
		LabelField = NameField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Calories Field Build
		LabelString = "Calories";
		TextField CaloriesField = new TextField();
		LabelField = CaloriesField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Fats Field Build
		LabelString = "Fats";
		TextField FatsField = new TextField();
		LabelField = FatsField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Carbs Field Build
		LabelString = "Carbs";
		TextField CarbsField = new TextField();
		LabelField = CarbsField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Fiber Field Build
		LabelString = "Fiber";
		TextField FiberField = new TextField();
		LabelField = FiberField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Protein Field Build
		LabelString = "Protein";
		TextField ProteinField = new TextField();
		LabelField = ProteinField;
		row++;

		addItemDetailsRow(ItemDetailsBox, LabelString, LabelField, row);

		// Setup action handler for AddItemButton
		AddItemButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String uniqueIDValue;
				String nameValue;
				Double caloriesValue;
				Double fatsValue;
				Double carbsValue;
				Double fiberValue;
				Double proteinValue;
				try {
					uniqueIDValue = UniqueIDField.getText();
					if (uniqueIDValue.equals(""))
						throw new Exception();
				} catch (Exception err) {
					Alert BlankNameAlert = new Alert(AlertType.ERROR, "A unique ID must be specified.");
					BlankNameAlert.show();
					return;
				}
				try {
					nameValue = NameField.getText();
					if (nameValue.equals(""))
						throw new Exception();
				} catch (Exception err) {
					Alert BlankNameAlert = new Alert(AlertType.ERROR, "Name value cannot be blank.");
					BlankNameAlert.show();
					return;
				}
				try {
					caloriesValue = Double.parseDouble(CaloriesField.getText());
					if (caloriesValue < 0)
						throw new Exception();
				} catch (NumberFormatException err) {
					Alert nonNumericValueAlert = new Alert(AlertType.ERROR,
							"Calories value \"" + CaloriesField.getText() + "\" is non-numeric.");
					nonNumericValueAlert.show();
					return;
				} catch (Exception err) {
					Alert negativeValueAlert = new Alert(AlertType.ERROR, "Calories cannot be a negative value");
					negativeValueAlert.show();
					return;
				}
				try {
					fatsValue = Double.parseDouble(FatsField.getText());
					if (fatsValue < 0)
						throw new Exception();
				} catch (NumberFormatException err) {
					Alert nonNumericValueAlert = new Alert(AlertType.ERROR,
							"Fats value \"" + FatsField.getText() + "\" is non-numeric.");
					nonNumericValueAlert.show();
					return;
				} catch (Exception err) {
					Alert negativeValueAlert = new Alert(AlertType.ERROR, "Fats cannot be a negative value");
					negativeValueAlert.show();
					return;
				}
				try {
					carbsValue = Double.parseDouble(CarbsField.getText());
					if (carbsValue < 0)
						throw new Exception();
				} catch (NumberFormatException err) {
					Alert nonNumericValueAlert = new Alert(AlertType.ERROR,
							"Carbs value \"" + CarbsField.getText() + "\" is non-numeric.");
					nonNumericValueAlert.show();
					return;
				} catch (Exception err) {
					Alert negativeValueAlert = new Alert(AlertType.ERROR, "Carbohydrate cannot be a negative value");
					negativeValueAlert.show();
					return;
				}
				try {
					fiberValue = Double.parseDouble(FiberField.getText());
					if (fiberValue < 0)
						throw new Exception();
				} catch (NumberFormatException err) {
					Alert nonNumericValueAlert = new Alert(AlertType.ERROR,
							"Fiber value \"" + FiberField.getText() + "\" is non-numeric.");
					nonNumericValueAlert.show();
					return;
				} catch (Exception err) {
					Alert negativeValueAlert = new Alert(AlertType.ERROR, "Fiber cannot be a negative value");
					negativeValueAlert.show();
					return;
				}
				try {
					proteinValue = Double.parseDouble(ProteinField.getText());
					if (proteinValue < 0)
						throw new Exception();
				} catch (NumberFormatException err) {
					Alert nonNumericValueAlert = new Alert(AlertType.ERROR,
							"Protein value \"" + ProteinField.getText() + "\" is non-numeric.");
					nonNumericValueAlert.show();
					return;
				} catch (Exception err) {
					Alert negativeValueAlert = new Alert(AlertType.ERROR, "Protein cannot be a negative value");
					negativeValueAlert.show();
					return;
				}

				FoodItem newFood = new FoodItem(uniqueIDValue, nameValue);
				newFood.addNutrient("calories", caloriesValue);
				newFood.addNutrient("fat", fatsValue);
				newFood.addNutrient("carbohydrate", carbsValue);
				newFood.addNutrient("fiber", fiberValue);
				newFood.addNutrient("protein", proteinValue);
				foodMaster.addFoodItem(newFood);
				resetDisplay(foodMaster);
				try {
          applyFilters(appliedFilters);
        } catch (Exception e1) {
          e1.printStackTrace();
        }
			}
		});
	}
	/**
	 * @param ItemDetailsBox - Box to allow users to add new Food Items to the app.
	 * @param LabelString - String to indicate the type of data being stored in this row.
	 * @param LabelField - Field to store the data in this row.
	 * @param row - Iterates as new rows are added.
	 */
	private void addItemDetailsRow(GridPane ItemDetailsBox, String LabelString, TextField LabelField, int row) {
		ItemDetailsBox.add(new Label(LabelString), 0, row);
		ItemDetailsBox.add(LabelField, 1, row);
		LabelField.setPromptText("Enter " + LabelString);
	}

	/**
	 * constructs the header for the food item tables
	 * @return gPane containing the label names
	 */
	public Node getHeader() {
		GridPane gPane = new GridPane();
		int numCols = 7;
		for (int col = 0; col < numCols; col++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100 / (numCols * 1.0));
			cc.setMaxWidth(60);
			gPane.getColumnConstraints().add(cc);
		}
		gPane.add(new Label("Name"), 0, 0);
		gPane.add(new Label("Calories"), 1, 0);
		gPane.add(new Label("Fat"), 2, 0);
		gPane.add(new Label("Carbs"), 3, 0);
		gPane.add(new Label("Fiber"), 4, 0);
		gPane.add(new Label("Protein"), 5, 0);
		gPane.add(new Label(""), 6, 0);
		return gPane;
	}

	/**
	 * Validation checks and follow up actions for when add filter button is pressed
	 * 
	 * @param macro      is the value from the macro field
	 * @param comparator is the value from the comparator field
	 * @param value      is the value from the value field
	 * @param filters    is the VBox section that current filter will be added to if
	 *                   all check pass
	 */
	public void addFilter(ComboBox<FoodData.macros> macro, ComboBox<String> comparator, TextField value, VBox filters) {
		Double filterValue;
		String filter;

		// check if all fields were passed in. create warning popup if not
		if (macro.getValue() == null || macro.getValue().toString().isEmpty() || comparator.getValue() == null
				|| comparator.getValue().toString().isEmpty()) {
			Alert missingValueAlert = new Alert(AlertType.WARNING,
					"All Fields must be filled out before applying a filter");
			missingValueAlert.show();
			return;
		}

		// check if a valid number was put in the numeric field area. create error popup
		// if not
		try {
			filterValue = Double.parseDouble(value.getText());
		} catch (NumberFormatException e) {
			Alert nonNumericValueAlert = new Alert(AlertType.ERROR,
					"Filter value " + value.getText() + " is non-numeric.");
			nonNumericValueAlert.show();
			return;
		}

		if (filterValue < 0) {
			Alert negativeValueAlert = new Alert(AlertType.ERROR,
					"Filter value " + value.getText() + " is not a positive number.");
			negativeValueAlert.show();
			return;
		}
		filter = macro.getValue() + " " + comparator.getValue() + " " + filterValue;
		filters.getChildren().add(new Label(filter));
	}
	
	/**
	 * Collects all filters from the filter list and calls filter functions on the FoodData item
	 * @param filters to apply to the food data
	 * @throws Exception
	 */
	public void applyFilters(VBox filters) throws Exception {
		if (filters.getChildren().isEmpty()) {
			resetDisplay(foodMaster);
			return;
		}
		List<String> filterList = new ArrayList<String>();
		ObservableList<Node> filterNodes = filters.getChildren();
		for (Node filterNode : filterNodes) {
			Label filterLabel = (Label) filterNode;
			String filter = filterLabel.getText();
			filterList.add(filter);
		}
		List<FoodItem> filteredList = foodMaster.filterByNutrients(filterList);
		foodList.retainAll(filteredList);
		updateFoodCount();
	}

	/**
	 * function to reset foodList with new data
	 * @param d new data to show
	 */
	public void resetDisplay(FoodData d) {
		foodList.setAll(d.getAllFoodItems());
		updateFoodCount();
	}
	/**
	 * function to update count of food in foodList
	 */
	public void updateFoodCount() {
		foodCount = foodList.size();
		foodCountLabel.setText("Count: " + foodCount);
	}
	/**
	 * clears the menuList and resets counts
	 */
	public void clearMenu() {
		groceryList.clear();
		menuCount = 0;
		totalCals = 0;
		totalFats = 0;
		totalCarbs = 0;
		totalFiber = 0;
		totalProtein = 0;
		// displaying text
		totalCalsLabel.setText(totalCals + "");
		totalFatsLabel.setText(totalFats + "");
		totalCarbsLabel.setText(totalCarbs + "");
		totalFiberLabel.setText(totalFiber + "");
		totalProteinLabel.setText(totalProtein + "");
		menuCountLabel.setText("Count: " + menuCount);
	}
	/**
	 * adds a food item to the menu list and updates counts
	 * @param f food item to add
	 */
	public void addToGroceryList(FoodItem f) {
		groceryList.add(f);
		groceryMaster.addGroceryItem(f, foodMaster);
		menuCount++;

		// keeping running totals up to date
		totalCals += f.getNutrientValue("calories");
		totalFats += f.getNutrientValue("fat");
		totalCarbs += f.getNutrientValue("carbohydrate");
		totalFiber += f.getNutrientValue("fiber");
		totalProtein += f.getNutrientValue("protein");

		// rounding because we're handling doubles.
		totalCals = Math.round(totalCals * 10.0) / 10.0;
		totalFats = Math.round(totalFats * 10.0) / 10.0;
		totalCarbs = Math.round(totalCarbs * 10.0) / 10.0;
		totalFiber = Math.round(totalFiber * 10.0) / 10.0;
		totalProtein = Math.round(totalProtein * 10.0) / 10.0;

		// displaying text
		totalCalsLabel.setText(totalCals + "");
		totalFatsLabel.setText(totalFats + "");
		totalCarbsLabel.setText(totalCarbs + "");
		totalFiberLabel.setText(totalFiber + "");
		totalProteinLabel.setText(totalProtein + "");
		menuCountLabel.setText("Count: " + menuCount);
	}

	/**
	 * removes a given food item from the list
	 * @param f food item to remove
	 */
	public void subFromGroceryList(FoodItem f) {
		groceryList.remove(f);
		menuCount--;

		// keeping running totals up to date
		totalCals -= f.getNutrientValue("calories");
		totalFats -= f.getNutrientValue("fat");
		totalCarbs -= f.getNutrientValue("carbohydrate");
		totalFiber -= f.getNutrientValue("fiber");
		totalProtein -= f.getNutrientValue("protein");

		// rounding because we're handling doubles.
		totalCals = Math.round(totalCals * 10.0) / 10.0;
		totalFats = Math.round(totalFats * 10.0) / 10.0;
		totalCarbs = Math.round(totalCarbs * 10.0) / 10.0;
		totalFiber = Math.round(totalFiber * 10.0) / 10.0;
		totalProtein = Math.round(totalProtein * 10.0) / 10.0;

		// displaying text
		totalCalsLabel.setText(totalCals + "");
		totalFatsLabel.setText(totalFats + "");
		totalCarbsLabel.setText(totalCarbs + "");
		totalFiberLabel.setText(totalFiber + "");
		totalProteinLabel.setText(totalProtein + "");
		menuCountLabel.setText("Count: " + menuCount);
	}
	
	public FoodItem isItemReccomended(FoodItem food) {
		Map<FoodItem, Map<FoodItem, Integer>> foods = groceryMaster.getFoodsMap();
		Set<FoodItem> foodsSet = foods.keySet();
		for (FoodItem checkFood : foodsSet) {
			if (groceryMaster.linkExists(checkFood, food)) {
				return checkFood;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}