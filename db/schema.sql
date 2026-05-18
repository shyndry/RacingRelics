CREATE DATABASE IF NOT EXISTS RacingRelics;
USE RacingRelics;

CREATE TABLE IF NOT EXISTS Utente (
    id_utente INT AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    ruolo VARCHAR(20) NOT NULL DEFAULT 'REGISTRATO',
    PRIMARY KEY (id_utente),
    UNIQUE (email)
)   ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Indirizzo (
    id_indirizzo INT AUTO_INCREMENT,
    id_utente INT NOT NULL,
    via VARCHAR(255) NOT NULL,
    citta VARCHAR(100) NOT NULL,
    provincia CHAR(2) NOT NULL,
    cap CHAR(5) NOT NULL,
    tipologia VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_indirizzo),
    FOREIGN KEY (id_utente) REFERENCES Utente(id_utente) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Prodotto (
    id_prodotto INT AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descrizione TEXT,
    prezzo_attuale DECIMAL(10, 2) NOT NULL,
    quantita_disponibile INT NOT NULL DEFAULT 1,
    immagine_path VARCHAR(255) DEFAULT 'default.jpg',
    scuderia VARCHAR(50),
    pilota VARCHAR(50),
    anno_campionato INT,
    gran_premio VARCHAR(100),
    attivo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_prodotto)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Ordine (
    id_ordine INT AUTO_INCREMENT,
    id_utente INT NOT NULL,
    id_indirizzo_consegna INT NOT NULL,
    data_ordine TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stato VARCHAR(50) NOT NULL DEFAULT 'ELABORATO',
    PRIMARY KEY (id_ordine),
    FOREIGN KEY (id_utente) REFERENCES Utente(id_utente) ON DELETE RESTRICT,
    FOREIGN KEY (id_indirizzo_consegna) REFERENCES Indirizzo(id_indirizzo) ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ComposizioneOrdine (
    id_ordine INT NOT NULL,
    id_prodotto INT NOT NULL,
    quantita INT NOT NULL DEFAULT 1,
    prezzo_acquisto DECIMAL (10,2) NOT NULL,
    PRIMARY KEY (id_ordine, id_prodotto),
    FOREIGN KEY (id_ordine) REFERENCES Ordine(id_ordine) ON DELETE CASCADE,
    FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id_prodotto) ON DELETE RESTRICT
) ENGINE = InnoDB;

