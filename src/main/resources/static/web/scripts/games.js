let appGames={};


function myFetch(url){
    fetch(url)
    .then(response =>{if(response.ok){return response.json();}else{throw new Error()}})
    .then(json=>{appGames=json;
                 document.querySelector("#gamesList").innerHTML=showList();
                 //showJoinButtons();
                 document.querySelector("#gameTable").innerHTML=createLeaderboard();
                 isLogged();
                                  
                }
    );
}

function showList(){
    let str=' <ol>';
    
    appGames.Games.forEach(appGame=>{str+=`<li class="text">${workDates(appGame.created)}; ${appGame.gamePlayers[0].player.email}, ${(appGame.gamePlayers[1])?appGame.gamePlayers[1].player.email:""}</li>`
    if(appGames.CurrentPlayer != "null"){
        if ((appGames.CurrentPlayer.games.flatMap(i=>i.idGame)).includes(appGame.idGame)){
            str+=`<li class="text"><tr><td><a href="/web/game.html?gp=${getGamePlayer(appGame)}", class="btn btn-warning" role="button" data-gp="${getGamePlayer(appGame)}" data-g="${appGame.idGame}">Go to game!</a></td>`;
            
        }else{
            if(appGame.gamePlayers.length<2 && str.includes(`data-gp=${getGamePlayer(appGame)}`)==false){
                str+=`<td><a class="btn btn-warning join" role="button" data-gp="${getGamePlayer(appGame)}" data-g="${appGame.idGame}" onclick="joinGameFetch('/api/game/${appGame.idGame}/players',${appGame.idGame})">Join game!</a></td></tr></li>`;
            }
        }
    }});
    
    str+="</ol>";
    
    return str;
}

function joinGameFetch(url, int){
    let methodJoin={method:"POST",
           body:{"gameId":`${int}`}};
    fetch(url, methodJoin)
    .then(response =>{if(response.ok){return response.json();}else{throw new Error()}})
    .then(json=>{window.location.href=`/web/game.html?gp=${json.idGamePlayer}`});
                 
                
}

function getGamePlayer(game){
    let url=0;   
    game.gamePlayers.forEach(gp=>{(gp.player.idPlayer==appGames.CurrentPlayer.idPlayer)? url=gp.idGamePlayer:null});
    
    return url;
}

function workDates(strDate){
    let date=new Date(Date.parse(strDate));
    
    return date.toLocaleString();
}

function createLeaderboard(){
    let aux=[];
    let str=`<table class="table table-hover">
                <thead>
                    <tr>
                        <th class="text">Name</th>
                        <th class="text">Total</th>
                        <th class="text">Won</th>
                        <th class="text">Lost</th>
                        <th class="text">Tied</th>
                    </tr>
                </thead>
                <tbody class="text">`
                    
    
    appGames.Players.forEach(pl=>{aux=getTotalScores(pl);
                                  str+=`<tr><td>${pl.userPlayer}</td>
                                        <td>${aux[0]}</td>
                                        <td>${aux[1]}</td>
                                        <td>${aux[2]}</td>
                                        <td>${aux[3]}</td></tr>`});
                   str+=`</tbody> </table>`;
    
    return str;
    
}

function getTotalScores(player){ /*el arreglo que devuelve contiene en sus posiciones el puntaje total, won, lost, tied (en ese orden)*/
    let aux;
    let total=0;
    let won=0;
    let tied=0;
    let lost=0;
    player.scoreInfo.forEach(sc=>{total+=sc.score;
                                    switch(sc.statusScore){
                                        case "WON":
                                            won++;
                                            break;
                                        case "LOST":
                                            lost++;
                                            break;
                                        case "TIED":
                                            tied++;
                                            break;
                                                }});
    
    return aux=[total, won, lost, tied];
}

function isPlayerInGame(){
    
}

myFetch("/api/games");

