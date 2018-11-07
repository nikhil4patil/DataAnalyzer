/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author aribdhuka
 */
public class CategoricalHashMap {
    
    ArrayList<String> tags;
    //Actual table
    LinkedList<LogObject>[] table;
    
    //constant load factor. Do not exceed
    private final float loadFactor = .75f;
    
    //constructor with base size of 20
    public CategoricalHashMap() {
        table = new LinkedList[20];
        tags = new ArrayList<>();
    }
    
    //constructor that allows user to define size as long as > 20
    public CategoricalHashMap(int size) {
        table = new LinkedList[Math.max(size, 20)];
        tags = new ArrayList<>();
    }
    
    //put a new value into the HashMap
    public void put(LogObject lo) {
        put(lo, table);
    }
    
    private void put(LogObject lo, LinkedList<LogObject>[] table) {
        //calculate the index from the tag
        int index = Math.abs(lo.getTAG().hashCode()) % table.length;
        if(!tags.contains(lo.getTAG())) {
            tags.add(lo.getTAG());
        }

        //find next null, empty, or matching tag position
        while(table[index] != null && !table[index].isEmpty() && !table[index].getFirst().getTAG().equals(lo.getTAG())) {
            index++;
            index %= table.length;
        }
        //if position is null create linked list here
        if(table[index] == null)
            table[index] = new LinkedList<>();
        //add object here
        table[index].add(lo);
        //check table load
        checkLoad();
    }
    
    public void put(LinkedList<LogObject> los) {
        put(los, table);
    }
    
    private void put(LinkedList<LogObject> los, LinkedList<LogObject>[] table) {
        //get the index based on the first element of the list. Assume all other elements are the same
        int index = Math.abs(los.get(0).getTAG().hashCode()) % table.length;
        
        //if the tag of the first element does not exist add it to the list of tags
        if(!tags.contains(los.get(0).getTAG())) {
            tags.add(los.get(0).getTAG());
            broadcastSizeChange();
        }
        
        //find next null, empty, or matching tag position
        while(table[index] != null && !table[index].isEmpty() && !table[index].getFirst().getTAG().equals(los.get(0).getTAG())) {
            index++;
            index %= table.length;
        }
        
        //put the list here
        table[index] = los;
        
        //check the load
        checkLoad();
        
    }
    
    
    
    //check the load and run and rehash if neccesary
    private void checkLoad() {
        //find how many places have a linked list with at least one element
        int loadCount = 0;
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null && !table[i].isEmpty())
                loadCount++;
        }
        
        //calculate the float value of load
        float load = loadCount / table.length;
        //if we exceed our loadfactor -- rehash
        if(load > loadFactor)
            rehash();
    }
    
    //extremely expensive function
    private void rehash() {
        //create new table
        LinkedList<LogObject>[] newTable = new LinkedList[table.length * 2];
        //for each index
        for(int i = 0; i < table.length; i++) {
            //if its null next index
            if(table[i] == null)
                continue;
            //use put list to put the whole list in the new table
            put(table[i], newTable);
        }
        
        //current table = new table
        table = newTable;
    }
    
    //get function
    public LinkedList<LogObject> getList(String TAG) {
        //for each linkedlist in the hash table
        for (LinkedList<LogObject> table1 : table) {
            //if its null or empty skip
            if (table1 == null || table1.isEmpty())
                continue;
            //if we find a linked list that matches the tag
            if(table1.getFirst().getTAG().equals(TAG))
                //return the whole linked list
                return table1;
        }
        
        //if we didnt find it return null
        return null;
    }
}
