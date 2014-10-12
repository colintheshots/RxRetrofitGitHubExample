package com.colintheshots.rxretrofitexample1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.colintheshots.rxretrofitexample1.adapters.GistAdapter;
import com.colintheshots.rxretrofitexample1.adapters.GistFilesAdapter;
import com.colintheshots.rxretrofitexample1.models.Gist;
import com.colintheshots.rxretrofitexample1.models.GistDetail;
import com.colintheshots.rxretrofitexample1.network.GitHubNetworkService;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import static butterknife.ButterKnife.findById;


public class MainActivity extends Activity
        implements GitHubNetworkService.GitHubCallback, ServiceConnection {
    @InjectView(R.id.listView)
    ListView mListView;

    private GitHubNetworkService mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().unbindService(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, GitHubNetworkService.class);
        getApplicationContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(this);
            mBound = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayGistList(final List<Gist> gists) {
        if (gists.size()>0 && mListView!=null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.setAdapter(new GistAdapter(MainActivity.this, gists));
                }
            });

        }
    }

    @Override
    public void displayFileList(final GistDetail gistDetail) {
        if (gistDetail.getFiles().size()>0 && mListView!=null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.setAdapter(new GistFilesAdapter(MainActivity.this, gistDetail));
                }
            });
        }
    }

    @OnItemClick(R.id.listView)
    public void pickItem(LinearLayout layout) {
        TextView tv = findById(layout, R.id.hiddenIdTextView);
        if (tv!=null) {
            mService.getGist(tv.getText().toString());
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        GitHubNetworkService.GitHubBinder binder = (GitHubNetworkService.GitHubBinder) iBinder;
        mService = binder.getService();
        if (GitHubNetworkService.GITHUB_PERSONAL_ACCESS_TOKEN.equals("XXX")) {
            Toast.makeText(getApplicationContext(), "GitHub Personal Access Token is Unset!", Toast.LENGTH_LONG).show();
        }
        mService.setCallback(this);
        mService.getGists();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService.unsetCallback();
        mService = null;
    }
}
