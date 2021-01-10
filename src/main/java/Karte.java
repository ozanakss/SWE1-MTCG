
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;



// KARTE
@JsonAutoDetect
public class Karte {

    @JsonProperty
    private final KartenKategorie kartenKategorie;
    @SuppressWarnings("unused")
    @JsonProperty
    private boolean locked;
    @JsonProperty
    private String name;
    @JsonProperty
    private double damage;
    @JsonProperty
    private DieElemente dieElemente;


     // EINE KARTE WIRD ERSTELLT  mit  Eigenschaften
    public Karte(String name, double damage, DieElemente dieElemente, KartenKategorie kartenKategorie) {
        this.name = name;
        this.damage = damage;
        this.dieElemente = dieElemente;
        this.kartenKategorie = kartenKategorie;
    }

     //Creates a new card with
    // Card names, the element type and the card category are determined automatically
    @JsonCreator
    public Karte(@JsonProperty("Id") String name, @JsonProperty("Name") String elementCardTyp, @JsonProperty("Damage") double damage){
        KartenKategorie tmpCard = HilfsfunktionenEnum.kartetypalsstring(elementCardTyp);
        DieElemente tmpElement = null;
        if(tmpCard == null){
            tmpCard = KartenKategorie.UNDEF;
        }

        //Special cards that have no element are assigned their natural element here
        switch (tmpCard.name().toLowerCase()) {
            case "dragon":
            case "fireelves":
                tmpElement = DieElemente.FIRE;
                break;
            case "ork":
            case "wizzard":
            case "knight":
            case "troll":
            case "goblin":
            case "elf":
                tmpElement = DieElemente.REGULAR;
                break;
            case "kraken":
                tmpElement = DieElemente.WATER;
                break;
        }

        this.name = name;
        this.damage = damage;
        this.dieElemente = tmpElement;
        this.kartenKategorie = tmpCard;
    }

    // GETTER UND SETTER
    @JsonGetter
    public String getName() {
        return this.name;
    }

    @JsonGetter
    public double getDamage() {
        return this.damage;
    }


    @JsonGetter
    public DieElemente getdieElemente() {
        return this.dieElemente;
    }

    @JsonGetter
    public KartenKategorie getKartenKategorie() {
        return this.kartenKategorie;
    }


    @JsonSetter
    public void setName(String neuerName) {
        this.name = neuerName;
    }


    @JsonSetter
    public void setDamage(int damage) {
        this.damage = damage;
    }

    @JsonSetter
    public void setElementType(DieElemente dieElemente) {
        this.dieElemente = dieElemente;
    }

   //here it is tested whether the two cards are the same
    public boolean equals(Karte karte){
        if(karte == null) return false;
        return this.name.equals(karte.getName()) && this.kartenKategorie == karte.getKartenKategorie() && this.dieElemente == karte.getdieElemente() && this.damage == karte.getDamage();
    }
}



@JsonAutoDetect
 class Karten {
    @JsonDeserialize(as = ArrayList.class, contentAs = Karte.class)
    private ArrayList<Karte> karten;


    @JsonCreator
    public Karten(@JsonProperty("cards") ArrayList<Karte> cardsArrayList) {
        this.karten = cardsArrayList;
    }


    public void setKarten(ArrayList<Karte> cards) {
        this.karten = cards;
    }

    //Adds a new card
    @JsonSetter
    public void addKarten(Karte neueKarte) {
        this.karten.add(neueKarte);
    }


    @JsonGetter
    public ArrayList<Karte> getKarten() {
        return this.karten;
    }

  //Deletes the given card
    public void löschKarte(Karte löschkarte) {
        this.karten.removeIf(obj -> obj.equals(löschkarte));
    }


    public boolean enthealtkarte(String searchkarte){
        AtomicBoolean returnval = new AtomicBoolean(false);
        this.karten.forEach(item -> returnval.set(item.getName().equals(searchkarte)));
        return returnval.get();
    }

    public boolean equals(Karten vergleichen){
        if (this.karten == null && vergleichen.getKarten() == null){
            return true;
        }else if ((this.karten == null && vergleichen.getKarten() != null) || (this.karten != null && vergleichen.getKarten() == null)){
            return false;
        }else return Objects.requireNonNull(this.karten).containsAll(vergleichen.getKarten()) && vergleichen.getKarten().containsAll(this.karten);
    }
}
