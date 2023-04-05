package meadowfen;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;


/*
 * This is a wrapper class for the sql database
 * TODO make date/times work both ways
 */
public class Record {
	int id;
	String user;
	String area;
	Date day;
	Time start;
	Time end;
	Time elapsed;
	String notes;
	
	/* creates a new record with all fields set */
	public Record(int id, String user, String area, Time start, Time end, String notes)
	{
		this.id = id;
		this.user = user;
		this.area = area;
		this.start = start;
		this.end = end;
		this.notes = notes;
	}
	/* creates a temporariy record for user/area */
	public Record(String user, String area)
	{
		this.user = user;
		this.area = area;
	}
	
	public int getId()
	{	return id; 	}
	public void setId(int id)
	{ 	this.id = id;	}
	
	public String getUser()
	{	return user;	}
	public void setUser(String user)
	{	this.user = user;	}
	
	public String getArea()
	{	return area;	}
	public void setArea(String area)
	{	this.area = area;	}
	
	public Date getDay()
	{	return day;		}
	public void setDay(Date day)
	{	this.day = day;		}
	
	public Time getStart()
	{	return start;	}
	public void setStart(Time start)
	{	this.start = start;	}
	
	public Time getEnd()
	{	return end;		}
	public void setEnd(Time end)
	{	this.end = end;		}
	
	public Time getElapsed()
	{	return elapsed;		}
	public void setElapsed(Time elapsed)
	{	this.elapsed = elapsed;		}
	
	public String getNotes()
	{	return notes;	}
	public void setNotes(String notes)
	{	this.notes = notes;		}

}
