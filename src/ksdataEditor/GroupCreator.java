package ksdataEditor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GroupCreator {

	private static final String DISPLAY_STROKE_PATHS = ("M18.38,46.36c2.37,0.64,5.38,0.73,7.74,0.47c14.39-1.58,36.51-4.46,51.25-5.75c2.51-0.22,6-0.33,7.89,0.42|M50.25,15.75c1,1.08,1.61,2.16,1.74,4.32C53.75,48.25,46.5,79.75,17,92.25|M51.5,45c8.29,11.97,23.78,31.58,35.16,41.85c2.37,2.14,4.59,4.15,8.09,5.4|M67.33,20.25c6.9,3.89,8.78,6.85,10.92,10.5");
	private static final String CREATED_AT = "1489261845162";
	private static final int GROUP_TYPE = 0;
	
	KsdataEditor editor;
	
	public GroupCreator(KsdataEditor e) throws SQLException {
		editor = e;
    	addGrouping();
        addGroup();
	}
	
	public void addGrouping() throws SQLException {
    	String name = editor.inputFile.getName();
        PreparedStatement pstmt = null;  
        try {
            pstmt = editor.conn.prepareStatement(
                        "INSERT INTO groupings" + " " +
                        "VALUES (?,?,?,?)");

            pstmt.setInt(1, editor.groupingsId);
            pstmt.setInt(2, GROUP_TYPE);
            pstmt.setString(3, name);
            pstmt.setString(4, CREATED_AT);
            pstmt.executeUpdate();
            pstmt.close();
        }	finally {
            if (pstmt != null) pstmt.close();
        }
    }
	
	public void addGroup() throws SQLException {
    	PreparedStatement pstmt = null;   
    	try {
    		pstmt = editor.conn.prepareStatement(
    				"INSERT INTO groups" + " " +
    				"VALUES(?,0,0,0,0,21516,?,0,?)");

    		pstmt.setInt(1, editor.groupsId);
    		pstmt.setString(2, DISPLAY_STROKE_PATHS);
    		pstmt.setInt(3, editor.groupingsId);
    		pstmt.executeUpdate();
    		pstmt.close();
    	}	finally {
    		if (pstmt != null) pstmt.close();
    	}
    }
}
