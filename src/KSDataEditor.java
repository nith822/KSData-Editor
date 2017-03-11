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
	
	Connection conn;
	File ksdata;
	File inputFile;
	ArrayList<Integer> userKanjiCode;
	ArrayList<String> inputLines;
	
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
    }
    
    public void update(String note, int kanji_code) throws SQLException {
    	 PreparedStatement pstmt = null;   
         try {
             pstmt = conn.prepareStatement(
                         "UPDATE user_kanji_info" + " " +
                         "SET notes = ?" + " " +
                         "WHERE kanji_code = ?");

             pstmt.setString(1, note);
             pstmt.setInt(2, kanji_code);
             pstmt.executeUpdate();
             pstmt.close();
         }	finally {
             if (pstmt != null) pstmt.close();
         }
    }
    
    public boolean isKanji(Character c) {
    	 return (c>=0x4e00 && c<0xa000);
    }
    
    public void scanUserKanji() throws SQLException {
    	userKanjiCode = new ArrayList<Integer>();
    	//don't overwrite existing sentences
    	ResultSet rs = conn.createStatement().executeQuery("SELECT kanji_code" + " " + "FROM user_kanji_info" + " " + "WHERE notes IS NULL");
        while (rs.next()) {
        	userKanjiCode.add(rs.getInt("kanji_code"));
        }
    	Collections.sort(userKanjiCode);
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
    	for(String line : inputLines) {
    		for(Character c: line.toCharArray()) {
    			if(isKanji(c)) {
    				int arrayListIndex;
    				if((arrayListIndex = Collections.binarySearch(userKanjiCode, (int)c)) >= 0) {
    					int kanji_code = userKanjiCode.get(arrayListIndex);
    					update(line, kanji_code);
    					userKanjiCode.remove(arrayListIndex);
    				}
    			}
    		}
    	}
    }
  }