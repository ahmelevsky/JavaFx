package klt.ui;

import javafx.scene.control.Button;

public class TagButton extends Button {

	public boolean isActive;
	
	public TagButton(String label){
		super(label);
		this.isActive=true;
		this.setStyle(
                "-fx-background-radius: 20px; " +
                "-fx-min-width: 20px; " +
                "-fx-min-height: 5px; "+
                "-fx-background-color: " +
                   " linear-gradient(#f2f2f2, #d6d6d6)," +
                   " linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                   " linear-gradient(#dddddd 0%, #f6f6f6 50%);"+
                   " -fx-text-fill: black;" //+
               //    " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
        );
		 this.setOnAction(click -> {
			 if(!this.isActive)
				 this.setActive(true);
			 else
				 this.setActive(false);
			});
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		if(isActive)
			this.setStyle(
	                "-fx-background-radius: 20px; " +
	                "-fx-min-width: 20px; " +
	                "-fx-min-height: 5px; "+
	                "-fx-background-color: " +
	                   " linear-gradient(#f2f2f2, #d6d6d6)," +
	                   " linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
	                   " linear-gradient(#dddddd 0%, #f6f6f6 50%);"+
	                   " -fx-text-fill: black;"// +
	                  // " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
	        );
		else
			this.setStyle(
	                "-fx-background-radius: 20px; " +
	                "-fx-min-width: 20px; " +
	                "-fx-min-height: 5px; "+
	                "-fx-background-color: white; " +
	                 //  " linear-gradient(#f2f2f2, #d6d6d6)," +
	                //   " linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
	                 //  " linear-gradient(#dddddd 0%, #f6f6f6 50%);"+
	                   " -fx-text-fill: grey;" //+
	                 //  " -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );"
	        );
	}

	
	
	
	
	
}
