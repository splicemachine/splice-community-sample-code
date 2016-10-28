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

import javax.annotation.security.DeclareRoles;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.splicemachine.tutorials.jpa.entity.ModelVersion;


@Stateless
@Path("/service/modelversion")
@Produces(MediaType.APPLICATION_JSON)

public class ModelVersionBean extends BaseBean {
 
	@GET
	@Path("{modelId}")
	public List<ModelVersion> find(@PathParam("modelId") long modelId) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<ModelVersion> q = cb.createQuery(ModelVersion.class);
	    Root<ModelVersion> c = q.from(ModelVersion.class);
	    q.select(c);
	    ParameterExpression<Long> p = cb.parameter(Long.class);
	    q.where(cb.equal(c.get("modelId"), p));
	    return getEntityManager().createQuery(q).getResultList();
	    
	    
	}

   @GET
    @Path("{modelId}/{id}")
    public ModelVersion find(@PathParam("modelId") Long modelId, @PathParam("id") Long id) {
        return getEntityManager().find(ModelVersion.class, id);
    }
	
	@POST
	public void addModelVersion(ModelVersion ModelVersion) {
		getEntityManager().persist(ModelVersion);
	}
 
	public void deleteModelVersion(ModelVersion ModelVersion) {
		getEntityManager().remove(ModelVersion);
	}
 
	@DELETE
    @Path("delete/{id}")
	public void deleteModelVersionId(@PathParam("id") long id) {
		ModelVersion ModelVersion = getEntityManager().find(ModelVersion.class, id);
		deleteModelVersion(ModelVersion);
	}
 
	@GET
	public List<ModelVersion> getModelVersions() {
		CriteriaQuery<ModelVersion> cq = getEntityManager().getCriteriaBuilder().createQuery(ModelVersion.class);
		cq.select(cq.from(ModelVersion.class));
		return getEntityManager().createQuery(cq).getResultList();
	}
}