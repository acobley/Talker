package org.r2dvd.aec;

public class TweetStore {
String Tweet=null;
String Name =null;
void setTweet(String Tweet){
	//We should add some cleanup here:
	//Detect url's and replace with "link"
	//Detect RT and replace with ReTweeted  However what about pls RT?

	Tweet=Tweet.replaceFirst("RT","Retweeted");
	Tweet=Tweet.replaceAll("RT","Retweet!");

	Tweet=Tweet.replaceAll("#", "Hash Tag");
	
	int link=Tweet.indexOf("http://");
    int endlink=Tweet.indexOf(" ", link);
    if ((link>=0) && (endlink>link)){
       String sLink= Tweet.substring(link, endlink);
       System.out.println(sLink);	
    }
    	
	
	Tweet=Tweet.replaceAll("http://", "Link to:");
	this.Tweet=Tweet;
}

void setName(String Name){
	this.Name=Name;
}

String getTweet(){
	return Tweet;
}

String getName(){
	return Name;
}


}
