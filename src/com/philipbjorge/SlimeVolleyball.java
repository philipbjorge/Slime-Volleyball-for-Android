package com.philipbjorge;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class SlimeVolleyball extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        View singleButton = findViewById(R.id.single_button);
        singleButton.setOnClickListener(this);
        View multiButton = findViewById(R.id.multi_button);
        multiButton.setOnClickListener(this);
        View settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// Auto-generated method stub
		switch(v.getId()) {
		case R.id.single_button:
			Intent i = new Intent(SlimeVolleyball.this, Game.class);
			i.putExtra("isSingle", true);
			startActivity(i);
			break;
		case R.id.multi_button:
			
			break;
		case R.id.settings_button:
			startActivity(new Intent(this, Settings.class));
			break;
		case R.id.exit_button:
			finish();
			break;
		}
	}
}