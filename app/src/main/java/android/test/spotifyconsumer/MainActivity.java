package android.test.spotifyconsumer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.test.spotifyconsumer.adapters.CustomListViewAdapter;
import android.test.spotifyconsumer.beans.RowItem;
import android.test.spotifyconsumer.util.DownloadImageTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    public static MainActivity context;
    private SpotifyService spotify;
    public ArrayList imageList;
    public ArrayList urlList;

    final static String POS = "Position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        imageList = new ArrayList<String>();
        urlList = new ArrayList<String>();

        SpotifyApi api = new SpotifyApi();

        spotify = api.getService();

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageList.clear();
                urlList.clear();

                EditText input = (EditText) findViewById(R.id.input);

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("Input method hide error", e.getMessage());
                }

                spotify.searchArtists(input.getText().toString(), new Callback<ArtistsPager>() {
                    @Override
                    public void success(final ArtistsPager result, Response response) {
                        if (!result.artists.items.isEmpty()) {
                            final Artist artist = result.artists.items.get(0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                                        .execute(artist.images.get(0).url);

                                        TextView text = (TextView) findViewById(R.id.textView2);

                                        text.setText("Name: " + artist.name
                                                + "\nFollowers: " + artist.followers.total
                                                + "\nPopularity: " + artist.popularity);

                                        fillAlbums(artist.name);

                                    } catch (Exception e) {
                                        Log.d("Error", e.getLocalizedMessage());
                                    }

                                }

                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView text = (TextView) findViewById(R.id.textView2);
                                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                    imageView.setImageResource(android.R.color.transparent);
                                    text.setText("No results");
                                }
                            });
                        }

                        Log.d("Search succesful", result.toString());
                    }


                    @Override
                    public void failure(RetrofitError error) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView text = (TextView) findViewById(R.id.textView2);

                                text.setText("Search failed");
                            }
                        });

                        Log.d("Search failed", error.getLocalizedMessage());
                    }
                });
            }
        });
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(context,ArtistDetail.class);
                intent.putExtra(POS, position);
                startActivity(intent);
            }
        });
    }


    public final void fillAlbums(String artist) {

        spotify.searchAlbums(artist, new Callback<AlbumsPager>() {
            @Override
            public void success(final AlbumsPager albumsPager, Response response) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ListView list = (ListView) findViewById(R.id.listView);

                        List<AlbumSimple> albums = albumsPager.albums.items;
                        List<RowItem> albumList = new ArrayList<RowItem>();

                        try {
                            for (AlbumSimple album : albums) {
                                String albumInfo = album.name;
                                if (album.available_markets.size() < 5) {
                                    albumInfo += "\nAvailable in:";
                                    for (String market : album.available_markets) {
                                        albumInfo += "\n" + market;
                                    }
                                }

                                RowItem item = new RowItem(album.images.get(0).url, albumInfo);
                                albumList.add(item);
                                imageList.add(album.images.get(0).url);
                                urlList.add(album.external_urls.get("spotify"));
                            }

                            CustomListViewAdapter adapter = new CustomListViewAdapter(context,
                                    R.layout.list_item, albumList);
                            list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Log.d("Error", e.getLocalizedMessage());
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Search failed", error.getLocalizedMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final ArrayList<String> getImageList() {
        return imageList;
    }

    public final ArrayList<String> getUrlList() {
        return urlList;
    }

}

