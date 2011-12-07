package org.r2dvd.aec;

public class AuthorStore {
	private long numPosts;
	private String Name;
	private String twitterName;
	private String emailName;
	private String bio;
	private String Address;
	private String Tel;
	private String GoogleName;
	private String YahooName;
	private long UserId=0;
	private long ProfilePic=-1;
	
	public AuthorStore(){
	
	}
	
	
	public long getnumPosts(){
		return numPosts;
	}
	
	public String getname(){
		return Name;
	}
	
	public String gettwitterName(){
		return twitterName;
	}
	
	public String getemailName(){
		return emailName;
	}
	
	public String getbio(){
		return bio;
	}
	public String getaddress(){
		return Address;
	}
	public String gettel(){
		return Tel;
	}
	
	//A convenient method to set all variables at once.
	public void setAll(int numPosts, String Name,String twitterName,String emailName, String bio,String Address,String Tel){
		this.Name=Name;
		this.numPosts=numPosts;
		this.twitterName=twitterName;
		this.emailName=emailName;
		this.bio=bio;
		this.Address=Address;
		this.Tel=Tel;
	}
	
	public void setnumPosts(long Posts){
		this.numPosts=Posts;
	}
	
	public void setname(String Name){
		this.Name=Name;
	}
	
	public void settwitterName(String TwitterName){
		this.twitterName=TwitterName;
	}
	
	public void setemailName(String Email){
		this.emailName=Email;
	}
	
	public void setbio(String bio){
		this.bio=bio;
	}
	public void setaddress(String address){
		this.Address=address;
	}
	public void settel(String tel){
		this.Tel=tel;
	}
	
	public void setGoogleName(String GoogleName){
		this.GoogleName=GoogleName;
	}
	
	public String getGoogleName(){
		return GoogleName;
	}
	
	public void setYahooName(String YahooName){
		this.YahooName=YahooName;
	}
	
	public String getYahooName(){
		return YahooName;
	}
	
	public void setUserId(long UserId){
		if (this.UserId ==0)
			this.UserId=UserId;
	}
	
	public long getUserId(){
		return this.UserId;
	}
	
	public void setProfilePic(long picId){
		this.ProfilePic=picId;
	}
	public long getProfilePic(){
		return this.ProfilePic;
	}
}
