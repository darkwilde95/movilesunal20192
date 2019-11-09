package co.edu.unal.tictactoe;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GamesList extends Fragment {

    private DatabaseReference mDatabase;
    private ListView gamesList;
    private ArrayAdapter<ListTile> adapter;
    private String nickname;

    public GamesList(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View gamesListFragment = inflater.inflate(R.layout.fragment_games_list, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        gamesList = (ListView) gamesListFragment.findViewById(R.id.games_list);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1);
        gamesList.setAdapter(adapter);


        mDatabase.child("rooms").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Room newRoom = dataSnapshot.getValue(Room.class);
                ListTile tile = new ListTile(newRoom.roomId, newRoom.creatorPlayer);
                if (newRoom.open) {
                    adapter.add(tile);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Room updatedRoom = dataSnapshot.getValue(Room.class);
                ListTile tile = new ListTile(updatedRoom.roomId, updatedRoom.creatorPlayer);
                if (!updatedRoom.open) {
                    adapter.remove(tile);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Room deleted = dataSnapshot.getValue(Room.class);
                ListTile tile = new ListTile(deleted.roomId, deleted.creatorPlayer);
                adapter.remove(tile);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.w("FIREBASE", "onChildMoved activated");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FIREBASE", "Error en la actualizacion de la lista");
            }
        });

        gamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListTile selected = adapter.getItem(position);
                mDatabase.child("rooms").child(selected.roomId).child("open").setValue(false);
                mDatabase.child("rooms").child(selected.roomId).child("guestPlayer").setValue(nickname);
                OnlineGame newFragment = new OnlineGame(selected.roomId, false);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return gamesListFragment;
    }

}
