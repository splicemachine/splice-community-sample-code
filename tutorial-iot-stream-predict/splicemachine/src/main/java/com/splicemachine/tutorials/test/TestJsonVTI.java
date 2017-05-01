package com.splicemachine.tutorials.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class TestJsonVTI {
    
    /**
     * Pass in the filename
     * @param args
     */
    public static void main(String[] args) {
        new TestJsonVTI(args[0], args[1]);
    }
    
    public TestJsonVTI(String filename, String jdbcUrl) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            //String contents = new String(Files.readAllBytes(Paths.get(filename)));
            
            List records = new ArrayList<String>();
            records.add("{ \"data\" : [{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float\",\"timeStamp\" : \"1477307252000\",\"value\"     : \"-0.06279052\", \"quality\"   : \"131\"},{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float(2)\",\"timeStamp\" : \"1477307252000\",\"value\"     : \"31.886976\",\"quality\"   : \"121\"}]}");
            records.add("{ \"data\" : [{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float\",\"timeStamp\" :\"1477307253000\",\"value\"     : \"-0.12533323\",\"quality\"   : \"132\"},{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float(2)\",\"timeStamp\" : \"1477307253000\",\"value\"     :\"31.7744\",\"quality\"   : \"122\"}] }");
            records.add("{ \"data\" : [{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float\",\"timeStamp\" : \"1477307254000\",\"value\"     : \"-0.18738131\",\"quality\"   : \"133\"},{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float(2)\",\"timeStamp\" : \"1477307254000\",\"value\"     : \"31.662714\",\"quality\"   : \"123\"}] }");
            records.add("{ \"data\" : [{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float\",\"timeStamp\" : \"1477307255000\",\"value\"     : \"-0.24868989\",\"quality\"   : \"134\"},{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float(2)\",\"timeStamp\" : \"1477307255000\",\"value\"     : \"31.552359\",\"quality\"   : \"124\"}] }");
            records.add("{ \"data\" : [{\"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float\",\"timeStamp\" : \"1477308362000\",\"value\"     : \"-0.637424\",  \"quality\"   : \"135\"  } ,{  \"fullName\"  : \"$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float(2)\",    \"timeStamp\" : \"1477308362000\",    \"value\"     : \"30.852636\",    \"quality\"   : \"125\"  }] }");
            
            
            con = DriverManager.getConnection(jdbcUrl); 
            String vti = 
                    " SELECT * from new com.splicemachine.tutorials.vti.ArrayListOfJsonVTI(?) " +
                    //" --splice-properties useSpark=true \n" +
                    " AS importVTI (FULLNAME VARCHAR(100), TIMESTAMP BIGINT, VALUE DOUBLE, QUALITY INT)";
            ps = con.prepareStatement(vti);
            byte[] bytes = serialize(records);
            ps.setBytes(1, bytes);
            ResultSet rs = ps.executeQuery();   
            while(rs.next()) {
                System.out.print("fullName=" + rs.getString(1));
                System.out.print(" timestamp=" + rs.getLong(2));
                System.out.print(" value=" + rs.getDouble(3));
                System.out.println(" quality=" + rs.getInt(4));
            }

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if(ps != null) {try {ps.close();} catch(Exception e){}}
            if(con != null) { try {con.close();} catch (Exception e) {}}
        } 
        /*
        * "fullName"  : "$MODEL/TimerEngine_In/CandyDataBase_In/KafkaBridge/Float",
        * "timeStamp" : "1477307252000",
        * "value"     : "-0.06279052", 
        * "quality"   : "128"},
*/
        
    }
    
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = null;
        ObjectOutputStream o = null;
        byte[] bytes = null;
        try {
            b = new ByteArrayOutputStream();
            o = new ObjectOutputStream(b);
            o.writeObject(obj);
            bytes = b.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(o != null) try { o.close(); } catch (Exception e){};
            if(b != null) try { b.close(); } catch (Exception e){};
        }
        return bytes;

    }
    
}