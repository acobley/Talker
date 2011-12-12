package org.r2dvd.aec;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.scribe.model.Response;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TweetParse extends DefaultHandler{
	 private HashMap ElementsMap = new HashMap();
	  private int CurrentState=-1;
	  TweetStore au;
	  List<TweetStore> tsl = new LinkedList<TweetStore>();
	  private StringBuilder tmpString=null;
	  boolean inUser=false;
	public TweetParse(){
		 ElementsMap.put("screen_name", 0);
		 ElementsMap.put("text", 1);
		 ElementsMap.put("id", 2);
		 ElementsMap.put("user", 3);
		 
		 
		 au = new TweetStore();

	}
	
	public List<TweetStore> GetStore(){
		
		return tsl;
	}
	
	public List<TweetStore> GetDetails(Response body){
		
		
		//DefaultHandler handler = new XMLParser();
		TweetParse handler=new TweetParse();
      // Use the default (non-validating) parser
      SAXParserFactory factory = SAXParserFactory.newInstance();
      List<TweetStore> aul=null;
      try {
          
      	synchronized (this){
          // Parse the input
          SAXParser saxParser = factory.newSAXParser();
          saxParser.parse(body.getStream(), handler);
          
          aul=handler.GetStore();
          //Collections.reverse(aul);
          //reverse it
      	}

      } catch (SAXParseException spe) {
         // Error generated by the parser
         System.out.println("\n** Parsing error"
            + ", line " + spe.getLineNumber()
            + ", uri " + spe.getSystemId());
         System.out.println("   " + spe.getMessage() );

         // Use the contained exception, if any
         Exception  x = spe;
         if (spe.getException() != null)
             x = spe.getException();
         x.printStackTrace();

      } catch (SAXException sxe) {
         // Error generated by this application
         // (or a parser-initialization error)
         Exception  x = sxe;
         if (sxe.getException() != null)
             x = sxe.getException();
         x.printStackTrace();

      } catch (ParserConfigurationException pce) {
          // Parser with specified options can't be built
          pce.printStackTrace();

      } catch (IOException ioe) {
         // I/O error
         ioe.printStackTrace();
      }
		
		
		
		
		return aul;
		
	}
	
	
	 public void startElement(String namespaceURI,
           String lName, // local name
           String qName, // qualified name
           Attributes attrs)
throws SAXException
{
//indentLevel++;
		 //System.out.print("ELEMENT: ");
		 tmpString=new StringBuilder();
		 String eName = lName; // element name
		 if ("".equals(eName)) 
			 eName = qName; // namespaceAware = false
		
		 if (ElementsMap.containsKey(eName)){
			 //System.out.println(eName);
			 Integer ICurrentState =(Integer) ElementsMap.get(eName);
			 try{
				 CurrentState=ICurrentState.intValue();
				 if (CurrentState==3){
					 inUser=true;
				 }
			 }catch(Exception et){
				 System.out.println("Can't convert CurrentState to int");
			 }
		 }else{
			 CurrentState=-1;
		 }

}

	 public void characters(char buf[], int offset, int len)
	    throws SAXException
	    {
	         String s = new String(buf, offset, len);
	         tmpString.append(s);
	        
	    }	 
	 

	 public void ignorableWhitespace(char buf[], int offset, int len)
	    throws SAXException
	    {
		 String s = new String(buf, offset, len); 
		
		    System.out.println("Whitespace: "+s+ " offset "+offset+"len "+len);
		    
		
	    }
	 
	 
	 
	 public void endElement(String uri,
             String localName,
             String qName)
      throws SAXException{
		//System.out.println("EndElement"+uri+","+localName+","+qName);
		 String s = tmpString.toString();
		 switch(CurrentState){
     	case 0:
     		   if (!s.trim().equals("")){
	        	   //System.out.println(s);
     	           //tmpString.append(s);
     			   au.setName(s);
     			   //System.out.println("Set "+au.getName());
     			   tsl.add(au);
     		   }
     			break;
     	case 1:
     		   if (!s.trim().equals("")){
	        	  //System.out.println(s);
     		      //tmpString.append(s);
     			  au= new TweetStore();
     			  au.setTweet(s); // This is Tweet
     			
     			  //System.out.println("Set"+au.getTweet());
     		   }
     			
     			break;
     	case 2:
  		   if (!s.trim().equals("")){
	        	  //System.out.println(s);
  		      //tmpString.append(s);
  			  if (inUser ==false){ 
  			    long id=0;
	  			 try{
	  				 id=Long.parseLong(s);
	  			 }catch(Exception et){
	  				 System.out.println("Can't parse id "+s);
	  			 }
	  			  au.setId(id); 
	  			
	  			  //System.out.println("Tweet ParseSet"+au.getId()+" : "+s);
  			  }
  		   }
  			
  			break;		

	
     	default:break;
     }
		 
		 
		 CurrentState=-1; //We've processed that now
		 int iName=0;
		 int iLocalName=0;
		 if  (qName !=null){
		    Integer IName=(Integer) ElementsMap.get(qName);
		    if (IName !=null)
		       iName=IName.intValue();
		 }
		 if (localName!=null){
		    Integer ILocalName =(Integer) ElementsMap.get(localName);
		    if (ILocalName!=null)
		       iLocalName=ILocalName.intValue();
		 }
		 if ((iName==3) || (iLocalName==3) ){
			 inUser=false;
		 }

	 }
	 
	
	 
	 
	 
}
