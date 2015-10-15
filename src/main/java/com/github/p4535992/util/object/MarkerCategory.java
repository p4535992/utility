package com.github.p4535992.util.object;

import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4535992 on 15/10/2015.
 */
public class MarkerCategory {

    private static Map<String,String> mapCategory = new HashMap<>();

    MarkerCategory(){}

    public static Map<String,String> getMapmarkerCategory(){
        mapCategory.put("affittacamere","markerAccommodation");
        mapCategory.put("villaggio_vacanze","markerAccommodation");
        mapCategory.put("albergo","markerAccommodation");
        mapCategory.put("casa_per_vacanze","markerAccommodation");
        mapCategory.put("casa_di_riposo","markerAccommodation");
        mapCategory.put("casa_per_ferie","markerAccommodation");
        mapCategory.put("bed_and_breakfast","markerAccommodation");
        mapCategory.put("ostello","markerAccommodation");
        mapCategory.put("residenza_turistica_alberghiera","markerAccommodation");
        mapCategory.put("agriturismo","markerAccommodation");
        mapCategory.put("residence_di_villeggiatura","markerAccommodation");
        mapCategory.put("centri_accoglienza_e_case_alloggio","markerAccommodation");
        mapCategory.put("campeggio","markerAccommodation");
        mapCategory.put("residenze_epoca","markerAccommodation");
        mapCategory.put("rifugio_alpino","markerAccommodation");

        mapCategory.put("autogrill","markerWineAndFood");
        mapCategory.put("bar_pasticceria","markerWineAndFood");
        mapCategory.put("paninoteche_pubs","markerWineAndFood");
        mapCategory.put("pizzeria","markerWineAndFood");
        mapCategory.put("forno","markerWineAndFood");
        mapCategory.put("rosticceria","markerWineAndFood");
        mapCategory.put("sushi_bar","markerWineAndFood");
        mapCategory.put("mense","markerWineAndFood");
        mapCategory.put("ristorante","markerWineAndFood");
        mapCategory.put("catering","markerWineAndFood");
        mapCategory.put("Enoteche_e_wine_bar","markerWineAndFood");
        mapCategory.put("gelateria","markerWineAndFood");
        mapCategory.put("trattoria","markerWineAndFood");

        mapCategory.put("museo","markerCulturalActivity");
        mapCategory.put("luogo_monumento","markerCulturalActivity");
        mapCategory.put("biblioteca","markerCulturalActivity");
        mapCategory.put("auditorium","markerCulturalActivity");

        mapCategory.put("istituti_tecnici_pubblici","markerEducation");
        mapCategory.put("scuole_elementari_pubbliche","markerEducation");
        mapCategory.put("licei_privati","markerEducation");
        mapCategory.put("universita_pubbliche","markerEducation");
        mapCategory.put("scuola_di_vela","markerEducation");
        mapCategory.put("istituti_magistrali","markerEducation");
        mapCategory.put("istituti_professionali_privati","markerEducation");
        mapCategory.put("istituti_tecnici_privati","markerEducation");
        mapCategory.put("scuole_materne_private","markerEducation");
        mapCategory.put("istituti_professionali_pubblici","markerEducation");
        mapCategory.put("conservatori_di_musica","markerEducation");
        mapCategory.put("scuola_di_formazione","markerEducation");
        mapCategory.put("licei_pubblici","markerEducation");
        mapCategory.put("scuola_di_sci","markerEducation");
        mapCategory.put("scuole_elementari_private","markerEducation");
        mapCategory.put("scuole_medie_pubbliche","markerEducation");
        mapCategory.put("corsi_di_lingue","markerEducation");
        mapCategory.put("scuole_materne_pubbliche","markerEducation");
        mapCategory.put("scuole_medie_private","markerEducation");
        mapCategory.put("scuola_di_sub","markerEducation");
        mapCategory.put("nidi_privati","markerEducation");

        mapCategory.put("farmacia","markerEmergency");
        mapCategory.put("guardia_costiera_capitaneria_di_porto","markerEmergency");
        mapCategory.put("polizia_stradale","markerEmergency");
        mapCategory.put("commissariato_di_pubblica_sicurezza","markerEmergency");
        mapCategory.put("pronto_soccorso","markerEmergency");
        mapCategory.put("polizia_municipale","markerEmergency");
        mapCategory.put("carabinieri","markerEmergency");
        mapCategory.put("numeri_utili","markerEmergency");
        mapCategory.put("guardia_medica","markerEmergency");
        mapCategory.put("soccorso_stradale","markerEmergency");
        mapCategory.put("guardia_di_finanza","markerEmergency");
        mapCategory.put("corpo_forestale_dello_stato","markerEmergency");
        mapCategory.put("protezione_civile","markerEmergency");
        mapCategory.put("vigili_del_fuoco","markerEmergency");

        mapCategory.put("cinema","markerEntertainment");
        mapCategory.put("discoteca","markerEntertainment");
        mapCategory.put("golf","markerEntertainment");
        mapCategory.put("sala_gioco","markerEntertainment");
        mapCategory.put("ludoteca","markerEntertainment");
        mapCategory.put("rafting_canoa_e_kayak","markerEntertainment");
        mapCategory.put("riserve_di_pesca","markerEntertainment");
        mapCategory.put("maneggi","markerEntertainment");
        mapCategory.put("teatro","markerEntertainment");
        mapCategory.put("centro_sociale","markerEntertainment");
        mapCategory.put("ippodromo","markerEntertainment");
        mapCategory.put("alpinismo","markerEntertainment");
        mapCategory.put("piscina","markerEntertainment");
        mapCategory.put("palestra_fitness","markerEntertainment");
        mapCategory.put("impianti_sciistici","markerEntertainment");
        mapCategory.put("impianto_sportivo","markerEntertainment");
        mapCategory.put("parco_naturale","markerEntertainment");
        mapCategory.put("acquario","markerEntertainment");

        mapCategory.put("banca","markerFinancialService");
        mapCategory.put("banche","markerFinancialService");
        mapCategory.put("assicurazione","markerFinancialService");
        mapCategory.put("ATM","markerFinancialService");
        mapCategory.put("istituto_monetario","markerFinancialService");

        mapCategory.put("ufficio_inps","markerGovernmentOffice");
        mapCategory.put("Agenzia_delle_entrate","markerGovernmentOffice");
        mapCategory.put("Informa_Giovani","markerGovernmentOffice");
        mapCategory.put("centro_per_l_impiego","markerGovernmentOffice");
        mapCategory.put("motorizzazione_civile","markerGovernmentOffice");
        mapCategory.put("anagrafe_e_uffici_vari","markerGovernmentOffice");
        mapCategory.put("cafè","markerGovernmentOffice");
        mapCategory.put("prefettura","markerGovernmentOffice");
        mapCategory.put("questura","markerGovernmentOffice");
        mapCategory.put("Consolato","markerGovernmentOffice");
        mapCategory.put("Ufficio_postale","markerGovernmentOffice");
        mapCategory.put("assistenti_sociali_uffici","markerGovernmentOffice");
        mapCategory.put("ufficio_oggetti_smarriti_aeroporto","markerGovernmentOffice");
        mapCategory.put("ufficio_oggetti_smarriti_stazione_treno","markerGovernmentOffice");

        mapCategory.put("ambulatorio_medico","markerHealthCare");
        mapCategory.put("ricoveri","markerHealthCare");
        mapCategory.put("veterinario","markerHealthCare");
        mapCategory.put("centro_unico_di_prenotazione","markerHealthCare");
        mapCategory.put("clinica_privata","markerHealthCare");
        mapCategory.put("poliambulatorio","markerHealthCare");
        mapCategory.put("croce_rossa","markerHealthCare");
        mapCategory.put("ospedale_pubblico","markerHealthCare");
        mapCategory.put("centri_di_riabilitazione","markerHealthCare");
        mapCategory.put("asl","markerHealthCare");
        mapCategory.put("distretto_sanitario","markerHealthCare");
        mapCategory.put("dentista","markerHealthCare");
        mapCategory.put("consultori","markerHealthCare");
        mapCategory.put("comunita_e_centri_di_recupero_per_dipendenze","markerHealthCare");
        mapCategory.put("centro_antiveleni","markerHealthCare");
        mapCategory.put("centri_assistenza","markerHealthCare");
        mapCategory.put("centri_diurni","markerHealthCare");

        mapCategory.put("ipermercati","markerShopping");
        mapCategory.put("grande_distribuzione_non_alimentare","markerShopping");
        mapCategory.put("negozio_artigiano","markerShopping");
        mapCategory.put("spacci_outlet_abbigliamento","markerShopping");
        mapCategory.put("centri_commerciali","markerShopping");
        mapCategory.put("negozi_monomarca","markerShopping");
        mapCategory.put("spacci_outlet_calzature","markerShopping");

        mapCategory.put("noleggio_veicoli","markerTourismService");
        mapCategory.put("camper_service","markerTourismService");
        mapCategory.put("agenzia_di_viaggi","markerTourismService");
        mapCategory.put("ufficio_visite_guidate","markerTourismService");
        mapCategory.put("tour_operator","markerTourismService");

        mapCategory.put("autobus_urbani","markerTransferService");
        mapCategory.put("elisuperfici","markerTransferService");
        mapCategory.put("aviosuperfici","markerTransferService");
        mapCategory.put("parcheggio_auto","markerTransferService");
        mapCategory.put("stazione_ferroviaria","markerTransferService");
        mapCategory.put("aeroporto_civile","markerTransferService");
        mapCategory.put("corriere_espresso","markerTransferService");

        mapCategory.put("fermata","markerBusStops");
        mapCategory.put("","markerEducation");
        return mapCategory ;
    }

    public String getCategoryFromSimilarString(String similarString){
        for(Map.Entry<String,String> entry: mapCategory.entrySet()){
            if(entry.getKey().equalsIgnoreCase(similarString.trim().replace(" ","_"))){
                return WordUtils.capitalize(entry.getKey().replace("_"," "));
            }
        }
        return "";
    }
}
