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

		//Connection connect = null;
		//try {
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
	public void fix(){
		textArea.setVisible(true);
	}
	
	public HomeWindow(Connection connect){
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
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = toolbar.getText();
				try {
					textArea.setVisible(false);
					textArea.setText(Parse(input.toLowerCase(),connect));
					
				} catch (SQLException | ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setBounds(327, 6, 117, 29);
		contentPane.add(button);
		//Allows scrolling when the output is too long
		JScrollPane scrollPane = new JScrollPane(textArea); //textArea is the child.
		scrollPane.setViewportBorder(null);
		scrollPane.setBounds(6, 44, 438, 227);	//The same as the text field
		scrollPane.setBorder(BorderFactory.createEmptyBorder());	//Gets rid of that dumb border. Keeps the flush look.
		contentPane.add(scrollPane);	
	}
	
	public String Parse(String input, Connection connect) throws SQLException, ParseException {
		Statement stmt = connect.createStatement();
		JTextArea textArea2 = new JTextArea();
		textArea2.setLineWrap(true);
		textArea2.setText("Hello, welcome to the Swimmer Database GUI.\n\nType 'help' for a list of commands to get started.");
		textArea2.setEditable(false);
		textArea2.setBackground(new Color(238,238,238));
		textArea2.setBounds(6, 44, 438, 227);
		textArea2.setVisible(false);
		
		String output ="";
		String query = "";
		String error = "Sorry, we did not understand your query.\n\nType 'help' for a list of commands to get started.";
		
		String[] words=input.split("\\s");
		//input for help overrides anything else
		boolean help = input.matches(".*\\bhelp\\b.*");
		if(help){
			fix();
			output = "Please keep quanities of swimmers in word form: 3 -> three.\nRace distances in number form: 100 butterfly, 1500 freestyle\n\nHere is a list of acceptable commands:\nAdd\n   Ex: Add a team, add a swimmer, add a record\nDelete\nModify\nFastest\n   Ex: Fastest three 100 fly swimmers\nSlowest\n   Ex: Slowest male swimmers";
			return output;
		}
		String[] keywords = {"specialAll","add", "record", "swimmer", "delete", "fastest", "slowest", "male", "males", "boys", "men", "female", "females", "girls", "women", "freestyle", "free", "butterfly", "fly", "breaststroke", "breast", "backstroke", "back", "50", "100","200","400","800","1500",
				"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
				"nineteen", "twenty"};
		ArrayList<String> residue = new ArrayList<String>();

		for(String w:words){  
			for(String k:keywords){
				if(w.equals(k))
				{residue.add(w);}
			}
		}
		
		boolean found = false;
		boolean dataModifications = false;
		boolean where = false;
		boolean groupBy = false;
		boolean having = false;
		boolean orderBy = false;
		boolean limit = false;
		
		String baseSelect = "SELECT FirstName, LastName";
		String baseFrom1 = "FROM (SELECT SwimmerId, FirstName, LastName";
		String baseFrom2 = "FROM Swimmer ) AS SwimmerSub NATURAL JOIN Records";
		String baseWhere = "WHERE";
		String baseGroupBy = "GROUP BY";
		String baseHaving = "HAVING";
		String baseOrderBy = "ORDER BY";
		String baseLimit = "LIMIT";
		int count=0;
		for(String r:residue){
			switch(r){
			case "specialAll":
				String specialAllQuery = "SELECT SwimmerId, FirstName, LastName FROM Swimmer";
				textArea2.setVisible(true);
				contentPane.add(textArea2);
				dataModifications=true;
				ResultSet results = stmt.executeQuery(specialAllQuery);
				String outputt="SwimmerId: Last Name, First Name\n\n";
				while(results.next()){
					outputt = outputt + results.getInt("SwimmerId") + ": " + results.getString("LastName") + ", " + results.getString("FirstName") + "\n";
				}
				textArea2.setText(outputt);
				break;
			case "add":
				Parse("specialAll", connect);
				//String addStr = add(connect, residue.get(count+1));
				textArea2.setVisible(true);
				contentPane.add(textArea2);
				dataModifications = true;
				break;
			case "delete":
				break;
			case "fastest":
				found=true;
				if(orderBy==false){
				orderBy= true;
				baseOrderBy = baseOrderBy + " Records.Time ASC";
				}
				break;
			case "slowest":
				found=true;
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
				break;
			}
		count++;
		}
		if(found){
			fix();
			query = baseSelect + " " + baseFrom1 + " " + baseFrom2;
			if(where) query = query + " " + baseWhere;
			if(groupBy) query = query + " " + baseGroupBy;
			if(having) query = query + " " + baseHaving;
			if(orderBy) query = query + " " + baseOrderBy;
			if(limit) query = query + " " + baseLimit;
				
			
			ResultSet results = stmt.executeQuery(query);
			while(results.next()){
				output = output +results.getString("FirstName") + " " + results.getString("LastName") + "\n";
			}
		} else if (dataModifications){} else output=error;
		
		return output;
	}
	
	private static String add(Connection connect, String type) throws SQLException, ParseException{
		String str="";
		switch(type){
		case "swimmer":
			String updateSwimmerStr = "INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`, `TeamId`,`EventId`) VALUES ('?','?','?','?',?','?','?','?')";
			PreparedStatement update = connect.prepareStatement(updateSwimmerStr);
			String input = JOptionPane.showInputDialog("*FirstName:");
			update.setString(1, input);
			input = JOptionPane.showInputDialog("MiddleName:");
			update.setString(2, input);
			input = JOptionPane.showInputDialog("*LastName:");
			update.setString(3, input);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			input = JOptionPane.showInputDialog("*BirthDate (Year/Month/Day):");
			Date date = (Date) sdf.parse(input);
			update.setDate(4, date);
			input = JOptionPane.showInputDialog("*Age:");
			update.setString(5, input);
			input = JOptionPane.showInputDialog("*Gender(M/F):");
			update.setString(6, input);
			input = JOptionPane.showInputDialog("*TeamId:"); //SELECT TeamId, TeamName FROM Team
			update.setString(7, input);
			input = JOptionPane.showInputDialog("*EventId:");
			update.setString(8, input);
			update.executeUpdate();
			break;
		case "record":
			String updateRecordStr = "INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES ('10','9', '50','1')";
			PreparedStatement updateRec = connect.prepareStatement(updateRecordStr);
			String inputRec = JOptionPane.showInputDialog("*FirstName:");
			updateRec.setString(1, inputRec);
			break;
		}
		return str;
	}
}
