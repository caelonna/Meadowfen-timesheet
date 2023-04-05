package meadowfen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.stage.*;

/*
 * Need to load this up to github
 * Need to change the orientation to make it a 2x2 grid of areas instead of a line
 * Need to add a notes area to each section
 */
public class HomePage extends Application {
	private static String user;
	private static BorderPane border;
	private static MysqlConnection sqlcon;
	ArrayList<String> all_users;
	
	public HBox createHeader()
	{
		HBox header = new HBox();
		header.setPadding(new Insets(15, 5, 15, 5));
		header.setStyle("-fx-background-color: #33ffff");
		
		Text title = new Text("Welcome to Meadowfen Timesheet system");
		title.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 20));
		header.getChildren().add(title);
		
		Text change_user = new Text("Select user");
		change_user.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 10));
		header.getChildren().add(change_user);
		
		List<String> choices = new ArrayList<>();
		for(int i = 0; i < all_users.size(); i ++)
			choices.add(all_users.get(i));
		choices.add("Create new user");
		
		ChoiceDialog<String> dialog = new ChoiceDialog<>(all_users.get(0), choices);
		dialog.setTitle("User Selection");
		dialog.setHeaderText("Show timesheet information for a specific user");
		dialog.setContentText("Select user:");

		
		if(user != null && user.trim() != "")
		{
			title.setText("Welcome to Meadowfen Timesheet system for user: " + user);
			change_user.setText("change user");
		}
		
		change_user.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent arg0) {
					// Traditional way to get the response value.
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()){
					    user = result.get();
					   
						if(user != null && user.trim() != "" && !user.equals("Create new user"))
						{
							title.setText("Welcome to Meadowfen Timesheet system for user: " + user);
							change_user.setText("change user");
						
							TimeSheetManagement tsm = new TimeSheetManagement(user, border, sqlcon);
							
							
							border.setLeft(tsm.listTimeSheets());
							
							border.setCenter(tsm.createSummaryGrid());
						}
						else if(user != null && user.equals("Create new user"))
						{
							GridPane grid = createNewUser();
							border.setMargin(grid, new Insets(5,5,5,5));
							border.setLeft(null);
							border.setCenter(grid);
						}
					}
				}
			});
		return header;
	}
	
	//TODO add functionality to create timesheets during user creation
	public GridPane createNewUser()
	{
		GridPane grid = new GridPane();
		
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setStyle("-fx-background-color: #33ffff");
		grid.setAlignment(Pos.BASELINE_LEFT);
		
		Label name = new Label("User name:");
		grid.add(name, 0, 0);
		
		TextField userName = new TextField();
		grid.add(userName, 0, 1);
		
		Button submit = new Button("create");
		grid.add(submit, 1, 1);
		
		Text errorText = new Text();
		grid.add(errorText, 0, 2);
		
		submit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	boolean error = false;
            	String errorMessage = "";
            	if(userName.getText() == null || userName.getText().trim() == "")
            	{
            		error = true;
            		errorMessage = "Username cannot be blank. ";
            	}
            	else if(userName.getText().toLowerCase().equals("create new user"))
            	{
            		error = true;
            		errorMessage = "Choose a real name. ";
            	}
            	else if(userName.getText().length() > 50)
            	{
            		error = true;
            		errorMessage = "name is too long, 50 character max ";
            	}
            	else
            	{
            		String testUser = userName.getText();
            		for(int i = 0; i < all_users.size() && !error; i++)
            		{
            			if(all_users.get(i).toLowerCase().equals(testUser.toLowerCase())) {
	            			error = true;
	                		errorMessage = "That name already exists. ";
            			}
            		}
            		
            		if(!error) {
	                	user = testUser;
	                	
	                	all_users.add(user);
	                	
	                	TimeSheetManagement tsm = new TimeSheetManagement(user, border, sqlcon);
						
	                	border.setTop(createHeader());
	                	border.setLeft(tsm.listTimeSheets());
						border.setCenter(tsm.createSummaryGrid());
            		}
            	}
            	
            	
                if(error)
                	errorText.setText(errorMessage);
            }
        });
		
		return grid;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("MeadowFen TimeSheets");
		
		
		/*
		 * TODO: add stylesheet for easy style editing
		 * TODO: add help file to explain how this all works
		 */
		border = new BorderPane();
		border.setPadding(new Insets(5, 5, 5, 5));
		border.setStyle("-fx-background-color: #0c9444");
		
		sqlcon = new MysqlConnection();
		all_users = sqlcon.getUsers();

		
		
		HBox header = createHeader();
		border.setMargin(header, new Insets(5,5,5,5));
		border.setTop(header);
		

		Scene scene = new Scene(border, 1000, 600);
		primaryStage.setScene(scene);
		
		// run form
		primaryStage.show();
	}

	public static void main(String[] args) {
		
		launch(args);
	}

}
