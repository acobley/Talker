package org.r2dvd.aec;

public class TweetStore {
String Tweet=null;
String Name =null;
void setTweet(String Tweet){
	//We should add some cleanup here:
	//Detect url's and replace with "link"
	//Detect RT and replace with ReTweeted  However what about pls RT?
	System.out.println(Tweet);
	Tweet.replaceAll("RT","Retweeted");
	System.out.println(Tweet);
	Tweet.replaceAll("#", "Hash Tag");
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
