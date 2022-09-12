package com.example.tictactoe;

import androidx.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;




public class Game extends AppCompatActivity {

    String gamemode;
    Boolean your_turn;
    public enum Player{
        X, O
    }
    Player player = Player.X;
    int[][] board = new int[3][3];

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference con = database.getReferenceFromUrl("https://tictactoe-e317b-default-rtdb.firebaseio.com/");

    private String player_id = "0";
    private Boolean playerFound = false;

    private String oponent_id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        player_id = String.valueOf(System.currentTimeMillis());



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
            your_turn = true;
        }else{

            con.child("cons").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            your_turn = true;
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

        if(your_turn && board[i][j] == 0)
        {
            TextView player1 = (TextView) findViewById(R.id.player_1_text);
            TextView player2 = (TextView) findViewById(R.id.player_2_text);
            String player1_text = player == Player.X ? "grasz" : "";
            String player2_text = player == Player.O ? "grasz" : "";
            player1.setText(player1_text);
            player2.setText(player2_text);

            ImageView v = new ImageView(Game.this);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            v.setImageResource( player == Player.X ? R.drawable.ic_outline_superscript_24 : R.drawable.circle);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            board[i][j] = player == Player.X ? 1 : -1;

            if(check())
            {
                String winner = player == Player.X ? "X" : "O";
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

            if(Objects.equals(this.gamemode, "0"))
            {
                player = player == Player.X ? Player.O : Player.X;
            }else{
                your_turn = false;
            }

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