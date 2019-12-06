/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
/**
 *
 * @author alumno
 */
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long idGame;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<GamePlayer> gamePlayers=new HashSet<>();
    
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Score> scores=new HashSet<>();
    
    enum statusGame {
        WAITINGFOROPPONENT,/*Waiting an opponent to join the game*/
        WAITINGOPPONENTSSHIPS,
        WAITINGOPPONENTSSALVOS,
        CONTINUE,
        GAMEOVER
    }
    private statusGame statusGame;
    
    public Game(){}
    
    public Game(LocalDateTime creationDate){
     this.creationDate=creationDate;
    }
   
   public long getId(){
       return idGame;
   }
   public void setId(long idGame){
       this.idGame=idGame;
   }
   
   public LocalDateTime getCreationDate(){
       return creationDate;
   }
   
   public void setCreationDate(LocalDateTime creationDate){
       this.creationDate=creationDate;
   }

    public statusGame getStatusGame() {
        return statusGame;
    }

    public void setStatusGame(statusGame statusGame) {
        this.statusGame = statusGame;
    }
   
    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
        gamePlayer.setGame(this);
   }
   
    public Set<GamePlayer>getGamePlayers(){
        return this.gamePlayers;
    }
    
    public void addScore(Score score){
        this.scores.add(score);
        score.setGame(this);
    }
    
    public Set<Score> getScores(){
        return this.scores;
    }
   
   public List<Player>getPlayers(){
       return  this.gamePlayers.stream().map(gamePl->gamePl.getPlayer()).collect(Collectors.toList());
   }
   
   public Map<String, Object> gameDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("idGame", this.idGame); /*"key", valor*/
       dto.put("created", this.creationDate);
       dto.put("gamePlayers", this.gamePlayers.stream().map(gp->gp.gamePlayerDTO()));
       dto.put("gameStatus", this.getStatusGame());
       return dto;
   }
}
