import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


public class Main {

	
	static String baseDir = "d:\\Downloads\\evo-lutio\\";
	static String index = baseDir + "index.html";
	
	public static void main(String[] args) {
		 createTagLinks();
	}

	public static void changeTitles() {
		Document doc;
		try {
			doc = Jsoup.parse(new File(index), null);
		} catch (IOException e) {
			System.out.println("Can't open " + index);
			return;
		}
		Elements links = doc.select("a[target=\"post\"]");
		for (Element l:links){
			Element title = l.selectFirst("i");
			String link = l.attributes().get("href");
			Document article;
			try {
				article = Jsoup.parse(new File(baseDir + link), null);
			} catch (IOException e) {
				System.out.println("Can't open " + baseDir + link);
				continue;
			}
			Element t = article.selectFirst("title");
			if (t!=null) {
			    System.out.println("Processing " + t);
				title.text(t.text());
			}
			
		}
		
		try {
			FileUtils.writeStringToFile(new File(index), doc.outerHtml(), "UTF-8");
		} catch (IOException e) {
			System.out.println("Can't write html to file, error" + e.getMessage());
		}
	}
	
	
	public static void createTagLinks() {
		
		Map<String, List<String>> tagsList = new HashMap<String, List<String>>();
		try {
			Files.walk(Paths.get(baseDir))
			.filter(f -> f.toString().endsWith(".html")).forEach(filePath -> {
				Document doc;
				File file = filePath.toFile();
				try {
					doc = Jsoup.parse(file, null);
				} catch (IOException e) {
					System.out.println("Can't open " + file.getAbsolutePath());
					return;
				}
				Elements links = doc.select(".b-singlepost-tags-items");
				if (links == null || links.isEmpty())
					return;
				Element tagsLink = links.get(0);
				String tagsString = tagsLink.text();
				if (tagsString == null || tagsString.isEmpty())
					return;
				String[] tags = tagsString.split(",");
				for (String t:tags){
					t=t.trim();
					List<String> pages = tagsList.get(t);
					if (pages == null) {
						System.out.println("New tag: " + t);
						pages = new ArrayList<String>();
						tagsList.put(t, pages);
					}
					pages.add(file.getName());
				}
			});
			//.toArray(file -> new File(""));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
		System.out.println("DONE WITH READING TAGS");
		
		for (String tag:tagsList.keySet()){
			Document doc;
			Document newDoc = Document.createShell("");
			newDoc.charset(Charset.forName("utf-8"));
			newDoc.body().append("<font color=\"gray\" size=\"20\">" + tag + "</font><br><hr>");
			try {
				doc = Jsoup.parse(new File(index), null);
			} catch (IOException e) {
				System.out.println("Can't open " + index);
				return;
			}
			
			Elements links = doc.select("a[target=\"post\"]");
			
			//List<Element> links = doc.getElementsByTag("p").stream().filter(el -> !el.getElementsByAttributeValue("target", "post").isEmpty()).collect(Collectors.toList());
			for (Element l:links){
				String href = l.attributes().get("href");
				boolean found = false;
				for (String p:tagsList.get(tag)){
					if (href.endsWith(p)){
						found = true;
						break;
				}
				}
				if (found) 
					newDoc.body().append("<p>").append(l.toString()).append("</p>");
				
			}
			try {
				System.out.println("Wrinting new tags file: " + tag + ".html");
				FileUtils.writeStringToFile(new File(baseDir + tag + ".html"), newDoc.outerHtml(), "UTF-8");
			} catch (IOException e) {
				System.out.println("Can't write html to file, error" + e.getMessage());
			}
		}
		
		}
	
	
	
	/*
	 * 
	 * 
	 * for (Element l:links){
				String href = l.attributes().get("href");
				boolean found = false;
				for (String p:tagsList.get(tag)){
					if (href.endsWith(p)){
						found = true;
						break;
				}
				}
				if (!found){
					List<Element> ppp = doc.getElementsByTag("p").stream().filter(el -> !el.getElementsByAttributeValue("href", l.attributes().get("href")).isEmpty()).collect(Collectors.toList());
					if (ppp!=null && !ppp.isEmpty())
						ppp.get(0).remove();
					else
					{
						l.nextElementSibling().remove();
						l.nextElementSibling().remove();
						l.remove();
					}
					doc.getElementsByTag("font").remove();
					doc.getElementsByTag("hr").remove();
					doc.getElementsByTag("tt").remove();
					doc.select("a[name]").remove();
					doc.getElementsMatchingOwnText("December").remove();
					doc.getElementsMatchingOwnText("October").remove();
					doc.getElementsMatchingOwnText("April").remove();
					doc.title(tag);
					doc.body().child(0).before("<font color=\"gray\" size=\"20\">" + tag + "</font>");
					
				}
			}
	 * 
	 */
	 
	
}
