let boolLogin=false;
let boolSignin=false;


function isLogged(){
    if(appGames.CurrentPlayer==null||appGames.CurrentPlayer=="null"){
        boolLogin=false;
        boolSignin=false;
        showButtons();
        showUser();
    }else{
        boolLogin=true;
        boolSignin=false;
        showButtons();
        showUser();
    }
}
    

function loginFetch(url){ /*Fetch para loguearse*/
    const data= new FormData(document.querySelector("#logForm"));
    let methodLogin={method:"POST",
           body:data};
    
    if(data.get("userName")==""|| data.get("pass")==""){
        writeMessage("Please complete all required fields");
    }else{
        fetch(url, methodLogin)
        .then(response =>{if(response.ok){
            boolLogin=true;
            
            showButtons(); 
            document.querySelector("#close").click();
            showUser(data); 
            myFetch("/api/games");
            console.log("logged in");

        }else{
            writeMessage("User doesn't exists. Please sign in");
            boolLogin=false; 
            boolSignin=true;
            showButtons(); 
            throw new Error()
        }})        
    }
    
}

function logoutFetch(url){ /*Fetch para desloguearse*/
    let methodLogout={method:"POST"};
    
    fetch(url, methodLogout)
    .then(response =>{if(response.ok){
            boolLogin=false; 
            showButtons();
            location.reload(); 
            document.querySelector("#close").click(); 
            writeMessage(""); 
             
            showUser();
            
            console.log("logged out");
            myFetch("/api/games");
            
    }else{throw new Error()
    }});
}

function signinFetch(url){ /*Fetch para registrarse*/
    const signData= new FormData(document.querySelector("#logForm"));
    let methodSignin={method:"POST",
                     body:signData};
    if(signData.get("userName")==""|| signData.get("pass")==""){
        writeMessage("Please complete all required fields");
    }else{
        fetch(url, methodSignin)
        .then(response=>{if(response.ok){
            console.log("signed in"); 
            loginFetch(`/api/login`); 
            document.querySelector("#close").click();
            myFetch("/api/games");
            
        }else{writeMessage("User already exists. Please try again"); 
              throw new Error()
        }}); 
    }
}

function createGameFetch(url){ /*Fetch para crear un nuevo juego*/
    let methodCreate={method:"POST"};
    let aux;
    fetch(url, methodCreate)
    .then(function (response){if(response.ok){
        console.log("game created");
        return response.json();
                     
        }else{
            if(response.status==401){showModalError("You must log in first.");
            }else{showModalError("Unable to create game. Please try again.");}
            throw new Error();
        }})
    .then(function (json){window.location.href=`/web/game.html?gp=${json.idGamePlayer}`})
    .catch(function(error){console.log(error)
                             });
    
}

function showModalError(msg){
    let str=`<div class="modal" role="dialog" id="modalmsg">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Something went wrong...</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="false">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p>${msg}</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>`
    
    document.querySelector("#modalError").innerHTML=str;
    $('#modalmsg').modal('show');
}

function writeMessage(str){ /*Escribe mensajes en el modal. Estados de login*/
    document.querySelector("#mensajito").innerHTML=str;
}

function showUser(formData){ /*Muestra en el navbar quien es el usuario logueado*/
    if(formData !=null){
        document.querySelector("#currentUser").innerHTML=formData.get("userName");
    }else{
       if(appGames.CurrentPlayer == "null" || appGames.CurrentPlayer==null){
        document.querySelector("#currentUser").innerHTML="";
        }else{
        document.querySelector("#currentUser").innerHTML=appGames.CurrentPlayer.email;; 
        }
    }
    
}

function showButtons(){ /*Muestra u oculta los botones de login, logout y signin*/
    if(boolLogin){
        document.querySelector("#loginButton").classList.add("buttonHide");
        document.querySelector("#logoutButton").classList.remove("buttonHide");
        
    }else{
        document.querySelector("#logoutButton").classList.add("buttonHide");
        document.querySelector("#loginButton").classList.remove("buttonHide");
        document.querySelector("#submit").classList.remove("buttonHide");
    }
    if(boolSignin){
        document.querySelector("#submit").setAttribute("disabled", "");
        document.querySelector("#signInSub").removeAttribute("disabled", "");
        document.querySelector("#signInSub").setAttribute("enabled", "");
        
           
    }else{document.querySelector("#signInSub").setAttribute("disabled", "");
        document.querySelector("#submit").removeAttribute("disabled", "");
        document.querySelector("#submit").setAttribute("enabled", "");
        
             
    }
}




//////////////////////////////////////////////////////////////////////////////////////////////////////////
/*Listeners de botones
*/////////////////////////////////////////////////////////////////////////////////////////////////////////


document.querySelector("#submit").addEventListener("click", function(){loginFetch(`/api/login`)});
document.querySelector("#signInSub").addEventListener("click", function(){signinFetch(`/api/players`)})
document.querySelector("#logoutButton").addEventListener("click", function(){logoutFetch(`/api/logout`)});
document.querySelector("#close").addEventListener("click", function(){boolSignin=false;
    console.log("salir"); 
    document.querySelector("#logForm").reset();
    showButtons();
    writeMessage(""); 
    
});
document.querySelector("#createButton").addEventListener("click", function(){createGameFetch(`/api/games`)});



