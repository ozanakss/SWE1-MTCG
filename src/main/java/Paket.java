
import com.fasterxml.jackson.annotation.*;


@JsonAutoDetect
public class Paket extends Karten{
    @JsonProperty
    private final int preis;
    @JsonProperty
    private String name;


  //Creates a new package with the given stats
    @JsonCreator
    public Paket(@JsonProperty Karten stack,@JsonProperty String name,@JsonProperty int preis) {
        super(stack.getKarten());
        this.name = name;
        this.preis = preis;
    }

    /// getter und setter
    @JsonGetter
    public String getName() {
        return this.name;
    }


    @JsonGetter
    public int getPreis() {
        return this.preis;
    }

    @JsonSetter
    public void setName(String neuName) {
        this.name = neuName;
    }
}
