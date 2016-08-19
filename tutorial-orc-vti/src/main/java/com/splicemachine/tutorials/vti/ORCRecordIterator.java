package com.splicemachine.tutorials.vti;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hadoop.hive.ql.io.orc.RecordReader;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;

public class ORCRecordIterator implements Iterable<LocatedRow>, Iterator<LocatedRow>, Closeable {
	 private ExecRow execRow;
	 private Reader reader;
	 private  StructObjectInspector inspector ;
	 RecordReader records ;
	 Object row = null;
	 
	 
	 public ORCRecordIterator (Reader reader, ExecRow execRow){
		 this.reader = reader;
		 try {
			  this.inspector = (StructObjectInspector)reader.getObjectInspector();
			 records = this.reader.rows();
		 }
		 catch (Exception e) {
         try {
             if (records != null)
            	 records.close();
         } catch (Exception cE) {
             throw new RuntimeException(cE);
         }
         throw new RuntimeException(e);
     }
     this.execRow = execRow;
	 }
	   
	 @Override
	    public Iterator<LocatedRow> iterator() {
	        return this;
	    }

	    @Override
	    public boolean hasNext() {
	        try {
	            return records.hasNext();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    @Override
	    public LocatedRow next() {
	        try {
	            ExecRow returnRow = execRow.getClone();
	            row = records.next(row);
                List value_lst = inspector.getStructFieldsDataAsList(row);
                String value = null;
	            for (int i = 1; i <= execRow.nColumns(); i++) {
	            	Object field  = value_lst.get(i-1);
	            	value = null;
                   if(field != null)
                   		value =field.toString();
                   //Note: Date is in same format as Splice Default,
                   //For timestamp format yyyy-mm-dd hh:mm:ss[.f...]., Check if Timestamp.valueOf(value) is required
	                returnRow.getColumn(i).setValue(value);
	            }
	            return new LocatedRow(returnRow);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    @Override
	    public void remove() {
	        throw new RuntimeException("Not Implemented");
	    }

	    @Override
	    public void close() throws IOException {
	        if (records != null) {
	           
	            	records.close();
	           
	        }
	    }

}
