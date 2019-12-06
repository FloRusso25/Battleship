/*creates the ships with the ability of been placed in the grid. 
It requires a shipType, that is, the id by wich the ship will be recongnized;
the amount of cells the ship is going to occupy in the grid;
a parent where the ship will be appended to;
and a boolean that specifies whether the ship can be moved or not.
*/
const createShips = function(shipType, length, orientation, parent, isStatic){

    let ship = document.createElement('DIV')
    let grip = document.createElement('DIV')
    let content = document.createElement('DIV')

    ship.classList.add('grid-item')
    ship.dataset.length = length
    ship.dataset.orientation = orientation
    ship.id = shipType

    if(orientation == 'vertical'){
        ship.style.transform = 'rotate(90deg)'
    }

    if(window.innerWidth >= 768){
        ship.style.width = `${length * 45}px` 
        ship.style.height = '45px'
    }else if(window.innerWidth >= 576){
        ship.style.width = `${length * 35}px` 
        ship.style.height = '35px'
    }else{
        ship.style.width = `${length * 30}px` 
        ship.style.height = '30px'
    }

    window.addEventListener('resize', () => {
        if(window.innerWidth >= 768){
            ship.style.width = `${length * 45}px` 
            ship.style.height = '45px'
        }else if(window.innerWidth >= 576){
            ship.style.width = `${length * 35}px` 
            ship.style.height = '35px'
        }else{
            ship.style.width = `${length * 30}px` 
            ship.style.height = '30px'
        }
    })
    
    if(!isStatic){
        grip.classList.add('grip')
        grip.draggable = 'true'
        grip.addEventListener('dragstart', dragShip)
        ship.addEventListener('touchmove', touchShip)
        ship.addEventListener('touchend', touchShipEnd)
        ship.appendChild(grip)
    }
    

    content.classList.add('grid-item-content')
    ship.appendChild(content)

    parent.appendChild(ship)

    if(!isStatic){
        rotateShips(shipType)
    }else{
        checkBusyCells(ship,parent)
    }
    


    //event to allow the ship beeing dragged
    function dragShip(ev){
        ev.dataTransfer.setData("ship", ev.target.parentNode.id)

    }

    //event to allow the ship beeing dragged on touch devices
    function touchShip(ev){
        // make the element draggable by giving it an absolute position and modifying the x and y coordinates
        ship.classList.add("absolute");
        
        var touch = ev.targetTouches[0];
        // Place element where the finger is
        ship.style.left = touch.pageX - 25 + 'px';
        ship.style.top = touch.pageY - 25 + 'px';
        event.preventDefault();
    }

    function touchShipEnd(ev){
        // hide the draggable element, or the elementFromPoint won't find what's underneath
        ship.style.left = '-1000px';
        ship.style.top = '-1000px';
        // find the element on the last draggable position
        var endTarget = document.elementFromPoint(
            event.changedTouches[0].pageX,
            event.changedTouches[0].pageY
            );

            
        // position it relative again and remove the inline styles that aren't needed anymore
        ship.classList.remove('absolute')
        ship.style.left = '';
        ship.style.top = '';
        // put the draggable into it's new home
        if (endTarget.classList.contains('grid-cell')) {
            let y = endTarget.dataset.y.charCodeAt() - 64
            let x = parseInt(endTarget.dataset.x)
            if(ship.dataset.orientation == 'horizontal'){
                if(parseInt(ship.dataset.length) + x > 11){
                    document.querySelector("#display p").innerText = 'movement not allowed'
                    return
                }
                for(let i = 1; i < ship.dataset.length;i++){
                    let id = (endTarget.id).match(new RegExp(`[^${endTarget.dataset.y}|^${endTarget.dataset.x}]`, 'g')).join('')
                    let cellId = `${id}${endTarget.dataset.y}${x + i}`
                    if(document.getElementById(cellId).className.search(/busy-cell/) != -1){
                        document.querySelector("#display p").innerText = 'careful'
                        return
                    }
                }
              } else{
                if(parseInt(ship.dataset.length) + y > 11){
                    document.querySelector("#display p").innerText = 'movement not allowed'
                    return
                }
                for(let i = 1; i < ship.dataset.length;i++){
                    let id = (endTarget.id).match(new RegExp(`[^${endTarget.dataset.y}|^${endTarget.dataset.x}]`, 'g')).join('')
                    let cellId = `${id}${String.fromCharCode(endTarget.dataset.y.charCodeAt() + i)}${x}`
                    if(document.getElementById(cellId).className.search(/busy-cell/) != -1){
                        document.querySelector("#display p").innerText = 'careful'
                        return
                    }
                }
              }
            endTarget.appendChild(ship);
            ship.dataset.x = x
            ship.dataset.y = String.fromCharCode(y + 64)

            checkBusyCells(ship, endTarget)
        }else{
            document.querySelector("#display p").innerText = 'movement not allowed'
            return
        }
    }

    //event to allow the ship rotation
    function rotateShips(shipType){

        document.querySelector(`#${shipType}`).addEventListener('click', function(ev){

            document.querySelector("#display p").innerText = ''

            let ship = ev.target.parentNode
            let orientation = ship.dataset.orientation
            let cell = ship.parentElement.classList.contains('grid-cell') ? ship.parentElement : null

            if(cell != null){
                if(orientation == 'horizontal'){
                    if(parseInt(ship.dataset.length) + (cell.dataset.y.charCodeAt() - 64) > 11){
                        document.querySelector("#display p").innerText = 'careful'
                        return
                    }
                    
                    for(let i = 1; i < ship.dataset.length;i++){
                        let id = (cell.id).match(new RegExp(`[^${cell.dataset.y}|^${cell.dataset.x}]`, 'g')).join('')
                        let cellId = `${id}${String.fromCharCode(cell.dataset.y.charCodeAt() + i)}${cell.dataset.x}`
                        if(document.getElementById(cellId).className.search(/busy-cell/) != -1){
                            document.querySelector("#display p").innerText = 'careful'
                            return
                        }
                    }

                } else{
                    if(parseInt(ship.dataset.length) + parseInt(cell.dataset.x) > 11){
                        document.querySelector("#display p").innerText = 'careful'
                        return
                    }

                    for(let i = 1; i < ship.dataset.length;i++){
                        let id = (cell.id).match(new RegExp(`[^${cell.dataset.y}|^${cell.dataset.x}]`, 'g')).join('')
                        let cellId = `${id}${cell.dataset.y}${parseInt(cell.dataset.x) + i}`
                        if(document.getElementById(cellId).className.search(/busy-cell/) != -1){
                            document.querySelector("#display p").innerText = 'careful'
                            return
                        }
                    }
                }
            }

            if(orientation == 'horizontal'){
                ship.dataset.orientation = 'vertical'
                ship.style.transform = 'rotate(90deg)'
                
            } else{
                ship.dataset.orientation = 'horizontal'
                ship.style.transform = 'rotate(360deg)'

            }
            if(cell != null){
                checkBusyCells(ship,cell)
            }
            
        })
    }

    
}

createShips('carrier', 5, 'horizontal', document.getElementById('dock'),false)
createShips('battleship', 4, 'horizontal', document.getElementById('dock'),false)
createShips('submarine', 3, 'horizontal', document.getElementById('dock'),false)
createShips('destroyer', 3, 'horizontal', document.getElementById('dock'),false)
createShips('patrol_boat', 2, 'horizontal', document.getElementById('dock'),false)


let queryParam = new URLSearchParams(location.search);
let gp = queryParam.get('gp');

let locationShip={};
let games={};
let gameStatus;

function locationShipFetch(url, indicator){
    
    fetch(url)
    .then(response =>{if(response.ok){return response.json();}else{throw new Error()}})
    .then(json=>{locationShip=json;
                 tellPlayer(indicator);
                    if(locationShip.ships.length==0){putShip();
                    }else{removeShip();putShip();}
                    
                    if(locationShip.salvos.lenght!=0){showMySalvos();}
                 gameStatus=locationShip.gameStatus;   
                 checkGameStatus();
                 hideButtons();           
                 showHits();
                  
                 
                }
    );
}

function tellPlayer(indicator){/*Muestra en el div con "welcome..." el player y su contrincante. Devuelve un arreglo con el player en la 1° posicion y el opponent en la 2°*/
    let str="";
    let player=""; 
    let opp="";
    if(locationShip.gamePlayer[0].idGamePlayer==gp){
        player=locationShip.gamePlayer[0].player.email;
        opp=(locationShip.gamePlayer[1]!=null)?locationShip.gamePlayer[1].player.email:null;
        str=`${player} (you) vs. ${(opp!=null)?opp:"waiting for a rival"}`
    }else{
        player=locationShip.gamePlayer[1].player.email;
        opp=locationShip.gamePlayer[0].player.email;
        str=`${player} (you) vs. ${opp}`;
        
    }
    
   /*(indicator)?document.querySelector("#display p").innerHTML+=str:document.querySelector("#display p").innerHTML=str;*/
    
    return [player, opp];
}

function tellPosition(ship){ /*Verifica orientación de los barcos*/
    if (ship[0][0]==ship[1][0]){
        return "horizontal";
    } else {return "vertical";}
}

function putShip(){ /*Posiciona los barcos en la cuadrícula*/
        
    locationShip.ships.forEach(ship=>{createShips(ship.typeShip.toLowerCase(), ship.location.length, tellPosition(ship.location), document.querySelector(`#ships${ship.location[0]}`), true)});
}

function removeShip(){ /*Borra los barcos de la cuadricula*/
    document.querySelectorAll('[id^="ships"] [class="grid-item"]').forEach(i=>i.remove())

}

function showMySalvos(){ /*colorea las celdas con los salvos del fetch*/
    
    locationShip.salvos.forEach(sal=>{sal.locationSalvos.forEach(pos=>{(sal.gPlayer==gp)?showMyHits(pos):""})});
}

function removeMySalvos(){/*borra los salvos de la grilla*/
    document.querySelectorAll('.clickSelected').forEach(i=>i.classList.remove("clickSelected"));
    
}

function showHits(){
    let aux=locationShip.ships.flatMap(i=> i.location);
    
    locationShip.salvos.forEach(sal=>{sal.locationSalvos.forEach(pos=>{(sal.player!=gp && aux.includes(pos))?document.querySelector(`#ships${pos}`).classList.add(`shipHit`):""})});
}

function showMyHits(element){
   if(locationShip.hits.flatMap(i=>i).includes(element)){
       document.querySelector(`#salvos${element}`).classList.add(`shipHit`);
   }else{document.querySelector(`#salvos${element}`).classList.add(`notHit`);}
    
}

function myShipsPositions(){ /*Crea un arreglo con las posiciones de los barcos que luego se envían al fetch*/
    let ship=[];
    let aux=[];
    
    document.querySelectorAll(".battleship-busy-cell").forEach(i=>aux.push(`${i.dataset.y}${i.dataset.x}`));
    ship.push({"typeShip":"BATTLESHIP","locationShip":aux});
    aux=[];
    document.querySelectorAll(".carrier-busy-cell").forEach(i=>aux.push(`${i.dataset.y}${i.dataset.x}`));
    ship.push({"typeShip":"CARRIER", "locationShip":aux});
    aux=[];
    document.querySelectorAll(".submarine-busy-cell").forEach(i=>aux.push(`${i.dataset.y}${i.dataset.x}`));
    ship.push({"typeShip":"SUBMARINE", "locationShip":aux});
    aux=[];
    document.querySelectorAll(".destroyer-busy-cell").forEach(i=>aux.push(`${i.dataset.y}${i.dataset.x}`));
    ship.push({"typeShip":"DESTROYER", "locationShip":aux});
    aux=[];
    document.querySelectorAll(".patrol_boat-busy-cell").forEach(i=>aux.push(`${i.dataset.y}${i.dataset.x}`));
    ship.push({"typeShip":"PATROLBOAT", "locationShip":aux});
    
    return ship;
}

function mySalvosPositions(){/*Crea objeto con las posiciones de las celdas seleccionados*/
    let salvo={};
    let aux=[];
    
    document.querySelectorAll(".clickSelected").forEach(i=>aux.push(`${i.dataset.y}${i.dataset.x}`));
    salvo["location"]=aux;
    
    
    return salvo;
}

function placeShipsFetch(url){ /*Fetch con la posición de los barcos*/
    let methodFetch={method:"POST",
                     body:JSON.stringify(myShipsPositions()),
                    headers:{"Content-Type":"application/json"}}
    fetch(url, methodFetch)
    .then(response=>{if(response.ok){console.log("ok enviado")
                                        locationShipFetch(`/api/game_view/${gp}`, false);
                                    }else{throw new Error()}});
    
    
}

function storeSalvosFetch(url){
    let methodFetch={method:"POST",
                     body:JSON.stringify(mySalvosPositions()),
                    headers:{"Content-Type":"application/json"}}
    fetch(url, methodFetch)
    .then(response=>{if(response.ok){console.log("ok salvo");
                                     locationShipFetch(`/api/game_view/${gp}`, false);
                                     removeMySalvos();
                                                                   
                                    }else{throw new Error()}});
    
}

function hideButtons(){
    if(locationShip.ships.length!=0){
        document.querySelector("#placeButton").classList.add("buttonHide");
        document.querySelectorAll('[id="dock"] [class="grid-item"]').forEach(i=>i.remove());
        
        
    }else{
        document.querySelector("#placeButton").classList.remove("buttonHide");
        putShip();
        showMySalvos();
        
    }
    
}

function areShipsPlaced(){
    
    let boolPos=true;
    
    if (document.querySelectorAll('[class$="-busy-cell"]').length!=17){
        let msg="Please, put all the ships first.";
        boolPos=false;
        let str=`<div id="modalmsg" class="modal fade" role="dialog">
  <div class="modal-dialog">
    
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Stop!</h4>
      </div>
      <div class="modal-body">
        <p>${msg}</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>`;
    document.querySelector("#modalShip").innerHTML=str;
    $('#modalmsg').modal('show'); 
        
    }
     
   
    
    return boolPos;
    
}

locationShipFetch(`/api/game_view/${gp}`, true);



document.querySelector("#placeButton").addEventListener("click", function(){
    if(areShipsPlaced()){placeShipsFetch(`/api/games/players/${gp}/ships`);
    }
});
document.querySelector("#salvoButton").addEventListener("click", function(){storeSalvosFetch(`/api/games/players/${gp}/salvos`); } );
document.getElementById('returnButton').href="/web/games.html"; /*Retorna al home*/


function checkGameStatus(){
    
    switch(gameStatus){
    
        case "WAITINGFOROPPONENT":
            document.querySelector("#salvoButton").classList.add("buttonHide");
            document.querySelector("#msgStatus").innerHTML=`<p>Welcome ${tellPlayer(false)[0]}. Waiting for an opponent to join the game.</p>`;
            
            setTimeout(function(){locationShipFetch(`/api/game_view/${gp}`, false);}, 5000);
            break;
            
        case "WAITINGOPPONENTSSHIPS":
            document.querySelector("#salvoButton").classList.add("buttonHide");
            document.querySelector("#msgStatus").innerHTML=`<p>Waiting for ${tellPlayer(false)[1]} to place the ships.</p>`;
            
            setTimeout(function(){locationShipFetch(`/api/game_view/${gp}`, false);}, 5000);
                       
            break;
            
        case "WAITINGOPPONENTSSALVOS":
            document.querySelector("#salvoButton").classList.add("buttonHide");
            document.querySelector("#msgStatus").innerHTML=`<p>Waiting for ${tellPlayer(false)[1]} to shoot.</p>`;
            
            setTimeout(function(){locationShipFetch(`/api/game_view/${gp}`, false);}, 5000);
            
            break;
            
        case "CONTINUE":
            if(locationShip.ships.length==0){
                document.querySelector("#salvoButton").classList.add("buttonHide");
            }else{document.querySelector("#salvoButton").classList.remove("buttonHide");}
            
            document.querySelector("#msgStatus").innerHTML=`<p></p>`;
            
            setTimeout(function(){locationShipFetch(`/api/game_view/${gp}`, false);}, 5000);
            break;
            
        case "GAMEOVER":
            document.querySelector("#msgStatus").innerHTML=`<p>Game Over!</p>`;
            setTimeout(function(){locationShipFetch(`/api/game_view/${gp}`, false);}, 5000);
            break;
    }
}

