package com.example.android.rssreader.utils;


import android.text.Html;
import android.util.Log;

import com.example.android.rssreader.model.RSSFeed;
import com.example.android.rssreader.model.RSSItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static com.example.android.rssreader.model.RSSTags.TAG_CATEGORY;
import static com.example.android.rssreader.model.RSSTags.TAG_DESCRIPTION;
import static com.example.android.rssreader.model.RSSTags.TAG_IMAGE;
import static com.example.android.rssreader.model.RSSTags.TAG_ITEM;
import static com.example.android.rssreader.model.RSSTags.TAG_LASTBUILDDATE;
import static com.example.android.rssreader.model.RSSTags.TAG_LINK;
import static com.example.android.rssreader.model.RSSTags.TAG_PUBDATE;
import static com.example.android.rssreader.model.RSSTags.TAG_TITLE;
import static com.example.android.rssreader.model.RSSTags.TAG_URL;

/** This class is used to parse the XML file
 * Is SAX parser's event handler*/
public class RSSFeedHandler extends DefaultHandler {
    // RSS 2.0 specification:
    // https://validator.w3.org/feed/docs/rss2.html

    // define model objects
    RSSFeed feed;
    RSSItem item;

    StringBuilder descriptionBuilder;

    boolean isTitle = false;
    boolean isDescription = false;
    boolean isLink = false;
    boolean isPubDate = false;
    boolean isLastBuildDate = false;
    boolean isItem = false;
    // channel may have image
    boolean isImage = false;
    boolean isUrl = false;
    private boolean isCategory=false;

    public RSSFeedHandler() {
    }

    /**
     * Returns RSSFeed object that was constructed from XML when SAX finished parsing it
     */
    public RSSFeed getFeed() {
        return feed;
    }

    // executed when parser starts reading the XML document
    public void startDocument() throws SAXException {
        // instantiate model objects so that they can be used by other methods in this class
        feed = new RSSFeed();
        item = new RSSItem(); // an item to temporarily store feed data
    }

    @Override
    public void endDocument() throws SAXException {
    }

    // Executed after parser reads a start element such as <item>
    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        Log.i("RSSHandler", "startElement()\t" + qName);
        // qName i qualified name of the element (if tag was <item> then qName will be "item")

        // depending on passed element name check whether this element is needed by the News Reader app
        // TODO FACTOR OUT CONSTANTS
        if (qName.equals(TAG_ITEM)) {
            // create a new item
            item = new RSSItem();
            isItem = true;
            return;
        } else if (qName.equals(TAG_TITLE)) {
            isTitle = true;
            return;
        } else if (qName.equals(TAG_DESCRIPTION)) {
            descriptionBuilder=new StringBuilder();
            isDescription = true;
            return;
        } else if (qName.equals(TAG_LINK)) {
            isLink = true;
            return;
        } else if (qName.equals(TAG_PUBDATE)) {
            isPubDate = true;
            return;
        }else if(qName.equals(TAG_LASTBUILDDATE)){
            isLastBuildDate=true;
            return;
        }else if(qName.equals(TAG_IMAGE)){
            isImage=true;
            return;
        }else if(qName.equals(TAG_URL)){
            isUrl=true;
            return;
        }else if(qName.equals(TAG_CATEGORY)){
            isCategory=true;
            return;
        }
    }

    // executed after the parser reads an end elements such as </item>
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {
        Log.i("RSSHandler", "endElement()\t\t" + qName);
        // add current item to the feed
        if (qName.equals(TAG_ITEM)) {
            feed.addItem(item);
            isItem = false;
            Log.d("RSSFeedHandler", item.toString());
            return;
        }else if(qName.equals(TAG_IMAGE)){
            isImage=false;
        }else if(qName.equals(TAG_DESCRIPTION)){
            if(isItem){
                isDescription=false;
                item.setDescription(descriptionBuilder.toString());
                descriptionBuilder=null;
            }
        }
    }

    // Executed when parser reads the characters within an element (<item>This will trigger characters()</item>)
    @Override
    public void characters(char ch[], int start, int length) {
        String s = new String(ch, start, length);
        Log.e("RSSHandler", "characters()\t\t\t" + s);

        // TODO - refactor: make main if to be if(isItem) and etc..
        // store this string in the appropriate RSSFeed or RSSItem
//        if (isTitle) {
//            Log.d("RSSHandler", "isTitle");
//            if (!isItem) {
//                // title has not been read before
//                // => this belong to RSSFeed
//                feed.setTitle(s);
//            } else {
//                // title was read before
//                // => this belongs to RSSItem
//                item.setTitle(s);
//            }
//            isTitle = false;
//        } else if (isLink) {
//            Log.d("RSSHandler", "isLink");
//            item.setLink(s);
//            isLink = false;
//        } else if (isDescription) {
//            Log.d("RSSHandler", "isDescription");
//            if(isItem){
//                if (s.startsWith("<")) {
//                    // links to somewhere
//                    item.setDescription("No description available.");
//                } else {
//                    item.setDescription(s);
//                }
//            }else{
//                // guaranteed to exist due to RSS 2.0 specification
//                feed.setDescription(s);
//            }
//            isDescription = false;
//        } else if (isPubDate) {
//            Log.d("RSSHandler", "isPubDate");
//            if (!isItem) {
//                //feed.setPubDate(s);
//            } else {
//                item.setPubDate(s);
//            }
//            isPubDate = false;
//        } else if (isLastBuildDate) {
//            feed.setLastBuildDate(s);
//            isLastBuildDate=false;
//        }else if(isImage && isUrl){
//            feed.setImageUri(s);
//            isImage=false;
//            isUrl=false;
//        }

        if(isItem){
            // RSSItem (item)
            if(isTitle){
                item.setTitle(s);
                isTitle=false;
            }else if(isDescription){
//                if (s.startsWith("<")) {
                    // TODO somehow save dat link (test with CNN)
                    // CDATA?
                    // links to somewhere
//                    item.setDescription("No description available.");
//                } else {
                // because stupid "clever" rss providers like to insert html tags into description
//                String prevDesc = item.getDescription()
//                    item.setDescription(item.getDescription()+s);
//                }
//                isDescription=false;
                descriptionBuilder.append(s);
            }else if(isLink){
                item.setLink(s);
                isLink=false;
            }else if(isPubDate){
                item.setPubDate(s);
                isPubDate=false;
            }else if(isCategory){
                item.setCategory(s);
                isCategory=false;
            }
        }else{
            // RSSFeed (channel)
            if(isTitle){
                feed.setTitle(s);
                isTitle=false;
            }else if(isDescription){
                feed.setDescription(s);
                isDescription=false;
            }else if(isLink){
                feed.setLink(s);
                isLink=false;
            }else if(isLastBuildDate){
                feed.setLastBuildDate(s);
                isLastBuildDate=false;
            }else if(isImage){
                if(isUrl){
                    feed.setImageUri(s);
                    isUrl=false;
                }
            }
        }
    }

    /* How it Works?
    * 1. startDocument() is called, where new feed and item objects are instantiated
    * 2. startElement() is called for each tag, followed by characters() and endElement()
    *
    * For example:
    * startElement() is called, qName = "description"
    * isDescription is set to true in our if-else clauses
    * characters() is called, else if (isDescription) is triggered
    * we know that if first character inside <description> tag is "<"
    * then there is no description text available.
    * Else item.setDescription(s);
    * In the end set isDescription to false, so it wont trigger again for this item.
    * endElement() is called. If QName is "item" then we finished parsing 1 item
    * => add it to feed's list
    * . . . parse more items . . .
    * endDocument() called. Parsing finished, we can use getFeed */

}