/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.p4535992.util.object.model;

import javax.persistence.*;
import java.net.URL;

/**
 * Classe che costruisce l'ogeeto GeoDocument  per essere utilizzato con una specifica ontologia
 *
 * @author Marco
 */

@Entity
@Table(name = "infodocument_ann")
public class InfoDocument {
    @Id @GeneratedValue
    @Column(name = "doc_id")
    private Integer doc_id;
    @Column(name = "url")
    private URL url;
    @Column(name = "regione")
    private String regione;
    @Column(name = "provincia")
    private String provincia;
    @Column(name = "city")
    private String city;
    @Column(name = "indirizzo")
    private String indirizzo;
    @Column(name = "iva")
    private String iva;
    @Column(name = "email")
    private String email;
    @Column(name = "telefono")
    private String telefono; 
    @Column(name = "edificio")
    private String edificio;
    @Column(name = "nazione")
    private String nazione;
    @Column(name = "latitude")
    private Double lat;
    @Column(name = "longitude")
    private Double lng;
    
    /////////////////////////
    @Column(name = "description")
    private String description;
    @Column(name = "indirizzoNoCAP")
    private String indirizzoNoCAP;
    @Column(name = "postalCode")
    private String postalCode;
    @Column(name = "fax")
    private String fax;
    /////////////////////////////////
    @Column(name = "indirizzoHasNumber")
    private String indirizzoHasNumber;
    //////////////////////////////////////////
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "name_location")
    private String name_location;
   
    
    public InfoDocument (){}
   
    public InfoDocument(Integer doc_id, URL url, String city, Double lat, Double lng) {
        this.doc_id = doc_id;
        this.url = url;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
    }
    
    public InfoDocument(URL url, String regione, String provincia, String city,
			String indirizzo,String iva, String email, String telefono,String fax,
			String edificio,Double lat, Double lng,String nazione,String description,
                        String indirizzoNoCAP, String postalCode,String indirizzoHasNumber,String identifier,
                        String name_location) {
		this.url = url;
		this.regione = regione;
                this.provincia = provincia;
		this.city = city;		
		this.indirizzo = indirizzo;
                this.iva = iva;
		this.email = email;
		this.telefono = telefono;
                this.fax = fax;
		this.edificio = edificio;
		this.lat = lat;
		this.lng = lng;
                this.nazione = nazione;
                this.description = description;
                this.indirizzoNoCAP = indirizzoNoCAP;
                this.postalCode = postalCode;
                this.indirizzoHasNumber = indirizzoHasNumber;
                this.identifier = identifier;
                this.name_location = name_location;
                                               
	}

    public String getIndirizzoNoCAP() {
        return indirizzoNoCAP;
    }

    public void setIndirizzoNoCAP(String indirizzoNoCAP) {
        this.indirizzoNoCAP = indirizzoNoCAP;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }   

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer doc_id) {
        this.doc_id = doc_id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    
    public String getIndirizzoHasNumber() {
        return indirizzoHasNumber;
    }

    public void setIndirizzoHasNumber(String indirizzoHasNumber) {
        this.indirizzoHasNumber = indirizzoHasNumber;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName_location() {
        return name_location;
    }

    public void setName_location(String name_location) {
        this.name_location = name_location;
    }

    @Override
    public String toString() {
         String s =
         "******************************************************************************************************" + System.getProperties().get("line.separator") + 
         "[InfoDocument]{" 
                + "doc_id = " + doc_id 
                + ", url = " + url 
                + ", regione = " +  regione 
                + ", provincia = " + provincia 
                + ", city = " + city 
                + ", indirizzo = " + indirizzo 
                + ", iva = " + iva 
                + ", email = " + email 
                + ", telefono = " + telefono 
                + ", edificio = " + edificio 
                + ", nazione = " + nazione 
                + ", lat = " + lat 
                + ", lng = " + lng 
                + ", description = " + description 
                + ", indirizzoNoCAP = " + indirizzoNoCAP 
                + ", postalCode=" + postalCode 
                + ", fax = " + fax 
                + ", indirizzoHasNumber = " + indirizzoHasNumber          
                + ", identifier=" + identifier 
                + ", name_location=" + name_location 
                + "}" 
                + System.getProperties().get("line.separator") 
                + "******************************************************************************************************"+ System.getProperties().get("line.separator");
        return s;
       
    }
    
    
}
