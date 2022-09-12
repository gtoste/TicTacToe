package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Game_Offline = findViewById(R.id.offline);
        Button Game_Online = findViewById(R.id.online);

        Play(Game_Offline, "0"); //gamemode 0
        Play(Game_Online, "1"); //gamemode 1
    }

    private void Play(Button Btn, String gamemode)
    {
        Btn.setOnClickListener(view -> {
            Intent game_intent = new Intent(this, Game.class);
            game_intent.putExtra("gamemode", gamemode);
            startActivity(game_intent);
        });
    }
}