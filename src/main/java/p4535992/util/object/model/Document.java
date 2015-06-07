/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package p4535992.util.object.model;
/**
 *
 * @author Marco
 */

import javax.persistence.*;

@Entity
@Table(name = "document")
public class Document {
    
    public Document(){}
    
    @Id @GeneratedValue
    @Column(name = "doc_id")
    private String doc_id;
    @Column(name = "url")
    private String url;
    @Column(name = "city")
    private String city;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "crawling_date")
    private String crawling_date;
    @Column(name = "crawling_time")
    private String crawling_time;

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCrawling_date() {
        return crawling_date;
    }

    public void setCrawling_date(String crawling_date) {
        this.crawling_date = crawling_date;
    }

    public String getCrawling_time() {
        return crawling_time;
    }

    public void setCrawling_time(String crawling_time) {
        this.crawling_time = crawling_time;
    }

    @Override
    public String toString() {
        String s = "Document{" + "doc_id=" + doc_id + ", url=" + url + ", city=" + city + ", latitude=" + latitude + ", longitude=" + longitude + ", crawling_date=" + crawling_date + ", crawling_time=" + crawling_time + '}';
        return s;
    }
    
    
}
