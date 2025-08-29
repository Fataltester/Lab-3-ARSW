/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Santiago
 */
public class HostBlackListThread extends Thread {

    private final int start;
    private final int end;
    private final String address;
    private final HostBlacklistsDataSourceFacade skds;
    private final List<Integer> blackListOccurrences = new LinkedList<>();
    private int checkedListsCount;
    private int ocurrencesCount;
    
    public HostBlackListThread(int start, int end, String address, HostBlacklistsDataSourceFacade skds) {
        this.start = start;
        this.end = end;
        this.address = address;
        this.skds = skds;
        checkedListsCount = 0; 
        ocurrencesCount=0;
    }
    
    @Override
    public void run(){
        for(int i = start; i < end; i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, address)){
                blackListOccurrences.add(i);
                ocurrencesCount++;
            }
        }
    }
    
    public int getOcurrencesCount(){
        return ocurrencesCount;
    }
    
    public int getCheckedListsCount(){
        return checkedListsCount;
    }
    
    public List<Integer> getBlackListOccurrences(){
        return blackListOccurrences;
    }
    
}
