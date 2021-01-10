import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;



  //die Verbindung Datenbank

public class DatenBankVerbindung {
    private Statement sqlstatement;
    private Connection dbconnection;




    public DatenBankVerbindung() {
        this.dbconnection = null;
    }



    public boolean init() {
        ArrayList<Boolean> errors = new ArrayList<>();
        errors.add(DatenBankConnect.executeSQLmitNachricht("CREATE TABLE IF NOT EXISTS USERS (username TEXT PRIMARY KEY NOT NULL, nachname TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL, bio TEXT, image TEXT, coins integer default 20 not null)", "!!!!!!!!!!!!!!!User Table wurde erstellt!!!!!!!!!!!!!!!"));
        errors.add(DatenBankConnect.executeSQL("CREATE TABLE IF NOT EXISTS CARD(NAME TEXT not null,DAMAGE FLOAT not null,ELEMENTTYP TEXT not null,CARDTYPE TEXT not null, PRIMARY KEY (\"name\"));"));
        errors.add(DatenBankConnect.executeSQLmitNachricht("create unique index IF NOT EXISTS card_name_uindex on CARD (NAME);", "!!!!!!!!!!!!!!!Karte Table wurde erstellt!!!!!!!!!!!!!!!"));
        errors.add(DatenBankConnect.executeSQLmitNachricht("create table IF NOT EXISTS package(\"ID\" varchar(255) not null,name varchar(255) not null constraint name references card, i serial not null constraint package_i primary key );", "!!!!!!!!!!!!!!!Package Table wurde erstellt!!!!!!!!!!!!!!!"));
        errors.add(DatenBankConnect.executeSQLmitNachricht("create table IF NOT EXISTS user_cards(username TEXT not null constraint user_cards_users_username_fk references users,name text not null, gesperrt boolean not null);", "!!!!!!!!!!!!!!!User Karte wurde erstellt!!!!!!!!!!!!!!!"));
        errors.add(DatenBankConnect.executeSQLmitNachricht("create table IF NOT EXISTS user_deck(username text not null constraint user_deck_users_username_fk references users,cardname text not null);", "!!!!!!!!!!!!!!!DECK USER wurde erstellt!!!!!!!!!!!!!!!"));
        errors.add(DatenBankConnect.executeSQL("create table if not exists battle(usernamecreator text not null constraint battle_users_username_fk references users,usernameplayer text constraint battle_users_username_fk_2 references users, battleid serial, deckcreator text not null);"));
        errors.add(DatenBankConnect.executeSQLmitNachricht("create unique index if not exists battle_battleid_uindex on battle (battleid);", "!!!!!!!!!!!!!!!BATTLE Table wurde erstellt!!!!!!!!!!!!!!!"));
        errors.add(DatenBankConnect.executeSQL("create table IF NOT EXISTS battle_log(id int not null constraint battle_log_pk primary key, playerone text not null,playertwo text not null,playeronescore text not null,playertwoscore text not null,log varchar(10485760));"));
        errors.add(DatenBankConnect.executeSQLmitNachricht("create unique index IF NOT EXISTS battle_log_id_uindex on battle_log (id);", "!!!!!!!!!!!!!!!log BATTLE Table wurde erstellt!!!!!!!!!!!!!!!"));
        return !errors.contains(false);
    }


    public ArrayList<String> getAllKampfIdbenutzer(String username){
        this.dbconnection = DatenBankConnect.verbinden();
        int id;
        ArrayList<String> kampfid = new ArrayList<>();
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select id from battle_log where playerone = '" + username + "' or playertwo = '" + username + "';");
            while (rs.next()) {
                id = rs.getInt("id");
                if (id > 0) {
                    kampfid.add(id + "");
                }else {
                    return null;
                }
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
            return kampfid;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }


    public int getletztekampfiduser(String username){
        this.dbconnection = DatenBankConnect.verbinden();
        int id;
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet result = sqlstatement.executeQuery("select max(id) from battle_log where playerone = '" + username + "' or playertwo = '" + username + "';");
            //noinspection LoopStatementThatDoesntLoop
            while (result.next()) {
                id = result.getInt("max");
                if (id > 0) {
                    return id;
                }else {
                    return -1;
                }
            }
            result.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
        return -1;
    }




    public boolean kampfhinzufugen(String benutzernamevonkampfErsteller){
        ArrayList<String> decknamen = getDeck(benutzernamevonkampfErsteller);
        if(decknamen != null && !decknamen.isEmpty()){
            Karten deck = getKartenausidlist(decknamen);
            if(deck != null && !deck.getKarten().isEmpty()){
                String deckJson = HilfsfunktionenJson.Umwandelnobjektinjson(deck.getKarten());
                if (deckJson != null && !deckJson.isEmpty()){
                    return DatenBankConnect.executeSQLmitNachricht("insert into battle (usernamecreator, deckcreator) VALUES ('" + benutzernamevonkampfErsteller + "','" + deckJson + "');", "Battle created");
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else{
            return false;
        }
    }

    public Map<String, String> getkampflogin(String battleId){
        this.dbconnection = DatenBankConnect.verbinden();
        String erstespieler, zweitespieler, score1, score2, log;
        int id;
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet result = sqlstatement.executeQuery("select * from battle_log where id = " + battleId + ";");
            //noinspection LoopStatementThatDoesntLoop
            while (result.next()) {
                id = result.getInt("id");
                erstespieler = result.getString("playerone");
                zweitespieler = result.getString("playertwo");
                score1 = result.getString("playeronescore");
                score2 = result.getString("playertwoscore");
                log = result.getString("log");
                if (id > 0 && !erstespieler.isEmpty() && !zweitespieler.isEmpty() && !score1.isEmpty() && !score2.isEmpty() && !log.isEmpty()){
                    Map<String, String> map = new java.util.HashMap<>(Collections.emptyMap());
                    map.put("playerone", erstespieler);
                    map.put("playertwo", zweitespieler);
                    map.put("playeronescore", score1);
                    map.put("playertwoscore", score2);
                    map.put("log", log);
                    map.put("id", id+"");
                    return map;
                }else{
                    return null;
                }

            }
            result.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return null;
    }

    public Kampf offeneKampf(){
        this.dbconnection = DatenBankConnect.verbinden();
        int kampfid;
        String creator;
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select * from battle limit 1;");
            //noinspection LoopStatementThatDoesntLoop
            while (rs.next()) {
                kampfid = rs.getInt("battleid");
                creator = rs.getString("usernamecreator");
                Benutzer player1 = new DatenBankVerbindung().getUser(creator);
                if(player1 != null){
                    ArrayList<String> deckPlayer1Arr = new DatenBankVerbindung().getDeck(creator);
                    if (deckPlayer1Arr != null){
                        Karten deckPlayer1 = new DatenBankVerbindung().getKartenausidlist(deckPlayer1Arr);
                        if(deckPlayer1 != null){
                            if(loeschbattle(kampfid+"")){
                                return new Kampf(kampfid, player1, deckPlayer1);
                            }else{
                                return null;
                            }
                        }else{
                            return null;
                        }
                    }else {
                        return null;
                    }
                }else{
                    return null;
                }
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean loeschbattle(String battleid){
        return DatenBankConnect.executeSQLmitNachricht("delete from battle where battleid = '" + battleid + "';", "Battle req deleted");
    }


    public boolean logeinfugen(String id, String playerone, String playertwo, String playeronescore, String playertwoscore, String log){
        return DatenBankConnect.executeSQL("insert into battle_log (id, playerone, playertwo, playeronescore, playertwoscore, log) VALUES ("+id+ ",'" +playerone+ "','" +playertwo+ "','" +playeronescore+ "','" +playertwoscore+ "','" +log+ "');");
    }



    public boolean aktualisierenkartelock(String name, boolean lock){
        return DatenBankConnect.executeSQL("Update user_cards set gesperrt = " + lock + " where name = '" + name + "';");
    }


    public boolean kartegesperttprufer(String name) throws SQLException {
        this.dbconnection = DatenBankConnect.verbinden();
        boolean locked = false;
        sqlstatement = this.dbconnection.createStatement();
        ResultSet rs = sqlstatement.executeQuery("select gesperrt from user_cards where name = '" + name + "';");
        while (rs.next()) {
            locked = rs.getBoolean("gesperrt");
        }
        rs.close();
        sqlstatement.close();
        this.dbconnection.close();
        return locked;
    }

    public boolean setDeck(String username, List<String> deck){
        for (String st :deck) {
            try {
                if(kartegesperttprufer(st)){
                    return false;
                }
            } catch (SQLException throwables) {
                System.err.println(throwables.getMessage());
                return false;
            }
        }
        Karten allCards = getCards(username);
        Karten deckCards = new Karten(new ArrayList<>());
        int count = 0;
        if(allCards != null && deck.size() == 4){
            for (String st : deck) {
                for (Karte ca: allCards.getKarten()) {
                    if(ca.getName().equals(st) && count < 4){
                        if(deckCards.getKarten().size() == 0){
                            if (!loschdeck(username)){
                                return false;
                            }
                        }
                        deckCards.addKarten(ca);
                        if(deckCards.getKarten().size() == 4){
                            int dbconnection = 0;
                            for(Karte cardtmp : deckCards.getKarten()){
                                dbconnection++;
                                if(!DatenBankConnect.executeSQLmitNachricht("INSERT INTO public.user_deck (username, cardname) VALUES ('" +username+ "', '" +cardtmp.getName()+ "');", "Karte #"+dbconnection+" added to Deck")){
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                count++;
            }
            return false;
        }
        return false;
    }

    public ArrayList<String> getDeck(String username){
        this.dbconnection = DatenBankConnect.verbinden();
        String cardname;
        ArrayList<String> cardnamenarray = new ArrayList<>();
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select * from user_deck where username = '" + username + "';");
            while (rs.next()) {
                cardname = rs.getString("cardname");
                cardnamenarray.add(cardname);
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return cardnamenarray;
    }

    public boolean loschdeck(String username){
        return DatenBankConnect.executeSQLmitNachricht("delete from user_deck where username = '" + username + "';", "Benutzer() Deck: " + username + ", deleted");
    }

    public boolean karteuberprufen(String name) {
        this.dbconnection = DatenBankConnect.verbinden();
        int count = 0;
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select count(*) from card where name = '" + name + "';");
            while (rs.next()) {
                count = rs.getInt("count");
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return count == 1;
    }
    public Paket RandompackageausdemShop(String username) {
        this.dbconnection = DatenBankConnect.verbinden();
        String id = "";
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select \"ID\" as id from package LIMIT 1;");
            while (rs.next()) {
                id = rs.getString("id");
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        this.dbconnection = DatenBankConnect.verbinden();
        String paketname = "", cardname, elementtyp, cardtype;
        int damage;
        Karten cards = new Karten(new ArrayList<>());
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select  i as zeilennummer,  package.\"ID\" as id, package.name as packagename, dbconnection.name as cardname, dbconnection.DAMAGE as damage, dbconnection.ELEMENTTYP as elementtyp, dbconnection.CARDTYPE as cardtype from package join card dbconnection on dbconnection.name = package.name where \"ID\" = '" + id + "';");
            while (rs.next()) {
                id = rs.getString("id");
                paketname = rs.getString("packagename");
                cardname = rs.getString("cardname");
                elementtyp = rs.getString("elementtyp");
                cardtype = rs.getString("cardtype");
                damage = rs.getInt("damage");
                Karte newCard = new Karte(cardname, elementtyp + cardtype, damage);
                cards.addKarten(newCard);
                if(!kartehinzufugen(username, newCard.getName())){
                    return null;
                }
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        if(!loeschpaket(id)){
            return null;
        }

        if (cards.getKarten().size() != 0) {
            return new Paket(cards, paketname, 5);
        } else {
            return null;
        }
    }

    public boolean loeschbenutzerkarte(String username, String cardname){
        return DatenBankConnect.executeSQL("delete from user_cards where username = '" +username+ "' and name = '" +cardname+ "';");
    }

    public boolean kartehinzufugen(String username, String cardName){
        Connection b = DatenBankConnect.verbinden();
        try {
            sqlstatement = b.createStatement();
            String sql = "INSERT INTO public.user_cards (username, name, gesperrt) VALUES ( '" + username + "','" + cardName + "', 'false');";
            sqlstatement.executeUpdate(sql);
            sqlstatement.close();
            b.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }
    public boolean loeschpaket(String name) {
        return DatenBankConnect.executeSQLmitNachricht("DELETE FROM package WHERE \"ID\" = '" + name + "';", "Package deleted successfully");
    }


    public boolean munzeupdate(int coins, String username) {
        return DatenBankConnect.executeSQLmitNachricht("UPDATE users SET coins = " + coins + " WHERE username = '" + username + "';", "Münzen wurden aktualisiert");
    }


    public int checkmunzen(String username) {
        this.dbconnection = DatenBankConnect.verbinden();
        int coins = 0;
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("Select coins from users where username = '" + username + "';");
            while (rs.next()) {
                coins = rs.getInt("coins");
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
        return coins;
    }


    public boolean pakethinzufugen(Paket paket) {
        for (Karte kar : paket.getKarten()) {
            if (!karteuberprufen(kar.getName())) {
                if (!kartehinzugen(kar)) {
                    return false;
                }
            }
            if(!DatenBankConnect.executeSQLmitNachricht("INSERT INTO package (\"ID\", \"name\") values ('" + paket.getName() + "','" + kar.getName() + "');", "Karte wurde hinzugefügt")){
                return false;
            }
        }
        return true;
    }


    public boolean kartehinzugen(Karte card) {
        return DatenBankConnect.executeSQLmitNachricht("insert into card (NAME, DAMAGE, ELEMENTTYP, CARDTYPE) values ('" + card.getName() + "','" + card.getDamage() + "','" + card.getdieElemente().name() + "','" + card.getKartenKategorie().name() + "')", "Karte added");
    }


    public int naechstepaketid() {
        this.dbconnection = DatenBankConnect.verbinden();
        String id = "";
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet result = sqlstatement.executeQuery("select max(\"ID\") from package;");
            while (result.next()) {
                id = result.getString("max");
            }
            if (id == null) {
                id = "0";
            }
            result.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
        return Integer.parseInt(id) + 1;
    }



    public boolean benutzerhinzufugen(String username, String password, String nachname, String email, String bio, String image) {
        return DatenBankConnect.executeSQLmitNachricht("INSERT INTO users (username, nachname, email, password, bio, image) values ('" + username + "','" + nachname + "','" + email + "','" + password + "','" + bio + "','" + image + "')", "Benutzer() added");
    }


    public boolean aktualisierenuser(String username, String bio, String image, String name){
        return DatenBankConnect.executeSQL("UPDATE public.users SET nachname = '" + name+ "', bio = '" +bio+ "', image = '" +image+ "' WHERE username LIKE '" +username+ "' ESCAPE '#'");
    }

    public Benutzer getUser(String usernm){
        this.dbconnection = DatenBankConnect.verbinden();
        String username = "", password = "", email = "", bio="", image="";
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("SELECT * FROM users where username = '" + usernm + "';");
            while (rs.next()) {
                username = rs.getString("username");
                email = rs.getString("email");
                password = rs.getString("password");
                bio = rs.getString("bio");
                image = rs.getString("image");
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return new Benutzer(new BenutzerAnmeldungInfo(username, password), username, username, email, new Münze(20), bio, image);
    }


    public Karten getKartenausidlist(List<String> cardnamenarray){
        Karten allCards = new Karten(new ArrayList<>());
        for (String st : cardnamenarray) {
            Connection b = DatenBankConnect.verbinden();
            try {
                sqlstatement = b.createStatement();
                ResultSet rs = sqlstatement.executeQuery("select * from card where NAME =  '" + st + "';");
                while (rs.next()) {
                    int damage =rs.getInt("damage");
                    String elementtyp =rs.getString("elementtyp");
                    String cardtype=rs.getString("cardtype");
                    allCards.addKarten(new Karte(st, elementtyp+cardtype, damage));
                }
                rs.close();
                sqlstatement.close();
                b.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                return null;
            }
        }
        return allCards;
    }


    public Karten getCards(String username){
        this.dbconnection = DatenBankConnect.verbinden();
        String cardname;
        ArrayList<String> cardnamenarray = new ArrayList<>();
        try {
            sqlstatement = this.dbconnection.createStatement();
            ResultSet rs = sqlstatement.executeQuery("select * from user_cards where username = '" + username + "';");
            while (rs.next()) {
                cardname = rs.getString("name");
                cardnamenarray.add(cardname);
            }
            rs.close();
            sqlstatement.close();
            this.dbconnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return getKartenausidlist(cardnamenarray);
    }
}



 class DatenBankConnect {



    public static Connection verbinden() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/ci",
                            "postgres", "postgres");
        } catch (Exception e) {
            System.out.println("Verbindungsfehler");
        }
        return conn;
    }


    public static boolean executeSQLmitNachricht(String sql, String nachricht){
        System.out.println(nachricht);
        return executeSQL(sql);
    }

    public static boolean executeSQL(String sql){
        Connection conn = verbinden();
        Statement state;
        try {
            state = conn.createStatement();
            state.executeUpdate(sql);
            state.close();
            conn.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("SQL Fehler!");
            return false;
        }
        return true;
    }
}
