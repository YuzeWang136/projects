import java.util.*;
import java.sql.*;
import java.io.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.math.BigDecimal;
public class team4{
	
	private static Connection dbcon;
	private static Scanner kboard;
	private static Scanner fileReader;
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat formatDAT = new SimpleDateFormat("MM-dd-yyyy HH24:mm");
	
	public static void main(String[] args){
		try{
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e){
			System.out.println("Error: Driver not able to be loaded");
			System.exit(1);
		}
		
		kboard = new Scanner(System.in);
		String fullUrl;
		String url;
		String database;
		String port;
		String user;
		String pass;
		
		while(true){ //default LOCAL answers: localhost,postgres,5432,postgres,[PASSWORD]

			System.out.println("\n\nDuring the sign-in process, enter '/' to restart if you enter anything incorrectly.");
			System.out.println("\n\nPlease enter the URL of your database (eg: class3.cs.pitt.edu, localhost, etc)");
			url = kboard.nextLine();
			if(url.equals("/"))
				continue;
			
			System.out.println("Please enter the database name (eg: postgres, etc): ");
			database = kboard.nextLine();
			if(database.equals("/"))
				continue;
			
			System.out.println("Please enter the port of your database (eg: 5432): ");
			port = kboard.nextLine();
			if(port.equals("/"))
				continue;
			
			System.out.println("Please enter your username: ");
			user = kboard.nextLine();
			if(user.equals("/"))
				continue;
			
			System.out.println("Please enter your password: ");
			pass = kboard.nextLine();
			if(pass.equals("/"))
				continue;

			fullUrl = "jdbc:postgresql://localhost:5432/postgres";
			try{
				dbcon = DriverManager.getConnection(fullUrl.toLowerCase(),user,pass);
				break;
			}catch(Exception e){
				System.out.println("\n\nError: Connection failed. Please check your input\n\n");
				dbcon = null;
			}
		}
		
		String role;
		while(true){
			System.out.println("\n\nPlease select your role (enter the number associated with the role): \n1:  admin\n2:  customer\n\n");
			role = kboard.nextLine();
			
			if(role.equals("1")){
				admin();
				break;
			}else if(role.equals("2")){
				customer();
				break;
			}
			
			System.out.println("Invalid Input: Please refer to the requested inputs");
		}
		
		System.out.println("\n\nThank you for using the Pitt Tours Flight Reservation System!");
	}
	
	
	
	
	
	
	
	public static void admin(){
		System.out.println("Welcome Administrator!\n");
		int choice; //used in all choices
		String info1; //used for info retrieval
		String info2; //used only when 2 values are needed in an info retrieval
		String[] pieces;
		
		PreparedStatement ptmt;
		ResultSet rs;
		
		while(true){
			System.out.println("\n\nWhat would you like to do? (enter the number associated with the action)\n1:  Erase the database\n2:  Load airline information\n3:  Load schedule information\n4:  Load pricing information\n5:  Load plane information\n6:  Generate passenger manifest for specific flight on given day\n7:  Update the current timestamp\n8:  Quit");
			choice = Integer.parseInt(kboard.nextLine());
			switch(choice){
				case 1:
					while(true){
						System.out.println("\n\nDo you really want to clear the database? (y/n)");
						info1 = kboard.nextLine().toLowerCase();
						if(info1.equals("y")){
							try{
								dbcon.setAutoCommit(false);
								ptmt = dbcon.prepareStatement("BEGIN");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM ourtimestamp CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM reservation_detail CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM reservation CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM customer CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM price CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM flight CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM plane CASCADE");
								ptmt.execute();
								ptmt = dbcon.prepareStatement("DELETE FROM airline CASCADE");
								ptmt.execute();
								dbcon.commit();
								
								dbcon.setAutoCommit(true);
								
								System.out.println("Successfully cleared database.");
							}catch(Exception e){
								System.out.println("Clearing database failed.\n" + e.toString());
								try{
									dbcon.rollback();
								}catch(SQLException ee){
									System.out.println(ee.toString());
								}
							}
							break;
						}else if(info1.equals("n"))
							break;
						else
							System.out.println("\n\nInvalid Input: Please refer to the requested inputs");
					}
					break;
					
					
				case 2:
					System.out.println("\n\nPlease supply the filename where airline information is stored.");
					info1 = kboard.nextLine();
					try{
						fileReader = new Scanner(new File(info1));
					}catch(FileNotFoundException e){
						System.out.println("File '" + info1 + "' not found\n");
					}
					
					try{
						dbcon.setAutoCommit(false);
						ptmt = dbcon.prepareStatement("BEGIN");
						ptmt.execute();
						
						while(fileReader.hasNextLine()){
							info2 = "INSERT INTO AIRLINE(airline_id, airline_name, airline_abbreviation, year_founded) VALUES (";
							pieces = fileReader.nextLine().split("\t");
							
							for(int i = 0; i<pieces.length;i++){
								if(pieces[i].length()==0)
									continue;

								if(i==pieces.length-1)
									info2 = info2 + "'" + pieces[i].trim() + "');";
								else
									info2 = info2 + "'" + pieces[i].trim() + "', ";
							} //1, 'txt txt txt', '
							ptmt = dbcon.prepareStatement(info2);
							ptmt.execute();
						}
						
						dbcon.commit();
						dbcon.setAutoCommit(true);
								
						System.out.println("Successfully added file data to database.");
					}catch(Exception e){
						System.out.println("Adding file data to database failed. Please verify that the data is valid.\n" + e.toString());
						try{
							dbcon.rollback();
						}catch(SQLException ee){
							System.out.println(ee.toString());
						}
					}
					
					//load
					break;
			
			
				case 3:
					System.out.println("\n\nPlease supply the filename where schedule information is stored.");
					info1 = kboard.nextLine();
					try{
						fileReader = new Scanner(new File(info1));
					}catch(FileNotFoundException e){
						System.out.println("File '" + info1 + "' not found\n");
					}
					
					try{
						dbcon.setAutoCommit(false);
						ptmt = dbcon.prepareStatement("BEGIN");
						ptmt.execute();
						while(fileReader.hasNextLine()){
							info2 = "INSERT INTO FLIGHT(flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time, weekly_schedule) VALUES (";
							pieces = fileReader.nextLine().split("\t");
							for(int i = 0; i<pieces.length;i++){
								if(pieces[i].length()==0)
									continue;
								if(i==pieces.length-1)
									info2 = info2 + "'" + pieces[i].trim() + "');";
								else
									info2 = info2 + "'" + pieces[i].trim() + "', ";
							} //1, 'txt txt txt', '
							info2 = info2.trim();
							ptmt = dbcon.prepareStatement(info2);
							ptmt.execute();
						}
						
						dbcon.commit();
						dbcon.setAutoCommit(true);
								
						System.out.println("Successfully added file data to database.");
					}catch(Exception e){
						System.out.println("Adding file data to database failed. Please verify that the data is valid.\n" + e.toString());
						try{
							dbcon.rollback();
						}catch(SQLException ee){
							System.out.println(ee.toString());
						}
					}
					//load
					break;
			
			
				case 4:
					System.out.println("\n\nPlease supply the filename where pricing information is stored.");
					info1 = kboard.nextLine();
					try{
						fileReader = new Scanner(new File(info1));
					}catch(FileNotFoundException e){
						System.out.println("File '" + info1 + "' not found\n");
					}
					
					try{
						dbcon.setAutoCommit(false);
						ptmt = dbcon.prepareStatement("BEGIN");
						ptmt.execute();
						
						while(fileReader.hasNextLine()){
							info2 = "INSERT INTO PRICE(departure_city, arrival_city, airline_id, high_price, low_price) VALUES (";
							pieces = fileReader.nextLine().split("\t");
							for(int i = 0; i<pieces.length;i++){
								if(pieces[i].length()==0)
									continue;
								if(i==pieces.length-1)
									info2 = info2 + "'" + pieces[i].trim() + "');";
								else
									info2 = info2 + "'" + pieces[i].trim() + "', ";
							} //1, 'txt txt txt', '
							info2 = info2.trim();
							ptmt = dbcon.prepareStatement(info2);
							ptmt.execute();
						}
						
						dbcon.commit();
						dbcon.setAutoCommit(true);
								
						System.out.println("Successfully added file data to database.");
					}catch(Exception e){
						System.out.println("Adding file data to database failed. Please verify that the data is valid.\n" + e.toString());
						try{
							dbcon.rollback();
						}catch(SQLException ee){
							System.out.println(ee.toString());
						}
					}
					//load
					break;
			
			
				case 5:
					System.out.println("\n\nPlease supply the filename where plane information is stored.");
					info1 = kboard.nextLine();
					try{
						fileReader = new Scanner(new File(info1));
					}catch(FileNotFoundException e){
						System.out.println("File '" + info1 + "' not found\n");
					}
					
					try{
						dbcon.setAutoCommit(false);
						ptmt = dbcon.prepareStatement("BEGIN");
						ptmt.execute();
						
						while(fileReader.hasNextLine()){
							info2 = "INSERT INTO PLANE(plane_type, manufacturer, plane_capacity, last_service, year, owner_id) VALUES (";
							pieces = fileReader.nextLine().split("\t");
							
							for(int i = 0; i<pieces.length;i++){
								if(pieces[i].length()==0)
									continue;
								if(i==pieces.length-1)
									info2 = info2 + "'" + pieces[i].trim() + "');";
								else{
									if(i==3){
										if(pieces[i].charAt(2)=='/'){ //may be more effective (and geenralized) way to recognize dates
											if(Integer.parseInt(pieces[i].substring(0,2))>12)
												info2 = info2 + "TO_DATE('" + pieces[i].trim() + "', 'DD/MM/YYYY'), ";
											else
												info2 = info2 + "TO_DATE('" + pieces[i].trim() + "', 'MM/DD/YYYY'), ";
										}else if(pieces[i].charAt(2)=='-'){
											if(Integer.parseInt(pieces[i].substring(0,2))>12)
												info2 = info2 + "TO_DATE('" + pieces[i].trim() + "', 'DD-MM-YYYY'), ";
											else
												info2 = info2 + "TO_DATE('" + pieces[i].trim() + "', 'MM-DD-YYYY'), ";
										}
									}else
										info2 = info2 + "'" + pieces[i].trim() + "', ";
								}
							} //1, 'txt txt txt', '
							
							info2 = info2.trim();
							ptmt = dbcon.prepareStatement(info2);
							ptmt.execute();
						}
						
						dbcon.commit();
						dbcon.setAutoCommit(true);
								
						System.out.println("Successfully added file data to database.");
					}catch(Exception e){
						System.out.println("Adding file data to database failed. Please verify that the data is valid. \n(Note: Date format must be MM/DD/YYYY, DD/MM/YYYY, MM-DD-YYYY, or DD-MM-YYYY)\n" + e.toString());
						try{
							dbcon.rollback();
						}catch(SQLException ee){
							System.out.println(ee.toString());
						}
					}
					//load
					break;
			
			
				case 6:
					System.out.println("\n\nPlease enter the desired flight number.");
					info1 = kboard.nextLine();
					
					while(true){
						System.out.println("\n\nPlease enter the desired date. (YYYY-MM-DD)");
						info2 = kboard.nextLine();
						if(isDate(info2)){
							break;
						}else
							System.out.println("\n\nInvalid Input: Please refer to the requested inputs");
						
					}
					
					try{//salutation,fname,lname
						ptmt = dbcon.prepareStatement("SELECT salutation, first_name, last_name from ((SELECT cid, salutation, first_name, last_name FROM customer) t1"
														+" join (SELECT reservation_number, cid FROM reservation) t2 on t1.cid = t2.cid) natural join reservation_detail natural join flight"
														+" where flight_number = '" + info1 +"' and"
														+" (flight_date between TO_DATE('" + info2 +"','YYYY-MM-DD') and (TO_DATE('" + info2 +"','YYYY-MM-DD') + '1 day'::interval));");
						rs = ptmt.executeQuery();
						
						System.out.println("\n");
						System.out.format("%-12s%-31s%-31s", "Salutation", "|First Name", "|Last Name");
						System.out.println("\n-----------------------------------------------------");
						
						while(rs.next()){
							System.out.format("%-12s%-31s%-31s", rs.getString("salutation"), ("|"+rs.getString("first_name")), ("|"+rs.getString("last_name")));
							System.out.println();
						}
						
						rs.close();
						System.out.println("\n\nDone!");		
						}catch(Exception e){
							System.out.println("Passenger Manifest retrieval failed.\n" + e.toString());
						}
					break;
			
			
				case 7:
					while(true){
						System.out.println("\n\nPlease enter the date to update to. (YYYY-MM-DD)");
						info1 = kboard.nextLine();
						if(isDate(info1)){
							break;
						}else
							System.out.println("\n\nInvalid Input: Please refer to the requested inputs");
					}
					
					while(true){
						System.out.println("\n\nPlease enter the time to update to. (HH:MM, 24 Hour Time Format (eg: 23:59))");
						info2 = kboard.nextLine();
						if(isTime(info2)){
							break;
						}else
							System.out.println("\n\nInvalid Input: Please refer to the requested inputs");
					}
					
					
					try{
						dbcon.setAutoCommit(false);
						ptmt = dbcon.prepareStatement("BEGIN");
						ptmt.execute();
						ptmt = dbcon.prepareStatement("DELETE FROM ourtimestamp CASCADE");
						ptmt.execute();
						ptmt = dbcon.prepareStatement("INSERT INTO OURTIMESTAMP (c_timestamp) VALUES (TO_TIMESTAMP('" + info1 + " " + info2 +  "', 'YYYY-MM-DD HH24:MI'));");
						ptmt.execute();
						dbcon.commit();
								
						dbcon.setAutoCommit(true);
								
								System.out.println("Successfully updated current OurTimeStamp to " + info1 + " " + info2 +".");
						}catch(Exception e){
							System.out.println("Updating OurTimeStamp failed.\n" + e.toString());
							try{
								dbcon.rollback();
							}catch(SQLException ee){
								System.out.println(ee.toString());
							}
						}
					
					//change time
					break;
			
				case 8:
					return; //returns to main
				default:
					System.out.println("\n\nNot a valid action");
			
			}
		}
	}
	
	
	
	
	
	

	public static void customer(){
		System.out.println("Customer view");
		int choice;
		String salutation, first_name, last_name, street, city, state, phone_number, email_address, credit_card_number, credit_card_expiration_date, frequent_miles;
		String city1, city2;
		String date;
		String airline_name;
		String weekly_schedule1, weekly_schedule2;
		String reservation_date;
		int airline_id;
		int reservation_number;
		int cid, flight_number, departure_date, leg;
		double high_price, low_price;
		BigDecimal cost;
		double discount;
		String sql, sql2;
		String[] name;
		boolean judge1, judge2;
		boolean ticketed;
		java.sql.Date sqlDate = null;
		java.sql.Timestamp sqlTime = null;
		java.util.Date javaDate = null;
		java.util.Date javaTime = null;
		PreparedStatement ptmt;
		ResultSet rs, rs2;
		while(true) {
			System.out.println("\n What would you like to do? (enter the number associated with the action)"
					+ "\n 1: Add customer"
					+ "\n 2: Show customer info, given customer name "
					+ "\n 3: Find price for flights between two cities"
					+ "\n 4: Find all routes between two cities"
					+ "\n 5: Find all routes between two cities of a given airline"
					+ "\n 6: Find all routes with available seats between two cities on a given date"
					+ "\n 7: Add reservation"
					+ "\n 8: Delete reservation"
					+ "\n 9: Show reservation info, given reservation number "
					+ "\n 10: Buy ticket from existing reservation" 
					+ "\n 11: Find the top-k customers for each airline"
					+ "\n 12: Find the top-k traveled customers for each airline"
					+ "\n 13: Rank the airlines based on customer satisfaction"
					+ "\n 14: Quit");
			choice = Integer.parseInt(kboard.nextLine());
			switch(choice) {
				case 1:
					//Add customer
					cid = 0;
					try {
						ptmt = dbcon.prepareStatement("SELECT cid FROM CUSTOMER ORDER BY cid DESC FETCH FIRST 1 ROW ONLY");
						rs = ptmt.executeQuery();
						if(!rs.next()) {
							cid = 1;
						}
						else {
							cid = rs.getInt("cid");
						}
					}catch (SQLException e) {
						System.out.println(e.getMessage());
					}
					System.out.println(cid);
					System.out.print("Adding new Customer");
					System.out.print("\nPlease input your salutation(Mr/Mrs/Ms): ");
					salutation = kboard.nextLine();
					System.out.print("\nPlease input your first name: ");
					first_name = kboard.nextLine();
					System.out.print("\nPlease input your last name: ");
					last_name = kboard.nextLine();
					System.out.print("\nPlease input your street: ");
					street = kboard.nextLine();
					System.out.print("\nPlease input your city: ");
					city = kboard.nextLine();
					System.out.print("\nPlease input your state: ");
					state = kboard.nextLine();
					System.out.print("\nPlease input your phone number: ");
					phone_number = kboard.nextLine();
					System.out.print("\nPlease input your email address: ");
					email_address = kboard.nextLine();
					System.out.print("\nPlease input your credit card number: ");
					credit_card_number = kboard.nextLine();
					judge2 = true; //check date format
					while(judge2) {
						System.out.print("\nPlease input your credit card expiration date (YYYY-MM-DD): ");
						credit_card_expiration_date = kboard.nextLine();
						if(isDate(credit_card_expiration_date)){
							try{
								javaDate = formatDate.parse(credit_card_expiration_date);
							judge2 = false;
							}catch(Exception e){
								judge2 = true;
								System.out.println("wrong date format.");
							}
						}else{
							judge2 = true;
							System.out.println("wrong date format.");
						}
					}
					sqlDate = new java.sql.Date(javaDate.getTime());
					System.out.print("\nPlease input your frequent miles: ");
					frequent_miles = kboard.nextLine();
					sql = "INSERT INTO CUSTOMER(cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state, phone, email, frequent_miles) Values(?,?,?,?,?,?,?,?,?,?,?,?)";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setInt(1, cid);
						ptmt.setString(2, salutation);
						ptmt.setString(3, first_name);
						ptmt.setString(4, last_name);
						ptmt.setString(5, credit_card_number);
						ptmt.setDate(6, sqlDate);
						ptmt.setString(7, street);
						ptmt.setString(8, city);
						ptmt.setString(9, state);
						ptmt.setString(10, phone_number);
						ptmt.setString(11, email_address); //EMIAL_DOMAIN check may done in the java side since there is no method for that datatype
						ptmt.setString(12, frequent_miles);
						ptmt.execute();
						judge1 = false; //stop the while loop
					}catch(SQLException e) {
						judge1 = true; //re-input
						System.out.println("insert fail.");
						System.out.println(e.getMessage());
					}
					System.out.println("insert success");
					break;
				case 2:
					//Show customer info, given customer name
					judge1 = true; //check for select fail;
					while (judge1) {
						judge2 = true;
						first_name = null;
						last_name = null;
						while(judge2) {//check for input name
							System.out.print("\nPlease input your name (format: \"firstname lastname\"): ");
							try{
								name = kboard.nextLine().split(" ");
								first_name = name[0];
								last_name = name[1];
								judge2 = false;
							}catch (Exception e){
								System.out.println("Wrong name format");
								judge2= true;
							}
						}
						sql = "SELECT salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state, phone, email, frequent_miles"
								+ " FROM CUSTOMER WHERE first_name = ? AND last_name = ?";
						try {
							ptmt = dbcon.prepareStatement(sql);
							ptmt.setString(1, first_name);
							ptmt.setString(2, last_name);
							rs = ptmt.executeQuery();
							rs.next();
							System.out.println("\n salutation: " + rs.getString("salutation")
								+"\n first name: " + rs.getString("first_name")
								+"\n last name: " + rs.getString("last_name")
								+"\n credit card num: " + rs.getString("credit_card_num")
								+"\n credit card expire: " + rs.getDate("credit_card_expire")
								+"\n street: " + rs.getString("street")
								+"\n city: " + rs.getString("city")
								+"\n state: " + rs.getString("state")
								+"\n phone: " + rs.getString("phone")
								+"\n email: " + rs.getString("email")
								+"\n frequent miles: " + rs.getString("frequent_miles"));
							judge1 = false;
						}catch(SQLException e) {
							System.out.println("select fail");
							System.out.println(e.getMessage());
							judge1 = true;
						}
					}
					break;
				case 3:
					//Find price for flights between two cities
					judge2 = true;
					first_name = null;
					last_name = null;
					frequent_miles = null;
					discount = 1;
					high_price = 0;
					low_price = 0;
					while(judge2) {//check for input name
						System.out.print("\nPlease input your name (format: \"firstname lastname\"): ");
						try{
							name = kboard.nextLine().split(" ");
							first_name = name[0];
							last_name = name[1];
							judge2 = false;
						}catch (Exception e){
							System.out.println("Wrong name format");
							judge2= true;
						}
					}
					city1 = null;
					city2 = null;
					sql = "SELECT frequent_miles"
							+ " FROM CUSTOMER WHERE first_name = ? AND last_name = ?";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, first_name);
						ptmt.setString(2, last_name);
						rs = ptmt.executeQuery();
						rs.next();
						frequent_miles = rs.getString("frequent_miles");
						judge2 = false;
					}
					catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}	
					sql = "SELECT high_price, low_price, airline_abbreviation"
							+ " FROM PRICE P JOIN AIRLINE A On P.airline_id = A.airline_id"
							+ " WHERE departure_city = ? AND arrival_city = ?";
					System.out.println("Please input the first city: ");
					city1 = kboard.nextLine();
					System.out.println("Please input the second city: ");
					city2 = kboard.nextLine();
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city1);
						ptmt.setString(2, city2);
						rs = ptmt.executeQuery();
						if(rs.next()) {
							if(frequent_miles.equals(rs.getString("airline_abbreviation"))) {
								discount = 0.9;
							}
							else {
								discount = 1;
							}
							high_price += rs.getInt("high_price") * discount;
							low_price += rs.getInt("low_price") * discount;
							System.out.println("\n from " + city1 + " to " + city2
									+"\n high price: " + rs.getInt("high_price") * discount
									+"\n low price: " + rs.getInt("low_price") * discount);
						}
						else {
							System.out.println("no route");
						}
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city2);
						ptmt.setString(2, city1);
						rs = ptmt.executeQuery();
						if(rs.next()) {
							if(frequent_miles.equals(rs.getString("airline_abbreviation"))) {
								discount = 0.9;
							}
							else {
								discount = 1;
							}
							high_price += rs.getInt("high_price") * discount;
							low_price += rs.getInt("low_price") * discount;
							System.out.println("\n from " + city2 + " to " + city1
									+"\n high price: " + rs.getInt("high_price") * discount
									+"\n low price: " + rs.getInt("low_price") * discount);
						}
						else {
							System.out.println("no route");
						}
						if(high_price == 0 && low_price == 0) {
							System.out.println("no route");
						}
						else {
							System.out.println("\n round trip between " + city1 + " and " + city2
									+"\n high price: " + high_price
									+"\n low price: " + low_price);
						}
						judge1 = false;
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					break;
				case 4:
					//Find all routes between two cities
					weekly_schedule1 = null;
					weekly_schedule2 = null;
					city1 = null;
					city2 = null;
					System.out.println("Please input the departure city: ");
					city1 = kboard.nextLine();
					System.out.println("Please input the arrival city: ");
					city2 = kboard.nextLine();
					sql = "SELECT flight_number, departure_city, departure_time, arrival_time" +
							" FROM FLIGHT WHERE departure_city = ? AND arrival_city = ?";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city1);
						ptmt.setString(2, city2);
						rs = ptmt.executeQuery();
						System.out.println("direct routes: ");
						while(rs.next()) {
							System.out.println("\n from " + city1 + " to " + city2
								+"\n flight number: " + rs.getString("flight_number")
								+"\n departure city: " + rs.getString("departure_city")
								+"\n departure time: " + rs.getString("departure_time")
								+"\n arrival time: " + rs.getString("arrival_time"));
						}
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					sql = "SELECT  F1.flight_number AS flight_number1, F2.flight_number AS flight_number2, F1.departure_city AS departure_city, F1.departure_time AS departure_time, F2.arrival_time AS arrival_time, F1.weekly_schedule AS weekly_schedule1, F2.weekly_schedule AS weekly_schedule2 " + 
							"FROM FLIGHT F1 LEFT JOIN FLIGHT F2 ON F1.arrival_city = F2.departure_city " + 
							"WHERE F1.departure_city = ? AND F2.arrival_city = ? AND CAST(F2.departure_time AS integer) - CAST(F1.arrival_time AS integer) >= 100";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city1);
						ptmt.setString(2, city2);
						rs = ptmt.executeQuery();
						System.out.println("double flights routes: ");
						while(rs.next()) {
							weekly_schedule1 = rs.getString("weekly_schedule1");
							weekly_schedule2 = rs.getString("weekly_schedule2");
							for(int i = 0; i < 7; i++) {
								if(weekly_schedule1.charAt(i) == weekly_schedule2.charAt(i)) {
									System.out.println("\n from " + city1 + " to " + city2
											+"\n first flight number: " + rs.getString("flight_number1")
											+"\n second flight number: " + rs.getString("flight_number2")
											+"\n departure city: " + rs.getString("departure_city")
											+"\n departure time: " + rs.getString("departure_time")
											+"\n arrival time: " + rs.getString("arrival_time"));
									break;
								}
							}
						}
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					break;
				case 5:
					//Find all routes between two cities of a given airline
					weekly_schedule1 = null;
					weekly_schedule2 = null;
					city1 = null;
					city2 = null;
					airline_name = null;
					airline_id = -1;
					sql = "SELECT airline_id "
							+ "FROM AIRLINE "
							+ "WHERE airline_name = ?";
					System.out.println("Please input the airline name: ");
					airline_name = kboard.nextLine();
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, airline_name);
						rs = ptmt.executeQuery();
						if(!rs.next()) {
							System.out.println("airline does not exist");
							break;
						}
						airline_id = rs.getInt("airline_id");
					}catch(SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					System.out.println("Please input the departure city: ");
					city1 = kboard.nextLine();
					System.out.println("Please input the arrival city: ");
					city2 = kboard.nextLine();
					sql = "SELECT flight_number, departure_city, departure_time, arrival_time" +
							" FROM FLIGHT WHERE departure_city = ? AND arrival_city = ? AND airline_id = ?";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city1);
						ptmt.setString(2, city2);
						ptmt.setInt(3, airline_id);
						rs = ptmt.executeQuery();
						System.out.println("direct routes: ");
						while(rs.next()) {
							System.out.println("\n from " + city1 + " to " + city2
								+"\n flight number: " + rs.getString("flight_number")
								+"\n departure city: " + rs.getString("departure_city")
								+"\n departure time: " + rs.getString("departure_time")
								+"\n arrival time: " + rs.getString("arrival_time"));
						}
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					sql = "SELECT  F1.flight_number AS flight_number1, F2.flight_number AS flight_number2, F1.departure_city AS departure_city, F1.departure_time AS departure_time, F2.arrival_time AS arrival_time, F1.weekly_schedule AS weekly_schedule1, F2.weekly_schedule AS weekly_schedule2 " + 
							"FROM FLIGHT F1 LEFT JOIN FLIGHT F2 ON F1.arrival_city = F2.departure_city " + 
							"WHERE F1.departure_city = ? AND F2.arrival_city = ? AND CAST(F2.departure_time AS integer) - CAST(F1.arrival_time AS integer) >= 100 AND F1.airline_id = ? AND F2.airline_id = ?";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city1);
						ptmt.setString(2, city2);
						ptmt.setInt(3, airline_id);
						ptmt.setInt(4, airline_id);
						rs = ptmt.executeQuery();
						System.out.println("double flights routes: ");
						while(rs.next()) {
							weekly_schedule1 = rs.getString("weekly_schedule1");
							weekly_schedule2 = rs.getString("weekly_schedule2");
							for(int i = 0; i < 7; i++) {
								if(weekly_schedule1.charAt(i) == weekly_schedule2.charAt(i)) {
									System.out.println("\n from " + city1 + " to " + city2
											+"\n first flight number: " + rs.getString("flight_number1")
											+"\n second flight number: " + rs.getString("flight_number2")
											+"\n departure city: " + rs.getString("departure_city")
											+"\n departure time: " + rs.getString("departure_time")
											+"\n arrival time: " + rs.getString("arrival_time"));
									break;
								}
							}
						}
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					break;
				case 6:
					String flight_date;
					//Find all routes with available seats between two cities on a given date
					System.out.println("Please input the departure city: ");
					city1 = kboard.nextLine();
					System.out.println("Please input the arrival city: ");
					city2 = kboard.nextLine();

					while (true){
						System.out.println("Please input the date (YYYY-MM-DD): ");
						date = kboard.nextLine();
						if(isDate(date)){
							break;
						} else {
							System.out.println("\n\nInvalid Input - Please input the date (YYYY-MM-DD): ");
						}
					}

					sql = "SELECT FLIGHT.flight_number, departure_city, departure_time, arrival_time, flight_date" +
							" FROM FLIGHT JOIN RESERVATION_DETAIL ON Flight.flight_number = Reservation_Detail.flight_number" +
							" WHERE departure_city = ? AND arrival_city = ?";

					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, city1);
						ptmt.setString(2, city2);
						rs = ptmt.executeQuery();
						System.out.println("\n Flights from " + city1 + " to " + city2 + " on " + date + "\n");
						while(rs.next()) {
							flight_number = Integer.parseInt(rs.getString("flight_number"));
							flight_date = rs.getString("flight_date").substring(0,10);
							ptmt = dbcon.prepareStatement("SELECT isPlaneFull FROM isPlaneFull("+flight_number+")");
							rs2 = ptmt.executeQuery();
							while(rs2.next()){
								if(rs2.getString("isPlaneFull").equals("0") && date.equals(flight_date)){ //isPlaneFull return false
									System.out.println("Flight number: " + flight_number);
									System.out.println("Departure city: " + rs.getString("departure_city"));
									System.out.println("Departure time: " + rs.getString("departure_time"));
									System.out.println("Arrival time: " + rs.getString("arrival_time"));
									System.out.println();
								} else {

								}
							}
						}
					}catch (Exception e) {
						System.out.println("Route retrieval failed:\n" + e.toString());
					}
					break;

				case 7:
					//Add reservation
					first_name = null;
					last_name = null;
					judge2 = true;
					credit_card_number = null;
					cid = 0;
					System.out.println("\n adding reservation");
					while(judge2) {//check for input name
						System.out.print("\nPlease input your name (format: \"firstname lastname\"): ");
						try{
							name = kboard.nextLine().split(" ");
							first_name = name[0];
							last_name = name[1];
							judge2 = false;
						}catch (Exception e){
							System.out.println("Wrong name format");
							judge2= true;
						}
					}
					// get cid frequent miles and credit_card_num by name
					sql = "SELECT cid, frequent_miles, credit_card_num "
							+ "FROM CUSTOMER "
							+ "WHERE first_name = ? AND last_name = ?";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setString(1, first_name);
						ptmt.setString(2, last_name);
						rs = ptmt.executeQuery();
						if(!rs.next()) {
							System.out.println("customer do not exist");
							break;
						}
						else {
							cid = rs.getInt("cid");
							frequent_miles = rs.getString("frequent_miles");
							credit_card_number = rs.getString("credit_card_num");
						}
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					//get reservation number
					reservation_number = 0;
					sql = "SELECT reservation_number "
							+"FROM reservation"
							+" ORDER BY reservation_number DESC"
							+" FETCH FIRST 1 ROW ONLY";
					try {
						ptmt = dbcon.prepareStatement(sql);
						rs = ptmt.executeQuery();
						if(rs.next()) {
							reservation_number = rs.getInt("reservation_number") + 1;
						}
						else {
							reservation_number = 1;
						}
					}catch (SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					leg = 0;
					judge2 = true;
					while(judge2) {
						System.out.println("Please input your reservation date (format: \"MM-dd-yyyy HH24:mm\"): ");
						reservation_date = kboard.nextLine();
						try{
							javaTime = formatDAT.parse(reservation_date);
							judge2 = false;
						}catch(Exception e){
							judge2 = true;
							System.out.println("wrong date format.");
						}
					}
					System.out.println("Please input your cost: ");
					cost = new BigDecimal(kboard.nextLine());
					//insert into reservation
					sqlTime = new java.sql.Timestamp(javaTime.getTime());
					sql = "INSERT INTO RESERVATION(reservation_number, cid, cost, credit_card_num, reservation_date, ticketed) "
							+ "VALUES(?, ?, ?, ?, ?, ?)";
					try {
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setInt(1, reservation_number);
						ptmt.setInt(2, cid);
						ptmt.setInt(3,  0);
						ptmt.setString(4, credit_card_number);
						ptmt.setTimestamp(5, sqlTime);
						ptmt.setBoolean(6, false);
						ptmt.execute();
					}catch (SQLException e) {
						System.out.println("insert fail");
						System.out.println(e.getMessage());
					}
					//insert into reservation detail
					while(leg <= 4) {
						leg++;
						sql = "INSERT INTO RESERVATION_DETAIL(reservation_number, flight_number, flight_date, leg)"
								+ " VALUES(?, ?, ?, ?)";
						System.out.println("Please input the flight number (0 if end): ");
						flight_number = Integer.parseInt(kboard.nextLine());
						if(flight_number == 0) break;
						judge2 = true;
						while(judge2) {
							System.out.println("Please input the departure time (format: HH:mm:ss): ");
							city2 = kboard.nextLine();
							try {
								javaTime = formatTime.parse(city2);
								judge2 = false;
							} catch (ParseException e) {
								judge2 = true;
								System.out.print("Wrong time format");
							}
							sqlTime = new java.sql.Timestamp(javaTime.getTime());
						}
						try {
							ptmt = dbcon.prepareStatement(sql);
							ptmt.setInt(1, reservation_number);
							ptmt.setInt(2, flight_number);
							ptmt.setTimestamp(3, sqlTime);
							ptmt.setInt(4, leg);
							ptmt.execute();
						}catch (SQLException e) {
							System.out.println("insert fail");
							System.out.println(e.getMessage());
						}
					}
					
					System.out.println("Inser success. Now you can buy the ticket in function 10 with reservation number " + reservation_number);
					break;
				case 8:
					//Delete reservation
					System.out.println("Please provide the reservation number to be deleted: ");
					choice = Integer.parseInt(kboard.nextLine());

					sql = "DELETE FROM RESERVATION WHERE reservation_number IN (?)";

					try{
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setInt(1, choice);
						rs = ptmt.executeQuery();
					} catch (Exception e){
						// Do nothing - Always returns deletion error since query returns nothing
					}

					sql = "DELETE FROM RESERVATION_DETAIL WHERE reservation_number = ?";

					try{
						ptmt = dbcon.prepareStatement(sql);
						ptmt.setInt(1, choice);
						rs = ptmt.executeQuery();
						System.out.println("\nDeltion complete\n");
					} catch (Exception e){
						// Do nothing - Always returns deletion error since query returns nothing
					}

					System.out.println("\nDeletion complete!\n");

					// Plane downgrade trigger should fire if applicable

					break;
				case 9:
					//Show reservation info, given reservation number
					//Show reservation info, given reservation number
					judge1 = true;
					while(judge1) {
						reservation_number = -1;
						sql = "SELECT flight_number, flight_date "
							+" FROM RESERVATION AS T1 JOIN RESERVATION_DETAIL T2 on T1.reservation_number = T2.reservation_number"
							+ " WHERE T1.reservation_number = ?";
						System.out.println("Please input your reservation number");
						reservation_number = Integer.parseInt(kboard.nextLine());
						try {
							ptmt = dbcon.prepareStatement(sql);
							ptmt.setInt(1, reservation_number);
							rs = ptmt.executeQuery();
							if(!rs.next()) {
								System.out.println("non-exist reservation number");
								break;
							}
							System.out.println("\n your reservation detial: ");
							System.out.println("\nflightnumber: " + rs.getString("flight_number") + 
									"\nflight date:" + rs.getString("flight_date"));
							while(rs.next()) {
								System.out.println("\nflightnumber: " + rs.getString("flight_number") + 
										"\nflight date:" + rs.getString("flight_date"));
							}
							judge1 = false;
						}catch (Exception e) {
							System.out.println(e.getMessage());
							judge1 = true;
						}
					}
					break;
				case 10:
					//Buy ticket from existing reservation
					judge1 = true;
					while(judge1) {
						reservation_number = -1;
						sql = "UPDATE RESERVATION "
							+" set ? = ?"
							+ " WHERE reservation_number = ?";
						System.out.println("Please input your reservation number");
						reservation_number = Integer.parseInt(kboard.nextLine());
						try {
							ptmt = dbcon.prepareStatement(sql);
							ptmt.setString(1, "ticketed");
							ptmt.setBoolean(2, true);
							ptmt.setInt(3, reservation_number);
							ptmt.execute();
							judge1 = false;
						}catch (Exception e) {
							System.out.println("update fail");
							judge1 = true;
						}
					}
					break;
				case 11:
					//Find the top-k customers for each airline
					System.out.println("How many top customers would you like to view?");
					choice = Integer.parseInt(kboard.nextLine());

					sql = "SELECT DISTINCT a.airline_name\n" +
							" FROM reservation JOIN customer on reservation.cid = customer.cid JOIN reservation_detail rd on reservation.reservation_number = rd.reservation_number JOIN airline a on customer.frequent_miles = a.airline_abbreviation\n" +
							" GROUP BY reservation.cid, cost, first_name, last_name, a.airline_name\n" +
							" ORDER BY a.airline_name ASC;";

					sql2 = "SELECT first_name, last_name, airline_name, SUM(cost) as thiscost\n" +
							"FROM (SELECT DISTINCT customer.first_name, customer.last_name, a.airline_name, cost\n" +
							"FROM reservation JOIN customer on reservation.cid = customer.cid JOIN reservation_detail rd on reservation.reservation_number = rd.reservation_number JOIN airline a on customer.frequent_miles = a.airline_abbreviation) AS sQ\n" +
							" WHERE airline_name = ?\n" +
							"GROUP BY first_name, last_name, airline_name\n" +
							"ORDER BY thiscost DESC" +
							" LIMIT ?;";


					try {
						System.out.println();
						ptmt = dbcon.prepareStatement(sql);
						rs = ptmt.executeQuery();
						while(rs.next()){
							airline_name = rs.getString("airline_name");
							try{
								ptmt = dbcon.prepareStatement(sql2);
								ptmt.setInt(2,choice);
								ptmt.setString(1,airline_name);
								rs2 = ptmt.executeQuery();
								while(rs2.next()){
									System.out.println(rs2.getString("first_name") + " " + rs2.getString("last_name") + "  Spent:" + rs2.getInt("thiscost") + "  " + rs2.getString("airline_name"));
								}
							} catch (Exception e) {
								System.out.println("Getting customers failed: " + e.toString());
							}
						}
					} catch (Exception e){
						System.out.println("Getting airline failed: " + e.toString());
					}
					break;
				case 12:
					//Find the top-k traveled customers for each airline
					System.out.println("How many top customers would you like to view?");
					choice = Integer.parseInt(kboard.nextLine());

					sql = "SELECT DISTINCT a.airline_name\n" +
							" FROM reservation JOIN customer on reservation.cid = customer.cid JOIN reservation_detail rd on reservation.reservation_number = rd.reservation_number JOIN airline a on customer.frequent_miles = a.airline_abbreviation\n" +
							" GROUP BY reservation.cid, cost, first_name, last_name, a.airline_name\n" +
							" ORDER BY a.airline_name ASC;";

					sql2 = "SELECT DISTINCT first_name, last_name, leg, a.airline_name\n" +
							" FROM reservation JOIN customer on reservation.cid = customer.cid JOIN reservation_detail rd on reservation.reservation_number = rd.reservation_number JOIN airline a on customer.frequent_miles = a.airline_abbreviation\n" +
							" WHERE airline_name = ?\n" +
							" ORDER BY leg DESC\n" +
							" LIMIT ?;";


					try {
						System.out.println();
						ptmt = dbcon.prepareStatement(sql);
						rs = ptmt.executeQuery();
						while(rs.next()){
							airline_name = rs.getString("airline_name");
							try{
								ptmt = dbcon.prepareStatement(sql2);
								ptmt.setInt(2,choice);
								ptmt.setString(1,airline_name);
								rs2 = ptmt.executeQuery();
								while(rs2.next()){
									System.out.println(rs2.getString("first_name") + " " + rs2.getString("last_name") + "  Legs:" + rs2.getInt("leg") + "  " + rs2.getString("airline_name"));
								}
							} catch (Exception e) {
								System.out.println("Getting customers failed: " + e.toString());
							}
						}
					} catch (Exception e){
						System.out.println("Getting airline failed: " + e.toString());
					}
					break;
				case 13:
					//Rank the airlines based on customer satisfaction
					sql = "SELECT DISTINCT T3.airline_name AS airline_name, COUNT(T3.cid) AS count " 
						+ "FROM(((RESERVATION R JOIN RESERVATION_DETAIL RD ON R.reservation_number = RD.reservation_number) T1 JOIN FLIGHT F ON T1.flight_number = F.flight_number) T2 JOIN AIRLINE A on T2.airline_id = A.airline_id) T3 " 
						+ "GROUP BY T3.airline_name " 
						+ "ORDER BY count DESC";
					try {
						ptmt = dbcon.prepareStatement(sql);
						rs = ptmt.executeQuery();
						System.out.println("\n airline companies rank: ");
						while(rs.next()) {
							System.out.println(rs.getString("airline_name"));
						}
					}catch(SQLException e) {
						System.out.println("select fail");
						System.out.println(e.getMessage());
					}
					break;
				case 14:
					return;	
				default:
					System.out.println("\n\nNot a valid action");
		}
	}
}

	
	public static boolean isDate(String s){
		if(s.length()!=10)
			return false;
		if(s.charAt(0)<'0'||s.charAt(0)>'9')
			return false;
		if(s.charAt(1)<'0'||s.charAt(1)>'9')
			return false;
		if(s.charAt(2)<'0'||s.charAt(2)>'9')
			return false;
		if(s.charAt(3)<'0'||s.charAt(3)>'9')
			return false;
		if(s.charAt(4)!='-')
			return false;
		if(s.charAt(5)<'0'||s.charAt(5)>'1')
			return false;
		if(s.charAt(6)<'0'||s.charAt(6)>'9')
			return false;
		if(s.charAt(7)!='-')
			return false;
		if(s.charAt(8)<'0'||s.charAt(8)>'3')
			return false;
		if(s.charAt(9)<'0'||s.charAt(9)>'9')
			return false;
		
		return true;
	}
	
	public static boolean isTime(String s){
		if(s.length()!=5)
			return false;
		if(s.charAt(0)<'0'||s.charAt(0)>'2')
			return false;
		if(s.charAt(1)<'0'||s.charAt(1)>'9')
			return false;
		if(s.charAt(2)!=':')
			return false;
		if(s.charAt(3)<'0'||s.charAt(3)>'5')
			return false;
		if(s.charAt(4)<'0'||s.charAt(4)>'9')
			return false;
		return true;
	}
}
