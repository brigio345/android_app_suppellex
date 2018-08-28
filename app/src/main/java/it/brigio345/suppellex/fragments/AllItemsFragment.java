package it.brigio345.suppellex.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import it.brigio345.suppellex.R;
import it.brigio345.suppellex.activities.MainActivity;
import it.brigio345.suppellex.adapters.AllItemsAdapter;
import it.brigio345.suppellex.misc.ContextualActionBarCallback;

import static android.widget.ExpandableListView.PACKED_POSITION_TYPE_CHILD;

public class AllItemsFragment extends Fragment {
    private ExpandableListView expandableListView;
    private AllItemsAdapter allItemsAdapter;
    private MainActivity mainActivity;
    private ContextualActionBarCallback callback;
    private int lastExpandedGroup = -1;

    public AllItemsFragment() {
        // Required empty public constructor
    }

    public static AllItemsFragment newInstance() {
        return new AllItemsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_items, container, false);

        mainActivity = (MainActivity) getActivity();

        expandableListView = rootView.findViewById(R.id.listview_all_items);
        allItemsAdapter = new AllItemsAdapter(this);
        expandableListView.setAdapter(allItemsAdapter);

        /*
         * Let expand only the last clicked group
         */
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedGroup != groupPosition)
                    expandableListView.collapseGroup(lastExpandedGroup);
                lastExpandedGroup = groupPosition;
            }
        });

       callback = new ContextualActionBarCallback(
               mainActivity, expandableListView);

       /*
        * If action mode is running, clicking a group will (de)select it, instead of expanding it
        * N.B.: if action mode is running a group can't be expanded due to the onItemLongClickListener
        */
       expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
           @Override
           public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
               ActionMode actionMode = callback.getActionMode();

               if (actionMode != null) {
                   expandableListView.setItemChecked(groupPosition,
                           !expandableListView.isItemChecked(groupPosition));

                   int checkedItemCount = expandableListView.getCheckedItemCount();

                   switch (checkedItemCount) {
                       case 1:
                           actionMode.getMenu().findItem(R.id.menu_item_edit).setVisible(true);
                           break;

                       case 2:
                           actionMode.getMenu().findItem(R.id.menu_item_edit).setVisible(false);
                           break;

                       case 0:
                           actionMode.finish();
                           return true;
                   }

                   actionMode.setTitle(String.valueOf(checkedItemCount));

                   return true;
               }

               // this allows the normal expanding/collapsing of groups when no item is selected
               return false;
           }
       });

       /*
        * If action mode is already running, do nothing
        * Otherwise if the long clicked item is a child, do nothing
        * Otherwise start action mode and select the long clicked group
        */
        expandableListView.setOnItemLongClickListener(new AbsListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (callback.getActionMode() != null)
                    return false;

                long packedPosition = expandableListView.getExpandableListPosition(position);

                if (ExpandableListView.getPackedPositionType(packedPosition)
                        == PACKED_POSITION_TYPE_CHILD)
                    return false;

                int groupCount = allItemsAdapter.getGroupCount();
                for (int i = 0; i < groupCount; i++)
                    if (expandableListView.isGroupExpanded(i)) {
                        expandableListView.collapseGroup(i);
                        break;
                    }

                mainActivity.startSupportActionMode(callback);

                expandableListView.setItemChecked(position,
                        !expandableListView.isItemChecked(position));

                return true;
            }
        });

        return rootView;
    }

    public void manageUpdatedItem(int position) {
        expandableListView.collapseGroup(position);
        mainActivity.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        allItemsAdapter.notifyDataSetChanged();
    }

    public ActionMode getActionMode() {
        return callback.getActionMode();
    }

    public void collapseGroup() {
        if (expandableListView.isGroupExpanded(lastExpandedGroup))
            expandableListView.collapseGroup(lastExpandedGroup);
    }
}