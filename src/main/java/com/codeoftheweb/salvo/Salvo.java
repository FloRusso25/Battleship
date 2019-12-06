/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author alumno
 */
@Entity
public class Salvo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long idSalvo;
    private long turn;
    private int sunken;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;
    
    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();

    public Salvo(){}
    
    public Salvo(List<String> locations){
        this.locations=locations;
    }
    
    public Salvo(long turn, List<String> locations){
        this.turn=turn;
        this.locations=locations;
    }
    
    public Salvo(GamePlayer gamePlayer, long turn, List<String> locations){
        this.turn=turn;
        this.gamePlayer=gamePlayer;
        this.locations=locations;
    }
    
    public long getIdSalvo() {
        return idSalvo;
    }

    public void setIdSalvo(long idSalvo) {
        this.idSalvo = idSalvo;
    }

    public long getTurn() {
        return turn;
    }

    public void setTurn(long turn) {
        this.turn = turn;
    }

   @JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocation() {
        return locations;
    }

    public void setLocation(List<String> locations) {
        this.locations = locations;
    }
     
    public List<String> getHits(){
        
        GamePlayer opp=this.getGamePlayer().getGame().getGamePlayers().stream().filter(g->g.getIdGamePlayer()!=this.getGamePlayer().getIdGamePlayer()).findFirst().orElse(null);
        List<String> hit=new ArrayList<> ();
        
        if(opp!=null){
            List<String> oppShips= new ArrayList<> ();
            opp.getShips().forEach(i->oppShips.addAll(i.getLocationShip()));
         
            this.locations.forEach(i->{if(oppShips.contains(i)){hit.add(i);}});
            
        }
        
        return hit;          
    }
    
    public List<Ship> getSunken (){
        
        GamePlayer opp=this.getGamePlayer().getGame().getGamePlayers().stream().filter(g->g.getIdGamePlayer()!=this.getGamePlayer().getIdGamePlayer()).findFirst().orElse(null);
        List<Ship> oppSunken= new ArrayList<>();
        
        if(opp!=null){
            List<String> gplSalvos=new ArrayList<>();
            this.getGamePlayer().getSalvos().forEach(i->gplSalvos.addAll(i.getLocation()));
            
            opp.getShips().forEach(i->{if(gplSalvos.containsAll(i.getLocationShip())){oppSunken.add(i);}});
        }
        
        return oppSunken;
    }
    
       
      
    public List<Ship> getMySunkenShips (){
        
        GamePlayer opp=this.getGamePlayer().getGame().getGamePlayers().stream().filter(g->g.getIdGamePlayer()!=this.getGamePlayer().getIdGamePlayer()).findFirst().orElse(null);
        List<Ship> gplSunken= new ArrayList<>();
        
        if(opp!=null){
            List<String> oppSalvos=new ArrayList<>();
            opp.getSalvos().forEach(i->oppSalvos.addAll(i.getLocation()));
          
            this.getGamePlayer().getShips().forEach(i->{if(oppSalvos.containsAll(i.getLocationShip())){gplSunken.add(i);}});
        }
        
        return gplSunken;
    }
           
    public Map<String, Object> salvoDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("turn", this.turn); /*"key", valor*/
       dto.put("gPlayer", this.gamePlayer.getIdGamePlayer());
       dto.put("locationSalvos", this.locations);
       dto.put("hits", getHits());
       dto.put("sunken", getSunken().stream().map(Ship::shipDTO));
         
       return dto;
   }
}
