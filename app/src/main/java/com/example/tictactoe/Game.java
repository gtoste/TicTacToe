package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Objects;

public class Game extends AppCompatActivity {

    String gamemode;
    public enum Player{
        X, O
    }
    Player player = Player.X;
    int[][] board = new int[3][3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        TextView player1 = (TextView) findViewById(R.id.player_1_text);
        player1.setText("");
        TextView player2 = (TextView) findViewById(R.id.player_2_text);
        player2.setText("grasz");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.gamemode = extras.getString("gamemode");
        }

        if(Objects.equals(this.gamemode, "0"))
        {
            //offline
        }else{
            //online
        }
    }


    public Boolean check()
    {
        if(board[0][0] == board[0][1] && board[0][1] ==  board[0][2] && board[0][0] != 0) return true;
        if(board[1][0] == board[1][1] && board[1][1] ==  board[1][2] && board[1][0] != 0) return true;
        if(board[2][0] == board[2][1] && board[2][1] ==  board[2][2] && board[2][0] != 0) return true;
        if(board[0][0] == board[1][0] && board[1][0] ==  board[2][0] && board[0][0] != 0) return true;
        if(board[0][1] == board[1][1] && board[1][1] ==  board[2][1] && board[0][1] != 0) return true;
        if(board[0][2] == board[1][2] && board[1][2] ==  board[2][2] && board[0][2] != 0) return true;
        if(board[0][0] == board[1][1] && board[1][1] ==  board[2][2] && board[0][0] != 0) return true;
        if(board[0][2] == board[1][1] && board[1][1] ==  board[2][0] && board[0][2] != 0) return true;

        return false;
    }


    public void onClick(View view)
    {
        String ID = (String) view.getTag();
        int i = ID.charAt(0) - 48;
        int j = ID.charAt(1) - 48;

        LinearLayout plate = (LinearLayout) view;

        if (board[i][j] == 0 && player == Player.X) {

            TextView player1 = (TextView) findViewById(R.id.player_1_text);
            player1.setText("grasz");
            TextView player2 = (TextView) findViewById(R.id.player_2_text);
            player2.setText("");


            ImageView v = new ImageView(Game.this);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            v.setImageResource(R.drawable.ic_outline_superscript_24);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            board[i][j] = 1;

            if(check())
            {
                String winner = "X";
                Intent end_intent = new Intent(this, PlayOrEnd.class);
                end_intent.putExtra("winner", winner);
                startActivity(end_intent);
            }
            else if(checkForTie()){
                String winner = "TIE";
                Intent end_intent = new Intent(this, PlayOrEnd.class);
                end_intent.putExtra("winner", winner);
                startActivity(end_intent);
            }
            player = Player.O;
            plate.addView(v);
        } else if (board[i][j] == 0 && player == Player.O) {

            TextView player1 = (TextView) findViewById(R.id.player_1_text);
            player1.setText("");
            TextView player2 = (TextView) findViewById(R.id.player_2_text);
            player2.setText("grasz");

            ImageView v = new ImageView(Game.this);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            v.setImageResource(R.drawable.circle);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            board[i][j] = -1;

            if(check())
            {
                String winner = "O";
                Intent end_intent = new Intent(this, PlayOrEnd.class);
                end_intent.putExtra("winner", winner);
                startActivity(end_intent);
            }
            else if(checkForTie()){
                String winner = "TIE";
                Intent end_intent = new Intent(this, PlayOrEnd.class);
                end_intent.putExtra("winner", winner);
                startActivity(end_intent);
            }
            player = Player.X;
            plate.addView(v);
        }


    }

    private Boolean checkForTie()
    {
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                if(board[i][j] == 0) return false;
            }
        }
        return true;
    }
}