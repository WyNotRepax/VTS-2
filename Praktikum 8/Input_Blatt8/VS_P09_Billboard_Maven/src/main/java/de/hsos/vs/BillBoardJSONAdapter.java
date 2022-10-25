/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsos.vs;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author User
 */
public class BillBoardJSONAdapter extends BillBoard implements BillBoardAdapterIf {

    public BillBoardJSONAdapter(String ctxt) {
        super(ctxt);
    }

    @Override
    public String readEntries(String caller_ip) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");
        stringBuilder.append(String.format("\t\"size\":%d,\n", this.billboard.length));
        stringBuilder.append("\t\"billboard\":[\n");
        //for(int i = 0; i < this.billboard.length; i++){
        //    stringBuilder.append("\t\t" + readEntry(i,caller_ip));
        //    if(i != this.billboard.length - 1){
        //        stringBuilder.append(",");
        //    }
        //    stringBuilder.append("\n");
        //}
        final String copy = caller_ip;
        stringBuilder.append(
                Arrays.stream(this.billboard).filter(new Predicate<BillBoardEntry>() {
                    @Override
                    public boolean test(BillBoardEntry billBoardEntry) {
                        return billBoardEntry.text != null;
                    }
                }).map(new Function<BillBoardEntry, String>() {
                    
                    @Override
                    public String apply(BillBoardEntry billBoardEntry) {
                        return String.format("{\"id\":%d,\"message\":\"%s\",\"readonly\":%b}", billBoardEntry.id, billBoardEntry.text, !billBoardEntry.owner_ip.equals(copy));
                    }
                }).collect(Collectors.joining(","))
        );
        stringBuilder.append("\t]\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public String readEntry(int idx, String caller_ip) {
        BillBoardEntry billBoardEntry = this.billboard[idx];
        return String.format("{\"id\":%d,\"message\":\"%s\",\"readonly\":%b}", billBoardEntry.id, billBoardEntry.text, !billBoardEntry.owner_ip.equals(caller_ip));
    }

}
