package com.splicemachine.tutorials.vti;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hadoop.hive.ql.io.orc.RecordReader;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

import com.splicemachine.access.api.FileInfo;
import com.splicemachine.db.iapi.error.StandardException;
import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataValueDescriptor;
import com.splicemachine.db.vti.VTICosting;
import com.splicemachine.db.vti.VTIEnvironment;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.load.ImportUtils;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.control.ControlDataSet;
import com.splicemachine.derby.stream.iapi.DataSet;
import com.splicemachine.derby.stream.iapi.DataSetProcessor;
import com.splicemachine.derby.stream.iapi.OperationContext;
import com.splicemachine.derby.vti.iapi.DatasetProvider;
import com.splicemachine.access.HConfiguration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by  
 */
public class SpliceORCFileVTI implements DatasetProvider, VTICosting {
    private String fileName;
    private String dateTimeFormat = "yyyy-mm-dd" ;
    private String timestampFormat ="yyyy-mm-dd hh:mm:ss.fffffffff";
    private int[] columnIndex;
    private OperationContext operationContext;
   
    private static final Logger LOG = Logger.getLogger(SpliceORCFileVTI.class);
    
    public SpliceORCFileVTI() {}

    public SpliceORCFileVTI(String fileName) {
        this.fileName = fileName;
    }

  
    public static DatasetProvider getSpliceORCFileVTI(String fileName) {
        return new SpliceORCFileVTI(fileName);
    }

    
    @Override
    public DataSet<LocatedRow> getDataSet(SpliceOperation op, DataSetProcessor dsp, ExecRow execRow) throws StandardException {
        operationContext = dsp.createOperationContext(op);
        ArrayList<LocatedRow> items = new ArrayList<LocatedRow>();
        
        try {
        	
        	FileSystem filesystem;
			
			 filesystem = FileSystem.get(HConfiguration.unwrapDelegate());	
			 Path toProcessPath = null;
			 /*
			 if (filesystem.isDirectory(new Path(fileName)))
			 {
				 RemoteIterator<LocatedFileStatus> fileList = filesystem.listFiles((new Path(fileName)), false);
				 while( fileList.hasNext()){
					 toProcessPath =fileList.next().getPath();
					 
					 LOG.error(" File to Process  :" + toProcessPath);
				 }
			 } 
			 */
				 
			 toProcessPath = new Path(fileName);			 
			 Reader reader = OrcFile.createReader(filesystem, toProcessPath);
			 ORCRecordIterator it = new ORCRecordIterator(reader,execRow);
	         op.registerCloseable(it);
	         return dsp.createDataSet(it);
	            
        			
         } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}finally {
            operationContext.popScope();
        }
        return new ControlDataSet<>(items);
    }

   


    @Override
    public double getEstimatedRowCount(VTIEnvironment vtiEnvironment) throws SQLException {
        
        return VTICosting.defaultEstimatedRowCount;
    }

    

    @Override
    public double getEstimatedCostPerInstantiation(VTIEnvironment vtiEnvironment) throws SQLException {
       
        return VTICosting.defaultEstimatedCost;
    }

    @Override
    public boolean supportsMultipleInstantiations(VTIEnvironment vtiEnvironment) throws SQLException {
        return false;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public OperationContext getOperationContext() {
        return operationContext;
    }

    public String getFileName() {
        return fileName;
    }
}
