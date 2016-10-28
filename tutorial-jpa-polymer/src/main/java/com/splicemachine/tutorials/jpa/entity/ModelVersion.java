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
@Table(name="MODEL_VERSION", schema="ML")
public class ModelVersion implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -8645120195779792115L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "MODEL_ID")
    private int modelId;

    @Column(name = "VERSION")
    private int version;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "MODEL_COEFFICIENTS")
    private String modelCoefficients;

    @Column(name = "MODEL_PLATFORM")
    private String modelPlatform;

    @Column(name = "MODEL_CREATE_DT")
    private Date modelCreateDate;

    @Transient
    private String formattedModelCreateDate;

    
    @Column(name = "EFFECTIVE_DATE")
    private Date effectiveDate;
    
    @Transient
    private String formattedEffectiveDate;
    
    @Column(name = "STATUS")
    private String status;
    
    @Transient
    private String statusDesc;
    
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

    public ModelVersion() {
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelCoefficients() {
        return modelCoefficients;
    }

    public void setModelCoefficients(String modelCoefficients) {
        this.modelCoefficients = modelCoefficients;
    }

    public String getModelPlatform() {
        return modelPlatform;
    }

    public void setModelPlatform(String modelPlatform) {
        this.modelPlatform = modelPlatform;
    }

    public Date getModelCreateDate() {
        return modelCreateDate;
    }

    public void setModelCreateDate(Date modelCreateDate) {
        this.modelCreateDate = modelCreateDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStatusDesc() {
        if("A".equals(this.getStatus())) {
            return "Active";
        } else if ("I".equals(this.getStatus())) {
            return "Inactive";
        }
        return "";
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getFormattedUpdateDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.getUpdateDate());
    }

    public void setFormattedUpdateDate(String formattedUpdateDate) {
        this.formattedUpdateDate = formattedUpdateDate;
    }    
    
    public String getFormattedEffectiveDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.getEffectiveDate());
    }
    
    public String getFormattedModelCreateDate() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.getModelCreateDate());
    }
    
}