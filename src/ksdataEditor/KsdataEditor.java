package ksdataEditor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KsdataEditor {
	
	protected boolean addKnown;

	protected Connection conn;
	protected File ksdata;
	protected File inputFile;
	
	protected int groupingsId;
	protected int groupsId;
	protected int groupsLinkId;
	
	protected int kanjiId;
	protected ArrayList<Integer> userKanjiCode;
	protected ArrayList<String> inputLines;
	
	private GroupCreator gc;
	private GroupEditor ge;
	
	//args[0] ksdataFilePath 
	//args[1] inputFilePath
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        KsdataEditor test = new KsdataEditor(args[0], args[1], Boolean.parseBoolean(args[2]));
        test.ge.updateNotes();
        test.conn.close();
    }
    
    public KsdataEditor(String ksdataPath, String inputFile, boolean addKnown) throws SQLException, IOException {
    	this.addKnown = addKnown;
    	this.ksdata = new File(ksdataPath);
    	this.inputFile = new File(inputFile);
    	conn = DriverManager.getConnection("jdbc:sqlite:" + ksdata.getPath());
    	initIds();
    	scanInputLines();
    	scanUserKanji();
    	gc = new GroupCreator(this);
    	ge = new GroupEditor(this);
    }
    
    public void initIds() throws SQLException {
    	ResultSet maxGroupingsId = conn.createStatement().executeQuery(
    			"SELECT MAX(id)" + " " + 
    			"FROM groupings");
        groupingsId = maxGroupingsId.getInt(1) + 1;
        maxGroupingsId.close();
        
        ResultSet groupsIdMax = conn.createStatement().executeQuery(
        		"SELECT MAX(id)" + " " + 
        		"FROM groups");
        groupsId = groupsIdMax.getInt(1) + 1;
        groupsIdMax.close();
        
        ResultSet maxGroupsId = conn.createStatement().executeQuery(
    			"SELECT MAX(group_id)" + " " + 
    			"FROM groups_link");
    	groupsLinkId = maxGroupsId.getInt(1) + 1;
    	maxGroupsId.close();
    	
    	ResultSet maxKanjiId = conn.createStatement().executeQuery(
    			"SELECT MAX(id)" + " " + 
    			"FROM user_kanji_info");
        kanjiId = maxKanjiId.getInt(1) + 1;
    	maxKanjiId.close();
    }
    
    public void scanUserKanji() throws SQLException {
    	userKanjiCode = new ArrayList<Integer>();
    	ResultSet rs = conn.createStatement().executeQuery(
    			"SELECT kanji_code" + " " + 
    			"FROM user_kanji_info");
        while (rs.next()) {
        	userKanjiCode.add(rs.getInt("kanji_code"));
        }
        rs.close();
    	Collections.sort(userKanjiCode);
    	//System.out.println(userKanjiCode.toString());
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
}