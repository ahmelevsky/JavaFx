package ot.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ot.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class MainController {
    Main app;
	
	@FXML 
	public TextArea scriptInput;
	
	@FXML 
	public RadioButton jsb;
	
	@FXML 
	public RadioButton asb;
	
	@FXML 
	public TextArea scriptOutput;
	
	@FXML 
	public Button runBtn;
	
	@FXML 
	private void initialize() {
		scriptInput.appendText("var app = Application('Adobe Illustrator');\n");
		scriptInput.appendText("if (!app.running())\n");
		scriptInput.appendText("app.activate()\n");
		 ToggleGroup group = new ToggleGroup();
		 jsb.setToggleGroup(group);
		 asb.setToggleGroup(group);
		 asb.setSelected(true);
	}

	public void setMainApp(Main main) {
		this.app = main;
		
	}
	
	@FXML 
	private void runScript(){
		 scriptOutput.clear();
		 Runtime runtime = Runtime.getRuntime();
		 List<String> command = new ArrayList<String>();
		 command.add("osascript");
		 if (jsb.isSelected()) {
			 command.add("-l");
			 command.add("JavaScript");
		 }
		 String[] lines = scriptInput.getText().split("\\n");
		 for (String l:lines){
			 command.add("-e");
			 command.add(l);
		 }
		    try
		    {
		      Process proc = runtime.exec(command.toArray(new String[command.size()]));
		      BufferedReader stdInput = new BufferedReader(new 
              InputStreamReader(proc.getInputStream()));

              BufferedReader stdError = new BufferedReader(new 
              InputStreamReader(proc.getErrorStream()));
    
		      String line = null;
		      while ( (line = stdInput.readLine()) != null)
		    	  scriptOutput.appendText(line + "\r\n");
		      while ( (line = stdError.readLine()) != null)
		    	  scriptOutput.appendText(line + "\r\n");

		      int exitVal;
				exitVal = proc.waitFor();
		      scriptOutput.appendText("Process exitValue: " + exitVal);
		    }
		    catch (InterruptedException | IOException e)
		    {
		      e.printStackTrace();
		    }
	}
	
	   
}
