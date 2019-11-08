package co.edu.unal.tictactoe;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GamesList extends Fragment {

    private DatabaseReference mDatabase;
    private ListView games;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View gamesListFragment = inflater.inflate(R.layout.fragment_games_list, container, false);
        String[] g = {"Game1", "Game2"};
        mDatabase = FirebaseDatabase.getInstance().getReference();
        games = (ListView) gamesListFragment.findViewById(R.id.games_list);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, g);

        games.setAdapter(adapter);

        return gamesListFragment;
    }

}
