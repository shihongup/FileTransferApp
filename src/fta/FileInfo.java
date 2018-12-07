package fta;

public class FileInfo implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	FileInfo(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
}