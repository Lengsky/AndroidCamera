package com.sgb.homepage;

import com.sgb.meitucamera.CameraPreview;
import com.sgb.homepage.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class HomePage extends Activity {
	ImageView camera,photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.homepage);
		camera =(ImageView)findViewById(R.id.camera);
		photo =(ImageView)findViewById(R.id.photo);
		camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(HomePage.this,CameraPreview.class);
				startActivity(intent);
			}
		});
		photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK, 
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//µ÷ÓÃandroidµÄÍ¼¿â 
						startActivityForResult(i, 2); 
			}
		});
	}

	

}
