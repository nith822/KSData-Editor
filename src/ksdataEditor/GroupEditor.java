package ksdataEditor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import ksdataEditor.Models.Groups_Link;

public class GroupEditor {

	KsdataEditor editor;
	
	public GroupEditor(KsdataEditor e) {
		editor = e;
	}
	
	public void updateNotes() throws SQLException {
    	for(String line : editor.inputLines) {
    		for(Character c: line.toCharArray()) {
    			if(isKanji(c)) {
    				if(editor.addKnown) {
    					editor.gc.addGroupsLink(c);
    		    		System.out.println(c + " :: " + line);
    				} else {
    					if(!(Collections.binarySearch(editor.userKanjiCode, (int)c) >= 0)) {
        					editor.gc.addGroupsLink(c);
    					}
    				}
    				int arrayListIndex;
    				if((arrayListIndex = Collections.binarySearch(editor.userKanjiCode, (int)c)) >= 0) {
    					int kanji_code = editor.userKanjiCode.get(arrayListIndex);
    					PreparedStatement pstmt = null;   
    			         try {
    			             pstmt = editor.conn.prepareStatement(
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
    					addKanji(c, editor.kanjiId++, line);
    				}
    			}
    		}
    	}
    }
    
    public void addKanji(Character c, int kanjiId, String line) throws SQLException {
    	editor.userKanjiCode.add((int)c);
    	Collections.sort(editor.userKanjiCode);
    	PreparedStatement pstmt = null;   
    	try {
    		pstmt = editor.conn.prepareStatement(
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
    
    public boolean isKanji(Character c) {
    	 return (c>=0x4e00 && c<0xa000);
    }
}
