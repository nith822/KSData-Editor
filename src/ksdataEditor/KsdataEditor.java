package ksdataEditor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import ksdataEditor.Models.Groups;
import ksdataEditor.Models.Groups_Link;

public class KsdataEditor {
	
	protected boolean addKnown;

	protected Connection conn;
	protected File ksdata;
	public File inputFile;
	
	protected int groupingsId;
	protected int groupsId;
	protected int groupsLinkId;
	
	protected int kanjiId;
	protected ArrayList<Integer> userKanjiCode;
	protected ArrayList<String> inputLines;
	
	protected GroupCreator gc;
	protected GroupEditor ge;
	
	private String encoding;

	protected Connection oldFileConn;
	protected Connection newFileConn;
	


	private static final String DEFAULT_CHARACTER_ENCODING = "ASCII";
	
	//args[0] ksdataFilePath 
	//args[1] inputFilePath
    public static void main(String[] args) throws Exception {
        KsdataEditor test;
        if(args.length == 4) {
        	test = new KsdataEditor(args[0], args[1], Boolean.parseBoolean(args[2]), args[3]);
        } else {
        	test = new KsdataEditor(args[0], args[1], Boolean.parseBoolean(args[2]), DEFAULT_CHARACTER_ENCODING);
        }
        test.ge.updateNotes();
        test.conn.close();
    }
    
    public KsdataEditor(String ksdataPath, String inputFile, boolean addKnown, String encoding) throws SQLException, IOException {
    	this.encoding = encoding;
    	this.addKnown = addKnown;
    	this.ksdata = new File(ksdataPath);
    	this.inputFile = new File(inputFile);
    	//this.oldFileConn = DriverManager.getConnection("jdbc:sqlite:" + "C:\\Users\\Harvey\\Desktop\\Sentence editor\\in.ksdata");
    	//this.newFileConn = DriverManager.getConnection("jdbc:sqlite:" + "C:\\Users\\Harvey\\Desktop\\Sentence editor\\out.ksdata");
    	
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
    }
    
    public void scanInputLines() throws IOException {
    	inputLines = new ArrayList<String>();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
    	String line;
    	while((line = reader.readLine()) != null) {
    		if(!line.isEmpty()) {
    			inputLines.add(line);
    		}
    	}
    	reader.close();
    }
}