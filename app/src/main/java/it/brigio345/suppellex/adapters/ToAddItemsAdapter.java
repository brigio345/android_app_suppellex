package it.brigio345.suppellex.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.brigio345.suppellex.data.Item;
import it.brigio345.suppellex.R;

public class ToAddItemsAdapter extends ArrayAdapter<Item> {
    private final ArrayList<Item> taiArrayList;
    private final Context context;

    public ToAddItemsAdapter(Context context, ArrayList<Item> taiArrayList) {
        super(context, R.layout.row_toadditems, taiArrayList);
        this.taiArrayList = taiArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Item item = taiArrayList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_toadditems, parent, false);

        TextView itemName = rowView.findViewById(R.id.textview_toadditemsrow_itemname);
        itemName.setText(item.getName());
        TextView toAdd = rowView.findViewById(R.id.textview_toadditemsrow_toadd);
        toAdd.setText(String.format(context.getString(R.string.to_add_number), item.getToAdd()));

        return rowView;
    }
}