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
package com.splicemachine.tutorials.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@Entity
@Table(name="MODEL_VERSION_PARAMETERS", schema="ML")
public class ModelVersionParameter implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 3609713027316681188L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "MODEL_ID")
    private int modelId;

    @Column(name = "MODEL_VERSION_ID")
    private int modelVersionId;

    @Column(name = "FIELD")
    private String field;

    @Column(name = "FIELD_LABEL")
    private String label;

    @Column(name = "FIELD_DATA_TYPE")
    private String fieldDataType;
        
    @Column(name = "CREATE_DT")
    private Date createDate;
    
    @Transient
    private String formattedCreateDate;
    
    @Column(name = "CREATE_USER")
    private String createUser;
    
    @Column(name = "UPDATE_DT")
    private Date updateDate;
    
    @Transient
    private String formattedUpdateDate;

    
    @Column(name = "UPDATE_USER")
    private String updateUser;

    public ModelVersionParameter() {
    }
    
    @PrePersist
    void preInsert() {
        if(createDate == null) {
            createDate = new Date();
            updateUser = createUser;
        }
        updateDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(int modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    
    public String getFormattedCreateDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.getCreateDate());
    }

    public void setFormattedCreateDate(String formattedCreateDate) {
        this.formattedCreateDate = formattedCreateDate;
    }

    public String getFormattedUpdateDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.getUpdateDate());
    }

    public void setFormattedUpdateDate(String formattedUpdateDate) {
        this.formattedUpdateDate = formattedUpdateDate;
    }    
}