package cricbuzzLLD;
/*We will focus on the following set of requirements while designing Cricinfo:
1. The system should keep track of all the cricket playing teams and their matches.
2. The system should show live ball-by-ball commentary of cricket matches.
3. All international cricket’s rules should be followed.
4. Any team playing a tournament will announce a squad (a set of players) for the
tournament.
5. For each match, both teams will announce their playing-eleven from the tournament
squad.
6. The system should be able to record stats about players, matches, and tournaments.
7. The system should be able to answer global stats queries like, who is the highest
wicket taker of all time? who has scored maximum numbers of 100s in test matches?
etc.
8. The system should keep track of all ODI, Test and T20 matches.
*/
import java.util.*;

public class Cricbuzz {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CricbuzzSystem cricbuzzSystem = CricbuzzSystem.getInstance();
		Player player1 = new Player("Virat Kohli", "P001", PlayerType.BATSMAN, null, BattingStyle.RIGHT_HANDED, new HashMap<>());
        Player player2 = new Player("Rohit Sharma", "P002", PlayerType.BATSMAN, null, BattingStyle.RIGHT_HANDED, new HashMap<>());
        Player player3 = new Player("Jasprit Bumrah", "P003", PlayerType.BALLER, BowlingStyle.RIGHT_ARM_FAST, null, new HashMap<>());
        Player player4 = new Player("MS Dhoni", "P004", PlayerType.WICKET_KEEPER, null, BattingStyle.RIGHT_HANDED, new HashMap<>());
        
        Player player5 = new Player("David Warner", "P005", PlayerType.BATSMAN, null, BattingStyle.LEFT_HANDED, new HashMap<>());
        Player player6 = new Player("Steve Smith", "P006", PlayerType.BATSMAN, null, BattingStyle.RIGHT_HANDED, new HashMap<>());
        Player player7 = new Player("Mitchell Starc", "P007", PlayerType.BALLER, BowlingStyle.LEFT_ARM_FAST, null, new HashMap<>());
        Player player8 = new Player("Alex Carey", "P008", PlayerType.WICKET_KEEPER, null, BattingStyle.LEFT_HANDED, new HashMap<>());

        // Create teams
        List<Player> teamAPlayers = Arrays.asList(player1, player2, player3, player4);
        List<Player> teamBPlayers = Arrays.asList(player5, player6, player7, player8);

        Team teamA = new Team();
        teamA.setName("India");
        teamA.setTeamID("T001");
        teamA.setPlayersList(teamAPlayers);

        Team teamB = new Team();
        teamB.setName("Australia");
        teamB.setTeamID("T002");
        teamB.setPlayersList(teamBPlayers);

        // Create match
        Match match = new Match("India vs Australia", "M001", teamA, teamB, true);
        Tournament WorldCup = new Tournament("WorldCup", "WC2027");
        cricbuzzSystem.addTornament(WorldCup);
        cricbuzzSystem.addMatch(match, WorldCup);
        cricbuzzSystem.setOpeningBowlerBatsman("M001", "WC2027", Arrays.asList(player1, player2), player7);
        cricbuzzSystem.startMatch(match, "WC2027");
        cricbuzzSystem.executeCommand("WC2027", "M001", CommandType.WIDE);
	}
}

enum CommandType {
	WIDE,
	ONE_RUN,
	TWO_RUN,
	THREE_RUN,
	FOUR,
	SIX
}
class CommandFactory {

	public static Command createCommand(CommandType cType, Match match) {
		// TODO Auto-generated method stub
		switch(cType) {
		case WIDE:
			return new WideCommand(match);
		default:
			return null;		
		}
		
	}
}
class CricbuzzSystem {
    private static CricbuzzSystem instance;

    private TournamentController tournamentController;
    private CommandRunner commandRunner = new CommandRunner();

    // Private constructor to prevent instantiation
    private CricbuzzSystem() {
        this.tournamentController = TournamentController.getInstance();
    }


	public void setOpeningBowlerBatsman(String matchID, String tourID, List<Player> asList, Player player7) {
		// TODO Auto-generated method stub
		Tournament tour = tournamentController.getTournament(tourID);
		Match match = tour.getMatchByID(matchID);
		match.getInnings().get(0).getScorecard().setCurrentBatters(asList);
		match.getInnings().get(0).getScorecard().setCurrentBowler(player7);;
	}


	public void executeCommand(String tourID, String matchID, CommandType cType) {
		// TODO Auto-generated method stub
		Tournament tour = tournamentController.getTournament(tourID);
		Match match = tour.getMatchByID(matchID);
		Command currentCommand = CommandFactory.createCommand(cType, match);
		commandRunner.executeCommand(currentCommand);
	}


	public void startMatch(Match match, String tourID) {
		// TODO Auto-generated method stub
		tournamentController.startmatch(match, tourID);
	}


	public void addMatch(Match match, Tournament worldCup) {
		// TODO Auto-generated method stub
    	tournamentController.addMatch(match, worldCup);
	}

	public void addTornament(Tournament worldCup) {
		// TODO Auto-generated method stub
    	tournamentController.addTournament(worldCup.getTourID(), worldCup);
	}

	// Public method to provide access to the single instance
    public static synchronized CricbuzzSystem getInstance() {
        if (instance == null) {
            instance = new CricbuzzSystem();
        }
        return instance;
    }
}

class TournamentController {
    private static TournamentController instance;

    private Map<String, Tournament> tournamentMap;

    // Private constructor to prevent instantiation
    private TournamentController() {
        this.tournamentMap = new HashMap<>();
    }

    public void startmatch(Match match, String tourID) {
		// TODO Auto-generated method stub
    	Tournament cur = tournamentMap.get(tourID);
    	cur.startMatch(match);
	}

	public void addMatch(Match match, Tournament worldCup) {
		// TODO Auto-generated method stub
    	worldCup.addMatch(match);
	}

	// Public method to provide access to the single instance
    public static synchronized TournamentController getInstance() {
        if (instance == null) {
            instance = new TournamentController();
        }
        return instance;
    }

    // Example methods to manage tournaments
    public void addTournament(String name, Tournament tournament) {
        tournamentMap.put(name, tournament);
    }

    public Tournament getTournament(String ID) {
        return tournamentMap.get(ID);
    }
}

class Tournament {
	private String name;
	private String tourID;
	private Map<String, Match> matches;
	public Tournament(String name, String tourID) {
		super();
		this.name = name;
		this.tourID = tourID;
		this.matches = new HashMap<String, Match>();
	}
	public void startMatch(Match match) {
		// TODO Auto-generated method stub
		match.start();
	}
	public void addMatch(Match match) {
		// TODO Auto-generated method stub
		matches.putIfAbsent(match.getMatchID(), match);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTourID() {
		return tourID;
	}
	public void setTourID(String tourID) {
		this.tourID = tourID;
	}
	public Map<String, Match> getMatches() {
		return matches;
	}
	public void setMatches(Map<String, Match> matches) {
		this.matches = matches;
	}
	public Match getMatchByID(String id) {
		return matches.get(id);
	}
}

class CommandRunner {
	public void executeCommand(Command command) {
		command.execute(command);
	}
}
interface Command {
	public void execute(Command command);
}

class WideCommand implements Command {
	private Match currentMatch;
	
	public WideCommand(Match currentMatch) {
		this.currentMatch = currentMatch;
	}

	@Override
	public void execute(Command command) {
		// TODO Auto-generated method stub
		currentMatch.executeWide();
	}
	
}
enum PlayerType {
	BATSMAN,
	BALLER,
	ALL_ROUNDAR,
	WICKET_KEEPER,
	CAPTAIN
}

enum BattingStyle{
	RIGHT_HANDED,
	LEFT_HANDED
}

enum BowlingStyle {
	RIGHT_ARM_FAST,
	LEFT_ARM_FAST,
	RIGHT_ARM_MEDIUM,
	LEFT_ARM_SLOW,
	LEG_BREAK
}

enum Format {
	ODI,
	T20,
	TEST
}

class Stats {
	private int runs;
	private int wickets;
	private Format format;
	public Stats(Format format) {
		this.runs = 0;
		this.wickets = 0;
		this.format = format;
	}
	public int getRuns() {
		return runs;
	}
	public void setRuns(int runs) {
		this.runs = runs;
	}
	public int getWickets() {
		return wickets;
	}
	public void setWickets(int wickets) {
		this.wickets = wickets;
	}
	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
}

class Player {
	private String name;
	private String playerID;
	private PlayerType playerType;
	private BowlingStyle bowlingStyle;
	private BattingStyle battingStyle;
	private Map<Format, Stats> statsByFormat;
	public Player(String name, String playerID, PlayerType playerType, BowlingStyle bowlingStyle,
			BattingStyle battingStyle, Map<Format, Stats> statsByFormat) {
		super();
		this.name = name;
		this.playerID = playerID;
		this.playerType = playerType;
		this.bowlingStyle = bowlingStyle;
		this.battingStyle = battingStyle;
		this.statsByFormat = new HashMap<>();
		statsByFormat.put(Format.ODI, new Stats(Format.ODI));
		statsByFormat.put(Format.TEST, new Stats(Format.TEST));
		statsByFormat.put(Format.T20, new Stats(Format.T20));
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlayerID() {
		return playerID;
	}
	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}
	public PlayerType getPlayerType() {
		return playerType;
	}
	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
	public BowlingStyle getBowlingStyle() {
		return bowlingStyle;
	}
	public void setBowlingStyle(BowlingStyle bowlingStyle) {
		this.bowlingStyle = bowlingStyle;
	}
	public BattingStyle getBattingStyle() {
		return battingStyle;
	}
	public void setBattingStyle(BattingStyle battingStyle) {
		this.battingStyle = battingStyle;
	}
	public Map<Format, Stats> getStatsByFormat() {
		return statsByFormat;
	}
	public void setStatsByFormat(Map<Format, Stats> statsByFormat) {
		this.statsByFormat = statsByFormat;
	}
}
//will make it singleton
class TeamController {
	private List<Team> teamsList;
	
}
class Team {
	private List<Player> playersList;
	private String name;
	private String teamID;
	private List<Match> matchHistory;

	public List<Player> getPlayersList() {
		return playersList;
	}
	public void setPlayersList(List<Player> playersList) {
		this.playersList = playersList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTeamID() {
		return teamID;
	}
	public void setTeamID(String teamID) {
		this.teamID = teamID;
	}
	public List<Match> getMatchHistory() {
		return matchHistory;
	}
	public void setMatchHistory(List<Match> matchHistory) {
		this.matchHistory = matchHistory;
	}
}

class Match {
	private String matchName;
	private String matchID;
	private Team teamA;
	private Team teamB;
	private boolean tossWonByTeamA;
	private List<Inning> innings;
	private int currentInning;
	
	
	public Match(String matchName, String matchID, Team teamA, Team teamB, boolean tossWonByTeamA) {
		this.matchName = matchName;
		this.matchID = matchID;
		this.teamA = teamA;
		this.teamB = teamB;
		this.tossWonByTeamA = tossWonByTeamA;
		this.innings = new ArrayList<>();
		this.currentInning = 0;
		if (tossWonByTeamA) {
			//assuming team a chose to bat first
			innings.add(new Inning(new Squad(teamB.getPlayersList()), new Squad(teamA.getPlayersList())));
			innings.add(new Inning(new Squad(teamA.getPlayersList()), new Squad(teamB.getPlayersList())));
		} else {
			innings.add(new Inning(new Squad(teamA.getPlayersList()), new Squad(teamB.getPlayersList())));
			innings.add(new Inning(new Squad(teamB.getPlayersList()), new Squad(teamA.getPlayersList())));
		}
	}
	
	public void start() {
		// TODO Auto-generated method stub
		System.out.println("Match " + this.matchName + " is started. Admin please give commands");
		System.out.println("Printing the scorecard " + innings.get(0).getScorecard().toString());
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public String getMatchID() {
		return matchID;
	}

	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public Team getTeamA() {
		return teamA;
	}

	public void setTeamA(Team teamA) {
		this.teamA = teamA;
	}

	public Team getTeamB() {
		return teamB;
	}

	public void setTeamB(Team teamB) {
		this.teamB = teamB;
	}

	public boolean isTossWonByTeamA() {
		return tossWonByTeamA;
	}

	public void setTossWonByTeamA(boolean tossWonByTeamA) {
		this.tossWonByTeamA = tossWonByTeamA;
	}

	public List<Inning> getInnings() {
		return innings;
	}

	public void setInnings(List<Inning> innings) {
		this.innings = innings;
	}

	public void executeWide() {
		// TODO Auto-generated method stub
		System.out.println(innings.get(currentInning).getScorecard().getCurrentBowler().getName()
				+ " Bowled wide to " + innings.get(currentInning).getScorecard().getCurrentBatters().get(0).getName());
		innings.get(currentInning).getScorecard().setScore(innings.get(currentInning).getScorecard().getScore() + 1);
		System.out.println(innings.get(currentInning).getScorecard().toString());
	}
	public void executeOneRun() {
		// TODO Auto-generated method stub
		
	}
	public void executeTwoRun() {
		// TODO Auto-generated method stub
		
	}
	public void executeThreeRun() {
		// TODO Auto-generated method stub
		
	}
	public void executeSix() {
		// TODO Auto-generated method stub
		
	}
	public void executeFour() {
		// TODO Auto-generated method stub
		
	}
	
}
class Inning{
	private Squad bowlingSquad;
	private Squad battingSquad;
	private Scorecard scorecard;
	public Inning(Squad bowlingSquad, Squad battingSquad) {
		super();
		this.bowlingSquad = bowlingSquad;
		this.battingSquad = battingSquad;
		this.scorecard = new Scorecard();
	}
	public Squad getBowlingSquad() {
		return bowlingSquad;
	}
	public void setBowlingSquad(Squad bowlingSquad) {
		this.bowlingSquad = bowlingSquad;
	}
	public Squad getBattingSquad() {
		return battingSquad;
	}
	public void setBattingSquad(Squad battingSquad) {
		this.battingSquad = battingSquad;
	}
	public Scorecard getScorecard() {
		return scorecard;
	}
	public void setScorecard(Scorecard scorecard) {
		this.scorecard = scorecard;
	}
	
}
class Scorecard {
	private List<Player> currentBatters;//2 batters
	private Player currentBowler;
	private int score;
	private int wickets;
	private Map<Player, BattingCard> battingCard;
	private Map<Player, BowlingCard> bowlingCard;
	public Scorecard() {
		super();
		this.score = 0;
		this.wickets = 0;
		this.battingCard = new HashMap<>();
		this.bowlingCard = new HashMap<>();
	}
	public void setOpeningBatters(List<Player> openers) {
		System.out.println("Opening batters " + openers.get(0).getName() + " " + openers.get(0).getName() + "are on the field ");
		currentBatters = openers;
	}
	public void setOpeningBowler(Player bowler) {
		System.out.println("Opening bowler is " + bowler.getName());
		currentBowler = bowler;
	}
	public List<Player> getCurrentBatters() {
		return currentBatters;
	}
	public void setCurrentBatters(List<Player> currentBatters) {
		this.currentBatters = currentBatters;
	}
	public Player getCurrentBowler() {
		return currentBowler;
	}
	public void setCurrentBowler(Player currentBowler) {
		this.currentBowler = currentBowler;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getWickets() {
		return wickets;
	}
	public void setWickets(int wickets) {
		this.wickets = wickets;
	}
	public Map<Player, BattingCard> getBattingCard() {
		return battingCard;
	}
	public void setBattingCard(Map<Player, BattingCard> battingCard) {
		this.battingCard = battingCard;
	}
	public Map<Player, BowlingCard> getBowlingCard() {
		return bowlingCard;
	}
	public void setBowlingCard(Map<Player, BowlingCard> bowlingCard) {
		this.bowlingCard = bowlingCard;
	}
	@Override
	public String toString() {
		return "Scorecard [score=" + score + ", wickets=" + wickets + "]";
	}
}

class BattingCard {
	private int runs;
	private int ballsPlayed;
	private int fours;
	private int six;
	private double strikeRate;
	public BattingCard() {
		super();
		this.runs = 0;
		this.ballsPlayed = 0;
		this.fours = 0;
		this.six = 0;
		this.strikeRate = 0;
	}
	public int getRuns() {
		return runs;
	}
	public void setRuns(int runs) {
		this.runs = runs;
	}
	public int getBallsPlayed() {
		return ballsPlayed;
	}
	public void setBallsPlayed(int ballsPlayed) {
		this.ballsPlayed = ballsPlayed;
	}
	public int getFours() {
		return fours;
	}
	public void setFours(int fours) {
		this.fours = fours;
	}
	public int getSix() {
		return six;
	}
	public void setSix(int six) {
		this.six = six;
	}
	public double getStrikeRate() {
		return strikeRate;
	}
	public void setStrikeRate(double strikeRate) {
		this.strikeRate = strikeRate;
	}
}

class BowlingCard {
	private int oversBowled;
	private int maiden;
	private int runsConceeded;
	private int wickets;
	private double economy;
	public BowlingCard() {
		super();
		this.oversBowled = oversBowled;
		this.maiden = maiden;
		this.runsConceeded = runsConceeded;
		this.wickets = wickets;
		this.economy = economy;
	}
	public int getOversBowled() {
		return oversBowled;
	}
	public void setOversBowled(int oversBowled) {
		this.oversBowled = oversBowled;
	}
	public int getMaiden() {
		return maiden;
	}
	public void setMaiden(int maiden) {
		this.maiden = maiden;
	}
	public int getRunsConceeded() {
		return runsConceeded;
	}
	public void setRunsConceeded(int runsConceeded) {
		this.runsConceeded = runsConceeded;
	}
	public int getWickets() {
		return wickets;
	}
	public void setWickets(int wickets) {
		this.wickets = wickets;
	}
	public double getEconomy() {
		return economy;
	}
	public void setEconomy(double economy) {
		this.economy = economy;
	}
}
class Squad {
	private List<Player> playersList;

	public Squad(List<Player> playersList) {
		super();
		this.playersList = playersList;
	}

	public List<Player> getPlayersList() {
		return playersList;
	}

	public void setPlayersList(List<Player> playersList) {
		this.playersList = playersList;
	}
}