/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity /*Crea tabla en base de datos*/
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long idPlayer;
    private String firstName;
    private String lastName;
    private String userName;
    private String pass;
    
    
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<GamePlayer> gamePlayers=new HashSet<>();
    
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Score> scores=new HashSet<>();
    
    public Player() { }
  
    public Player(String user, String pass) {
      this.userName = user;
      this.pass=pass;
    }
    
    public Player(String user, String first, String last, String pass) {
      this.userName = user;
      this.firstName = first;
      this.lastName = last;
      this.pass=pass;
    }
    
    public long getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(long idPlayer) {
        this.idPlayer = idPlayer;
    }
  
    public String getFirstName() {
        return firstName;
    }
  
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
  
    public String getLastName() {
        return lastName;
    }
  
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
  
    public String getUserName() {
        return userName;
    }
  
    public void setUserName(String userName) {
        this.userName = userName;
    }
  
    @Override
    public String toString() {
      return firstName + " " + lastName;
    }
    
    public void addGamePlayer(GamePlayer gamePlayer){
        this.gamePlayers.add(gamePlayer);
        gamePlayer.setPlayer(this);
    }
    
    public Set<GamePlayer>getGamePlayers(){
        return this.gamePlayers;
    }
    
    public void addScore(Score score){
        this.scores.add(score);
        score.setPlayer(this);
    }
    
    public Score getScore(Game game){
        return this.scores.stream().filter(s->s.getGame().getId()==game.getId()).findFirst().orElse(null);
    }
    
   @JsonIgnore 
    public List<Map<String, Object>> getGames(){
        return this.gamePlayers.stream().map(gamePl -> gamePl.getGame().gameDTO()).collect(Collectors.toList());
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    
    
    public Map<String, Object> playerDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("idPlayer", this.idPlayer); /*"key", valor*/
       dto.put("email", this.userName);
             
       return dto;
   }
   
    public Map<String, Object> currentplayerDTO(){ /*Indica cómo y qué info pasar al JSON*/
       Map<String, Object> dto=new LinkedHashMap<>(); /*Crea el Map que devuelve la info (ordenada por el Linked) a continuación detallada*/
       dto.put("idPlayer", this.idPlayer); /*"key", valor*/
       dto.put("email", this.userName);
       dto.put("games", getGames());
             
       return dto;
   }
    
    public Map<String, Object> playerBoardDTO(){
        Map<String, Object> dto=new LinkedHashMap<>();
        dto.put("idPlayer", this.idPlayer);
        dto.put("userPlayer", this.userName);
        dto.put("scoreInfo", this.scores.stream().map(Score::score2DTO));
       
        return dto;
    }
    
}
