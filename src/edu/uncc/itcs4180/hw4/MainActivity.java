/*
 * James Keller
 * Kenneth Holsey
 * ITCS 4180 - 091
 * HW4
 * 3/25/14
 * 
 */

package edu.uncc.itcs4180.hw4;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	//Variable declarations
	ProgressDialog dialog;
	int progressCounter;
	AlertDialog simpleAlert;
	int numImages;
	//ArrayLists for ImageViews and TextViews that will be set using the existing XML template in
	//the inner async class
	ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
	ArrayList<TextView> textViewList = new ArrayList<TextView>();
	//Fetching and storing the ids for the text labels provided in the strings.xml
	final int [] textLabels = {R.string.label_uncc, R.string.label_sports, R.string.label_ifest, R.string.label_commencement};
	final int [] stringArrayThumbs = {R.array.uncc_thumbs, R.array.football_thumbs, R.array.ifest_thumbs, R.array.commencement_thumbs};
	final int [] stringArrayPhotos = {R.array.uncc_photos, R.array.football_photos, R.array.ifest_photos, R.array.commencement_photos};
	final static String THUMBNAIL_KEY = "thumbnail key";
	final static String PHOTO_KEY = "photo_key";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Creation of an alert dialog to be used during exception handling
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("An Error Has Occurred!")
        .setMessage("The aplication will now close.")
        .setCancelable(false)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
        
        //Alert dialog instantiation
        simpleAlert = builder.create();
        
		//Fetching and storing the strings for the asynctask provided in the strings.xml
		String [] imageUrls = {getString(R.string.uncc_main_thumb), getString(R.string.football_main_thumb), getString(R.string.ifest_main_thumb),
				getString(R.string.commencement_main_thumb)};
		
		//Creating and loading the ArrayLists for the ImageViews and TextViews 
		imageViewList.add((ImageView) findViewById(R.id.imageView1));
		imageViewList.add((ImageView) findViewById(R.id.imageView2));
		imageViewList.add((ImageView) findViewById(R.id.imageView3));
		imageViewList.add((ImageView) findViewById(R.id.imageView4));
		textViewList.add((TextView) findViewById(R.id.textView1));
		textViewList.add((TextView) findViewById(R.id.textView2));
		textViewList.add((TextView) findViewById(R.id.textView3));
		textViewList.add((TextView) findViewById(R.id.textView4));
		
		//Counter initialization...used in the asynctask to iterate through the collections
		progressCounter = 0;
		numImages = imageUrls.length;
		
		//Progress dialog creation and setup...created here because the context reference passed through
		//the constructor when used in onPreExecute caused reference issues and hung the dialog even when 
		//dialog.dismiss was used. We were unable to find any solutions or documentation that provided a fix, 
		//besides creating the ProgressDialog here.
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");
        dialog.show();
        
		//For each loop that passes the urls to the asynctask
		for(String url : imageUrls) {
			new GetImagesAsyncTask().execute(url);
		}
		//Exit button creation along with Onclick
        Button exit = (Button) findViewById(R.id.button1);
        exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//Inner asynctask that handles downloading and setting the preconstructed UI with the 
	//proper elements
	public class GetImagesAsyncTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		//Fetches and downloads the appropriate images from the passed urls
		@Override
		protected Bitmap doInBackground(String... params) {
			String imgUrl = params[0];
			Bitmap image = null;
			try {
				URL url = new URL(imgUrl);
				image = BitmapFactory.decodeStream(url.openStream());
			} catch (MalformedURLException e) {
				simpleAlert.show();
			} catch (IOException e) {
				simpleAlert.show();
			}
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			//Sets the ImageViews and TextViews appropriately via the progressCounter, if an image cannot 
			//be found a placeholder image is used instead along with a not found label for the text
			if (result != null) {
				final Intent galleryIntent = new Intent(MainActivity.this, GalleryActivity.class);
				galleryIntent.putExtra(THUMBNAIL_KEY, stringArrayThumbs[progressCounter]);
				galleryIntent.putExtra(PHOTO_KEY, stringArrayPhotos[progressCounter]);
				imageViewList.get(progressCounter).setImageBitmap(result);
				imageViewList.get(progressCounter).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						startActivity(galleryIntent);
					}
				});
				textViewList.get(progressCounter).setText(textLabels[progressCounter]);
				//Log.d("demo", progressCounter + "");
			} else if(result == null) {
				result = ((BitmapDrawable) getResources().getDrawable(
						R.drawable.placeholder)).getBitmap();
				textViewList.get(progressCounter).setText(R.string.notfound_label);
			}
			//Once all of the urls and images have been handled the progress dialog is dismissed
			if(progressCounter >= 3) {
				dialog.dismiss();
			}
			progressCounter++;
		}
	}	
}

