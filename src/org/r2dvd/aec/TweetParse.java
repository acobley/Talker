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
import java.util.LinkedList;
import java.util.List;

public class TweetParse extends DefaultHandler{
	 private HashMap ElementsMap = new HashMap();
	  private int CurrentState=-1;
	  TweetStore au;
	  List<TweetStore> tsl = new LinkedList<TweetStore>();
	public TweetParse(){
		 ElementsMap.put("screen_name", 0);
		 ElementsMap.put("text", 1);
		 
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
		 String eName = lName; // element name
		 if ("".equals(eName)) 
			 eName = qName; // namespaceAware = false
		
		 if (ElementsMap.containsKey(eName)){
			 System.out.println(eName);
			 Integer ICurrentState =(Integer) ElementsMap.get(eName);
			 try{
				 CurrentState=ICurrentState.intValue();
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
	        //System.out.println("CHARS:   ");
	        String s = new String(buf, offset, len);
	        
	        if (au ==null)
	        	System.out.println("Hmm, why is au null ?");
	        switch(CurrentState){
	        	case 0: if (!s.trim().equals(""))
		        			System.out.println(s);
	        	       au= new TweetStore();
	        			au.setName(s);
	        			System.out.println("Set "+au.getName());
	        			
	        			CurrentState=-1; //We've processed that now
	        			break;
	        	case 1:	if (!s.trim().equals(""))
		        			System.out.println(s);
	        			au.setTweet(s); // This is Tweet
	        			
	        			System.out.println("Set"+au.getTweet());
	        			tsl.add(au);
	        			CurrentState=-1; //We've processed that now
	        			break;

		
	        	default:break;
	        }
	    }	 
}
