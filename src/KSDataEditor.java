import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KSDataEditor {
	
	private static final String DISPLAY_STROKE_PATHS = ("M18.38,46.36c2.37,0.64,5.38,0.73,7.74,0.47c14.39-1.58,36.51-4.46,51.25-5.75c2.51-0.22,6-0.33,7.89,0.42|M50.25,15.75c1,1.08,1.61,2.16,1.74,4.32C53.75,48.25,46.5,79.75,17,92.25|M51.5,45c8.29,11.97,23.78,31.58,35.16,41.85c2.37,2.14,4.59,4.15,8.09,5.4|M67.33,20.25c6.9,3.89,8.78,6.85,10.92,10.5");
	private static final String CREATED_AT = "1489261845162";
	private static final int GROUP_TYPE = 0;
	
	private int groupsLinkId;
	private int groupingsId;
	
	public Connection conn;
	private File ksdata;
	private File inputFile;
	private ArrayList<Integer> userKanjiCode;
	private ArrayList<String> inputLines;
	
	
	//args[0] ksdataFilePath 
	//args[1] inputFilePath
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        KSDataEditor test = new KSDataEditor(args[0], args[1]);
        test.updateNotes();
        test.conn.close();
    }
    
    public KSDataEditor(String ksdataPath, String inputFile) throws SQLException, IOException {
    	this.ksdata = new File(ksdataPath);
    	this.inputFile = new File(inputFile);
    	conn = DriverManager.getConnection("jdbc:sqlite:" + ksdata.getPath());
    	scanInputLines();
    	scanUserKanji();
        createNewGroup();
    	addGrouping();
    }
    
    public boolean isKanji(Character c) {
    	 return (c>=0x4e00 && c<0xa000);
    }
    
    public void scanUserKanji() throws SQLException {
    	userKanjiCode = new ArrayList<Integer>();
    	//don't overwrite existing sentences
    	ResultSet rs = conn.createStatement().executeQuery(
    			"SELECT kanji_code" + " " + 
    			"FROM user_kanji_info");
        while (rs.next()) {
        	userKanjiCode.add(rs.getInt("kanji_code"));
        }
        rs.close();
    	Collections.sort(userKanjiCode);
    	System.out.println(userKanjiCode.toString());
    }
    
    public void scanInputLines() throws IOException {
    	inputLines = new ArrayList<String>();
    	BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
    	String line;
    	while((line = reader.readLine()) != null) {
    		if(!line.isEmpty()) {
    			inputLines.add(line);
    		}
    	}
    	reader.close();
    }
    
    public void updateNotes() throws SQLException {
    	ResultSet maxID = conn.createStatement().executeQuery(
    			"SELECT MAX(id)" + " " + 
    			"FROM user_kanji_info");
        int kanjiId = maxID.getInt(1) + 1;
    	maxID.close();
    	for(String line : inputLines) {
    		for(Character c: line.toCharArray()) {
    			if(isKanji(c)) {

    				addToGroup(c);
    				int arrayListIndex;
    				if((arrayListIndex = Collections.binarySearch(userKanjiCode, (int)c)) >= 0) {
    					int kanji_code = userKanjiCode.get(arrayListIndex);
    					PreparedStatement pstmt = null;   
    			         try {
    			             pstmt = conn.prepareStatement(
    			                         "UPDATE user_kanji_info" + " " +
    			                         "SET notes = ?" + " " +
    			                         "WHERE notes IS NULL" + " " +
    			                         "AND kanji_code = ?");

    			             pstmt.setString(1, line);
    			             pstmt.setInt(2, kanji_code);
    			             pstmt.executeUpdate();
    			             pstmt.close();
    			         }	finally {
    			             if (pstmt != null) pstmt.close();
    			         }
    				} else {
    					addKanji(c, kanjiId++, line);
    				}
    			}
    		}
    	}
    }
    
    public void createNewGroup() throws SQLException {
    	String name = inputFile.getName();
    	ResultSet maxID = conn.createStatement().executeQuery(
    			"SELECT MAX(id)" + " " + 
    			"FROM groupings");
        int groupId = maxID.getInt(1) + 1;
        groupingsId = maxID.getInt(1) + 1;
        maxID.close();
        PreparedStatement pstmt = null;  
        try {
            pstmt = conn.prepareStatement(
                        "INSERT INTO groupings" + " " +
                        "VALUES (?,?,?,?)");

            pstmt.setInt(1, groupId);
            pstmt.setInt(2, GROUP_TYPE);
            pstmt.setString(3, name);
            pstmt.setString(4, CREATED_AT);
            pstmt.executeUpdate();
            pstmt.close();
        }	finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    public void addKanji(Character c, int kanjiId, String line) throws SQLException {
    	userKanjiCode.add((int)c);
    	Collections.sort(userKanjiCode);
    	PreparedStatement pstmt = null;   
    	try {
    		pstmt = conn.prepareStatement(
    				"INSERT INTO user_kanji_info" + " " +
    				"VALUES(?,?,0,0,0,0,0,0,0,0.0,0,0,0,0,0.0,0,0,0,0,?)");

    		pstmt.setInt(1, kanjiId);
    		pstmt.setInt(2, (int)c);
    		pstmt.setString(3, line);
    		pstmt.executeUpdate();
    		pstmt.close();
    	}	finally {
    		if (pstmt != null) pstmt.close();
    	}
    }
    
    public void addToGroup(Character c) throws SQLException {
    	PreparedStatement pstmt = null;   
    	try {
    		pstmt = conn.prepareStatement(
    				"INSERT INTO groups_link" + " " +
    				"VALUES(?,?,0)");

    		pstmt.setInt(1, groupsLinkId);
    		pstmt.setInt(2, (int)c);
    		pstmt.executeUpdate();
    		pstmt.close();
    	}	catch (SQLException e) {
    		System.out.println(c + "::" + (int)c);
    	}	finally {
    		if (pstmt != null) pstmt.close();
    	}
    }
    
    public void addGrouping() throws SQLException {
    	ResultSet groupsLinkMax = conn.createStatement().executeQuery(
    			"SELECT MAX(group_id)" + " " + 
    			"FROM groups_link");
    	groupsLinkId = groupsLinkMax.getInt(1) + 1;
    	groupsLinkMax.close();
    	
    	ResultSet groupsIdMax = conn.createStatement().executeQuery(
    			"SELECT MAX(id)" + " " + 
    			"FROM groups");
    	int groupId = groupsIdMax.getInt(1) + 1;
    	groupsIdMax.close();
    	PreparedStatement pstmt = null;   
    	try {
    		pstmt = conn.prepareStatement(
    				"INSERT INTO groups" + " " +
    				"VALUES(?,0,0,0,0,21516,?,0,?)");

    		pstmt.setInt(1, groupId);
    		pstmt.setString(2, DISPLAY_STROKE_PATHS);
    		pstmt.setInt(3, groupingsId);
    		pstmt.executeUpdate();
    		pstmt.close();
    	}	finally {
    		if (pstmt != null) pstmt.close();
    	}
    }
}