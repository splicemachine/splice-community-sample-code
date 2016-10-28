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

package com.splicemachine.tutorials.jpa.beans;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.ws.rs.core.Application;

import com.splicemachine.tutorials.jpa.EMF;



/**
 * Base Class for Data Access
 *
 * @author Erin Driggers
 */

public abstract class BaseBean extends Application {
    
    @PersistenceContext(unitName="splicedb", type=PersistenceContextType.TRANSACTION)
    EntityManager entityManager;

    public String table = null;

    public EntityManager getEntityManager() {
        if(entityManager == null) {
            entityManager = EMF.createEntityManager();
        }
        return entityManager;
    }
    
    public void setTable(String sTable) {
        this.table = sTable;
    }
    
    public Integer count() {
        Query query = getEntityManager().createQuery("SELECT COUNT(p.id) FROM " + table + " p ");
        return ((Long) query.getSingleResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Object> find(int startPosition, int maxResults, String sortFields, String sortDirections) {
        Query query =
                getEntityManager().createQuery("SELECT p FROM " + table + " p ORDER BY p." + sortFields + " " + sortDirections);
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> find(int startPosition, String sortFields, String sortDirections) {
        Query query =
                getEntityManager().createQuery("SELECT p FROM " + table + " p ORDER BY p." + sortFields + " " + sortDirections);
        query.setFirstResult(startPosition);
        return query.getResultList();
    }
    

    public Integer countSubset(String filterColumnName, Long filterId) {
        Query query = getEntityManager().createQuery("SELECT COUNT(p.id) FROM " + table + " p where p." + filterColumnName + " = " + filterId );
        return ((Long) query.getSingleResult()).intValue();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> findSubset(int startPosition, int maxResults, String sortFields, String sortDirections, String filterColumnName, Long filterId) {
        Query query =
                getEntityManager().createQuery("SELECT p FROM " + table + " p where p." + filterColumnName + " = " + filterId +" ORDER BY p." + sortFields + " " + sortDirections);
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        
        List<Object> temp = query.getResultList();

        return temp;
    }
   
}
