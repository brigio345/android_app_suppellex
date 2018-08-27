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

    public void loadFromFile(FileInputStream fis) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            String[] tokens;
            Item item;
            while ((line = br.readLine()) != null) {
                tokens = line.split(",");

                item = new Item(tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                        Boolean.parseBoolean(tokens[3]));

                items.add(item);

                if (item.isToAdd())
                    toAddItems.add(item);
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

    public void updateItem(int index, String name, int needed, int available, boolean timeDependent)
        throws DuplicateItemException {
        Item it = items.get(index);

        if (!it.getName().equals(name)) {
            for (Item i : items)
                if (i.getName().equals(name))
                    throw new DuplicateItemException(name);

            it.setName(name);
        }

        it.setTimeDependent(timeDependent);

        boolean oldNeeded = it.isToAdd();

        if (needed != -1)
            it.setNeeded(needed);

        if (available != -1)
            it.setAvailable(available);

        boolean newNeeded = it.isToAdd();

        if (newNeeded != oldNeeded) {
            if (newNeeded)
                toAddItems.add(it);

            else
                toAddItems.remove(it);
        }
    }

    public void removeItem(int index) {
        Item it = items.get(index);
        items.remove(index);

        if (it.isToAdd())
            toAddItems.remove(it);
    }

    public void updateItemAvailability(int position, int available) {
        Item it = items.get(position);

        boolean oldNeeded = it.isToAdd();

        it.setAvailable(available);

        boolean newNeeded = it.isToAdd();

        if (newNeeded != oldNeeded) {
            if (newNeeded)
                toAddItems.add(it);

            else
                toAddItems.remove(it);
        }
    }
}