-- phpMyAdmin SWFM Dump
-- version 4.7.7
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Creato il: Feb 12, 2018 alle 10:48
-- Versione del server: 5.7.21
-- Versione PHP: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `SWFM_DB`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `swfm_crash`
--

CREATE TABLE `swfm_crash` (
  `id` int(11) NOT NULL,
  `crashtime` datetime NOT NULL,
  `intensity` int(11) NOT NULL,
  `stationary` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `swfm_crash`
--

INSERT INTO `swfm_crash` (`id`, `crashtime`, `intensity`, `stationary`) VALUES
(1, '2018-01-07 07:00:00', 2, 0),
(2, '2018-01-02 02:00:00', 2, 0),
(3, '2018-01-07 01:00:00', 2, 1),
(3, '2018-01-07 03:00:00', 2, 1),
(3, '2018-01-07 04:00:00', 4, 1),
(3, '2018-01-07 05:00:00', 3, 1),
(3, '2018-01-08 02:03:00', 3, 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `swfm_evaluation`
--

CREATE TABLE `swfm_evaluation` (
  `id` int(11) NOT NULL,
  `pointstotal` int(11) NOT NULL DEFAULT '0',
  `pointsacceleration` int(11) NOT NULL DEFAULT '0',
  `pointsbraking` int(11) NOT NULL DEFAULT '0',
  `pointssteering` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `swfm_evaluation`
--

INSERT INTO `swfm_evaluation` (`id`, `pointstotal`, `pointsacceleration`, `pointsbraking`, `pointssteering`) VALUES
(1, 50, 49, 51, 48),
(2, 49, 51, 49, 52),
(3, 40, 50, 40, 55);

-- --------------------------------------------------------

--
-- Struttura della tabella `swfm_resume`
--

CREATE TABLE `swfm_resume` (
  `email` varchar(254) NOT NULL,
  `pointstotal` int(11) NOT NULL DEFAULT '0',
  `pointsacceleration` int(11) NOT NULL DEFAULT '0',
  `pointsbraking` int(11) NOT NULL DEFAULT '0',
  `pointssteering` int(11) NOT NULL DEFAULT '0',
  `drivenhours` float NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `swfm_resume`
--

INSERT INTO `swfm_resume` (`email`, `pointstotal`, `pointsacceleration`, `pointsbraking`, `pointssteering`, `drivenhours`) VALUES
('gabriele_serra@hotmail.it', 61, 41, 39, 82, 3.5);

-- --------------------------------------------------------

--
-- Struttura della tabella `swfm_stat`
--

CREATE TABLE `swfm_stat` (
  `id` int(11) NOT NULL,
  `numberacc` int(11) NOT NULL DEFAULT '0',
  `worstacc` int(11) NOT NULL DEFAULT '0',
  `numberbra` int(11) NOT NULL DEFAULT '0',
  `worstbra` int(11) NOT NULL DEFAULT '0',
  `numbercur` int(11) NOT NULL DEFAULT '0',
  `worstcur` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `swfm_stat`
--

INSERT INTO `swfm_stat` (`id`, `numberacc`, `worstacc`, `numberbra`, `worstbra`, `numbercur`, `worstcur`) VALUES
(1, 1, 2, 1, 5, 3, 0),
(2, 0, 0, 1, 4, 0, 0),
(3, 2, 5, 3, 1, 5, 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `swfm_trip`
--

CREATE TABLE `swfm_trip` (
  `id` int(11) NOT NULL,
  `email` varchar(254) NOT NULL,
  `starttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `secondslength` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `swfm_trip`
--

INSERT INTO `swfm_trip` (`id`, `email`, `starttime`, `secondslength`) VALUES
(1, 'gabriele_serra@hotmail.it', '2018-01-07 06:30:00', 120),
(2, 'gabriele_serra@hotmail.it', '2018-01-07 12:32:00', 90),
(3, 'gabriele_serra@hotmail.it', '2018-01-05 04:15:00', 180);

-- --------------------------------------------------------

--
-- Struttura della tabella `swfm_user`
--

CREATE TABLE `swfm_user` (
  `email` varchar(254) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(32) NOT NULL,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  `avatar` varchar(255) NOT NULL DEFAULT 'avatar-default.png',
  `cover` varchar(255) NOT NULL DEFAULT 'cover-default.png',
  `admin` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `swfm_user`
--

INSERT INTO `swfm_user` (`email`, `username`, `password`, `name`, `surname`, `avatar`, `cover`, `admin`) VALUES
('ciabbi94@live.it', 'ciabbi94', '63a9f0ea7bb98050796b649e85481845', 'Silvio', 'Bacci', 'avatar-ciabbi94.jpg', 'cover-ciabbi94.jpg', 1),
('gabriele_serra@hotmail.it', 'gabriserra', 'dc647eb65e6711e155375218212b3964', 'Gabriele', 'Serra', 'avatar-gabriserra.png', 'cover-gabriserra.png', 0);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `swfm_crash`
--
ALTER TABLE `swfm_crash`
  ADD PRIMARY KEY (`id`,`crashtime`);

--
-- Indici per le tabelle `swfm_evaluation`
--
ALTER TABLE `swfm_evaluation`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `swfm_resume`
--
ALTER TABLE `swfm_resume`
  ADD PRIMARY KEY (`email`);

--
-- Indici per le tabelle `swfm_stat`
--
ALTER TABLE `swfm_stat`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `swfm_trip`
--
ALTER TABLE `swfm_trip`
  ADD PRIMARY KEY (`id`),
  ADD KEY `email` (`email`);

--
-- Indici per le tabelle `swfm_user`
--
ALTER TABLE `swfm_user`
  ADD PRIMARY KEY (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `swfm_trip`
--
ALTER TABLE `swfm_trip`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `swfm_crash`
--
ALTER TABLE `swfm_crash`
  ADD CONSTRAINT `swfm_crash_ibfk_1` FOREIGN KEY (`id`) REFERENCES `swfm_trip` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `swfm_evaluation`
--
ALTER TABLE `swfm_evaluation`
  ADD CONSTRAINT `swfm_evaluation_ibfk_1` FOREIGN KEY (`id`) REFERENCES `swfm_trip` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `swfm_resume`
--
ALTER TABLE `swfm_resume`
  ADD CONSTRAINT `swfm_resume_ibfk_1` FOREIGN KEY (`email`) REFERENCES `swfm_user` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `swfm_stat`
--
ALTER TABLE `swfm_stat`
  ADD CONSTRAINT `swfm_stat_ibfk_1` FOREIGN KEY (`id`) REFERENCES `swfm_trip` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `swfm_trip`
--
ALTER TABLE `swfm_trip`
  ADD CONSTRAINT `swfm_trip_ibfk_1` FOREIGN KEY (`email`) REFERENCES `swfm_user` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
