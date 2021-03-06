package net.idik.lib.slimadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.idik.lib.slimadapter.diff.SlimDiffUtil;
import net.idik.lib.slimadapter.ex.loadmore.SlimMoreLoader;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linshuaibin on 16/05/2017.
 */
public class SlimAdapterEx extends SlimAdapter {

    private final static int TYPE_EX = -100;
    private final static int TYPE_EMPTY = -90;

    private List<View> headerViews;
    private List<View> footerViews;

    private SlimMoreLoader moreLoader;

    private View emptyView;


    protected SlimAdapterEx() {
        super();
        headerViews = new ArrayList<>();
        footerViews = new ArrayList<>();
    }

    public SlimAdapterEx addHeaderView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        headerViews.add(view);
        notifyDataSetChanged();
        return this;
    }

    public SlimAdapterEx addFooterView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        footerViews.add(view);
        notifyDataSetChanged();
        return this;
    }

    public SlimAdapterEx addHeaderView(Context context, int layoutRes) {
        return addHeaderView(LayoutInflater.from(context).inflate(layoutRes, null));
    }

    public SlimAdapterEx addFooterView(Context context, int layoutRes) {
        return addFooterView(LayoutInflater.from(context).inflate(layoutRes, null));
    }

    public SlimAdapterEx enableEmptyView(Context context, int layoutRes) {
        return enableEmptyView(LayoutInflater.from(context).inflate(layoutRes, null));
    }

    public SlimAdapterEx enableEmptyView(View emptyView) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        emptyView.setLayoutParams(params);
        this.emptyView = emptyView;
        return this;
    }

    public SlimAdapterEx enableLoadMore(SlimMoreLoader slimMoreLoader) {
        this.moreLoader = slimMoreLoader;
        slimMoreLoader.setSlimAdapterEx(this);
        notifyDataSetChanged();
        return this;
    }

    private Object getExItem(int position) {
        if (position < headerViews.size()) {
            return headerViews.get(position);
        } else {
            position -= headerViews.size();
            if (position < footerViews.size()) {
                return footerViews.get(position);
            } else {
                return moreLoader;
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (moreLoader != null) {
            recyclerView.addOnScrollListener(moreLoader);
        }
    }

    @Override
    public SlimAdapterEx updateData(List<?> data) {
        if (moreLoader != null) {
            moreLoader.reset();
        }
        super.updateData(data);
        return this;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (moreLoader != null) {
            recyclerView.removeOnScrollListener(moreLoader);
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemViewType(int position) {
        if (super.getItemCount() == 0 && emptyView != null) {
            return TYPE_EMPTY;
        }
        if (position < headerViews.size()) {
            return TYPE_EX - position;
        } else {
            position -= headerViews.size();
            if (position < super.getItemCount()) {
                return super.getItemViewType(position);
            } else {
                position -= super.getItemCount();
                if (position < footerViews.size()) {
                    return TYPE_EX - position - headerViews.size();
                } else {
                    return TYPE_EX - headerViews.size() - footerViews.size();
                }
            }
        }
    }

    @Override
    public SlimViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SlimViewHolder viewHolder;
        if (viewType == TYPE_EMPTY) {
            viewHolder = new SlimExViewHolder(emptyView);
        } else if (viewType <= TYPE_EX) {
            Object item = getExItem(TYPE_EX - viewType);
            if (item instanceof SlimMoreLoader) {
                viewHolder = new SlimExLoadMoreViewHolder(((SlimMoreLoader) item).getLoadMoreView());
            } else {
                viewHolder = new SlimExViewHolder((View) item);
            }
        } else {
            viewHolder = super.onCreateViewHolder(parent, viewType);
        }
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if (super.getItemCount() == 0 && emptyView != null) {
            return 1;
        }
        return footerViews.size() + headerViews.size() + super.getItemCount() + (moreLoader == null ? 0 : 1);
    }

    @Override
    public Object getItem(int position) {
        if (super.getItemCount() == 0 && this.emptyView != null && position == 0) {
            return emptyView;
        }
        if (position < headerViews.size()) {
            return headerViews.get(position);
        } else {
            position -= headerViews.size();
            if (position < super.getItemCount()) {
                return super.getItem(position);
            } else {
                position -= super.getItemCount();
                if (position < footerViews.size()) {
                    return footerViews.get(position);
                } else {
                    return moreLoader;
                }
            }
        }
    }

    @Override
    public SlimAdapterEx enableDiff() {
        return (SlimAdapterEx) super.enableDiff();
    }

    @Override
    public SlimAdapterEx enableDiff(SlimDiffUtil.Callback diffCallback) {
        return (SlimAdapterEx) super.enableDiff(diffCallback);
    }

    @Override
    public SlimAdapterEx registerDefault(int layoutRes, SlimInjector slimInjector) {
        return (SlimAdapterEx) super.registerDefault(layoutRes, slimInjector);
    }

    @Override
    public <T> SlimAdapterEx register(int layoutRes, SlimInjector<T> slimInjector) {
        return (SlimAdapterEx) super.register(layoutRes, slimInjector);
    }

    @Override
    public SlimAdapterEx attachTo(RecyclerView... recyclerViews) {
        return (SlimAdapterEx) super.attachTo(recyclerViews);
    }

    private class SlimExViewHolder extends SlimViewHolder<Object> {


        public SlimExViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Object data, IViewInjector injector) {

        }
    }

    private class SlimExLoadMoreViewHolder extends SlimViewHolder<SlimMoreLoader> {

        public SlimExLoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(SlimMoreLoader data, IViewInjector injector) {
        }
    }
}
