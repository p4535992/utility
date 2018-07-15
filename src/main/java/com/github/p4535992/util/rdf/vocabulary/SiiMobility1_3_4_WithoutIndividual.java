package com.github.p4535992.util.rdf.vocabulary;

/* CVS $Id: $ */

import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
 
/**
 * Vocabulary definitions from SiiMobility1.3.4.owl 
 * @author Auto-generated by schemagen on 16 lug 2014 12:24 
 */
public class SiiMobility1_3_4_WithoutIndividual {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static OntModel m_model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.disit.dinfo.unifi.it/SiiMobility#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS 
     *  @return uri
     */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final ObjectProperty accessTo = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#accessTo" );
    
    public static final ObjectProperty allows = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#allows" );
    
    public static final ObjectProperty approvedBy = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#approvedBy" );
    
    public static final ObjectProperty atThe = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#atThe" );
    
    public static final ObjectProperty beginsAt = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#beginsAt" );
    
    public static final ObjectProperty belongTo = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#belongTo" );
    
    public static final ObjectProperty coincideWith = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#coincideWith" );
    
    public static final ObjectProperty concern = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#concern" );
    
    public static final ObjectProperty concerning = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#concerning" );
    
    public static final ObjectProperty contains = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#contains" );
    
    public static final ObjectProperty ending = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#ending" );
    
    public static final ObjectProperty ends = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#ends" );
    
    public static final ObjectProperty endsAt = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#endsAt" );
    
    public static final ObjectProperty finishesAt = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#finishesAt" );
    
    public static final ObjectProperty forming = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#forming" );
    
    public static final ObjectProperty forms = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#forms" );
    
    public static final ObjectProperty has = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#has" );
    
    public static final ObjectProperty hasAccess = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasAccess" );
    
    public static final ObjectProperty hasApproved = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasApproved" );
    
    public static final ObjectProperty hasExpectedTime = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasExpectedTime" );
    
    public static final ObjectProperty hasExternalAccess = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasExternalAccess" );
    
    public static final ObjectProperty hasFirstElem = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasFirstElem" );
    
    public static final ObjectProperty hasFirstSection = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasFirstSection" );
    
    public static final ObjectProperty hasFirstStop = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasFirstStop" );
    
    public static final ObjectProperty hasForecast = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasForecast" );
    
    public static final ObjectProperty hasInternalAccess = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasInternalAccess" );
    
    public static final ObjectProperty hasLastStopTime = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasLastStopTime" );
    
    public static final ObjectProperty hasMunicipality = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasMunicipality" );
    
    public static final ObjectProperty hasProduced = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasProduced" );
    
    public static final ObjectProperty hasProvince = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasProvince" );
    
    public static final ObjectProperty hasPublicOffice = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasPublicOffice" );
    
    public static final ObjectProperty hasRecord = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasRecord" );
    
    public static final ObjectProperty hasRule = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasRule" );
    
    public static final ObjectProperty hasSecondElem = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasSecondElem" );
    
    public static final ObjectProperty hasSection = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasSection" );
    
    public static final ObjectProperty hasSegment = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasSegment" );
    
    public static final ObjectProperty hasStatistic = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasStatistic" );
    
    public static final ObjectProperty hasStreetNumber = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasStreetNumber" );
    
    public static final ObjectProperty hasSurvey = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasSurvey" );
    
    public static final ObjectProperty hasThirdElem = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hasThirdElem" );
    
    public static final ObjectProperty inMunicipalityOf = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#inMunicipalityOf" );
    
    public static final ObjectProperty include = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#include" );
    
    public static final ObjectProperty installedOn = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#installedOn" );
    
    public static final ObjectProperty instantAVM = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#instantAVM" );
    
    public static final ObjectProperty instantForecast = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#instantForecast" );
    
    public static final ObjectProperty instantObserv = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#instantObserv" );
    
    public static final ObjectProperty instantParking = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#instantParking" );
    
    public static final ObjectProperty instantWReport = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#instantWReport" );
    
    public static final ObjectProperty isComposed = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isComposed" );
    
    public static final ObjectProperty isComposedOf = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isComposedOf" );
    
    public static final ObjectProperty isDescribed = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isDescribed" );
    
    public static final ObjectProperty isIn = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isIn" );
    
    public static final ObjectProperty isMadeUp = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isMadeUp" );
    
    public static final ObjectProperty isObservatedBy = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isObservatedBy" );
    
    public static final ObjectProperty isPartOf = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isPartOf" );
    
    public static final ObjectProperty isPartOfLot = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isPartOfLot" );
    
    public static final ObjectProperty isPartOfProvince = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isPartOfProvince" );
    
    public static final ObjectProperty isPartOfRegion = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#isPartOfRegion" );
    
    public static final ObjectProperty lastStop = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#lastStop" );
    
    public static final ObjectProperty managingAuthority = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#managingAuthority" );
    
    public static final ObjectProperty measuredBy = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#measuredBy" );
    
    public static final ObjectProperty measuredTime = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#measuredTime" );
    
    public static final ObjectProperty observationTime = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#observationTime" );
    
    public static final ObjectProperty observe = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#observe" );
    
    public static final ObjectProperty on = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#on" );
    
    public static final ObjectProperty ownerAuthority = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#ownerAuthority" );
    
    public static final ObjectProperty placedIn = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#placedIn" );
    
    public static final ObjectProperty refersTo = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#refersTo" );
    
    public static final ObjectProperty relatedTo = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#relatedTo" );
    
    public static final ObjectProperty scheduledOn = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#scheduledOn" );
    
    public static final ObjectProperty serviceCategory = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#serviceCategory" );
    
    public static final ObjectProperty situated = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#situated" );
    
    public static final ObjectProperty starting = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#starting" );
    
    public static final ObjectProperty starts = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#starts" );
    
    public static final ObjectProperty startsAt = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#startsAt" );
    
    public static final ObjectProperty updateTime = m_model.createObjectProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#updateTime" );
    
    public static final DatatypeProperty adRoadName = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#adRoadName" );
    
    public static final DatatypeProperty adminClass = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#adminClass" );
    
    public static final DatatypeProperty atecoCode = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#atecoCode" );
    
    public static final DatatypeProperty averageDistance = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#averageDistance" );
    
    public static final DatatypeProperty averageSpeed = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#averageSpeed" );
    
    /** <p>Tempo medio tra 2 transiti</p> */
    public static final DatatypeProperty averageTime = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#averageTime" );
    
    public static final DatatypeProperty capacity = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#capacity" );
    
    public static final DatatypeProperty carParkStatus = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#carParkStatus" );
    
    public static final DatatypeProperty classCode = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#classCode" );
    
    public static final DatatypeProperty composition = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#composition" );
    
    public static final DatatypeProperty concentration = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#concentration" );
    
    public static final DatatypeProperty day = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#day" );
    
    /** <p>direzione percorso</p> */
    public static final DatatypeProperty direction = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#direction" );
    
    public static final DatatypeProperty distance = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#distance" );
    
    public static final DatatypeProperty elemLocation = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#elemLocation" );
    
    public static final DatatypeProperty elementClass = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#elementClass" );
    
    public static final DatatypeProperty elementType = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#elementType" );
    
    public static final DatatypeProperty email = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#email" );
    
    public static final DatatypeProperty entryType = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#entryType" );
    
    public static final DatatypeProperty exitRate = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#exitRate" );
    
    /** <p>orario in cui e' previsto l'arrivo del bus a quella fermata DA ELIMINARE QUANDO 
     *  INSTANT FUNZIONERA' ALLA PERFEZIONE</p>
     */
    public static final DatatypeProperty expectedTime = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#expectedTime" );
    
    public static final DatatypeProperty exponent = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#exponent" );
    
    public static final DatatypeProperty extendName = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#extendName" );
    
    public static final DatatypeProperty extendNumber = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#extendNumber" );
    
    public static final DatatypeProperty fillRate = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#fillRate" );
    
    public static final DatatypeProperty free = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#free" );
    
    public static final DatatypeProperty heightHour = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#heightHour" );
    
    public static final DatatypeProperty hour = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#hour" );
    
    public static final DatatypeProperty humidity = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#humidity" );
    
    /** <p>orario in cui il bus ha raggiunto l'ultima fermata DA ELIMINARE QUANDO INSTANT 
     *  FUNZIONERA' ALLA PERFEZIONE</p>
     */
    public static final DatatypeProperty lastStopTime = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#lastStopTime" );
    
    public static final DatatypeProperty length = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#length" );
    
    public static final DatatypeProperty lunarPhase = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#lunarPhase" );
    
    /** <p>ente gestore del sistema AVM</p> */
    public static final DatatypeProperty managingBy = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#managingBy" );
    
    public static final DatatypeProperty maneuverType = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#maneuverType" );
    
    public static final DatatypeProperty maxTemp = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#maxTemp" );
    
    public static final DatatypeProperty minTemp = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#minTemp" );
    
    public static final DatatypeProperty moonrise = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#moonrise" );
    
    public static final DatatypeProperty moonset = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#moonset" );
    
    public static final DatatypeProperty name = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#name" );
    
    public static final DatatypeProperty nodeType = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#nodeType" );
    
    public static final DatatypeProperty number = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#number" );
    
    public static final DatatypeProperty occupancy = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#occupancy" );
    
    /** <p>numero di posti occupati</p> */
    public static final DatatypeProperty occupied = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#occupied" );
    
    public static final DatatypeProperty operatingStatus = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#operatingStatus" );
    
    /** <p>sente proprietario del sistema AVM</p> */
    public static final DatatypeProperty owner = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#owner" );
    
    /** <p>percentuale di posti occupati</p> */
    public static final DatatypeProperty parkOccupancy = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#parkOccupancy" );
    
    public static final DatatypeProperty perTemp = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#perTemp" );
    
    public static final DatatypeProperty porteCochere = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#porteCochere" );
    
    public static final DatatypeProperty recTemp = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#recTemp" );
    
    public static final DatatypeProperty restrictionType = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#restrictionType" );
    
    public static final DatatypeProperty restrictionValue = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#restrictionValue" );
    
    /** <p>stato della corsa: anticipo, ritardo, in orario</p> */
    public static final DatatypeProperty rideState = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#rideState" );
    
    public static final DatatypeProperty roadName = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#roadName" );
    
    public static final DatatypeProperty roadType = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#roadType" );
    
    /** <p>lunghezza percorso</p> */
    public static final DatatypeProperty routeLength = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#routeLength" );
    
    public static final DatatypeProperty snow = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#snow" );
    
    public static final DatatypeProperty speedLimit = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#speedLimit" );
    
    public static final DatatypeProperty speedPercentile = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#speedPercentile" );
    
    public static final DatatypeProperty sunHeight = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#sunHeight" );
    
    public static final DatatypeProperty sunrise = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#sunrise" );
    
    public static final DatatypeProperty sunset = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#sunset" );
    
    public static final DatatypeProperty text = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#text" );
    
    public static final DatatypeProperty thresholdPerc = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#thresholdPerc" );
    
    /** <p>ANCHE SE VERRA' INSERITO IL COLLEGAMENTO CON INSTANT, QUESTO ATTRIBUTO NON 
     *  VA CANCELLATO</p>
     */
    public static final DatatypeProperty timestamp = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#timestamp" );
    
    public static final DatatypeProperty trafficDir = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#trafficDir" );
    
    public static final DatatypeProperty uv = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#uv" );
    
    public static final DatatypeProperty validityStatus = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#validityStatus" );
    
    public static final DatatypeProperty value = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#value" );
    
    /** <p>numero di riconoscimento mezzo per azineda TPL</p> */
    public static final DatatypeProperty vehicle = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#vehicle" );
    
    public static final DatatypeProperty vehicleFlow = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#vehicleFlow" );
    
    public static final DatatypeProperty width = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#width" );
    
    public static final DatatypeProperty wind = m_model.createDatatypeProperty( "http://www.disit.dinfo.unifi.it/SiiMobility#wind" );
    
    /** <p>Corsa programmata da una certa azienda TPL su un certo percorso di una certa 
     *  linea</p>
     */
    public static final OntClass AVMRecord = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#AVMRecord" );
    
    /** <p>Alberghi e strutture simili</p> */
    public static final OntClass Accommodation = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Accommodation" );
    
    /** <p>Classe le cui istanze sono le estese amministrative definite nel grafo stradale</p> */
    public static final OntClass AdministrativeRoad = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#AdministrativeRoad" );
    
    /** <p>Punto di interconnessione tra segmenti di strada per determinare il percorso 
     *  di una linea di TPL</p>
     */
    public static final OntClass BusStop = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#BusStop" );
    
    /** <p>previsione di arrivo ad una certa fermata</p> */
    public static final OntClass BusStopForecast = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#BusStopForecast" );
    
    /** <p>Sensore che raccoglie i dati all'interno di un parcheggio</p> */
    public static final OntClass CarParkSensor = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#CarParkSensor" );
    
    /** <p>biblioteche, archivi, musei ed altre attivita' culturali</p> */
    public static final OntClass CulturalActivity = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#CulturalActivity" );
    
    /** <p>attivita' dei servizi delle agenzie di viaggio, tour operator e servizi prenotazione</p> */
    public static final OntClass Education = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Education" );
    
    /** <p>Veterinario</p> */
    public static final OntClass Emergency = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Emergency" );
    
    /** <p>Servizi per l'intrattenimento del cittadino</p> */
    public static final OntClass Entertainment = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Entertainment" );
    
    /** <p>Classe le cui istanze sono i possibili ingressi ai numeri civici</p> */
    public static final OntClass Entry = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Entry" );
    
    /** <p>Classe le cui istanze sono le regole di accesso ai differenti elementi stradali</p> */
    public static final OntClass EntryRule = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#EntryRule" );
    
    /** <p>banche, istituti monetari e altri servizi finanziari</p> */
    public static final OntClass FinancialService = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#FinancialService" );
    
    /** <p>Uffici aperti al pubblico</p> */
    public static final OntClass GovernmentOffice = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#GovernmentOffice" );
    
    /** <p>ospedali, studi medici, laboratori analisi e altre strutture che forniscono 
     *  servizi sanitari</p>
     */
    public static final OntClass HealthCare = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#HealthCare" );
    
    /** <p>Punto di interconnessione tra segmenti di strada per disegnare un RoadElement</p> */
    public static final OntClass Junction = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Junction" );
    
    /** <p>Insieme di linee TPL, individuabili a livello regionale come lotto</p> */
    public static final OntClass Lot = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Lot" );
    
    /** <p>Classe le cui istanze sono le possibili manovre che possono essere effettuate 
     *  su un elemento stradale</p>
     */
    public static final OntClass Maneuver = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Maneuver" );
    
    /** <p>Classe le cui istanze sono i cippi chilometrici che si trovano lungo le principali 
     *  strade</p>
     */
    public static final OntClass Milestone = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Milestone" );
    
    /** <p>Classe le cui istanze sono i vari comuni</p> */
    public static final OntClass Municipality = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Municipality" );
    
    /** <p>Classe le cui istanze sono i nodi che congiungono gli elementi stradali</p> */
    public static final OntClass Node = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Node" );
    
    /** <p>Singolo sensore per osservare velocita', traffico, concentrazione o densita'</p> */
    public static final OntClass Observation = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Observation" );
    
    /** <p>Comuni, regioni e provincie</p> */
    public static final OntClass Pa = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Pa" );
    
    /** <p>Classe le cui istanze sono le varie provincie</p> */
    public static final OntClass Province = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Province" );
    
    /** <p>Classe le cui istanze sono le varie regioni</p> */
    public static final OntClass Region = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Region" );
    
    /** <p>Delibera approvata da una qualche Pa</p> */
    public static final OntClass Resolution = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Resolution" );
    
    /** <p>Corsa programmata da una certa azienda TPL su un certo percorso di una certa 
     *  linea</p>
     */
    public static final OntClass Ride = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Ride" );
    
    /** <p>Classe le cui istanze sono le strade</p> */
    public static final OntClass Road = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Road" );
    
    /** <p>Classe le cui istanze sono gli elementi che compongono le strade</p> */
    public static final OntClass RoadElement = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#RoadElement" );
    
    /** <p>Tratto di strada lineare delimitato da due giunzioni che compone il road element</p> */
    public static final OntClass RoadLink = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#RoadLink" );
    
    /** <p>Percorso che percorre un mezzo di trasporto pubblico</p> */
    public static final OntClass Route = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Route" );
    
    /** <p>Tratto di strada delimitato da due TPL junction che determina il percorso 
     *  di una linea TPL</p>
     */
    public static final OntClass RouteLink = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#RouteLink" );
    
    /** <p>Tratto di strada compreso tra due successive fermate dell'autobus di una certa 
     *  linea</p>
     */
    public static final OntClass RouteSection = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#RouteSection" );
    
    /** <p>Singolo sensore per osservare velocita', traffico, concentrazione o densita'</p> */
    public static final OntClass SensorSite = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#SensorSite" );
    
    /** <p>Insieme di sensori che rappresentano un unica installazione, un unico sito</p> */
    public static final OntClass SensorSiteTable = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#SensorSiteTable" );
    
    /** <p>attivita' commerciali, servizi al cittadino, uffici...che possono essere localizzati 
     *  in un punto</p>
     */
    public static final OntClass Service = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Service" );
    
    public static final OntClass ServiceCategory = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#ServiceCategory" );
    
    /** <p>negozi, centri commerciali, spacci, ogni forma di attivita' di vendita al 
     *  pubblico</p>
     */
    public static final OntClass Shopping = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#Shopping" );
    
    /** <p>Registrazione della situazione di occupazione di un determinato parcheggio 
     *  in un certo istante</p>
     */
    public static final OntClass SituationRecord = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#SituationRecord" );
    
    /** <p>Valore che fa riferimento ad un dato statistico legato ad una strada o ad 
     *  una Pa</p>
     */
    public static final OntClass StatisticalData = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#StatisticalData" );
    
    /** <p>Classe le cui istanze sono i numeri civici conosciuti</p> */
    public static final OntClass StreetNumber = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#StreetNumber" );
    
    /** <p>Punto di interconnessione tra segmenti di strada per determinare il percorso 
     *  di una linea di TPL</p>
     */
    public static final OntClass TPLJunction = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TPLJunction" );
    
    /** <p>Linea di una certa azienda TPL</p> */
    public static final OntClass TPLLine = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TPLLine" );
    
    /** <p>attivita' dei servizi delle agenzie di viaggio, tour operator e servizi prenotazione</p> */
    public static final OntClass TourismService = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TourismService" );
    
    /** <p>sottoclasse delle osservazioni relative alla concentrazione di auto</p> */
    public static final OntClass TrafficConcentration = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TrafficConcentration" );
    
    /** <p>sottoclasse delle osservazioni relative alla flusso auto</p> */
    public static final OntClass TrafficFlow = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TrafficFlow" );
    
    /** <p>sottoclasse delle osservazioni sul tempo medio di transito tra auto</p> */
    public static final OntClass TrafficHeadway = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TrafficHeadway" );
    
    /** <p>sottoclasse delle osservazioni relative alla velocita' media</p> */
    public static final OntClass TrafficSpeed = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TrafficSpeed" );
    
    /** <p>Parcheggi auto, stazioni ferroviarie o degli autobus, tutto cio' che deve 
     *  essere localizzato su una mappa e fa riferimento al trasporto</p>
     */
    public static final OntClass TransferService = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#TransferService" );
    
    /** <p>Previsione metereologica relativa ad una specifica parte del giorno</p> */
    public static final OntClass WeatherPrediction = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#WeatherPrediction" );
    
    /** <p>Bollettino metereologico con informazioni relative a temperatura, unidita', 
     *  neve, etc</p>
     */
    public static final OntClass WeatherReport = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#WeatherReport" );
    
    /** <p>Ristoranti, enoteche e tutte le altre attivita' enogastronomiche</p> */
    public static final OntClass WineAndFood = m_model.createClass( "http://www.disit.dinfo.unifi.it/SiiMobility#WineAndFood" );
    
}