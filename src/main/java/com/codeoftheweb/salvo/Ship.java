/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author alumno
 */
@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long idShip;

    enum typeShip {
        CARRIER,
        BATTLESHIP,
        SUBMARINE,
        DESTROYER,
        PATROLBOAT
    }
    private typeShip typeShip;

    @ElementCollection
    @Column(name = "location")
    private List<String> locationShip = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {
    }

    public Ship(typeShip typeShip, List<String> locationShip) {
        this.typeShip = typeShip;
        this.locationShip = locationShip;
    }

    public Ship(typeShip typeShip, List<String> locationShip, GamePlayer gamePlayer) {
        this.typeShip = typeShip;
        this.locationShip = locationShip;
        this.gamePlayer = gamePlayer;
    }

    public long getIdShip() {
        return idShip;
    }

    public void setIdShip(long idShip) {
        this.idShip = idShip;
    }

    public typeShip getTypeShip() {
        return typeShip;
    }

    public void setTypeShip(typeShip typeShip) {
        this.typeShip = typeShip;
    }

    public List<String> getLocationShip() {
        return locationShip;
    }

    public void setLocationShip(List<String> locationsShip) {
        this.locationShip = locationsShip;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
    
    public Map<String, Object> shipDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("typeShip", this.typeShip); /*"key", valor*/
       dto.put("location", this.locationShip);
       
       return dto;
   }
}
