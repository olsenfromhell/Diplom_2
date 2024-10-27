package site.nomoreparties.stellarburgers;

import com.github.javafaker.Faker;

public class FakerData {

    static Faker faker = new Faker();

    public static String email = faker.internet().emailAddress();
    public static String name = faker.name().username();
    public static String password = faker.internet().password();

}