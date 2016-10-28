/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
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
package com.splicemachine.tutorials.tensorflow;

import java.nio.FloatBuffer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.bytedeco.javacpp.tensorflow;
import org.bytedeco.javacpp.helper.tensorflow.StringArray;
import org.bytedeco.javacpp.tensorflow.*;



public class ApplyTensorModel {


    /**
     * Applies the passed in parameters to the specified model
     * 
     * @param modelName - The name of the model 
     * @param modelParmNameValues - a command delimited list of name/pairs separated by equals sign
     * @param resultSets - The resultset that is returned to the caller
     * @throws StandardException
     */
    public static void applyTensorModel(String modelName, String modelParmNameValues, ResultSet[] resultSets) throws Exception {
        try {
            Connection conn = DriverManager.getConnection("jdbc:default:connection");
            
            //Create a hashmap that contains the name value pairs
            HashMap<String, String> nameValuePairs = new HashMap<String,String>();
            String[] nameValues = modelParmNameValues.split(",");
            for(String nameValue : nameValues) {
                String[] vals = nameValue.split("=");
                nameValuePairs.put(vals[0], vals[1]);
            }

            PreparedStatement ps = conn.prepareStatement("SELECT TOP 1 V.ID, V.MODEL, V.MODEL_COEFFICIENTS "
                    + "FROM ML.MODEL M, ML.MODEL_VERSION V "
                    + "WHERE M.DISPLAY_NAME = ? "
                    + "AND M.STATUS = ? "
                    + "AND V.STATUS = ? "
                    + "AND M.ID = V.MODEL_ID "
                    + "ORDER BY V.EFFECTIVE_DATE DESC "
                    );
            ps.setString(1, modelName);
            ps.setString(2, "A");
            ps.setString(3, "A");
            ResultSet rs = ps.executeQuery();
            
            Clob model = null;
            Clob modelCoefficeints = null;
            int modelVersionId = -1;

            // Retrieve the data
            if (rs.next()) {
                modelVersionId = rs.getInt(1);
                model = rs.getClob(2);
                modelCoefficeints = rs.getClob(3);
            }
            
            if(rs != null) {
                try {
                    rs.close();
                } catch(Exception e) {
                    //ignore the exception
                }
            }
            
            // A record was found in the database
            if (modelVersionId > -1) {

                //Retrieve the columns / data types
                
                ps = conn.prepareStatement("SELECT FIELD, FIELD_DATA_TYPE "
                        + "FROM ML.MODEL M, ML.MODEL_VERSION V "
                        + "WHERE MODEL_VERSION_ID = ? "
                        );
                ps.setInt(modelVersionId, -1);
                rs = ps.executeQuery();
                
                while(rs.next()) {
                    
                }
                
                //Read the proto file to create a Graph definition
                final Session session = new Session(new SessionOptions());
                GraphDef def = new GraphDef();
                tensorflow.ReadBinaryProto(Env.Default(), 
                                           "somedir/trained_model.proto", def);
                Status s = session.Create(def);
                if (!s.ok()) {
                    throw new RuntimeException(s.error_message().getString());
                }
                
                // Restore the weights and bias
                Tensor fn = new Tensor(tensorflow.DT_STRING, new TensorShape(1));
                StringArray a = fn.createStringArray();
                a.position(0).put("somedir/trained_model.sd"); 
                s = session.Run(new StringTensorPairVector(new String[]{"save/Const:0"}, new Tensor[]{fn}), new StringVector(), new StringVector("save/restore_all"), new TensorVector());
                if (!s.ok()) {
                   throw new RuntimeException(s.error_message().getString());
                }
                
                //Make the prediction
                Tensor inputs = new Tensor(
                         tensorflow.DT_FLOAT, new TensorShape(2,5));
                FloatBuffer x = inputs.createBuffer();
                x.put(new float[]{-6.0f,22.0f,383.0f,27.781754111198122f,-6.5f});
                x.put(new float[]{66.0f,22.0f,2422.0f,45.72160947712418f,0.4f});
                Tensor keepall = new Tensor(
                        tensorflow.DT_FLOAT, new TensorShape(2,1));
                ((FloatBuffer)keepall.createBuffer()).put(new float[]{1f, 1f});
                TensorVector outputs = new TensorVector();
                // to predict each time, pass in values for placeholders
                outputs.resize(0);
                s = session.Run(new StringTensorPairVector(new String[] {"Placeholder", "Placeholder_2"}, new Tensor[] {inputs, keepall}),
                 new StringVector("Sigmoid"), new StringVector(), outputs);
                if (!s.ok()) {
                   throw new RuntimeException(s.error_message().getString());
                }
                // this is how you get back the predicted value from outputs
                FloatBuffer output = outputs.get(0).createBuffer();
                for (int k=0; k < output.limit(); ++k){
                   System.out.println("prediction=" + output.get(k));
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    

}
