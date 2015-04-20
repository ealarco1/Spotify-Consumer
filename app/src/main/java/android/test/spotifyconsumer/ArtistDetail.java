package android.test.spotifyconsumer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.test.spotifyconsumer.util.DownloadImageTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ArtistDetail  extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail);
        Intent intent = getIntent();

        final int pos = intent.getIntExtra(MainActivity.POS,0);

        new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                .execute(MainActivity.context.getImageList().get(pos));
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MainActivity.context.getUrlList().get(pos)));
                startActivity(intent);

            }

        });
    }
}
