/* Add "EquivalenceThreshold" column to games. */
ALTER TABLE puzzles ADD `Force_Hamcrest` tinyint(1) DEFAULT '0';

/* Update puzzle views */
CREATE OR REPLACE VIEW `view_battleground_games` AS
SELECT games.*, classes.Name, classes.JavaFile, classes.ClassFile, classes.Alias, classes.RequireMocking, classes.Active
FROM games,
     classes
WHERE Mode = 'PARTY'
  AND games.Class_ID = classes.Class_ID;

CREATE OR REPLACE VIEW `view_puzzle_games` AS
SELECT games.*, classes.Name, classes.JavaFile, classes.ClassFile, classes.Alias, classes.RequireMocking, classes.Active
FROM games,
     classes
WHERE Mode = 'PUZZLE'
  AND games.Class_ID = classes.Class_ID;