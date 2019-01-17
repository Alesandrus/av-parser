package ru.alesandrus.models.enumerations;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
public enum Category {
    VELOSIPEDY("Велосипеды"),
    SPORT_I_OTDYH("Спорт и отдых"),
    TOVARY_DLYA_DETEY_I_IGRUSHKI("Товары для детей и игрушки");

    private String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Category getCategoryFromUrl(String url) {
        String[] urlArr = url.split("/");
        String categoryName = urlArr[4];
        switch (categoryName) {
            case "sport_i_otdyh":
                return Category.SPORT_I_OTDYH;
            case "velosipedy":
                return Category.VELOSIPEDY;
            case "tovary_dlya_detey_i_igrushki":
                return Category.TOVARY_DLYA_DETEY_I_IGRUSHKI;
            default:
                return null;
        }
    }
}
