/*
 * Copyright (C) 2016-2019 Code Defenders contributors
 *
 * This file is part of Code Defenders.
 *
 * Code Defenders is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Code Defenders is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Code Defenders. If not, see <http://www.gnu.org/licenses/>.
 */
package org.codedefenders.database;

import org.codedefenders.game.GameClass;
import org.codedefenders.game.GameLevel;
import org.codedefenders.game.GameState;
import org.codedefenders.game.Role;
import org.codedefenders.game.puzzle.Puzzle;
import org.codedefenders.game.puzzle.PuzzleChapter;
import org.codedefenders.game.puzzle.PuzzleGame;
import org.codedefenders.validation.code.CodeValidatorLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * This class handles the database logic for puzzles.
 *
 * @see Puzzle
 * @see PuzzleChapter
 * @see PuzzleGame
 */
public class PuzzleDAO {
    private static final Logger logger = LoggerFactory.getLogger(PuzzleDAO.class);

    /**
     * Returns the {@link PuzzleChapter} for the given chapter ID.
     *
     * @param chapterId The chapter ID.
     * @return The {@link PuzzleChapter} for the given chapter ID.
     */
    public static PuzzleChapter getPuzzleChapterForId(int chapterId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM puzzle_chapters",
                "WHERE Chapter_ID = ?;"
        );

        return DB.executeQueryReturnValue(query, PuzzleDAO::getPuzzleChapterFromResultSet, DatabaseValue.of(chapterId));
    }

    /**
     * Returns a {@link List} of all {@link PuzzleChapter PuzzleChapters}, sorted by the position in the chapter list.
     *
     * @return A {@link List} of all {@link PuzzleChapter PuzzleChapters}, sorted by the position in the chapter list.
     */
    public static List<PuzzleChapter> getPuzzleChapters() {
        String query = String.join("\n",
                "SELECT *",
                "FROM puzzle_chapters",
                "ORDER BY Position;"
        );

        return DB.executeQueryReturnList(query, PuzzleDAO::getPuzzleChapterFromResultSet);
    }

    /**
     * Returns the {@link Puzzle} for the given puzzle ID.
     *
     * @param puzzleId The puzzle ID.
     * @return The {@link Puzzle} for the given puzzle ID.
     */
    public static Puzzle getPuzzleForId(int puzzleId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM puzzles",
                "WHERE Puzzle_ID = ?;"
        );

        return DB.executeQueryReturnValue(query, PuzzleDAO::getPuzzleFromResultSet, DatabaseValue.of(puzzleId));
    }

    /**
     * Returns a {@link List} of all {@link Puzzle Puzzles}, sorted by the chapter ID and position in the chapter.
     *
     * @return A {@link List} of all {@link Puzzle Puzzles}, sorted by the chapter ID and position in the chapter.
     */
    public static List<Puzzle> getPuzzles() {
        String query = String.join("\n",
                "SELECT *",
                "FROM puzzles",
                "ORDER BY Chapter_ID, Position;"
        );

        return DB.executeQueryReturnList(query, PuzzleDAO::getPuzzleFromResultSet);
    }

    /**
     * Returns a {@link List} of all {@link Puzzle Puzzles} in the given {@link PuzzleChapter}, sorted by the position
     * in the chapter.
     *
     * @param chapterId The chapter ID.
     * @return A {@link List} of all {@link Puzzle Puzzles} in the given {@link PuzzleChapter}, sorted by the position
     * in the chapter.
     */
    public static List<Puzzle> getPuzzlesForChapterId(int chapterId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM puzzles",
                "WHERE Chapter_ID = ?",
                "ORDER BY Position;"
        );

        return DB.executeQueryReturnList(query, PuzzleDAO::getPuzzleFromResultSet, DatabaseValue.of(chapterId));
    }

    /**
     * Returns the {@link PuzzleGame} for the given game ID.
     *
     * @param gameId The game ID.
     * @return The {@link PuzzleGame} for the given game ID.
     */
    public static PuzzleGame getPuzzleGameForId(int gameId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM view_puzzle_games games",
                "WHERE ID = ?;"
        );

        return DB.executeQueryReturnValue(query, PuzzleDAO::getPuzzleGameFromResultSet, DatabaseValue.of(gameId));
    }

    /**
     * Returns the {@link PuzzleGame} that represents the latest try on the given puzzle by the given user.
     *
     * @param puzzleId The puzzle ID.
     * @param userId   The user ID.
     * @return The {@link PuzzleGame} that represents the latest try on the given puzzle by the given user.
     */
    public static PuzzleGame getLatestPuzzleGameForPuzzleAndUser(int puzzleId, int userId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM view_puzzle_games games",
                "WHERE Puzzle_ID = ?",
                "  AND Creator_ID = ?",
                "ORDER BY Timestamp DESC;"
        );

        return DB.executeQueryReturnValue(query, PuzzleDAO::getPuzzleGameFromResultSet, DatabaseValue.of(puzzleId), DatabaseValue.of(userId));
    }

    /**
     * Returns a {@link List} of {@link PuzzleGame PuzzleGames} that represents the tries on the given puzzle by the
     * given user. The list is sorted by the the timestamp of the games.
     *
     * @param puzzleId The puzzle ID.
     * @param userId   The user ID.
     * @return A {@link List} of {@link PuzzleGame PuzzleGames} that represents the tries on the given puzzle by the
     * given user. The list is sorted by the the timestamp of the games.
     */
    public static List<PuzzleGame> getPuzzleGamesForPuzzleAndUser(int puzzleId, int userId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM view_puzzle_games games",
                "WHERE Puzzle_ID = ?",
                "  AND Creator_ID = ?",
                "ORDER BY Timestamp DESC;"
        );

        return DB.executeQueryReturnList(query, PuzzleDAO::getPuzzleGameFromResultSet, DatabaseValue.of(puzzleId), DatabaseValue.of(userId));
    }

    /**
     * Returns a {@link List} of the active {@link PuzzleGame PuzzleGames} played by the given user.
     * The list is sorted by the the timestamp of the games.
     *
     * @param userId The user ID.
     * @return A {@link List} of the active {@link PuzzleGame PuzzleGames} played by the given user.
     * The list is sorted by the the timestamp of the games.
     */
    public static List<PuzzleGame> getActivePuzzleGamesForUser(int userId) {
        String query = String.join("\n",
                "SELECT *",
                "FROM view_puzzle_games games",
                "WHERE State = 'ACTIVE'",
                "  AND Creator_ID = ?",
                "ORDER BY Timestamp DESC;"
        );

        return DB.executeQueryReturnList(query, PuzzleDAO::getPuzzleGameFromResultSet, DatabaseValue.of(userId));
    }

    /**
     * Stores the given {@link Puzzle} in the database.
     *
     * @param puzzle The {@link Puzzle}.
     * @return The ID of the stored puzzle, or -1 if the insert failed.
     */
    public static int storePuzzle(Puzzle puzzle) {
        String query = String.join("\n",
                "INSERT INTO puzzles",

                "(Class_ID,",
                "Active_Role,",
                "Level,",
                "Max_Assertions,",
                "Force_Hamcrest,",
                "Mutant_Validator_Level,",
                "Editable_Lines_Start,",
                "Editable_Lines_End,",
                "Chapter_ID,",
                "Position,",
                "Title,",
                "Description)",

                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );

        DatabaseValue[] values = new DatabaseValue[]{
                DatabaseValue.of(puzzle.getClassId()),
                DatabaseValue.of(puzzle.getActiveRole().toString()),
                DatabaseValue.of(puzzle.getLevel().toString()),
                DatabaseValue.of(puzzle.getMaxAssertionsPerTest()),
                DatabaseValue.of(puzzle.isForceHamcrest()),
                DatabaseValue.of(puzzle.getMutantValidatorLevel().toString()),
                DatabaseValue.of(puzzle.getEditableLinesStart()),
                DatabaseValue.of(puzzle.getEditableLinesEnd()),
                DatabaseValue.of(puzzle.getChapterId()),
                DatabaseValue.of(puzzle.getPosition()),
                DatabaseValue.of(puzzle.getTitle()),
                DatabaseValue.of(puzzle.getDescription())
        };

        return DB.executeUpdateQueryGetKeys(query, values);
    }

    /**
     * Stores the given {@link PuzzleChapter} in the database.
     *
     * @param chapter The {@link PuzzleChapter}.
     * @return The ID of the stored puzzle chapter, or -1 if the insert failed.
     */
    public static int storePuzzleChapter(PuzzleChapter chapter) {
        String query = String.join("\n",
                "INSERT INTO puzzle_chapters",

                "(Chapter_ID,",
                "Position,",
                "Title,",
                "Description)",

                "VALUES (?, ?, ?, ?);"
        );

        DatabaseValue[] values = new DatabaseValue[]{
                DatabaseValue.of(chapter.getChapterId()),
                DatabaseValue.of(chapter.getPosition()),
                DatabaseValue.of(chapter.getTitle()),
                DatabaseValue.of(chapter.getDescription()),
        };

        return DB.executeUpdateQueryGetKeys(query, values);
    }

    /**
     * Stores the given {@link PuzzleGame} in the database.
     *
     * @param game The {@link PuzzleGame}.
     * @return The game ID of the stored game, or -1 if the insert failed.
     */
    public static int storePuzzleGame(PuzzleGame game) {
        String query = String.join("\n",
                "INSERT INTO games",

                "(Class_ID,",
                "Level,",
                "Creator_ID,",
                "MaxAssertionsPerTest,",
                "ForceHamcrest,",
                "MutantValidator,",
                "State,",
                "CurrentRound,",
                "ActiveRole,",
                "Mode,",
                "Puzzle_ID)",

                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );

        DatabaseValue[] values = new DatabaseValue[]{
                DatabaseValue.of(game.getClassId()),
                DatabaseValue.of(game.getLevel().toString()),
                DatabaseValue.of(game.getCreatorId()),
                DatabaseValue.of(game.getMaxAssertionsPerTest()),
                DatabaseValue.of(game.isForceHamcrest()),
                DatabaseValue.of(game.getMutantValidatorLevel().toString()),
                DatabaseValue.of(game.getState().toString()),
                DatabaseValue.of(game.getCurrentRound()),
                DatabaseValue.of(game.getActiveRole().toString()),
                DatabaseValue.of(game.getMode().toString()),
                DatabaseValue.of(game.getPuzzleId()),
        };

        return DB.executeUpdateQueryGetKeys(query, values);
    }

    /**
     * Updates the given {@link PuzzleGame}'s values in the database.
     *
     * @param game The {@link PuzzleGame}.
     * @return {@code true} if the update was successful, {@code false}a otherwise.
     */
    public static boolean updatePuzzleGame(PuzzleGame game) {
        String query = String.join("\n",
                "UPDATE games",

                "SET Class_ID = ?,",
                "    Level = ?,",
                "    Creator_ID = ?,",
                "    MaxAssertionsPerTest = ?,",
                "    ForceHamcrest = ?,",
                "    MutantValidator = ?,",
                "    State = ?,",
                "    CurrentRound = ?,",
                "    ActiveRole = ?,",
                "    Puzzle_ID = ?",

                "WHERE ID = ?;"
        );

        DatabaseValue[] values = new DatabaseValue[]{
                DatabaseValue.of(game.getClassId()),
                DatabaseValue.of(game.getLevel().toString()),
                DatabaseValue.of(game.getCreatorId()),
                DatabaseValue.of(game.getMaxAssertionsPerTest()),
                DatabaseValue.of(game.isForceHamcrest()),
                DatabaseValue.of(game.getMutantValidatorLevel().toString()),
                DatabaseValue.of(game.getState().toString()),
                DatabaseValue.of(game.getCurrentRound()),
                DatabaseValue.of(game.getActiveRole().toString()),
                DatabaseValue.of(game.getPuzzleId()),
                DatabaseValue.of(game.getId()),
        };

        return DB.executeUpdateQuery(query, values);
    }

    /**
     * Creates a {@link PuzzleChapter} from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet}.
     * @return The created {@link PuzzleChapter}.
     */
    private static PuzzleChapter getPuzzleChapterFromResultSet(ResultSet rs) {
        try {
            int chapterId = rs.getInt("puzzle_chapters.Chapter_ID");

            Integer position = rs.getInt("puzzle_chapters.Position");
            if (rs.wasNull()) position = null;

            String title = rs.getString("puzzle_chapters.Title");
            String description = rs.getString("puzzle_chapters.Description");

            return new PuzzleChapter(chapterId, position, title, description);
        } catch (SQLException e) {
            logger.error("Caught SQL exception while checking ResultSet.", e);
            return null;
        }
    }

    /**
     * Creates a {@link Puzzle} from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet}.
     * @return The created {@link Puzzle}.
     */
    private static Puzzle getPuzzleFromResultSet(ResultSet rs) {
        try {
            int puzzleId = rs.getInt("puzzles.Puzzle_ID");
            int classId = rs.getInt("puzzles.Class_ID");
            Role activeRole = Role.valueOf(rs.getString("puzzles.Active_Role"));

            Integer chapterId = rs.getInt("puzzles.Chapter_ID");
            if (rs.wasNull()) chapterId = null;

            Integer position = rs.getInt("puzzles.Position");
            if (rs.wasNull()) position = null;

            String title = rs.getString("puzzles.Title");
            String description = rs.getString("puzzles.Description");

            GameLevel level = GameLevel.valueOf(rs.getString("puzzles.Level"));
            
            int maxAssertions = rs.getInt("Max_Assertions");
            boolean forceHamcrest = rs.getBoolean("Force_Hamcrest"); 
            
            CodeValidatorLevel mutantValidatorLevel = CodeValidatorLevel.valueOf(rs.getString("puzzles.Mutant_Validator_Level"));

            Integer editableLinesStart = rs.getInt("puzzles.Editable_Lines_Start");
            if (rs.wasNull()) editableLinesStart = null;

            Integer editableLinesEnd = rs.getInt("puzzles.Editable_Lines_End");
            if (rs.wasNull()) editableLinesEnd = null;

            return new Puzzle(puzzleId, classId, activeRole, level, maxAssertions, forceHamcrest, mutantValidatorLevel,
                    editableLinesStart, editableLinesEnd, chapterId, position, title, description);
        } catch (SQLException e) {
            logger.error("Caught SQL exception while checking ResultSet.", e);
            return null;
        }
    }

    /**
     * Creates a {@link PuzzleGame} from a {@link ResultSet}.
     *
     * @param rs The {@link ResultSet}.
     * @return The created {@link PuzzleGame}.
     */
    private static PuzzleGame getPuzzleGameFromResultSet(ResultSet rs) {
        try {
            GameClass cut = GameClassDAO.gameClassFromRS(rs);
            int gameId = rs.getInt("games.ID");
            int classId = rs.getInt("games.Class_ID");
            GameLevel level = GameLevel.valueOf(rs.getString("games.Level"));
            int creatorId = rs.getInt("games.Creator_ID");
            int maxAssertionsPerTest = rs.getInt("games.MaxAssertionsPerTest");
            boolean forceHamcrest = rs.getBoolean("games.ForceHamcrest");
            CodeValidatorLevel mutantValidatorLevel = CodeValidatorLevel.valueOf(rs.getString("games.MutantValidator"));
            GameState state = GameState.valueOf(rs.getString("games.State"));
            int currentRound = rs.getInt("games.CurrentRound");
            Role activeRole = Role.valueOf(rs.getString("games.ActiveRole"));
            int puzzleId = rs.getInt("games.Puzzle_ID");

            return new PuzzleGame(cut, puzzleId, gameId, classId, level, creatorId,
                maxAssertionsPerTest, forceHamcrest, mutantValidatorLevel, state, currentRound, activeRole);
        } catch (SQLException e) {
            logger.error("Caught SQL exception while checking ResultSet.", e);
            return null;
        }
    }
}
