import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;


public class LibraryCatalogue extends JFrame implements ActionListener{
	
	//Declare all components of the program
	static int numItems, numUsers, numAdmins;
	int row,col, num;
	int count;
	static String catalogue[][] = new String[200][9];
	static String toPrintArray[][] = new String[200][9];
	static String users[][] = new String[50][14];
	static String admins[][] = new String[5][14];
	static String checkedOutItems[] = new String[10];
	static String genres[] ={"Select One", "Action", "Alternative", "Childrens", "Comedy", "Dance", "Documentary", "Drama", 
							"Electronic", "Family", "Fantasy", "Fiction", "Graphic", "History", "Humor", "Literature", "Metal", "Musical", 
							"Mystery", "Nonfiction", "Pop", "Rap", "Rock", "Romance", "Science Fiction", "Thriller", "Tragedy", "Travel"};
		
	static String logInUsername, logInPassword, loggedInAs;
	Container contentPane;
	JLabel L1, L2, L3, L4;
	JPanel libraryPane, bottomPane;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem;
	JButton logIn, signOutButton, returnButton;
	JTextField usernameField, passwordField, fnameField, lnameField, newUserField;
	JTextField newPassField1, newPassField2, adminUser, adminPass, searchField;
	JTable table;
	JComboBox genreList;
	boolean logInStatus = false;
	static boolean adminLoggedIn = false;
	static boolean loadedItems = false;
	static boolean loadedAccounts = false;
	boolean browsingEntireCatalogue;
	static JFrame window;
	
	//Main method, opens window
	public static void main(String[] args) {
    	window = new LibraryCatalogue();
		window.setTitle("ICS4UZ Library Catalogue");
		window.setSize(500,500);
		window.setVisible(true);
		window.setLocation(5,5);
		
		//Set the default operation when closing program to hide the window
		//After hidden call the saveOnSignout method to save all users, admins and the entire catalogue
		window.setDefaultCloseOperation(HIDE_ON_CLOSE);
		window.addComponentListener(new ComponentAdapter(){
			
			@Override
			public void componentHidden(ComponentEvent f){
				
				try{
					saveOnSignout();
				}catch(IOException ioerror){}
				
				((JFrame)(f.getComponent())).dispose();
			}
			
			
		});
		
    }
	
	public LibraryCatalogue(){
			
		//Initiate the menu bar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//Initiate Menus and Menu options
		//File menu and file menu options
		menu = new JMenu("File");
		menuBar.add(menu);
		menuItem = new JMenuItem("Main Menu");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Log In");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("My Account");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Log Out");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Register");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Quit");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		//Search menu and search menu options
		menu = new JMenu("Search");
		menuBar.add(menu);
		menuItem = new JMenuItem("Entire Catalogue");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		
		//Browse menu and browse menu options
		menu = new JMenu("Browse");
		menuBar.add (menu);
		menuItem = new JMenuItem("Browse Entire Catalogue");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Browse by Genre");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Browse by Format");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
				
	}

	//Action performed method for redirecting button presses to proper methods
	public void actionPerformed(ActionEvent e) {
		String event =e.getActionCommand();
		
		//Conditions for checking whether user clicked menu or window buttons.
				
		//If user selects log in option, check if anyone is already logged in, 
		//then either give an error message or take them to the log in screen
		if((event.equals("Log In")) || (event.equals("                                LOG IN                                 "))){
			if(logInStatus==true){
				JOptionPane.showMessageDialog(libraryPane, "Signed in as "+loggedInAs+".\nLog out before trying to log in again.","Login Error",2);
			}else{
				logIn();
			}
			
		//If main menu is selected, go to the main menu method
		}else if(event.equals("Main Menu")){
			mainMenu();
		
		//If user chooses to see their account,check if they are logged in. If 
		//they are, go the the myAccount method, if not, display error message
		}else if(event.equals("My Account") || event.equals("                          MY ACCOUNT                          ")){
			if(logInStatus==false){
				//Display error if nobody is logged in
				JOptionPane.showMessageDialog(libraryPane, "Nobody is currently logged in.", "Logout Error",2);
			}else{
				myAccount();
			}
		
		//If user chooses to log out, go to logOut method
		}else if(event.equals("Log Out") || event.equals("                              LOG OUT                              ")){
			logOut();
		
		//If user chooses to register, bring up the registration screen but only if nobody is logged in
		}else if(event.equals("Register") || event.equals("                              REGISTER                             ")){
			if(logInStatus==true){
				JOptionPane.showMessageDialog(libraryPane, "Signed in as "+loggedInAs+".\nLog out before trying to register.","Registration Error", 2);
			}else{
				register();
			}
		
		//If user chooses quit, save all the data to the appropriate files and then exit the program
		}else if(event.equals("Quit") || event.equals("                                   QUIT                                  ")){
			try{
				saveOnSignout();
			}catch(IOException ioerror){}
			System.exit(1);
		
		//If user chosses to search the catalogue, go to the search method
		}else if(event.equals("Entire Catalogue") || event.equals("                               SEARCH                               ")){
			search();
		
		//if user chooses to browse the entire catalogue, load the catalogue if needed and then user the printData method to print the catalogue
		}else if(event.equals("Browse Entire Catalogue") || event.equals("                                 ENTIRE CATALOGUE                                 ")){
			try{
				if(loadedItems==false){
					loadCatalogue();
				}
			}catch(IOException ioerror){}
			printData(catalogue,false, "Catalogue");
		
		//If user chooses to browse by format, go to the format selection screen
		}else if(event.equals("Browse by Format") || event.equals("                                         BY FORMAT                                         ")){
			browseByFormat();
		
		//If user chooses to browse by genre, go to the genre selection screen
		}else if(event.equals("Browse by Genre") || event.equals("                                          BY GENRE                                            ")){
			browseByGenre();
			//Add an item listener to the drop down menu on the screen
			genreList.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent ie){
					//Wait until user selects a genre
					if((ie.getStateChange()==ItemEvent.SELECTED)){
						//store the selection in a string variable
						String genreChoice = (String)genreList.getSelectedItem();
						//use the search method to search for the selected genre, a table 
						//then prints out with all items containing the selected genre 
						search(genreChoice, false, "Genre");						
					}
				}
			});
			
			
		}
		//Log in screen SIGN IN button, call the logInButton method to log user in when pressed
		else if(event.equals("SIGN IN")){
			logInButton();
			
		}
		//Button for confirming the registration of a new administrator. Calls the confirmRegistration method
		else if(event.equals("CONFIRM REGISTRATION")){
			try{
			confirmRegistration();
			}catch(IOException ioerror){
				System.out.println("Input/Output Error - Program Crashed." + ioerror);
				
			}
		//Button for registering as a user
		}else if(event.equals("Register as user")){
			try{
				userRegisterButton();
			}
			catch(IOException ioerror){
				System.out.println("Input/Output Error - Program Crashed "+ioerror);
			}
		
		//Button for registering as an administrator
		}else if(event.equals("Register as admin")){
			try{
				adminRegisterButton();
			}
			catch(IOException ioerror){
				System.out.println("Input/Output Error - Program Crashed "+ioerror);
			}
		
		//Search button on the search screen, calls the search method to look for the entered term
		}else if(event.equals("Search")){
			search(searchField.getText(),true,null);
		
		//Button for checing out a selected item
		}else if(event.equals("Check Out Item")){
			checkOutButton(row, col, browsingEntireCatalogue);
			
		//Button for returning an item
		}else if(event.equals("Return Item")){
			returnButton(row, col, browsingEntireCatalogue);
		
		//if user presses the browse button on the main menu, call the browse method to display browsing options. 
		}else if(event.equals("                              BROWSE                              ")){
			browse();
		
		//If user chooses to browse by book, use the search method to search for all books
		}else if(event.equals("                                               BOOK                                               ")){
			search("Book", false, "Format");
		
		//If user chooses to browse by audiobook, use the search method to search for all audiobooks		
		}else if(event.equals("                                         AUDIOBOOK                                         ")){
			search("Audiobook",false, "Format");
		
		//If user chooses to browse by graphic novels, use the search method to search for all graphic novels
		}else if(event.equals("                                              COMIC                                               ")){
			search("Graphic", false, "Format");
		
		//If user chooses to browse by CD, use the search method to search for all CD's
		}else if(event.equals("                                                  CD                                                   ")){
			search("CD", false, "Format");
		
		//If user chooses to browse by DVD, use the search method to search for all DVD's	
		}else if(event.equals("                                                 DVD                                                  ")){
			search("DVD", false, "Format");
		
		//If user chooses to see the items checked out by them, call the items out method
		}else if(event.equals("                           ITEMS OUT                             ")){
			itemsOut();
			
		}
		
	}

	//Method for adding new labels to screen
    public void addALabel(String text, JPanel pane, boolean format){
    	JLabel label = new JLabel(text);
	    if(format==true){
	    	label.setFont (new Font ("Arial", Font.BOLD, 24));
	    	pane.add (label, BorderLayout.NORTH);
    	}
    	pane.add(label);
    }
    
    //Method for adding new buttons to the screen
    public void addAButton(String text, JPanel pane, boolean align){
    	JButton button = new JButton(text);
    	button.addActionListener(this);
    	if(align == true){
    		button.setAlignmentX(Component.CENTER_ALIGNMENT);
    	}
    	pane.add(button);
    }
    
    //Main menu method
    public void mainMenu(){
    	//Set window size and clear the contentPane
    	setWindowSize(500,500);
    	contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
    	
		//Add a header and all main menu buttons
    	addALabel("                              Main Menu                              ", libraryPane, true);
    	addAButton("                                LOG IN                                 ", libraryPane, true);
    	addAButton("                          MY ACCOUNT                          ", libraryPane, true);
    	addAButton("                              LOG OUT                              ", libraryPane, true);
    	addAButton("                              REGISTER                             ", libraryPane, true);
    	addAButton("                               SEARCH                               ", libraryPane, true);
    	addAButton("                              BROWSE                              ", libraryPane, true);
    	addAButton("                                   QUIT                                  ", libraryPane, true);
    	
    	contentPane.add(libraryPane);
    	validate();
    	
    	
    	
    }
    
    //Log in screen method
    public void logIn(){
    	//Load all accounts if they have not already been loaded
		try{
			loadAccounts();
		}
		catch(IOException ioerror){}
		
		setWindowSize(500,500);
		
		//Clear window
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		
		//Create header
		addALabel("                              Log In                              ", libraryPane, true);
		//Add a username label and a text field to enter it in
		addALabel("Username:", libraryPane, false);
		usernameField = new JTextField(35);
		libraryPane.add(usernameField);
		//Add a password label and a password field to enter it in 
		addALabel("Password: ", libraryPane, false);
		passwordField = new JPasswordField(35);
		libraryPane.add(passwordField);
		//Create sign in button
		addAButton("SIGN IN", libraryPane, true);
		
		//add everything to window
		contentPane.add(libraryPane);
		validate();
	}
	
	//Log in button method
    public void logInButton(){
		
    	//Store the entered username and password
		logInUsername = usernameField.getText();
		logInPassword = passwordField.getText();
				
		//Check the admins database
		for(int x=0;x<numAdmins;x++){
			//Check if username exists
			if(logInUsername.equals(admins[x][0])){
				//If username is found, check if password is correct
				if(logInPassword.equals(admins[x][1])){
					//Log in the administrator
					loggedInAs = admins[x][2];
					logInStatus = true;
					adminLoggedIn = true;
					//load all of user's checked out items 
					for(int a=0;a<10;a++){
						checkedOutItems[a] = admins[x][a+4];
					}
					//display confirmation message
					JOptionPane.showMessageDialog(libraryPane, "Logged in as ADMIN: "+loggedInAs, "Login Confirmed",1);
					//Return to main menu
					mainMenu();
					
				}		
			}
		}
		
		//Only check the user database if the admin log in check gave no results
		if(logInStatus == false){
			//Check the user database for the entered username
			for(int x=0;x<numUsers;x++){
				//Check if username exists
				if(logInUsername.equals(users[x][0])){
					//If username is found, check if password is correct
					if(logInPassword.equals(users[x][1])){
						//Log user in
						loggedInAs = users[x][2];
						logInStatus = true;
						//load all of user's checked out items
						for(int a=0;a<10;a++){
							checkedOutItems[a] = users[x][a+4];
						}
						//display a confirmation message
						JOptionPane.showMessageDialog(libraryPane, "Logged in as USER: "+loggedInAs, "Login Confirmed",1);
						
						//Return to main menu
						mainMenu();
						
					}		
				}
			}
		}
		
		//If username or password didn't match, display error
		if(logInStatus==false){
			JOptionPane.showMessageDialog(libraryPane, "Invalid username or password","Log In Error",2);
			passwordField.setText("");
		}else{
			usernameField.setText("");
			passwordField.setText("");
		}
	}
	
	//My account method
    public void myAccount(){
		setWindowSize(500,500);
		//clear window
    	contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
    	
		//Add a header and buttons
    	addALabel("                              My Account                              ", libraryPane, true);
    	addAButton("                           ITEMS OUT                             ", libraryPane, true);
    	addAButton("                              LOG OUT                              ", libraryPane, true);    	
    	
    	//add everything to the window
    	contentPane.add(libraryPane);
    	validate();
		
		
	}
	
	//method to display all items currently checked out by user
    public void itemsOut(){
		//Check if the catalogue needs to be loaded, if it does, call the loadItems to load it
		if(loadedItems==false){
			try{
				loadCatalogue();
			}catch(IOException ioerror){}
		}

		int num=0;
		//Check if the toPrintArray needs to emptied, empty if needed.
		if((toPrintArray[0][0]==(null))==false){
			for(int a=0;a<100;a++){
				for(int b=0;b<9;b++){
					toPrintArray[a][b] = null;				
				}
			}
		}
		//Find the number of items out if a user is logged in
		if(adminLoggedIn==false){
			for(int x=0;x<numUsers;x++){
				if(loggedInAs.equalsIgnoreCase(users[x][2])){
					num = Integer.valueOf(users[x][3]).intValue();
					break;
				}
			}
		//Find the number of items out if an admin is logged in
		}else{
			for(int x=0;x<numAdmins;x++){
				if(loggedInAs.equalsIgnoreCase(admins[x][2])){
					num = Integer.valueOf(admins[x][3]).intValue();
					break;
				}
			}
			
		}
		//For loop goes through all items that user or admin has out 
		for(int y=0;y<num;y++){
			//For loop checks and transfers all data on checked out item into toPrintArray for printing
			for(int h=0;h<numItems;h++){
				if(checkedOutItems[y].equalsIgnoreCase(catalogue[h][0])){
					for(int g=0;g<9;g++){
						toPrintArray[y][g] = catalogue[h][g];
					}
					break;
				}
			}
		}
		//call the printData method to print the items the user has out
		printData(toPrintArray,false,"Account");
	}
	
	//method for logging the user out
    public void logOut(){
		//Checks if anyone is logged in
		if(logInStatus==true){
			if(adminLoggedIn==true){
				JOptionPane.showMessageDialog(libraryPane, "Logged out from ADMIN: "+loggedInAs, "Logout Confirmed",1);
				passwordField.setText("");
			}else{
				JOptionPane.showMessageDialog(libraryPane, "Logged out from USER: "+loggedInAs, "Logout Confirmed",1);
			}
			//Logs out
			logInStatus = false;
			loggedInAs = null;
			if(adminLoggedIn == true){
				adminLoggedIn = false;
			}
			for(int x=0;x<10;x++){
				checkedOutItems[x] = " ";
			}
			
		}else{
			//Display error if nobody is logged in
			JOptionPane.showMessageDialog(libraryPane, "Nobody is currently logged in.", "Logout Error",2);
		}
		
	}

	//Method for registering a new user or administrator
    public void register(){
		//Check if all accounts need to be loaded, if yes, call the loadAccounts method to load them
		try{
			loadAccounts();
		}
		catch(IOException ioerror){
			System.out.println("Input/Output Error - Program Crashed "+ioerror);
		}
		//Clear window
		setWindowSize(500,500);
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		
		
		//Add a label for first name, last name, username, password, and confirm password
		//Add a textfield or password field for each label
		addALabel("       First Name:        ", libraryPane, false);
		fnameField = new JTextField(32);
		libraryPane.add(fnameField);
		addALabel("       Last Name:        ", libraryPane, false);
		lnameField = new JTextField(32);
		libraryPane.add(lnameField);
		addALabel("        Username:        ", libraryPane, false);
		newUserField = new JTextField(32);
		libraryPane.add(newUserField);
		addALabel("       Password:         ", libraryPane, false);
		newPassField1 = new JPasswordField(32);
		libraryPane.add(newPassField1);
		addALabel("Confirm Password:", libraryPane, false);
		newPassField2 = new JPasswordField(32);
		libraryPane.add(newPassField2);
		
		//Add 2 buttons for registering
		addAButton("Register as user", libraryPane, true);
		addAButton("Register as admin", libraryPane, true);
		
		//Add everything to the window
		contentPane.add(libraryPane);
		validate();
		
		
	}
	
	//Method to register a user into the database
    public void userRegisterButton()throws IOException{
		
		boolean duplicateUsername = false;
		//Go through all admin usernames and set duplicateUsername 
		//boolean to true if the chosen username is found
		if(duplicateUsername==false){
			for(int x=0;x<numAdmins;x++){
				if((newUserField.getText()).equals(admins[x][0])){
					duplicateUsername = true;
					break;
				}
			}
		}
		//Go through all user usernames and set duplicateUsername 
		//boolean to true if the chosen username is found
		if(duplicateUsername==false){
			for(int x=0;x<numUsers;x++){
				if((newUserField.getText()).equals(users[x][0])){
					duplicateUsername = true;
					break;
				}	
			}
		}
		
		//Conditional to check if any of the text fields are empty, display error if one or more are.
		if((fnameField.getText()).equals("")||(lnameField.getText()).equals("")||
		(newUserField.getText()).equals("")|| (newPassField1.getText()).equals("") || (newPassField2.getText()).equals("")){
			
			JOptionPane.showMessageDialog(libraryPane, "You have left a textfield blank", "Restration Error", 2);
		
		//Conditional to check if the chosen username already exists, displays error
		}else if(duplicateUsername == true){
			
			JOptionPane.showMessageDialog(libraryPane, "The chosen username already exists.\nPlease choose another.", "Restration Error", 2);
			newUserField.setText("");
		
		//Checks if passwords match, displays error
		}else if(((newPassField1.getText()).equals(newPassField2.getText()))==false){
			JOptionPane.showMessageDialog(libraryPane, "Passwords do not match");
			newPassField1.setText("");
			newPassField2.setText("");
		
		//If no errors, save existing user data and then the new user data
		}else{
			numUsers++;
			String firstName, lastName;
			
			//Capitalize first and last names of new user
			firstName = ((fnameField.getText()).substring(0,1)).toUpperCase() + ((fnameField.getText()).substring(1));
			lastName = ((lnameField.getText()).substring(0,1)).toUpperCase() + ((lnameField.getText()).substring(1));
			
			PrintWriter fileOutput;
			fileOutput = new PrintWriter(new FileWriter("users.txt"));
			
			//Save number of users
			fileOutput.println(numUsers);
			//Loop to save all existing users
			for(int x=0;x<numUsers-1;x++){
				for(int a=0;a<14;a++){
					fileOutput.println(users[x][a]);
				}
			}
			//Save the new user data
			fileOutput.println(newUserField.getText());
			fileOutput.println(newPassField1.getText());
			fileOutput.println(firstName + " " + lastName);
			fileOutput.println("0");
			for(int b=4;b<14;b++){
				fileOutput.println(" ");
			}
			fileOutput.close();	
			//load updated account list
			loadAccounts();
			//Display confirmation message
			JOptionPane.showMessageDialog(libraryPane, "New user "+ firstName + " " + lastName + " has been created", "Registration Successful",1);
			mainMenu();
		}
		
	}
	
	//Method for registering a new administrator
    public void adminRegisterButton()throws IOException{
		
		boolean duplicateUsername = false;
		//Goes through all admin usernames and sets duplicateUsername 
		//boolean to true if the chosen username is found
		if(duplicateUsername==false){
			for(int x=0;x<numAdmins;x++){
				if((newUserField.getText()).equals(admins[x][0])){
					duplicateUsername = true;
					break;
				}
			}
		}
		//Goes through all user usernames and sets duplicateUsername 
		//boolean to true if the chosen username is found
		if(duplicateUsername==false){
			for(int x=0;x<numUsers;x++){
				if((newUserField.getText()).equals(users[x][0])){
					duplicateUsername = true;
					break;
				}	
			}
		}
		
		//Conditional to check if any of the text fields are empty, display error if one or more are.
		if((fnameField.getText()).equals("")||(lnameField.getText()).equals("")||
		(newUserField.getText()).equals("")|| (newPassField1.getText()).equals("") || (newPassField2.getText()).equals("")){
			
			JOptionPane.showMessageDialog(libraryPane, "You have left a textfield blank", "Restration Error", 2);
		
		//Conditional to check if the chosen username already exists, displays error
		}else if(duplicateUsername == true){
			
			JOptionPane.showMessageDialog(libraryPane, "The chosen username already exists.\nPlease choose another.", "Restration Error", 2);
			newUserField.setText("");
		
		//Checks if passwords match, displays error
		}else if(((newPassField1.getText()).equals(newPassField2.getText()))==false){
			JOptionPane.showMessageDialog(libraryPane, "Passwords do not match");
			newPassField1.setText("");
			newPassField2.setText("");
		
		//Display a message telling the user that an administrator must log in to create a new admin account
		}else{
			
			//Display message
			int i = JOptionPane.showConfirmDialog(libraryPane, "To register as an administrator, you must\nhave another administrator enter their credentials.", "Restering as Administrator", JOptionPane.OK_CANCEL_OPTION,1);
			
			//If user presses okay, pull up a window to have an existing admin log in
			if(i == 0){
				//clear window
				contentPane = getContentPane();
				if(libraryPane != null){
					contentPane.remove(libraryPane);
				}
				libraryPane = new JPanel();
				
				//Add a header
				addALabel("                        Admin Confirmation                          ", libraryPane, true);
				//Add a username label and a text field to enter it in
				addALabel("Username:", libraryPane, false);
				adminUser = new JTextField(35);
				libraryPane.add(adminUser);
				//Add a password label and a passwordfield to enter it in 
				addALabel("Password: ", libraryPane, false);
				adminPass = new JPasswordField(35);
				libraryPane.add(adminPass);
				//Create confirmation button
				addAButton("CONFIRM REGISTRATION", libraryPane, true);
				
				//add everything to window
				contentPane.add(libraryPane);
				validate();
				
			}
		}
	}
	
    //Method for confirming the registration of a new admin
	public void confirmRegistration()throws IOException{
		
		
		logInUsername = adminUser.getText();
		logInPassword = adminPass.getText();
				
		//Check the admins database
		for(int x=0;x<numAdmins;x++){
			//Check if username exists
			if(logInUsername.equals(admins[x][0])){
				//If username is found, check if password is correct
				if(logInPassword.equals(admins[x][1])){
					//Log user in
					adminLoggedIn = true;
					
				}		
			}
		}
		//If valid administrator credentials have been entered, add the new admin to the database
		if(adminLoggedIn == true){
			adminLoggedIn = false;
			//Increment the number of admins
			numAdmins++;
			String firstName, lastName;
			
			//Capitalize first and last names
			firstName = ((fnameField.getText()).substring(0,1)).toUpperCase() + ((fnameField.getText()).substring(1));
			lastName = ((lnameField.getText()).substring(0,1)).toUpperCase() + ((lnameField.getText()).substring(1));
			
			PrintWriter fileOutput;
			fileOutput = new PrintWriter(new FileWriter("admins.txt"));
			
			//Save number of admins
			fileOutput.println(numAdmins);
			//Loop to save all existing admins
			for(int x=0;x<numAdmins-1;x++){
				for(int a=0;a<14;a++){
					fileOutput.println(admins[x][a]);
				}
				
			}
			//Save the new account data
			fileOutput.println(newUserField.getText());
			fileOutput.println(newPassField1.getText());
			fileOutput.println(firstName + " " + lastName);
			fileOutput.println("0");
			for(int b=4;b<14;b++){
				fileOutput.println(" ");
			}
			fileOutput.close();			
			loadAccounts();
			//Display confirmation message
			JOptionPane.showMessageDialog(libraryPane, "New admin "+ firstName + " " + lastName + " has been created", "Registration Successful",1);
			
			adminUser.setText("");
			adminPass.setText("");
			mainMenu();
			
		}else{
			//If admin confirmation failed, display an error message
			JOptionPane.showMessageDialog(libraryPane, "The administrator confimation failed.", "Registration Error",1);
			register();
		}	
	}
	
	//Method for the search window
	public void search(){
		//clear window
		setWindowSize(500,500);
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		
		//Add a header
		addALabel("                              Search                              ", libraryPane, true);
		//Add a label and a text field
		addALabel("Enter Search Term:",libraryPane,false);
		searchField = new JTextField(30);
		libraryPane.add(searchField);
		addAButton("Search",libraryPane,false);
		
		//Add everything to window
		contentPane.add(libraryPane);
		validate();
		
		
	}

	//Method for searching the catalogue
	public void search(String search, boolean searchOrNot, String browse){
		
		//Check if catalogue needs to be loaded, if yse, call the loadCatalogue method to load catalogue
		if(loadedItems == false){
			try{
				loadCatalogue();
			}catch(IOException ioerror){
				System.out.println("Input/Output Error - Program Crashed." + ioerror);
			}
		}
		
		//Check if the toPrint array needs to be emptied by checking the first element, empty the array if needed
		if((toPrintArray[0][0]==(null))==false){
			for(int a=0;a<100;a++){
				for(int b=0;b<9;b++){
					toPrintArray[a][b] = null;				
				}
			
			}
		}
		if(searchOrNot == true){
		count=0;
		//First for loop goes through each entry in the catalogue
			for(int x=0;x<numItems;x++){
				//Second for loop goes through the data in each entry and compares it to search term
				for(int y=0;y<8;y++){
					//Conditional checks if the search term is contained with the element at [x][y]
					//Comparison is done in lowercase for better results
					if(((catalogue[x][y]).toLowerCase()).contains(search.toLowerCase())){
						//If search term is found, for loop copies all data from the marked 
						//catalogue entry into separate array that will later be printed 
						for(int t=0;t<9;t++){
							toPrintArray[count][t] = catalogue[x][t];
						}
						count++;
					}
				}	
			}
		}else{
			count=0;
			//First for loop goes through each entry in the catalogue
			for(int x=0;x<numItems;x++){
				//Second for loop goes through the data in each entry and compares it to search term
				for(int y=0;y<8;y++){
					//Conditional checks if the search term is contained with the element at [x][y]
					//Comparison is done in lowercase for better results
					if(((catalogue[x][y]).equalsIgnoreCase(search))){
						//If search term is found, for loop copies all data from the marked 
						//catalogue entry into separate array that will later be printed 
						for(int t=0;t<9;t++){
							toPrintArray[count][t] = catalogue[x][t];
						}
						count++;
					}
				}	
			}	
		}	
		//Call the printData method with proper arguments based on whether the user is searching or browsing
		if(searchOrNot == true){
			printData(toPrintArray,true,null);
		}else{
			printData(toPrintArray, false, browse);
		}
		
		
		
	}
	
	//Method for the browsing options
	public void browse(){
		//clear the window
		setWindowSize(500,500);
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		
		//Create a header and buttons for all browsing options
		addALabel("                              BROWSE                              ", libraryPane, true);
    	addAButton("                                 ENTIRE CATALOGUE                                 ", libraryPane, true);
    	addAButton("                                         BY FORMAT                                         ", libraryPane, true);
    	addAButton("                                          BY GENRE                                            ", libraryPane, true);
		
    	//Add everything to window
		contentPane.add(libraryPane);
		validate();
		
	}

	//Method for the browse-by-format window
	public void browseByFormat(){
		
		//Clear window
		setWindowSize(500,500);
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		//Create a header and buttons for all formats
		addALabel("                            CHOOSE FORMAT                            ", libraryPane, true);
    	addAButton("                                               BOOK                                               ", libraryPane, true);
    	addAButton("                                         AUDIOBOOK                                         ", libraryPane, true);
    	addAButton("                                              COMIC                                               ", libraryPane, true);
    	addAButton("                                                  CD                                                   ", libraryPane, true);
    	addAButton("                                                 DVD                                                  ", libraryPane, true);
		
    	//Add everything to the window
		contentPane.add(libraryPane);
		validate();
	} 
	
	//Method for browsing-by-genre window
	public void browseByGenre(){
		//Clear window
		setWindowSize(500,500);
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		
		//Add a header and spacer
		addALabel("                            CHOOSE GENRE                            ", libraryPane, true);
		addALabel("                                                                                  ",libraryPane,true);
		
		//Create a drop-down menu containing all genres
		genreList = new JComboBox(genres);
		//Set the displayed index to 0 to display the "Select One" option
		genreList.setSelectedIndex(0);
		
		//Add everything to the window
		libraryPane.add(genreList);		
		contentPane.add(libraryPane);
		validate();		
		
	}

	//Method for printing the requested data
	public void printData(String array[][], boolean searchOrNot, String browse){
		
		browsingEntireCatalogue = false;
		
		//Clear window and make it full screen
		window.setExtendedState(Frame.MAXIMIZED_BOTH); 
		contentPane = getContentPane();
		if(libraryPane != null){
			contentPane.remove(libraryPane);
		}
		libraryPane = new JPanel();
		libraryPane.setLayout(new BorderLayout());
		
		//Choose the correct label
		if(searchOrNot == true){
			L1 = new JLabel(count+" search result(s) for: "+searchField.getText(),JLabel.CENTER);	
		}else{
			if(browse.equals("Catalogue")){
				L1 = new JLabel("Browsing All Items",JLabel.CENTER);
				browsingEntireCatalogue = true;
			}else if(browse.equals("Format")){
				L1 = new JLabel("Browsing By Format",JLabel.CENTER);	
			}else if(browse.equals("Genre")){
				L1 = new JLabel("Browsing By Genre",JLabel.CENTER);
			}else if(browse.equals("Account")){
				L1 = new JLabel("Browsing "+loggedInAs+"'s Checked Out Items",JLabel.CENTER);
			}
		}
		
		//Add the label to the window
		L1.setFont(new Font("Arial",Font.BOLD,24));
		libraryPane.add(L1,BorderLayout.NORTH);
		
		//Create new panel at the bottom of window
		bottomPane = new JPanel ();
		bottomPane.setLayout (new BorderLayout ());

		//Add label, check out button and return button to bottom panel
		L2 = new JLabel ("Select Row (Mouse click on row) and choose option:",JLabel.CENTER);
		bottomPane.add (L2, BorderLayout.CENTER);
		signOutButton = new JButton ("Check Out Item");
		signOutButton.addActionListener (this);
		bottomPane.add (signOutButton, BorderLayout.WEST);
		returnButton = new JButton ("Return Item");
		returnButton.addActionListener (this);
		bottomPane.add (returnButton, BorderLayout.EAST);
		
		libraryPane.add (bottomPane, BorderLayout.SOUTH);
		
		//Create headers for table and the table
		String [] columnNames = {"Title", "Creator","Format", "ISBN Number", "Length", "Genre", "Genre", "Publish/Release Date", "Status"};
		table = new JTable(array,columnNames);
		
		table.addMouseListener (new MouseAdapter ()
		{
		    public void mouseClicked (MouseEvent e)
		    {
			row = table.getSelectedRow ();
			col = table.getSelectedColumn ();
			System.out.println(row +" "+ col);
			if ((row == -1) || (col == -1))
			    return;
		    }
		}
		);
		
		JScrollPane scrollPane = new JScrollPane (table);
		libraryPane.add(scrollPane, BorderLayout.CENTER);
		
		//Add everything to window
		contentPane.add(libraryPane);
		validate();		
		
	}
	
	//Method for checking out items from the catalogue
	public void checkOutButton(int row, int col, boolean entireCat){
		
		//If browsing entire catalogue, transfer the catalogue to the toPrintArray
		if(entireCat==true){
			for(int x=0;x<numItems;x++){
				for(int y=0;y<9;y++){
					toPrintArray[x][y] = catalogue[x][y];
				}
			}
		}
		
		//Check if user is logged in, display error if they aren't
		if(logInStatus==false){
			int a = JOptionPane.showConfirmDialog(libraryPane, "You must be signed in to check items out.\nSign in?", "Check Out Error", JOptionPane.YES_NO_OPTION,2);
			//If user agrees to log in, call the logIn method to log the user in
			if(a==0){
				logIn();
			}
			
		}
		
		//Check if the item is already checked out, display error if it is
		else if(toPrintArray[row][8].equals("Checked Out")){
			JOptionPane.showMessageDialog(libraryPane, "This item is already checked out.", "Check Out Error",2);
		
		//If user is logged in and the item is not checked out, check the item out
		}else{
			//Check if an administrator is logged in or not
			if(adminLoggedIn == false){
				//Find the user that is logged in
				for(int x=0;x<numUsers;x++){
					if(loggedInAs.equalsIgnoreCase(users[x][2])){
						//Get the number of books the user has
						num = Integer.valueOf(users[x][3]).intValue();
						//Check if they are not exceeding their checkout limit
						if(num<10){
							//Increment the number of books checked out by user by 1 and save it in the users array
							num++;
							users[x][3] = Integer.toString(num);
							//Save the title of the book to the users array at index 3+number of items taken out
							users[x][3+num] = toPrintArray[row][0];
							checkedOutItems[num-1] = toPrintArray[row][0];
							
							//If browsing entire catalogue, set the status of item at index row to "Checked Out"
							if(entireCat == true){
								catalogue[row][8] = "Checked Out";								
							//If not browsing the entire catalogue, find the checked out item in the catalogue, and set its status to "Chcked out"
							}else{
								//For loop searches through catalogue to find the item
								for(int s=0;s<numItems;s++){
									//If found, set is status to "Checked Out"
									if(toPrintArray[row][0].equalsIgnoreCase(catalogue[s][0])){
										catalogue[s][8] = "Checked Out";
										toPrintArray[row][8] = "Checked Out";
										break;
									}
								}
							}
							//Display confirmation message
							JOptionPane.showMessageDialog(libraryPane, "You have successfully checked out " + toPrintArray[row][0]+".", "Check Out Successful",1);
							
						}else{
							//Display error if too many items are out
							JOptionPane.showMessageDialog(libraryPane, "You have reached your maximum check out limit.\nReturn one of your books to check out more.", "Check Out Error",2);
						}
						break;
					}
				}
			}else{
				//Find the admin that is logged in
				for(int x=0;x<numAdmins;x++){
					if(loggedInAs.equalsIgnoreCase(admins[x][2])){
						//Get the number of books the admin has
						num = Integer.valueOf(admins[x][3]).intValue();
						//Check if they are not exceeding their checkout limit
						if(num<10){
							//Increment the number of books checked out by user by 1 and save it in the admins array
							num++;
							admins[x][3] = Integer.toString(num);
							//Save the title of the book to the admins array at index 3+number of items taken out
							admins[x][3+num] = toPrintArray[row][0];
							checkedOutItems[num-1] = toPrintArray[row][0];
							
							//If browsing entire catalogue, set the status of item at index row to "Checked Out"
							if(entireCat == true){
								catalogue[row][8] = "Checked Out";			
							//If not browsing the entire catalogue, find the checked out item in the catalogue, and set its status to "Chcked out"
							}else{
								//For loop searches through catalogue to find the item
								for(int num=0;num<numItems;num++){
									//If found, set is status to "Checked Out"
									if(toPrintArray[row][0].equalsIgnoreCase(catalogue[num][0])){
										System.out.println("Found");
										catalogue[num][8] = "Checked Out";
										toPrintArray[row][8] = "Checked Out";
										break;
									}
								}
							}
							//Display confirmation message
							JOptionPane.showMessageDialog(libraryPane, "You have checked out " + toPrintArray[row][0]+".", "Check Out Successful",1);
							
						}else{
							//Display error if too many items are out
							JOptionPane.showMessageDialog(libraryPane, "You have reached your maximum check out limit.\nReturn one of your books to check out more.", "Check Out Error",2);
						}
						break;
					}
				}				
			}			
		}
	} 

	//Method for returning items to the catalogue
	public void returnButton(int row, int col, boolean entireCat){
		
		boolean checkedOutItem=false;
		
		//If browsing entire catalogue, transfer the catalogue to the toPrintArray
		if(entireCat==true){
			for(int x=0;x<numItems;x++){
				for(int y=0;y<9;y++){
					toPrintArray[x][y] = catalogue[x][y];
				}
			}
		}
		//Check if user is logged in, display error if they aren't
		if(logInStatus==false){
			
			int a = JOptionPane.showConfirmDialog(libraryPane, "You must be signed in to return items.\nSign in?", "Return Error", JOptionPane.YES_NO_OPTION,2);

			//If user agrees to log in, call the logIn method to log the user in
			if(a==0){
				logIn();
				
			}
		}
		if(logInStatus==true){
			//Check if the item is checked out by the user
			for(int d=0;d<10;d++){
				if(checkedOutItems[d].equalsIgnoreCase(toPrintArray[row][0])){
					checkedOutItem = true;
					break;
				}
			}
			//Display error is item isnt checked out by user
			if(checkedOutItem == false){
				JOptionPane.showMessageDialog(libraryPane, "You have not checked this item out.", "Return Error",2);
			
			}else{
				
				int b = JOptionPane.showConfirmDialog(libraryPane, "Are you sure you want to return "+toPrintArray[row][0]+"?", "Return Confirmation", JOptionPane.YES_NO_OPTION,2);
				//If user confirms the returning of the item, return the item to the catalogue
				if(b==0){
					
					if(adminLoggedIn==false){
					
						//Go through all users
						for(int z=0;z<numUsers;z++){
							//Find the user that is logged in
							if(loggedInAs.equals(users[z][2])){
								//Convert number of items user has out to integer
								int num = Integer.valueOf(users[z][3]).intValue();
								//For loop running 10 times searched for the index of selected item
								for(int k=4;k<14;k++){
									if(toPrintArray[row][0].equalsIgnoreCase(users[z][k])){
										//For loop that shifts all items ahead of selected item back one space
										//Number of iterations depends of index of selected item and number of items taken out
										for(int m=0;m<(num+3-k);m++){
											users[z][m+k] = users[z][m+k+1];
										}
										users[z][num+3] = " ";
										num-=1;
										users[z][3] = Integer.toString(num);
										if(entireCat == true){
											catalogue[row][8] = "Available";								
										}else{
											for(int s=0;s<numItems;s++){
												if(toPrintArray[row][0].equalsIgnoreCase(catalogue[s][0])){
													catalogue[s][8] = "Available";
													toPrintArray[row][8] = "Available";
													JOptionPane.showMessageDialog(libraryPane, "You have successfully returned "+toPrintArray[row][0]+".", "Return Successful",1);
													break;
												}
											}
										}
										break;
									}
								}
								break;
							}
						}
					}else{
						//Go through all admins
						for(int z=0;z<numAdmins;z++){
							//Find the user that is logged in
							if(loggedInAs.equals(admins[z][2])){
								System.out.println(loggedInAs+" "+admins[z][2]);
								//Convert number of items user has out to integer
								int num = Integer.valueOf(admins[z][3]).intValue();
								//For loop running 10 times searched for the index of selected item
								for(int k=4;k<14;k++){
									if(toPrintArray[row][0].equalsIgnoreCase(admins[z][k])){
										//For loop that shifts all items ahead of selected item back one space
										//Number of iterations depends of index of selected item and number of items taken out
										for(int m=0;m<(num+3-k);m++){
											admins[z][m+k] = admins[z][m+k+1];
										}
										admins[z][num+3] = " ";
										num-=1;
										admins[z][3] = Integer.toString(num);
										JOptionPane.showMessageDialog(libraryPane, "You have successfully returned "+toPrintArray[row][0]+".", "Return Successful",1);
										if(entireCat == true){
											catalogue[row][8] = "Available";								
										}else{
											for(int s=0;s<numItems;s++){
												if(toPrintArray[row][0].equalsIgnoreCase(catalogue[s][0])){
													catalogue[s][8] = "Available";
													toPrintArray[row][8] = "Available";
													break;
												}
											}
										}
										break;
									}
								}
								break;
							}
						}						
					}
				}
		
			}
		}
	}

	//Method for loading all accounts
	public void loadAccounts()throws IOException{
		
		
		//Read in all user accounts
		String line;
		BufferedReader fileInput = new BufferedReader(new FileReader("users.txt"));
		line = fileInput.readLine();
		numUsers = Integer.valueOf(line).intValue();
		for(int x=0;x<numUsers;x++){
			for(int y=0;y<14;y++){
				users[x][y] = fileInput.readLine();
			}
		}
		fileInput.close();
		
		//Read in all admin accounts
		BufferedReader fileInputAdmins = new BufferedReader(new FileReader("admins.txt"));
		numAdmins = Integer.valueOf(fileInputAdmins.readLine()).intValue();
		for(int x=0;x<numAdmins;x++){
			for(int y=0;y<14;y++){
				admins[x][y] = fileInputAdmins.readLine();
			}
		}
		fileInputAdmins.close();
		
		loadedAccounts = true;
	}
	
	//Method for loading all the items into the catalogue
	public void loadCatalogue()throws IOException{
		//Read in all items in the catalogue file
			String line;
			BufferedReader fileInput = new BufferedReader(new FileReader("items2.txt"));
			line = fileInput.readLine();
			numItems = Integer.valueOf(line).intValue();
			for(int x=0;x<numItems;x++){
				for(int y=0;y<9;y++){
					catalogue[x][y] = fileInput.readLine();
				}
			}
			fileInput.close();
			loadedItems = true;
			
	}

	//Method for saving all the data on signout or closing
	public static void saveOnSignout()throws IOException{
	
		if(loadedAccounts==true){
			if(adminLoggedIn==false){
				PrintWriter fileOutput = new PrintWriter(new FileWriter("users.txt"));
				fileOutput.println(numUsers);
				for(int x=0;x<numUsers;x++){
					for(int y=0;y<14;y++){
						fileOutput.println(users[x][y]);
					}
				}
				fileOutput.close();
			}else{
				PrintWriter fileOutput = new PrintWriter(new FileWriter("admins.txt"));
				fileOutput.println(numAdmins);
				for(int x=0;x<numAdmins;x++){
					for(int y=0;y<14;y++){
						fileOutput.println(admins[x][y]);
					}
				}
				fileOutput.close();
			}
		}
		
		if(loadedItems == true){
			
			PrintWriter fileOutput = new PrintWriter(new FileWriter("items2.txt"));
			fileOutput.println(numItems);
			for(int x=0;x<numItems;x++){
				for(int y=0;y<9;y++){
					fileOutput.println(catalogue[x][y]);
				}
			}
			fileOutput.close();
			
			
		}
	}

	//Method for setting the window size and location
	public void setWindowSize(int x, int y){
		
		window.setSize(x,y);
		window.setLocation(5,5);
		
	}
}




























