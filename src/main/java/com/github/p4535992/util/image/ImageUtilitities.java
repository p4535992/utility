package com.github.p4535992.util.image;

import info.aduna.io.FileUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by 4535992 on 14/07/2015.
 * @author 4535992.
 * @version 2015-07-14.
 */
@SuppressWarnings("unused")
public class ImageUtilitities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ImageUtilitities.class);

    public static void captureScreen(String fileName) throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRectangle);
        ImageIO.write(image, "png", new File(fileName));
    }

    public static Image scaledImage(Image image,int width,int height,int scale){
        BufferedImage scaled = new BufferedImage((width * scale),(height * scale), DEFAULT_IMAGE_TYPE);
        AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
        Graphics2D g2d = (Graphics2D) scaled.getGraphics();
        g2d.drawImage(scaled, new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR), 0, 0);
        return scaled;
    }

    /**
     * **
     * This application creates a gray level image, with pixels values between 0 (black) and 255 (white).
     * The JAI API is not used in this example.
     * @param bImage BufferedImage to write to a File.
     * @param filePathImage location of the image file.
     */
    public static void writeImage(BufferedImage bImage,String filePathImage){

        // We need its raster to set the pixels' values.
        WritableRaster raster = bImage.getRaster();
        // Put the pixels on the raster, using values between 0 and 255.
        for(int h=0; h < bImage.getHeight(); h++) {
            for (int w = 0; w < bImage.getWidth(); w++) {
                int value = 127 + (int) (128 * Math.sin(w / 32.) * Math.sin(h / 32.)); // Weird sin pattern.
                raster.setSample(w, h, 0, value);
            }
        }
        // Store the image using the PNG format.
        try {
            ImageIO.write(bImage, FileUtil.getFileExtension(filePathImage).toUpperCase(),new File(filePathImage));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    public static String toString(File file){
        StringBuilder sb = new StringBuilder();
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            while (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                //System.out.printf("formatName: %s%n", reader.getFormatName());
                sb.append(reader.getFormatName());
            }
            return sb.toString();
        }catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String toString(URL url){
        StringBuilder sb = new StringBuilder();
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(url.openStream());
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            while (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                //System.out.printf("formatName: %s%n", reader.getFormatName());
                sb.append(reader.getFormatName());
            }
            return sb.toString();
        }catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Posted by alpha02 at http://www.dreamincode.net/code/snippet1076.htm
     * @param image the Image Object to convert.
     * @return the BufferedImage.
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage)return (BufferedImage)image;
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        // Determine if the image has transparent pixels
        boolean hasAlpha = hasAlpha(image);
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) transparency = Transparency.BITMASK;
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            logger.warn(e.getMessage(), e);
        } //No screen
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {type = BufferedImage.TYPE_INT_ARGB;}
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    public static  BufferedImage toBufferedImage(File file){
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static BufferedImage toBufferedImage(URL url) throws IOException {
        // URLConnection.guessContentTypeFromStream only needs the first 12 bytes, but
        // just to be safe from future java api enhancements, we'll use a larger number
        int pushbackLimit = 100;
        InputStream urlStream = url.openStream();
        PushbackInputStream pushUrlStream = new PushbackInputStream(urlStream, pushbackLimit);
        byte [] firstBytes = new byte[pushbackLimit];
        // download the first initial bytes into a byte array, which we will later pass to
        // URLConnection.guessContentTypeFromStream
        pushUrlStream.read(firstBytes);
        // push the bytes back onto the PushbackInputStream so that the stream can be read
        // by ImageIO reader in its entirety
        pushUrlStream.unread(firstBytes);
        //String imageType = null;
        // Pass the initial bytes to URLConnection.guessContentTypeFromStream in the form of a
        // ByteArrayInputStream, which is mark supported.
        ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
        String mimeType = URLConnection.guessContentTypeFromStream(bais);
        //if (mimeType.startsWith("image/")) imageType = mimeType.substring("image/".length());
        // else handle failure here
        // read in image
        return ImageIO.read(pushUrlStream);

    }

    /**
     * @href http://www.jguru.com/faq/view.jsp?EID=134008
     */
    public static RenderedImage toRenderedImage(BufferedImage bImage,Image image){
    	// construct the buffered image
    	//BufferedImage bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
    	//obtain it's graphics
    	Graphics2D bImageGraphics = bImage.createGraphics();
    	//draw the Image (image) into the BufferedImage (bImage)
    	bImageGraphics.drawImage(image, null, null);
    	// cast it to rendered image
    	RenderedImage rImage = (RenderedImage)bImage;
    	return rImage;
    }
    
    /**
     * @href https://gist.github.com/jpt1122/f6bf89e8a97d40971150
     */
    public static BufferedImage toBufferedImage(RenderedImage img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		ColorModel cm = img.getColorModel();
		int width = img.getWidth();
		int height = img.getHeight();
		WritableRaster raster = cm
				.createCompatibleWritableRaster(width, height);
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		Hashtable<String,Object> properties = new Hashtable<String,Object>();
		String[] keys = img.getPropertyNames();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				properties.put(keys[i], img.getProperty(keys[i]));
			}
		}
		BufferedImage result = new BufferedImage(cm, raster,
				isAlphaPremultiplied, properties);
		img.copyData(raster);
		return result;
    }
    
    /**
     * @href https://coderanch.com/t/380929/java/convert-Image-RenderedImage
     */
    public static BufferedImage toBufferedImage(final Image image, final int type) {
    	if (image instanceof BufferedImage)
    		return (BufferedImage) image;
    	if (image instanceof VolatileImage)
    		return ((VolatileImage) image).getSnapshot();
    	loadImage(image);
    	final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    	final Graphics2D g2 = buffImg.createGraphics();
    	g2.drawImage(image, null, null);
    	g2.dispose();
    	return buffImg;
    }
    
    /**
     * @href https://coderanch.com/t/380929/java/convert-Image-RenderedImage
     */
    private static void loadImage(final Image image) {
    	class StatusObserver implements ImageObserver {
    		boolean imageLoaded = false;
    		@Override
    		public boolean imageUpdate(final Image img, final int infoflags, 
    				final int x, final int y, final int width, final int height) {
    			if (infoflags == ALLBITS) {
    				synchronized (this) {
    					imageLoaded = true;
    					notify();
    				}
    				return true;
    			}
    			return false;
    		}
    	}
    	final StatusObserver imageStatus = new StatusObserver();
    	synchronized (imageStatus) {
    		if (image.getWidth(imageStatus) == -1 || image.getHeight(imageStatus) == -1) {
    			while (!imageStatus.imageLoaded) {
    				try {
    					imageStatus.wait();
    				} catch (InterruptedException ex) {}
    			}
    		}
    	}
    }
    
    private static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) return ((BufferedImage)image).getColorModel().hasAlpha();
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        // Get the image's color model
        return pg.getColorModel().hasAlpha();
    }

    private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

    public BufferedImage convertImageToBufferedImage2(Image image) {
        return convertImageToBufferedImage2(image, DEFAULT_IMAGE_TYPE);
    }

    public BufferedImage convertImageToBufferedImage2(Image image, int type) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, null, null);
        waitForImage(bufferedImage);
        return bufferedImage;
    }

    private void waitForImage(BufferedImage bufferedImage) {
        final ImageLoadStatus imageLoadStatus = new ImageLoadStatus();
        bufferedImage.getHeight(new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                if (infoflags == ALLBITS) {
                    imageLoadStatus.heightDone = true;
                    return true;
                }
                return false;
            }
        });
        bufferedImage.getWidth(new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                if (infoflags == ALLBITS) {
                    imageLoadStatus.widthDone = true;
                    return true;
                }
                return false;
            }
        });
        while (!imageLoadStatus.widthDone && !imageLoadStatus.heightDone) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(),e);
            }
        }
    }

    class ImageLoadStatus {

        public boolean widthDone = false;
        public boolean heightDone = false;
    }

}
