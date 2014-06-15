package com.gamerspot.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.gamerspot.R;
import com.gamerspot.beans.NewsFeed;
import com.gamerspot.database.DAO;
import com.gamerspot.extra.NewsFeedsAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Adrian on 13-Jun-14.
 */
public class NewsHeadlinesFragment extends ListFragment {

    private Context context;
    private static FeedFetcherTask downloadTask;
    private NewsFeedsAdapter feedsAdapter;

    private ArrayList<NewsFeed> feedList;
    private DAO dao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();

        feedList = new ArrayList<NewsFeed>();

        downloadTask = new FeedFetcherTask(context);
        dao = new DAO(context);

        feedList = dao.getAllFeeds();
        feedsAdapter = new NewsFeedsAdapter(context, feedList);
        setListAdapter(feedsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        downloadTask.execute();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class FeedFetcherTask extends AsyncTask<String, Void, Void> {

        private Context context;
        private String[] pcFeedUrls;
        private String[] xboxFeedUrls;
        private String[] playstationFeedUrls;
        private String[] nintendoFeedUrls;
        private String[] mobileFeedUrls;

        private ArrayList<NewsFeed> newFeeds;
        private int newRowsInserted = 0;

        public FeedFetcherTask(Context c) {

            context = c;
            pcFeedUrls = c.getResources().getStringArray(R.array.pc_feeds);
            xboxFeedUrls = c.getResources().getStringArray(R.array.xbox_feeds);
            playstationFeedUrls = c.getResources().getStringArray(R.array.playstation_feeds);
            nintendoFeedUrls = c.getResources().getStringArray(R.array.nintendo_feeds);
            mobileFeedUrls = c.getResources().getStringArray(R.array.mobile_feeds);

            newFeeds = new ArrayList<NewsFeed>();
        }

        @Override
        protected Void doInBackground(String... params) {

            long time = System.currentTimeMillis();

            getActivity().setProgressBarIndeterminateVisibility(true);

            getNewsForPc();
            getNewsForXbox();
            getNewsForPlaystation();
            getNewsForNintendo();
            getNewsForMobile();

            storeNewsInDatabase();

            long finish = System.currentTimeMillis()-time;
            Log.i("time elapsed", finish / 1000.0 + "");

            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            super.onPostExecute(o);

            feedList.addAll(0, newFeeds);
            feedsAdapter.notifyDataSetChanged();
            retainListViewPosition();

            getActivity().setProgressBarIndeterminateVisibility(false);

            if(newFeeds.size() > 0 ) {
                Toast.makeText(context, newFeeds.size() + " "+context.getResources().getString(R.string.new_feeds_count), Toast.LENGTH_LONG).show();
            }

        }

        private void retainListViewPosition(){

            int index = getListView().getFirstVisiblePosition() + newFeeds.size();
            View v = getListView().getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            getListView().setSelectionFromTop(index, top);
        }

        private void getNewsForPc(){

            int platform = NewsFeed.PLATFORM_PC;

            for(String url: pcFeedUrls) {
                parseRssFeed(url, platform);
            }
        }

        private void getNewsForXbox(){

            int platform = NewsFeed.PLATFORM_XBOX;

            for(String url: xboxFeedUrls) {
                parseRssFeed(url, platform);
            }
        }

        private void getNewsForPlaystation(){

            int platform = NewsFeed.PLATFORM_PLAYSTATION;

            for(String url: playstationFeedUrls) {

                Log.i("URL_STRING", url);

                parseRssFeed(url, platform);
            }
        }

        private void getNewsForNintendo(){

            int platform = NewsFeed.PLATFORM_NINTENDO;

            for(String url: nintendoFeedUrls) {
                parseRssFeed(url, platform);
            }
        }

        private void getNewsForMobile(){

            int platform = NewsFeed.PLATFORM_MOBILE;

            for(String url: mobileFeedUrls) {
                parseRssFeed(url, platform);
            }
        }

        private void parseRssFeed(String urlIn, int platformIn){

            try {
                URL url = new URL(urlIn);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                InputSource is = new InputSource(br);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(is);

                NodeList nodeList = document.getElementsByTagName("item");

                String provider = document.getElementsByTagName("title").item(0).getTextContent();
                int platform = platformIn;

                Node node;
                NewsFeed feed = null;
                String title;
                String link;
                String description;
                NodeList guidNodeList;
                String guid;
                String pubDate;
                NodeList creatorNodeList;
                String creator;

                Element element;

                for(int i=0;i<nodeList.getLength(); i++) {

                    node = nodeList.item(i);

                    if(node.getNodeType() == Node.ELEMENT_NODE) {

                        element = (Element) node;
                        feed = new NewsFeed();

                        title = element.getElementsByTagName("title").item(0).getTextContent();
                        feed.setTitle(title);

                        link = element.getElementsByTagName("link").item(0).getTextContent();
                        feed.setLink(link);

                        description = element.getElementsByTagName("description").item(0).getTextContent();
                        feed.setDescription(description);

                        guidNodeList = element.getElementsByTagName("guid");

                        if(guidNodeList == null | guidNodeList.getLength() < 1){
                            feed.setGuid(link);
                        }
                        else{
                            guid = guidNodeList.item(0).getTextContent();
                            feed.setGuid(guid);
                        }

                        pubDate = element.getElementsByTagName("pubDate").item(0).getTextContent();
                        feed.setDate(pubDate);

                        creatorNodeList = element.getElementsByTagName("dc:creator");

                        if(creatorNodeList == null | creatorNodeList.getLength() <1) {
                            feed.setCreator(provider);
                        }
                        else {
                            creator = creatorNodeList.item(0).getTextContent();
                            feed.setCreator(creator);
                        }

                        feed.setProvider(provider);
                        feed.setPlatform(platform);

                        newFeeds.add(feed);
                    }
                }
            }
            catch (ConnectException ce) {
                Log.i("EXCEPTION", "ConnectException");
                Toast.makeText(getActivity().getApplicationContext(), getActivity().getApplicationContext().getResources().getString(R.string.connection_exception_error), Toast.LENGTH_SHORT).show();
            }

            catch(UnknownHostException uhe){
                uhe.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("EXCEPTION", "Exception");
            }
        }

        private void storeNewsInDatabase(){

            newFeeds = dao.insertAllFeeds(newFeeds);
        }
    }
}