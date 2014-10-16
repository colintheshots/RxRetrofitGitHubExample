package com.colintheshots.rxretrofitexample1.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.colintheshots.rxretrofitexample1.models.Gist;
import com.colintheshots.rxretrofitexample1.models.GistDetail;

import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Provides access to GitHub REST API through a bound service.
 *
 * While this service isn't necessary in this simple example,
 * one may use a service to cache returned data and provide it
 * to any activity.
 *
 * Created by colin.lee on 10/10/14.
 */
public class GitHubNetworkService extends Service {

    /** The GitHub REST API Endpoint */
    public final static String GITHUB_BASE_URL = "https://api.github.com";

    /** Set this variable to your GitHub personal access token */
    public final static String GITHUB_PERSONAL_ACCESS_TOKEN = "XXX";

    private GitHubClient mGitHubClient;
    private IBinder mBinder = new GitHubBinder();
    private GitHubCallback mCallback;

    /**
     * Retrofit interface to GitHub API methods
     */
    public interface GitHubClient {
        @GET("/gists")
        Observable<List<Gist>> gists();

        @GET("/gists/{id}")
        Observable<GistDetail> gist(@Path("id") String id);
    }

    /**
     * Callback interface to the activity
     */
    public interface GitHubCallback {
        void displayGistList(final List<Gist> gists);
        void displayFileList(final GistDetail gistDetail);
    }

    public class GitHubBinder extends Binder {
        public GitHubNetworkService getService() {
            return GitHubNetworkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mGitHubClient == null) {
            mGitHubClient = new RestAdapter.Builder()
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addHeader("Authorization", "token " + GITHUB_PERSONAL_ACCESS_TOKEN);
                        }
                    })
                    .setEndpoint(GITHUB_BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.HEADERS).build()
                    .create(GitHubClient.class);
        }
    }

    /**
     * Sets the callback for binding the service to the current activity.
     * @param c
     */
    public void setCallback(GitHubCallback c) {
        mCallback = c;
    }

    /**
     * Unsets the callback when an activity goes away to allow garbage collection and prevent callbacks from firing.
     */
    public void unsetCallback() {
        mCallback = null;
    }

    /**
     * Calls the GitHub REST API to access the list of gists on your account and calls the callback method to display them
     */
    public void getGists() {
        mGitHubClient.gists().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Gist>>() {
            @Override
            public void call(List<Gist> gists) {
                mCallback.displayGistList(gists);
            }
        },new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(getClass().getName(),"ERROR: " + throwable.getMessage());
            }
        });
    }

    /**
     * Calls the GitHub REST API to access the contents of one gist and calls the callback method to display the list of files
     * @param id
     */
    public void getGist(String id) {
        mGitHubClient.gist(id).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GistDetail>() {
            @Override
            public void call(GistDetail gistDetail) {
                mCallback.displayFileList(gistDetail);
            }
        },new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(getClass().getName(),"ERROR: " + throwable.getMessage());
            }
        });
    }

}
