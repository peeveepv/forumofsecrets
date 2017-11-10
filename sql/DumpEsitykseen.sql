CREATE DATABASE  IF NOT EXISTS `foorumi` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `foorumi`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: foorumi
-- ------------------------------------------------------
-- Server version	5.7.20-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `henkilo`
--

DROP TABLE IF EXISTS `henkilo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `henkilo` (
  `hloid` int(11) NOT NULL AUTO_INCREMENT,
  `kayttajanimi` varchar(128) NOT NULL,
  `salasana` varchar(128) NOT NULL,
  `nimimerkki` varchar(128) DEFAULT NULL,
  `kuvaus` text,
  `rooli` enum('admin','rekisteroitynyt','moderaattori','muu') DEFAULT NULL,
  PRIMARY KEY (`hloid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `henkilo`
--

LOCK TABLES `henkilo` WRITE;
/*!40000 ALTER TABLE `henkilo` DISABLE KEYS */;
INSERT INTO `henkilo` VALUES (1,'root','admin','Mr. Hauska','Pääylläpitäjä','admin'),(2,'anonymous','anon','Anonyymi','Anonyymi käyttäjä, ei kirjautunut',NULL),(3,'Antti','pass',NULL,NULL,NULL),(4,'testi','',NULL,NULL,NULL),(5,'Shark','shark','sharkie omg','Olen HAI *chomp*',NULL),(6,'Samu','samu','Samushka','Samun testitunnus',NULL),(7,'Mika','master','mikaster','Tässä on mika','rekisteroitynyt'),(8,'Antarktis','anakonda',NULL,NULL,'rekisteroitynyt'),(9,'höpöpöhö','höpöhöpö','mirkkuli','fffffffffffff','rekisteroitynyt'),(10,'Kala','kissa',NULL,NULL,'rekisteroitynyt');
/*!40000 ALTER TABLE `henkilo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `keskustelu`
--

DROP TABLE IF EXISTS `keskustelu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keskustelu` (
  `keskusteluid` int(11) NOT NULL AUTO_INCREMENT,
  `nimi` varchar(128) NOT NULL,
  `kuvaus` text NOT NULL,
  PRIMARY KEY (`keskusteluid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `keskustelu`
--

LOCK TABLES `keskustelu` WRITE;
/*!40000 ALTER TABLE `keskustelu` DISABLE KEYS */;
INSERT INTO `keskustelu` VALUES (1,'Yleinen','Yleistä keskustelua'),(2,'Koirat','täällä koirameemejä'),(3,'Samun salaisuudet','Tänne ei pääse muut'),(4,'Mikan tyylittelyt','Täältä löytyy kaikki Mikan tyylittelyt'),(12,'Vauva.fi','täällä keskustellaan vain asiasta!');
/*!40000 ALTER TABLE `keskustelu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `viesti`
--

DROP TABLE IF EXISTS `viesti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `viesti` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kirjoittaja` int(11) NOT NULL,
  `keskusteluid` int(11) NOT NULL,
  `otsikko` varchar(255) DEFAULT NULL,
  `viesti` text NOT NULL,
  `vastaus` int(11) DEFAULT NULL,
  `kirjoitettu` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `viimeksimuutettu` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `viesti_henkilo_fk` (`kirjoittaja`),
  KEY `viesti_keskustelu_fk` (`keskusteluid`),
  CONSTRAINT `viesti_henkilo_fk` FOREIGN KEY (`kirjoittaja`) REFERENCES `henkilo` (`hloid`),
  CONSTRAINT `viesti_keskustelu_fk` FOREIGN KEY (`keskusteluid`) REFERENCES `keskustelu` (`keskusteluid`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `viesti`
--

LOCK TABLES `viesti` WRITE;
/*!40000 ALTER TABLE `viesti` DISABLE KEYS */;
INSERT INTO `viesti` VALUES (1,1,1,'Tervetuloa','Tervetuloa keskustelemaan',NULL,'2017-11-07 09:11:24','2017-11-07 09:11:24'),(7,2,2,'koira1','tähän tulee koiratekstiä, ensimmäinen viesti',NULL,'2017-11-08 12:03:04','2017-11-08 12:03:04'),(13,1,4,NULL,'Kaikistta!',12,'2017-11-09 09:29:27','2017-11-09 09:29:27'),(15,1,2,NULL,'asdf',6,'2017-11-09 09:34:58','2017-11-09 09:34:58'),(16,5,2,NULL,'jjjjjj',6,'2017-11-09 09:35:22','2017-11-09 09:35:22'),(17,5,2,NULL,'asdf',6,'2017-11-09 09:35:25','2017-11-09 09:35:25'),(18,1,2,NULL,'Toinen vastaus',7,'2017-11-09 09:35:54','2017-11-09 09:35:54'),(19,1,2,NULL,'Kolmas vastaus!',7,'2017-11-09 09:36:00','2017-11-09 09:36:00'),(20,5,4,'Hyvä koodityyli!','Mika osaa tehdä selkeitä ohjeita!',NULL,'2017-11-09 09:36:55','2017-11-09 09:36:55'),(21,1,4,NULL,'No niin osaa!',20,'2017-11-09 09:37:08','2017-11-09 09:37:08'),(22,2,4,NULL,'En ole samaa mieltä :(',20,'2017-11-09 09:37:26','2017-11-09 09:37:26'),(23,1,3,'Samu on...','...Soveltolla työntekijänä!',NULL,'2017-11-09 11:41:57','2017-11-09 11:41:57'),(24,1,3,'ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss','ss\r\n',NULL,'2017-11-09 11:42:04','2017-11-09 11:42:04'),(25,1,3,'dddd','sss',NULL,'2017-11-09 11:42:30','2017-11-10 09:01:24'),(26,1,3,NULL,'',25,'2017-11-09 11:42:32','2017-11-09 11:42:32'),(27,1,3,NULL,'asdfasdfasf',25,'2017-11-09 11:42:40','2017-11-10 09:01:24'),(38,1,12,'!!!','Miksi miehillä on nenäkarvoja?',NULL,'2017-11-09 18:11:31','2017-11-09 18:11:31'),(39,2,12,NULL,'Etkö edes tuota tiedä?',38,'2017-11-09 18:11:46','2017-11-09 18:11:46'),(40,2,12,'Katso...','...<a href=\"http://www.vauva.fi\">Vauva.fi</a>',NULL,'2017-11-09 18:12:25','2017-11-09 18:12:25'),(41,5,12,NULL,'wanha',40,'2017-11-09 18:14:15','2017-11-09 18:14:15'),(42,9,12,NULL,'Mun lemppari!',40,'2017-11-09 18:14:54','2017-11-09 18:14:54'),(43,9,12,NULL,'Siellä osataan asioita!',40,'2017-11-09 18:15:04','2017-11-09 18:15:04'),(44,2,12,NULL,'Eikä osata!',40,'2017-11-09 18:15:29','2017-11-09 18:15:29'),(45,2,12,NULL,'pelkkiä äityleitä\'',40,'2017-11-09 18:15:45','2017-11-09 18:15:45'),(46,2,4,NULL,'Mika on mun idoli!',20,'2017-11-10 07:30:33','2017-11-10 07:30:33'),(47,2,4,NULL,'pöö',20,'2017-11-10 07:56:44','2017-11-10 07:56:44');
/*!40000 ALTER TABLE `viesti` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-11-10 11:03:25
