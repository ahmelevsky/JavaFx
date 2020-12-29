package klt.ui;

import javafx.scene.control.Spinner;

public class IntegerSpinner<T> extends Spinner<T>{
    // All constructor call init method

    private void init(){
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                getEditor().setText(oldValue);
            }
        });
    }
}