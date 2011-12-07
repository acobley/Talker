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
    
    private OloginSetup ols =null;
    private OGetResponse grs=null;
	
    
    TextView textView;
	WebView webview;
	OAuthService s =null;
	Token requestToken=null; 
	String authURL =null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView)findViewById(R.id.textview);
        webview = (WebView) findViewById(R.id.webView1);
        speakBtn = (Button)findViewById(R.id.Speak);

     // Check to be sure that TTS exists and is okay to use
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
        ols= new OloginSetup();
        ols.execute();
        
      //attach WebViewClient to intercept the callback url
	       webview.setWebViewClient(new WebViewClient(){
	       	@Override
	    	public boolean shouldOverrideUrlLoading(WebView view, String url){

	    		//check for our custom callback protocol
	       //otherwise use default behavior
	    		if(url.startsWith("oauth")){
	    			//authorization complete hide webview for now.
	    			webview.setVisibility(View.GONE);
	    			 grs= new OGetResponse();
	    			 grs.execute(url);
	    			 
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
    	mTts.speak("Hello World", TextToSpeech.QUEUE_ADD, null);
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
                Log.v(TAG, "Pico is installed okay");
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
                // missing data, install it
                Log.v(TAG, "Need language stuff: " + resultCode);
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
            default:
                Log.e(TAG, "Got a failure. TTS apparently not available");
            }
        }
        else {
            // Got something else
        }
    }

    
  


    private class OloginSetup extends AsyncTask<String, Void, String> {
    

    	@Override
        protected void onPreExecute() {
            Log.v("AsyncTask", "onPreExecute");
        }

    	
        protected String doInBackground(String... urls) {
        	
        	Log.v(TAG, "Starting async");
        	//set up service and get request token as seen on scribe website
            //https://github.com/fernandezpablo85/scribe-java/wiki/Getting-Started
        	
           s = new ServiceBuilder()
            .provider(TwitterApi.class)
            .apiKey(APIKEY)
            .apiSecret(APISECRET)
    		.callback(CALLBACK)
    		.build();
            Log.v(TAG, "got Service");
            
            try {
            	 requestToken= s.getRequestToken();
            }catch(Exception et){
            	Log.v(TAG, "didn't get token  "+et);
            	return (null);
            }
    		Log.v(TAG, "got token");
    		authURL = s.getAuthorizationUrl(requestToken);
    		Log.v(TAG, "got authurl" + authURL);

    	    //send user to authorization page
    		webview.loadUrl(authURL); 
            return null ;
        }

    
    
        
        protected void onPostExecute(String url) {
        	Log.v(TAG, "On Post Execute");   		
        	
        	
            
        }
    }
    
    
    private class OGetResponse extends AsyncTask<String, Void, String> {
    	 Response response=null;

    	@Override
        protected void onPreExecute() {
            Log.v("Oget Response", "onPreExecute");
        }
    	 protected String doInBackground(String... urls) {
         	
         	Log.v(TAG, "Starting Response " +urls.length);
         	int count = urls.length;
            
            for (int i = 0; i < count; i++) {
            	Log.v(TAG, "url "+urls[i]);	
         	  response=GetResponse(urls[i]);
         	   Log.v(TAG, "response "+response.getBody());	
         	   
            }
            return null;
         	
    	 }
    	 protected void onPostExecute(String url) {
         	Log.v(TAG, "On Post Execute");   		
         	textView.setText(response.getBody());
         	
             
         }
    	 
    	 Response GetResponse(String url){
         	Uri uri = Uri.parse(url);
 			String verifier = uri.getQueryParameter("oauth_verifier");
 			Verifier v = new Verifier(verifier);

 			//save this token for practical use.
 			Token accessToken = s.getAccessToken(requestToken, v);

 			//host twitter detected from callback oauth://twitter
 			if(uri.getHost().equals("twitter")){
 				//requesting xml because its easier
            //for human to read as it comes back
    			OAuthRequest req = new OAuthRequest(Verb.GET,
                    "http://api.twitter.com/1/account/verify_credentials.xml");
    			s.signRequest(accessToken, req);
    			Response response = req.send();
    			XMLParser xmlparse=new XMLParser();
    			return response;
 			}
 			return null;
 			
         }
    }
}