package it.brigio345.suppellex.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Storage {
    private static final Storage instance = new Storage();

    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Item> toAddItems = new ArrayList<>();

    private Storage() {}

    public static Storage getInstance() {
        return instance;
    }

    public void addItem(String name, int needed, int available, boolean timeDependant)
            throws DuplicateItemException {
            for (Item i: items)
                if (i.getName().equals(name))
                    throw new DuplicateItemException(name);

            Item item = new Item(name, needed, available, timeDependant);
            items.add(item);

            if (item.isToAdd())
                toAddItems.add(item);
    }

    /*
     * Print to file data contained in "item" List using CSV format
     */
    public void saveToFile(FileOutputStream fos) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
            Item item;
            int size = items.size();
            for (int i = 0; i < size; i++) {
                item = items.get(i);
                bw.write(item.getName() + "," + item.getNeeded() + "," +
                        item.getAvailable() + "," + item.isTimeDependent() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Read data from CSV formatted file
     */
    public void loadFromFile(FileInputStream fis) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            String[] tokens;
            Item item;
            while ((line = br.readLine()) != null) {
                tokens = line.split(",");

                try {
                    item = new Item(tokens[0], Integer.parseInt(tokens[1]),
                            Integer.parseInt(tokens[2]), Boolean.parseBoolean(tokens[3]));

                    items.add(item);

                    if (item.isToAdd())
                        toAddItems.add(item);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> getItems () {
        return items;
    }

    public ArrayList<Item> getToAddItems() {
        return toAddItems;
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    /*
     * Edit the item identified by the index
     * If the new name corresponds to another existing item, throw a DuplicateItemException
     */
    public void editItem(int index, String name, int needed, int available, boolean timeDependent)
        throws DuplicateItemException {
        Item item = items.get(index);

        if (!item.getName().equals(name)) {
            for (Item i : items)
                if (i.getName().equals(name))
                    throw new DuplicateItemException(name);

            item.setName(name);
        }

        item.setTimeDependent(timeDependent);

        boolean oldNeeded = item.isToAdd();

        item.setNeeded(needed);
        item.setAvailable(available);

        updateToAddItems(item, oldNeeded);
    }

    public void removeItem(int index) {
        Item item = items.get(index);
        items.remove(index);

        if (item.isToAdd())
            toAddItems.remove(item);
    }

    public void updateItemAvailability(int index, int available) {
        Item item = items.get(index);

        boolean oldToAdd = item.isToAdd();

        item.setAvailable(available);

        updateToAddItems(item, oldToAdd);
    }

    public void updateToAddItems(Item item, boolean oldToAdd) {
        if (item.isToAdd() != oldToAdd) {
            if (oldToAdd)
                toAddItems.remove(item);

            else
                toAddItems.add(item);
        }
    }
}