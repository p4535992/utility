package com.github.p4535992.util.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates methods for requesting a server via HTTP GET/POST and
 * provides methods for parsing response from the server.
 * href: http://www.codejava.net/java-se/networking/an-http-utility-class-to-send-getpost-request.
 * href: http://www.codejava.net/java-se/networking/upload-files-by-sending-multipart-request-programmatically.
 * @author www.codejava.net.
 * 
 */
public class HttpUtility {
	private static String boundary;
	private static HttpURLConnection httpConn;
	private static final String LINE_FEED = "\r\n";
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;
	/**
	 * Makes an HTTP request using GET method to the specified URL.
	 * 
	 * @param requestURL
	 *            the URL of the remote server.
	 * @return An HttpURLConnection object.
	 * @throws IOException
	 *             thrown if any I/O org.p4535992.mvc.error occurred.
	 */
	public static HttpURLConnection sendGetRequest(String requestURL)
			throws IOException {
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);

		httpConn.setDoInput(true); // true if we want to read server's response
		httpConn.setDoOutput(false); // false indicates this is a GET request

		return httpConn;
	}

	/**
	 * Makes an HTTP request using POST method to the specified URL.
	 * 
	 * @param requestURL
	 *            the URL of the remote server.
	 * @param params
	 *            A map containing POST data in form of key-value pairs.
	 * @return An HttpURLConnection object.
	 * @throws IOException
	 *             thrown if any I/O org.p4535992.mvc.error occurred.
	 */
	public static HttpURLConnection sendPostRequest(String requestURL,
			Map<String, String> params) throws IOException {
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);

		httpConn.setDoInput(true); // true indicates the server returns response

		StringBuffer requestParams = new StringBuffer();

		if (params != null && params.size() > 0) {

			httpConn.setDoOutput(true); // true indicates POST request

			// creates the params string, encode them using URLEncoder
			Iterator<String> paramIterator = params.keySet().iterator();
			while (paramIterator.hasNext()) {
				String key = paramIterator.next();
				String value = params.get(key);
				requestParams.append(URLEncoder.encode(key, "UTF-8"));
				requestParams.append("=").append(
						URLEncoder.encode(value, "UTF-8"));
				requestParams.append("&");
			}

			// sends POST data
			OutputStreamWriter writer = new OutputStreamWriter(
					httpConn.getOutputStream());
			writer.write(requestParams.toString());
			writer.flush();
		}

		return httpConn;
	}

	/**
	 * Returns only one line from the server's response. 
         * This method should be used if the server returns only a single line of String.
	 * 
	 * @return a String of the server's response.
	 * @throws IOException
	 *             thrown if any I/O error occurred.
	 */
	public static String readSingleLineRespone() throws IOException {
		InputStream inputStream = null;
		if (httpConn != null) {
			inputStream = httpConn.getInputStream();
		} else {
			throw new IOException("Connection is not established.");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));

		String response = reader.readLine();
		reader.close();

		return response;
	}

	/**
	 * Returns an array of lines from the server's response. This method should
	 * be used if the server returns multiple lines of String.
	 * 
	 * @return an array of Strings of the server's response.
	 * @throws IOException
	 *             thrown if any I/O error occurred.
	 */
	public static String[] readMultipleLinesRespone() throws IOException {
		InputStream inputStream = null;
		if (httpConn != null) {
			inputStream = httpConn.getInputStream();
		} else {
			throw new IOException("Connection is not established.");
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		List<String> response = new ArrayList<String>();

		String line = "";
		while ((line = reader.readLine()) != null) {
			response.add(line);
		}
		reader.close();

		return response.toArray(new String[0]);
	}
	
	/**
	 * Closes the connection if opened.
	 */
	public static void disconnect() {
		if (httpConn != null) {
			httpConn.disconnect();
		}
	}

	/**
	 * This constructor initializes a new HTTP POST request with content type
	 * is set to multipart/form-data.
	 * @param requestURL url for the request.
	 * @param charset charset for encoding the request.
	 * @throws IOException thrown if any I/O error occurred.
	 */
	public void MultipartUtility(String requestURL, String charset)
			throws IOException {
		this.charset = charset;

		// creates a unique boundary based on time stamp
		boundary = "===" + System.currentTimeMillis() + "===";

		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true);	// indicates POST method
		httpConn.setDoInput(true);
		httpConn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
		httpConn.setRequestProperty("Test", "Bonjour");
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
				true);
	}

	/**
	 * Adds a form field to the request.
	 * @param name field name.
	 * @param value field value.
	 */
	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
				.append(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + charset).append(
				LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(value).append(LINE_FEED);
		writer.flush();
	}

	/**
	 * Adds a upload file section to the request.
	 * @param fieldName name attribute in input type="file" name="..." /.
	 * @param uploadFile a File to be uploaded.
	 * @throws IOException thrown if any I/O error occurred..
	 */
	public void addFilePart(String fieldName, File uploadFile)
			throws IOException {
		String fileName = uploadFile.getName();
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append(
				"Content-Disposition: form-data; name=\"" + fieldName
						+ "\"; filename=\"" + fileName + "\"")
				.append(LINE_FEED);
		writer.append(
				"Content-Type: "
						+ URLConnection.guessContentTypeFromName(fileName))
				.append(LINE_FEED);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.flush();

		FileInputStream inputStream = new FileInputStream(uploadFile);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();

		writer.append(LINE_FEED);
		writer.flush();
	}

	/**
	 * Adds a header field to the request.
	 * @param name - name of the header field.
	 * @param value - value of the header field.
	 */
	public void addHeaderField(String name, String value) {
		writer.append(name + ": " + value).append(LINE_FEED);
		writer.flush();
	}

	/**
	 * Completes the request and receives response from the server.
	 * @return a list of Strings as response in case the server returned
	 * status OK, otherwise an exception is thrown.
	 * @throws IOException thrown if any I/O error occurred.
	 */
	public List<String> finish() throws IOException {
		List<String> response = new ArrayList<String>();

		writer.append(LINE_FEED).flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.add(line);
			}
			reader.close();
			httpConn.disconnect();
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}

		return response;
	}
}