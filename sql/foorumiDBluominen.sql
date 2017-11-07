DROP DATABASE IF EXISTS Foorumi;
CREATE DATABASE Foorumi
DEFAULT CHARACTER SET utf8;
USE Foorumi;

SET NAMES 'utf8';  -- tämä tiedosto utf8 -> varmistetaan, että menee samassa formaatissa kantaan
SET AUTOCOMMIT=0;

DROP TABLE IF EXISTS `keskustelu`;
CREATE TABLE `keskustelu` (
	`keskusteluid` int NOT NULL AUTO_INCREMENT,
	`nimi` varchar(128) NOT NULL,
	`kuvaus` text NOT NULL,
	PRIMARY KEY (`keskusteluid`)
);

INSERT INTO `keskustelu` VALUES(
1, 'Yleinen', 'Yleistä keskustelua');

DROP TABLE IF EXISTS `henkilo`;
CREATE TABLE `henkilo` (
	`hloid` int NOT NULL AUTO_INCREMENT,
	`kayttajanimi` varchar(128) NOT NULL,
	`salasana` varchar(128) NOT NULL,
	`nimimerkki` varchar(128),
	`kuvaus` text,
	`rooli` ENUM(
		'admin', 
		'rekisteroitynyt', 
		'moderaattori', 
		'muu'),
	PRIMARY KEY (`hloid`)
);

INSERT INTO `henkilo` VALUES(
1, 'root', 'admin', 'Mestari', 'Pääylläpitäjä', 'admin');

INSERT INTO `henkilo` VALUES(
2, 'anonymous', '', 'Anonyymi', 'Anonyymi käyttäjä, ei kirjautunut', NULL);

DROP TABLE IF EXISTS `viesti`;
CREATE TABLE `viesti` (
	`id` int NOT NULL AUTO_INCREMENT,
	`kirjoittaja` int NOT NULL,
	`keskusteluid` int NOT NULL,
	`otsikko` varchar(255),
	`viesti` text NOT NULL,
	`vastaus` int DEFAULT NULL,
	`kirjoitettu` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`viimeksimuutettu` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	CONSTRAINT `viesti_henkilo_fk` FOREIGN KEY (`kirjoittaja`) REFERENCES `henkilo` (`hloid`),
	CONSTRAINT `viesti_keskustelu_fk` FOREIGN KEY (`keskusteluid`) REFERENCES `keskustelu` (`keskusteluid`),
	PRIMARY KEY (`id`)
);

INSERT INTO `viesti` VALUES(
1, 1, 1, 'Tervetuloa', 'Tervetuloa keskustelemaan', NULL, NULL, NULL);

COMMIT;
SET AUTOCOMMIT=1;