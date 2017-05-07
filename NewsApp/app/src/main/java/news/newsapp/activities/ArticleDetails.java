package news.newsapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import news.newsapp.R;
import news.newsapp.helpers.ImageUtil;

public class ArticleDetails extends AppCompatActivity {
    public static String author,title, description, url, urlToImage,publishedAt;
    TextView Author, Title, Description, PublishedOn, ReadMore;
    ImageView ArticleImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Article Details");
        author = getIntent().getExtras().getString("author");
        title = getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("description");
        url = getIntent().getExtras().getString("url");
        urlToImage = getIntent().getExtras().getString("imageurl");
        publishedAt = getIntent().getExtras().getString("publishedon");


        Author = (TextView) findViewById(R.id.txt_articleauthor);
        Description = (TextView) findViewById(R.id.txt_articledesc);
        Title = (TextView) findViewById(R.id.txt_articletitle);
        ArticleImage = (ImageView) findViewById(R.id.item_image);
        PublishedOn = (TextView) findViewById(R.id.txt_publishedon);
        ReadMore = (TextView) findViewById(R.id.txt_readmore);


        Author.setText("Written by "+author);
        Description.setText(description);
        Title.setText(title);
        PublishedOn.setText("On "+publishedAt);
        ReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        //loading image to image view from url
        ImageLoader loader = ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(ArticleDetails.this));
        ImageUtil.displayImage(ArticleImage, urlToImage, null);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
