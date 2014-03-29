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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MotionEvent;

public class ImageViewerActivity extends Activity {
	static int currentPosition;
	int fullSizeArrayID;
	String [] fullPhotos;
	ImageView displayedImage;
	Button nextButton;
	Button previousButton;
	Button backButton;
	static ProgressDialog imageDialog;
	AlertDialog simpleAlert;
	FrameLayout frameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);

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
        
        //Obtaining the incoming intent from the gallery activity and creating
        //the appropriate full size photo array of urls. Also gets the current
        //index position of the selected thumbnail from the gallery
		if(getIntent().getExtras() != null) {
			currentPosition = getIntent().getExtras().getInt(GalleryActivity.CURRENT_POSITION);
			fullSizeArrayID = getIntent().getExtras().getInt(GalleryActivity.PHOTO_ID_KEY);
		}
		//Binding XML objects
		displayedImage = (ImageView) findViewById(R.id.imageView1);
		fullPhotos = getResources().getStringArray(fullSizeArrayID);
		nextButton = (Button) findViewById(R.id.button3);
		backButton = (Button) findViewById(R.id.button2);
		previousButton = (Button) findViewById(R.id.button1);
		frameLayout = (FrameLayout) findViewById(R.id.FrameLayout1);
		//Instantiating the progress dialog
        imageDialog = new ProgressDialog(this);
        imageDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        imageDialog.setCancelable(false);
        imageDialog.setMessage("Loading Photo...");
		//Gets the current image based on the position of the thumbnail selected
        //in the gallery along with a toast confirming position
		new GetLargeImageAsyncTask().execute(fullPhotos[currentPosition]);
		Toast.makeText(ImageViewerActivity.this, currentPosition + 1 + "/" + fullPhotos.length, Toast.LENGTH_SHORT).show();
		
		displayedImage.setOnTouchListener(new OnSwipeTouchListener(this) {

		    public void onSwipeRight() {
		    	currentPosition--;
				if(currentPosition < 0) {
					currentPosition = fullPhotos.length - 1;
				}
				new GetLargeImageAsyncTask().execute(fullPhotos[currentPosition]);
				Toast.makeText(ImageViewerActivity.this, currentPosition + 1 + "/" + fullPhotos.length, Toast.LENGTH_SHORT).show();
		        //Toast.makeText(ImageViewerActivity.this, "right", Toast.LENGTH_SHORT).show();
		        //Log.d("demo", "right");
		    }
		    public void onSwipeLeft() {
		    	currentPosition++;
				if(currentPosition > fullPhotos.length - 1) {
					currentPosition = 0;
				}
				new GetLargeImageAsyncTask().execute(fullPhotos[currentPosition]);
				Toast.makeText(ImageViewerActivity.this, currentPosition + 1 + "/" + fullPhotos.length, Toast.LENGTH_SHORT).show();
		        //Toast.makeText(ImageViewerActivity.this, "left", Toast.LENGTH_SHORT).show();
		        //Log.d("demo", "left");
		    }

			public boolean onTouch(View v, MotionEvent event) {
			    return gestureDetector.onTouchEvent(event);
			}
		});
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Increments position on click and determines if the current index
				//is larger than the array. Resets to 0 if it is.
				currentPosition++;
				if(currentPosition > fullPhotos.length - 1) {
					currentPosition = 0;
				}
				new GetLargeImageAsyncTask().execute(fullPhotos[currentPosition]);
				Toast.makeText(ImageViewerActivity.this, currentPosition + 1 + "/" + fullPhotos.length, Toast.LENGTH_SHORT).show();
			}
		});
		
		previousButton.setOnClickListener(new View.OnClickListener() {
			//Decrements the current position. If that position is at the 
			//beginning of the array, it resets the current position to the end of 
			//the array
			@Override
			public void onClick(View v) {
				currentPosition--;
				if(currentPosition < 0) {
					currentPosition = fullPhotos.length - 1;
				}
				new GetLargeImageAsyncTask().execute(fullPhotos[currentPosition]);
				Toast.makeText(ImageViewerActivity.this, currentPosition + 1 + "/" + fullPhotos.length, Toast.LENGTH_SHORT).show();
			}
		});
		
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_viewer, menu);
		return true;
	}
	//Async task for fetching the large photos for the activity. Begins and ends the 
	//progress dialog when the image has finished loading
	public class GetLargeImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
	        imageDialog.show();
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
			imageDialog.dismiss();
			displayedImage.setImageBitmap(result);
		}		
	}
}
