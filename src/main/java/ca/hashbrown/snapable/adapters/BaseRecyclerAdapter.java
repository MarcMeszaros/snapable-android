package ca.hashbrown.snapable.adapters;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


abstract class BaseRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>  {

    // https://gist.github.com/ssinss/e06f12ef66c51252563e
    public static abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

        private int currentPage = 1;
        private LinearLayoutManager mLayoutManager;

        public EndlessRecyclerOnScrollListener(LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            // turn off the loading flag
            if (loading && totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }

            // End has been reached
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                currentPage++;
                onLoadMore(currentPage); // Do something
                loading = true;
            }
        }

        // loading logic goes here
        public abstract void onLoadMore(int currentPage);
    }
}
