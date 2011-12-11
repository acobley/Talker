package org.r2dvd.aec;
import java.net.*;
import javax.xml.parsers.*;

import java.io.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import android.os.AsyncTask;

import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TalkActivity extends Activity  implements OnInitListener {
    /** Called when the activity is first created. */
	private static final int REQ_TTS_STATUS_CHECK = 0;
	private TextToSpeech mTts;
	private Button speakBtn = null;
	private static final String TAG = "TTS Demo";
	
	private static final long serialVersionUID = 1L;
	static final long ONE_HOUR = 3600000L;
    static final long TWO_HOUR = ONE_HOUR * 2L;
    static final String ATTR_MAC = "openid_mac";
    static final String ATTR_ALIAS = "openid_alias";  
	final static String APIKEY = "w0guDSUAGr35fUjVfe19kg";
	final static String APISECRET = "bdNwfL8A99z7vBTVhx3tjf8s60ERVL7MNSzWuuXhg";
	final static String CALLBACK = "oauth://twitter";
    
    private HashMap ProvidersMap = new HashMap();
    
    private OLoginSetup ols =null;
    private OGetResponse grs=null;
	private OGetStatus gs=null;
    
    TextView textView;
    TextView responseView;
	WebView webview;
	OAuthService s =null;
	Token requestToken=null; 
	String authURL =null;
	Token accessToken=null;
	long lastTweet=0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView)findViewById(R.id.textview);
        responseView = (TextView)findViewById(R.id.responseview);
        webview = (WebView) findViewById(R.id.webView1);
        speakBtn = (Button)findViewById(R.id.Speak);

     // Check to be sure that TTS exists and is okay to use
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
        ols= new OLoginSetup();
        ols.execute();
        
        
        
        
      //attach WebViewClient to intercept the callback url
	       webview.setWebViewClient(new WebViewClient(){
	       	@Override
	    	public boolean shouldOverrideUrlLoading(WebView view, String url){
	       		Scheduler sc=null;
	    		//check for our custom callback protocol
	       //otherwise use default behavior
	    		if(url.startsWith("oauth")){
	    			//authorization complete hide webview for now.
	    			webview.setVisibility(View.GONE);
	    			 grs= new OGetResponse();
	    			 grs.execute(url);
	    			 
	    			 gs= new OGetStatus();
	    			 gs.execute(url);
	    			 
	    			 sc=new Scheduler(url);
	    			 sc.start();
                 //Response response=ols.GetResponse(url);
	    			/*
	    			if (response !=null){
	    				textView.setText(response.getBody());
	    			}
                    */
	    			return true;
	    		}

	    		return super.shouldOverrideUrlLoading(view, url);
	    	}
	    });

       
       
    }
    
    public void saySomething(View view) {
    	 
    	CharSequence ch =responseView.getText();
    	String text=ch.toString();
    	mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
    	mTts.speak("Goodbye World", TextToSpeech.QUEUE_ADD, null);
    }
    
    @Override
    public void onInit(int status) {
        // Now that the TTS engine is ready, we enable the button
        if( status == TextToSpeech.SUCCESS) {
            speakBtn.setEnabled(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        // if we're losing focus, stop talking
        if( mTts != null)
            mTts.stop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mTts.shutdown();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TTS_STATUS_CHECK) {
            switch (resultCode) {
            case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                // TTS is up and running
                mTts = new TextToSpeech(this, this);
                //Log.v(TAG, "Pico is installed okay");
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
                // missing data, install it
                //Log.v(TAG, "Need language stuff: " + resultCode);
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
            default:
                //Log.e(TAG, "Got a failure. TTS apparently not available");
            }
        }
        else {
            // Got something else
        }
    }

    
  


    private class OLoginSetup extends AsyncTask<String, Void, String> {
    

    	@Override
        protected void onPreExecute() {
            //Log.v("AsyncTask", "onPreExecute");
        }

    	
        protected String doInBackground(String... urls) {
        	
        	//Log.v(TAG, "Starting async");
        	//set up service and get request token as seen on scribe website
            //https://github.com/fernandezpablo85/scribe-java/wiki/Getting-Started
        	
           s = new ServiceBuilder()
            .provider(TwitterApi.class)
            .apiKey(APIKEY)
            .apiSecret(APISECRET)
    		.callback(CALLBACK)
    		.build();
            //Log.v(TAG, "got Service");
            
            try {
            	 requestToken= s.getRequestToken();
            }catch(Exception et){
            	//Log.v(TAG, "didn't get token  "+et);
            	return (null);
            }
    		//Log.v(TAG, "got token");
    		authURL = s.getAuthorizationUrl(requestToken);
    		//Log.v(TAG, "got authurl" + authURL);

    	    //send user to authorization page
    		webview.loadUrl(authURL); 
            return null ;
        }

    
    
        
        protected void onPostExecute(String url) {
        	//Log.v(TAG, "On Post Execute");   		
        	
        	
            
        }
    }
    
    
    private class OGetResponse extends AsyncTask<String, Void, String> {
    	 Response response=null;
      
         AuthorStore aus=null;
    	@Override
        protected void onPreExecute() {
            //Log.v("Oget Response", "onPreExecute");
        }
    	 protected String doInBackground(String... urls) {
         	
         	//Log.v(TAG, "Starting Response " +urls.length);
         	int count = urls.length;
            
            for (int i = 0; i < count; i++) {
            	//Log.v(TAG, "url "+urls[i]);	
         	  response=GetResponse(urls[i]);
         	   ////Log.v(TAG, "response "+response.getBody());	
         	   
            }
            return null;
         	
    	 }
    	 protected void onPostExecute(String url) {
         	//Log.v(TAG, "On Post Execute");   		
         	
         	textView.setText("User "+aus.getname()+" ID "+aus.getUserId());
         	
             
         }
    	 
    	 Response GetResponse(String url){
         	Uri uri = Uri.parse(url);
 			String verifier = uri.getQueryParameter("oauth_verifier");
 			Verifier v = new Verifier(verifier);

 			//save this token for practical use.
 			accessToken = s.getAccessToken(requestToken, v);

 			//host twitter detected from callback oauth://twitter
 			if(uri.getHost().equals("twitter")){
 				//requesting xml because its easier
            //for human to read as it comes back
    			OAuthRequest req = new OAuthRequest(Verb.GET,
                    "http://api.twitter.com/1/account/verify_credentials.xml");
    			s.signRequest(accessToken, req);
    			response = req.send();
    			////System.out.println(response.getBody());
    			XMLParser xmlparse=new XMLParser();
    			aus=xmlparse.GetDetails(response);
    			
    			
    			return response;
 			}
 			return null;
 			
         }
    }
    
    
    
    private class OGetStatus extends AsyncTask<String, Void, String> {
   	 Response response=null;
   	 String body=null;
   	 String output=null;

   	@Override
       protected void onPreExecute() {
           //Log.v("Oget Response", "onPreExecute");
       }
   	 protected String doInBackground(String... urls) {
        	
        	//Log.v(TAG, "Starting Response " +urls.length);
        	int count = urls.length;
           
           for (int i = 0; i < count; i++) {
           	//Log.v(TAG, "url "+urls[i]);	
        	  body=GetResponse(urls[i]);
        	   ////Log.v(TAG, "response "+response.getBody());	
        	   
           }
           return null;
        	
   	 }
   	 protected void onPostExecute(String url) {
        	//Log.v(TAG, " Get Status On Post Execute");   		
     
        	responseView.setText(output);
            
        }
   	  
   	 String GetResponse(String url){
        	Uri uri = Uri.parse(url);


			//host twitter detected from callback oauth://twitter
			if(uri.getHost().equals("twitter")){
				//requesting xml because its easier
           //for human to read as it comes back
 
   		 	
   			OAuthRequest req = new OAuthRequest(Verb.GET,
                       "http://api.twitter.com/1/statuses/home_timeline.xml");
       	    s.signRequest(accessToken, req);
       	    response = req.send();
       	    //System.out.println(response.getBody());
       	    TweetParse xmlparse=new TweetParse();
			List<TweetStore> aus=xmlparse.GetDetails(response);
			Iterator<TweetStore> it=aus.iterator();
			boolean lastTweetSet=false;
			output="";
			while (it.hasNext()){
				TweetStore Tweet=(TweetStore)it.next();
				   System.out.println("LastTweet "+lastTweet+" "+Tweet.getId()+" : "+lastTweetSet+" Says  "+Tweet.getTweet());

				if (Tweet.getId()>lastTweet){
				   output=Tweet.getId()+" :"+Tweet.getName()+" Says  "+Tweet.getTweet()+".."+output;
				   if (lastTweetSet==false){
					   lastTweet=Tweet.getId();
					   lastTweetSet=true;
				   }
				}
			}
   			return output;
			}
			return null;
    
        }
   }
    
   private class Scheduler implements Runnable {
	   Thread myThread1;
	   int i=0;
	   String url=null;
	   Scheduler (String url){
		   this.url=url;
	   }
	   public void start(){
		     if  (myThread1 == null){
		          myThread1= new Thread(this);
		          myThread1.start();
		     }
		  }
		 

		public void run(){

		  if (Thread.currentThread() == myThread1){
		     while(true){
		        try{
		          Thread.sleep(100000);
		        }catch (Exception ignored){
		           return;
		        }
		        
		        System.out.println("Running " +i);
                i++;
                gs= new OGetStatus();
   			 gs.execute(url);
		      
		     }
		   }
		}
		
		public void stop(){
		     if  (myThread1 != null){
		          myThread1.stop();
		          myThread1= null;
		     }
		 }
	   
   }
}