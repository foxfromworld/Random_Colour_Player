package com.example.randomcolourplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

import com.example.randomcolourplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'randomcolourplayer' library on application startup.
    static {
        System.loadLibrary("randomcolourplayer");
    }

    private ActivityMainBinding binding;
    int[] rgbAnimation;
    // An array to store all the appeared colours.
    // The length of the array is (number of colours) * 3 (RGB values) with the sequence R-G-B.
    int i = 0; // The pointer to get every set of colour.
    float x1, x2, y1, y2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button bt = binding.button; // Bind the Button with the activity
        bt.setOnClickListener(new View.OnClickListener() { // The click listener for the Button
            Timer timer; // The timer to schedule tasks (reply of the appeared colours)
            public void onClick(View v) {
                i = 0; // The pointer has to be reset or there will be Index Out Of Bounds Exception
                timer = new Timer(); // Create a new timer when the button is clicked
                rgbAnimation = replay(); // The native function to return the array
                System.out.println(rgbAnimation.length);
                // The debug message to check the length of the returned array
                Toast.makeText(getApplicationContext(), "Button pressed",
                        Toast.LENGTH_SHORT).show();
                // A notification to notify that the button is pressed

                MyTimerTask mt = new MyTimerTask(); // The task to be executed
                timer.schedule(mt, 1000, 1000);
            }
            class MyTimerTask extends TimerTask {
                public void run() {
                    View viewPanel = binding.view2; // Bind the View with the activity
                    runOnUiThread(()-> {
                        viewPanel.setBackgroundColor(Color.rgb(rgbAnimation[i], // R value
                                rgbAnimation[i+1], // G value
                                rgbAnimation[i+2])); // B value
                        // Change the background colour of the View
                        System.out.println("R:" + rgbAnimation[i]+ " " + i);
                        // The debug message to show the R value
                        System.out.println("G:" + rgbAnimation[i+1] + " " + (i+1));
                        // The debug message to show the G value
                        System.out.println("B:" + rgbAnimation[i+2] + " " + (i+2));
                        // The debug message to show the B value
                        i+=3; // Get to the next set of colour
                        if(i >= rgbAnimation.length)
                            timer.cancel();
                        // When the pointer reaches the end of the array, terminate the timer
                    });
                }
            }
        });
    }
    public boolean onTouchEvent(MotionEvent touchEvent){ // The touch listener for the TextView
        TextView tv = binding.sampleText; // Bind the sampleText with the activity
        int[] rgb; // The pointer to get every set of colour.
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                // Get the horizontal position of the touch when the finger comes
                // in contact with the screen
                //y1 = touchEvent.getY();
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                // Get the horizontal position of the touch when finger leaves the screen
                //y2 = touchEvent.getY();
                if(x1 < x2){ //
                    Toast.makeText(getApplicationContext(), "Swipe right",
                            Toast.LENGTH_SHORT).show();
                    // A notification to notify that the button is pressed
                    insertNodeAtTheEnd();
                    // The native function to insert a node at the end of the linked list
                    tv.setText(stringFromJNI());
                    // Set the text of the TextView as the id of the current node
                    getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                    // To set the background colour of the Activity
                    tv.setTextColor(Color.WHITE);
                    // To set the colour of the TextView
                    System.out.println("0_0_0");
                }else if(x1 > x2){
                    Toast.makeText(getApplicationContext(), "Swipe left",
                            Toast.LENGTH_SHORT).show();
                    // A notification to notify that the button is pressed
                    //tv.setText(String.valueOf(getRandomRGBValue()));
                    rgb = insertNode();
                    // The native function to insert new node after the current node in the
                    // linked list and return the RGB values in an array.
                    tv.setText(stringFromJNI());
                    // Set the text of the TextView as the id of the current node
                    getWindow().getDecorView().setBackgroundColor(Color.rgb(rgb[0], rgb[1],
                            rgb[2]));
                    // To set the background colour of the Activity
                    System.out.println(rgb[0] + "_" + rgb[1] + "_" + rgb[2]);
                    // The debug message to show the RGB value
                }
        }
        return false;
    }
    /**
     * A native method that is implemented by the 'randomcolourplayer' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int[] insertNode();
    public native int[] replay();
    public native void insertNodeAtTheEnd();
}