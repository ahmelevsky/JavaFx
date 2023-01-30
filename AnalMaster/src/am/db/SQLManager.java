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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import am.Main;
import am.ShutterImage;

public class SQLManager {

	public final String fileLocation = System.getProperty("user.home") + File.separator + "AnalMaster_1.db"; 
	private final Logger LOGGER;
	public Main app;
	public Connection connection;
	public final String IMAGESTOPTABLE = "imagestopdata";
	public final String IMAGESTABLE = "imagesdata";
	public final String KEYSTABLE = "keysdata";
	
	
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
          String sql = "CREATE TABLE IF NOT EXISTS " + this.IMAGESTOPTABLE + " (\n"
                  + "	media_id integer PRIMARY KEY,\n"
                  + "	downloads integer,\n"
                  + "	earnings real,\n"
                  + "	image_url text NOT NULL,\n"
                  + "   FOREIGN KEY (media_id)"
                  + "	REFERENCES " + this.IMAGESTABLE + " (media_id) "
                  + ");";
          statement.execute(sql);
          
          
          sql = "CREATE TABLE IF NOT EXISTS " + this.IMAGESTABLE + " (\n"
                  + "	media_id integer PRIMARY KEY,\n"
                  + "	original_filename text,\n"
                  + "	uploaded_date text,\n"
                  + "	upload_id integer,\n"
                  + "	preview_path text,\n"
                  + "	imagebinary blob,\n"
                  + "	type text,\n"
                  + "	description text,\n"
                  + "	is_illustration integer,\n"
                  + "	keywords text\n"
                  + ");";
          statement.execute(sql);
          
          sql = "CREATE TABLE IF NOT EXISTS " + this.KEYSTABLE + " (\n"
                  + "	media_id integer,\n"
                  + "	keyword text,\n"
                  + "	rate integer,\n"
                  + "	PRIMARY KEY (media_id, keyword),\n"
                  + "   FOREIGN KEY (media_id)"
                  + "	REFERENCES " + this.IMAGESTABLE + " (media_id) "
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
	 
	

	 public void insertNewImages(List<ShutterImage> images) {
		   String sql = "INSERT INTO " + this.IMAGESTABLE + "(media_id) VALUES(?)";
		   int i = 0;

	        try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        	for(ShutterImage image:images) {
	        	
	        		pstmt.setLong(1, image.getMedia_id());
	        		pstmt.addBatch();
	        		
	        		i++;
	                if (i % 100 == 0 || i == images.size()) {
	                	   pstmt.executeBatch(); // Execute every 100 items.
	                   }
	        	}
	        }catch(SQLException e){ 
	        	System.out.println(e.getMessage());
	        	LOGGER.severe(e.getMessage());
	        	}
	    }
	
	 
	 
	 public void insertImageData(ShutterImage image) {
		 
		  String sql = "UPDATE "+ this.IMAGESTABLE + " SET original_filename = ? , "
				    + "uploaded_date = ? , "
				    + "upload_id = ? , "
				    + "preview_path = ? , "
				    + "imagebinary = ? , "
				    + "type = ? , "
				    + "description = ? , "
				    + "is_illustration = ? , "
	                + "keywords = ? "
	                + "WHERE media_id = ?";
		  
		  try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        		pstmt.setString(1, image.getOriginal_filename());
	        		pstmt.setString(2, image.getUploaded_date());
	        		pstmt.setLong(3, image.getUpload_id());
	        		pstmt.setString(4, image.getPreviewPath());
	        		pstmt.setBytes(5, image.getPreviewBytes());
	        		pstmt.setString(6, image.getType());
	        		pstmt.setString(7, image.getDescription());
	        		if (image.getIs_illustration())
	        			pstmt.setInt(8, 1);
	        		else
	        			pstmt.setInt(8, 0);
	        		pstmt.setString(9, String.join(",", image.keywords));
	        		pstmt.setLong(10, image.getMedia_id());
	        		pstmt.addBatch();
	                pstmt.executeBatch();
	        }catch(SQLException e){ 
	        	System.out.println(e.getMessage());
	        	LOGGER.severe(e.getMessage());
	        	}
		 
		 
	 }
	 
	 
	 
	 public void insertImagesData(List<ShutterImage> images) {
		 
		  String sql = "UPDATE "+ this.IMAGESTABLE + " SET original_filename = ? , "
				    + "uploaded_date = ? , "
				    + "upload_id = ? , "
				    + "preview_path = ? , "
				    + "imagebinary = ? , "
				    + "type = ? , "
				    + "description = ? , "
				    + "is_illustration = ? , "
	                + "keywords = ? "
	                + "WHERE media_id = ?";
		  
		  try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        	for(ShutterImage image:images) {
	        		pstmt.setString(1, image.getOriginal_filename());
	        		pstmt.setString(2, image.getUploaded_date());
	        		pstmt.setLong(3, image.getUpload_id());
	        		pstmt.setString(4, image.getPreviewPath());
	        		pstmt.setBytes(5, image.getPreviewBytes());
	        		pstmt.setString(6, image.getType());
	        		pstmt.setString(7, image.getDescription());
	        		if (image.getIs_illustration())
	        			pstmt.setInt(8, 1);
	        		else
	        			pstmt.setInt(8, 0);
	        		pstmt.setString(9, String.join(",", image.keywords));
	        		pstmt.setLong(10, image.getMedia_id());
	        		pstmt.addBatch();
	                pstmt.executeBatch();
	                   }
	        }catch(SQLException e){ 
	        	System.out.println(e.getMessage());
	        	LOGGER.severe(e.getMessage());
	        	}
		 
		 
	 }
	

	 public void insertImages(List<ShutterImage> images) {
		   String sql = "INSERT INTO " + this.IMAGESTABLE + "(media_id,original_filename,uploaded_date,upload_id,preview_path,imagebinary,type,description,is_illustration,keywords) VALUES(?,?,?,?,?,?,?,?,?,?)";
		   int i = 0;

	        try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        	for(ShutterImage image:images) {
	        	
	        		pstmt.setLong(1, image.getMedia_id());
	        		pstmt.setString(2, image.getOriginal_filename());
	        		pstmt.setString(3, image.getUploaded_date());
	        		pstmt.setLong(4, image.getUpload_id());
	        		pstmt.setString(5, image.getPreviewPath());
	        		pstmt.setBytes(6, image.getPreviewBytes());
	        		pstmt.setString(7, image.getType());
	        		pstmt.setString(8, image.getDescription());
	        		if (image.getIs_illustration())
	        			pstmt.setInt(9, 1);
	        		else
	        			pstmt.setInt(9, 0);
	        		pstmt.setString(10, String.join(",", image.keywords));
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
	    
	 
	
	 public void insertImagesTop(List<ShutterImage> images) {
		   String sql = "INSERT INTO " + this.IMAGESTOPTABLE + "(media_id,downloads,earnings,image_url) VALUES(?,?,?,?)";
		   int i = 0;

	        try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        	for(ShutterImage image:images) {
	        	
	        		pstmt.setLong(1, image.getMedia_id());
	        		pstmt.setInt(2, image.getDownloads());
	        		pstmt.setDouble(3, image.getEarnings());
	        		pstmt.setString(4, image.getImage_url());
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
	 
	 
	 
	 public void insertKeywords(long media_id, Map<String,Integer> keywords) {
		   String sql = "INSERT INTO " + this.KEYSTABLE + "(media_id,keyword,rate) VALUES(?,?,?)";

	        try {
	        	PreparedStatement pstmt = this.connection.prepareStatement(sql);
	        	  for (Map.Entry<String, Integer> entry : keywords.entrySet()) {
	        	
	        		pstmt.setLong(1, media_id);
	        		pstmt.setString(2, entry.getKey());
	        		pstmt.setInt(3, entry.getValue());
	        		pstmt.addBatch();
	        	  }
	            
	        	  pstmt.executeBatch(); 
	        	
	        }catch(SQLException e){ 
	        	System.out.println(e.getMessage());
	        	LOGGER.severe(e.getMessage());
	        	}
	    }
	 
	 
	    
	public  Map<String,Integer> getKeywordsMapForImage(long media_id) {
		Map<String,Integer> keywords = new HashMap<String,Integer>();
		String sql = "SELECT * FROM " + this.KEYSTABLE + " WHERE media_id=" + media_id;
		  try {
		 PreparedStatement pstmt  = this.connection.prepareStatement(sql);
		 ResultSet rs  = pstmt.executeQuery();
		 while (rs.next()) {
			 keywords.put(rs.getString("keyword"), rs.getInt("rate"));
            }
		   }catch(SQLException e){ 
	        	System.out.println(e.getMessage());
	        	LOGGER.severe(e.getMessage());
	        	}
		return keywords;
	}
	
	 
	 
	 
	
	public List<ShutterImage> getImagesFromDB(String sql){
		List<ShutterImage> images = new ArrayList<ShutterImage>();
		 try {
			 PreparedStatement pstmt  = this.connection.prepareStatement(sql);
			 ResultSet rs  = pstmt.executeQuery();
			 while (rs.next()) {
				 ShutterImage image = new ShutterImage(rs.getInt("media_id"));
				 image.setOriginal_filename(rs.getString("original_filename"));
				 image.setUploaded_date(rs.getString("uploaded_date"));
				 image.setUpload_id(rs.getLong("upload_id"));
				 image.setPreviewPath(rs.getString("preview_path"));
				 image.setPreviewBytes(rs.getBytes("imagebinary"));
				 image.setType(rs.getString("type"));
				 image.setDescription(rs.getString("description"));
				 image.keywords.addAll(rs.getString("keywords").split(","));
				 image.setIs_illustration(rs.getInt("is_illustration")==1);
				 image.setImage();
				 images.add(image);
				 image.setDownloads(rs.getInt("downloads"));
				 image.setEarnings(rs.getDouble("earnings"));
				 image.setImage_url(rs.getString("image_url"));
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
			 String sql = "SELECT MAX(verdict_time) FROM " + this.IMAGESTABLE;
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
			 String sql = "SELECT EXISTS(SELECT 1 FROM " + this.IMAGESTABLE + " WHERE media_id=" + id +");";
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
	 
	 public List<Integer> getEmptyImages(){
		 List<Integer> list = new ArrayList<Integer>();
		 
		 try {
			 Statement statement = this.connection.createStatement();
			 statement.setQueryTimeout(1);
			 String sql = "SELECT media_id FROM imagesdata WHERE upload_id is NULL";
			 ResultSet rs =  statement.executeQuery(sql);
			 
			 while (rs.next()) {
				list.add(rs.getInt("media_id"));
	            }
		 }
		 catch(SQLException e){ 
			 System.out.println(e.getMessage());
     		 LOGGER.severe(e.getMessage());
     	}
		Collections.reverse(list);
		return list;
	 }
	 
	 
	 
	 
	 public boolean isImageHasData(long id) {
		 try {
			 Statement statement = this.connection.createStatement();
			 statement.setQueryTimeout(1);
			 String sql = "SELECT EXISTS(SELECT 1 FROM " + this.IMAGESTABLE + " WHERE media_id=" + id +" AND upload_id is not NULL);";
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
