package com.example.songle;

import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Environment;
import android.app.Activity;
import android.widget.Toast; 
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView; 
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class Songle extends Activity {
  private final String TAG = "Songle";
	Button start;
	Button stop;
	Button upload;
	MediaRecorder mr;
	private TextView text;
	private TextView text3;
	private boolean isSDCardExit; 
	private File SDPathDir;
	private File mrFile;
	private String result = null;
	private String urlStr="http://175.159.117.19/searchTrack.php"; 
	public static final int UPDATE = 0;  
	private static final int RECORDING = 1;
	private TextView time;  
    private int duration = 0;  
    private int state = 0;  
    private static final int IDLE = 0; 
    private Handler handler = new Handler() {  
   	 
        @Override  
        public void handleMessage(Message msg) {  
            if (state == RECORDING) {  
                super.handleMessage(msg);  
                duration++;  
                time.setText(timeToString());  
                handler.sendMessageDelayed(handler.obtainMessage(UPDATE), 1000);  
            }  
        }  
 
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songle);
          
        start=(Button)findViewById(R.id.Start);
			      
        stop=(Button)findViewById(R.id.Stop);
        upload=(Button)findViewById(R.id.Upload);
        text = (TextView) findViewById(R.id.textView2);
        time = (TextView) findViewById(R.id.timer);  
        text3 = (TextView) findViewById(R.id.textView3);
        text3.setText("The searching result:");
        start.setEnabled(true);
        stop.setEnabled(false);
        upload.setEnabled(true);
        
        isSDCardExit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	    	if(isSDCardExit){
	    		SDPathDir = Environment.getExternalStorageDirectory();
	    	}
    	
    	buttonListener(); 
      
 } 
     
    private void buttonListener() {
		// TODO Auto-generated method stub
    	start.setOnClickListener(new View.OnClickListener(){
  	   		 
			public void onClick(View v) { 
				// TODO Auto-generated method stub

			    text.setText("Preparing");
			    
		        mr=new MediaRecorder();
		        state = RECORDING;  
	            handler.sendEmptyMessage(UPDATE);
		        
		        mr.setAudioSource(AudioSource.MIC);
		       	mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		          
				try{
					mrFile= File.createTempFile("mrFile", ".mp3",SDPathDir);
					RefreshSDCard();
				
				 }catch(Exception e){
					e.printStackTrace();
				} 
				mr.setOutputFile(mrFile.getAbsolutePath());
				
				startRecorder();
				text.setText("Recording");
				//text3.setText("File Path:"+mrFile.getAbsolutePath());
			
				start.setEnabled(false);
	    		stop.setEnabled(true); 
	    		upload.setEnabled(true); 
	    		  
			  }

			
			} );
    	 
	     stop.setOnClickListener(new View.OnClickListener(){
	        
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mr!=null){
						text.setText("Stopping");
						mr.stop();
						mr.release();
						mr=null;
						 handler.removeMessages(UPDATE);  
					        state = IDLE;  
					        duration = 0;  
						text.setText("Stop now");								
					}
					
					start.setEnabled(true);
		    		stop.setEnabled(false);
		    		upload.setEnabled(true);  
				}});
 
              
	    upload.setOnClickListener(new View.OnClickListener(){
	    	 
			public void onClick(View v) {
				// TODO Auto-generated method stub
				text.setText("Connecting to the server"); 
				RefreshSDCard();
				start.setEnabled(true);
	    		stop.setEnabled(true);
	    		upload.setEnabled(true);   		 

 	    		try {
		 	       		String end = "\r\n";
		 	       	    String hyphens = "--";
		 	       	    String boundary = "*****";
		 	   			URL url = new URL(urlStr);
		 	   			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		 	   			 
		 	   			conn.setDoInput(true);
		 	   			conn.setDoOutput(true);
		 	   			conn.setUseCaches(false);
		 	   			 
		 	   			conn.setRequestMethod("POST");
		 	   			conn.setRequestProperty("Charset", "UTF-8");
		 	   			conn.setRequestProperty("Connection", "Keep-Alive");
		 	   			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);	   			
		 	   			 
		 	   			Log.e(TAG, mrFile.toString());
		 	   			if(mrFile != null){
		 	   				
		 	   			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
			   				 
			   				ds.writeBytes(hyphens + boundary + end);
			   				ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\";filename=\"" + 
			   						mrFile.getName() +"\"" + end);
			   				ds.writeBytes(end);
			   				InputStream input = new FileInputStream(mrFile);
			   				int size = 1024;
			   				byte[] buffer = new byte[size];
			   				int length = -1;
			   				 
			   				while((length = input.read(buffer)) != -1){
			   					ds.write(buffer, 0, length);
			   				}
			   				input.close();
			   				ds.writeBytes(end);
			   				ds.writeBytes(hyphens + boundary + hyphens + end);
			   				ds.flush();
			   				}
		
		 	   				Log.e(TAG, conn.getResponseCode() + "=======");
		 	   				if(conn.getResponseCode() == 200){
			 	   				Log.e(TAG, "request success");  
			
			 	   				InputStream input =  conn.getInputStream();  
			
			 	   				StringBuffer sb1= new StringBuffer();  
			
			 	   				int ss ;  
			
			 	   				 while((ss=input.read())!=-1)  {  
				
			 	   					 sb1.append((char)ss);  
				
				 	   				}  
			
			 	   				result = sb1.toString();  
			 	   				Log.e(TAG, "result : "+ result);  
		
		 	   				}
		 	   			
		 	   		} catch (MalformedURLException e) {
		 	   			// TODO Auto-generated catch block
		 	   			e.printStackTrace();
		 	   		} catch (IOException e) {
		 	   			// TODO Auto-generated catch block
		 	   			e.printStackTrace();
		 	   		}
 	    		  text.setText("Get the result:");
	    		  text3.setText(result);
				}
			});

	}
    private String timeToString() {  
        if (duration >= 60) {  
            int min = duration / 60;  
            String m = min > 9 ? min + "" : "0" + min;  
            int sec = duration % 60;  
            String s = sec > 9 ? sec + "" : "0" + sec;  
            return m + ":" + s;  
        } else {  
            return "00:" + (duration > 9 ? duration + "" : "0" + duration);  
        }  
    }  
 
  
	private void startRecorder() {
		// TODO Auto-generated method stub
		try{
			if(!isSDCardExit){
				Toast.makeText(this, "Please insert the SD card !", Toast.LENGTH_LONG).show();	 
				return;
			}
			mr.prepare();
			mr.start();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
    }
	
	 public String doGet(String url)  
	    {  
	        try{  
	            HttpGet method = new HttpGet(url);  	      
	            DefaultHttpClient client = new DefaultHttpClient();  
	            method.setHeader( "Connection", "Keep-Alive" );  	              
	            HttpResponse response = client.execute( method );  
	            int status = response.getStatusLine().getStatusCode();  
	            if ( status != HttpStatus.SC_OK )  
	                throw new Exception( "" );  
	            //text.setText("Get the result from the server");   
	            return EntityUtils.toString( response.getEntity(), "UTF-8" );  
	        }catch(Exception e){  
	            return null;  
	        }  
	    }
     
    private void RefreshSDCard() {  
	    StringBuilder stringBuilder = new StringBuilder("file://");  
	    File file = Environment.getExternalStorageDirectory();  
	    Uri uri = Uri.parse(stringBuilder + file.toString());  
	    Intent mIntent1 = new Intent("android.intent.action.MEDIA_MOUNTED", uri);  
	    sendBroadcast(mIntent1);  
	}  
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_songle, menu);
        return true;
    }
    
}
