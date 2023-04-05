package meadowfen;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


/*
 * I try to keep all the sql stuff in one spot here
 * but I do create queryies in the timesheet class that frequently calls this one.
 */
/*
 * TODO add another table that keeps a list of users and their timeheets
 * table: user, area
 */
public class MysqlConnection {
	String url, username, password;
	
	public MysqlConnection()
	{
		url = "jdbc:mysql://localhost:3306/meadowfen";
	    username = "admin";
	    password = "bootchair2railing&brick";
	    
	    //DB db = DB.newEmbeddedDB(3306);
	}

	public ArrayList<String> getUsers() {
		ArrayList<String> users = new ArrayList<String>();
		 Connection con = null;

		    try {
		      //Class.forName("com.mysql.jdbc.Driver");
		      con = DriverManager.getConnection(url, username, password);

		      //System.out.println("Connected!");
		      
		      PreparedStatement pstmt = con.prepareStatement("select distinct user from timesheet");
			    ResultSet rs = pstmt.executeQuery();
			    
			   
			    while(rs.next())
			    {
			    	String u = rs.getString("user");
			    	users.add(u);
			    }

		    } catch (SQLException ex) {
		        throw new Error("Error ", ex);
		    } finally {
		      try {
		        if (con != null) {
		            con.close();
		        }
		      } catch (SQLException ex) {
		          System.out.println(ex.getMessage());
		      }
		    }
		    return users;
	}

		//get all data
		public ArrayList<Record> getData(String area, String user, LocalDate start, LocalDate end) {
			ArrayList<Record> data = new ArrayList<Record>();
			Connection con = null;

		    try {
		      //Class.forName("com.mysql.jdbc.Driver");
		      con = DriverManager.getConnection(url, username, password);

		      //System.out.println("Connected!");
		      PreparedStatement pstmt;
		     if(start == null && end == null)
		    	  pstmt = con.prepareStatement("select id, day, start, end, timediff(end,start) as 'elapsed', notes from timesheet where user = '" + user + "' and area = '" + area + "' order by day desc");
		      else if (start != null && end == null)
		    	  pstmt = con.prepareStatement("select id, day, start, end, timediff(end,start) as 'elapsed', notes from timesheet where user = '" + user + "' and area = '" + area + "' and day >= '" + start.toString() + "' order by day desc");//where date is between start and now
		     else
		    	  pstmt = con.prepareStatement("select id, day, start, end, timediff(end,start) as 'elapsed', notes from timesheet where user = '" + user + "' and area = '" + area + "' and day BETWEEN '" + start.toString() + "' and '" + end.toString() + "' order by day desc");//where date is between start and end5
		      
		      ResultSet rs = pstmt.executeQuery();
		      
		       
			    //String headers = " date\t\t| time\t| notes";
			   // data.add(headers);
			    //Date last_day = null;
			    
			    while(rs.next())
			    {
			    	Record r = new Record(user, area);
			    	r.setId(rs.getInt("id"));
			    	r.setDay(rs.getDate("day"));
			    	r.setStart(rs.getTime("start"));
			    	r.setEnd(rs.getTime("end"));
			    	r.setElapsed(rs.getTime("elapsed"));
			    	r.setNotes(rs.getString("notes"));
			    	data.add(r);
			    	/*String row = "";
			    	Date day = rs.getDate("day");
			    	if(day.equals(last_day))
			    		row += "  -\t  -\t\t| ";
			    	else
			    		row += day.toString() + "\t| ";
			    	
			    	row += rs.getTime("elapsed").toString() + "\t| ";
			    	row += rs.getString("notes");
			    	
			    	if(row.length()>80)
			    	{
			    		int space = row.indexOf(' ', 70);
			    		String subString = row.substring(space);
			    		row = row.substring(0,space);
			    		
			    		while(subString.length() > 55)
			    		{
			    			int sp = subString.indexOf(' ',45);
			    			row += "\n\t\t\t\t\t| " + subString.substring(0, sp);
			    			subString = subString.substring(sp);
			    		}
			    		row += "\n\t\t\t\t\t| " + subString;
			    				
			    	}
			    	
			    	data.add(row);
			    	
			    	last_day = day;*/
			    }

		    } catch (SQLException ex) {
		        throw new Error("Error ", ex);
		    } finally {
		      try {
		        if (con != null) {
		            con.close();
		        }
		      } catch (SQLException ex) {
		          System.out.println(ex.getMessage());
		      }
		    }
			return data;
		}
	public ArrayList<String> getAreas(String user) {
		ArrayList<String> areas = new ArrayList<String>();
		Connection con = null;

	    try {
	      //Class.forName("com.mysql.jdbc.Driver");
	      con = DriverManager.getConnection(url, username, password);

	      //System.out.println("Connected!");
	      
	      PreparedStatement pstmt = con.prepareStatement("select distinct area from timesheet where user = '" + user + "'");
		    ResultSet rs = pstmt.executeQuery();
		    
		   
		    while(rs.next())
		    {
		    	String a = rs.getString("area");
		    	areas.add(a);
		    }

	    } catch (SQLException ex) {
	        throw new Error("Error ", ex);
	    } finally {
	      try {
	        if (con != null) {
	            con.close();
	        }
	      } catch (SQLException ex) {
	          System.out.println(ex.getMessage());
	      }
	    }
		return areas;
	}

	
	public int[] runTimeQuery(String query, String result_name)
	{
		ResultSet rs = null;
		Connection con = null;
		int[] totalTime = {0,0};

	    try {
	      //Class.forName("com.mysql.jdbc.Driver");
	      con = DriverManager.getConnection(url, username, password);

	      //System.out.println("Connected!");
	      
	      PreparedStatement pstmt = con.prepareStatement(query);
		  rs = pstmt.executeQuery();
		  
		  while(rs.next())
			{
				Time total = rs.getTime(result_name);
				String toParse = total.toString();
				
				int FC = toParse.indexOf(':');
				String hours = toParse.substring(0, FC);
				int h = Integer.parseInt(hours);
				totalTime[0] += h;
				
				toParse = toParse.substring(FC+1);
				int SC = toParse.indexOf(':');
				String minutes = toParse.substring(0,SC);
				int m = Integer.parseInt(minutes);
				totalTime[1] += m;
			}
		  
		// converting minute total to hours
	  	if(totalTime[1] >= 60)
		{
			totalTime[0] += totalTime[1]/60;
			totalTime[1] %= 60;
		}
			
	    } catch (SQLException ex) {
	        throw new Error("Error ", ex);
	    } finally {
	      try {
	        if (con != null) {
	            con.close();
	        }
	      } catch (SQLException ex) {
	          System.out.println(ex.getMessage());
	      }
	    }
		return totalTime;
	}

	public void updateDatabase(String query) throws Exception
	{
		Connection con = null;

	    try {
	      //Class.forName("com.mysql.jdbc.Driver");
	      con = DriverManager.getConnection(url, username, password);

	      //System.out.println("Connected!");
	      
	      PreparedStatement pstmt = con.prepareStatement(query);
		  pstmt.executeUpdate();

	    } catch (SQLException ex) {
	    	System.out.println("caught the sql error");
	        throw new Exception(ex.getMessage());
	    } finally {
	      try {
	        if (con != null) {
	            con.close();
	        }
	      } catch (SQLException ex) {
	          System.out.println("got an error in mysql database method");
	    	  throw new Exception(ex.getMessage());
	      }
	    }
	}
}
