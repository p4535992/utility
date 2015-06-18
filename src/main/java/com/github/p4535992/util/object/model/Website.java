/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.p4535992.util.object.model;

/**
 *
 * @author Marco
 */

import javax.persistence.*;

@Entity
@Table(name = "website")
public class Website {
    @Id @GeneratedValue
    @Column(name = "id")
    private String id;   
    @Column(name = "crawling_date")
    private String crawling_date;    
    @Column(name = "date_of_booking")
    private String date_of_booking;    
    @Column(name = "url")
    private String url; 
    @Column(name = "processing_status")
    private String processing_status;
    @Column(name = "city")
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public Website(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCrawling_date() {
        return crawling_date;
    }

    public void setCrawling_date(String crawling_date) {
        this.crawling_date = crawling_date;
    }
   
    public String getDate_of_booking() {
        return date_of_booking;
    }

    public void setDate_of_booking(String date_of_booking) {
        this.date_of_booking = date_of_booking;
    }   

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProcessing_status() {
        return processing_status;
    }

    public void setProcessing_status(String processing_status) {
        this.processing_status = processing_status;
    }

    @Override
    public String toString() {
        String s = 
                "******************************************************************************************************" 
                + System.getProperties().get("line.separator") 
                + "Website{" + "id=" + id + ", crawling_date=" + crawling_date + ", date_of_booking=" + date_of_booking + ", url=" + url + ", processing_status=" + processing_status + '}'
                + System.getProperties().get("line.separator") 
                + "******************************************************************************************************" 
                + System.getProperties().get("line.separator");
        return s;
    }  
    
}
