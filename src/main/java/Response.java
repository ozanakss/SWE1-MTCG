
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import java.util.*;

//Creates and sends a response based on the request
public class Response {
    private final String payload;
    private final String authorizationString;
    private final PrintStream output;
    private final String       UrlAdresse;



    public Response(String UrlAdresse, String cmd, PrintStream output, String authorizationString, String payload){
        this.authorizationString = authorizationString;
        this.UrlAdresse = UrlAdresse;
        this.output = output;
        this.payload = payload;
        if (this.UrlAdresse != null) {
            switch (cmd) {
                case "GET":
                    if (login()) {
                        getMethodes();
                    } else {
                        sendResponse("There is an error in logging", "401");
                        System.out.println("Die Anfrage kann nicht ohne gültige Authentifizierung durchgeführt werden");

                    }
                    break;
                case "POST":
                    try {
                        postMethodes();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case "PUT":
                    if (login()) {
                        putMethodes();
                    } else {
                        sendResponse("There is an error in logging", "401");
                        System.out.println("Die Anfrage kann nicht ohne gültige Authentifizierung durchgeführt werden");

                    }
                    break;
                default:
                    sendResponse(cmd + " konnte nicht gefunden wwerden!", "405");
                    System.out.println(	"Die angeforderte Ressource wurde nicht gefunden.");

                    break;
            }
        }
    }


    //*******************************************************************GET METHODEN*************************************************
    private void getMethodes(){
          if (this.UrlAdresse.startsWith("/cards")) {
            String username = BasisauthentifizierungGetbenutzername(this.authorizationString);
            Karten allekarten = new DatenBankVerbindung().getCards(username);
            String jsonkarten = HilfsfunktionenJson.Umwandelnobjektinjson (allekarten);
            if (jsonkarten != null && !jsonkarten.isEmpty()){
                System.out.println("Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort übertragen.\n");

                sendResponse("User   "+username+"  hat folgende Karten:"+ jsonkarten, "200");
            }else{
                sendResponse("ERROR Bei der Get cards Methode ist ein Fehler aufgetreten", "500");
                System.out.println("Bei der Get cards Methode ist ein Fehler aufgetreten");
            }
        }else if(this.UrlAdresse.startsWith("/deck")) {
            String format = this.UrlAdresse.substring(this.UrlAdresse.lastIndexOf('?') + 1);
            String benutzername = BasisauthentifizierungGetbenutzername(this.authorizationString);
            ArrayList<String> allCards = new DatenBankVerbindung().getDeck(benutzername);
            Karten deck;
            Object tp;
            if(format.startsWith("format=plain")){
                tp = allCards;
            }else{
                deck = new DatenBankVerbindung().getKartenausidlist(allCards);
                tp = deck;
            }
            String jsonCards = HilfsfunktionenJson.Umwandelnobjektinjson(tp);
            if (jsonCards != null && !jsonCards.isEmpty()) {
                System.out.println("Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort übertragen.\n");
                sendResponse("**User ->"+benutzername+" hat folgende Karten im Deck************      "+jsonCards, "200");
            } else {
                sendResponse("Bei der Get deck Methode ist ein Fehler aufgetreten", "500");
                System.out.println("Bei der Get deck Methode ist ein Fehler aufgetreten");

            }
        }
       else if (this.UrlAdresse.startsWith("/users")) {
            String username = this.UrlAdresse.substring(this.UrlAdresse.lastIndexOf('/') + 1);
            Benutzer benutzer;
              benutzer = new DatenBankVerbindung().getUser(username);
            if (benutzer != null){
                String benutzerinjson = HilfsfunktionenJson.benutzerumwandelnjson(benutzer);
                if(benutzerinjson != null && !benutzerinjson.isEmpty()){
                    System.out.println("Die Anfrage wurde erfolgreich bearbeitet und das Ergebnis der Anfrage wird in der Antwort übertragen.\n");
                    sendResponse(benutzerinjson, "200");

                }
            }else{
                System.out.println("Bei der Get user Methode ist ein Fehler aufgetreten");
                sendResponse("ERROR! Bei der Get user Methode ist ein Fehler aufgetreten", "500");
            }
        }

        else if(this.UrlAdresse.startsWith("/score")) {
            String username = BasisauthentifizierungGetbenutzername(this.authorizationString);
            if (username != null && !username.isEmpty()){
                int leztzekampfid = new DatenBankVerbindung().getletztekampfiduser(username);
                if (leztzekampfid > -1){
                    Map<String, String> map = new DatenBankVerbindung().getkampflogin(leztzekampfid + "");
                    if(map != null && !map.isEmpty()){
                        sendResponse("!!KAMPF!!" + map.get("id") + "\nSCORE\n" + map.get("ERSTE FIGHTER&SPIELER") + "(ERSTE SPIELER!!!) !!!GEGEN!!! " + map.get("ZWEITE FIGHTER&SPIELER") + "(ZWEITE SPIELER!!!!) \n" + map.get("SCORE VOM ERSTEN SPIELER:->") + "(SPIELER 1) **GEGEN** " + map.get("SCORE VOM ZWEITEN SPIELER:->") + "(SPIELER 2) \nSPIEL logging :\n" + HilfsfunktionenResponse.Zeilenumbruchlog(map.get("log")), "200");
                    }else {
                        sendResponse("Couldn't get Battle log :(", "500");
                    }
                }else {
                    sendResponse("Error! Letzter Battle ID-Fehler ", "500");
                    System.out.println("Letzter Battle ID-Fehler ");

                }

            }else{
                sendResponse(" There is an error in logging", "401");            }
        }

        else if(this.UrlAdresse.startsWith("/stats")) {

            String benutzername = BasisauthentifizierungGetbenutzername(this.authorizationString);
            if (benutzername != null && !benutzername.isEmpty()) {
                ArrayList<String> battleIds = new DatenBankVerbindung().getAllKampfIdbenutzer(benutzername);
                if (battleIds != null && !battleIds.isEmpty()){
                    StringBuilder res = new StringBuilder();
                    for(String i : battleIds){
                        Map<String, String> map = new DatenBankVerbindung().getkampflogin(i + "");
                        if(map != null && !map.isEmpty()){
                            res = new StringBuilder("!!KAMPF!!" + map.get("id") + "\nSCORE\n" + map.get("ERSTE FIGHTER&SPIELER") + "(ERSTE SPIELER!!!) !!!GEGEN!!! " + map.get("ZWEITE FIGHTER&SPIELER") + "(ZWEITE SPIELER!!!!) \n" + map.get("SCORE VOM ERSTEN SPIELER:->") + "(SPIELER 1) **GEGEN** " + map.get("SCORE VOM ZWEITEN SPIELER:->") + "(SPIELER 2) \nSPIEL logging:\n");
                            res.append(HilfsfunktionenResponse.Zeilenumbruchlog(map.get("log")));
                        }else {
                            sendResponse("Couldn't get Battle log", "500");
                        }
                    }
                    sendResponse(res.toString(), "200");
                }else {
                    sendResponse("Couldn't get Battle log", "500");
                }
            }else{
                sendResponse("There is an error in logging", "401");
            }
        }else{
            sendResponse(this.UrlAdresse + " not found!", "404");
        }
    }

    private String BasisauthentifizierungGetbenutzername(String authorizationString) {
        String[] values;
        if (authorizationString != null) {
            byte[] credDecoded = Base64.getDecoder().decode(authorizationString);
            String credentials = new String(credDecoded);
            values = credentials.split(":", 2);
        }else{
            values = null;
        }
        return Objects.requireNonNull(values)[0];
    }
  //VergleichEN
    private boolean Basisauthentifizierung(String username, String password, String vergleichenmit){
        String authStringEnc = BasisauthentifizierungBase64(username, password);
        return vergleichenmit.equals(authStringEnc);
    }

   //Creates the basicAuth Base64 token from the username and password
    private String BasisauthentifizierungBase64(String username, String password){
        String authorizationString = username + ":" + password;
        byte[] authEncBytes = Base64.getEncoder().encode(authorizationString.getBytes());
        return new String(authEncBytes);
    }



   // POST METHODEN**********************************************
    private void postMethodes() throws JsonProcessingException {
        if (this.UrlAdresse.startsWith("/users")) {
            Map<String, Object> map = HilfsfunktionenJson.jsonzumap(this.payload);
            String username = (String) Objects.requireNonNull(map).get("Username");
            String password = (String) map.get("Password");
            Benutzer newUser = new Benutzer(new BenutzerAnmeldungInfo(username, password), username, username, "not implemented",  new Münze(20), "BIO", "IMAGE");
            DatenBankVerbindung con = new DatenBankVerbindung();
            if(!con.benutzerhinzufugen(newUser.getBenutzerAnmeldungInfo().getUsername(), newUser.getBenutzerAnmeldungInfo().getPasswort(), newUser.getBenutzerAnmeldungInfo().getUsername(), newUser.getEmail(), newUser.getBio(), newUser.getImage())){
                sendResponse("", "409");
            }
            String userJson = HilfsfunktionenJson.benutzerumwandelnjson(newUser);
            if(userJson != null) {
                sendResponse("User "+username+"wurde erfolgreich erstellt!", "201");
                System.out.println("Die Anfrage wurde erfolgreich bearbeitet");

            }else{
                sendResponse("", "500");
            }
        }else if (this.UrlAdresse.startsWith("/sessions")) {
            Map<String, Object> map = HilfsfunktionenJson.jsonzumap(this.payload);
            String username = (String) Objects.requireNonNull(map).get("Username");
            String password = (String) map.get("Password");
            Benutzer user;

            DatenBankVerbindung con = new DatenBankVerbindung();
            user = con.getUser(username);

            String authorizationString = BasisauthentifizierungBase64(user.getBenutzerAnmeldungInfo().getUsername(), user.getBenutzerAnmeldungInfo().getPasswort());
            if(Basisauthentifizierung(username, password, authorizationString)){
                sendResponse("User "+username+"erfolgreich angemeldet & successfully logged in", "200");
            }else{
                sendResponse("There is an error in logging", "401");
            }
        }else if (this.UrlAdresse.startsWith("/packages")) {
            if (Basisauthentifizierung("admin", "istrator", this.authorizationString)) {
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayList<Karte> listCards = objectMapper.readValue(this.payload, new TypeReference<>() {
                });
                Paket packageCards = new Paket(new Karten(listCards), new DatenBankVerbindung().naechstepaketid() + "", 5);
                if (!new DatenBankVerbindung().pakethinzufugen(packageCards)) {
                    sendResponse("", "500");
                } else {
                    String packageJson = HilfsfunktionenJson.Umwandelnobjektinjson (packageCards);
                    if (packageJson != null) {
                        System.out.println("packages wurden erstellt");
                        sendResponse("***PACKAGE WURDE ERSTELLT***","201");


                    } else {
                        sendResponse("", "500");
                    }
                }
            } else {
                sendResponse("There is an error in logging", "401");
            }
        }else if (this.UrlAdresse.startsWith("/transactions/packages")) {
            if (login()) {
                DatenBankVerbindung db = new DatenBankVerbindung();
                String username = BasisauthentifizierungGetbenutzername(this.authorizationString);
                int coins = new DatenBankVerbindung().checkmunzen(username);
                if (!(coins - 5 >= 0)) {
                    sendResponse("Du hast nur  " + coins + " Coins", "500");
                }else {
                    Paket neupaket = db.RandompackageausdemShop(username);
                    if (neupaket == null) {
                        sendResponse("there are no packages left! ", "500");
                    } else {
                        String packageJson = HilfsfunktionenJson.Umwandelnobjektinjson (neupaket);
                        if (packageJson == null) {
                            sendResponse("Paket Json Fehler", "500");
                        } else {
                            if(!new DatenBankVerbindung().munzeupdate(coins - 5, username)){
                                sendResponse("", "500");
                            }
                            sendResponse("Benutzer "+username+"  succesfully aquired Package :D ", "200");
                        }
                    }
                }
            }else{
                sendResponse("There is an error in logging :(", "401");
            }

        }

        else if (this.UrlAdresse.startsWith("/battle")) {

            if(login()){
                String benutzername = BasisauthentifizierungGetbenutzername(this.authorizationString);
                if (benutzername != null && !benutzername.isEmpty()) {
                    List<String> namevomdeck = new DatenBankVerbindung().getDeck(benutzername);
                    if (namevomdeck != null && !namevomdeck.isEmpty()) {
                        Karten deck = new DatenBankVerbindung().getKartenausidlist(namevomdeck);
                        if(deck != null && deck.getKarten().size() == 4) {
                            Kampf kampfstarten = new DatenBankVerbindung().offeneKampf();
                            if (kampfstarten == null) {

                                if(new DatenBankVerbindung().kampfhinzufugen(benutzername)){
                                    sendResponse("You are FIRST FIGHTER \nBattle was created by: " + benutzername + "(#PLAYER1) \n***************2. Player will now join the battle************************","200");
                                }else {
                                    sendResponse("error", "500");
                                }

                            } else {
                                // Dem Spieler beitreten
                                Benutzer player2 = new DatenBankVerbindung().getUser(benutzername);
                                if(player2 != null){
                                    kampfstarten.setPlayer2(player2);
                                    kampfstarten.setDeckPlayer2(deck);
                                    if(new DatenBankVerbindung().loeschbattle(kampfstarten.getId() + "")) {
                                        if (kampfstarten.kampfstarten()){
                                            if (new DatenBankVerbindung().logeinfugen(kampfstarten.getId() + "", kampfstarten.getPlayer1().getName(), kampfstarten.getPlayer2().getName(), kampfstarten.getScorePlayer1() + "", kampfstarten.getScorePlayer2() + "", kampfstarten.getLog().toString())) {
                                                if (new DatenBankVerbindung().loschdeck(kampfstarten.getPlayer1().getBenutzerAnmeldungInfo().getUsername()) && new DatenBankVerbindung().loschdeck(kampfstarten.getPlayer2().getBenutzerAnmeldungInfo().getUsername())) {
                                                    //ALTE DECKKARTEN LÖSCHEN
                                                    ArrayList<String> oldDeck1 = new ArrayList<>();
                                                    for (Karte kk : kampfstarten.getDeckPlayer1Init().getKarten()) {
                                                        oldDeck1.add(kk.getName());
                                                    }
                                                    ArrayList<String> oldDeck2 = new ArrayList<>();
                                                    for (Karte ca : kampfstarten.getDeckPlayer2Init().getKarten()) {
                                                        oldDeck2.add(ca.getName());
                                                    }
                                                    //NEUE KARTEN LÖSCHEN, WENN SIE EXISTIEREN
                                                    Karten player1cards = new DatenBankVerbindung().getCards(kampfstarten.getPlayer1().getBenutzerAnmeldungInfo().getUsername());
                                                    for (Karte ca : kampfstarten.getDeckPlayer1().getKarten()) {
                                                        oldDeck1.add(ca.getName());
                                                    }
                                                    if (player1cards.getKarten() != null && !player1cards.getKarten().isEmpty()) {
                                                        for (String kk : oldDeck1) {
                                                            if (!new DatenBankVerbindung().loeschbenutzerkarte(kampfstarten.getPlayer1().getBenutzerAnmeldungInfo().getUsername(), kk)) {
                                                                sendResponse("Fehler beim Löschen der Benutzerkarte 1 " + kk, "500");
                                                            }
                                                        }
                                                    }
                                                    Karten player2cards = new DatenBankVerbindung().getCards(kampfstarten.getPlayer2().getBenutzerAnmeldungInfo().getUsername());
                                                    for (Karte ca : kampfstarten.getDeckPlayer2().getKarten()) {
                                                        oldDeck2.add(ca.getName());
                                                    }
                                                    if (player2cards.getKarten() != null && !player2cards.getKarten().isEmpty()) {
                                                        for (String ca : oldDeck2) {
                                                            if (!new DatenBankVerbindung().loeschbenutzerkarte(kampfstarten.getPlayer2().getBenutzerAnmeldungInfo().getUsername(), ca)) {
                                                                sendResponse("Fehler beim Löschen der Benutzerkarte 2 " + ca, "500");
                                                            }
                                                        }
                                                    }

                                                    //KARTE HINZUFUGEN DECK
                                                    for (Karte ca : kampfstarten.getDeckPlayer1().getKarten()) {
                                                        if (!new DatenBankVerbindung().kartehinzufugen(kampfstarten.getPlayer1().getBenutzerAnmeldungInfo().getUsername(), ca.getName())) {
                                                            sendResponse("Fehler beim Hinzufügen der Karte zu Benutzer1 " + ca.getName(), "500");
                                                        }
                                                    }
                                                    for (Karte ca : kampfstarten.getDeckPlayer2().getKarten()) {
                                                        if (!new DatenBankVerbindung().kartehinzufugen(kampfstarten.getPlayer2().getBenutzerAnmeldungInfo().getUsername(), ca.getName())) {
                                                            sendResponse("Fehler beim Hinzufügen der Karte zu Benutzer 2 " + ca.getName(), "500");
                                                        }
                                                    }
                                                    sendResponse("You are  FIGHTER 2 \n !!!!!!!!!KAMPF!!!!!!! --> " + kampfstarten.getPlayer1().getName() + "**SPIELER 1 ** **GEGEN** " + kampfstarten.getPlayer2().getName() + "**SPIELER 2 **\n", "200");
                                                }
                                            } else {
                                                sendResponse("", "500"); //ERROR
                                            }
                                        }else {
                                            sendResponse("", "500");
                                        }
                                    }else{
                                        sendResponse("", "500"); //ERROR
                                    }
                                }else{
                                    sendResponse("", "500"); //ERROR
                                }
                            }
                        }else {
                            sendResponse("","424");
                        }
                    }else {
                        sendResponse("","424");
                    }
                }else {
                    sendResponse("", "500");
                }
            }else {
                sendResponse("There is an error in logging :(", "401");
            }
        } else{
            sendResponse(this.UrlAdresse + " konnte nicht gefunden werden", "404");
        }

    }


    private boolean login(){
        if(this.authorizationString != null && !this.authorizationString.isEmpty()){
            String username = BasisauthentifizierungGetbenutzername(this.authorizationString);
            Benutzer user;
            user = new DatenBankVerbindung().getUser(username);
            return Basisauthentifizierung(user.getBenutzerAnmeldungInfo().getUsername(), user.getBenutzerAnmeldungInfo().getPasswort(), this.authorizationString);

        }else{
            return false;
        }
    }

   // PUT METHODEN******************************************************
    private void putMethodes(){
        if (this.UrlAdresse.startsWith("/users")) {
            String benutzername = this.UrlAdresse.substring(this.UrlAdresse.lastIndexOf('/') + 1);
            Benutzer benutzer;
            benutzer = new DatenBankVerbindung().getUser(benutzername);
            if(benutzer != null) {
                Map<String, Object> map = HilfsfunktionenJson.jsonzumap(this.payload);
                String bio = (String) Objects.requireNonNull(map).get("Bio");
                String image = (String) map.get("Image");
                String name = (String) map.get("Name");
                benutzer.setBio(bio);
                benutzer.setImage(image);
                benutzer.setNachname(name);
                if (new DatenBankVerbindung().aktualisierenuser(benutzername, benutzer.getBio(), benutzer.getImage(), benutzer.getNachname())) {
                    sendResponse(HilfsfunktionenJson.benutzerumwandelnjson(benutzer), "200");
                } else {
                    sendResponse("error", "500");
                }
            }else{
                sendResponse("error", "500");
            }
        }else if(this.UrlAdresse.startsWith("/deck")) {
            List<String> deckIds = HilfsfunktionenJson.Jsonzulist(this.payload);
            if (deckIds != null && deckIds.size() == 4){
                if (new DatenBankVerbindung().setDeck(BasisauthentifizierungGetbenutzername(this.authorizationString), deckIds)){
                    Karten deck = new DatenBankVerbindung().getKartenausidlist(deckIds);
                    String deckJson = HilfsfunktionenJson.Umwandelnobjektinjson (deck);
                    if (deck != null && deckJson != null){
                        sendResponse(deckJson, "200");
                    }else {
                        sendResponse("Deck could not be taken from the Database", "500");
                    }
                }else {
                    sendResponse("", "500");
                }
            }else{
                sendResponse(Objects.requireNonNull(deckIds).size() + "of 4 cards are in the deck.","500");
            }
        }else{
            sendResponse(this.UrlAdresse + " konnte leider nicht gefunden werden !", "404");
        }
    }




   // RESPONSE SCHICKEN
    private void sendResponse(String responseText, String code){
        output.print("HTTP/1.0 "+code+"\r\n");
        output.print("Content-Type: text/plain\r\n");
        output.print("Content-Length: "+responseText.length()+"\r\n");
        output.print("Server: localhost\r\n");
        output.print("\r\n");
        output.print(responseText);
    }
}





 class HilfsfunktionenJson {

   // UMWANDELN
    public static String Umwandelnobjektinjson (Object objekt){
        ObjectMapper objectMapper = new ObjectMapper();
        String packageJson = "";
        if(objekt != null) {
            try {
                packageJson += objectMapper.writeValueAsString(objekt);
            } catch (JsonProcessingException e) {
                packageJson = "Error:  Exception Json  " + e.getMessage();
            }
            return packageJson;
        }else{
            return null;
        }
    }


    public static List<String> Jsonzulist(String payload){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Map<String, Object> jsonzumap(String payload){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(payload, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String benutzerumwandelnjson(Benutzer user){
        return Umwandelnobjektinjson (user);
    }

}




 class HilfsfunktionenEnum {


    public static DieElemente elementalsstring(String elementTypeString){
        for (DieElemente c : DieElemente.values()) {
            if (elementTypeString.toLowerCase().contains(c.name().toLowerCase())) {
                return c;
            }
        }
        return null;
    }


    public static KartenKategorie kartetypalsstring(String cardTypeString){
        for (KartenKategorie re : KartenKategorie.values()) {
            if (cardTypeString.toLowerCase().contains(re.toString().toLowerCase())) {
                return re;
            }
        }
        return null;
    }
}

 class HilfsfunktionenResponse {

    public static StringBuilder Zeilenumbruchlog(String zeilelog){
        StringBuilder stringx = new StringBuilder();
        for (char lo: zeilelog.toCharArray()){
            if(lo == ','){
                stringx.append("\n");
            }else if(lo == '['){
                stringx.append("\n");
            }else if(lo == ']'){
                stringx.append("\n");
            }else {
                stringx.append(lo);
            }
        }
        return stringx;
    }
}
