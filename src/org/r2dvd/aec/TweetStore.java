package org.r2dvd.aec;

import java.net.URL;

public class TweetStore {
String Tweet=null;
String Name =null;
long id=0;

void setTweet(String Tweet){
	//We should add some cleanup here:
	//Detect url's and replace with "link"
	//Detect RT and replace with ReTweeted  However what about pls RT?
   // System.out.println("Decoding Tweet"+Tweet);
	Tweet=Tweet.replaceFirst("RT","Retweeted");
	Tweet=Tweet.replaceAll("RT","Retweet!");

	Tweet=Tweet.replaceAll("#", ", Hash Tag ");
	
	Tweet=Tweet.replaceAll("WTF", ", What the Fuck ");
	
	int link=Tweet.indexOf("http://");
    int endlink=Tweet.indexOf(" ", link);
    if ((link>=0) && (endlink>link)){
       String sLink= Tweet.substring(link, endlink);
       URL url=null;
       try {
          url = new URL(sLink);
          //System.out.println( url.getHost());
       }catch(Exception et){
    	   System.out.println("URl can't be decoded");
       }
       //System.out.println(sLink);	
       Tweet=Tweet.replaceAll(sLink, ",Link to  "+url.getHost() );
    }
    	
	
	Tweet=Tweet.replaceAll("http://", ", Link to:");
	this.Tweet=Tweet;
}

void setName(String Name){
	this.Name=Name;
}

void setId(long id){
	this.id=id;
}

String getTweet(){
	return Tweet;
}

String getName(){
	return Name;
}

long getId(){
	return id;
}



}
