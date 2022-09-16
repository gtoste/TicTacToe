package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.Objects;




public class Game extends AppCompatActivity {

    String gamemode;
    Boolean your_turn;
    public enum Player{
        X, O
    }
    Player player = Player.X;
    int[][] board = new int[3][3];

    TextView playerX;
    TextView playerO;

    private String playerId = "0";
    private String player_name = "";
    private String gameId = "";

    private ValueEventListener turnsEventListener, winEventListener;

    FirebaseDatabase database;
    DatabaseReference databaseReference;
    boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerO = findViewById(R.id.playerO);
        playerX = findViewById(R.id.playerX);
        TextView player1 = (TextView) findViewById(R.id.player_1_text);
        player1.setText("");
        TextView player2 = (TextView) findViewById(R.id.player_2_text);
        player2.setText("grasz");


        database = FirebaseDatabase.getInstance("https://tictactoe-e317b-default-rtdb.firebaseio.com/");
        databaseReference = database.getReference("database");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.gamemode = extras.getString("gamemode");
            this.player_name = extras.getString("nickname");
        }

        playerId = String.valueOf(System.currentTimeMillis());

//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Waiting for oponent");
//        progressDialog.show();

        if(Objects.equals(this.gamemode, "0"))
        {
            player = Player.X;
            your_turn = true;
        }else
        {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    DataSnapshot games = dataSnapshot.child("games");
                    for(DataSnapshot game : games.getChildren())
                    {
                        //if room open
                        if(game.getChildrenCount() < 3)
                        {
                            gameId = game.getKey();
                            databaseReference.child("games").child(gameId).child(playerId).setValue(player_name);
                            playerX.setText(player_name);
                            found = true;
                        }
                    }

                    if(!found)
                    {
                        //create new room
                        gameId = String.valueOf(System.currentTimeMillis());
                        databaseReference.child("games").child(gameId).child(playerId).setValue(player_name);
                        databaseReference.child("games").child(gameId).child("last_move").setValue("null");
                        playerO.setText(player_name);
                        found = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference match = databaseReference.child("games").child(gameId);
            match.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("test", dataSnapshot.child(gameId).getKey());
                    Log.d("test", (String) dataSnapshot.child(gameId).getValue());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        return board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0;
    }

    private void showAlert(String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(Game.this).create();
        alertDialog.setTitle("Alert Dialog");
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }


    public void onClick(View view)
    {
        String ID = (String) view.getTag();
        int i = ID.charAt(0) - 48;
        int j = ID.charAt(1) - 48;

        LinearLayout plate = (LinearLayout) view;

        if(your_turn && board[i][j] == 0)
        {
            TextView playerX = (TextView) findViewById(R.id.player_1_text);
            TextView playerO = (TextView) findViewById(R.id.player_2_text);
            String playerX_text = player == Player.X ? "grasz" : "";
            String playerO_text = player == Player.O ? "grasz" : "";
            playerX.setText(playerX_text);
            playerO.setText(playerO_text);

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
                databaseReference.child("games").child(gameId).child("last_move").child("row").setValue(String.valueOf(i));
                databaseReference.child("games").child(gameId).child("last_move").child("col").setValue(String.valueOf(j));
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