package com.waikato.comp204.javalibrary;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by SadViper on 12/05/2017.
 */

public class Doc extends Observable implements Serializable, Runnable {

    protected static final long serialVersionUID = 42L;
    private String title;
    private Doc parent = null;
    private URL homeURL = null;
    private ArrayList<Doc> myDocList = new ArrayList<>();

    public Doc(Doc _parent, String _title, String url) {
        parent = _parent;
        title = _title;
        try {
            homeURL = new URL(url);
        } catch (Exception ex) {
        }
    }

    public Doc(Doc _parent, String _title) {
        parent = _parent;
        title = _title;

    }

    @Override
    public void run() {
            getDocs(homeURL);
            setChanged();
            notifyObservers();
            update();
    }

    public void update() {
        for (int i = 0; i < myDocList.size(); i++) {
            if (myDocList.get(i).getHomeURL() != null)
                while(Thread.activeCount() > 3) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                new Thread(myDocList.get(i)).start();
        }
    }

    @Override
    public String toString() {
        return title;
    }

    public void getDocs(URL url) {
        InputStream is = null;
        BufferedReader br;
        String line;
        Boolean startTable = false;

        try {
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                if (line.contains("<h2 title=\"")) {
                    //Log.d("ASD", getTitle(line));
                }
                if (line.contains("<table")) {
                    startTable = true;
                }
                if (startTable) {
                    if (line.contains("<caption><span>")) {
                        myDocList.add(getTableName(line));
                    } else if (line.contains("<td class=\"colFirst\"><a href=")) {
                        myDocList.add(getTableContent(line));
                        myDocList.get(myDocList.size() - 1).setHomeURL(getContentLink(line));
                    }
                }
                if (line.contains("</table>")) {
                    startTable = false;
                }
            }
            br.close();
            is.close();
        } catch (MalformedURLException mue) {
            Log.e("Web mue", mue.getMessage());
        } catch (IOException ioe) {
            Log.e("Web ioe", ioe.getMessage());
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
    }

    private Doc getTableName(String line) {
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
        Doc doc = new Doc(this, line);
        return doc;
    }

    private Doc getTableContent(String line) {
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
        if(line.contains("..")) {
            Log.d("LINE", line);
            Log.d("LINE", "WORKED");
            index = line.indexOf("..");
            Log.d("LINE", String.valueOf(index));
            remove = line.substring(index, index+2);
            Log.d("LINE", line);
            line = line.replace(remove, "");
            Log.d("LINE", line);
        }
        line = "https://docs.oracle.com/javase/8/docs/api/" + line;
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

    public ArrayList<Doc> getDocList() {
        return myDocList;
    }

    public URL getHomeURL() {
        return homeURL;
    }

    public void setHomeURL(String url) {
        try {
            homeURL = new URL(url);
        } catch (Exception ex) {
        }
    }

    public Doc getParent() {
        return (parent == null ? null : parent);
    }

    public String getTitle() {
        return title;
    }


}
