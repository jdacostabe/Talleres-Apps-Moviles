class MatchesInfo {
    constructor() {
        this.matches = {
            //Llave: id del match
            //Valor: Numero de jugadores (EXTENSIÓN: Se podría almacenar el puntaje total)
        };
        this.restarts = {
            //Llave: id del match
            //Valor: Restars acumulados
        };
        this.actualId = 0;
    }

    get toJSON() {
        return {
            matches: this.matches
        }
    }

    createMatch() {
        this.actualId++;
        this.matches[this.actualId] = 1;
        this.restarts[this.actualId] = 0;
        return this.actualId;
    }

    addPlayertoMatch(id) {
        if (this.matches[id] && this.matches[id] == 1) {
            this.matches[id]++;
            return true;
        }
        return false;
    }

    removePlayerfromMatch(id) {
        if (this.matches[id]) {
            this.matches[id]--;
            if (this.matches[id] == 0) this.deleteMatch(id);
            this.restoreRestarts(id);
            return true
        }
        return false;
    }

    deleteMatch(id) {
        delete this.matches[id];
        delete this.restarts[id];
    }

    restoreRestarts(id) {
        this.restarts[id] = 0;
    }

    setRestarts(id) {
        this.restarts[id]++;
    }

    enoughtToRestart(id) {
        return this.restarts[id] >= 2;
    }
}

module.exports = MatchesInfo;