
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

//from  w  ww . j ava2  s  .  c om
public class Main extends Application {
	
	double rate = 1.0;
	double minus = 0.05;
	Timeline timeline;
	
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
   
	  
 Text msg = new Text("��� ���������� ����� ������ ���� �� �����, ������ ������������ ����� ����������� ��-������. ��� ��������� � ���� ���������. ���� ������, ��� ��� ��� � ����� � ������ � �� ���� ������������-������������, � �������� ����, ��� �� ����� ���� � ��� � ����� ����. ��������� ��� ������������ ��� ������ ���� � ���������� ������������� � ������ ���������, � ����� ������� �����, � �����������. ��� ����� ����� � ��������� �����������, ��� ��� ������ � �� ������������ � ��� �� ������ ��������� ����� �������� ���������� ���� ����� ������� ����� �����, ��� ���, ����� ����� � ��������� ���������. ���� �� �������� �� ����� ������, ���� ������ ���� �� ���� ����. ���� ������ �� ����� ����, ��� ����������; ���������� ����������� � ��������� � �������� ������� ������������, ����� ��������� �� ����� �����; ����� ���� ����� �� �����, �� ����� ������ �����; ������ ������� � ����� ������� �������.�� ������ ���� ����� ����� ����� ������ �������� ��������� � �����, ��� ��� ����� � �����, � � ������� ���, �� ���� � ������ ����� ����, ��������� �� � ������� ����, � � ����� ��������, �� ��������� ������. �� �������� ���� ������, ���������� ���� �� �������� ������, ��� �� ����� ����� ������� �������, � ������ ������� ������ ����� ������� � �������� � ��� �����; �� ����� �������, ��� �� ����� � ������ �����. ���, ��, ��� ��� ����? � ����� ��, ��������� ���. � ��, ��� ��� ����? ��! ������ ����� ���� � ����������;");
    msg.setTextAlignment(TextAlignment.JUSTIFY);
    
    msg.setFont(Font.font("Arial", 250));
    msg.setStyle("-fx-line-spacing: -10px;");
    Pane root = new Pane(msg);
   
    root.setPrefSize(500, 500);
    Scene scene = new Scene(root);
    
    root.setStyle("-fx-background-color: #000000;");
    msg.setFill(Color.WHITE);
   
    scene.setOnScroll(
            new EventHandler<ScrollEvent>() {
                @Override
                public void handle(ScrollEvent event) {
                	//rate += 0.1;
                	//timeline.setRate(rate+=event.getDeltaY()/10);
                	
                	minus+=event.getDeltaY()/5000;
                    event.consume();
                }
            });
    
    stage.setScene(scene);
    stage.setTitle("Scrolling Text");
    stage.setFullScreen(true);
    stage.show();
   
    double sceneHeight = scene.getHeight();
    double msgHeight =  msg.getBoundsInLocal().getHeight();
    
    
    
	System.out.println(msg.getBoundsInLocal().getHeight());
    
    
   System.out.println(msg.getBoundsInLocal().getHeight());
    msg.setWrappingWidth(scene.getWidth());
    
    msg.setTranslateY(sceneHeight);
    
Thread thread = new Thread(new Runnable	() {

	@Override
	public void run() {
	while(true){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
		msg.setTranslateY(msg.translateYProperty().doubleValue()-minus);
	}
	}
});
thread.setDaemon(true);
thread.start();


/*
    KeyValue initKeyValue = new KeyValue(msg.translateYProperty(), sceneHeight);
    KeyFrame initFrame = new KeyFrame(Duration.ZERO, initKeyValue);

  //  KeyValue endKeyValue = new KeyValue(msg.translateYProperty(), -1.0        * msgHeight);
    KeyValue endKeyValue = new KeyValue(msg.translateYProperty(), -1.0 * msgHeight);
    KeyFrame endFrame = new KeyFrame(Duration.millis(30000), endKeyValue);
    timeline = new Timeline(initFrame, endFrame);
    //timeline.setRate(10);
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();*/
  }
    
}