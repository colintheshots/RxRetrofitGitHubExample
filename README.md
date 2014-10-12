# RxRetrofitExample #1

This GitHub project is an example of RxJava combined with Retrofit for my AnDevCon SF 2014 session, Be Reactive with Retrofit.

This example uses the GitHub API to read the current user's list of Gists, show them as a ListView. When an Gist URL is clicked in the ListView, a ListView is populated with the contents of the files in the Gist.

# Topics to Learn

- Returning Observables from Retrofit
- Using Retrofit as a Service

## Returning Observables from Retrofit

Since January 2014, Retrofit has allowed developers to return an RxJava Observable instead of accepting a callback for asynchronous operation or returning a Plain Old Java Object for synchronous operation.

For example, this code includes the following interface to download the list of Gists from GitHub's REST server:

    public interface GitHubClient {
        @GET("/gists")
        Observable<List<Gist>> gists();
    }

## Using Retrofit as a Service

Using a service offers the possibility of loose coupling between activities and the network requests they initiate. This means one may initiate a network request in one activity, cause an orientation change, and then retrieve the result in the newly-created activity. For a working example, check out [Stephane Nicolas' Robospice](https://github.com/stephanenicolas/robospice).