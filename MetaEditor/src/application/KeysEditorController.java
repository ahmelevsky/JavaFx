package application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.jpeg.iptc.JpegIptcRewriter;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcBlock;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import org.apache.commons.imaging.formats.jpeg.iptc.JpegIptcRewriter;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;


public class KeysEditorController implements Initializable {

	@FXML
	private TextArea obligatoryText;
	
	@FXML
	private TextArea formText;
	
	@FXML
	private TextArea backgroundText;
	
	@FXML
	private TextArea kindText;
	
	@FXML
	private TextArea highText;
	
	@FXML
	private TextArea lowText;

	@FXML
	private Button selectFolderBtn;
	
	@FXML
	private Label folderPathLabel;
	
	@FXML
	private TextField lowCount;
	
	@FXML
	private TextField highCount;
	
	@FXML
	private ComboBox<String> formSelector;
	
	@FXML
	private ComboBox<String> backgroundSelector;
	
	@FXML
	private ComboBox<String> kindSelector;
	
	@FXML
	private Button clearBtn;
	
	@FXML
	private Label countLabel;
	
	public Main app;
	
	private String obligatoryKeys;
	private String formKeys;
	private String backgroundKeys;
	private String kindKeys;
	private String highKeys;
	private String lowKeys;
	private int lCount;
	private int hCount;
	
	ObservableList<String> formOptions =    FXCollections.observableArrayList();
	ObservableList<String> backgroundOptions =    FXCollections.observableArrayList();
	ObservableList<String> kindOptions =    FXCollections.observableArrayList();
	

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//obligatoryText.setText("shampoo, bubble, bubbly, blue, invite, flyer, background, swimming, pool, foam, soap, liquid, cool, bath, shower, cleaning, wash, sea, deep, 3d, oxygen, water, transparent");
		//highText.setText("abstract, closeup, wet, nature, clean, fresh, bright, drop, drink, light, air, macro, hygiene, bathroom, template, vector, splash, color, rainbow, sphere, shine, orb, circle, glossy, ball, realistic, blowing, reflection");
		//lowText.setText("fizz, flow, froth, close-up, blow, ripple, wave, purity, beverage, underwater, freshness, powder, washing powder, detergent, diving, aqua park");
		
		highCount.setText("12");
		lowCount.setText("5");
		formSelector.setItems(formOptions);
		backgroundSelector.setItems(backgroundOptions);
		kindSelector.setItems(kindOptions);
		
		backgroundSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToBackgroundText();
		    }
		});
		formSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToFormText();
		    }
		});
		kindSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
		    if (! isShowing) {
		    	addSelectionToKindText();
		    }
		});
		
		Utils.addDeleteButtonToCombobox(backgroundSelector);
		Utils.addDeleteButtonToCombobox(formSelector);
		Utils.addDeleteButtonToCombobox(kindSelector);
		
		obligatoryText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		highText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		lowText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		kindText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		backgroundText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		formText.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		highCount.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
		lowCount.textProperty().addListener((observable, oldValue, newValue) -> {
			countLabel.setText("央钼: " + getKeysFromUI().size());
		});
	}
	@FXML
	private void clearForms(){
		obligatoryText.clear();
		highText.clear();
		lowText.clear();
		kindText.clear();
		formText.clear();
		backgroundText.clear();
	}
	
	
	public List<String> generateKeywords(){
		
		if (formText.getText() != null && !formText.getText().isEmpty() && !formOptions.contains(formText.getText()))
			formOptions.add(formText.getText());
		if (backgroundText.getText()!=null && !backgroundText.getText().isEmpty() && !backgroundOptions.contains(backgroundText.getText()))
			backgroundOptions.add(backgroundText.getText());
		if (kindText.getText()!=null && !kindText.getText().isEmpty() && !kindOptions.contains(kindText.getText()))
			kindOptions.add(kindText.getText());
		
		List<String> keys = getKeysFromUI();
		
		cutList(keys, 50);
	
		return keys;
	}
	
public List<String> generateKeywordsForMetadata(){
	    List<String> keys = new ArrayList<String>();
		
		addToList(obligatoryKeys, keys);
		addToList(formKeys, keys);
		addToList(backgroundKeys, keys);
		addToList(kindKeys, keys);
		
		addNRandomToList(highKeys, keys, hCount);
		addNRandomToList(lowKeys, keys, lCount);
		
		keys = keys.stream().distinct().collect(Collectors.toList());
		
		cutList(keys, 50);
	
		return keys;
	}
	
	public void saveKeywordsSource(){
		obligatoryKeys = obligatoryText.getText();
		formKeys = formText.getText();
		backgroundKeys = backgroundText.getText();
		kindKeys = kindText.getText();
		highKeys = highText.getText();
		lowKeys = lowText.getText();
		
		hCount = 0;
		lCount = 0;
		try{
			hCount = Integer.parseInt(highCount.getText());
			lCount = Integer.parseInt(lowCount.getText());
		}
		catch (Exception e){
		}
	}
	
	
	
	private List<String> getKeysFromUI(){
        List<String> keys = new ArrayList<String>();
		
		addToList(obligatoryText.getText(), keys);
		addToList(formText.getText(), keys);
		addToList(backgroundText.getText(), keys);
		addToList(kindText.getText(), keys);
		
		int nh = 0;
		int nl = 0;
		try{
			nh = Integer.parseInt(highCount.getText());
			nl = Integer.parseInt(lowCount.getText());
		}
		catch (Exception e){
			//return null;
		}
		addNRandomToList(highText.getText(), keys, nh);
		addNRandomToList(lowText.getText(), keys, nl);
		
		keys = keys.stream().distinct().collect(Collectors.toList());
		
		return keys;
	}
	
	
	
	
	private void addToList(String string, List<String> list){
		if (string==null) return;
		String[] array = string.split(",");
		for (String s:array)
			if (!s.trim().isEmpty()) list.add(s.trim());
	}
	
	
	private void addNRandomToList(String string, List<String> list, int N){
		Random random = new Random();
		String[] array = string.split(",");
		List<String> l = new ArrayList<String>(Arrays.asList(array));
		while (N>0){
			if (l.isEmpty())
				break;
			String s = l.remove(random.nextInt(l.size()));
			if (!s.trim().isEmpty() && !list.contains(s.trim())){
			list.add(s.trim());
			N--;
			}
		}
	}
	
	
	private String listToString(List<String> list){
		return StringUtils.join(list, ", ");
	}
	
	private void addToClipboad(String theString){
		StringSelection selection = new StringSelection(theString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(selection, selection);
	}
	
	
	/*#FXML
	private void showCount(){
		countLabel.setText("央钼: " + generateKeywords().size());
	}
	*/
	
	
	private List<String> cutList(List<String>list, int size){
		if (list.size() > size)
			list.subList(size, list.size()).clear();
		return list;
	}
	
	
	
		
	private static Set<String> findDuplicates(List<String> listContainingDuplicates) {
		 
		final Set<String> setToReturn = new HashSet<String>();
		final Set<String> set1 = new HashSet<String>();
 
		for (String yourInt : listContainingDuplicates) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt); 
			}
		}
		return setToReturn;
	}
	
	@FXML
	private void addSelectionToFormText(){
		if (formSelector.getSelectionModel().getSelectedItem() != null && !formSelector.getSelectionModel().getSelectedItem().isEmpty())
			formText.setText(formSelector.getSelectionModel().getSelectedItem());
	}
	
	@FXML
	private void addSelectionToBackgroundText(){
		if (backgroundSelector.getSelectionModel().getSelectedItem() != null && !backgroundSelector.getSelectionModel().getSelectedItem().isEmpty())
			backgroundText.setText(backgroundSelector.getSelectionModel().getSelectedItem());
	}
	
	@FXML
	private void addSelectionToKindText(){
		if (kindSelector.getSelectionModel().getSelectedItem() != null && !kindSelector.getSelectionModel().getSelectedItem().isEmpty())
			kindText.setText(kindSelector.getSelectionModel().getSelectedItem());
	}
	

	}

					
