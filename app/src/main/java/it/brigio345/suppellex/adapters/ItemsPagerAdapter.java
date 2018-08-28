package it.brigio345.suppellex.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.view.ActionMode;

import it.brigio345.suppellex.R;
import it.brigio345.suppellex.fragments.AllItemsFragment;
import it.brigio345.suppellex.fragments.ToAddItemsFragment;

public class ItemsPagerAdapter extends FragmentPagerAdapter {
    private final AllItemsFragment allItemsFragment;
    private final ToAddItemsFragment toAddItemsFragment;
    private final Context context;

    public ItemsPagerAdapter(FragmentManager fm, AllItemsFragment allItemsFragment,
                             ToAddItemsFragment toAddItemsFragment, Context context) {
        super(fm);
        this.allItemsFragment = allItemsFragment;
        this.toAddItemsFragment = toAddItemsFragment;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return allItemsFragment;

            case 1:
                return toAddItemsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.all);

            case 1:
                return context.getString(R.string.to_add);

            default:
                return null;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        allItemsFragment.notifyDataSetChanged();
        toAddItemsFragment.notifyDataSetChanged();
    }

    public ActionMode getActionMode() {
        return allItemsFragment.getActionMode();
    }

    public void collapseAllItemsGroup() {
        allItemsFragment.collapseGroup();
    }
}
