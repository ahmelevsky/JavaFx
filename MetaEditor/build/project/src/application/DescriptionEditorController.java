package application;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;

public class DescriptionEditorController implements Initializable {

	@FXML
	private TextArea text1;

	@FXML
	private TextArea text2;

	@FXML
	private TextArea text3;

	@FXML
	private TextArea text4;

	@FXML
	private TextArea text5;

	@FXML
	private ComboBox<String> selector1;

	@FXML
	private ComboBox<String> selector2;

	@FXML
	private ComboBox<String> selector3;

	@FXML
	private ComboBox<String> selector4;

	@FXML
	private ComboBox<String> selector5;

	@FXML
	private Button generateBtn;

	@FXML
	private Button clearBtn;

	@FXML
	private Text countLabel;

	@FXML
	private TextArea resultText;

	@FXML
	private Text addedTexts;

	private final int LIMIT = 200;
	public Main app;

	ObservableList<String> options1 = FXCollections.observableArrayList();
	ObservableList<String> options2 = FXCollections.observableArrayList();
	ObservableList<String> options3 = FXCollections.observableArrayList();
	ObservableList<String> options4 = FXCollections.observableArrayList();
	ObservableList<String> options5 = FXCollections.observableArrayList();

	private List<TextArea> textFields = new ArrayList<TextArea>();
	private List<ObservableList<String>> options = new ArrayList<ObservableList<String>>();

	List<String> descriptions1 = new ArrayList<String>();
	List<String> descriptions2 = new ArrayList<String>();
	List<String> descriptions3 = new ArrayList<String>();
	List<String> descriptions4 = new ArrayList<String>();
	List<String> descriptions5 = new ArrayList<String>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ImageView imageView = new ImageView(new Image(new File(
				"resources/add.png").toURI().toString()));

		imageView.setFitWidth(50);

		imageView.setFitHeight(50);

		generateBtn.setGraphic(imageView);

		// Background background = new Background(backgroundImage);
		// generateBtn.setBackground(background);

		resultText.setEditable(false);

		textFields.add(text1);
		textFields.add(text2);
		textFields.add(text3);
		textFields.add(text4);
		textFields.add(text5);
		options.add(options1);
		options.add(options2);
		options.add(options3);
		options.add(options4);
		options.add(options5);
		
		selector1.setItems(options1);
		selector2.setItems(options2);
		selector3.setItems(options3);
		selector4.setItems(options4);
		selector5.setItems(options5);
		countLabel.setText("");

		
		Utils.addDeleteButtonToCombobox(selector1);
		Utils.addDeleteButtonToCombobox(selector2);
		Utils.addDeleteButtonToCombobox(selector3);
		Utils.addDeleteButtonToCombobox(selector4);
		Utils.addDeleteButtonToCombobox(selector5);


		for (TextArea tf : textFields) {
			tf.textProperty().addListener((observable, oldValue, newValue) -> {

				countLabel.setText("Символов: " + recount());
			});
			denyTab(tf);
		}
	}

	private int recount() {
		List<String> currentText = new ArrayList<String>();
		for (TextArea tf : textFields)
			if (!tf.getText().trim().isEmpty()) {
				currentText.add(Collections.max(
						Arrays.asList(
								tf.getText().trim().split("\\r\\n|\\n|\\r"))
								.stream().distinct()
								.collect(Collectors.toList()),
						Comparator.comparing(s -> s.length())));
			}
		String current = StringUtils.join(currentText, " ");
		resultText.setText(current);

		if (current.length() > this.LIMIT) {
			generateBtn.setDisable(true);
			countLabel.setFill(Color.RED);
		} else {
			generateBtn.setDisable(false);
			countLabel.setFill(Color.BLACK);
		}

		return current.length();
	}

	@FXML
	public void clearForms() {
		text1.clear();
		text2.clear();
		text3.clear();
		text4.clear();
		text5.clear();
		resultText.clear();
		countLabel.setText("");
		options1.clear();
		options2.clear();
		options3.clear();
		options4.clear();
		options5.clear();
	}

	@FXML
	public void add() {

		if (!checkLimit(true))
			return;
		List<String> textSet = new ArrayList<String>();
		for (int i = 0; i < textFields.size(); i++) {
			String textContent = textFields.get(i).getText();
			String[] addedVariants = textContent.split("\\r\\n|\\n|\\r");
			if (addedVariants.length == 0)
				continue;
			List<String> added = Arrays.asList(addedVariants).stream()
					.distinct().collect(Collectors.toList());
			textSet.addAll(added);
			final int final_i = i;
			added.forEach(a -> {
				if (!a.isEmpty() && !options.get(final_i).contains(a))
					options.get(final_i).add(a);
			});
		}
	}

	public void saveDescriptionsSource() {
		descriptions1.clear();
		descriptions1.addAll(options1);

		descriptions2.clear();
		descriptions2.addAll(options2);

		descriptions3.clear();
		descriptions3.addAll(options3);

		descriptions4.clear();
		descriptions4.addAll(options4);

		descriptions5.clear();
		descriptions5.addAll(options5);
	}

	public String generateRandomDescriptionForMetadata() {
		StringBuilder sb = new StringBuilder();
		String text = getRandomOption(descriptions1);
		sb.append(text);
		text = getRandomOption(descriptions2);
		if (!text.isEmpty())
			sb.append(" " + text);
		text = getRandomOption(descriptions3);
		if (!text.isEmpty())
			sb.append(" " + text);
		text = getRandomOption(descriptions4);
		if (!text.isEmpty())
			sb.append(" " + text);
		text = getRandomOption(descriptions5);
		if (!text.isEmpty())
			sb.append(" " + text);
		return sb.toString();
	}

	public String generateRandomDescription() {
		StringBuilder sb = new StringBuilder();
		String text = getRandomOption(options1);
		sb.append(text);
		text = getRandomOption(options2);
		if (!text.isEmpty())
			sb.append(" " + text);
		text = getRandomOption(options3);
		if (!text.isEmpty())
			sb.append(" " + text);
		text = getRandomOption(options4);
		if (!text.isEmpty())
			sb.append(" " + text);
		text = getRandomOption(options5);
		if (!text.isEmpty())
			sb.append(" " + text);
		return sb.toString();
	}

	private String getRandomOption(List<String> variants) {
		if (variants.isEmpty())
			return "";
		return variants.get(new Random().nextInt(variants.size()));
	}

	private void addToClipboad(String theString) {
		StringSelection selection = new StringSelection(theString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public boolean checkLimit(boolean showMessage) {

		List<String> longest = new ArrayList<String>();
		for (int i = 0; i < textFields.size(); i++) {
			List<String> variants = new ArrayList<String>();
			variants.addAll(options.get(i));
			if (!textFields.get(i).getText().trim().isEmpty())
				variants.addAll(Arrays
						.asList(textFields.get(i).getText().trim()
								.split("\\r\\n|\\n|\\r")).stream().distinct()
						.collect(Collectors.toList()));
			String longestStr = longestString(variants);
			if (!longestStr.isEmpty())
				longest.add(longestStr);
		}
		String longestAll = StringUtils.join(longest, " ");
		addedTexts.setText("Наиболее длинная комбинация, символов: "
				+ longestAll.length());

		if (longestAll.length() > this.LIMIT) {
			addedTexts.setFill(Color.RED);
			if (showMessage) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error!");
				alert.setHeaderText("Ошибка добавления");
				alert.setContentText("Некоторые комбинации приводит к превышению ограничения в "
						+ this.LIMIT + " символов.");
				alert.showAndWait();
			}

			return false;
		} else {
			addedTexts.setFill(Color.BLACK);
			return true;
		}

	}

	private String longestString(List<String> list) {
		if (list.isEmpty())
			return "";
		return Collections.max(list, Comparator.comparing(s -> s.length()));
	}

	public boolean checkDataIsCorrect() {
		return descriptions1.stream().allMatch(
				s -> app.isCorrectKey(s))
				&& descriptions2.stream().allMatch(
						s -> app.isCorrectKey(s))
				&& descriptions3.stream().allMatch(
						s -> app.isCorrectKey(s))
				&& descriptions4.stream().allMatch(
						s -> app.isCorrectKey(s))
				&& descriptions5.stream().allMatch(
						s -> app.isCorrectKey(s));
	}

	private void denyTab(TextArea textArea) {
		textArea.addEventFilter(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						if (event.getCode() == KeyCode.TAB) {
							TextAreaSkin skin = (TextAreaSkin) textArea
									.getSkin();
							if (skin.getBehavior() instanceof TextAreaBehavior) {
								TextAreaBehavior behavior = (TextAreaBehavior) skin
										.getBehavior();
								if (event.isControlDown()) {
									behavior.callAction("InsertTab");
								} else {
									behavior.callAction("TraverseNext");
								}
								event.consume();
							}

						}
					}
				});
	}

}
