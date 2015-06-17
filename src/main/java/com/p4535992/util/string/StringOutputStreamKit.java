/*
 * $Id: StringOutputStream.java,v 1.1 2004/02/16 15:19:03 nwalsh Exp $
 * 
 * @@ Where did this come from originaly??
 */
package com.p4535992.util.string;

import com.p4535992.util.log.SystemLog;

import java.io.*;
import java.util.Vector;

/**
 * Provides a OutputStream to an internal String.
 * Internally converts bytes to a Strings.
 * storing them in an internal StringBuffer.
 * Converts the bytes to chars, each char having 
 * as its lower 8 bits the value of each byte.
 * Created from 4535992 2015-05-05
 */
public class StringOutputStreamKit extends OutputStream implements Serializable {
    private static final long serialVersionUID = 7L;
/**
 * The internal StringBuffer to contain all the data
 * written to this OutputStream.
 */
	protected StringBuffer buf = null;
	
/**
 * Creates a StringOutputStream.
 * Makes a new internal StringBuffer.
 */
	public StringOutputStreamKit(){
		super();
		buf = new StringBuffer();
	}
/**
 * Returns the content of the internal
 * StringBuffer, the result of all writing
 * to this OutputStream.
 * @return Returns the content of the internal
 * StringBuffer.
 */
	public String toString(){
		return buf.toString();
	}
/**
 * Returns the content of the internal
 * StringBuffer, the result of all writing
 * to this OutputStream.
 * @return Returns the content of the internal
 * StringBuffer.
 */
	public String getString(){
		return buf.toString();
	}
/**
 * Sets the internal StringBuffer to null.
 *
 */
	public void close(){
		buf = null;
	}
/**
 * Does nothing in StringOutputStream
 * due to all the data is self contained.
 */
	public void flush(){ //does nothing.
	}
/**
 * Writes and appends a byte array to the 
 * internal StringBuffer.
 * bytes are copied to chars, each char having 
 * as its lower 8 bits the value of each byte.
 * @param b the byte array to write to the
 * internal StringBuffer.
 */
	public void write(byte[] b){
		buf.append(convertBytesToString(b));
	}
/**
 * Writes and appends a byte array to the 
 * internal StringBuffer.
 * bytes are copied to chars, each char having 
 * as its lower 8 bits the value of each byte.
 * @param b the byte array to write to the
 * internal StringBuffer.
 * @param off the index of byte array b to start
 * reading from.
 * @param len the number of bytes from byte array
 * b to read into internal StringBuffer.
 */
	public void write(byte[] b, int off, int len){
		if (off < 0 || len < 0 || off + len > b.length)
			throw new IndexOutOfBoundsException(
				"StringOutputStream.write: Parameters out of bounds.");
		byte[] bytes = new byte[len];
		int start = off;
		for(int i=0; i<bytes.length; i++){
			bytes[i] = b[start++];
		}
		buf.append(convertBytesToString(bytes));
	}
/**
 * Writes and appends a single byte as an int to the 
 * internal StringBuffer.
 * The int is copied to a char, having 
 * as its value the lower eight bits of the int.
 * @param b the byte as an int add to the 
 * internal StringBuffer, taking the lower
 * eight bits of the int as the value of the char.
 */
	public void write(int b){
		buf.append((char) (b & 255)); //gets the eight low order bits of the int.
	}
/**
 * Converts a String to a byte array, taking the
 * eight lower bits of each char as the eight bits of the bytes
 * for the byte array.
 * @param Str the String to convert to byte array.
 * @return the new byte array converted from a String.
 */
	public static byte[] convertStringToBytes(String Str){
		char[] NewChr = Str.toCharArray();
		byte[] NewByt = new byte[NewChr.length];
		for (int i=0; i < NewByt.length; i++){
			int Ci = NewChr[i] & 255;
			NewByt[i] = (byte) Ci;
		}
		return NewByt;	
	}
/**
 * Converts a byte array to a String, taking the
 * eight bits of each byte as the lower eight bits of the chars
 * in the String.
 * @param bytes the byte array to convert to char array.
 * @return the new String converted from a byte array.
 */
	public static String convertBytesToString(byte[] bytes){
		return new String(convertBytesToChars(bytes));
	}
/**
 * Converts a byte array to a char array, taking the
 * eight bits of each byte as the lower eight bits of the char.
 * @param bytes the byte array to convert to char array.
 * @return the new char array converted from a byte array.
 */
	public static char[] convertBytesToChars(byte[] bytes){
		char[] NewChr = new char[bytes.length];
		for (int i=0; i < NewChr.length; i++){
			int Ci = bytes[i] & 255;
			NewChr[i] = (char) Ci;
		}
		return NewChr;	
	}
/**
 * Replaces all occurences of char of one type with another 
 * char in a given byte array and returns it.
 * Change is made to the byte array and it is also returned.
 * @param Bytes the byte array to change bytes in.
 * @param Old the char to find, converted to a byte by the
 * lower eight bits, ignoring the higher eight bits.
 * @param New the char to replace all occurences of Old char
 * converted to a byte by the
 * lower eight bits, ignoring the higher eight bits. 
 * @return the changed byte array.
 */
	public static byte[] replaceBytes(byte[] Bytes, char Old, char New){
		for(int i = 0; i < Bytes.length; i++){
			int bint = Bytes[i] & 255; //full byte is byte & 255 - converts to int
			if (bint == (Old & 127)) Bytes[i] = (byte)(New & 127); //ASCII is char & 127
		}			
		return Bytes;
	}
/**
 * Replaces one String with another where it occurs of a byte array
 * making a new array due to the possibility of different size.
 * Goes through the array just once, so any new occurances of Old 
 * String that appear due to the New String replacement are not replaced.
 * Does no change to the byte array parameter Bytes.
 * @param Bytes the byte array copy and search through but does no
 * change to this parameter, returning the resulting byte array.
 * @param Old the old String to replace.
 * @param New the new String to replace Old String with.
 * @return the new byte array with replacements done.
 */
	public static byte[] replaceBytes(byte[] Bytes, String Old, String New){
		String NewStr = replace(new String(Bytes), Old, New);
		char[] NewChr = NewStr.toCharArray();
		byte[] NewByt = new byte[NewChr.length];
		for (int i=0; i < NewByt.length; i++){
			int Ci = NewChr[i] & 255;
			NewByt[i] = (byte) Ci;
		}
		return NewByt;
	}
	/**
	 * <P>Convenience method for writing bytes to an OutputStream.
	 * Closes resources within a try finally block.
	 * 
	 * @param OPut           OutputStream to write to.
	 * 
	 * @param bbuf           The contents to write to the OutputStream, OPut.
	 *
	 * @throws Exception     Probably an IO Exception if any.
	 */

	public static void writeBytes(FileOutputStream OPut, byte[] bbuf) throws Exception{
		try{
			OPut.write(bbuf, 0, bbuf.length);
			OPut.flush();		
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			OPut.close();
		}
	}

	/**
	 * <P>Convenience method for reading bytes from an InputStream.
	 * Closes resources within a try finally block.
	 * @param IPut           InputStream to read from.
	 * @return               The contents read from the InputStream, IPut.
	 * @throws Exception     Probably an IO Exception if any.
	 */

	public static byte[] readBytes(InputStream IPut) throws Exception {
		Vector<byte[]> BytArrsV = new Vector<>();
		int size=0;
		byte[] FinalVal = new byte[0];
		int read=0;
		try{
			int i=0;
			byte[] bbuf = new byte[1024];
			while ((read  = IPut.read(bbuf, 0, bbuf.length)) > -1){
				byte[] bbuf2 = new byte[read];
				for (i=0; i < bbuf2.length; i++){
					bbuf2[i] = bbuf[i];
				}
				BytArrsV.addElement(bbuf2);
				size += read;
			}
			FinalVal = new byte[size];
			int j = 0;
			for (i = 0; i < BytArrsV.size(); i++){
				byte[] byarr = BytArrsV.elementAt(i);
				for (int k = 0; k < byarr.length; k++){
					FinalVal[j++] = byarr[k];
				}
			}
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			IPut.close();
		}
		return FinalVal;
	}

	/**
	 * <P>Used to replace one String segment with
	 * another String segment inside a String.
	 * Similar to the replace method in String but instead of using
	 * char it uses String for replacing old with new.
	 *
	 * @param Text          The String from which is produced the new String 
	 *                      with which replacement has occurred.
	 * @param Old           The old String that is replaced by the new one
	 *                      in The Text String.
	 * @param New           The new String to replace the old String 
	 *                      in the Text String.
	 * @return              The new String with replacement having occurred.
	 *
	 */

	public static String replace(String Text, String Old, String New){
		if (Old.length() == 0) return Text;
		StringBuffer buf = new StringBuffer();
		int i=0, j=0;
		while((i = Text.indexOf(Old, j)) > -1){
			buf.append(Text.substring(j,i) + New);
			j = i + Old.length();
		}
		if (j < Text.length())
			buf.append(Text.substring(j));
		return buf.toString();
	}

	//OTHER METHODS
	/**
	 * Returns a String with the content of the InputStream.
	 * @param is with the InputStream.
	 * @return string with the content of the InputStream.
	 * @throws IOException error.
	 */
	public static String convertInputStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is,"UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * Returns am InputStream with the parameter.
	 *
	 * @param string string.
	 * @return InputStream with the string value.
	 */
	public static InputStream convertStringToInputStream(String string) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			SystemLog.exception(e);
		}
		return is;
	}

	public static byte[] convertInputStreamToBytes(InputStream is){
		try {
			return new byte[is.available()];
		}catch (IOException e){
			return null;
		}
	}

	public static InputStream convertBytesToInputStream(byte[] bbuf){
		return  new ByteArrayInputStream(bbuf);
	}
}

