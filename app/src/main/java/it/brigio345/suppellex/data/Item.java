package it.brigio345.suppellex.data;

public class Item {
    private String name;
    private int needed;
    private int available;
    private int toAdd;
    private boolean timeDependent;

    Item(String name, int needed, int available, boolean timeDependent) {
        this.name = name;
        this.needed = needed;
        this.available = available;
        this.timeDependent = timeDependent;
        setToAdd();
    }

    void setNeeded(int newNeeded) {
        this.needed = newNeeded;
        setToAdd();
    }

    void setAvailable(int newAvailable) {
        this.available = newAvailable;
        setToAdd();
    }

    void setToAdd() {
        toAdd = needed - available > 0 ? needed - available : 0;
    }

    void setName(String name) {
        this.name = name;
    }

    void setTimeDependent(boolean timeDependent) {
        this.timeDependent = timeDependent;
    }

    public String getName() {
        return name;
    }

    public int getNeeded() {
        return needed;
    }

    public int getAvailable() {
        return available;
    }

    public int getToAdd() {
        return toAdd;
    }

    public boolean isToAdd() {
        return toAdd != 0;
    }

    public boolean isTimeDependent() {
        return timeDependent;
    }
}