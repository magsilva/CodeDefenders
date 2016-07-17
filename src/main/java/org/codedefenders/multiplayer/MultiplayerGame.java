package org.codedefenders.multiplayer;

import org.apache.commons.lang.ArrayUtils;
import org.codedefenders.*;

import java.sql.*;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.codedefenders.Mutant.Equivalence.*;

public class MultiplayerGame extends AbstractGame {

	private int defenderValue;
	private int attackerValue;
	private float lineCoverage;
	private float mutantCoverage;
	private float price;
	private int attackerLimit;
	private int defenderLimit;
	private int minAttackers;
	private int minDefenders;
	private long startDateTime;
	private long finishDateTime;


	public void setId(int id) {
		this.id = id;
		if (this.state != State.FINISHED && finishDateTime < System.currentTimeMillis()){
			this.state = State.FINISHED;
			update();
		}
	}

	public int getDefenderValue() {
		return defenderValue;
	}

	public void setDefenderValue(int defenderValue) {
		this.defenderValue = defenderValue;
	}

	public int getAttackerValue() {
		return attackerValue;
	}

	public void setAttackerValue(int attackerValue) {
		this.attackerValue = attackerValue;
	}

	public float getLineCoverage() {
		return lineCoverage;
	}

	public void setLineCoverage(float lineCoverage) {
		this.lineCoverage = lineCoverage;
	}

	public float getMutantCoverage() {
		return mutantCoverage;
	}

	public void setMutantCoverage(float mutantCoverage) {
		this.mutantCoverage = mutantCoverage;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getStartDateTime() {
		Date date = new Date(startDateTime);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public String getFinishDateTime() {
		Date date = new Date(finishDateTime);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public MultiplayerGame(int classId, int creatorId, Level level,
	                       float lineCoverage, float mutantCoverage, float price,
	                       int defenderValue, int attackerValue, int defenderLimit,
	                       int attackerLimit, int minDefenders, int minAttackers,
	                       long startDateTime, long finishDateTime, String status) {
		this.classId = classId;
		this.creatorId = creatorId;
		this.level = level;
		this.mode = Mode.PARTY;
		this.lineCoverage = lineCoverage;
		this.mutantCoverage = mutantCoverage;
		this.price = price;
		this.defenderValue = defenderValue;
		this.attackerValue = attackerValue;
		this.defenderLimit = defenderLimit;
		this.attackerLimit = attackerLimit;
		this.minDefenders = minDefenders;
		this.minAttackers = minAttackers;
		this.state = State.valueOf(status);
		this.startDateTime = startDateTime;
		this.finishDateTime = finishDateTime;
	}

	public ArrayList<MultiplayerMutant> getMutants() {
		int[] attackers = getPlayerIds();
		return DatabaseAccess.getMutantsForAttackers(attackers);
	}

	public ArrayList<MultiplayerMutant> getAliveMutants() {
		ArrayList<MultiplayerMutant> aliveMutants = new ArrayList<>();
		for (MultiplayerMutant m : getMutants()) {
			if (m.isAlive() && m.getEquivalent().equals(Mutant.Equivalence.ASSUMED_NO) && (m.getClassFile() != null)) {
				aliveMutants.add(m);
			}
		}
		return aliveMutants;
	}

	public ArrayList<MultiplayerMutant> getKilledMutants() {
		ArrayList<MultiplayerMutant> killedMutants = new ArrayList<>();
		for (MultiplayerMutant m : getMutants()) {
			if (!m.isAlive() && (m.getClassFile() != null)) {
				killedMutants.add(m);
			}
		}
		return killedMutants;
	}

	public ArrayList<MultiplayerMutant> getMutantsMarkedEquivalent() {
		ArrayList<MultiplayerMutant> equivMutants = new ArrayList<>();
		for (MultiplayerMutant m : getMutants()) {
			if (!m.getEquivalent().equals(ASSUMED_NO) && !m.getEquivalent().equals(PROVEN_NO)) {
				equivMutants.add(m);
			}
		}
		return equivMutants;
	}

	public MultiplayerMutant getMutantByID(int mutantID) {
		for (MultiplayerMutant m : getMutants()) {
			if (m.getId() == mutantID)
				return m;
		}
		return null;
	}

	public ArrayList<Test> getTests() {
		return getExecutableTests();
	}

	public ArrayList<Test> getExecutableTests() {
		ArrayList<Test> allTests = new ArrayList<>();
		int[] defenders = getPlayerIds();
		for (int i = 0; i < defenders.length; i++){
			ArrayList<Test> tests = DatabaseAccess.getExecutableTestsForMultiplayerGame(defenders[i]);
			allTests.addAll(tests);
		}
		return allTests;
	}

	public int[] getDefenderIds(){
		return DatabaseAccess.getDefendersForMultiplayerGame(getId());
	}

	public int[] getAttackerIds(){
		return DatabaseAccess.getAttackersForMultiplayerGame(getId());
	}

	public int[] getPlayerIds() { return ArrayUtils.addAll(getDefenderIds(), getAttackerIds());}

	public boolean addPlayer(int userId, Role role) {
		if (state != State.FINISHED && canJoinGame(role)) {
			String sql = String.format("INSERT INTO players " +
							"(Game_ID, User_ID, Points, Role) VALUES " +
							"(%d, %d, 0, '%s');",
					id, userId, role);

			return runStatement(sql);
		}
		return false;
	}

	private boolean canJoinGame(Role role) {
		if (role.equals(Role.ATTACKER))
			return (attackerLimit == 0 || getAttackerIds().length < attackerLimit);
		else
			return (defenderLimit == 0 || getDefenderIds().length < defenderLimit);
	}

	public boolean insert() {

		Connection conn = null;
		Statement stmt = null;
		String sql = null;

		// Attempt to insert game info into database
		try {
			conn = DatabaseAccess.getConnection();

			stmt = conn.createStatement();
			sql = String.format("INSERT INTO games " +
					"(Class_ID, Level, Price, Defender_Value, Attacker_Value, Coverage_Goal, Mutant_Goal, Creator_ID, " +
					"Attackers_Needed, Defenders_Needed, Attackers_Limit, Defenders_Limit, Start_Time, Finish_Time, State, Mode) VALUES " +
					"('%s', 	'%s', '%f', 	'%d',			'%d',			'%f',			'%f',		'%d'," +
					"'%d',				'%d',				'%d',			'%d',			'%s', '%s',		'%s', 'PARTY');",
					classId, level.name(), price, defenderValue, attackerValue, lineCoverage, mutantCoverage, creatorId,
					minAttackers, minDefenders, attackerLimit, defenderLimit, new Timestamp(startDateTime), new Timestamp(finishDateTime), state.name());

			stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
				stmt.close();
				conn.close();
				return true;
			}

		} catch (SQLException se) {
			System.out.println(se);
			//Handle errors for JDBC
		} catch (Exception e) {
			System.out.println(e);
			//Handle errors for Class.forName
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do

			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				System.out.println(se);
			}//end finally try
		} //end try

		return false;
	}

	public boolean update() {

		Connection conn = null;
		Statement stmt = null;
		String sql = null;

		try {
			conn = DatabaseAccess.getConnection();

			// Get all rows from the database which have the chosen username
			stmt = conn.createStatement();
				sql = String.format("UPDATE games SET " +
						"Class_ID = '%s', Level = '%s', Price = %f, Defender_Value=%d, Attacker_Value=%d, Coverage_Goal=%f" +
						", Mutant_Goal=%f, State='%s' WHERE ID='%d'",
						classId, level.name(), price, defenderValue, attackerValue, lineCoverage, mutantCoverage, state.name(), id);
			stmt.execute(sql);
			return true;

		} catch (SQLException se) {
			System.out.println(se);
			//Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			System.out.println(e);
			//Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			//finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do

			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}//end finally try
		} //end try

		return false;
	}

	public class PlayerScore {
		private int playerId;
		private int totalScore;
		private int quantity;
		private String additionalInformation;

		public String getAdditionalInformation() {
			return additionalInformation;
		}

		public void setAdditionalInformation(String additionalInformation) {
			this.additionalInformation = additionalInformation;
		}

		public PlayerScore(int playerId) {
			this.playerId = playerId;
			this.totalScore = 0;
			this.quantity = 0;
		}

		public int getPlayerId() {
			return playerId;
		}

		public int getTotalScore() {
			return totalScore;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setPlayerId(int playerId) {
			this.playerId = playerId;
		}

		public void increaseTotalScore(int score) {
			this.totalScore += score;
		}

		public void increaseQuantity() {
			quantity++;
		}
	}

	public HashMap<Integer, PlayerScore> getMutantScores(){
		HashMap<Integer, PlayerScore> mutantScores = new HashMap<Integer, PlayerScore>();

		HashMap<Integer, Integer> mutantsAlive = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> mutantsKilled = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> mutantsEquiv = new HashMap<Integer, Integer>();

		ArrayList<MultiplayerMutant> allMutants = new ArrayList<MultiplayerMutant>();
		allMutants.addAll(getAliveMutants());
		allMutants.addAll(getKilledMutants());
		for (MultiplayerMutant mm : getMutantsMarkedEquivalent()){
			if (!mm.getEquivalent().equals(Mutant.Equivalence.DECLARED_YES) && !mm.getEquivalent().equals(Mutant.Equivalence.ASSUMED_YES)){
				allMutants.add(mm);
			}
		}


		for (MultiplayerMutant mm : allMutants){
			if (!mutantScores.containsKey(mm.getPlayerId())){
				mutantScores.put(mm.getPlayerId(), new PlayerScore(mm.getPlayerId()));
				mutantsAlive.put(mm.getPlayerId(), 0);
				mutantsEquiv.put(mm.getPlayerId(), 0);
				mutantsKilled.put(mm.getPlayerId(), 0);
			}

			PlayerScore ps = mutantScores.get(mm.getPlayerId());
			ps.increaseQuantity();
			ps.increaseTotalScore(mm.getScore());

			if (mm.getEquivalent().equals(ASSUMED_YES) || mm.getEquivalent().equals(DECLARED_YES)){
				mutantsEquiv.put(mm.getPlayerId(), mutantsEquiv.get(mm.getPlayerId())+1);
			} else if (mm.isAlive()){
				mutantsAlive.put(mm.getPlayerId(), mutantsAlive.get(mm.getPlayerId())+1);
			} else {
				mutantsKilled.put(mm.getPlayerId(), mutantsKilled.get(mm.getPlayerId())+1);
			}

		}

		for (int i : mutantsKilled.keySet()){
			PlayerScore ps = mutantScores.get(i);
			ps.setAdditionalInformation(mutantsAlive.get(i) + ", " + mutantsKilled.get(i) + ", " + mutantsKilled.get((i)));
		}

		return mutantScores;
	}

	public HashMap<Integer, PlayerScore> getTestScores(){
		HashMap<Integer, PlayerScore> testScores = new HashMap<Integer, PlayerScore>();
		HashMap<Integer, Integer> mutantsKilled = new HashMap<Integer, Integer>();
		for (Test tt : getTests()){
			if (!testScores.containsKey(tt.getPlayerId())){
				testScores.put(tt.getPlayerId(), new PlayerScore(tt.getPlayerId()));
				mutantsKilled.put(tt.getPlayerId(), 0);
			}
			PlayerScore ps = testScores.get(tt.getPlayerId());
			ps.increaseQuantity();
			ps.increaseTotalScore(tt.getScore());

			mutantsKilled.put(tt.getPlayerId(), mutantsKilled.get(tt.getPlayerId())+1);

		}

		for (int i : mutantsKilled.keySet()){
			PlayerScore ps = testScores.get(i);
			ps.setAdditionalInformation("" + mutantsKilled.get(i));
		}

		return testScores;
	}
}
