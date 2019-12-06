/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long idScore;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;
    
    private LocalDateTime finishDate;
    private long score;
    
    enum statusScore {
        WON,
        LOST,
        TIED
    }
    private statusScore statusScore;

   
    
    public Score(){}
    
    public Score(long score){
        this.score=score;
    }
    
    public Score(Game game, Player player, long score, statusScore statusScore) {
        this.game=game;
        this.player=player;
        this.finishDate=LocalDateTime.now();
        this.score=score;
        this.statusScore=statusScore;
    }

    public long getIdScore() {
        return idScore;
    }

    public void setIdScore(long idScore) {
        this.idScore = idScore;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
    
     public statusScore getStatusScore() {
        return statusScore;
    }

    public void setStatusScore(statusScore statusScore) {
        this.statusScore = statusScore;
    }
    
    public Map<String, Object> scoreDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("idScore", this.idScore); /*"key", valor*/
       dto.put("finishDate", this.finishDate);
       dto.put("score", this.score);
       dto.put("StatusScore", this.statusScore);
       
       return dto;
    }
    
    public Map<String, Object>score2DTO(){
        Map<String, Object> dto=new LinkedHashMap<>();
        dto.put("score", this.score);
        dto.put("idGameScore", this.game.getId());
        dto.put("statusScore", this.statusScore);
        return dto;
    }

    }





