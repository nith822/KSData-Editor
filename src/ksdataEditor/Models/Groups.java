package ksdataEditor.Models;

public class Groups {

	private int id;
	private int level;
	private int level_mode;
	private int type;
	private int position;
	private int display_code;
	private String display_stroke_paths;
	private String last_studied_at;
	private int groupingId;
	
	public Groups(int id, int level, int level_mode, int type, int position, int display_code, 
			String display_stroke_paths, String last_studied_at, int groupingId) {
			
			setId(id);
			setLevel(level);
			setLevel_mode(level_mode);
			setType(type);
			setPosition(position);
			setDisplay_code(display_code);
			setDisplay_stroke_paths(display_stroke_paths);
			setLast_studied_at(last_studied_at);
			setGroupingId(groupingId);
		
	}
	
	public String toString() {
		String x = "id :: " + id + "\n"
				 + "level  :: " + level + "\n"
				 + "level_mode :: " + level_mode + "\n"
				 + "type :: " + type + "\n"
				 + "position :: " + position + "\n"
				 + "display_code :: " + display_code + "\n"
				 + "display_stroke_paths :: " + display_stroke_paths + "\n"
				 + "last_studied_at :: " + last_studied_at + "\n" 
				 + "groupingId :: " + groupingId + "\n";
		return x;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel_mode() {
		return level_mode;
	}

	public void setLevel_mode(int level_mode) {
		this.level_mode = level_mode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getDisplay_code() {
		return display_code;
	}

	public void setDisplay_code(int display_code) {
		this.display_code = display_code;
	}

	public String getDisplay_stroke_paths() {
		return display_stroke_paths;
	}

	public void setDisplay_stroke_paths(String display_stroke_paths) {
		this.display_stroke_paths = display_stroke_paths;
	}

	public String getLast_studied_at() {
		return last_studied_at;
	}

	public void setLast_studied_at(String last_studied_at) {
		this.last_studied_at = last_studied_at;
	}

	public int getGroupingId() {
		return groupingId;
	}

	public void setGroupingId(int groupingId) {
		this.groupingId = groupingId;
	}
}
