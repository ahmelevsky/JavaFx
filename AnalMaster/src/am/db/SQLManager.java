package am.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import am.Main;
import am.ShutterImage;

public class SQLManager {

	public final String fileLocation = System.getProperty("user.home") + File.separator + "SubmitMaster.db"; 
	private final Logger LOGGER;
	public Main app;
	public Connection connection;
	public final String TABLENAME = "imagesdata";
	
	public SQLManager(Main application) {
		this.app = application;
		LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        try  {
        	this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.fileLocation);
            if (this.connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("Connection success.");
                LOGGER.fine("The driver name is " + meta.getDriverName());
                LOGGER.fine("Connection success.");
                createTables();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            LOGGER.severe(e.getMessage());
            this.app.showAlert("Can't connect database: " + e.getMessage());
        }
		
	}

	private void createTables(){
		 try  {
		  Statement statement = this.connection.createStatement();
          statement.setQueryTimeout(1); 
          String sql = "CREATE TABLE IF NOT EXISTS " + this.TABLENAME + " (\n"
                  + "	media_id integer PRIMARY KEY,\n"
                  + "	upload_id integer,\n"
                  + "	media_type text,\n"
                  + "	status text NOT NULL,\n"
                  + "	type text NOT NULL,\n"
                  + "	reasons text,\n"
                  + "	uploaded_date text NOT NULL,\n"
                  + "	verdict_time text NOT NULL,\n"
                  + "	original_filename text NOT NULL,\n"
                  + "	previewPath text NOT NULL,\n"
                  + "	imagebinary blob,\n"
                  + "	description text NOT NULL,\n"
                  + "	submitter_note text NOT NULL,\n"
                  + "	has_property_release integer,\n"
                  + "	is_illustration integer,\n"
                  + "	keywords text\n"
                  + ");";
          statement.execute(sql);
          
		 } catch (SQLException e) {
	            System.out.println(e.getMessage());
	            LOGGER.severe(e.getMessage());
	        }
	}
	
	
	private boolean tableExists(String tableName){
        try{
            DatabaseMetaData md = this.connection.getMetaData();
            ResultSet rs = md.getTables(null, null, tableName, null);
            rs.last();
             return rs.getRow() > 0;
        }catch(SQLException e){ 
        	System.out.println(e.getMessage());
        	LOGGER.severe(e.getMessage());
        	}
        return false;
    }
	
	
	/*
	 public void insert(List<ShutterImage> images) {
		   String sql = "INSERT INTO " + this.TABLENAME + "(media_id,upload_id,media_type,status,type,reasons,uploaded_date,verdict_time,"
		   		+ "original_filename,previewPath,imagebinary,description,submitter_note,has_property_release,is_illustration,keywords) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		   int i = 0;

	        try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        	for(ShutterImage image:images) {
	        	
	        		pstmt.setLong(1, image.getMedia_id());
	        		pstmt.setLong(2, image.getUpload_id());
	        		pstmt.setString(3, image.getMedia_type());
	        		pstmt.setString(4, image.getStatus());
	        		pstmt.setString(5, image.getType());
	        		pstmt.setString(6, image.getReasonsString());
	        		pstmt.setString(7, image.getUploaded_date());
	        		pstmt.setString(8, image.getVerdict_time());
	        		pstmt.setString(9, image.getOriginal_filename());
	        		pstmt.setString(10, image.getPreviewPath());
	        		pstmt.setBytes(11, image.getPreviewBytes());
	        		pstmt.setString(12, image.getDescription());
	        		pstmt.setString(13, image.getSubmitter_note());
	        		if (image.getHas_property_release())
	        			pstmt.setInt(14, 1);
	        		else
	        			pstmt.setInt(14, 0);
	        		if (image.getIs_illustration())
	        			pstmt.setInt(15, 1);
	        		else
	        			pstmt.setInt(15, 0);
	        		pstmt.setString(16, String.join(",", image.keywords));
	        		pstmt.addBatch();
	        		
	        		i++;
	                if (i % 100 == 0 || i == images.size()) {
	                	   pstmt.executeBatch(); // Execute every 1000 items.
	                   }
	        	}
	        }catch(SQLException e){ 
	        	System.out.println(e.getMessage());
	        	LOGGER.severe(e.getMessage());
	        	}
	    }
	    
	    
	    */
	
	
	/*
	public List<ShutterImage> getImagesFromDB(String sql){
		List<ShutterImage> images = new ArrayList<ShutterImage>();
		// String sql = "SELECT * FROM " + this.TABLENAME + " ORDER BY media_id DESC LIMIT " + limit;
		 try {
			 PreparedStatement pstmt  = this.connection.prepareStatement(sql);
			 ResultSet rs  = pstmt.executeQuery();
			 while (rs.next()) {
				 ShutterImage image = new ShutterImage(rs.getInt("media_id"), rs.getString("original_filename"), 
						 rs.getString("uploaded_date"), rs.getString("verdict_time"));
				 image.setDescription(rs.getString("description"));
				 image.setStatus(rs.getString("status"));
				 image.keywords.addAll(rs.getString("keywords").split(","));
				 image.setPreviewBytes(rs.getBytes("imagebinary"));
				 image.setReasonsString(rs.getString("reasons"));
				 image.setImage();
				 images.add(image);
	            }
			 Collections.reverse(images);
			 return images;
		 }
		 
		 catch(SQLException e){ 
			 System.out.println(e.getMessage());
     		LOGGER.severe(e.getMessage());
     		return null;
     	}
	}
	
	*/
	
	public int getImagesCount(String sql){
		 try {
			 Statement statement = this.connection.createStatement();
			 statement.setQueryTimeout(1);
			 ResultSet rs =  statement.executeQuery(sql);
			 return rs.getInt(1);
		 }
		 
		 catch(SQLException e){ 
			 System.out.println(e.getMessage());
     		LOGGER.severe(e.getMessage());
     		return 0;
     	}
	}
	
	 
	 public LocalDateTime getLastUpdateDate() {
		 try {
			 Statement statement = this.connection.createStatement();
			 statement.setQueryTimeout(1);
			 String sql = "SELECT MAX(verdict_time) FROM " + this.TABLENAME;
			 ResultSet rs =  statement.executeQuery(sql);
			 String value = rs.getString(1);
			 DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			 return LocalDateTime.parse(value, formatterTime);
		 }
		 catch(SQLException e){ 
			 System.out.println(e.getMessage());
     		LOGGER.severe(e.getMessage());
     		return null;
     	}
	 }
	 
	 public boolean isInDB(long id) {
		 try {
			 Statement statement = this.connection.createStatement();
			 statement.setQueryTimeout(1);
			 String sql = "SELECT EXISTS(SELECT 1 FROM " + this.TABLENAME + " WHERE media_id=" + id +");";
			 ResultSet rs =  statement.executeQuery(sql);
			 if (rs.getInt(1)==1) return true;
			 else return false;
		 }
		 catch(SQLException e){ 
			 System.out.println(e.getMessage());
     		LOGGER.severe(e.getMessage());
     		return false;
     	}
	 }
	 
	 
	 public byte[] readPicture(ResultSet rs, String field) throws IOException, SQLException {

	        	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	                InputStream input = rs.getBinaryStream("picture");
	                byte[] buffer = new byte[1024];
	                while (input.read(buffer) > 0) {
	                	bos.write(buffer);
	                }
	                return bos.toByteArray();
	 }
	 
}
