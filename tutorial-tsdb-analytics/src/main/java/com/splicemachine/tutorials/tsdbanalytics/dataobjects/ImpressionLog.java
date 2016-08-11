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
 * ImpressionLog Object definition. This is the object read from the stream
 * 
 * @author Jyotsna Ramineni
 *
 */
public class ImpressionLog  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8256414314586579087L;
	
	private Long timestamp;
	private String publisher;
	private String advertiser;
	private String website;
	private String geo;
	private Double bid;
	private String cookie;
	
	
	
	public ImpressionLog(Long timestamp, String publisher, String advertiser,
			String website, String geo, Double bid, String cookie)  {
		super();
		this.timestamp = timestamp;
		this.publisher = publisher;
		this.advertiser = advertiser;
		this.website = website;
		this.geo = geo;
		this.bid = bid;
		this.cookie = cookie;
	}
	
	

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public Double getBid() {
		return bid;
	}

	public void setBid(Double bid) {
		this.bid = bid;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	
	

}
