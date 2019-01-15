package ru.alesandrus.models.enumerations;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
public enum Category {
    VELOSIPEDY("velosipedy"),
    SPORT_I_OTDYH("sport_i_otdyh"),
    TOVARY_DLYA_DETEY_I_IGRUSHKI("tovary_dlya_detey_i_igrushki");

    private String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
