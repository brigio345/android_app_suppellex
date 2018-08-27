package it.brigio345.suppellex.data;

public class DuplicateItemException extends Exception {
    DuplicateItemException(String itemName) {
        super("Item " + itemName + " already present.");
    }
}