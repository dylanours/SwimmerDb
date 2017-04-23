import java.awt.BorderLayout;

import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.awt.Dialog;
import java.util.concurrent.TimeUnit;

public class HomeWindow extends JFrame {

    private JPanel contentPane;

    public static void main(String[] args) throws SQLException {
        // JDBC connector

        String url = "jdbc:mysql://localhost:3306/SwimmerDB?autoReconnect=true&useSSL=false";
        String username = "user";
        String password = "password";

        Connection connect = DriverManager.getConnection(url, username, password);

        // Call the GUI
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

    // Frame Creation
    private static JButton button = new JButton("Search");
    private static JTextField toolbar = new JTextField();
    private JTextArea textArea = new JTextArea();

    public HomeWindow(Connection connect) throws SQLException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 475, 350);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        toolbar.setBounds(6, 6, 309, 26);
        contentPane.add(toolbar);
        toolbar.setColumns(10);

        textArea.setLineWrap(true);
        textArea.setText(
                "Hello, welcome to the Swimmer Database GUI.\n\nType 'help' for a list of commands to get started.");
        textArea.setEditable(false);
        textArea.setBackground(new Color(238, 238, 238));
        textArea.setBounds(6, 44, 438, 227);
        contentPane.add(textArea);

        JSeparator separator = new JSeparator();
        separator.setBounds(6, 34, 438, 12);
        contentPane.add(separator);

        button.setBounds(327, 6, 117, 29);
        contentPane.add(button);
        // Allows scrolling when the output is too long

        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // textArea is the child.
        scrollPane.setViewportBorder(null);
        scrollPane.setBounds(6, 44, 438, 227); // The same as the text field
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Gets rid of that dumb border. Keeps the flush look.
        contentPane.add(scrollPane);

        Statement stmt = connect.createStatement();
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean moveon = true;
                String input = toolbar.getText();
                ArrayList<String> result = Parse(input.toLowerCase());
                if (result.size() > 0) {
                    try {
                        if (result.get(0).equals("delmarker")) {
                            moveon = true;
                            switch (result.get(2)) {
                            case "swimmer":
                                String specialAllQuery = "SELECT SwimmerId, FirstName, LastName FROM Swimmer";
                                ResultSet results = stmt.executeQuery(specialAllQuery);
                                String output = "SwimmerId: Last Name, First Name\n\n";
                                while (results.next()) {
                                    output = output + results.getInt("SwimmerId") + ": " + results.getString("LastName")
                                            + ", " + results.getString("FirstName") + "\n";
                                }
                                textArea.setText(output);
                                result.remove(0);
                                break;
                            case "meet":
                                specialAllQuery = "SELECT SanctionNumber, Name, DateHeld FROM Meet";
                                results = stmt.executeQuery(specialAllQuery);
                                output = "SanctionNumber: Name, Date Held\n\n";
                                while (results.next()) {
                                    output = output + results.getInt("SanctionNumber") + ": "
                                            + results.getString("Name") + ", " + results.getDate("DateHeld") + "\n";
                                }
                                textArea.setText(output);
                                result.remove(0);
                                break;
                            case "team":
                                specialAllQuery = "SELECT TeamId, HeadCoach, TeamName FROM Team";
                                results = stmt.executeQuery(specialAllQuery);
                                output = "TeamId: Head Coach's Name, Team Name\n\n";
                                while (results.next()) {
                                    output = output + results.getInt("TeamId") + ": " + results.getString("HeadCoach")
                                            + ", " + results.getString("TeamName") + "\n";
                                }
                                textArea.setText(output);
                                result.remove(0);
                                break;
                            case "result":
                                specialAllQuery = "SELECT * FROM Records";
                                results = stmt.executeQuery(specialAllQuery);
                                output = "EventId, SwimmerId, Time, SanctionNumber\n\n";
                                while (results.next()) {
                                    output = output + results.getInt("EventId") + ", " + results.getString("SwimmerId")
                                            + ", " + results.getString("Time") + ", "
                                            + results.getString("SanctionNumber") + "\n";
                                }
                                textArea.setText(output);
                                result.remove(0);
                                break;
                            }
                        }
                        if (result.get(0).equals("modmarker")) {
                            moveon = false;
                            Statement stmt = connect.createStatement();
                            JFrame frame = new JFrame("InputDialog");
                            switch (result.get(2)) {
                            case "swimmer":
                                String query = "SELECT FirstName, LastName FROM Swimmer";
                                ResultSet results = stmt.executeQuery(query);
                                ArrayList<String> namesFirst = new ArrayList<String>();
                                while (results.next()) {
                                    namesFirst
                                            .add(results.getString("FirstName") + " " + results.getString("LastName"));
                                }
                                int s = namesFirst.size();
                                String[] names = new String[s];
                                for (int i = 0; i < s; i++) {
                                    names[i] = namesFirst.get(i);
                                }
                                String inName = null;
                                while (inName == null) {
                                    inName = (String) JOptionPane.showInputDialog(null,
                                            "Pick the name of the swimmer to modify:", "Input",
                                            JOptionPane.INFORMATION_MESSAGE, null, names, names[0]);
                                }
                                String[] firstLast = inName.split("\\s");
                                String firstName = firstLast[0];
                                String lastName = firstLast[1];
                                query = "SELECT * FROM (SELECT SwimmerId, FirstName, MiddleName, BirthDate, Age, LastName, Gender, TeamId FROM Swimmer) AS SwimmerSub JOIN Records on SwimmerSub.SwimmerId = Records.SwimmerId NATURAL JOIN `Team` NATURAL JOIN `Event` WHERE ( FirstName = '"
                                        + firstName + "' AND LastName = '" + lastName + "' )";
                                results = stmt.executeQuery(query);
                                results.next();
                                int swimId = results.getInt("SwimmerId");
                                String output = "SwimmerId: " + results.getInt("SwimmerId") + "\nName: "
                                        + results.getString("FirstName") + " " + results.getString("MiddleName") + " "
                                        + results.getString("LastName") + "\nBirthDate: "
                                        + results.getString("BirthDate") + "\n  Age: " + results.getInt("Age")
                                        + "\nGender: " + results.getString("Gender") + "\nTeam Name: "
                                        + results.getString("TeamName") + "\nEvent: " + results.getString("Distance")
                                        + " " + results.getString("Stroke");
                                String[] attributes = { "FirstName", "MiddleName", "LastName", "BirthDate", "Age",
                                        "Gender", "Team", "Event" };
                                textArea.setText(output);
                                String attribute = null;
                                attribute = (String) JOptionPane.showInputDialog(null,
                                        "Which attribute would you like to modify?", "Input",
                                        JOptionPane.INFORMATION_MESSAGE, null, attributes, "Pick an attribute...");
                                if (attribute != null) {
                                    String val = JOptionPane.showInputDialog("Enter a new " + attribute + ".",
                                            "Type here...");
                                    String query2 = "UPDATE Swimmer SET " + attribute + "= \"" + val
                                            + "\" WHERE SwimmerId = \'" + swimId + "\'";
                                    stmt.executeUpdate(query2);
                                }
                                results = stmt.executeQuery(query);
                                results.next();
                                output = "Updated Swimmer...\n\nSwimmerId: " + results.getInt("SwimmerId") + "\nName: "
                                        + results.getString("FirstName") + " " + results.getString("MiddleName") + " "
                                        + results.getString("LastName") + "\nBirthDate: "
                                        + results.getString("BirthDate") + "\n  Age: " + results.getInt("Age")
                                        + "\nGender: " + results.getString("Gender") + "\nTeam Name: "
                                        + results.getString("TeamName") + "\nEvent: " + results.getString("Distance")
                                        + " " + results.getString("Stroke");
                                textArea.setText(output);
                                result.remove(0);
                                break;
                            case "meet":
                                String mquery = "SELECT Name FROM Meet";
                                ResultSet mresults = stmt.executeQuery(mquery);
                                ArrayList<String> mnamesFirst = new ArrayList<String>();
                                while (mresults.next()) {
                                    mnamesFirst.add(mresults.getString("Name"));
                                }
                                int ms = mnamesFirst.size();
                                String[] mnames = new String[ms];
                                for (int i = 0; i < ms; i++) {
                                    mnames[i] = mnamesFirst.get(i);
                                }
                                String minName = null;
                                while (minName == null) {
                                    minName = (String) JOptionPane.showInputDialog(null,
                                            "Pick the name of the meet to modify:", "Input",
                                            JOptionPane.INFORMATION_MESSAGE, null, mnames, mnames[0]);
                                }
                                String mfirstName = minName;
                                mquery = "SELECT SanctionNumber, Name, Dateheld FROM Meet WHERE Name = '" + mfirstName
                                        + "'";
                                mresults = stmt.executeQuery(mquery);
                                mresults.next();
                                int mswimId = mresults.getInt("SanctionNumber");
                                String moutput = "SanctionNumber: " + mresults.getInt("SanctionNumber") + "\nName: "
                                        + mresults.getString("Name") + "\nDate: " + mresults.getString("DateHeld");
                                String[] mattributes = { "Name", "DateHeld" };
                                textArea.setText(moutput);
                                String mattribute = null;
                                mattribute = (String) JOptionPane.showInputDialog(null,
                                        "Which attribute would you like to modify?", "Input",
                                        JOptionPane.INFORMATION_MESSAGE, null, mattributes, "Pick an attribute...");
                                if (mattribute != null) {
                                    String mval = JOptionPane.showInputDialog("Enter a new " + mattribute + ".",
                                            "Type here...");
                                    String mquery2 = "UPDATE Meet SET " + mattribute + "= \"" + mval
                                            + "\" WHERE SanctionNumber = \'" + mswimId + "\'";
                                    stmt.executeUpdate(mquery2);
                                }
                                mresults = stmt.executeQuery(mquery);
                                mresults.next();
                                moutput = "Updated Meet...\n\nSanctionNumber: " + mresults.getInt("SanctionNumber")
                                        + "\nName: " + mresults.getString("Name") + "\nDate: "
                                        + mresults.getString("DateHeld");
                                textArea.setText(moutput);
                                result.remove(0);
                                break;
                            case "team":
                                String tquery = "SELECT TeamName FROM Team";
                                ResultSet tresults = stmt.executeQuery(tquery);
                                ArrayList<String> tnamesFirst = new ArrayList<String>();
                                while (tresults.next()) {
                                    tnamesFirst.add(tresults.getString("TeamName"));
                                }
                                int ts = tnamesFirst.size();
                                String[] tnames = new String[ts];
                                for (int i = 0; i < ts; i++) {
                                    tnames[i] = tnamesFirst.get(i);
                                }
                                String tinName = null;
                                while (tinName == null) {
                                    tinName = (String) JOptionPane.showInputDialog(null,
                                            "Pick the name of the team to modify:", "Input",
                                            JOptionPane.INFORMATION_MESSAGE, null, tnames, tnames[0]);
                                }
                                String tfirstName = tinName;
                                tquery = "SELECT TeamId, HeadCoach, TeamName, PoolStreet, PoolNumber, PoolCity, PoolCountry FROM Team WHERE TeamName = '"
                                        + tfirstName + "'";
                                tresults = stmt.executeQuery(tquery);
                                tresults.next();
                                int tswimId = tresults.getInt("TeamId");
                                String toutput = "TeamId: " + tresults.getInt("TeamId") + "\nTeam Name: "
                                        + tresults.getString("TeamName") + "\nHead Coach Name: "
                                        + tresults.getString("HeadCoach") + "\nPool Address: "
                                        + tresults.getString("PoolStreet") + " " + tresults.getInt("PoolNumber") + ", "
                                        + tresults.getString("PoolCity") + ", " + tresults.getString("PoolCountry");
                                String[] tattributes = { "TeamName", "HeadCoach", "PoolStreet", "PoolNumber",
                                        "PoolCity", "PoolCountry" };
                                textArea.setText(toutput);
                                String tattribute = null;
                                tattribute = (String) JOptionPane.showInputDialog(null,
                                        "Which attribute would you like to modify?", "Input",
                                        JOptionPane.INFORMATION_MESSAGE, null, tattributes, "Pick an attribute...");
                                if (tattribute != null) {
                                    String tval = JOptionPane.showInputDialog("Enter a new " + tattribute + ".",
                                            "Type here...");
                                    String tquery2 = "UPDATE Team SET " + tattribute + "= \"" + tval
                                            + "\" WHERE TeamId = \'" + tswimId + "\'";
                                    stmt.executeUpdate(tquery2);
                                }
                                tresults = stmt.executeQuery(tquery);
                                tresults.next();
                                toutput = "Updated Team...\n\nTeamId: " + tresults.getInt("TeamId") + "\nTeam Name: "
                                        + tresults.getString("TeamName") + "\nHead Coach Name: "
                                        + tresults.getString("HeadCoach") + "\nPool Address: "
                                        + tresults.getString("PoolStreet") + " " + tresults.getInt("PoolNumber") + ", "
                                        + tresults.getString("PoolCity") + ", " + tresults.getString("PoolCountry");
                                textArea.setText(toutput);
                                result.remove(0);
                                break;
                            /*case "result":
                                String rquery = "SELECT SwimmerId, Time FROM Records NATURAL JOIN (SELECT SwimmerId, FirstName, LastName FROM `Team`)";
                                ResultSet rresults = stmt.executeQuery(rquery);
                                ArrayList<String> rnamesFirst = new ArrayList<String>();
                                while (tresults.next()) {
                                    tnamesFirst.add(tresults.getString("TeamName"));
                                }
                                int ts = tnamesFirst.size();
                                String[] tnames = new String[ts];
                                for (int i = 0; i < ts; i++) {
                                    tnames[i] = tnamesFirst.get(i);
                                }
                                String tinName = null;
                                while (tinName == null) {
                                    tinName = (String) JOptionPane.showInputDialog(null,
                                            "Pick the name of the team to modify:", "Input",
                                            JOptionPane.INFORMATION_MESSAGE, null, tnames, tnames[0]);
                                }
                                String tfirstName = tinName;
                                tquery = "SELECT TeamId, HeadCoach, TeamName, PoolStreet, PoolNumber, PoolCity, PoolCountry FROM Team WHERE TeamName = '"
                                        + tfirstName + "'";
                                tresults = stmt.executeQuery(tquery);
                                tresults.next();
                                int tswimId = tresults.getInt("TeamId");
                                String toutput = "TeamId: " + tresults.getInt("TeamId") + "\nTeam Name: "
                                        + tresults.getString("TeamName") + "\nHead Coach Name: "
                                        + tresults.getString("HeadCoach") + "\nPool Address: "
                                        + tresults.getString("PoolStreet") + " " + tresults.getInt("PoolNumber") + ", "
                                        + tresults.getString("PoolCity") + ", " + tresults.getString("PoolCountry");
                                String[] tattributes = { "TeamName", "HeadCoach", "PoolStreet", "PoolNumber",
                                        "PoolCity", "PoolCountry" };
                                textArea.setText(toutput);
                                String tattribute = null;
                                tattribute = (String) JOptionPane.showInputDialog(null,
                                        "Which attribute would you like to modify?", "Input",
                                        JOptionPane.INFORMATION_MESSAGE, null, tattributes, "Pick an attribute...");
                                if (tattribute != null) {
                                    String tval = JOptionPane.showInputDialog("Enter a new " + tattribute + ".",
                                            "Type here...");
                                    String tquery2 = "UPDATE Team SET " + tattribute + "= \"" + tval
                                            + "\" WHERE TeamId = \'" + tswimId + "\'";
                                    stmt.executeUpdate(tquery2);
                                }
                                tresults = stmt.executeQuery(tquery);
                                tresults.next();
                                toutput = "Updated Team...\n\nTeamId: " + tresults.getInt("TeamId") + "\nTeam Name: "
                                        + tresults.getString("TeamName") + "\nHead Coach Name: "
                                        + tresults.getString("HeadCoach") + "\nPool Address: "
                                        + tresults.getString("PoolStreet") + " " + tresults.getInt("PoolNumber") + ", "
                                        + tresults.getString("PoolCity") + ", " + tresults.getString("PoolCountry");
                                textArea.setText(toutput);
                                result.remove(0);
                                break;*/
                            }
                        }
                        if (moveon) {
                            textArea.setText(Analysis(result, connect));
                        }
                    } catch (SQLException | ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        MouseAdapter ma = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                toolbar.setText("");
            }
        };
        toolbar.addMouseListener(ma);
        toolbar.addActionListener(al);
        button.addActionListener(al);

    }

    public static ArrayList<String> Parse(String input) {
        String[] words = input.split("\\s");
        String[] keywords = { "help", "all", "add", "compare", "modify", "record", "relay", "swimmer", "swimmers",
                "teams", "events", "meets", "records", "times", "results", "team", "meet", "result", "delete",
                "fastest", "slowest", "male", "males", "boys", "men", "female", "females", "girls", "women",
                "freestyle", "free", "butterfly", "fly", "breaststroke", "breast", "backstroke", "back", "50", "100",
                "200", "400", "800", "1500", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
                "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
                "nineteen", "twenty" };
        ArrayList<String> residue = new ArrayList<String>();

        for (String w : words) {
            switch (w) {
            case "modify":
                residue.add("modmarker");
                break;
            case "delete":
                residue.add("delmarker");
                break;

            }
        }

        for (String w : words) {
            for (String k : keywords) {
                if (w.equals(k)) {
                    residue.add(w);
                }
            }
        }

        return residue;
    }

    public String Analysis(ArrayList<String> result, Connection connect) throws SQLException, ParseException {
        Statement stmt = connect.createStatement();
        String output = "";
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
        boolean eventFound = false;

        int event = 0;

        String baseSelect = "SELECT FirstName, LastName";
        String baseFrom = "FROM Swimmer";
        String baseFrom1 = "FROM (SELECT SwimmerId, FirstName, LastName";
        String baseFrom2 = "FROM Swimmer ) AS SwimmerSub NATURAL JOIN Records";
        String baseWhere = "WHERE";
        String baseGroupBy = "GROUP BY";
        String baseHaving = "HAVING";
        String baseOrderBy = "ORDER BY";
        String baseLimit = "LIMIT";
        int count = 0;
        for (String r : result) {
            switch (r) {
            case "help":
                output = "Please keep quanities of swimmers in word form: 3 -> three.\nRace distances in number form: 100 butterfly, 1500 freestyle\n\nHere is a list of acceptable commands:\nAdd\n   Ex: Add a team, add a swimmer, add a record\nDelete\n   Ex: Delete a team, Delete a swimmer, delete a record\nModify\nFastest\n   Ex: Fastest three 100 fly swimmers\nSlowest\n   Ex: Slowest male swimmers";
                dataModifications = true;
                break;
            case "compare":
                Compare(connect);
                return "";
            case "all":
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "swimmers":
                        query = "SELECT SwimmerId, FirstName, LastName FROM Swimmer";
                        output = "Swimmer Id: First Name, Last Name\n\n";
                        ResultSet results = stmt.executeQuery(query);
                        while (results.next()) {
                            output = output + results.getString("SwimmerId") + ": " + results.getString("FirstName")
                                    + " " + results.getString("LastName") + "\n";
                        }
                        return output;
                    case "meets":
                        query = "SELECT SanctionNumber, Name, DateHeld FROM Meet";
                        ResultSet resultsMeets = stmt.executeQuery(query);
                        output = "Sanction Number: Name, Date held\n\n";
                        while (resultsMeets.next()) {
                            output = output + resultsMeets.getString("SanctionNumber") + ": "
                                    + resultsMeets.getString("Name") + ", " + resultsMeets.getDate("DateHeld") + "\n";
                        }
                        return output;
                    case "events":
                        query = "SELECT EventId, Stroke, Distance FROM Event";
                        ResultSet resultsEvents = stmt.executeQuery(query);
                        output = "EventId: Event Name\n\n";
                        while (resultsEvents.next()) {
                            output = output + resultsEvents.getString("EventId") + ": "
                                    + resultsEvents.getString("Distance") + " " + resultsEvents.getString("Stroke")
                                    + "\n";
                        }
                        return output;
                    case "teams":
                        query = "SELECT TeamId, TeamName, HeadCoach FROM Team";
                        ResultSet resultsTeams = stmt.executeQuery(query);
                        output = "Team Id: Name of team, Name of head coach\n\n";
                        while (resultsTeams.next()) {
                            output = output + resultsTeams.getString("TeamId") + ": "
                                    + resultsTeams.getString("TeamName") + ", " + resultsTeams.getString("HeadCoach")
                                    + "\n";
                        }
                        return output;
                    case "records":
                    case "times":
                    case "results":
                        query = "SELECT EventId, SwimmerId, Time, SanctionNumber FROM Records";
                        ResultSet resultsRecords = stmt.executeQuery(query);
                        output = "Event Id: Swimmer Id, Time, Sanction number of meet\n\n";
                        while (resultsRecords.next()) {
                            output = output + resultsRecords.getInt("EventId") + ": "
                                    + resultsRecords.getInt("SwimmerId") + ", " + resultsRecords.getInt("Time") + ", "
                                    + resultsRecords.getInt("SanctionNumber") + "\n";
                        }
                        return output;
                    }
                }
                break;
            case "add":
                if (count < (result.size() - 1)) {
                    add(connect, result.get(count + 1));
                }
                dataModifications = true;
                output = result.get(count + 1) + " added.";
                break;
            case "delete":
                delete(connect, result.get(count + 1));
                output = result.get(count + 1) + " deleted.";
                dataModifications = true;
                break;
            case "fastest":
                found = true;
                needed = true;
                if (orderBy == false) {
                    orderBy = true;
                    baseOrderBy = baseOrderBy + " Records.Time ASC";
                }
                break;
            case "slowest":
                found = true;
                needed = true;
                if (orderBy == false) {
                    orderBy = true;
                    baseOrderBy = baseOrderBy + " Records.Time DESC";
                }
                break;
            case "female":
            case "females":
            case "girls":
            case "women":
                found = true;
                if (where) {
                    baseWhere = baseWhere + " AND Gender='F'";
                } else {
                    where = true;
                    baseWhere = baseWhere + " Gender='F'";
                }
                baseFrom1 = baseFrom1 + ", Gender";
                baseSelect = baseSelect + ", Gender";
                break;
            case "male":
            case "males":
            case "boys":
            case "men":
                found = true;
                if (where) {
                    baseWhere = baseWhere + " AND Gender='M'";
                } else {
                    where = true;
                    baseWhere = baseWhere + " Gender='M'";
                }
                baseFrom1 = baseFrom1 + ", Gender";
                baseSelect = baseSelect + ", Gender";
                break;
            case "50":
                found = true;
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "free":
                    case "freestyle":
                        eventFound = true;
                        event = 1;
                        break;
                    case "back":
                    case "backstroke":
                        found = false;
                        break;
                    case "fly":
                    case "butterfly":
                        found = false;
                        break;
                    case "breast":
                    case "breastStroke":
                        found = false;
                        break;
                    }
                } else {
                    found = false;
                }
                break;
            case "100":
                found = true;
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "free":
                    case "freestyle":
                        eventFound = true;
                        event = 2;
                        break;
                    case "back":
                    case "backstroke":
                        eventFound = true;
                        event = 6;
                        break;
                    case "fly":
                    case "butterfly":
                        eventFound = true;
                        event = 10;
                        break;
                    case "breast":
                    case "breastStroke":
                        eventFound = true;
                        event = 8;
                        break;
                    }
                } else {
                    found = false;
                }
                break;
            case "200":
                found = true;
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "free":
                    case "freestyle":
                        eventFound = true;
                        event = 3;
                        break;
                    case "back":
                    case "backstroke":
                        eventFound = true;
                        event = 7;
                        break;
                    case "fly":
                    case "butterfly":
                        eventFound = true;
                        event = 11;
                        break;
                    case "breast":
                    case "breastStroke":
                        eventFound = true;
                        event = 9;
                        break;
                    }
                } else {
                    found = false;
                }
                break;
            case "400":
                found = true;
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "free":
                    case "freestyle":
                        if (count < (result.size() - 2)) {
                            switch (result.get(count + 2)) {
                            case "relay":
                                eventFound = true;
                                event = 14;
                                break;
                            default:
                                eventFound = true;
                                event = 4;
                            }
                        }
                        break;
                    case "back":
                    case "backstroke":
                        found = false;
                        break;
                    case "fly":
                    case "butterfly":
                        found = false;
                        break;
                    case "breast":
                    case "breastStroke":
                        found = false;
                        break;
                    case "medley":
                        eventFound = true;
                        event = 13;
                        break;
                    }
                } else {
                    found = false;
                }
                break;
            case "800":
                found = true;
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "free":
                    case "freestyle":
                        if (count < (result.size() - 2)) {
                            switch (result.get(count + 2)) {
                            case "relay":
                                eventFound = true;
                                event = 15;
                                break;
                            default:
                                found = false;
                            }
                        }
                        break;
                    case "back":
                    case "backstroke":
                        found = false;
                        break;
                    case "fly":
                    case "butterfly":
                        found = false;
                        break;
                    case "breast":
                    case "breastStroke":
                        found = false;
                        break;
                    case "medley":
                        found = false;
                        break;
                    }
                } else {
                    found = false;
                }
                break;
            case "1500":
                found = true;
                if (count < (result.size() - 1)) {
                    switch (result.get(count + 1)) {
                    case "free":
                    case "freestyle":
                        eventFound = true;
                        event = 5;
                        break;
                    case "back":
                    case "backstroke":
                        found = false;
                        break;
                    case "fly":
                    case "butterfly":
                        found = false;
                        break;
                    case "breast":
                    case "breastStroke":
                        found = false;
                        break;
                    case "medley":
                        found = false;
                        break;
                    }
                } else {
                    found = false;
                }
                break;
            case "one":
                limit = true;
                baseLimit = baseLimit + " 1";
                break;
            case "two":
                limit = true;
                baseLimit = baseLimit + " 2";
                break;
            case "three":
                limit = true;
                baseLimit = baseLimit + " 3";
                break;
            case "four":
                limit = true;
                baseLimit = baseLimit + " 4";
                break;
            case "five":
                limit = true;
                baseLimit = baseLimit + " 5";
                break;
            case "six":
                limit = true;
                baseLimit = baseLimit + " 6";
                break;
            case "seven":
                limit = true;
                baseLimit = baseLimit + " 7";
                break;
            case "eight":
                limit = true;
                baseLimit = baseLimit + " 8";
                break;
            case "nine":
                limit = true;
                baseLimit = baseLimit + " 9";
                break;
            case "ten":
                limit = true;
                baseLimit = baseLimit + " 10";
                break;
            case "eleven":
                limit = true;
                baseLimit = baseLimit + " 11";
                break;
            case "twelve":
                limit = true;
                baseLimit = baseLimit + " 12";
                break;
            case "thirteen":
                limit = true;
                baseLimit = baseLimit + " 13";
                break;
            case "fourteen":
                limit = true;
                baseLimit = baseLimit + " 14";
                break;
            case "fifteen":
                limit = true;
                baseLimit = baseLimit + " 15";
                break;
            case "sixteen":
                limit = true;
                baseLimit = baseLimit + " 16";
                break;
            case "seventeen":
                limit = true;
                baseLimit = baseLimit + " 17";
                break;
            case "eighteen":
                limit = true;
                baseLimit = baseLimit + " 18";
                break;
            case "nineteen":
                limit = true;
                baseLimit = baseLimit + " 19";
                break;
            case "twenty":
                limit = true;
                baseLimit = baseLimit + " 20";
                break;
            }
            count++;
        }
        if (eventFound) {
            baseFrom2 = "FROM Swimmer ) AS SwimmerSub on SwimmerSub.SwimmerId = Records.SwimmerId";
            if (where) {
                baseWhere = baseWhere + " AND EventId='" + event + "'";
            } else {
                where = true;
                baseWhere = baseWhere + " EventId='" + event + "'";
            }
            baseFrom1 = baseFrom1 + ", EventId";
            baseSelect = baseSelect + ", EventId";
        }
        if (found) {
            if (needed)
                query = baseSelect + " " + baseFrom1 + " " + baseFrom2;
            else
                query = baseSelect + " " + baseFrom;
            if (where)
                query = query + " " + baseWhere;
            if (groupBy)
                query = query + " " + baseGroupBy;
            if (having)
                query = query + " " + baseHaving;
            if (orderBy)
                query = query + " " + baseOrderBy;
            if (limit)
                query = query + " " + baseLimit;

            // return query;
            ResultSet results = stmt.executeQuery(query);
            while (results.next()) {
                output = output + results.getString("FirstName") + " " + results.getString("LastName") + "\n";
            }
        } else if (dataModifications) {
        } else {
            output = error;
        }

        return output;
    }

    private void add(Connection connect, String type) throws SQLException, ParseException {
        String str = "";
        switch (type) {
        case "swimmer":
            // Parse("specialAllS", connect);
            String updateSwimmerStr = "INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`, `TeamId`,`EventId`) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement update = connect.prepareStatement(updateSwimmerStr);
            String input = null;
            while (input == null) {
                input = JOptionPane.showInputDialog("*FirstName:");
            }
            str = str + input + ", ";
            update.setString(1, input);
            input = JOptionPane.showInputDialog("MiddleName:");
            str = str + input + ", ";
            update.setString(2, input);
            input = null;
            while (input == null) {
                input = JOptionPane.showInputDialog("*LastName:");
            }
            str = str + input + ", ";
            update.setString(3, input);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            input = null;
            while (input == null) {
                input = JOptionPane.showInputDialog("*BirthDate (Year/Month/Day):");
            }
            java.util.Date utilDate = null;
            utilDate = sdf.parse(input);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            update.setDate(4, sqlDate);
            String todayDateStr = "2017/3/27";
            String[] date1 = input.split("/");
            String[] date2 = todayDateStr.split("/");
            str = str + input + ", ";
            int age = Integer.parseInt(date2[0]) - Integer.parseInt(date1[0]);
            update.setInt(5, age);
            input = null;
            while (input == null) {
                input = JOptionPane.showInputDialog("*Gender(M/F):");
            }
            str = str + input + ", ";
            update.setString(6, input);
            input = null;
            while (input == null) {
                input = JOptionPane.showInputDialog("*TeamId:");
            }
            str = str + input + ", ";
            update.setString(7, input);
            input = null;
            while (input == null) {
                input = JOptionPane.showInputDialog("*EventId:");
            }
            str = str + input;
            update.setString(8, input);

            update.executeUpdate();
            // JOptionPane.showMessageDialog(null, str);
            break;
        case "record":
            String updateRecordStr = "INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES (?,?,?,?)";
            PreparedStatement updateRec = connect.prepareStatement(updateRecordStr);
            String inputRec = null;
            inputRec = JOptionPane.showInputDialog("*FirstName:");
            while (inputRec == null)
                inputRec = JOptionPane.showInputDialog("*EventId:");
            str = str + inputRec + ", ";
            updateRec.setString(1, inputRec);
            inputRec = null;
            while (inputRec == null)
                inputRec = JOptionPane.showInputDialog("*SwimmerId:");
            str = str + inputRec + ", ";
            updateRec.setString(2, inputRec);
            inputRec = null;
            while (inputRec == null)
                inputRec = JOptionPane.showInputDialog("*Time");
            str = str + inputRec + ", ";
            updateRec.setString(3, inputRec);
            inputRec = null;
            while (inputRec == null)
                inputRec = JOptionPane.showInputDialog("*SanctionNumber");
            str = str + inputRec + ", ";
            updateRec.setString(4, inputRec);
            updateRec.executeUpdate();
            break;
        case "team":
            String updateTeamStr = "INSERT INTO `Team` (`HeadCoach`,`TeamName`,`PoolStreet`,`PoolNumber`,`PoolCity`,`PoolState`,`PoolCountry`) VALUES (?, ?, ?, ?, ?, ?,?)";
            PreparedStatement updateTeam = connect.prepareStatement(updateTeamStr);

            String inputTeam = JOptionPane.showInputDialog("Head Coach:");
            updateTeam.setString(1, inputTeam);
            inputTeam = null;
            while (inputTeam == null) {
                inputTeam = JOptionPane.showInputDialog("*Team Name:");
            }
            updateTeam.setString(2, inputTeam);
            inputTeam = JOptionPane.showInputDialog("Pool Street:");
            updateTeam.setString(3, inputTeam);
            inputTeam = JOptionPane.showInputDialog("Pool Number:");
            updateTeam.setInt(4, Integer.parseInt(inputTeam));
            inputTeam = JOptionPane.showInputDialog("Pool City:");
            updateTeam.setString(5, inputTeam);
            inputTeam = JOptionPane.showInputDialog("Pool State:");
            updateTeam.setString(6, inputTeam);
            inputTeam = JOptionPane.showInputDialog("Pool Country:");
            updateTeam.setString(7, inputTeam);
            updateTeam.executeUpdate();
            break;
        case "meet":
            String updateMeetStr = "INSERT INTO `Meet` (`Name`,`DateHeld`) VALUES (?, ?)";
            PreparedStatement updateMeet = connect.prepareStatement(updateMeetStr);
            String inputMeet = JOptionPane.showInputDialog("Name:");
            updateMeet.setString(1, inputMeet);
            inputMeet = JOptionPane.showInputDialog("DateHeld (Year/Month/Day):");
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            java.util.Date jUtilDate = null;
            jUtilDate = df.parse(inputMeet);
            java.sql.Date dateSQL = new java.sql.Date(jUtilDate.getTime());
            updateMeet.setDate(2, dateSQL);
            updateMeet.executeUpdate();
            break;
        }
    }

    private void delete(Connection connect, String type) throws SQLException {
        switch (type) {
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

    private JTextField barLeft;
    private JTextField barRight;
    private JPanel contentPaneCompare;

    public void Compare(Connection connect) throws SQLException {
        JFrame compareWindow = new JFrame();
        compareWindow.setVisible(true);
        compareWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // end();
        /*
         * compareWindow.addWindowListener(new WindowAdapter() { public void
         * windowClosing(WindowEvent e) {
         * setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); } });
         */
        compareWindow.setBounds(100, 100, 615, 450);
        contentPaneCompare = new JPanel();
        contentPaneCompare.setBorder(new EmptyBorder(2, 2, 2, 2));
        compareWindow.setContentPane(contentPaneCompare);
        contentPaneCompare.setLayout(null);

        JTextArea textLeft = new JTextArea();
        textLeft.setEditable(false);
        textLeft.setBackground(UIManager.getColor("Button.background"));
        textLeft.setBorder(null);
        textLeft.setBounds(6, 52, 250, 370);
        contentPaneCompare.add(textLeft);

        JTextArea textRight = new JTextArea();
        textRight.setEditable(false);
        textRight.setBackground(UIManager.getColor("Button.background"));
        textRight.setBorder(null);
        textRight.setBounds(344, 52, 250, 370);
        contentPaneCompare.add(textRight);

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        separator.setBounds(294, 52, 12, 370);
        contentPaneCompare.add(separator);

        barLeft = new JTextField();
        barLeft.setBounds(6, 14, 250, 26);
        contentPaneCompare.add(barLeft);
        barLeft.setColumns(10);

        barRight = new JTextField();
        barRight.setColumns(10);
        barRight.setBounds(344, 14, 250, 26);
        contentPaneCompare.add(barRight);

        ActionListener alL = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = barLeft.getText();
                barLeft.setText("");

                try {
                    boolean error = true;
                    Statement stmt = connect.createStatement();
                    ArrayList<String> swimmers = new ArrayList<String>();

                    String query = "SELECT FirstName, LastName FROM Swimmer";
                    ResultSet results = stmt.executeQuery(query);

                    while (results.next()) {
                        swimmers.add(results.getString("FirstName") + " " + results.getString("LastName"));
                    }

                    for (String s : swimmers) {
                        if (s.toLowerCase().equals(input.toLowerCase())) {
                            error = false;
                        }
                    }
                    if (error) {
                        String errorStr = "We didn't understand your entry.\nHere is a list of all of the swimmers.\n\n";
                        for (int i = 0; i < swimmers.size(); i++) {
                            errorStr = errorStr + (i + 1) + ": " + swimmers.get(i) + "\n";
                        }
                        textLeft.setText(errorStr);
                    } else {
                        String[] names = input.split("\\s");
                        String firstName = names[0];
                        String lastName = names[1];
                        query = "SELECT COUNT(records.Time) FROM (SELECT SwimmerId, FirstName, LastName FROM Swimmer) AS SwimmerSub JOIN Records on SwimmerSub.SwimmerId = Records.SwimmerId NATURAL JOIN `Event` WHERE ( FirstName = '"
                                + firstName + "' AND LastName = '" + lastName + "' )";
                        results = stmt.executeQuery(query);
                        int size = 0;
                        while (results.next()) {
                            size = results.getInt("COUNT(records.Time)");
                        }

                        String data[][] = new String[3][size];
                        query = "SELECT FirstName, LastName, Distance, Stroke, Records.Time FROM (SELECT SwimmerId, FirstName, LastName FROM Swimmer) AS SwimmerSub JOIN Records on SwimmerSub.SwimmerId = Records.SwimmerId NATURAL JOIN `Event` WHERE ( FirstName = '"
                                + firstName + "' AND LastName = '" + lastName + "' )";
                        results = stmt.executeQuery(query);
                        int cnt = 0;
                        while (results.next()) {
                            data[0][cnt] = results.getString("Stroke");
                            data[1][cnt] = results.getString("Distance");
                            data[2][cnt] = results.getString("records.Time");
                            cnt++;
                        }
                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                        String output = firstName + " " + lastName + "\n\n";
                        String Stroke = "";
                        String Distance = "";

                        for (int i = 0; i < size; i++) {
                            if (!Stroke.equals(data[0][i]) || !Distance.equals(data[1][i])) {
                                Stroke = data[0][i];
                                Distance = data[1][i];
                                output = output + "  " + Distance + " " + Stroke + "\n";
                            }
                            output = output + "    " + data[2][i] + "\n";
                        }
                        textLeft.setText(output);
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        };

        ActionListener alR = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = barRight.getText();
                barRight.setText("");

                try {
                    boolean error = true;
                    Statement stmt = connect.createStatement();
                    ArrayList<String> swimmers = new ArrayList<String>();

                    String query = "SELECT FirstName, LastName FROM Swimmer";
                    ResultSet results = stmt.executeQuery(query);

                    while (results.next()) {
                        swimmers.add(results.getString("FirstName") + " " + results.getString("LastName"));
                    }

                    for (String s : swimmers) {
                        if (s.toLowerCase().equals(input.toLowerCase())) {
                            error = false;
                        }
                    }
                    if (error) {
                        String errorStr = "We didn't understand your entry.\nHere is a list of all of the swimmers.\n\n";
                        for (int i = 0; i < swimmers.size(); i++) {
                            errorStr = errorStr + (i + 1) + ": " + swimmers.get(i) + "\n";
                        }
                        textRight.setText(errorStr);
                    } else {
                        String[] names = input.split("\\s");
                        String firstName = names[0];
                        String lastName = names[1];
                        query = "SELECT COUNT(records.Time) FROM (SELECT SwimmerId, FirstName, LastName FROM Swimmer) AS SwimmerSub JOIN Records on SwimmerSub.SwimmerId = Records.SwimmerId NATURAL JOIN `Event` WHERE ( FirstName = '"
                                + firstName + "' AND LastName = '" + lastName + "' )";
                        results = stmt.executeQuery(query);
                        int size = 0;
                        while (results.next()) {
                            size = results.getInt("COUNT(records.Time)");
                        }

                        String data[][] = new String[3][size];
                        query = "SELECT FirstName, LastName, Distance, Stroke, Records.Time FROM (SELECT SwimmerId, FirstName, LastName FROM Swimmer) AS SwimmerSub JOIN Records on SwimmerSub.SwimmerId = Records.SwimmerId NATURAL JOIN `Event` WHERE ( FirstName = '"
                                + firstName + "' AND LastName = '" + lastName + "' )";
                        results = stmt.executeQuery(query);
                        int cnt = 0;
                        while (results.next()) {
                            data[0][cnt] = results.getString("Stroke");
                            data[1][cnt] = results.getString("Distance");
                            data[2][cnt] = results.getString("records.Time");
                            cnt++;
                        }
                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
                        String output = firstName + " " + lastName + "\n\n";
                        String Stroke = "";
                        String Distance = "";

                        for (int i = 0; i < size; i++) {
                            if (!Stroke.equals(data[0][i]) || !Distance.equals(data[1][i])) {
                                Stroke = data[0][i];
                                Distance = data[1][i];
                                output = output + "  " + Distance + " " + Stroke + "\n";
                            }
                            output = output + "    " + data[2][i] + "\n";
                        }
                        textRight.setText(output);
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }
        };

        barLeft.addActionListener(alL);
        barRight.addActionListener(alR);

    }// Compare end
}
