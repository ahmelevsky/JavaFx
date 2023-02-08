package am.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import am.Main;
import am.SetData;
import am.ShutterImage;
import am.db.SQLManager;
import am.ui.MainController.TableKeyEventHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class SetController implements Initializable {

	
	public Main app;
	
	public Tab tab;
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@FXML
	private TableView<SetData> tableView;
	
	@FXML
	private TableColumn<SetData, String> columnName;
	
	@FXML
	private TableColumn<SetData, String> columnStartDate;
	
	@FXML
	private TableColumn<SetData, String> columnLastDate;
	
	@FXML
	private TableColumn<SetData, Integer> columnImagesCount;
	
	@FXML
	private TableColumn<SetData, Integer> columnSoldImagesCount;
	
	@FXML
	private TableColumn<SetData, Double> columnSoldPercent;
	
	@FXML
	private TableColumn<SetData, Double> columnSum;
	
	@FXML
	private LineChart lineChart;
	
	
	public ObservableList<SetData> sets = FXCollections.observableArrayList();
	private SQLManager sqlManager;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setItems(sets);
		setupTableViewColumn();
		tableView.getSelectionModel().setCellSelectionEnabled(true);
	    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	    tableView.setOnKeyPressed(new TableKeyEventHandler());
	}
	
	public void setup() {
		this.sqlManager = app.sqlManager;
	}
	
	
	public void loadData() {
		 Runnable r = new Runnable() {
	         public void run() {
	        	 List<String> views = sqlManager.getSetsNames();
	     		for (String view:views) {
	     			SetData set = new SetData(view);
	     			set.setStartUpload(sqlManager.executeRequestForString("SELECT MIN(uploaded_date) FROM " + view + ";"));
	     			set.setLastUpload(sqlManager.executeRequestForString("SELECT MAX(uploaded_date) FROM " + view + ";"));
	     			int imagesCount = sqlManager.executeRequestForInt("SELECT COUNT(*) FROM " + view + ";");
	     			set.setImagesCount(imagesCount);
	     			int soldImagesCount = sqlManager.executeRequestForInt("SELECT COUNT(*) FROM " + view + " WHERE downloads>0;");
	     			set.setSoldImagesCount(soldImagesCount);
	     			double val = soldImagesCount/(double)imagesCount*10000;
	     			set.setSoldPercent( Math.round(val)/100.0);
	     			set.setSum(sqlManager.executeRequestForInt("SELECT SUM(earnings) FROM " + view + " ;"));
	     			sets.add(set);
	     		}
	         }
	     };

	     new Thread(r).start();
	}
	
	
	private void setupTableViewColumn() {
		columnName.setPrefWidth(200);
		columnStartDate.setCellValueFactory(new PropertyValueFactory<>("startUpload"));
		columnLastDate.setCellValueFactory(new PropertyValueFactory<>("lastUpload"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnImagesCount.setCellValueFactory(new PropertyValueFactory<>("imagesCount"));
		columnSoldImagesCount.setCellValueFactory(new PropertyValueFactory<>("soldImagesCount"));
		columnSoldPercent.setCellValueFactory(new PropertyValueFactory<>("soldPercent"));
		columnSum.setCellValueFactory(new PropertyValueFactory<>("sum"));
    }

}
