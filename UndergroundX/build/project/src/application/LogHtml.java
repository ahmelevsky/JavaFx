package application;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogHtml {

    private String log;
    private String user;
    private URL messageIcon = LogHtml.class.getResource("/micon.jpg");
    
    public LogHtml(String user){
    	createLogTemplate();
    	this.user = user;
    }

    public String addMessage(String message) {
    	String date = (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.UK)).format(new Date());
    	StringBuilder sb = new StringBuilder(log);
    	
   //Заголовок сообщения: Иконка, Пользователь, Время
    	sb.append("<img src=\""+messageIcon+"\">");
    	sb.append("\t");
    	sb.append("<b><font color=\"blue\">" + user + "</font></b>");
    	sb.append("\t");
    	sb.append("<i><font color=\"green\" > (" + date + ")</font></i>");
    	sb.append(" <br> ");
    	
   //Строка самого сообщения
    	sb.append(message);
    	sb.append(" <br> ");
    	sb.append(" <br> ");
    	
    	log = sb.toString();
    	return log;
    }
    
    public void clearLog(){
    	createLogTemplate();
    }
    
    
	public String getLog() {
		return log;
	}

	private void createLogTemplate(){
		StringBuilder html = new StringBuilder();
    	html.append("<html>");
    	html.append("<head>");  
        html.append("   <script language=\"javascript\" type=\"text/javascript\">");  
        html.append("       function toBottom(){");  
        html.append("           window.scrollTo(0, document.body.scrollHeight);");  
        html.append("       }");  
        html.append("   </script>");  
        html.append("</head>");  
        html.append("<body onload='toBottom()'>");  
    	log = html.toString();
	}
	
}
