package ksdataEditor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ksdataEditor.Models.Groups;
import ksdataEditor.Models.Groups_Link;

public class GroupCreator {

	private static final String DISPLAY_STROKE_PATHS = ("M18.38,46.36c2.37,0.64,5.38,0.73,7.74,0.47c14.39-1.58,36.51-4.46,51.25-5.75c2.51-0.22,6-0.33,7.89,0.42|M50.25,15.75c1,1.08,1.61,2.16,1.74,4.32C53.75,48.25,46.5,79.75,17,92.25|M51.5,45c8.29,11.97,23.78,31.58,35.16,41.85c2.37,2.14,4.59,4.15,8.09,5.4|M67.33,20.25c6.9,3.89,8.78,6.85,10.92,10.5");
	private static final String CREATED_AT = "1489261845162";
	private static final int GROUP_TYPE = 0;
	
	KsdataEditor editor;

	protected List<Groups>groups;
	protected List<Groups_Link>groups_link;
	
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
                        "VALUES (?,?,?,?,?)");

            pstmt.setInt(1, editor.groupingsId);
            pstmt.setInt(2, GROUP_TYPE);
            pstmt.setString(3, name);
            pstmt.setInt(4, 0);
            pstmt.setString(5, CREATED_AT);
            pstmt.executeUpdate();
            pstmt.close();

    		System.out.println("Added into grouping " + editor.groupingsId + "::" + name);
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

    		System.out.println("Added into groupings " + editor.groupsId + "::" + editor.groupingsId);
    	}	finally {
    		if (pstmt != null) pstmt.close();
    	}
    }
	
	//inits all groupLink
	public void setGroups_link() {
		groups_link = new ArrayList<Groups_Link>();
		PreparedStatement pstmt = null;   
        try {
        	ResultSet groupsLinkData = editor.oldFileConn.createStatement().executeQuery(
        			"SELECT *" + " " + 
        			"FROM groups_link");
        	while(groupsLinkData.next()) {
        		Groups_Link currentGroupLink = new Groups_Link(groupsLinkData.getInt("group_id"), groupsLinkData.getInt("code"), groupsLinkData.getString("sequence"));
        		groups_link.add(currentGroupLink);
        	}
            groupsLinkData.close();
        } catch(SQLException e) {
        	System.out.println("Could not read from oldFile");
        	e.printStackTrace();
        }
	}
	
	//writes all GroupLink
	public void writeGroupsLink() {
		PreparedStatement pstmt = null;   
        try {
        	for(Groups_Link groups_linkData: groups_link) {
        		try{
        			pstmt = editor.newFileConn.prepareStatement(
        					"INSERT INTO groups_link(group_id, code, sequence, date_added)" + " " +
        							"SELECT ?,?,?,?" + " ");
        			pstmt.setInt(1, groups_linkData.getGroup_id());
        			pstmt.setInt(2, groups_linkData.getCode());
        			pstmt.setString(3, groups_linkData.getSequence());
        			pstmt.setString(4, groups_linkData.getDate_added());
        			pstmt.executeUpdate();
        		} catch (SQLException e) {
        			
        		}
        	}
        	pstmt.close();
        } catch (SQLException e) {
        	System.out.println("Could not write to new file");
        	e.printStackTrace();
        }
	}
	
	//insert one
	public void addGroupsLink(Character c) {
		PreparedStatement pstmt = null;   
        try {
        	try{
        		pstmt = editor.conn.prepareStatement(
        				"INSERT INTO groups_link(group_id, code, sequence, date_added)" + " " +
        							"SELECT ?,?,?,?" + " ");
        		pstmt.setInt(1, editor.groupsId);
        		pstmt.setInt(2, (int)c);
        		pstmt.setString(3, "0");
        		pstmt.setString(4, "0");
        		pstmt.executeUpdate();
        		System.out.println("Added into groupsLink " + editor.groupsId + "::" + (int)c);
        	} catch (SQLException e) {
        			
        	}
        	pstmt.close();
        } catch (SQLException e) {
        	System.out.println("Could not write to new file");
        	e.printStackTrace();
        }
	}
	
	//inits all Groups
	public void setGroups() {
		groups = new ArrayList<Groups>();
		PreparedStatement pstmt = null;   
        try {
        	ResultSet groupsData = editor.oldFileConn.createStatement().executeQuery(
        			"SELECT *" + " " + 
        			"FROM groups");
        	while(groupsData.next()) {
        		Groups currentGroup = new Groups(groupsData.getInt("id"), 
        				groupsData.getInt("level"),
        				groupsData.getInt("level_mode"),
        				groupsData.getInt("type"),
        				groupsData.getInt("position"),
        				groupsData.getInt("display_code"),
        				groupsData.getString("display_stroke_paths"),
        				groupsData.getString("last_studied_at"),
        				groupsData.getInt("grouping_id"));
        		//System.out.println(currentGroup);
        		groups.add(currentGroup);
        	}
            groupsData.close();
        } catch(SQLException e) {
        	System.out.println("Could not read from oldFile");
        	e.printStackTrace();
        }
	}
	
	//writes all Groups
	public void writeGroups() {
		PreparedStatement pstmt = null;   
        try {
        	for(Groups groupsData: groups) {
        		try{
        			pstmt = editor.newFileConn.prepareStatement(
        					"INSERT INTO groups(id, level, level_mode, type, position, display_code, display_stroke_paths, last_studied_at, grouping_id)" + " " +
        					"SELECT ?,?,?,?,?,?,?,?,?");
        			pstmt.setInt(1, groupsData.getId());
        			pstmt.setInt(2, groupsData.getLevel());
        			pstmt.setInt(3, groupsData.getLevel_mode());
        			pstmt.setInt(4, groupsData.getType());
        			pstmt.setInt(5, groupsData.getPosition());
        			pstmt.setInt(6, groupsData.getDisplay_code());
        			pstmt.setString(7, groupsData.getDisplay_stroke_paths());
        			pstmt.setString(8, groupsData.getLast_studied_at());
        			pstmt.setInt(9, groupsData.getGroupingId());
        			pstmt.executeUpdate();
        		} catch (SQLException e) {
        			
        		}
        	}
            pstmt.close();
        } catch (SQLException e) {
        	System.out.println("Could not write to new file");
        	e.printStackTrace();
        }
	}
	
}
