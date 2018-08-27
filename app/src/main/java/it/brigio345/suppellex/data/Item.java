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

    public void setNeeded(int newNeeded) {
        this.needed = newNeeded;
        this.toAdd = this.needed - this.available;
        if (this.toAdd < 0)
            this.toAdd = 0;
    }

    public void setAvailable(int newAvailable) {
        this.available = newAvailable;
        this.toAdd = this.needed - this.available;
        if (this.toAdd < 0)
            this.toAdd = 0;
    }

    private void setToAdd() {
        toAdd = needed - available > 0 ? needed - available : 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeDependent(boolean timeDependent) {
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