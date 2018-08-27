package it.brigio345.suppellex.misc;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import it.brigio345.suppellex.R;
import it.brigio345.suppellex.activities.MainActivity;
import it.brigio345.suppellex.data.DuplicateItemException;
import it.brigio345.suppellex.data.Item;
import it.brigio345.suppellex.data.Storage;

public class ContextualActionBarCallback implements ActionMode.Callback {
    private final Storage storage = Storage.getInstance();
    private ActionMode actionMode;
    private final MainActivity mainActivity;
    private final ExpandableListView expandableListView;

    public ContextualActionBarCallback(MainActivity mainActivity,
                                       ExpandableListView expandableListView) {
        this.mainActivity = mainActivity;
        this.expandableListView = expandableListView;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mainActivity.getWindow().setStatusBarColor(
                ContextCompat.getColor(mainActivity, R.color.colorPrimaryDarkSelected));
        mainActivity.findViewById(R.id.tablayout_main).setBackgroundColor(
                ContextCompat.getColor(mainActivity, R.color.colorPrimarySelected));
        mode.getMenuInflater().inflate(R.menu.menu_context, menu);

        actionMode = mode;

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        final SparseBooleanArray checkedItemPositions = expandableListView
                .getCheckedItemPositions();
        final ActionMode currentMode = mode;

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                builder.setTitle(R.string.delete_confirmation)
                        .setMessage(R.string.delete_confirmation_message)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // starting removing items from the bottom
                                // doesn't mess up indexes in case of multiple deletion
                                for (int i = checkedItemPositions.size() - 1; i != -1; i--)
                                    if (checkedItemPositions.valueAt(i))
                                        storage.removeItem(checkedItemPositions.keyAt(i));
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                currentMode.finish();
                            }
                        });

                builder.show();

                break;

            case R.id.menu_item_edit:
                int i;
                int size = checkedItemPositions.size();
                for (i = 0; i < size; i++)
                    if (checkedItemPositions.valueAt(i))
                        break;

                final int itemPosition = checkedItemPositions.keyAt(i);
                final Item it = storage.getItem(itemPosition);

                View dialogView = mainActivity.getLayoutInflater()
                        .inflate(R.layout.dialog_add_item, null);

                builder.setView(dialogView);

                final TextView titleTV = dialogView.findViewById(R.id.text_view_title);
                final EditText nameET = dialogView.findViewById(R.id.edit_text_name);
                final EditText neededET = dialogView.findViewById(R.id.edit_text_needed);
                final EditText availableET =
                        dialogView.findViewById(R.id.edit_text_available);
                final Switch timeDependantS =
                        dialogView.findViewById(R.id.switch_time_dependant);

                titleTV.setText(R.string.edit);
                nameET.setText(it.getName());
                neededET.setText(String.valueOf(it.getNeeded()));
                availableET.setText(String.valueOf(it.getAvailable()));
                timeDependantS.setChecked(it.isTimeDependent());

                builder.setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                currentMode.finish();
                            }
                        });

                final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        final Button button = ((AlertDialog) dialog)
                                .getButton(AlertDialog.BUTTON_POSITIVE);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = nameET.getText().toString();
                                String neededStr = neededET.getText().toString();
                                String availableStr = availableET.getText().toString();
                                boolean timeDependant = timeDependantS.isChecked();

                                if (name.isEmpty() || neededStr.isEmpty() ||
                                        availableStr.isEmpty()) {
                                    Toast.makeText(mainActivity, R.string.error_empty_fields,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        // remove commas since data will be saved as CSV
                                        name = name.replaceAll(",", ".");
                                        storage.editItem(itemPosition, name,
                                                Integer.parseInt(neededStr),
                                                Integer.parseInt(availableStr), timeDependant);
                                        dialog.dismiss();
                                    } catch (DuplicateItemException die) {
                                        Toast.makeText(mainActivity,
                                                String.format(mainActivity.getString(
                                                        R.string.error_duplicate), name),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

                        // pressing "enter" keyboard button while focus is on latest EditText
                        // will behave the same as pressing "ok" button
                        availableET.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                    switch (keyCode) {
                                        case KeyEvent.KEYCODE_DPAD_CENTER:
                                        case KeyEvent.KEYCODE_ENTER:
                                            button.performClick();
                                            return true;
                                    }
                                }

                                return false;
                            }
                        });
                    }
                });

                dialog.show();

                break;
        }

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mainActivity.findViewById(R.id.tablayout_main).setBackgroundColor(
                ContextCompat.getColor(mainActivity, R.color.colorPrimary));
        mainActivity.getWindow().setStatusBarColor(
                ContextCompat.getColor(mainActivity, R.color.colorPrimaryDark));
        expandableListView.clearChoices();

        mainActivity.notifyDataSetChanged();

        actionMode = null;
    }

    public ActionMode getActionMode() {
        return actionMode;
    }
}
