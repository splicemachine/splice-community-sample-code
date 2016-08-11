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

package com.splicemachine.tutorials.tsdbanalytics.dataobjects;

import java.io.Serializable;
/**
 * PublisherGeoKey Object definition. This object is used as key for mapping impression Log
 * in prepartion for aggregation
 * 
 * @author Jyotsna Ramineni
 *
 */
public class PublisherGeoKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895676943619006281L;
	
	
	private String publisher;
	private String geo;
	
	public PublisherGeoKey(String publisher, String geo)  {
		super();
		this.publisher = publisher;
		this.geo = geo;
	}
	
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		// object must be Test at this point
		PublisherGeoKey test = (PublisherGeoKey)obj;
		return ((publisher == test.publisher || (publisher != null && publisher.equals(test.publisher))) &&
		(geo == test.geo || (geo != null && geo.equals(test.geo))));
	}

	public int hashCode()
	{
		int hash = 7;
		hash = 31 * hash + (null == publisher ? 0 : publisher.hashCode());
		hash = 31 * hash + (null == geo ? 0 : geo.hashCode());
		return hash;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	
	
}
