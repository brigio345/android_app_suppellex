package it.brigio345.suppellex.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import it.brigio345.suppellex.data.Item;
import it.brigio345.suppellex.R;
import it.brigio345.suppellex.data.Storage;
import it.brigio345.suppellex.fragments.AllItemsFragment;

public class AllItemsAdapter extends BaseExpandableListAdapter {
    private final ArrayList<Item> taiArrayList;
    private final AllItemsFragment allItemsFragment;
    private final Context context;
    private final Activity activity;
    private final LayoutInflater inflater;
    private final InputMethodManager inputMethodManager;
    private final Storage storage = Storage.getInstance();
    private int previousListPosition = -1;

    public AllItemsAdapter(AllItemsFragment allItemsFragment) {
        this.taiArrayList = storage.getItems();
        this.allItemsFragment = allItemsFragment;
        context = allItemsFragment.getContext();
        activity = allItemsFragment.getActivity();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return null;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return null;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.child_all_items, parent, false);

        final EditText editText = convertView.findViewById(R.id.edit_text_new_available);

        if (listPosition != previousListPosition) {
            previousListPosition = listPosition;

            final Button button = convertView.findViewById(R.id.button_new_available);

            editText.setText("");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newAvailable = editText.getText().toString();

                    if (!newAvailable.isEmpty()) {
                        storage.updateItemAvailability(listPosition, Integer.parseInt(newAvailable));
                        allItemsFragment.manageUpdatedItem(listPosition);
                        previousListPosition = -1;
                    }
                }
            });

            // pressing "enter" keyboard button while focus is on the EditText
            // will behave the same as pressing "ok" button
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                button.performClick();
                                return true;
                            default:
                                break;
                        }
                    }

                    return false;
                }
            });
        }

        editText.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return taiArrayList.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @NonNull
    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        Item item = taiArrayList.get(listPosition);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.group_allitems, parent, false);

        TextView itemName = convertView.findViewById(R.id.textview_allitemsrow_itemname);
        itemName.setText(item.getName());
        TextView needed = convertView.findViewById(R.id.textview_allitemsrow_needed);
        needed.setText(String.format(context.getString(R.string.needed_number), item.getNeeded()));
        TextView available = convertView.findViewById(R.id.textview_allitemsrow_available);
        available.setText(String.format(context.getString(R.string.available_number), item.getAvailable()));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}