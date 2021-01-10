
import com.fasterxml.jackson.annotation.*;

//user
@JsonAutoDetect
public class Benutzer{
    @JsonProperty
    private String name, nachname, email, bio, image;
    @JsonProperty
    private final BenutzerAnmeldungInfo benutzerAnmeldungInfo;

    @JsonCreator
    public Benutzer(@JsonProperty BenutzerAnmeldungInfo benutzerAnmeldungInfo, @JsonProperty String name, @JsonProperty String nachname, @JsonProperty String email, @SuppressWarnings("unused") @JsonProperty Münze coins, @JsonProperty String bio, @JsonProperty String image) {
        this.benutzerAnmeldungInfo = benutzerAnmeldungInfo;
        this.name = name;
        this.nachname = nachname;
        this.email = email;
        this.bio = bio;
        this.image = image;
    }

  // get und set methoden
    @JsonGetter
    public String getBio() {
        return bio;
    }


    @JsonSetter
    public void setBio(String bio) {
        this.bio = bio;
    }


    @JsonGetter
    public String getImage() {
        return image;
    }


    @JsonSetter
    public void setImage(String image) {
        this.image = image;
    }


    @JsonGetter
    public BenutzerAnmeldungInfo getBenutzerAnmeldungInfo() {
        return benutzerAnmeldungInfo;
    }


    @JsonGetter
    public String getName() {
        return this.name;
    }


    @JsonGetter
    public String getNachname() {
        return this.nachname;
    }


    @JsonGetter
    public String getEmail() {
        return this.email;
    }


    @JsonSetter
    public void setName(String name) {
        this.name = name;
    }


    @JsonSetter
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }


    @JsonSetter
    public void setEmail(String email) {
        this.email = email;
    }

}

// Benutzer Anmeldung Informationen

 class BenutzerAnmeldungInfo {
    private final String passwort;
    private final String username;


    public BenutzerAnmeldungInfo(String username, String passwort){
        this.username = username;
        this.passwort = passwort;
    }


    public String getPasswort() {
        return passwort;
    }

    public String getUsername() {
        return username;
    }

}
