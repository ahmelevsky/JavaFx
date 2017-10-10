
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
   
	  
 Text msg = new Text("Все счастливые семьи похожи друг на друга, каждая несчастливая семья несчастлива по-своему. Все смешалось в доме Облонских. Жена узнала, что муж был в связи с бывшею в их доме француженкою-гувернанткой, и объявила мужу, что не может жить с ним в одном доме. Положение это продолжалось уже третий день и мучительно чувствовалось и самими супругами, и всеми членами семьи, и домочадцами. Все члены семьи и домочадцы чувствовали, что нет смысла в их сожительстве и что на каждом постоялом дворе случайно сошедшиеся люди более связаны между собой, чем они, члены семьи и домочадцы Облонских. Жена не выходила из своих комнат, мужа третий день не было дома. Дети бегали по всему дому, как потерянные; англичанка поссорилась с экономкой и написала записку приятельнице, прося приискать ей новое место; повар ушел вчера со двора, во время самого обеда; черная кухарка и кучер просили расчета.На третий день после ссоры князь Степан Аркадьич Облонский — Стива, как его звали в свете, — в обычный час, то есть в восемь часов утра, проснулся не в спальне жены, а в своем кабинете, на сафьянном диване. Он повернул свое полное, выхоленное тело на пружинах дивана, как бы желая опять заснуть надолго, с другой стороны крепко обнял подушку и прижался к ней щекой; но вдруг вскочил, сел на диван и открыл глаза. «Да, да, как это было? — думал он, вспоминая сон. — Да, как это было? Да! Алабин давал обед в Дармштадте;");
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