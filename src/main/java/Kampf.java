
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Kampf {

    private final Benutzer erstespieler;
    private Benutzer zweitespieler;
    private int scorevomerstenspieler;
    private int scorevomzweitenspieler;
    private final int id;
    private Karten Deckvomerstenspieler;
    private Karten Deckvomzweitenspieler;
    private final Karten spieler1DeckzubeginndesBattles;
    private Karten spieler2DeckzubeginndesBattles;
    private final ArrayList<String> log = new ArrayList<>();
    private ArrayList<Karte> dck1 = new ArrayList<>(), dck2 = new ArrayList<>();


    public Kampf(int id, Benutzer erstespieler, Karten Deckvomerstenspieler){
        this.id = id;
        this.erstespieler = erstespieler;
        this.Deckvomerstenspieler = Deckvomerstenspieler;
        this.scorevomerstenspieler = 100;
        this.scorevomzweitenspieler = 100;
        this.Deckvomzweitenspieler = null;
        this.spieler1DeckzubeginndesBattles = Deckvomerstenspieler;
        this.spieler2DeckzubeginndesBattles = null;
    }

       // Startet  Kampf

    public boolean kampfstarten(){
        System.out.println("KAMPF BEGINNT");
        System.out.println(erstespieler.getName() + " gegen" + zweitespieler.getName());

        if(Deckvomerstenspieler.getKarten().size() == 4 && Deckvomzweitenspieler.getKarten().size() == 4) {
             //deck mischen
            this.dck1 = Deckvomerstenspieler.getKarten();
            this.dck2 = Deckvomzweitenspieler.getKarten();
            Collections.shuffle(this.dck1);
            Collections.shuffle(this.dck2);
            Deckvomerstenspieler.setKarten(this.dck1);
            Deckvomerstenspieler.setKarten(this.dck2);

            //round
            int maximaleRundenzahl = 100, zaehler = 0, zaehler2 = 0;
            while(zaehler<maximaleRundenzahl) {
                zaehler++;
                zaehler2++;
                if(zaehler2 < this.dck1.size() || zaehler2 < this.dck2.size()){
                    zaehler2 = 0;
                }
                if (this.dck1.size() > 0 && this.dck2.size() > 0 && zaehler<=maximaleRundenzahl) {
                    System.out.println("ROUND:D: " + zaehler);
                    System.out.println("DIE GROSSE VON 1.DECK: " + this.dck1.size() + " DIE GROSSE VON 2.DECK: " + this.dck2.size() + " zaehler2: " + zaehler2);
                    Karte kr1 = this.dck1.get(zaehler2);
                    Karte kr2 = this.dck2.get(zaehler2);
                    System.out.println("ERSTE KARTE: " + kr1.getdieElemente() + kr1.getKartenKategorie() + "\nZWEITE KARTE: " + kr2.getdieElemente() + kr2.getKartenKategorie());
                    if (!kr1.getKartenKategorie().name().equalsIgnoreCase("SPELL") && !kr2.getKartenKategorie().name().equalsIgnoreCase("SPELL")) {
                        if ((kr1.getKartenKategorie().name().equalsIgnoreCase("DRAGON") && !kr2.getKartenKategorie().name().equalsIgnoreCase("DRAGON") || (!kr1.getKartenKategorie().name().equalsIgnoreCase("DRAGON") && kr2.getKartenKategorie().name().equalsIgnoreCase("DRAGON")))) {
                            Karte dragon;
                            int drachebesitzer;
                            if (kr1.getKartenKategorie().name().equalsIgnoreCase("DRAGON")) {
                                dragon = kr1;
                                drachebesitzer = 1;
                            } else if (kr2.getKartenKategorie().name().equalsIgnoreCase("DRAGON")) {
                                dragon = kr2;
                                drachebesitzer = 2;
                            } else {
                                return false;
                            }
                            if ((kr1.getKartenKategorie().name().equalsIgnoreCase("GOBLIN") && !kr2.getKartenKategorie().name().equalsIgnoreCase("GOBLIN") || (!kr1.getKartenKategorie().name().equalsIgnoreCase("GOBLIN") && kr2.getKartenKategorie().name().equalsIgnoreCase("GOBLIN")))) {
                                Karte goblin;
                                if (kr1.getKartenKategorie().name().equalsIgnoreCase("GOBLIN")) {
                                    goblin = kr1;
                                } else if (kr2.getKartenKategorie().name().equalsIgnoreCase("GOBLIN")) {
                                    goblin = kr2;
                                } else {
                                    return false;
                                }
                                if (dragon.getDamage() > goblin.getDamage()) {
                                    if (drachebesitzer == 1) {
                                        this.dck1.add(kr2);
                                        this.dck2.remove(kr2);
                                        this.scorevomerstenspieler += 3;
                                        this.scorevomzweitenspieler -= 5;
                                    } else {
                                        this.dck2.add(kr1);
                                        this.dck1.remove(kr1);
                                        this.scorevomerstenspieler -= 5;
                                        this.scorevomzweitenspieler += 3;
                                    }
                                    this.log.add("FIGHTER " + drachebesitzer + " WIN!!!!!!!!!!!\nDragon is NATURALLY stronger:D!! DRACHE: " + dragon.getDamage() + " GEGEN Goblin: " + goblin.getDamage() + "\nSCORE VOM ERSTEN SPIELER:->" + scorevomerstenspieler + "\nSCORE VOM ZWEITEN SPIELER:-> " + scorevomzweitenspieler);
                                }
                            } else if ((kr1.getKartenKategorie().name().equalsIgnoreCase("ELF") && kr1.getdieElemente().name().equalsIgnoreCase("FIRE") && !kr2.getKartenKategorie().name().equalsIgnoreCase("ELF") || (!kr1.getKartenKategorie().name().equalsIgnoreCase("ELF") && kr2.getdieElemente().name().equalsIgnoreCase("FIRE") && kr2.getKartenKategorie().name().equalsIgnoreCase("ELF")))) {
                                // Fire Elf und  drache
                                Karte fireelf;
                                if (drachebesitzer == 1) {
                                    fireelf = kr2;
                                } else {
                                    fireelf = kr1;
                                }
                                //Fireelf is stronnger
                                if (fireelf.getDamage() > dragon.getDamage()) {
                                    if (drachebesitzer == 1) {
                                        this.dck2.add(kr1);
                                        this.dck1.remove(kr1);
                                        this.scorevomzweitenspieler += 3;
                                        this.scorevomerstenspieler -= 5;
                                    } else {
                                        this.dck1.add(kr2);
                                        this.dck2.remove(kr2);
                                        this.scorevomzweitenspieler -= 5;
                                        this.scorevomerstenspieler += 3;
                                    }
                                    this.log.add("FIGHTER " + drachebesitzer + " WIN!!!!!!!!!!!\nWizzard  is NATURALLY stronger:D!!DRACHE:  " + dragon.getDamage() + " GEGEN FireElves: " + fireelf.getDamage() + "\nSCORE VOM ERSTEN SPIELER:-> " + scorevomerstenspieler + "\nSCORE VOM ZWEITEN SPIELER:-> " + scorevomzweitenspieler);
                                }
                            } else {
                                winnerermitteln(kr1, kr2);
                            }

                        } else if ((kr1.getKartenKategorie().name().equalsIgnoreCase("WIZZARD") && kr2.getKartenKategorie().name().equalsIgnoreCase("ORK") || (kr2.getKartenKategorie().name().equalsIgnoreCase("WIZZARD") && kr1.getKartenKategorie().name().equalsIgnoreCase("ORK")))) {
                            Karte wizzard = null;
                            Karte ork = null;
                            int WIZZARDBESITZER = 0;
                            if (kr1.getKartenKategorie().name().equalsIgnoreCase("WIZZARD")) {
                                WIZZARDBESITZER = 1;
                                wizzard = kr1;
                                ork = kr2;
                            } else if (kr2.getKartenKategorie().name().equalsIgnoreCase("WIZZARD")) {
                                WIZZARDBESITZER = 2;
                                wizzard = kr2;
                                ork = kr1;
                            }
                            if (wizzard != null && wizzard.getDamage() > ork.getDamage()) {
                                if (WIZZARDBESITZER == 1) {
                                    this.dck1.add(kr2);
                                    this.dck2.remove(kr2);
                                    this.scorevomerstenspieler += 3;
                                    this.scorevomzweitenspieler -= 5;
                                } else {
                                    this.dck2.add(kr1);
                                    this.dck1.remove(kr1);
                                    this.scorevomzweitenspieler += 3;
                                    this.scorevomerstenspieler -= 5;
                                }

                                this.log.add("FIGHTER " + WIZZARDBESITZER + " WIN!!!!!!!!!!!!\nWizzard  is NATURALLY stronger:D!!Wizzard:  " + wizzard.getDamage() + " gegen Ork: " + ork.getDamage() + "\nSCORE VOM ERSTEN SPIELER:->  " + scorevomerstenspieler + "\nSCORE VOM ZWEITEN SPIELER:-> " + scorevomzweitenspieler);
                            }
                        } else {
                            //PURE MONSTER
                            winnerermitteln(kr1, kr2);
                        }
                    } else {
                        double schadenspieler1, schadenspieler2;

                        if (kr1.getKartenKategorie().name().equalsIgnoreCase("KNIGHT") || kr2.getKartenKategorie().name().equalsIgnoreCase("KNIGHT")) {
                            Karte knight = null, other = null;
                            int knightOwner = 0;
                            if (kr1.getKartenKategorie().name().equalsIgnoreCase("KNIGHT")) {
                                knight = kr1;
                                other = kr2;
                                knightOwner = 1;
                            } else if (kr2.getKartenKategorie().name().equalsIgnoreCase("KNIGHT")) {
                                knight = kr2;
                                other = kr1;
                                knightOwner = 2;
                            }
                            double damageKnight = -1, damageOther = -1;
                            if (Objects.requireNonNull(other).getdieElemente().name().equalsIgnoreCase("WATER")) {
                                //tot
                                damageKnight = 0;
                                damageOther = other.getDamage();
                            } else if (other.getdieElemente().name().equalsIgnoreCase("FIRE") && Objects.requireNonNull(knight).getdieElemente().name().equals("REGULAR")) {
                                //not effective
                                damageKnight = knight.getDamage() / 2;
                                //effective
                                damageOther = other.getDamage() * 2;
                            } else if (other.getdieElemente().name().equalsIgnoreCase("FIRE") && Objects.requireNonNull(knight).getdieElemente().name().equals("FIRE")) {
                                //no effect
                                damageKnight = knight.getDamage();
                                //no effect
                                damageOther = other.getDamage();
                            } else if (other.getdieElemente().name().equalsIgnoreCase("FIRE") && Objects.requireNonNull(knight).getdieElemente().name().equals("WATER")) {
                                //effective
                                damageKnight = knight.getDamage() * 2;
                                //not effective
                                damageOther = other.getDamage() / 2;
                            } else if (other.getdieElemente().name().equalsIgnoreCase("REGULAR") && Objects.requireNonNull(knight).getdieElemente().name().equals("REGULAR")) {
                                //no effect
                                damageKnight = knight.getDamage();
                                //no effect
                                damageOther = other.getDamage();
                            } else if (other.getdieElemente().name().equalsIgnoreCase("REGULAR") && Objects.requireNonNull(knight).getdieElemente().name().equals("FIRE")) {
                                //effective
                                damageKnight = knight.getDamage() * 2;
                                //not effective
                                damageOther = other.getDamage() / 2;
                            } else if (other.getdieElemente().name().equalsIgnoreCase("REGULAR") && Objects.requireNonNull(knight).getdieElemente().name().equals("WATER")) {
                                //not effective
                                damageKnight = knight.getDamage() / 2;
                                //effective
                                damageOther = other.getDamage() * 2;
                            }
                            if (damageKnight > damageOther) {
                                if (knightOwner == 1) {
                                    player1gewinnt(kr1,kr2);
                                } else {
                                    player2gewinnt(kr1, kr2);
                                }
                            } else if (damageKnight < damageOther) {
                                if (knightOwner == 2) {
                                    player1gewinnt(kr1, kr2);
                                } else {
                                    player2gewinnt(kr1, kr2);
                                }
                            }
                        } else if (kr1.getKartenKategorie().name().equalsIgnoreCase("KRAKEN") || kr2.getKartenKategorie().name().equalsIgnoreCase("KRAKEN")) {
                            if (kr1.getKartenKategorie().name().equalsIgnoreCase("KRAKEN")) {
                                player1gewinnt(kr1, kr2);
                            } else if (kr2.getKartenKategorie().name().equalsIgnoreCase("KRAKEN")) {
                                player2gewinnt(kr1, kr2);
                            }
                        } else {
                            //Player 1 Damage berechnen
                            schadenspieler1 = calculateEffectiveness(kr1, kr2);
                            //P2 damage
                            schadenspieler2 = calculateEffectiveness(kr2, kr1);

                            if (schadenspieler1 > -1 && schadenspieler2 > -1) {
                                if (schadenspieler1 > schadenspieler2) {
                                    player1gewinnt(kr1, kr2);
                                } else if (schadenspieler2 > schadenspieler1) {
                                    player2gewinnt(kr1, kr2);
                                }
                            } else {
                                return false;
                            }
                        }
                    }
                }else {
                    return true;
                }
            }
            this.Deckvomerstenspieler = new Karten(this.dck1);
            this.Deckvomzweitenspieler = new Karten(this.dck2);
        }else{
            System.err.println(" too few cards in the deck");
            return false;
        }
        return true;
    }

  // Spieler 1 gewinnt

    private void player1gewinnt(Karte kr1, Karte kr2){
        this.dck1.add(kr2);
        this.dck2.remove(kr2);
        this.scorevomerstenspieler += 3;
        this.scorevomzweitenspieler -= 5;
        this.log.add("ERSTE SPIELER WIN!!!!\n" + kr1.getdieElemente() + kr1.getKartenKategorie() + " is NATURALLY stronger!!! " + kr1.getdieElemente() + kr1.getKartenKategorie() + ": " + kr1.getDamage() + " GEGEN " + kr2.getdieElemente() + kr2.getKartenKategorie() + ": " + kr2.getDamage() + "\nSCORE VOM ERSTEN SPIELER " + scorevomerstenspieler + "\nSCORE VOM ZWEITEN SPIELER:" + scorevomzweitenspieler);
    }


    // Spieler 2 gewinnt
    private void player2gewinnt(Karte kr1, Karte kr2){
        this.dck2.add(kr1);
        this.dck1.remove(kr1);
        this.scorevomzweitenspieler += 3;
        this.scorevomerstenspieler -= 5;
        this.log.add("ZWEITE SPIELER WIN!!!!\n" + kr2.getdieElemente() + kr2.getKartenKategorie() + " is NATURALLY stronger!!! " + kr2.getdieElemente() + kr2.getKartenKategorie() + ": " + kr1.getDamage() + " GEGEN " + kr1.getdieElemente() + kr1.getKartenKategorie() + ": " + kr2.getDamage() + "\nSCORE VOM ERSTEN SPIELER: " + scorevomerstenspieler + "\nSCORE VOM ZWEITEN SPIELER: " + scorevomzweitenspieler);
    }


     // the winner of the round

    private void winnerermitteln(Karte kr1, Karte kr2) {
        if (kr1.getDamage() > kr2.getDamage()) {
            player1gewinnt(kr1, kr2);
        } else if (kr1.getDamage() < kr2.getDamage()) {
            player2gewinnt(kr1, kr2);
        }
    }

    private double calculateEffectiveness(Karte kr1, Karte kr2){
        double schadenspieler1 = 0;
        switch (kr1.getdieElemente().name().toUpperCase()) {
            case "FIRE":
                switch (kr2.getdieElemente().name().toUpperCase()) {
                    case "REGULAR":
                        //effective
                        schadenspieler1 = kr1.getDamage() * 2;
                        break;
                    case "WATER":
                        //not effective
                        schadenspieler1 = kr1.getDamage() / 2;
                        break;
                    case "FIRE":
                        //no effect
                        schadenspieler1 = kr1.getDamage();
                        break;
                }
                break;
            case "WATER":
                switch (kr2.getdieElemente().name().toUpperCase()) {
                    case "FIRE":
                        //effective
                        schadenspieler1 = kr1.getDamage() * 2;
                        break;
                    case "WATER":
                        //no effect
                        schadenspieler1 = kr1.getDamage();
                        break;
                    case "REGULAR":
                        //not effective
                        schadenspieler1 = kr1.getDamage() / 2;
                        break;
                }
                break;
            case "REGULAR":
                switch (kr2.getdieElemente().name().toUpperCase()) {
                    case "WATER":
                        //effective
                        schadenspieler1 = kr1.getDamage() * 2;
                        break;
                    case "FIRE":
                        //not effective
                        schadenspieler1 = kr1.getDamage() / 2;
                        break;
                    case "REGULAR":
                        //no effect
                        schadenspieler1 = kr1.getDamage();
                        break;
                }
                break;
        }
        return schadenspieler1;
    }


    public ArrayList<String> getLog() {
        return this.log;
    }


  // GETTER UND SETTER METHODEN *********************************
    public int getId() {
        return id;
    }


    public Benutzer getPlayer1() {
        return erstespieler;
    }


    public Benutzer getPlayer2() {
        return zweitespieler;
    }


    public void setPlayer2(Benutzer zweitespieler) {
        this.zweitespieler = zweitespieler;
    }


    public int getScorePlayer1() {
        return scorevomerstenspieler;
    }


    public int getScorePlayer2() {
        return scorevomzweitenspieler;
    }


    public Karten getDeckPlayer1() {
        return Deckvomerstenspieler;
    }


    public Karten getDeckPlayer2() {
        return Deckvomzweitenspieler;
    }


    public void setDeckPlayer2(Karten Deckvomzweitenspieler) {
        this.Deckvomzweitenspieler = Deckvomzweitenspieler;
        this.spieler2DeckzubeginndesBattles = Deckvomzweitenspieler;
    }


    public Karten getDeckPlayer1Init() {
        return spieler1DeckzubeginndesBattles;
    }


    public Karten getDeckPlayer2Init() {
        return spieler2DeckzubeginndesBattles;
    }
}
