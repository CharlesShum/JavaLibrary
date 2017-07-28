package com.waikato.comp204.javalibrary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by SadViper on 11/05/2017.
 */

public class collectDocs extends Thread {

    URL urlDefault;
    URL parent;
    ArrayList<String> myDocList = new ArrayList<>();
    ArrayList<URL> myDocListURL = new ArrayList<>();

    public collectDocs() {
        try {
            urlDefault = new URL("https://docs.oracle.com/javase/8/docs/api/overview-summary.html");
        } catch (Exception ex) {

        }
    }

    @Override
    public void run() {
        Log.d("Hello Docs", "Starting get docs");
        try {
            getURL(new URL("https://docs.oracle.com/javase/8/docs/api/overview-summary.html"));
        } catch (Exception ex) {
            //URL has changed
        }
    }

    public void openList(int position) {
        URL url = myDocListURL.get(position);
        parent = url;
        Log.d("URL", url.toString());
        myDocList = new ArrayList<>();
        myDocListURL = new ArrayList<>();
        getURL(url);
    }
    public void openList() {
        parent = null;
        myDocList = new ArrayList<>();
        myDocListURL = new ArrayList<>();
        getURL(urlDefault);
    }

    public void openList(URL url) {
        parent = url;
        myDocList = new ArrayList<>();
        myDocListURL = new ArrayList<>();
        getURL(url);
    }

    public void getURL(URL url) {
        InputStream is = null;
        BufferedReader br;
        String line;
        Boolean startTable = false;

        try {
            //url = new URL("https://docs.oracle.com/javase/8/docs/api/java/lang/package-summary.html");
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                //Gets the Title of the page
                if (line.contains("<h2 title=\"")) {
                    Log.d("ASD", getTitle(line));
                }
                if (line.contains("<table")) {
                    startTable = true;
                    Log.d("startTable", "Table Start");
                }
                if (startTable) {
                    if (line.contains("<caption><span>")) {
                        Log.d("tableContent", line);
                        myDocList.add(getTableName(line));
                        myDocListURL.add(null);
                        Log.d("tableContent", getTableName(line));
                    } else if (line.contains("<td class=\"colFirst\"><a href=")) {
                        Log.d("tableContent", line);
                        myDocList.add(getTableContent(line));
                        myDocListURL.add(new URL(getContentLink(line)));
                    }

                }
                if (line.contains("</table>")) {
                    startTable = false;
                    Log.d("startTable", "Table End");
                }
                Log.d("Web", line);
            }
        } catch (MalformedURLException mue) {
            Log.e("Web mue", mue.getMessage()); //mue.printStackTrace()
        } catch (IOException ioe) {
            Log.e("Web ioe", ioe.getMessage()); //ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
    }

    private String getTableName(String line) {
        String remove;
        int index = line.indexOf(">");
        remove = line.substring(0, index + 1);
        line = line.replace(remove, "");
        index = line.indexOf(">");
        remove = line.substring(0, index + 1);
        line = line.replace(remove, "");
        index = line.indexOf("<");
        remove = line.substring(index, line.length());
        line = line.replace(remove, "");
        Log.d("Title_Works", line);
        return line;
    }

    private String getTableContent(String line) {
        return getTableName(line);
    }

    private String getContentLink(String line) {
        String remove;
        int index = line.indexOf(">");
        remove = line.substring(0, index + 1);
        line = line.replace(remove, "");
        index = line.indexOf("\"");
        remove = line.substring(0, index + 1);
        line = line.replace(remove, "");
        index = line.indexOf("\"");
        remove = line.substring(index, line.length());
        line = line.replace(remove, "");
        line = "https://docs.oracle.com/javase/8/docs/api/" + line;
        Log.d("Title_Link", line);
        return line;
    }

    private String getTitle(String line) {
        //Gets the Title of the page
        String remove;
        int index = line.indexOf(">");
        remove = line.substring(0, index + 1);
        line = line.replace(remove, "");
        index = line.indexOf(" ");
        remove = line.substring(0, index + 1);
        line = line.replace(remove, "");
        index = line.indexOf("<");
        remove = line.substring(index, line.length());
        line = line.replace(remove, "");
        return line;
    }

    public ArrayList getDocList() {
        return myDocList;
    }

    public ArrayList getDocListURL() {
        return myDocListURL;
    }

    public URL getParent() {
        return (parent == null ? null : parent);
    }

}
