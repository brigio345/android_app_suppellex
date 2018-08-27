package it.brigio345.suppellex.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import it.brigio345.suppellex.data.Item;
import it.brigio345.suppellex.data.Storage;
import it.brigio345.suppellex.R;
import it.brigio345.suppellex.adapters.ToAddItemsAdapter;

public class ToAddItemsFragment extends Fragment {
    private final Storage storage = Storage.getInstance();
    private ToAddItemsAdapter toAddItemsFragment;

    public ToAddItemsFragment() {
        // Required empty public constructor
    }

    public static ToAddItemsFragment newInstance() {
        return new ToAddItemsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_to_add_items, container,false);

        ListView aiListView = rootView.findViewById(R.id.listview);
        ArrayList<Item> aiList = storage.getToAddItems();
        toAddItemsFragment = new ToAddItemsAdapter(getActivity().getApplicationContext(), aiList);
        aiListView.setAdapter(toAddItemsFragment);

        return rootView;
    }

    public void notifyDataSetChanged() {
        toAddItemsFragment.notifyDataSetChanged();
    }
}
