package meadowfen;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/*
 * This class manages the information for the individual timesheets
 */
public class TimeSheet {
	String area;
	String user;
	Timestamp start;
	Timestamp end;
	boolean active;
	String notes = "";
	MysqlConnection con;
	int id;
	
	public TimeSheet(String n, String u, MysqlConnection c, int new_id)
	{
		area = n;
		user = u;
		active = false;
		con = c;
		id = new_id;
	}
	public int getId()
	{
		return id;
	}
	public String getTimeSheetName() {
		return area;
	}
	public String getUser() {
		return user;
	}
	public boolean getActive() {
		return active;
	}
	public int[] getTotalTime() {
		int[] time = {0,0};
		
		String query = "select timediff(end,start) as 'total time' from timesheet" +
				" Where user = '" + user + "' and area = '" + area + "'";
		
	    time = con.runTimeQuery(query, "total time");
		
		return time;
	}
	public int[] getTotalTime(LocalDate start, LocalDate end) {
		int[] time = {0,0};
		
		String query = "select timediff(end,start) as 'total time' from timesheet" +
				" Where user = '" + user + "' and area = '" + area + "' and day between '"
				+ start.toString() + "' and '" + end.toString() + "'";
		
	    time = con.runTimeQuery(query, "total time");
		
		return time;
	}
	public ArrayList<Record> getData(LocalDate start, LocalDate end)
	{
		//String allData = "";
		
		ArrayList<Record> data = con.getData(area, user, start, end);
		
		//for(int i = 0; i < data.size(); i ++)
			//allData += data.get(i) + "\n";
		
		return data;
	}
	public int[] getTodaysTime() {
		int[] time = {0,0};
		
		String query = "select timediff(end,start) as 'total time' from timesheet" +
				" Where user = '" + user + "' and area = '" + area + "' and day = cast(now() as date)";
		
	    time = con.runTimeQuery(query, "total time");
		
		return time;
	}
	
	public int[] getThisWeeksTime() {
		int[] time = {0,0};
		
		LocalDate start = LocalDate.now();
		int weekDay = start.getDayOfWeek().getValue();
		start = start.minusDays((long)weekDay);
		LocalDate currentDay = LocalDate.now();
		
		String query = "select timediff(end,start) as 'total time' from timesheet" +
				" Where user = '" + user + "' and area = '" + area 
				+ "' and day BETWEEN '" + start.toString() + "' AND '" + currentDay + "'";
		
	    time = con.runTimeQuery(query, "total time");
		
		return time;
	}
	public void setStart(Timestamp t)
	{
		start = t;
		active = true;
	}
	public void setEnd(Timestamp t, String n) throws Exception
	{
		notes = n;
		end = t;
		if(start != null && end != null && start.before(end))
		{
			mySQL_update();
			notes = "";
		}
		active = false;
		
	}
	public String getNotes()
	{
		return notes;
	}
	public void setNotes(String s)
	{
		notes = s;
	}
	public void setEntry(Timestamp s, Timestamp e, String n) throws Exception
	{
		start = s; end = e;
		
		notes = n;
		mySQL_update();
		notes = "";
	}
	
	private void mySQL_update() throws Exception
	{
		//items needed for update
		String base_insert = "insert into timesheet (user, area, day, start, end, notes) ";
		Date day = new Date(start.getTime());
		Time s = new Time(start.getTime());
		Time e = new Time(end.getTime());
		//String editedNotes = addEscapeCharacters(notes);
		
		String values = "values ('" + user + "', '" + area + "', '" + day + "', '" + s + "', '" + e + "', '" + notes + "')";
		
		con.updateDatabase(base_insert + values);
	}

}
