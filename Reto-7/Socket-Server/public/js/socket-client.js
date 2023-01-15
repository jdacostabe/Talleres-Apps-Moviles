const lblOnline = document.querySelector('#lblOnline')
const lblOffline = document.querySelector('#lblOffline')
const txtMensaje = document.querySelector('#txtMensaje');
const btnCrear = document.querySelector('#btnCrear');
const btnUnir = document.querySelector('#btnUnir');
const btnDesconectar = document.querySelector('#btnDesconectar');
const btnConectar = document.querySelector('#btnConectar');
const btnJugar = document.querySelector('#btnJugar');
const btnQuit = document.querySelector('#btnQuit');
const listGroup = document.querySelector('#listaJugadas');
const btnRes = document.querySelector('#btnRes');

var lastToPlay = false;

const socket = io("http://localhost:3000");

socket.on('connect', () => {
    lblOffline.style.display = "none";
    restartGame()

    //Emitir un get-matches solo cuando sea necesario en el cliente!!!!!!!!!!!!!!!!!!!!!!!!!!!
    socket.emit('get-matches', {}, ({ matches }) => {
        console.log(matches);
    });
});

socket.on('disconnect', () => {
    lblOffline.style.display = "";
    lblOnline.style.display = "none";
    restartGame()

    btnJugar.disabled = true;
    console.log("Se desconectó de una partida");
});

//******** Acciones de los botones ********//

// Unirse a partida (Cuando se une, no debe ser capaz de jugar, pues empieza el creador de la partida)
btnUnir.addEventListener('click', () => {
    payload = {
        id: txtMensaje.value
    };

    socket.emit('join-match', payload, ({ error }) => {
        if (error) console.log(error);
        else btnRes.disabled = false;
    });
});

// Crear una partida
btnCrear.addEventListener('click', () => {
    socket.emit('create-match', {}, ({ error }) => { console.log(error); });
});

// Conectarse al socket
btnConectar.addEventListener('click', () => { socket.connect(); });

// Desconectarse del socket
btnDesconectar.addEventListener('click', () => { socket.disconnect(); });

// Salir de la partida
btnQuit.addEventListener('click', () => {
    socket.emit('quit-match', {}, () => {
        btnRes.disabled = true;
        btnJugar.disabled = true;
        restartGame();
    });
});

// Enviar una jugada (Solo está activo si está dentro de una partida)
btnJugar.addEventListener('click', () => {
    payload = {
        mensaje: txtMensaje.value
    };

    socket.emit('send-play', payload, () => {
        //Cuando se envia una  jugada, se verifica si el juego ya terminó (La desactivación del juego siempre se hará)
        btnJugar.disabled = true;
        listGroup.innerHTML += `<li class="list-group-item">${txtMensaje.value}</li>`;
        if (finishedGame()) lastToPlay = true;
    });
});

//Solicitar un reinicio (No importa si es durante o después)
btnRes.addEventListener('click', () => {
    btnRes.disabled = true;
    socket.emit('restart');
});


//********Actualizaciones enviadas por el Servidor********//

// Actualizar el feed de partidas
socket.on('update-matches', ({ matches }) => { console.log(matches); });

// Ser notificado que un jugador se ha unido a la partida (El creador del juego empieza)
socket.on('new-player', () => {
    enableInteractions();
});

//Ser notificado que el otro jugador salió de la partida
socket.on('quit-player', () => {
    disableInteractions();
    restartGame();
});

//Ser notificado que el otro jugador hizo un movimiento, y por ende, puede jugar
socket.on('receive-play', ({ mensaje }) => {
    listGroup.innerHTML += `<li class="list-group-item">${mensaje}</li>`;
    //Cuando se recibe una jugada, se verifica si el juego ya terminó y permite jugar o no
    btnJugar.disabled = finishedGame();
});

socket.on('restart-game', () => {
    if (!lastToPlay) {
        btnJugar.disabled = false;
    }
    lastToPlay = false;
    btnRes.disabled = false;
    restartGame();
});

function restartGame() {
    listGroup.innerHTML = ``;
}

function enableInteractions() {
    btnJugar.disabled = false;
    btnRes.disabled = false;
}

function disableInteractions() {
    btnJugar.disabled = true;
    btnRes.disabled = true;
}

function finishedGame() {
    return listGroup.getElementsByTagName("li").length > 2;
}



//Si un jugador se sale, se reinicia el juego
//Cuando alguien gana o pierde, se pregunta si se quiere reiniciar
//Se puede reiniciar en plena partida
//Cuando termina el juego, ninguno puede jugar



//Reiniciar???????????????????
