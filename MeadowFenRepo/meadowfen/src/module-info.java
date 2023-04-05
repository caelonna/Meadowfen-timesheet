/**
 * 
 */
/**
 * @author caelo
 *
 */
module meadowfen {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires javafx.base;
	
	opens meadowfen to javafx.graphics, javafx.base;
}