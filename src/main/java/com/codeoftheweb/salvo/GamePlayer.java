/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author alumno
 */
@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long idGamePlayer;
    private LocalDateTime joinDate;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;
    
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Ship> ships=new HashSet<>();
    
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Salvo> salvos=new HashSet<>();
    
    public GamePlayer(){}
    
    public GamePlayer(Game game, Player player){
        this.game=game;
        this.player=player;
        this.joinDate= LocalDateTime.now();
    }

    public long getIdGamePlayer() {
        return idGamePlayer;
    }

    public void setIdGamePlayer(long idGamePlayer) {
        this.idGamePlayer = idGamePlayer;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    public Set<Ship> getShips(){
       return this.ships;
   }
   
   public void addShip(Ship ship){
       this.ships.add(ship);
       ship.setGamePlayer(this);
   }
   
   public Set<Salvo> getSalvos(){
       return this.salvos;
   }
   
   public void addSalvo(Salvo salvo){
       this.salvos.add(salvo);
       salvo.setGamePlayer(this);
   }
   
             
    public Map<String, Object> gamePlayerDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("idGamePlayer", this.idGamePlayer); /*"key", valor*/
       dto.put("player", this.player.playerDTO());
       if(this.player.getScore(this.game)==null){
           dto.put("score","null");
       }else{
           dto.put("score",this.player.getScore(this.game).scoreDTO());
       }       
       return dto;
   }
    
    
    
   
}
