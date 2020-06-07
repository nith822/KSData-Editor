package ksdataEditor.Models;

public class Groups_Link {

	private int group_id;
	private int code; 
	private String sequence;
	private String date_added;
	
	public Groups_Link(int group_id, int code, String sequence) {
		setGroup_id(group_id);
		setCode(code);
		setSequence(sequence);
		this.date_added = "0";
	}
	
	public String toString() {
		String x = "group_id :: " + group_id + "\n"
				 + "code :: " + code + "\n"
				 + "sequence :: " + sequence + "\n"
				 + "date_added :: " + date_added + "\n";
		return x;
	}

	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDate_added() {
		return date_added;
	}

	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}
}
