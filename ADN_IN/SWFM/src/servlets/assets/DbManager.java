package servlets.assets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class DbManager {
	private String db_driver = "com.mysql.jdbc.Driver";
	private String db_name = "jdbc:mysql://localhost/SWFM_DB";
	private String username = "root";
	private String password = "root";
	Connection con;
	Statement st;  
	
	public DbManager() {}
	
	public DbManager(String d, String n, String usr, String pwd) {
		db_driver = d;
		db_name = n;
		username = usr;
		password = pwd;
	}
	
	private void openConnection() throws Exception {
		Class.forName(db_driver);
		con = DriverManager.getConnection(db_name, username, password);  
		st = con.createStatement();  
	}
	
	private void closeConnection() throws Exception {
		con.close(); 
	}
	
	private ResultSet executeQuery(String query) throws Exception { 
		return st.executeQuery(query);    
	}
	
	private int executeUpdate(String query) throws Exception {
		return st.executeUpdate(query);  
	}
	
	private String createColumns(Collection<String> data) {
		String keys = "";
		for (String k : data) {
			if(k != null) {
				keys = keys + " `" + k + "`,";
			}
		}
		
		keys = keys.substring(0, keys.length() - 1);
		
		return keys;
	}
	
	private String createValues(Collection<String[]> data) {
		String values = "";

		for (String[] v : data) {
			if(v[0] != null) {
				if(v[0].equals("on"))
					values = values + " '" + 1 + "',";
				else
					values = values + " '" + v[0] + "',";
			}
		}
		
		values = values.substring(0, values.length() - 1);
		
		return values;
	}
	
	public String encrypt_password(String pwd) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		
		md5.update(pwd.getBytes());
		byte[] bytes_password = md5.digest();
		StringBuffer encrypted_password = new StringBuffer();
		for (byte b : bytes_password)
			encrypted_password.append(String.format("%02x", b & 0xff));
		
		return encrypted_password.toString();
	}
	
	public boolean alreadyExist(String usr) throws Exception {
		String query = "SELECT * FROM `swfm_user` WHERE username = '" + usr + "'";
		
		openConnection();
		ResultSet rs = executeQuery(query);
		boolean exist = rs.first();
		closeConnection();

		if(exist)
			return true;

		return false;
	}
	
	public HashMap<String, Object> login(String usr, String pwd) throws Exception {
		String query = "SELECT * FROM `swfm_user` WHERE username = '" + usr + "' AND password= '" + pwd + "'";
		
		openConnection();
		ResultSet rs = executeQuery(query);
		
		if (!rs.first()) {
			closeConnection();
			return null;
		}
		
		HashMap<String, Object> user = new HashMap<String, Object>();
		user.put("email", rs.getString("email"));
		user.put("username", rs.getString("username"));
		user.put("name", rs.getString("name"));
		user.put("surname", rs.getString("surname"));
		user.put("avatar", rs.getString("avatar"));
		user.put("cover", rs.getString("cover"));
		user.put("admin", rs.getBoolean("admin"));
		
		closeConnection();
	
		return user;
	}
	
	public boolean signup(Map<String, String[]> d)  throws Exception {
		Map<String, String[]> data = new HashMap<String, String[]>(d);
		String [] encrypted_password = new String[1];
		encrypted_password[0] = encrypt_password(data.get("password")[0]);
		data.put("password", encrypted_password);
		String columns = createColumns(data.keySet());
		String values = createValues(data.values());
		
		String query = "INSERT INTO `swfm_user` (" + columns + ")" + " VALUES (" + values + ")";
		
		openConnection();
		executeUpdate(query);
		closeConnection();
		
		return true;
	}
}
