const MatchesInfo = require("../src/matches-info");
const matchesControl = new MatchesInfo();

const socketController = (socket) => {

    var matchId = -1;
    socket.join("want-updates");

    //******* Notificaciones del Cliente *******//

    //Solicitud para obtener las partidas
    socket.on("get-matches", (payload, callback) => {
        callback(matchesControl.toJSON);
    });

    //Solicitud para crear una partida
    socket.on("create-match", (payload, callback) => {
        if (matchId != -1) {
            callback({ error: "Ya se ha unido a una partida" });
            return;
        }

        socket.leave("want-updates");

        matchId = matchesControl.createMatch();
        socket.join(matchId.toString());

        socket.to("want-updates").emit('update-matches', matchesControl.toJSON);
    });

    //Solicitud para conocer el id de la partida a la que se unió
    socket.on('getMatchNumber', (payload, callback) => {
        callback(matchId);
    });

    //Solicitud para unirse a una partida
    socket.on("join-match", ({ id }, callback) => {

        if (matchId != -1) {
            callback({ error: "Ya se ha unido a una partida" });
            return;
        }

        if (matchesControl.addPlayertoMatch(id)) {
            matchId = id;
            socket.to(id.toString()).emit('new-player');

            socket.leave("want-updates");
            socket.join(matchId.toString());

            socket.to("want-updates").emit('update-matches', matchesControl.toJSON);
            callback({});
            return;
        }

        callback({ error: "La partida está llena o no existe" });
    });

    socket.on('send-play', (payload, callback) => {
        socket.to(matchId.toString()).emit('receive-play', payload);
        callback();
    });

    socket.on('restart', () => {
        matchesControl.setRestarts(matchId);
        if (matchesControl.enoughtToRestart(matchId)) {
            socket.emit('restart-game');
            socket.to(matchId.toString()).emit('restart-game');
            matchesControl.restoreRestarts(matchId);
        }
    });

    //Solicitud para salirse de una partida sin desconectar el socket
    socket.on('quit-match', (payload, callback) => {
        if (matchesControl.removePlayerfromMatch(matchId)) {
            socket.to(matchId.toString()).emit('quit-player');
            socket.leave(matchId.toString());
            matchId = -1;

            socket.to("want-updates").emit('update-matches', matchesControl.toJSON);
            socket.emit('update-matches', matchesControl.toJSON);
            socket.join("want-updates");
            callback();
        }
    });

    socket.on('disconnect', () => {
        if (matchesControl.removePlayerfromMatch(matchId)) {
            socket.to(matchId.toString()).emit('quit-player');
            socket.to("want-updates").emit('update-matches', matchesControl.toJSON);
        }
    });
}

//CONTROLAR QUE EL OTRO JUGADOR ESTÁ CONECTADO

module.exports = {
    socketController
}