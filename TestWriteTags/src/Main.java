import static org.junit.Assert.assertNotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class Main {

	public static void main(String[] args) throws Exception {
		addIptcData();
		//IPTCKeywordsDataUpdater updater = new IPTCKeywordsDataUpdater();
		//updater.updateInfo(new File("SIaaq7mFeG4.jpg"), "Test0.jpg", "Pelevin", "RURURU");
	}
	
	

    
    public static void addIptcData() throws Exception
    {
        List<File> images = getJpegImages();
        for (int i = 0; i < images.size(); i++)
        {
        	
        	File imageFile = images.get(i);
        	final ByteSource byteSource = new ByteSourceFile(imageFile);
        	 
            final Map<String, Object> params = new HashMap<>();
            final boolean ignoreImageData = false;
           params.put(ImagingConstants.PARAM_KEY_READ_THUMBNAILS, Boolean.valueOf(!ignoreImageData));
    
           final JpegPhotoshopMetadata metadata = new JpegImageParser().getPhotoshopMetadata(byteSource, params);
    
           final List<IptcBlock> newBlocks = metadata.photoshopApp13Data.getNonIptcBlocks();
           final List<IptcRecord> newRecords = new ArrayList<>();
         //  final List<IptcBlock> newBlocks = new ArrayList<>();
    //
           newRecords.add(new IptcRecord(IptcTypes.HEADLINE, "Header"));
            newRecords.add(new IptcRecord(IptcTypes.CAPTION_ABSTRACT, "Description"));
           newRecords.add(new IptcRecord(IptcTypes.KEYWORDS, "Cat"));
           newRecords.add(new IptcRecord(IptcTypes.KEYWORDS, "Dog"));
           newRecords.add(new IptcRecord(IptcTypes.KEYWORDS, "Pig"));
            final PhotoshopApp13Data newData = new PhotoshopApp13Data(newRecords,
                    newBlocks);

            final File updated = writeIptc(byteSource, newData, new File(imageFile.getName()+"new.jpg"));
    
           // final ByteSource updateByteSource = new ByteSourceFile(updated);
           // final JpegPhotoshopMetadata outMetadata = new JpegImageParser().getPhotoshopMetadata(updateByteSource, params);
        	
            }

    }

    public static File writeIptc(final ByteSource byteSource, final PhotoshopApp13Data newData, final File updated) throws IOException, ImageReadException, ImageWriteException {
        try (FileOutputStream fos = new FileOutputStream(updated);
                OutputStream os = new BufferedOutputStream(fos)) {
            new JpegIptcRewriter().writeIPTC(byteSource, os, newData);
        }
        return updated;
    }

    protected static File createTempFile(String prefix, String suffix)
            throws IOException {
        File tempFolder = new File("tmp");
        if (!tempFolder.exists())
            tempFolder.mkdirs();

        File result = File.createTempFile(prefix, suffix, tempFolder);
        result.deleteOnExit();
        return result;
    }

	private static List<File> getJpegImages() {
		return new ArrayList<File>(Arrays.asList(new File(".").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				 return name.toLowerCase().endsWith(".jpg");
			}
		})));
		
	}

}
