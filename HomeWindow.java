import java.awt.BorderLayout;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import java.awt.Button;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.awt.TextArea;
import java.awt.ScrollPane;
import javax.swing.*;
import java.util.Scanner;

public class HomeWindow extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) throws SQLException {
		//JDBC connector

		String url = "jdbc:mysql://localhost:3306/SwimmerDB?autoReconnect=true&useSSL=false";
		String username = "user";
		String password = "password";

		
		Connection connect = DriverManager.getConnection(url, username, password);

		
		//Call the GUI
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeWindow frame = new HomeWindow(connect);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	//Frame Creation
	private static JButton button = new JButton("Search");
	private static JTextField toolbar = new JTextField();
	private JTextArea textArea = new JTextArea();

	
	public HomeWindow(Connection connect) throws SQLException{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		toolbar.setBounds(6, 6, 309, 26);
		contentPane.add(toolbar);
		toolbar.setColumns(10);
		
		textArea.setLineWrap(true);
		textArea.setText("Hello, welcome to the Swimmer Database GUI.\n\nType 'help' for a list of commands to get started.");
		textArea.setEditable(false);
		textArea.setBackground(new Color(238,238,238));
		textArea.setBounds(6, 44, 438, 227);
		contentPane.add(textArea);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(6, 34, 438, 12);
		contentPane.add(separator);
		
		button.setBounds(327, 6, 117, 29);
		contentPane.add(button);
		//Allows scrolling when the output is too long

		JScrollPane scrollPane = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ); //textArea is the child.
		scrollPane.setViewportBorder(null);
		scrollPane.setBounds(6, 44, 438, 227);	//The same as the text field
		scrollPane.setBorder(BorderFactory.createEmptyBorder());	//Gets rid of that dumb border. Keeps the flush look.*/
		contentPane.add(scrollPane);	
		
		Statement stmt = connect.createStatement();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = toolbar.getText();
				try {
					ArrayList<String> result = Parse(input.toLowerCase());
					if(result.get(0).equals("marker"))
					{switch(result.get(2)){
					case "swimmer":
						String specialAllQuery = "SELECT SwimmerId, FirstName, LastName FROM Swimmer";
						ResultSet results = stmt.executeQuery(specialAllQuery);
						String output="SwimmerId: Last Name, First Name\n\n";
						while(results.next()){
							output = output + results.getInt("SwimmerId") + ": " + results.getString("LastName") + ", " + results.getString("FirstName") + "\n";
						}
						textArea.setText(output);
						result.remove(0);
						break;
					case "meet":
						specialAllQuery = "SELECT SanctionNumber, Name, DateHeld FROM Meet";
						results = stmt.executeQuery(specialAllQuery);
						output="SanctionNumber: Name, Date Held\n\n";
						while(results.next()){
							output = output + results.getInt("SanctionNumber") + ": " + results.getString("Name") + ", " + results.getDate("DateHeld") + "\n";
						}
						textArea.setText(output);
						result.remove(0);
						break;
					case "team":
						specialAllQuery = "SELECT TeamId, HeadCoach, TeamName FROM Team";
						results = stmt.executeQuery(specialAllQuery);
						output="TeamId: Head Coach's Name, Team Name\n\n";
						while(results.next()){
							output = output + results.getInt("TeamId") + ": " + results.getString("HeadCoach") + ", " + results.getString("TeamName") + "\n";
						}
						textArea.setText(output);
						result.remove(0);
						break;
					case "result":
						specialAllQuery = "SELECT * FROM Records";
						results = stmt.executeQuery(specialAllQuery);
						output="EventId, SwimmerId, Time, SanctionNumber\n\n";
						while(results.next()){
							output = output + results.getInt("EventId") + ", " + results.getString("SwimmerId") + ", " + results.getString("Time") + ", " + results.getString("SanctionNumber") + "\n";
						}
						textArea.setText(output);
						result.remove(0);
						break;
					}}
					textArea.setText(Analysis(result,connect));
				} catch (SQLException | ParseException e1) {
					e1.printStackTrace();
				}
			}
		});

	}
	public static ArrayList<String> Parse(String input){
		String[] words=input.split("\\s");
		String[] keywords = {"help","all","add", "record", "swimmer", "team","meet","result", "delete", "fastest", "slowest", "male", "males", "boys", "men", "female", "females", "girls", "women", "freestyle", "free", "butterfly", "fly", "breaststroke", "breast", "backstroke", "back", "50", "100","200","400","800","1500",
				"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
				"nineteen", "twenty"};
		ArrayList<String> residue = new ArrayList<String>();

		for(String w: words)
		{switch(w){
		case "delete":
			residue.add("marker");
			break;
		}}
		
		for(String w:words){  
			for(String k:keywords){
				if(w.equals(k))
				{residue.add(w);}
			}
		}
		

		return residue;
	}
	
	
	public String Analysis(ArrayList<String> result, Connection connect) throws SQLException, ParseException {
		Statement stmt = connect.createStatement();
		String output ="";
		String query = "";
		String error = "Sorry, we did not understand your query.\n\nType 'help' for a list of commands to get started.";
		
		
		
		boolean found = false;
		boolean dataModifications = false;
		boolean where = false;
		boolean groupBy = false;
		boolean having = false;
		boolean orderBy = false;
		boolean limit = false;
		boolean needed = false;
		
		String baseSelect = "SELECT FirstName, LastName";
		String baseFrom = "FROM Swimmer";
		String baseFrom1 = "FROM (SELECT SwimmerId, FirstName, LastName";
		String baseFrom2 = "FROM Swimmer ) AS SwimmerSub NATURAL JOIN Records";
		String baseWhere = "WHERE";
		String baseGroupBy = "GROUP BY";
		String baseHaving = "HAVING";
		String baseOrderBy = "ORDER BY";
		String baseLimit = "LIMIT";
		int count=0;
		for(String r:result){
			switch(r){
			case "help":
				output = "Please keep quanities of swimmers in word form: 3 -> three.  \n words \nwords   \nRace distances in number form: 100 butterfly, 1500 freestyle\n\nHere is a list of acceptable commands:\nAdd\n   Ex: Add a team, add a swimmer, add a record\nDelete\n   Ex: Delete a team, Delete a swimmer, delete a record\nModify\nFastest\n   Ex: Fastest three 100 fly swimmers\nSlowest\n   Ex: Slowest male swimmers";
				dataModifications=true;
				break;
			case "all":
				found=true;
				break;
			case "add":
				add(connect, result.get(count+1));
				dataModifications = true;
				output = result.get(count+1) + " added.";
				break;
			case "delete":
				delete(connect, result.get(count+1));
				output = result.get(count+1) + " deleted.";
				dataModifications = true;
				break;
			case "fastest":
				found=true;
				needed=true;
				if(orderBy==false){
				orderBy= true;
				baseOrderBy = baseOrderBy + " Records.Time ASC";
				}
				break;
			case "slowest":
				found=true;
				needed=true;
				if(orderBy==false){
					orderBy= true;
					baseOrderBy = baseOrderBy + " Records.Time DESC";
				}
				break;
			case"female":
			case"females": 
			case "girls":
			case "women":
				found=true;
				where = true;
				baseSelect = baseSelect + ", Gender";
				baseWhere = baseWhere + " Gender='F'";
				baseFrom1 = baseFrom1 + ", Gender";
				break;
			case"male":
			case"males": 
			case "boys":
			case "men":
				found=true;
				where = true;
				baseWhere = baseWhere + " Gender='M'";
				baseFrom1 = baseFrom1 + ", Gender";
				baseSelect = baseSelect + ", Gender";
				break;
			}
		count++;
		}
		if(found){
			if(needed) query = baseSelect + " " + baseFrom1 + " " + baseFrom2;
			else query = baseSelect + " " + baseFrom;
			if(where) query = query + " " + baseWhere;
			if(groupBy) query = query + " " + baseGroupBy;
			if(having) query = query + " " + baseHaving;
			if(orderBy) query = query + " " + baseOrderBy;
			if(limit) query = query + " " + baseLimit;
				
			
			ResultSet results = stmt.executeQuery(query);
			while(results.next()){
				output = output +results.getString("FirstName") + " " + results.getString("LastName") + "\n";
			}
		} else if (dataModifications){} else {output=error;}
		
		return output;
	}
	
	private void add(Connection connect, String type) throws SQLException, ParseException{
		String str="";
		switch(type){
		case "swimmer":
			//Parse("specialAllS", connect);
			String updateSwimmerStr = "INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`, `TeamId`,`EventId`) VALUES (?,?,?,?,?,?,?,?)";
			PreparedStatement update = connect.prepareStatement(updateSwimmerStr);
			String input = null;
			while(input==null)
			{input = JOptionPane.showInputDialog("*FirstName:");}
			str = str + input + ", ";
			update.setString(1, input);
			input = JOptionPane.showInputDialog("MiddleName:");
			str = str + input + ", ";
			update.setString(2, input);
			input=null;
			while(input==null)
			{input = JOptionPane.showInputDialog("*LastName:");}
			str = str + input + ", ";
			update.setString(3, input);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			input=null;
			while(input==null)
			{input = JOptionPane.showInputDialog("*BirthDate (Year/Month/Day):");}
		    java.util.Date utilDate = null;
		    utilDate = sdf.parse(input);
		    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			update.setDate(4, sqlDate);
			String todayDateStr = "2017/3/27";
			String[] date1=input.split("/");
			String[] date2=todayDateStr.split("/");
			str = str + input + ", ";
			int age = Integer.parseInt(date2[0]) - Integer.parseInt(date1[0]);
			update.setInt(5, age);
			input=null;
			while(input==null)
			{input = JOptionPane.showInputDialog("*Gender(M/F):");}
			str = str + input + ", ";
			update.setString(6, input);
			input=null;
			while(input==null)
			{input = JOptionPane.showInputDialog("*TeamId:");}
			str = str + input + ", ";
			update.setString(7, input);
			input=null;
			while(input==null)
			{input = JOptionPane.showInputDialog("*EventId:");}
			str = str + input;
			update.setString(8, input);
			update.executeUpdate();
			//JOptionPane.showMessageDialog(null, str);
			break;
		case "record":
			String updateRecordStr = "INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES ('10','9', '50','1')";
			PreparedStatement updateRec = connect.prepareStatement(updateRecordStr);
			String inputRec = JOptionPane.showInputDialog("*FirstName:");
			updateRec.setString(1, inputRec);
			break;
		case "team":
			String updateTeamStr = "INSERT INTO `Team` (`HeadCoach`,`TeamName`,`PoolStreet`,`PoolNumber`,`PoolCity`,`PoolState`,`PoolCountry`) VALUES (?, ?, ?, ?, ?, ?,?)";
			PreparedStatement updateTeam = connect.prepareStatement(updateTeamStr);
			
			String inputTeam = JOptionPane.showInputDialog("Head Coach:");
			updateTeam.setString( 1, inputTeam );
			inputTeam=null;
			while(inputTeam==null)
			{inputTeam = JOptionPane.showInputDialog("*Team Name:");}
			updateTeam.setString( 2, inputTeam);
			inputTeam = JOptionPane.showInputDialog("Pool Street:");
			updateTeam.setString( 3, inputTeam);
			inputTeam = JOptionPane.showInputDialog("Pool Number:");
			updateTeam.setInt( 4, Integer.parseInt(inputTeam));
			inputTeam = JOptionPane.showInputDialog("Pool City:");
			updateTeam.setString( 5, inputTeam);
			inputTeam = JOptionPane.showInputDialog("Pool State:");
			updateTeam.setString( 6, inputTeam);
			inputTeam = JOptionPane.showInputDialog("Pool Country:");
			updateTeam.setString( 7, inputTeam);
			updateTeam.executeUpdate();
			break;
		}
	}
	private void delete(Connection connect, String type) throws SQLException{
		switch(type){
		case "swimmer":
			String updateSwimmerStr = "DELETE FROM Swimmer WHERE SwimmerId = ?";
			PreparedStatement update = connect.prepareStatement(updateSwimmerStr);
			update.setString(1, JOptionPane.showInputDialog("*SwimmerId:"));
			update.executeUpdate();
			break;
		case "team":
			String updateTeamStr = "DELETE FROM Team WHERE TeamId = ?";
			PreparedStatement updateTeam = connect.prepareStatement(updateTeamStr);
			updateTeam.setInt(1, Integer.parseInt(JOptionPane.showInputDialog("*TeamId:")));
			updateTeam.executeUpdate();
			break;
		case "record":
			String updateRecordStr = "DELETE FROM Records WHERE (EventId = ? AND SwimmerId = ? AND SanctionNumber =?) ";
			PreparedStatement updateRecords = connect.prepareStatement(updateRecordStr);
			updateRecords.setInt(1, Integer.parseInt(JOptionPane.showInputDialog("*TeamId:")));
			updateRecords.setInt(2, Integer.parseInt(JOptionPane.showInputDialog("*SwimmerId:")));
			updateRecords.setInt(3, Integer.parseInt(JOptionPane.showInputDialog("*SanctionNumber:")));
			updateRecords.executeUpdate();
			break;
		case "meet":
			String updateMeetStr = "DELETE FROM Records WHERE SanctionNumber=? ";
			PreparedStatement updateMeet = connect.prepareStatement(updateMeetStr);
			updateMeet.setInt(1, Integer.parseInt(JOptionPane.showInputDialog("*SanctionNumber:")));
			updateMeet.executeUpdate();
			break;	
		}
	}
	
}