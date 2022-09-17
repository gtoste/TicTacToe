package com.example.tictactoe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
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
    private String opponentNick = "";
    private final String turn = "turn";

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://tictactoe-e317b-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference = database.getReference("database");
    boolean found = false;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerO = findViewById(R.id.playerO);
        playerX = findViewById(R.id.playerX);
        TextView player1 = findViewById(R.id.player_1_text);
        player1.setText("");
        TextView player2 = findViewById(R.id.player_2_text);
        player2.setText(turn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.gamemode = extras.getString("gamemode");
            this.player_name = extras.getString("nickname");
        }

        if(Objects.equals(this.gamemode, "0"))
        {
            player = Player.X;
            your_turn = true;
        }else if(Objects.equals(this.gamemode, "1"))
        {
            playerId = String.valueOf(System.currentTimeMillis());

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Waiting for opponent");
            progressDialog.show();


            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    DataSnapshot games = dataSnapshot.child("games");
                    for(DataSnapshot game : games.getChildren())
                    {
                        if(game.child("players").getChildrenCount() < 2)
                        {
                            gameId = game.getKey();
                            for( DataSnapshot player : game.child("players").getChildren() )
                            {
                                String userName =  player.getValue(String.class);
                                if(!playerId.equals(player.getKey()))
                                {
                                    opponentNick = userName;
                                    playerX.setText(opponentNick);
                                }
                            }
                            databaseReference.child("games").child(gameId).child("players").child(playerId).setValue(player_name);
                            playerO.setText(player_name);
                            player = Player.O;
                            found = true;
                            your_turn = false;
                        }
                    }

                    if(!found)
                    {
                        //create new room
                        gameId = String.valueOf(System.currentTimeMillis());
                        databaseReference.child("games").child(gameId).child("players").child(playerId).setValue(player_name);
                        List<Integer> moves = new ArrayList<>();
                        moves.add(-1);
                        moves.add(-1);
                        databaseReference.child("games").child(gameId).child("last_move").setValue(moves);
                        playerX.setText(player_name);
                        player = Player.X;
                        found = true;
                        your_turn = true;
                    }

                    DatabaseReference match = databaseReference.child("games").child(gameId).getRef();

                    match.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int getOpponentsCount = (int)dataSnapshot.child("players").getChildrenCount();
                            if(getOpponentsCount == 2)
                            {
                                for (DataSnapshot player : dataSnapshot.child("players").getChildren())
                                {
                                    if(!Objects.equals(player.getKey(), playerId) && opponentNick.equals(""))
                                    {
                                        opponentNick = String.valueOf(player.getValue());
                                        playerO.setText(opponentNick);
                                    }
                                }

                                progressDialog.dismiss();
                            }
                            int i,j;

                            i = Integer.parseInt(String.valueOf(dataSnapshot.child("last_move").child("0").getValue()));
                            j = Integer.parseInt(String.valueOf(dataSnapshot.child("last_move").child("1").getValue()));

                            if(i != -1 && j != -1 && board[i][j] == 0)
                            {
                                String plateID = "p" + i + "" + j;
                                LinearLayout plate = (LinearLayout) findViewById(getResources().getIdentifier(plateID, "id", getPackageName()));
                                board[i][j] = player == Player.X ? -1 : 1;
                                ImageView v = new ImageView(Game.this);
                                v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                v.setImageResource( player == Player.O ? R.drawable.ic_outline_superscript_24 : R.drawable.circle);
                                v.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                plate.addView(v);

                                if( check() )
                                {
                                    endDialog("Lost!", "Player " + opponentNick + " got lucky this time!").show();
                                }else if(checkForTie())
                                {
                                    endDialog("Tie!", "You have almost won!").show();
                                }

                                your_turn = true;
                                changeTurns(true);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private AlertDialog endDialog(String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(Game.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    databaseReference.child("games").child(gameId).child("players").removeValue();
                    Intent intent = new Intent(Game.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
        return alertDialog;
    }

    private void changeTurns(Boolean opponentTurn)
    {
        TextView playerX = findViewById(R.id.player_1_text);
        TextView playerO = findViewById(R.id.player_2_text);
        String playerX_text = "";
        String playerO_text = "";
        if(!opponentTurn)
        {
            playerX_text = player == Player.X ? turn : "";
            playerO_text = player == Player.O ? turn : "";
        }else{
            playerX_text = player == Player.O ? turn : "";
            playerO_text = player == Player.X ? turn : "";
        }

        playerX.setText(playerX_text);
        playerO.setText(playerO_text);
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


    public void onClick(View view)
    {
        String ID = (String) view.getTag();
        int i = ID.charAt(0) - 48;
        int j = ID.charAt(1) - 48;

        changeTurns(false);

        LinearLayout plate = (LinearLayout) view;

        if(your_turn && board[i][j] == 0)
        {


            ImageView v = new ImageView(Game.this);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            v.setImageResource( player == Player.X ? R.drawable.ic_outline_superscript_24 : R.drawable.circle);
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            board[i][j] = player == Player.X ? 1 : -1;

            if(check())
            {
                if(gamemode.equals("0"))
                {
                    String winner = player == Player.X ? "X" : "O";
                    Intent end_intent = new Intent(this, PlayOrEnd.class);
                    end_intent.putExtra("winner", winner);
                    startActivity(end_intent);
                }else{
                    endDialog("Win!", "You're clearly better than " + opponentNick).show();
                }
            }
            else if(checkForTie()){
                if(gamemode.equals("0"))
                {
                    String winner = "Tie";
                    Intent end_intent = new Intent(this, PlayOrEnd.class);
                    end_intent.putExtra("winner", winner);
                    startActivity(end_intent);
                }else{
                    endDialog("Tie!", "You have almost won!").show();
                }
            }

            if(Objects.equals(this.gamemode, "0"))
            {
                player = player == Player.X ? Player.O : Player.X;
            }else{
                your_turn = false;
                List<Integer> moves = new ArrayList<Integer>();
                moves.add(i);
                moves.add(j);
                databaseReference.child("games").child(gameId).child("last_move").setValue(moves);
            }

            plate.addView(v);
        }
    }

    private Boolean checkForTie()
    {
        for (int[] ints : board) {
            for (int anInt : ints) {
                if (anInt == 0) return false;
            }
        }
        return true;
    }
}