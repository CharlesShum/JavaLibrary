package com.waikato.comp204.javalibrary;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    Doc doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Hello", "onCreate");
        //doc = new Doc( null, "java");asd
        //doc = load();
        //if (true) { //doc == null) {
        Log.d("LOAD", "Load null");
        doc = new Doc(null, "java", "https://docs.oracle.com/javase/8/docs/api/overview-summary.html");
        doc.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                    }
                });
            }
        });
        new Thread(doc).start();
        ListView lv = (ListView) findViewById(R.id.ListViewDocs);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doc = doc.getDocList().get(position);
                updateList();
            }
        });
    }

    public Doc load() {
        try {
            String filePath = getApplicationContext().getFilesDir().getPath() + "/Docs.txt";
            File f = new File(filePath);
            FileInputStream fis = new FileInputStream(new File(f.getAbsolutePath()));
            ObjectInputStream is = new ObjectInputStream(fis);
            Doc doc = (Doc) is.readObject();
            is.close();
            fis.close();
            return doc;
        } catch (Exception ex) {
            Log.d("LOAD", ex.getMessage());
        }
        return null;
    }

    public void save(Doc doc) {
        try {
            String filePath = getApplicationContext().getFilesDir().getPath() + "/Docs.txt";
            File f = new File(filePath);
            FileOutputStream fos = new FileOutputStream(new File(f.getAbsolutePath()), true);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(doc);
            os.close();
            fos.close();
        } catch (InvalidClassException inex) {
        } catch (NotSerializableException nsex) {
            Log.d("LOADSAVE", nsex.getMessage());
        } catch (IOException ioe) {
        } catch (IllegalArgumentException iaex) {
            Log.d("LOADSAVE", iaex.getMessage());
        } catch (Exception ex) {
            Log.d("LOADSAVE", ex.getMessage());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (doc.getParent() != null) {
                doc = doc.getParent();
                updateList();
            } else
                this.finish();
        } else if (keyCode == 82) {
            Log.d("KEY82", doc.toString());
        }
        Log.d("KEY", String.valueOf(keyCode));
        return true;
    }

    public void updateList() {
        if (doc.getDocList() == null)
            return;
        ListView lv = (ListView) findViewById(R.id.ListViewDocs);
        ArrayAdapter<Doc> myArrayAdapter = new MyAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, doc.getDocList(), doc);
        lv.setAdapter(myArrayAdapter);
        lv.setTextFilterEnabled(false);
        try {
            getActionBar().setTitle(doc.getTitle());
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        save(doc);
        Log.d("Hello", "onPause");
    }

    @Override
    protected void onResume() {
        Log.d("Hello", "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Hello", "onDestroy");
    }
}
