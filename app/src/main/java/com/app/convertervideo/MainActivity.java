package com.app.convertervideo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;
import com.learnncode.mediachooser.activity.HomeFragmentActivity;


import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	InterstitialAd interstitial;
	boolean isAdLod = false;
	AdRequest adRequest;
	MediaGridViewAdapter adapter;
	GridView gridView;
	int PICKFILE_RESULT_CODE = 101;
	ProgressDialog dialog ;
	TextView txtBox;
	Button btnSelect,btnExtension,btnConvert,btnMoreApps,btnRate;
	CharSequence[] extension = new CharSequence[9];
	String ORG_PATH,EXTENSION;
	boolean isExtensionSelected=false;
	Timer timer ;
	Spinner spOptions;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);




		interstitial = new InterstitialAd(MainActivity.this);
		interstitial.setAdUnitId("ca-app-pub-1700338446399525/9379726899");


		AdView adView = (AdView) this.findViewById(R.id.adView);
		// Request for Ads
		adRequest = new AdRequest.Builder()
				.build();

		// Load ads into Banner Ads
		adView.loadAd(adRequest);



		 txtBox = (TextView)findViewById(R.id.txtBox);
		 spOptions = (Spinner)findViewById(R.id.spOptions);
		 btnSelect = (Button)findViewById(R.id.btnSelect);
		 btnExtension = (Button)findViewById(R.id.btnExtension);
		 btnConvert = (Button)findViewById(R.id.btnConvert);
		 btnMoreApps = (Button)findViewById(R.id.btnMoreApps);
		 btnRate = (Button)findViewById(R.id.btnRate);

		ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.spinneritem,R.id.nameTextView,getResources().getStringArray(R.array.options));
		spOptions.setAdapter(arrayAdapter);



		 IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
			registerReceiver(videoBroadcastReceiver, videoIntentFilter);

	
		 extension[0]=".mp3";
		 extension[1]=".wav";
		 extension[2]=".m4a";
		 
		 extension[3]=".aac";
		 extension[4]=".3gp";
		 extension[5]=".avi";
		 
		 extension[6]=".mkv";
		 extension[7]=".flv";
		 extension[8]=".mov";
		 extension[8]=".flac";
		 
		 
		 btnExtension.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  showExtensions(); 
			}
		});
		
		 btnSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			/*	//MediaChooser.setSelectionLimit(1);
				//MediaChooser.showOnlyVideoTab();

			//	Intent intent = new Intent(MainActivity.this, BucketHomeFragmentActivity.class);
				Intent intent = new Intent(MainActivity.this, HomeFragmentActivity.class);
				startActivity(intent);*/

				
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("file/mp4");
				startActivityForResult(intent, PICKFILE_RESULT_CODE);
			}
		});
		 
		 
		 
		 btnConvert.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(txtBox.getText().toString().length()==0){
					Toast.makeText(MainActivity.this, "Please select source video file to convert", Toast.LENGTH_SHORT).show();
				}else if(!isExtensionSelected){
					Toast.makeText(MainActivity.this, "Please select video convert type", Toast.LENGTH_SHORT).show();
				}else{
					processConvert();
				}
			}
		});

		int count = 100; //Declare as inatance variable
/*
		 timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {


						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {

									interstitial.loadAd(adRequest);
								displayInterstitial();

							}
						}, 50000);

					}
				});
			}
		}, 0, 1000);
		*/




	}
	public void stoptimertask(View v) {
		//stop the timer, if it's not already null
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
			isAdLod=true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onBackPressed() {

		interstitial.loadAd(adRequest);
		// Prepare an Interstitial Ad Listener
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				// Call displayInterstitial() function
				displayInterstitial();

			}

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				timer.cancel();
				finish();
				//super.onBackPressed();
			}
		});



	}

	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			//Toast.makeText(MainActivity.this, "yippiee Video ", Toast.LENGTH_SHORT).show();
			//Toast.makeText(MainActivity.this, "Video SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			setAdapter(intent.getStringArrayListExtra("list"));
		}
	};


	private void setAdapter( List<String> filePathList) {

		Log.e("Video Path",filePathList.get(0).toString());

			String Fpath = filePathList.get(0).toString();
			ORG_PATH = Fpath;
			String temp = Fpath;
			String temp2 = Fpath;
			String Extension = temp.substring(Fpath.lastIndexOf("."),temp.length());


			if(Extension.equalsIgnoreCase(".mp4") || Extension.equalsIgnoreCase(".mkv")
					|| Extension.equalsIgnoreCase(".flv")
					|| Extension.equalsIgnoreCase(".vob")
					|| Extension.equalsIgnoreCase(".avi")
					|| Extension.equalsIgnoreCase(".wmv")
					|| Extension.equalsIgnoreCase(".mng")
					|| Extension.equalsIgnoreCase(".mpg")
					|| Extension.equalsIgnoreCase(".mpeg")
					|| Extension.equalsIgnoreCase(".svi")
					|| Extension.equalsIgnoreCase(".mp3")
					|| Extension.equalsIgnoreCase(".3gp")){

				String Filename = temp2.substring(Fpath.lastIndexOf("/")+1,temp2.length());

				txtBox.setText(Filename);

			}else{
				txtBox.setText("");
				Toast.makeText(MainActivity.this, "Please select valid Video file", Toast.LENGTH_LONG).show();

			}


	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		  // Get the Uri of the selected file  
       // Uri uri = data.getData();
		
		if(RESULT_OK==resultCode){
			String Fpath = data.getData().getPath();
			ORG_PATH = Fpath;
			String temp = Fpath;
			String temp2 = Fpath;
			String Extension = temp.substring(Fpath.lastIndexOf("."),temp.length());
			
				
			if(Extension.equalsIgnoreCase(".mp4") || Extension.equalsIgnoreCase(".mkv")
					 || Extension.equalsIgnoreCase(".flv")
					 || Extension.equalsIgnoreCase(".vob")
					 || Extension.equalsIgnoreCase(".avi")
					 || Extension.equalsIgnoreCase(".wmv")
					 || Extension.equalsIgnoreCase(".mng")
					 || Extension.equalsIgnoreCase(".mpg")
					 || Extension.equalsIgnoreCase(".mpeg")
					 || Extension.equalsIgnoreCase(".svi")
					 || Extension.equalsIgnoreCase(".3gp")){
							
				String Filename = temp2.substring(Fpath.lastIndexOf("/")+1,temp2.length());
				
				txtBox.setText(Filename);
				
					}else{
						txtBox.setText("");
						Toast.makeText(MainActivity.this, "Please select valid Video file", Toast.LENGTH_LONG).show();
			
			}
			
		}
			
		
		
		
	}
	
	private void processConvert(){
		startConversion();
		
	    dialog = new ProgressDialog(MainActivity.this);
		dialog.setMessage("Converting");
		dialog.setCancelable(false);
		dialog.show();
		
		new CountDownTimer(3000,1000) {		
			
			
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinish() {
				dialog.dismiss();				
				// TODO Auto-generated method stub
				
				Toast.makeText(MainActivity.this, "Video converted...", Toast.LENGTH_LONG).show();
			}
		}.start();
		
		
		
	}
	
	private void startConversion(){
		
		String filepath = ORG_PATH;
		File newFile = new File(filepath);
		
		String tempName = newFile.getName().toString();
		String tempSubname = tempName.substring(0, tempName.lastIndexOf("."));
		tempSubname += EXTENSION;
		
		
		String orginalpath = filepath.substring(0,  filepath.lastIndexOf("/"));
		
		
		
		/*File newF =  new File(orginalpath,tempSubname);
		newFile.renameTo(newF);*/
		
		
		// creating new path on sd card
        ContextWrapper cw = new ContextWrapper(getApplicationContext());        
        String root = Environment.getExternalStorageDirectory().toString();
        String newfilepath = root+"/Video Converter/";
        File directory = new File(newfilepath);
        if (!directory.exists())
        { 
        	directory.mkdirs();
        } 
        
        try{
        	InputStream in = new FileInputStream(filepath);
        	
        	String sdCard = Environment.getExternalStorageDirectory().toString();
             
        	File targetLocation = new File (sdCard + "/Video Converter/"+tempSubname);
        	
        	OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
             
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
             
            in.close();
            out.close();
            out.flush();
            
             
            Log.e("copy", "Copy file successful.");
 
        }catch(Exception e){
        	Log.e("exception in copy",e.toString());
        }
 
        // end
        
        
		
		Log.e("in filepath",filepath);
		Log.e("in orginalpath",orginalpath);
		Log.e("in tempSubname",tempSubname);
	}
	
	private void showExtensions() { 
		 
		 
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Extension");
        builder.setItems(extension, new DialogInterface.OnClickListener() {
            @Override 
            public void onClick(DialogInterface dialog, int item) {
            	isExtensionSelected = true;
            	EXTENSION = String.valueOf(extension[item]);
            	btnExtension.setText("Convert to "+extension[item]);
 
              
            } 
        }); 
        builder.show();
    } 

	
}
