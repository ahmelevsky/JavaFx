package artiom.view;


import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import artiom.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;

/**
 * ���������� ��� ������������� ���������� ���� ��������.
 * 
 * @author Marco Jakob
 */
public class BirthdayStatisticsController {

    @FXML
    private BarChart barChart;

    @FXML
    private CategoryAxis xAxis;

    private ObservableList monthNames = FXCollections.observableArrayList();

    /**
     * �������������� �����-����������. ���� ����� ���������� �������������
     * ����� ����, ��� fxml-���� ��� ��������.
     */
    @FXML
    private void initialize() {
        // �������� ������ � ����������� ������� �������.
        String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
        // ����������� ��� � ������ � ��������� � ��� ObservableList �������.
        monthNames.addAll(Arrays.asList(months));

        // ��������� ����� ������� ����������� ��� �������������� ���.
        xAxis.setCategories(monthNames);
    }

    /**
     * ����� ���������, � ������� ����� �������� ����������.
     * 
     * @param persons
     */
    public void setPersonData(List<Person> persons) {
        // ������� ���������, ������� ��� �������� � ��������� ������.
        int[] monthCounter = new int[12];
        for (Person p : persons) {
            int month = p.getBirthday().getMonthValue() - 1;
            monthCounter[month]++;
        }

        XYChart.Series series = new XYChart.Series<>();

        // ������ ������ XYChart.Data ��� ������� ������.
        // ��������� ��� � �����.
        for (int i = 0; i < monthCounter.length; i++) {
            series.getData().add(new XYChart.Data<>(monthNames.get(i), monthCounter[i]));
        }

        barChart.getData().add(series);
    }
}