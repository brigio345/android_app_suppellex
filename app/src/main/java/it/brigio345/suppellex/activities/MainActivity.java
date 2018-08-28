package it.brigio345.suppellex.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import it.brigio345.suppellex.data.DuplicateItemException;
import it.brigio345.suppellex.data.Storage;
import it.brigio345.suppellex.fragments.AllItemsFragment;
import it.brigio345.suppellex.adapters.ItemsPagerAdapter;
import it.brigio345.suppellex.R;
import it.brigio345.suppellex.fragments.ToAddItemsFragment;

public class MainActivity extends AppCompatActivity {
    private final Storage storage = Storage.getInstance();
    private ItemsPagerAdapter itemsPagerAdapter;
    private boolean dataSetChanged = false;

    private static final String FILE_NAME = "storage.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsPagerAdapter = new ItemsPagerAdapter(getSupportFragmentManager(),
                AllItemsFragment.newInstance(), ToAddItemsFragment.newInstance(), this);

        ViewPager mViewPager = findViewById(R.id.viewpager_main);
        mViewPager.setAdapter(itemsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    itemsPagerAdapter.collapseAllItemsGroup();

                    ActionMode actionMode = itemsPagerAdapter.getActionMode();
                    if (actionMode != null)
                        actionMode.finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        builder.setView(dialogView);

        final EditText nameET = dialogView.findViewById(R.id.edit_text_name);
        final EditText neededET = dialogView.findViewById(R.id.edit_text_needed);
        final EditText availableET = dialogView.findViewById(R.id.edit_text_available);
        final Switch timeDependantS = dialogView.findViewById(R.id.switch_time_dependant);

        builder.setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null);

        final AlertDialog dialog = builder.create();

        final InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = nameET.getText().toString();
                        String neededStr = neededET.getText().toString();
                        String availableStr = availableET.getText().toString();
                        boolean timeDependant = timeDependantS.isChecked();

                        if (name.isEmpty() || neededStr.isEmpty() || availableStr.isEmpty()) {
                            Toast.makeText(getApplicationContext(), R.string.error_empty_fields,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                // remove commas since data will be saved as CSV
                                name = name.replaceAll(",", ".");
                                storage.addItem(name, Integer.parseInt(neededStr),
                                        Integer.parseInt(availableStr), timeDependant);
                                notifyDataSetChanged();
                                dialog.dismiss();
                                nameET.setText("");
                                neededET.setText("");
                                availableET.setText("");
                                timeDependantS.setChecked(true);
                            } catch (DuplicateItemException die) {
                                Toast.makeText(getApplicationContext(),
                                        String.format(getString(R.string.error_duplicate), name),
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
                                    inputMethodManager.toggleSoftInput(
                                            InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    return true;
                                default:
                                    break;
                            }
                        }

                        return false;
                    }
                });
            }
        });

        final FloatingActionButton fab = findViewById(R.id.floatingactionbutton_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                nameET.requestFocus();
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        if (storage.getItems().size() == 0)
            try (FileInputStream fis = openFileInput(FILE_NAME)) {
                storage.loadFromFile(fis);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (dataSetChanged) {
            try (FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                storage.saveToFile(fos);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void notifyDataSetChanged() {
        itemsPagerAdapter.notifyDataSetChanged();
        dataSetChanged = true;
    }
}