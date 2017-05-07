package news.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import news.newsapp.R;
import news.newsapp.activities.ArticleDetails;
import news.newsapp.helpers.ImageUtil;
import news.newsapp.helpers.ItemClickListener;
import news.newsapp.models.ArticleItem;

/**
 * Created by chebet on 5/7/2017.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    ArticleAdapter.ViewHolder viewHolder;
    private Context mContext;
    private ArrayList<ArticleItem> aItems = new ArrayList<ArticleItem>();
    ArrayList<ArticleItem> arraylist;

    public ArticleAdapter(Context context, ArrayList<ArticleItem> articleread) {
        aItems = articleread;
        mContext = context;
        arraylist = new ArrayList<ArticleItem>();
        arraylist.addAll(aItems);

    }
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.article_item, null);

        // create ViewHolder

        viewHolder = new ArticleAdapter.ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleAdapter.ViewHolder holder, final int position) {
        final int pos = position;
        viewHolder = holder;

        viewHolder.author = aItems.get(position).getAuthor();
        viewHolder.title = aItems.get(position).getTitle();
        viewHolder.description = aItems.get(position).getDescription();
        viewHolder.url = aItems.get(position).getUrl();
        viewHolder.urlToImage = aItems.get(position).getUrlToImage();
        viewHolder.publishedAt = aItems.get(position).getPublishedAt();

        viewHolder.Author.setText("By "+viewHolder.author);
        viewHolder.ArticleTitle.setText(viewHolder.title);
        viewHolder.DatePublished.setText(viewHolder.publishedAt);
        //loading image to image view from url
        ImageLoader loader = ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(mContext));
        ImageUtil.displayRoundImage(viewHolder.ArticleIcon, viewHolder.urlToImage, null);

        viewHolder.VisitSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(aItems.get(position).getUrl()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent articledetails = new Intent(mContext, ArticleDetails.class);
                articledetails.putExtra("title",aItems.get(position).getTitle());
                articledetails.putExtra("author",aItems.get(position).getAuthor());
                articledetails.putExtra("description",aItems.get(position).getDescription());
                articledetails.putExtra("url", aItems.get(position).getUrl());
                articledetails.putExtra("imageurl",aItems.get(position).getUrlToImage());
                articledetails.putExtra("publishedon",aItems.get(position).getPublishedAt());
                mContext.startActivity(articledetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return aItems.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView ArticleTitle, Author, DatePublished;
        public ArticleItem singlearticle;
        public ImageView ArticleIcon;
        public Button VisitSite;
        private ItemClickListener clickListener;
        public String author,title, description, url, urlToImage,publishedAt;
        private static String TAG = "Article Items";
        public int position;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);


            ArticleTitle = (TextView) itemLayoutView.findViewById(R.id.txt_articletitle);
            ArticleIcon = (ImageView) itemLayoutView.findViewById(R.id.img_articleicon);
            Author = (TextView) itemLayoutView.findViewById(R.id.txt_author);
            DatePublished = (TextView) itemLayoutView.findViewById(R.id.txt_publishedon);
            VisitSite = (Button) itemLayoutView.findViewById(R.id.btn_visitsite);


            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);

        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}
