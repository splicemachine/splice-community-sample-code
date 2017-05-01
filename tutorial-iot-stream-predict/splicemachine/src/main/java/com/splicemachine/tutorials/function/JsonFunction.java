/*
 * Copyright 2012 - 2017 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.splicemachine.tutorials.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.services.io.ArrayUtil;
import com.splicemachine.db.iapi.services.io.StoredFormatIds;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataValueDescriptor;
import com.splicemachine.db.iapi.types.DateTimeDataValue;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.function.SpliceFlatMapFunction;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.derby.stream.output.WriteReadUtils;
import com.splicemachine.derby.utils.SpliceDateFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.io.*;

/**
 *
 * Function for parsing CSV files that are splittable by Hadoop.  The tokenizer swaps in and out
 * the line to be tokenized.
 *
 * Special attention should be paid to permissive execution of the OperationContext.  This occurs
 * during imports so that failures are <i>handled</i>.
 * @param <I>
 *
 *
 */
public class JsonFunction<I> extends SpliceFlatMapFunction<SpliceOperation, String, LocatedRow> implements Serializable {
    
    ExecRow execRow;
    
    JsonParser parser = new JsonParser();
    
    private transient Calendar calendar;
    /*
    private String timeFormat;
    private String dateTimeFormat;
    private String timestampFormat;
    */
    public JsonFunction() {
        super();
    }
    public JsonFunction(ExecRow execRow, OperationContext operationContext) {
        super(operationContext);
        this.execRow = execRow;
    }
    

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        try {
            ArrayUtil.writeIntArray(out, WriteReadUtils.getExecRowTypeFormatIds(execRow));
        } catch (StandardException se) {
            throw new IOException(se);
        }
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        execRow =WriteReadUtils.getExecRowFromTypeFormatIds(ArrayUtil.readIntArray(in));

    }


    /**
     *
     * Call Method for parsing the string into either a singleton List with a LocatedRow or
     * an empty list.
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public Iterator<LocatedRow> call(final String jsonString) throws Exception {

        Iterator<LocatedRow> itr = Collections.<LocatedRow>emptyList().iterator();
        if(jsonString == null) {
            return itr;
        }
        
        JsonElement elem = parser.parse(jsonString);
        JsonObject root = elem.getAsJsonObject();   
        JsonArray dataArray = root == null ? null : root.getAsJsonArray("data");
        int numRcds = dataArray == null ? 0 : dataArray.size();
        
        if(numRcds == 0) {
            return itr;
        }
        
        List<LocatedRow> rows = new ArrayList<LocatedRow>();
        //Loop through each of the entries in the array
        for(int i=0; i<numRcds; i++) {
            
            //The definition of the row to be returned
            ExecRow returnRow = execRow.getClone();
            
            //Reset the column counter
            int column = 1;
                        
            JsonObject json = dataArray.get(i).getAsJsonObject();
            
            //Retrieve the properties for each entry
            Set<Entry<String, JsonElement>> keys = json.entrySet();
            for(Map.Entry<String,JsonElement> entry : keys){
                String key = entry.getKey();
                JsonElement valueElem = json.get(key);
                
                DataValueDescriptor dvd = returnRow.getColumn(column);
                int type = dvd.getTypeFormatId();
                
                String value = valueElem.getAsJsonPrimitive().getAsString();

                
                switch(type){
                    case StoredFormatIds.SQL_TIME_ID:
                        if(calendar==null)
                            calendar = new GregorianCalendar();
                        //if (timeFormat == null || value==null){
                            ((DateTimeDataValue)dvd).setValue(value,calendar);
                       // }else
                       //     dvd.setValue(SpliceDateFunctions.TO_TIME(value, timeFormat),calendar);
                        break;
                    case StoredFormatIds.SQL_DATE_ID:
                        if(calendar==null)
                            calendar = new GregorianCalendar();
                        //if (dateTimeFormat == null || value == null)
                            ((DateTimeDataValue)dvd).setValue(value,calendar);
                        //else
                        //    dvd.setValue(SpliceDateFunctions.TO_DATE(value, dateTimeFormat),calendar);
                        break;
                    case StoredFormatIds.SQL_TIMESTAMP_ID:
                        if(calendar==null)
                            calendar = new GregorianCalendar();
                        //if (timestampFormat == null || value==null)
                            ((DateTimeDataValue)dvd).setValue(value,calendar);
                        //else
                        //    dvd.setValue(SpliceDateFunctions.TO_TIMESTAMP(value, timestampFormat),calendar);
                        break;
                    default:
                        dvd.setValue(value);

                }
                column++;
            }
            
            rows.add(new LocatedRow(returnRow));
        }
        return rows.iterator();
    }

    
    /**
     * 
     * { "data" : [{
     * "fullName"  : "$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float",
     * "timeStamp" : "1477307252000",
     * "value"     : "-0.06279052", 
     * "quality"   : "128"},
     * 
     * {"fullName"  : "$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float(2)","timeStamp" : "1477307252000","value"     : "31.886976","quality"   : "128"}]}
     * 
     * 
     * 
     */
    
}