package application;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.JpegPhotoshopMetadata;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcBlock;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcRecord;
import org.apache.commons.imaging.formats.jpeg.iptc.IptcTypes;
import org.apache.commons.imaging.formats.jpeg.iptc.JpegIptcRewriter;
import org.apache.commons.imaging.formats.jpeg.iptc.PhotoshopApp13Data;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class MetadataWriter {
	
	public static Main app;
	
	public static void writeMetadataToFile(File toFile, List<String> keys, String title, String description) throws ImageReadException, IOException, ImageWriteException, SAXException, ParserConfigurationException, TransformerException{
   	 ByteSource byteSource = new ByteSourceFile(toFile);
       List<IptcBlock> newBlocks =  new ArrayList<>();
       List<IptcRecord> newRecords = new ArrayList<>();
       
       final Map<String, Object> params = new HashMap<>();
       final boolean ignoreImageData = false;
       params.put(ImagingConstants.PARAM_KEY_READ_THUMBNAILS, Boolean.valueOf(!ignoreImageData));
       
       JpegPhotoshopMetadata metadata = new JpegImageParser().getPhotoshopMetadata(byteSource, params);
       if (metadata!=null)
       	newBlocks = metadata.photoshopApp13Data.getNonIptcBlocks();
       	
           String xmpXml = new JpegImageParser().getXmpXml(byteSource, new HashMap());
           String xmlToWrite = changeTitleInXMP(xmpXml, title);
       for (String key:keys)
       	newRecords.add(new IptcRecord(IptcTypes.KEYWORDS, key));
       if (!title.isEmpty())
       	newRecords.add(new IptcRecord(IptcTypes.HEADLINE, title));
       if (!description.isEmpty())
       	newRecords.add(new IptcRecord(IptcTypes.CAPTION_ABSTRACT, description));
       final PhotoshopApp13Data newData = new PhotoshopApp13Data(newRecords,
               newBlocks);
       writeIptcXMP(byteSource, newData, xmlToWrite, toFile);
       app.log("");
       app.log("Запись метаданных в файл "  + toFile.getAbsolutePath());
       app.log("Title: " + title);
       app.log("Decription: " + description);
       app.log("Keys: " + StringUtils.join(keys, ", "));
              
      //writeIptc(byteSource, newData, toFile);
     // writeXMP(byteSource, xmlToWrite, toFile);
	}
	
public static File writeIptc(final ByteSource byteSource, final PhotoshopApp13Data newData, final File updated) throws IOException, ImageReadException, ImageWriteException {
byte[] bytes = byteSource.getAll();
try (FileOutputStream fos = new FileOutputStream(updated);
       OutputStream os = new BufferedOutputStream(fos)) {
   new JpegIptcRewriter().writeIPTC(bytes, os, newData);
}
catch  (Exception e){
	app.log("ОШИБКА: Не могу записать данные в файл " +  updated.getAbsolutePath() + " ,возможно после этой попытки файл будет повережден (проверьте его размер)");
}
return updated;
}


public static File writeIptcXMP(final ByteSource byteSource, final PhotoshopApp13Data newData, String newXML, final File updated) throws IOException, ImageReadException, ImageWriteException {
byte[] bytes = byteSource.getAll();
ByteArrayOutputStream baos = new ByteArrayOutputStream();
try (FileOutputStream fos = new FileOutputStream(updated);
       OutputStream os = new BufferedOutputStream(fos)) {
new JpegIptcRewriter().writeIPTC(bytes, baos, newData);
new JpegXmpRewriter().updateXmpXml(baos.toByteArray(), os, newXML);
}
return updated;
}



public static File writeXMP(final ByteSource byteSource, String newXML, final File updated) throws IOException, ImageReadException, ImageWriteException {
byte[] bytes = byteSource.getAll();
try (FileOutputStream fos = new FileOutputStream(updated);
       OutputStream os = new BufferedOutputStream(fos)) {
	new JpegXmpRewriter().updateXmpXml(bytes, os, newXML);
}
return updated;
}

public static void removeXMP(ByteSource byteSource, File file) throws FileNotFoundException, IOException, ImageReadException {
	try (FileOutputStream fos = new FileOutputStream(file);
	        OutputStream os = new BufferedOutputStream(fos)) {
		 new JpegXmpRewriter().removeXmpXml(byteSource, os);
	}
}

	
	
	public static String changeTitleInXMP(String xmp, String newTitle) throws SAXException, IOException, ParserConfigurationException, TransformerException{
		  if (xmp==null || xmp.equals("")) return null;
		  DocumentBuilderFactory dbf =
		            DocumentBuilderFactory.newInstance();
		        DocumentBuilder db = dbf.newDocumentBuilder();
		        InputSource is = new InputSource();
		        is.setCharacterStream(new StringReader(xmp));
		  
		  Document doc = db.parse(is);
			Node top = getNodeByNodeName(doc.getChildNodes(), "x:xmpmeta");
			if (top==null)
				return null;
			Node rdf = getNodeByNodeName(top.getChildNodes(), "rdf:RDF");
			if (rdf==null)
				return null;
			Node desc = getNodeByNodeName(rdf.getChildNodes(), "rdf:Description");
			if (desc==null)
				return null;
			Node oldtitle = getNodeByNodeName(desc.getChildNodes(), "dc:title");
			Node newtitle = writeTitleToDocument(doc, newTitle);
			if (oldtitle!=null)
				desc.replaceChild(newtitle, oldtitle);
			else 
				desc.appendChild(newtitle);
			String result = docToString(doc);
			return result;
	}
	
	private static Node writeTitleToDocument(Document doc, String title){
		Element item = doc.createElement("dc:title");
		Element item2 = doc.createElement("rdf:Alt");
		Element item3 = doc.createElement("rdf:li");
		item3.setAttribute("xml:lang", "x-default");
	    item3.appendChild(doc.createTextNode(title));
	    item2.appendChild(item3);
	    item.appendChild(item2);
	    return item;
	}
	
	private static Node getNodeByNodeName(NodeList nodes, String nodeName){
		Node result = null;
		for (int i=0;i<nodes.getLength();i++){
			if (nodes.item(i).getNodeName().equalsIgnoreCase(nodeName))
				result = nodes.item(i);
		}
	   return result;
	}
	
	
	private static void printNode(Node node){
		System.out.println("NodeName: " + node.getNodeName() + ", NodeValue: "  + node.getNodeValue());
	}
	

	  public static String nodeToString(Node node) throws TransformerException {
		  StringWriter sw = new StringWriter();
		   Transformer t = TransformerFactory.newInstance().newTransformer();
		   t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		   t.setOutputProperty(OutputKeys.INDENT, "yes");
		   t.transform(new DOMSource(node), new StreamResult(sw));
		  return sw.toString();
		  }
	  
	
	
	  public static String getCharacterDataFromElement(Node node) {
	    Node child = node.getFirstChild();
	    if (child instanceof CharacterData) {
	       CharacterData cd = (CharacterData) child;
	       return cd.getData();
	    }
	    return "?";
	  }
	  
	  public static String docToString(Document doc) throws TransformerException{
		  TransformerFactory tf = TransformerFactory.newInstance();
		  Transformer transformer = tf.newTransformer();
		  transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		  StringWriter writer = new StringWriter();
		  transformer.transform(new DOMSource(doc), new StreamResult(writer));
		  String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		  return output;
	  }
	  
}
