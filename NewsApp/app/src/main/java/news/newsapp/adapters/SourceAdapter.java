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
import news.newsapp.helpers.ImageUtil;
import news.newsapp.helpers.ItemClickListener;
import news.newsapp.models.ArticleItem;
import news.newsapp.models.SourceItem;

/**
 * Created by chebet on 5/7/2017.
 */

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
    SourceAdapter.ViewHolder viewHolder;
    private Context mContext;
    private ArrayList<SourceItem> sItems = new ArrayList<SourceItem>();
    ArrayList<SourceItem> arraylist;

    public SourceAdapter(Context context, ArrayList<SourceItem> sourcevisit) {
        sItems = sourcevisit;
        mContext = context;
        arraylist = new ArrayList<SourceItem>();
        arraylist.addAll(sItems);

    }

    @Override
    public SourceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.source_item, null);

        // create ViewHolder

        viewHolder = new SourceAdapter.ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SourceAdapter.ViewHolder holder, final int position) {
        final int pos = position;
        viewHolder = holder;

        viewHolder.name = sItems.get(position).getName();
        viewHolder.description = sItems.get(position).getDescription();
        viewHolder.url = sItems.get(position).getUrl();
        viewHolder.category = sItems.get(position).getCategory();
        viewHolder.language = sItems.get(position).getLanguage();
        viewHolder.country = sItems.get(position).getCountry();

        viewHolder.SourceName.setText(viewHolder.name);
        viewHolder.Category.setText(viewHolder.category);
        viewHolder.Language.setText("Language: "+viewHolder.language);
        viewHolder.Country.setText("Country: "+viewHolder.country);


        viewHolder.VisitSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(sItems.get(position).getUrl()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sItems.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView SourceName, Category, Language, Country;
        public ArticleItem singlesource;
        public ImageView SourceIcon;
        public Button VisitSite;
        private ItemClickListener clickListener;
        public String id,name, description, url, category,language,country;
        private static String TAG = "Article Items";
        public int position;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);


            SourceName = (TextView) itemLayoutView.findViewById(R.id.txt_name);
            SourceIcon = (ImageView) itemLayoutView.findViewById(R.id.img_sourceicon);
            Category = (TextView) itemLayoutView.findViewById(R.id.txt_category);
            Language = (TextView) itemLayoutView.findViewById(R.id.txt_language);
            Country = (TextView) itemLayoutView.findViewById(R.id.txt_country);
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
