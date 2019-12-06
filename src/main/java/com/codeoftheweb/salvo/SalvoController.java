/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codeoftheweb.salvo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author alumno
 */
@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private PasswordEncoder passEncoder;
    
    @RequestMapping("/user")
    @ResponseBody
    private Player getCurrentUser(Authentication authentication){
        return (authentication != null)? playerRepository.findByUserName(authentication.getName()):null;
    }
    
    @RequestMapping(path="/players", method=RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String userName, @RequestParam String pass){
        if(userName.isEmpty()||pass.isEmpty()){
            return new ResponseEntity<> ("Missing Data", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByUserName(userName)!= null){
            return new ResponseEntity<> ("Username already in use", HttpStatus.FORBIDDEN);
        }
        
        playerRepository.save(new Player(userName, passEncoder.encode(pass)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    
    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){
        
        Map<String, Object> myJson=new LinkedHashMap<>();
        myJson.put("CurrentPlayer", (getCurrentUser(authentication) !=null)?getCurrentUser(authentication).currentplayerDTO():"null");
        myJson.put("Players", playerRepository.findAll().stream().map(e->e.playerBoardDTO()).collect(Collectors.toList()));
        myJson.put("Games", gameRepository.findAll().stream().map(e->e.gameDTO()).collect(Collectors.toList()));
               
        return myJson;
    }
    
    @RequestMapping(path="/games", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        
        if(getCurrentUser(authentication)==null){
            return new ResponseEntity<>(createMap("Unauthorized", "none"), HttpStatus.UNAUTHORIZED);
        }else{
            
            Game newGame=gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer newGP=gamePlayerRepository.save(new GamePlayer (newGame, getCurrentUser(authentication)));
            
            return new ResponseEntity<>(createMap("idGamePlayer",newGP.getIdGamePlayer()), HttpStatus.CREATED);
            
        }
    }
     
    @RequestMapping(path="/game/{gameId}/players", method=RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> joinGame(@PathVariable long gameId, Authentication authentication){
        if(getCurrentUser(authentication)==null){
            return new ResponseEntity<>(createMap("Unauthorised", "not logged"), HttpStatus.UNAUTHORIZED);
        }else{
            if(gameRepository.findByIdGame(gameId)==null){
                return new ResponseEntity<>(createMap("Game", "doesn't exist"), HttpStatus.FORBIDDEN);
            }else{
                if(gameRepository.findByIdGame(gameId).getPlayers().size()>1){
                    return new ResponseEntity<>(createMap("Game", "is full"), HttpStatus.FORBIDDEN);
                }else{
                    if(getCurrentUser(authentication).getGames().contains(gameRepository.findByIdGame(gameId))==false)
                    {
                        GamePlayer newGP=gamePlayerRepository.save(new GamePlayer (gameRepository.findByIdGame(gameId), getCurrentUser(authentication)));
                        return new ResponseEntity<>(createMap("idGamePlayer",newGP.getIdGamePlayer()), HttpStatus.CREATED); 
                    }else{
                        return new ResponseEntity<>(createMap("Cannot", "join this game again"), HttpStatus.FORBIDDEN);
                    }
                }
            }
        }
    }
    
    @RequestMapping("/players")
    public List<Map<String, Object>>getPlayers(){
        return playerRepository.findAll().stream().map(e->e.playerBoardDTO()).collect(Collectors.toList());
    }
    
    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long gamePlayerId, Authentication authentication){
        if(gamePlayerRepository.findByIdGamePlayer(gamePlayerId) == null){
            return new ResponseEntity<>(createMap("GamePlayer", "doesn't exist"), HttpStatus.UNAUTHORIZED);
        }else{
            if(gamePlayerRepository.findByIdGamePlayer(gamePlayerId).getPlayer().getIdPlayer() != getCurrentUser(authentication).getIdPlayer()){
                return new ResponseEntity<>(createMap("Unauthorised", "access"), HttpStatus.UNAUTHORIZED);
            }else{
                return new ResponseEntity<>(this.gameViewDTO(gamePlayerRepository.findById(gamePlayerId).orElse(null)), HttpStatus.CREATED);
            }
        }
            
    }
    
    @RequestMapping(path="/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@PathVariable long gamePlayerId, @RequestBody Set<Ship> ships, Authentication auth){
        GamePlayer gpl=gamePlayerRepository.findByIdGamePlayer(gamePlayerId);
        if(getCurrentUser(auth)==null){
            return new ResponseEntity<>(createMap("Unauthorised", "not logged"), HttpStatus.UNAUTHORIZED);
        }else{
            if(gpl==null){
                return new ResponseEntity<>(createMap("Invalid", "gamePlayer"), HttpStatus.UNAUTHORIZED);
            }else{
                if (getCurrentUser(auth).getIdPlayer()!= gpl.getPlayer().getIdPlayer()){
                    return new ResponseEntity<>(createMap("Unauthorised", "not your game"), HttpStatus.UNAUTHORIZED);
                }else{
                    if(gpl.getShips().isEmpty()){
                        ships.forEach(i->{gpl.addShip(i);gamePlayerRepository.save(gpl);});
                        return new ResponseEntity<>(createMap("Ships", "saved"), HttpStatus.CREATED);
                    }else{
                       return new ResponseEntity<>(createMap("Ships", "already placed"), HttpStatus.FORBIDDEN);
                    }
                    
                }
            }
        }
    }
    
    @RequestMapping(path="/games/players/{gamePlayerId}/salvos", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> storeSalvos(@PathVariable long gamePlayerId, @RequestBody Salvo salvo, Authentication auth){
        GamePlayer gpl=gamePlayerRepository.findByIdGamePlayer(gamePlayerId);
        GamePlayer opp=gpl.getGame().getGamePlayers().stream().filter(i->i.getIdGamePlayer()!=gpl.getIdGamePlayer()).collect(toList()).get(0);
        Game currentGame=gpl.getGame();
        if(getCurrentUser(auth)==null){
            return new ResponseEntity<>(createMap("Unauthorised", "not logged"), HttpStatus.UNAUTHORIZED);
        }else{
            if(gpl==null){
                return new ResponseEntity<>(createMap("Invalid", "gamePlayer"), HttpStatus.UNAUTHORIZED);
            }else{
                if (getCurrentUser(auth).getIdPlayer()!= gpl.getPlayer().getIdPlayer()){
                    return new ResponseEntity<>(createMap("Unauthorised", "not your game"), HttpStatus.UNAUTHORIZED);
                }else{ 
                    if(currentGame.getStatusGame()!=Game.statusGame.GAMEOVER){
                        if(gpl.getSalvos().size()<=opp.getSalvos().size()){
                                                                                 
                            int x=getMySunkenShips(gpl).size();
                            if(x==5 && gpl.getSalvos().size()<opp.getSalvos().size()){
                                x=4;
                            }
                            if(salvo.getLocation().size() == (5-(x))){
                                salvo.setTurn(setTurn1(gpl.getSalvos()));
                                gpl.addSalvo(salvo);
                                gamePlayerRepository.save(gpl);
                                return new ResponseEntity<>(createMap("Salvo", "added"), HttpStatus.CREATED);
                            }else{
                                return new ResponseEntity<>(createMap("Too many", "salvos shot"), HttpStatus.I_AM_A_TEAPOT);
                            }
                        }else{
                        return new ResponseEntity<>(createMap("Salvo", "already submitted"), HttpStatus.FORBIDDEN);
                        }
                    }else{
                        return new ResponseEntity<>(createMap("Game", "over"), HttpStatus.FORBIDDEN);
                    }
                    
                    
                }
            }
        }
    }
    
    private long setTurn1(Set<Salvo> salvos){
        return salvos.size()+1;
        
    }
    
    
    public Map<String, Object> gameViewDTO(GamePlayer gPl){
        Map<String, Object> dto=new LinkedHashMap<>();
        if (gPl != null){
            dto.put("idGame", gPl.getGame().getId());
            dto.put("creationDate", gPl.getGame().getCreationDate());
            dto.put("gamePlayer", gPl.getGame().getGamePlayers().stream().map(GamePlayer::gamePlayerDTO));
            dto.put("player", gPl.getPlayer().getUserName());
            dto.put("ships", gPl.getShips().stream().map(Ship::shipDTO));
            dto.put("salvos", gPl.getGame().getGamePlayers().stream().flatMap(gplyr->gplyr.getSalvos().stream().map(Salvo::salvoDTO)));
            dto.put("hits", gPl.getSalvos().stream().map(i->i.getHits()));
            dto.put("sunkenopp", gPl.getSalvos().stream().map(i->i.getSunken().stream().map(Ship::shipDTO)).findFirst().orElse(null));
            dto.put("mySunken", gPl.getSalvos().stream().map(i->i.getMySunkenShips().stream().map(Ship::shipDTO)).findFirst().orElse(null));
            dto.put("gameStatus", getStatus(gPl).getStatusGame());
            
            if (gPl.getGame().getStatusGame()==Game.statusGame.GAMEOVER){
               dto.put("score", gPl.getGame().getScores().stream().filter(i->i.getPlayer()==gPl.getPlayer()).findFirst().orElse(null).scoreDTO());
            }else{
                dto.put("score", "first finish game");
            }
            
        } else{
            dto.put("error", "this game doesn't exist");
        }
       
        
        return dto;
    }
    
    public Map<String, Object> createMap(String str, Object obj){
        Map<String, Object> map=new LinkedHashMap<>();
        map.put(str, obj);
        
        return map;
    }
    
    public List<Ship> getMySunkenShips (GamePlayer gpl){
        
        GamePlayer opp=gpl.getGame().getGamePlayers().stream().filter(g->g.getIdGamePlayer()!=gpl.getIdGamePlayer()).findFirst().orElse(null);
        List<Ship> gplSunken= new ArrayList<>();
        
        if(opp!=null){
            List<String> oppSalvos=new ArrayList<>();
            opp.getSalvos().forEach(i->oppSalvos.addAll(i.getLocation()));
          
            gpl.getShips().forEach(i->{if(oppSalvos.containsAll(i.getLocationShip())){gplSunken.add(i);}});
        }
        
        return gplSunken;
    }
    
    public Game getStatus(GamePlayer gpl){
        Game currentGame=gpl.getGame();
        GamePlayer opp=currentGame.getGamePlayers().stream().filter(i->i.getIdGamePlayer()!=gpl.getIdGamePlayer()).findFirst().orElse(null);
        if(opp!=null){
            if (opp.getShips().size()==5){
                if(opp.getSalvos().size()<gpl.getSalvos().size()){
                    currentGame.setStatusGame(Game.statusGame.WAITINGOPPONENTSSALVOS);
                }else{
                    boolean oppSunken=gpl.getSalvos().stream().anyMatch(i->i.getSunken().size()== 5); /*si todos los barcos del oponente fueron hundidos*/
                    boolean mySunken = opp.getSalvos().stream().anyMatch(i-> i.getSunken().size() == 5);
                    
                    if(opp.getSalvos().size()==gpl.getSalvos().size() && (oppSunken || mySunken)){
                        currentGame.setStatusGame(Game.statusGame.GAMEOVER);
                        setScore(currentGame, gpl);
                    }else{
                        currentGame.setStatusGame(Game.statusGame.CONTINUE);
                    }
                }
            }else{
                currentGame.setStatusGame(Game.statusGame.WAITINGOPPONENTSSHIPS);
            }
        }else{
            currentGame.setStatusGame(Game.statusGame.WAITINGFOROPPONENT);
        }
        
        return currentGame;
    }
    
    public void setScore(Game game, GamePlayer gpl){
        
        GamePlayer opp=game.getGamePlayers().stream().filter(i->i.getIdGamePlayer()!=gpl.getIdGamePlayer()).findFirst().orElse(null);
        boolean oppSunken=gpl.getSalvos().stream().anyMatch(i->i.getSunken().size()== 5); /*si todos los barcos del oponente fueron hundidos*/
        boolean mySunken = opp.getSalvos().stream().anyMatch(i-> i.getSunken().size() == 5);
       
        if(game.getStatusGame()==Game.statusGame.GAMEOVER){
            if(oppSunken){
                gpl.getGame().addScore(new Score(gpl.getGame(), gpl.getPlayer(), 2, Score.statusScore.WON));
                opp.getGame().addScore(new Score(opp.getGame(), opp.getPlayer(), 0, Score.statusScore.LOST));
            }
            if(mySunken){
                gpl.getGame().addScore(new Score(gpl.getGame(), gpl.getPlayer(), 0, Score.statusScore.LOST));
                opp.getGame().addScore(new Score(opp.getGame(), opp.getPlayer(), 2, Score.statusScore.WON));
            }
            if(oppSunken && mySunken){
                gpl.getGame().addScore(new Score(gpl.getGame(), gpl.getPlayer(), 1, Score.statusScore.TIED));
                opp.getGame().addScore(new Score(opp.getGame(), opp.getPlayer(), 1, Score.statusScore.TIED));
            }
        }       
    }
    
    
    
}
