package Model;

import java.util.List;

public enum Country {
    FR ("France"),
    ES ("Spain"),
    DE ("Germany"),
    CH ("Switzerland"),
    GB ("United Kingdom"),
    IT ("Italy"),
    LU ("Luxembourg"),
    NL ("Netherlands"),
    PT ("Portugal"),
    CZ ("Czech Republic"),
    AT ("Austria"),
    AD ("Andorra"),
    GR ("Greece"),
    DK ("Denmark"),
    BE ("Belgium"),
    IE ("Ireland"),
    HR ("Croatia"),
    HU ("Hungary"),
    RO ("Romania"),
    RS ("Serbia"),
    SK ("Slovakia"),
    SI ("Slovenia"),
    LT ("Lithuania"),
    RU ("Russia"),
    UA ("Ukraine"),
    BG ("Bulgaria"),
    SE ("Sweden"),
    NO ("Norway"),
    BY ("Belarus"),
    MK ("Macedonia"),
    LV ("Latvia"),
    MA ("Morocco"),
    FI ("Finland"),
    EE ("Estonia"),
    BA ("Bosnia and Herzegovina"),
    CY ("Cyprus"),
    ME ("Montenegro"),
    AL ("Albania"),
    SM ("San Marino"),
    MT ("Malta"),
    LI ("Liechtenstein"),
    MD ("Moldova"),
    TR ("Turkey"),
    PL ("Poland");

    private final String name;

    Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<Country> getAllCountries() {
        return List.of(Country.values());
    }
}
