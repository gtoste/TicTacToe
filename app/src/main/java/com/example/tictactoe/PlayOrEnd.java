package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PlayOrEnd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_or_end);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TextView t = (TextView) findViewById(R.id.text);
            t.setText("Winner: " + extras.getString("winner"));
        }
    }

    public void play(View v)
    {
        Intent game_intent = new Intent(this, Game.class);
        startActivity(game_intent);
    }

    public void end(View v)
    {
        Intent end_intent = new Intent(this, MainActivity.class);
        startActivity(end_intent);
    }
}