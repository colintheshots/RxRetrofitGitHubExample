package com.colintheshots.rxretrofitexample1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.colintheshots.rxretrofitexample1.R;
import com.colintheshots.rxretrofitexample1.models.Gist;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by colin.lee on 10/11/14.
 */
public class GistAdapter extends BaseAdapter {

    private Context mContext;
    private List<Gist> mGists;

    public GistAdapter(Context mContext, List<Gist> mGists) {
        this.mContext = mContext;
        this.mGists = mGists;
    }

    @Override
    public int getCount() {
        return mGists.size();
    }

    @Override
    public Object getItem(int i) {
        return mGists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        GistHolder holder;
        if (view != null) {
            holder = (GistHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.row_gist, viewGroup, false);
            holder = new GistHolder(view);
            view.setTag(holder);
        }

        Gist g = mGists.get(i);

        holder.title.setText(g.getHtml_url());
        holder.id.setText(g.getId());

        return view;
    }

    static class GistHolder {
        @InjectView(R.id.gistTextView) TextView title;
        @InjectView(R.id.hiddenIdTextView) TextView id;

        public GistHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
