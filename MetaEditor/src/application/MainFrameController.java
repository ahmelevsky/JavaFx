package application;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcBlock;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import org.apache.commons.imaging.formats.jpeg.iptc.JpegIptcRewriter;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegRewriter;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;

public class MainFrameController implements Initializable{

	public Main app;
	private File folder;
	
	@FXML
	private Button writeBtn;
	@FXML
	private Hyperlink folderPath;
	
	
	@FXML
	private TabPane tabs;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void addTab(String tabTitle, Node node){
		tabs.getTabs().add(new Tab(tabTitle, node));
	}
	@FXML
	public void writeMetadata() throws SAXException, ParserConfigurationException, TransformerException{
		int done = 0;
		if (folder!=null && folder.exists()){
		File[] images=	folder.listFiles(new FileFilter() {
		        public boolean accept(File f) {
		        	 return f.isFile() && f.getName().toLowerCase().endsWith(".jpg");
		        }
		    });
		
		if (images.length==0)
			app.showAlert("В заданной папке нет файлов .jpg");
		else{
			try{
				
			for (File image:images){
			MetadataWriter.writeMetadataToFile (image, app.keysEditorController.generateKeywords(), app.titleEditorController.getTitle(), app.descriptionEditorController.generateRandomDescription());
			done++;
			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Done!");
			alert.setHeaderText("Done!");
			alert.setContentText("Метаданные успешно записаны, количество файлов: " + done);
			alert.showAndWait();
			
			} catch (ImageReadException e) {
				app.showAlert("Невозможно прочитать файл, ошибка: " + e.getMessage());
			} catch (ImageWriteException e) {
				app.showAlert("Невозможно записать файл, ошибка: " + e.getMessage());
			} catch (IOException e) {
				app.showAlert(e.getMessage());
			}
		}
		
		}
		else{
			app.showAlert("Папка не выбрана или не существует");
		}
			
	}
	
	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle("Выберите корневую папку для батчей");
          File selected = new File(this.folderPath.getText());
          if (selected.exists())
        	  directoryChooser.setInitialDirectory(selected.getParentFile());

          File file = directoryChooser.showDialog(app.getPrimaryStage());

         if(file!=null){
        	 this.folder = file;
        	 this.folderPath.setText(file.getAbsolutePath());
         }
	}
	
	
	



}
