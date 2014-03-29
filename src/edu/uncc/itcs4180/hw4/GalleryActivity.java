/*
 * James Keller
 * ITCS 4180 - 091
 * HW4
 * 3/25/14
 * 
 */

package edu.uncc.itcs4180.hw4;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.ImageView;

public class GalleryActivity extends Activity {
	int indexCounter;
	String [] thumbs;
	String [] photos;
	int thumbsArrayID;
	int photosArrayID;
	GridLayout myGridLayout;
	AlertDialog simpleAlert;
	ProgressDialog galleryProgress;
	final static String CURRENT_POSITION = "Current Position";
	final static String PHOTO_ID_KEY = "Large Photo Array";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
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
        
        //Counter to keep track of the correct position in the thumbnail array
		indexCounter = 0;
		
		//Retrieving the extras from the intent passed from the main activity. The array ID's
		//correspond to the correct thumbnail and full photo arrays based on the users previous
		//choice
		if(getIntent().getExtras() != null) {
			thumbsArrayID = getIntent().getExtras().getInt(MainActivity.THUMBNAIL_KEY);
			photosArrayID = getIntent().getExtras().getInt(MainActivity.PHOTO_KEY);
		}
		//Fetch the correct array based on the ID passed with the intent
		thumbs = getResources().getStringArray(thumbsArrayID);
		//Creates a grid container and sets a column size of 4 for the thumbnails
		//to be loaded
		myGridLayout = (GridLayout)findViewById(R.id.GridLayout1);
		myGridLayout.setColumnCount(4);
		//Creates a progress dialog while the thumbnails are being loaded
        galleryProgress = new ProgressDialog(this);
        galleryProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        galleryProgress.setCancelable(false);
        galleryProgress.setMessage("Loading Thumbnails...");
        galleryProgress.show();
        //For each loop that fetches all the thumbnails by passing the 
        //appropriate url
		for(String url : thumbs) {
			new GetThumbsAsyncTask().execute(url);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
		return true;
	}
	//Async task used to fetch the thumbnails in a child thread
	public class GetThumbsAsyncTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String imgUrl = params[0];
			Bitmap image = null;
			URL url;
			try {
				url = new URL(imgUrl);
				image = BitmapFactory.decodeStream(url.openStream());
			} catch (MalformedURLException e) {
				simpleAlert.show();
				e.printStackTrace();
			} catch (IOException e) {
				simpleAlert.show();
				e.printStackTrace();
			}
			return image;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(result != null) {
				//Creating and formatting the image being passed to the grid layout
				ImageView imageViewToBeAdded = new ImageView(getBaseContext());
				imageViewToBeAdded.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				imageViewToBeAdded.setPadding(1, 1, 1, 1);
				imageViewToBeAdded.setImageBitmap(result);
				//Used for testing...
				imageViewToBeAdded.setId(indexCounter);
				
				//Intent is created based on the current position of the index and passed to the image viewer activity
				//on click
				final Intent imageViewerIntent = new Intent(GalleryActivity.this, ImageViewerActivity.class);
				imageViewerIntent.putExtra(CURRENT_POSITION, indexCounter);
				imageViewerIntent.putExtra(PHOTO_ID_KEY, photosArrayID);
				imageViewToBeAdded.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						startActivity(imageViewerIntent);
					}
				});
				
				myGridLayout.addView(imageViewToBeAdded);
			}
			//Ends the progress dialog once all of the urls in the array have
			//been fetched
			if(indexCounter >= thumbs.length - 1) {
				galleryProgress.dismiss();
			}
			indexCounter++;
		}
		
	}

}
