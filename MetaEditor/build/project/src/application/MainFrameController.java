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

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressBar;
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
	
	@FXML
	private ProgressBar progress;
	
	private Task<Integer> task;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
			
	}
	
	public void addTab(String tabTitle, Node node){
		tabs.getTabs().add(new Tab(tabTitle, node));
	}
	
	
	@FXML
	public void writeMetadata() throws SAXException, ParserConfigurationException, TransformerException{
		
		
		if (folder==null && !folder.exists()){
			app.showAlert("����� �� ������� ��� �� ����������");
			return;
		}
    	File[] images =	folder.listFiles(new FileFilter() {
			        public boolean accept(File f) {
			        	 return f.isFile() && f.getName().toLowerCase().endsWith(".jpg");
			        }
			    });
			
			if (images.length==0) {
				app.showAlert("� �������� ����� ��� ������ .jpg");
				return;
			}
			
		app.keysEditorController.saveKeywordsSource();
		app.descriptionEditorController.add();
		app.descriptionEditorController.saveDescriptionsSource();
		app.titleEditorController.saveTitleSource();
		
       		
		 task = new Task<Integer>() {
			    @Override public Integer call() {
			    	int done = 0;
			       try{ 
			    	for (File image:images){
						MetadataWriter.writeMetadataToFile (image, app.keysEditorController.generateKeywordsForMetadata(), app.titleEditorController.getTitleForMetadata(), app.descriptionEditorController.generateRandomDescriptionForMetadata());
						done++;
						updateProgress(done, images.length);
						}
					
						
						} catch (ImageReadException e) {
							app.showAlert("���������� ��������� ����, ������: " + e.getMessage());
						} catch (ImageWriteException e) {
							app.showAlert("���������� �������� ����, ������: " + e.getMessage());
						} catch (IOException e) {
							app.showAlert(e.getMessage());
						} catch (SAXException e) {
							app.showAlert(e.getMessage());
						} catch (ParserConfigurationException e) {
							app.showAlert(e.getMessage());
						} catch (TransformerException e) {
							app.showAlert(e.getMessage());
						}
			        return done;
			    }
			};
			
			task.setOnFailed(e -> {
				writeBtn.setDisable(false);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("������");
				alert.setHeaderText("������");
				alert.setContentText("������ ���������� ������ ���������");
				alert.showAndWait();
			});
			
			task.setOnCancelled(e -> {
				writeBtn.setDisable(false);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("������");
				alert.setHeaderText("������");
				alert.setContentText("������ ���������� ���� ��������");
				alert.showAndWait();
			});
			
			task.setOnSucceeded(e -> {
				writeBtn.setDisable(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Done!");
				alert.setHeaderText("Done!");
				alert.setContentText("���������� ������� ��������, ���������� ������: " + task.getValue());
				alert.showAndWait();
			});
			
	      progress.progressProperty().bind(task.progressProperty());
	     
	    writeBtn.setDisable(true);
		new Thread(task).start();
			
	}
	
	@FXML
    private void selectPath(){
		  DirectoryChooser directoryChooser = new DirectoryChooser(); 

          directoryChooser.setTitle("�������� �������� ����� ��� ������");
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
