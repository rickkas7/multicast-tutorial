package com.rickk.multicast;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listView1;
    private ArrayAdapter<String> listAdapter1;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // You must use the WifiManager to create a multicast lock in order to receive
        // multicast packets. Only do this while you're actively receiving data, because
        // it decreases battery life.
        // See: https://bugreports.qt.io/browse/QTBUG-34111
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("HelloAndroid");
            lock.acquire();
        }

        // Standard code for initializing a ListView from an ArrayList
        listView1 = (ListView) findViewById(R.id.listView1);
        items = new ArrayList<String>();
        listAdapter1 = new ArrayAdapter<String>(this, R.layout.simple_list_row, items);
        listView1.setAdapter(listAdapter1);

        // This thread receives the packets, as you can't do it from the main thread
        runThread();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // http://stackoverflow.com/questions/11140285/how-to-use-runonuithread
    private void runThread() {

        new Thread() {
            public void run() {
                MulticastSocket socket = null;
                InetAddress group = null;

                try {
                    socket = new MulticastSocket(7234);
                    group = InetAddress.getByName("239.1.1.234");
                    socket.joinGroup(group);

                    DatagramPacket packet;
                    while (true) {
                        byte[] buf = new byte[256];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        // Java byte values are signed. Convert to an int so we don't have to deal with negative values for bytes >= 0x7f (unsigned).
                        int[] valueBuf = new int[2];
                        for (int ii = 0; ii < valueBuf.length; ii++) {
                            valueBuf[ii] = (buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256;
                        }

                        final int value = (valueBuf[0] << 8) | valueBuf[1];

                        String s = Integer.toString(value);
                        Log.d(TAG, s);

                        synchronized (items) {
                            items.add(s);
                        }

                        // We're running on a worker thread here, but we need to update the list view from the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (items) {
                                    listAdapter1.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
                catch(IOException e) {
                    System.out.println(e.toString());
                }
                finally {
                    if (socket != null) {
                        try {
                            if (group != null) {
                                socket.leaveGroup(group);
                            }
                            socket.close();
                        }
                        catch(IOException e) {

                        }
                    }
                }
            }


        }.start();
    }
}
