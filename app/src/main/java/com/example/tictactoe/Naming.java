package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Naming extends AppCompatActivity {

    private String gamemode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naming);
        Log.d("s", "?: ");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gamemode = extras.getString("gamemode");
        }

        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(this::play);
    }

    public void play(View v){
        EditText naming_edit = findViewById(R.id.nickname);
        String name = naming_edit.getText().toString();

        Intent game_intent = new Intent(this, Game.class);
        game_intent.putExtra("gamemode", gamemode);
        game_intent.putExtra("nickname", name);
        startActivity(game_intent);
    }
}