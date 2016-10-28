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

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.splicemachine.tutorials.jpa.entity.Model;


@Stateless
@Path("/service/model")
@Produces(MediaType.APPLICATION_JSON)
public class ModelBean extends BaseBean{

 
	@GET
	@Path("{id}")
	public Model find(@PathParam("id") long id) {
		return getEntityManager().find(Model.class, id);

	}
 
	@POST
	public void addModel(Model Model) {
		getEntityManager().persist(Model);
	}
 
	public void deleteModel(Model Model) {
		getEntityManager().remove(Model);
	}
 
	@DELETE
	@Path("delete/{id}")
	public void deleteModelId(@PathParam("id") long id) {
		Model Model = getEntityManager().find(Model.class, id);
		deleteModel(Model);
	}
 
	@GET
	public List<Model> getModels() {
	    CriteriaQuery<Model> cq = getEntityManager().getCriteriaBuilder().createQuery(Model.class);
	    cq.select(cq.from(Model.class));
	    return getEntityManager().createQuery(cq).getResultList();
	}
}