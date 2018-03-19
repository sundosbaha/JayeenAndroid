package com.jayeen.customer.parse;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.jayeen.customer.CustomerApplication;
import com.jayeen.customer.R;
import com.jayeen.customer.utils.AndyUtils;
import com.jayeen.customer.utils.AppLog;
import com.jayeen.customer.utils.Const;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultiPartRequester {

	private Map<String, String> paramsMap;
	Map<String, DataPart> paramsMultipart = new HashMap<>();
	private AsyncTaskCompleteListener mAsynclistener;
	private Response.ErrorListener errorListener;
	//    private Activity activity;
	public int serviceCode;
	String TAG = getClass().getSimpleName();
//    private HttpClient httpclient;
//    private AsyncHttpRequest request;

	public MultiPartRequester(Activity activity, Map<String, String> map,
							  int serviceCod, AsyncTaskCompleteListener asyncTaskCompleteListener,
							  Response.ErrorListener errorListnr) {
		this.paramsMap = map;
		this.serviceCode = serviceCod;
//        this.activity = activity;
		this.errorListener = errorListnr;
		// is Internet Connection Available...
		if (AndyUtils.isNetworkAvailable(activity)) {
			mAsynclistener = (AsyncTaskCompleteListener) asyncTaskCompleteListener;
			String url = map.get("url");
			for (String key : map.keySet()) {
				if (key.equalsIgnoreCase(Const.Params.PICTURE) && !TextUtils.isEmpty(map.get(key))) {
					File f = new File(map.get(key));
					try {
						paramsMultipart.put(key,
								new DataPart(
										f.getName(),
										getBytesFromFile(f), "image/jpeg"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				AppLog.Log(TAG, key + "---->" + map.get(key));
			}


			VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
				@Override
				public void onResponse(NetworkResponse response) {
					mAsynclistener.onTaskCompleted(new String(response.data), serviceCode);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					errorListener.onErrorResponse(error);
				}
			}) {
				@Override
				protected Map<String, String> getParams() {
					if (paramsMap.containsKey("picture"))
						paramsMap.remove("picture");
					return paramsMap;
				}

				@Override
				protected Map<String, DataPart> getByteData() {
					// file name could found file base or direct access from real path
					return paramsMultipart;
				}
			};
			multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
					60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

			CustomerApplication.requestQueue.add(multipartRequest);


		} else {
			AndyUtils.showToast(activity.getResources().getString(R.string.no_internet), R.id.coordinatorLayout, activity);
		}
	}

    /*

    class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            map.remove("url");
            try {
                HttpPost httppost = new HttpPost(urls[0]);
                AppLog.Log(Const.TAG, "Register Request URL" + urls[0]);
                httpclient = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 60000);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                for (String key : map.keySet()) {
                    AppLog.Log(key, map.get(key));
                    if (key.equalsIgnoreCase(Const.Params.PICTURE)) {
                        if (!TextUtils.isEmpty(map.get(key))) {
                            File f = new File(map.get(key));
                            builder.addBinaryBody(key, f, ContentType.MULTIPART_FORM_DATA, f.getName());
                        }
                    } else {
                        builder.addTextBody(key, map.get(key), ContentType.create("text/plain", MIME.UTF8_CHARSET));
                    }
                }
                httppost.setEntity(builder.build());
                ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                if (manager.getMemoryClass() < 25) {
                    System.gc();
                }
                HttpResponse response = httpclient.execute(httppost);
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
                AppLog.Log(Const.TAG, "Response Multipart" + responseBody);
                return responseBody;

            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError oume) {
                System.gc();
                AndyUtils.showToast(activity.getResources().getString(R.string.text_out_of_memory),
                        R.id.coordinatorLayout, activity.getParent().getParent());
            } finally {
                if (httpclient != null)
                    httpclient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (mAsynclistener != null) {
                mAsynclistener.onTaskCompleted(response, serviceCode);
            }
        }
    }
*/

//    private void showToast(String msg) {
//        AndyUtils.showToast(msg, R.id.coordinatorLayout, activity);
//    }

	//    public void cancelTask() {
//        request.cancel(true);
//    }
	public class VolleyMultipartRequest extends Request<NetworkResponse> {
		private final String twoHyphens = "--";
		private final String lineEnd = "\r\n";
		private final String boundary = "apiclient-" + System.currentTimeMillis();

		private Response.Listener<NetworkResponse> mListener;
		private Response.ErrorListener mErrorListener;
		private Map<String, String> mHeaders;

		/**
		 * Default constructor with predefined header and post method.
		 *
		 * @param url           request destination
		 * @param headers       predefined custom header
		 * @param listener      on success achieved 200 code from request
		 * @param errorListener on error http or library timeout
		 */
		public VolleyMultipartRequest(String url, Map<String, String> headers,
									  Response.Listener<NetworkResponse> listener,
									  Response.ErrorListener errorListener) {
			super(Method.POST, url, errorListener);
			this.mListener = listener;
			this.mErrorListener = errorListener;
			this.mHeaders = headers;
		}

		/**
		 * Constructor with option method and default header configuration.
		 *
		 * @param method        method for now accept POST and GET only
		 * @param url           request destination
		 * @param listener      on success event handler
		 * @param errorListener on error event handler
		 */
		public VolleyMultipartRequest(int method, String url,
									  Response.Listener<NetworkResponse> listener,
									  Response.ErrorListener errorListener) {
			super(method, url, errorListener);
			this.mListener = listener;
			this.mErrorListener = errorListener;
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			return (mHeaders != null) ? mHeaders : super.getHeaders();
		}

		@Override
		public String getBodyContentType() {
			return "multipart/form-data;boundary=" + boundary;
		}

		@Override
		public byte[] getBody() throws AuthFailureError {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			try {
				// populate text payload
				Map<String, String> params = getParams();
				if (params != null && params.size() > 0) {
					textParse(dos, params, getParamsEncoding());
				}

				// populate data byte payload
				Map<String, DataPart> data = getByteData();
				if (data != null && data.size() > 0) {
					dataParse(dos, data);
				}

				// close multipart form data after text and file data
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				return bos.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Custom method handle data payload.
		 *
		 * @return Map data part label with data byte
		 * @throws AuthFailureError
		 */
		protected Map<String, DataPart> getByteData() throws AuthFailureError {
			return null;
		}

		@Override
		protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
			try {
				return Response.success(
						response,
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (Exception e) {
				return Response.error(new ParseError(e));
			}
		}

		@Override
		protected void deliverResponse(NetworkResponse response) {
			mListener.onResponse(response);
		}

		@Override
		public void deliverError(VolleyError error) {
			mErrorListener.onErrorResponse(error);
		}

		/**
		 * Parse string map into data output stream by key and value.
		 *
		 * @param dataOutputStream data output stream handle string parsing
		 * @param params           string inputs collection
		 * @param encoding         encode the inputs, default UTF-8
		 * @throws IOException
		 */
		private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
			try {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
				}
			} catch (UnsupportedEncodingException uee) {
				throw new RuntimeException("Encoding not supported: " + encoding, uee);
			}
		}

		/**
		 * Parse data into data output stream.
		 *
		 * @param dataOutputStream data output stream handle file attachment
		 * @param data             loop through data
		 * @throws IOException
		 */
		private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
			for (Map.Entry<String, DataPart> entry : data.entrySet()) {
				buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
			}
		}

		/**
		 * Write string data into header and data output stream.
		 *
		 * @param dataOutputStream data output stream handle string parsing
		 * @param parameterName    name of input
		 * @param parameterValue   value of input
		 * @throws IOException
		 */
		private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
			dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
			dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
			//dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
			dataOutputStream.writeBytes(lineEnd);
			dataOutputStream.writeBytes(parameterValue + lineEnd);
		}

		/**
		 * Write data file into header and data output stream.
		 *
		 * @param dataOutputStream data output stream handle data parsing
		 * @param dataFile         data byte as DataPart from collection
		 * @param inputName        name of data input
		 * @throws IOException
		 */
		private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
			dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
			dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
					inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
			if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
				dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
			}
			dataOutputStream.writeBytes(lineEnd);

			ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
			int bytesAvailable = fileInputStream.available();

			int maxBufferSize = 1024 * 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dataOutputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dataOutputStream.writeBytes(lineEnd);
		}

		/**
		 * Simple data container use for passing byte file
		 */

	}

	public class DataPart {
		private String fileName;
		private byte[] content;
		private String type;

		/**
		 * Default data part
		 */
		public DataPart() {
		}

		/**
		 * Constructor with data.
		 *
		 * @param name label of data
		 * @param data byte data
		 */
		public DataPart(String name, byte[] data) {
			fileName = name;
			content = data;
		}

		/**
		 * Constructor with mime data type.
		 *
		 * @param name     label of data
		 * @param data     byte data
		 * @param mimeType mime data like "image/jpeg"
		 */
		public DataPart(String name, byte[] data, String mimeType) {
			fileName = name;
			content = data;
			type = mimeType;
		}

		/**
		 * Getter file name.
		 *
		 * @return file name
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * Setter file name.
		 *
		 * @param fileName string file name
		 */
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		/**
		 * Getter content.
		 *
		 * @return byte file data
		 */
		public byte[] getContent() {
			return content;
		}

		/**
		 * Setter content.
		 *
		 * @param content byte file data
		 */
		public void setContent(byte[] content) {
			this.content = content;
		}

		/**
		 * Getter mime type.
		 *
		 * @return mime type
		 */
		public String getType() {
			return type;
		}

		/**
		 * Setter mime type.
		 *
		 * @param type mime type
		 */
		public void setType(String type) {
			this.type = type;
		}
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// Get the size of the file
		long length = file.length();
		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
}

