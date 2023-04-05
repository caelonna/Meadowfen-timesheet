# Meadowfen-timesheet

This project requires a MySQL database connection.  
If you try to run this project, you will need to update the database info in the MysqlConnection class.
The database should have a table called "timesheet" with the format (id, area, user, day[date], start[time], end[time], notes)
It should work even when the table is empty.
