package meadowfen;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TimeSheetManagement {
	static ArrayList<TimeSheet> mySheets;
	static String user;
	static BorderPane border;
	static MysqlConnection con;
	static int selectedSheet;//keep track of and highlight selected sheet
	
	public TimeSheetManagement(String selected_user, BorderPane b, MysqlConnection sql_connection)
	{
		user = selected_user;
		border = b;
		con = sql_connection;
		selectedSheet = 0;
		
		mySheets = new ArrayList<TimeSheet>();
		
		ArrayList<String> areas = con.getAreas(user);
		for(int i = 0; i < areas.size(); i ++)
		{
			mySheets.add(new TimeSheet(areas.get(i),user, con, i+1));
		}
	}
	
	/*TODO work on this
	 * ?? why is there extra space after the notes field
	 * */
	private static TableView<Record> showAllData(ArrayList<Record> data)
	{
		TableView<Record> table = new TableView<Record>();
		
		TableColumn<Record, String> Area = new TableColumn<Record, String>("area");
        Area.setMinWidth(50);
        Area.setCellValueFactory(
                new PropertyValueFactory<Record, String>("area"));
        
        TableColumn<Record, Date> Day = new TableColumn<Record, Date>("day");
        Day.setMinWidth(50);
        Day.setCellValueFactory(
                new PropertyValueFactory<Record, Date>("day"));
        
        TableColumn<Record, Time> Start = new TableColumn<Record, Time>("start");
        Start.setMinWidth(20);
        Start.setCellValueFactory(
                new PropertyValueFactory<Record, Time>("start"));
        
        TableColumn<Record, Time> End = new TableColumn<Record, Time>("end");
        End.setMinWidth(20);
        End.setCellValueFactory(
                new PropertyValueFactory<Record, Time>("end"));
        
        TableColumn<Record, Time> Elapsed = new TableColumn<Record, Time>("elapsed");
        Elapsed.setMinWidth(20);
        Elapsed.setCellValueFactory(
                new PropertyValueFactory<Record, Time>("elapsed"));
		
		TableColumn<Record, String> Notes = new TableColumn<Record, String>("notes");
        Notes.setMinWidth(200);
        Notes.setCellValueFactory(
                new PropertyValueFactory<Record, String>("notes"));
        
        table.getColumns().addAll(Area,Day,Start,End,Elapsed,Notes);
        
        ObservableList<Record> records = FXCollections.observableList(data);
        
        table.setItems(records);
		
		return table;
	}
	
	//TODO add cancel button
	private static GridPane createManualEntryGrid(TimeSheet timesheet)
	{
		String name = timesheet.getTimeSheetName();
		
		GridPane grid = new GridPane();
		
		Text title = new Text("This is the manuel entry form for " + name + " timesheet");
		grid.add(title, 0, 0, 6, 1);
		
		Label date = new Label("Date");
		grid.add(date, 0, 1);
		
		DatePicker checkInDatePicker = new DatePicker();
		grid.add(checkInDatePicker, 1, 1, 6, 1);
		
		Label start = new Label("Start time:");
		grid.add(start, 0, 2);
		ComboBox<String> start_hour = new ComboBox<String>();
		for(int i = 1; i <= 12; i++)
		{
			String time = "" + i;
			start_hour.getItems().add(time);
		}
		grid.add(start_hour, 1, 2);
		Text colon = new Text(":");
		grid.add(colon, 2, 2);
		ComboBox<String> start_minute = new ComboBox<String>();
		for(int i = 0; i < 60; i+=5)
		{
			String time = "" + i;
			if(i < 10)
				time = "0" + i;
			start_minute.getItems().add(time);
		}
		grid.add(start_minute, 3, 2);
		ComboBox<String> start_ap = new ComboBox<String>();
		start_ap.getItems().add("AM");
		start_ap.getItems().add("PM");
		grid.add(start_ap, 4, 2);
		
		Label end = new Label("End time:");
		grid.add(end, 0, 3);
		ComboBox<String> end_hour = new ComboBox<String>();
		for(int i = 1; i <= 12; i++)
		{
			String time = "" + i;
			end_hour.getItems().add(time);
		}
		grid.add(end_hour, 1, 3);
		Text end_colon = new Text(":");
		grid.add(end_colon, 2, 3);
		ComboBox<String> end_minute = new ComboBox<String>();
		for(int i = 0; i < 60; i+=5)
		{
			String time = "" + i;
			if(i < 10)
				time = "0" + i;
				
			end_minute.getItems().add(time);
		}
		grid.add(end_minute, 3, 3);
		ComboBox<String> end_ap = new ComboBox<String>();
		end_ap.getItems().add("AM");
		end_ap.getItems().add("PM");
		grid.add(end_ap, 4, 3);
			
		
		Label notes = new Label("Notes:");
		grid.add(notes,0,4);
		
		TextArea notesText = new TextArea(timesheet.getNotes());
		grid.add(notesText, 1, 4,6,2);
		
		Button submit = new Button("Submit");
		grid.add(submit, 4, 6);
		
		Button cancel = new Button("Cancel");
		grid.add(cancel, 5, 6);
		
		Text errorText = new Text();
		errorText.setFill(Color.RED);
		grid.add(errorText, 0, 7, 10, 1);
		
		submit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	
            	boolean error = false;
            	String error_message = "";
            	
            	String ds = "";
            	if(checkInDatePicker.getValue() == null)
            	{
            		error = true;
            		error_message = "No date chosen. ";
            	}
            	else
            		ds = checkInDatePicker.getValue().toString();
            	
            	
            	int s_hour = 0; int s_minute = 0; 
            	if(start_hour.getValue() == null || start_hour.getValue() == "")
            	{
            		error = true;
            		error_message += "Invalid start hour. ";
            	}
            	else
            		 s_hour = Integer.valueOf(start_hour.getValue());
            	if(start_minute.getValue() == null || start_minute.getValue() == "")
            	{
            		error = true;
            		error_message += "Invalid start minute. ";
            	}
            	else
            		s_minute = Integer.valueOf(start_minute.getValue());
            	
            	if(start_ap.getValue() == null || start_ap.getValue() == "")
            	{
            		error = true;
            		error_message += "Choose start AM/PM. ";
            	}
            	else if(start_ap.getValue().equals("PM") && s_hour != 12)
            		s_hour += 12;
            	else if(s_hour == 12 && start_ap.getValue().equals("AM"))// the midnight problem
            		s_hour = 0;
            	
            	int e_hour = 0; int e_minute = 0;
            	if(end_hour.getValue() == null || end_hour.getValue() == "")
            	{
            		error = true;
            		error_message += "Invalid end hour. ";
            	}
            	else
            		e_hour = Integer.valueOf(end_hour.getValue());
            	if(end_minute.getValue() == null || end_minute.getValue() == "")
            	{
            		error = true;
            		error_message += "Invalid end minute. ";
            	}
            	else
            		e_minute = Integer.valueOf(end_minute.getValue());
            	if(end_ap.getValue() == null || end_ap.getValue() == "")
            	{
            		error = true;
            		error_message += "Choose end AM/PM. ";
            	}
            	else if(end_ap.getValue().equals("PM") && e_hour != 12)
            		e_hour += 12;
            	else if(e_hour == 12 && end_ap.getValue().equals("AM"))// the midnight problem
            		e_hour = 0;	
            	
            	if(notes.getText() != null && notes.getText().length() > 400)
            	{
            		error = true;
            		error_message += "notes are too long, 400 charactrs max.";
            	}
            	
            	if(!error)
            	{
            		String ss = ds + " " + s_hour + ":" + s_minute + ":00.000";
                	Timestamp start = Timestamp.valueOf(ss);

                	String es = ds + " " + e_hour + ":" + e_minute + ":00.000";
                	Timestamp end = Timestamp.valueOf(es);
                	
                	String n = notesText.getText();
                	n = n.replaceAll("'", "\\\\\'");
                	
                	try {

                    	timesheet.setEntry(start, end, n);
                	} catch (Exception er) {
                		error = true;
                		error_message = "submit failed ";
                		System.out.println(er.getMessage());
                	}
            	}
            	
            	if(!error)
            		border.setCenter(createDataGrid(timesheet));
            	else
            		errorText.setText(error_message);
            }
        });
		
		cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	
            	border.setCenter(createDataGrid(timesheet));
            	
            }
        });
		
		return grid;
	}

	
	//helper method for working with dates
	// get day of week
	private static String getDayOfWeek(Calendar cal)
	{
		String day = "";
		int week_day = cal.get(Calendar.DAY_OF_WEEK);
		switch(week_day) {
			case Calendar.SUNDAY: day = "Sunday"; break;
			case Calendar.MONDAY: day = "Monday"; break;
			case Calendar.TUESDAY: day = "Tuesday"; break;
			case Calendar.WEDNESDAY: day = "Wednesday"; break;
			case Calendar.THURSDAY: day = "Thursday"; break;
			case Calendar.FRIDAY: day = "Friday"; break;
			case Calendar.SATURDAY: day = "Saturday";
		}
		return day;
	}
	//get month 
	private static String getMonth(Calendar cal)
	{
		String month = "";
		int m = cal.get(Calendar.MONTH);
		switch(m) {
			case Calendar.JANUARY: month = "January"; break;
			case Calendar.FEBRUARY: month = "February"; break;
			case Calendar.MARCH: month = "March"; break;
			case Calendar.APRIL: month = "April"; break;
			case Calendar.MAY: month = "May"; break;
			case Calendar.JUNE: month = "June"; break;
			case Calendar.JULY: month = "July"; break;
			case Calendar.AUGUST: month = "August"; break;
			case Calendar.SEPTEMBER: month = "September"; break;
			case Calendar.OCTOBER: month = "October"; break;
			case Calendar.NOVEMBER: month = "November"; break;
			case Calendar.DECEMBER: month = "December";
		}
		
		return month;
	}
	
	

	public static GridPane createSummaryGrid()
	{
		GridPane grid = new GridPane();
		grid.setStyle("-fx-background-color: #33ffff");
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.setVgap(5);
		border.setMargin(grid, new Insets(5,5,5,5));
		
		Text title = new Text("Summary of all Timesheets");
		title.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		grid.add(title, 0, 0 );
		
		Calendar cal = Calendar.getInstance();
		Text today = new Text("Today is " + getDayOfWeek(cal) + ", " + getMonth(cal) + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR));
		grid.add(today, 0, 1);
		
		//TODO check to see if this still works on a Sunday
		LocalDate start = LocalDate.now();
		int weekDay = start.getDayOfWeek().getValue()%7;
		start = start.minusDays((long)weekDay);
		LocalDate currentDay = LocalDate.now();
				
		int[] todays_hours = {0,0};
		ArrayList<Record> hoursbreakout = new ArrayList<Record>();
		int[] weeks_hours = {0,0};
		ArrayList<Record> weekbreakout = new ArrayList<Record>();
		
		//int[] dayOfWeek_hours = new int[7];
		for(int i = 0; i < mySheets.size(); i++)
		{
			TimeSheet ts = mySheets.get(i);
			int[] sheet_time = ts.getTodaysTime();
			todays_hours[0] += sheet_time[0];
			todays_hours[1] += sheet_time[1];

			String tString = "";
			if(sheet_time[0] > 0)
				tString += "" + sheet_time[0] + " hrs ";
			if(sheet_time[1] > 0)
				tString += "" + sheet_time[1] + " min ";
			
			if(sheet_time[0] > 0 || sheet_time[1] > 0)
				hoursbreakout.addAll(ts.getData(currentDay, null));;
			
			while(todays_hours[1] >= 60)
			{
				todays_hours[0] ++;
				todays_hours[1] -= 60;
			}
				
			// get hours for each day of the week
			int[] week_time = ts.getThisWeeksTime();
			weeks_hours[0] += week_time[0];
			weeks_hours[1] += week_time[1];
			
			if(week_time[0] > 0 || week_time[1] > 0)
				weekbreakout.addAll(ts.getData(start, currentDay));
			
			while(weeks_hours[1] >= 60)
			{
				weeks_hours[0] ++;
				weeks_hours[1] -= 60;
			}
		}
		
		Text todaysHours = new Text("Total time for today: " + todays_hours[0] + " hours and " + todays_hours[1] + " minutes");
		grid.add(todaysHours, 0, 2);
		
		TableView<Record> recent = showAllData(hoursbreakout);
		
		grid.add(recent, 0, 3, 5, 7);
		
		Text weekHours = new Text("Total hours for this week: " + weeks_hours[0] + " hours and " + weeks_hours[1] + " minutes");
		grid.add(weekHours, 0, 10);
		
		
		TableView<Record> weeklyInfo = showAllData(weekbreakout);
		grid.add(weeklyInfo, 0, 11,5,7);

		
		return grid;
	}
	
	//TODO add edit mode and search mode
	private static GridPane createDataGrid(TimeSheet timesheet)
	{
		String name = timesheet.getTimeSheetName();
		
		int row = 0;
		
		GridPane grid = new GridPane();
		grid.setStyle("-fx-background-color: #33ffff");
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.setVgap(5);
		border.setMargin(grid, new Insets(5,5,5,5));
		
		
		Text title = new Text(name + " timesheet info");
		title.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		grid.add(title, 0, row, 3, 1); row++;
		row++;
		
		int[] today_time = timesheet.getTodaysTime();
		String today_minutes = "" + today_time[1];
		if(today_time[1] < 10)
			today_minutes = "0" + today_minutes;
		int[] total_time = timesheet.getTotalTime();
		String total_minutes = "" + total_time[1];
		if(total_time[1] < 10)
			total_minutes = "0" + total_minutes;
		
		Text today = new Text("Time clocked today: " + today_time[0] + ":" + today_minutes);
		grid.add(today, 0, row, 3, 1); row++;
		Text total = new Text("Total time clocked: " + total_time[0] + ":" + total_minutes);
		grid.add(total, 0, row, 3, 1); row++;
		
		Text errorMessage = new Text();
		errorMessage.setFill(Color.RED);
		grid.add(errorMessage, 0, row, 5, 1); row++;
		
		
		Text showing = new Text("Showing time:");
		Button showAll = new Button("All");
    	showAll.setStyle("-fx-background-color: #00ff00");
		DatePicker begin = new DatePicker();
		Text to = new Text(" to ");
		DatePicker end = new DatePicker();
		Button showLimited = new Button("Limit Results");
		grid.add(showing, 0, row);
		grid.add(showAll, 1, row);
		grid.add(begin, 2, row);
		grid.add(to, 3, row);
		grid.add(end, 4, row);
		grid.add(showLimited, 5, row); row++;

		
		
		
		//Text headers = new Text("date\t\t\t| time\t| notes");
		//grid.add(headers, 0, row, 5, 1); row++;
		//TextArea recent = new TextArea();
		//recent.setEditable(false);
		//recent.setMaxSize(500, 100);
		
		//String all_data = "";//timesheet.getDataWithoutHeader(null, null);// double nulls is all data
		//recent.setText(all_data);
		TableView<Record> allData = showAllData(timesheet.getData(null, null));
		
		grid.add(allData, 0, row, 5, 5); row+=5;
		
		showAll.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	showAll.setStyle("-fx-background-color: #00ff00");
            	showLimited.setStyle("-fx-background-color: #ffff00");
            	
            	//String all_data = "";//timesheet.getDatawithHeader(null, null);// double nulls is all data
            	allData.setItems(FXCollections.observableList(timesheet.getData(null, null)));
        		int[] set_time = timesheet.getTotalTime();
        		total.setText("Total time clocked: " + set_time[0] + ":" + set_time[1]);
            }
		});
		showLimited.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	boolean error = false;
            	
            	if(begin.getValue() == null || end.getValue() == null)
            	{
            		error = true;
            		errorMessage.setText("Show limited requires 2 valid dates.");
            	}
            	else if(end.getValue().isBefore(begin.getValue()))
            	{
            		error = true;
            		errorMessage.setText("End date must be before start date.");
            	}
            
            	if(!error) {
	            	showLimited.setStyle("-fx-background-color: #00ff00");
	            	showAll.setStyle("-fx-background-color: #ffff00");
	            	
	            	allData.setItems(FXCollections.observableList(timesheet.getData(begin.getValue(), end.getValue())));// double nulls is all data
	        		int[] set_time = timesheet.getTotalTime(begin.getValue(), end.getValue());
	        		total.setText("Total time clocked: " + set_time[0] + ":" + set_time[1]);
            	}
        		
            }
		});
		
		Label addHours = new Label("Add Hours");
		grid.add(addHours, 0, row);
		Button clockBtn = new Button("Time Clock");
		grid.add(clockBtn, 1, row);
		Button manualBtn = new Button("Manual Entry");
		grid.add(manualBtn, 2, row); row++;
		
		clockBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	Rectangle cover = new Rectangle();
            	cover.setHeight(250);
            	cover.setWidth(600);
            	cover.setFill(Color.web("#33ffff"));
            	grid.add(cover, 0, 13, 6, 6);
            	grid.add(createTimeClockGrid(timesheet), 0, 13, 6, 6);
            	
            }
        });
		
		manualBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	Rectangle cover = new Rectangle();
            	cover.setHeight(250);
            	cover.setWidth(600);
            	cover.setFill(Color.web("#33ffff"));
            	grid.add(cover, 0, 13, 6, 6);
            	grid.add(createManualEntryGrid(timesheet), 0, 13, 6, 6);           
            }
        });
		
		return grid;
	}
	
	//TODO add a cancel button
	private static GridPane createTimeClockGrid(TimeSheet timesheet)
	{
		String name = timesheet.getTimeSheetName();
		
		//Grid arrangement is column first, row second
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setAlignment(Pos.BASELINE_LEFT);
		border.setMargin(grid, new Insets(5,5,5,5));
		
		Text homeTitle = new Text("Time tracking for " + name + " tasks");
		grid.add(homeTitle, 0, 0, 3, 1);

		Label home = new Label(name + ":");
		grid.add(home, 0, 1);

		Button startBtn = new Button("Start");
		if(!timesheet.getActive())
		{
			startBtn.setStyle("-fx-background-color: #ffffff");
		} else
		{
			startBtn.setDisable(true);
            startBtn.setStyle("-fx-background-color: #00ff00");
		}
		grid.add(startBtn, 1, 1);

		Button endBtn = new Button("Finish");
		if(!timesheet.getActive())
		{
			endBtn.setDisable(true);
		}
		grid.add(endBtn, 2, 1);
		
		Text startTime = new Text();
		grid.add(startTime, 0, 2, 4, 1);
		
		Text notes = new Text("Notes:");
		grid.add(notes,0,3);
		
		TextArea notesText = new TextArea(timesheet.getNotes());
		notesText.wrapTextProperty();
		grid.add(notesText, 1, 3,8,2);
		
		Button saveNotes = new Button("save notes");
		grid.add(saveNotes, 7, 5);
		
		Button cancel = new Button("cancel");
		grid.add(cancel, 8, 5);
		
		startBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	Timestamp ts = new Timestamp(System.currentTimeMillis());
            	timesheet.setStart(ts);
            	String time = ts.toString().substring(10,16);
            	
                startTime.setText("Work at " + name + " started at " + time);
                startBtn.setDisable(true);
                startBtn.setStyle("-fx-background-color: #00ff00");
                endBtn.setDisable(false);
                
            }
        });
		
		Text errorMessage = new Text();
		errorMessage.setFill(Color.RED);
		grid.add(errorMessage, 0, 5, 6, 1);
		
		endBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	Timestamp ts = new Timestamp(System.currentTimeMillis());
            	
            	if(notesText.getText() != null && notesText.getText().length() >= 400)
            		errorMessage.setText("notes text is too long, max 400 characters.");
            	else {
            	
	            	try {
	            		timesheet.setEnd(ts, notesText.getText().replaceAll("'", "\\\\\'"));
	                	
	                	startBtn.setDisable(false);
	                    startBtn.setStyle("-fx-background-color: #ffffff");
	                    endBtn.setDisable(true);
	                    notesText.setText("");
	                    border.setCenter(createDataGrid(timesheet));
	            	} catch (Exception er)
	            	{
	            		errorMessage.setText("Submit failed, probably due to illegal characters in the notes field");
	            	}
            	}
            }
        });
		
		saveNotes.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	timesheet.setNotes(notesText.getText());
            }
        });
		
		cancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	try {
					timesheet.setEnd(null, "");
				} catch (Exception e1) {
				}
                border.setCenter(createDataGrid(timesheet));
            	
            }
        });
		
		return grid;
	}
	
	@SuppressWarnings("static-access")
	private static GridPane createNewGrid()
	{
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(25, 25, 25, 25));
		grid.setStyle("-fx-background-color: #33ffff");
		grid.setAlignment(Pos.BASELINE_LEFT);
		border.setMargin(grid, new Insets(5,5,5,5));
		
		Label name = new Label("Timesheet name:");
		grid.add(name, 0, 0);
		
		TextField tsName = new TextField();
		grid.add(tsName, 0, 1);
		
		Button submit = new Button("create");
		grid.add(submit, 1, 1);
		
		Text errorText = new Text();
		grid.add(errorText, 0, 2);
		
		submit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            	boolean error = false;
            	String errorMessage = "";
            	if(tsName.getText() == null || tsName.getText().trim() == "")
            	{
            		error = true;
            		errorMessage = "Timesheet name cannot be blank. ";
            	}
            	else if(tsName.getText().length() >= 50) {
            		error = true;
            		errorMessage = "Timesheet name is too long, 50 characters max. ";
            	}
            	else {
            		
            		TimeSheet newSheet = new TimeSheet(tsName.getText().trim(),user, con, mySheets.size()+1);
            		mySheets.add(newSheet);
            		selectedSheet = newSheet.getId();

            		border.setLeft(listTimeSheets());
            		border.setCenter(createDataGrid(newSheet));
            	}


            	if(error)
            		errorText.setText(errorMessage);
                
            }
        });
		
		
		
		return grid;
	}
	
	@SuppressWarnings("static-access")
	public static VBox listTimeSheets()
	{
		VBox list = new VBox();
		list.setPadding(new Insets(25, 25, 25, 25));
		list.setSpacing(25);
		list.setStyle("-fx-background-color: #33ffff");
		border.setMargin(list, new Insets(5,5,5,5));
		
		Text showAll = new Text("Timesheet Summary");
		if(selectedSheet == 0)
		{	
			showAll.setUnderline(true);
			showAll.setFill(Color.BLUE);
		}
		showAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent arg0) {
					selectedSheet = 0;
					border.setCenter(createSummaryGrid());
					border.setLeft(listTimeSheets());
				}
			});
			list.getChildren().add(showAll);
		
		for(int i = 0; i < mySheets.size(); i++)
		{
			TimeSheet ts = mySheets.get(i);
			Text title = new Text(ts.getTimeSheetName() + " timesheet");
			if(selectedSheet == ts.getId())
			{
				title.setUnderline(true);
				title.setFill(Color.BLUE);
			}
			title.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent arg0) {
					selectedSheet = ts.getId();
					border.setCenter(createDataGrid(ts));
					border.setLeft(listTimeSheets());
				}
			});
			list.getChildren().add(title);
		}
		
		Text addMore = new Text("new timesheet");
		if(selectedSheet == mySheets.size() +1)
		{
			addMore.setUnderline(true);
			addMore.setFill(Color.BLUE);
		}
		addMore.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent arg0) {
					selectedSheet = mySheets.size() +1;
					border.setCenter(createNewGrid());
					border.setLeft(listTimeSheets());
				}
			});
			list.getChildren().add(addMore);
		
		return list;
	}
	
	

}
