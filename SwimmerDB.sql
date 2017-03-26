DROP DATABASE IF EXISTS `SwimmerDB`;

CREATE DATABASE `SwimmerDB`;

USE `SwimmerDB`;

CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL ON SwimmerDB.* TO 'user'@'localhost' IDENTIFIED BY 'password';

CREATE TABLE `Swimmer`(

	`SwimmerId` INT NOT NULL AUTO_INCREMENT,
	`FirstName` NVARCHAR(20) NOT NULL,
    `MiddleName` NVARCHAR(20),
	`LastName` NVARCHAR(20) NOT NULL,
    `BirthDate` DATE,
    `Age` INT,
    `Gender` NVARCHAR(1) ,
    `TeamId` INT,
    `EventId` INT,
    CONSTRAINT `PK_Swimmer` PRIMARY KEY (`SwimmerId`)
);

CREATE TABLE `Event`(
	`EventId` INT NOT NULL AUTO_INCREMENT,
    `Stroke` NVARCHAR(20) NOT NULL,
    `Distance` INT NOT NULL,
    CONSTRAINT `PK_Event` PRIMARY KEY (`EventId`)
);

CREATE TABLE `Team`(
	`TeamId` INT NOT NULL AUTO_INCREMENT,
    `HeadCoach` NVARCHAR(40),
    `TeamName` NVARCHAR(120) NOT NULL,
    `SeasonWins` INT,
    `SeasonLoses` INT,
    `PoolStreet` NVARCHAR(70),
    `PoolNumber` INT,
    `PoolCity` NVARCHAR(40),
    `PoolState` NVARCHAR(40),
    `PoolCountry` NVARCHAR(40),
    CONSTRAINT `PK_Team` PRIMARY KEY (`TeamId`)
);

CREATE TABLE `Records`(
	`EventId` INT NOT NULL AUTO_INCREMENT,
    `SwimmerId`INT NOT NULL,
    `Time`INT NOT NULL,
    `SanctionNumber`INT NOT NULL,
    CONSTRAINT `PK_Records` PRIMARY KEY (`EventId`, `SwimmerId`, `SanctionNumber`)
);

CREATE TABLE `Meet`(
	`SanctionNumber` INT NOT NULL AUTO_INCREMENT,
    `Name` NVARCHAR(120) NOT NULL,
    `DateHeld` DATE,
    CONSTRAINT `PK_Meet` PRIMARY KEY (`SanctionNumber`)
);

ALTER TABLE `Swimmer` ADD CONSTRAINT `FK_SwimmerTeamId`
    FOREIGN KEY (`TeamId`) REFERENCES `Team` (`TeamId`) ON DELETE NO ACTION ON UPDATE NO ACTION;
    
ALTER TABLE `Swimmer` ADD CONSTRAINT `FK_SwimmerPrimaryEvent`
    FOREIGN KEY (`EventId`) REFERENCES `Event` (`EventId`) ON DELETE NO ACTION ON UPDATE NO ACTION;
    
ALTER TABLE `Records` ADD CONSTRAINT `FK_RecordEventId`
    FOREIGN KEY (`EventId`) REFERENCES `Event` (`EventId`) ON DELETE NO ACTION ON UPDATE NO ACTION;
    
ALTER TABLE `Records` ADD CONSTRAINT `FK_RecordSwimmerId`
    FOREIGN KEY (`SwimmerId`) REFERENCES `Swimmer` (`SwimmerId`) ON DELETE NO ACTION ON UPDATE NO ACTION;
    
ALTER TABLE `Records` ADD CONSTRAINT `FK_RecordSanctionNumber`
    FOREIGN KEY (`SanctionNumber`) REFERENCES `Meet` (`SanctionNumber`) ON DELETE NO ACTION ON UPDATE NO ACTION;
    
INSERT INTO `Team` (`HeadCoach`,`TeamName`,`PoolStreet`,`PoolNumber`,`PoolCity`,`PoolState`,`PoolCountry`) VALUES ('Bob Bowman', 'USA', 'Pennsylvania Ave NW', '1600', 'D.C.', 'Maryland','United States');
INSERT INTO `Team` (`HeadCoach`,`TeamName`,`PoolStreet`,`PoolNumber`,`PoolCity`,`PoolCountry`) VALUES ('Jacco Verhaeren', 'Australia', 'Olympic Blvc', '1600','Sydney Olympic Park', 'Australia');

INSERT INTO `Meet` (`DateHeld`,`Name`) VALUES ('2014/8/21','Pan Pacific Swimming Championships');

INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Freestyle','50');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Freestyle','100');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Freestyle','200');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Freestyle','400');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Freestyle','1500');   -- 5
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Backstroke','100');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Backstroke','200');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Breaststroke','100');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Breaststroke','200');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Butterfly','100');		-- 10
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Butterfly','200');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Medley','200');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('Medley','400');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('FreestyleRelay','400');
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('FreestyleRelay','800');		-- 15
INSERT INTO `Event` (`Stroke`,`Distance`) VALUES ('MedleyRelay','400');

INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`, `TeamId`,`EventId`) VALUES ('Michael','Fred','Phelps', '1985/6/30','31','M','1','10');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Ryan','Steven','Lochte', '1984/10/3','32','M','1','10');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Aaron','Wells','Peirsol', '1983/7/23','33','M','1','6');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Brendan','Joseph','Hansen', '1981/8/15','35','M','1','8');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Jason','Edward','Lezak', '1975/11/12','41','M','1','1');	-- 5
INSERT INTO `Swimmer` (`FirstName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Lenoid','Krayzelburg', '1975/9/28','41','M','1','6');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Glenn','Edward','Moses', '1980/6/7','36','M','1','8');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Ian','Lowell','Crocker', '1982/8/31','34','M','1','10');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Leisel','Marie','Jones', '1985/8/30','31','F','2','14');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Samantha','Linette','Riley', '1972/11/13','44','F','2','8');		-- 10
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Stephanie','Louise','Rice', '1988/6/17','31','F','2','12');
INSERT INTO `Swimmer` (`FirstName`,`MiddleName`,`LastName`,`BirthDate`,`Age`,`Gender`,`TeamId`,`EventId`) VALUES ('Grant','George','Hackett', '1980/5/9','36','M','2','5');

INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES ('10','9', '50','1');
INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES ('10','10', '56','1');
INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES ('10','11', '49','1');
INSERT INTO `Records` (`EventId`, `SwimmerId`, `Time`, `SanctionNumber`) VALUES ('10','12', '42','1');