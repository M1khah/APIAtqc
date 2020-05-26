package com.atqc.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.javafaker.Faker;
import lombok.*;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetModel {

    static Faker faker = new Faker();

    private long id;
    private String name;;
    private PetStatus petStatus;
    private List<String> photoUrls;

    public enum PetStatus{

        AVAILABLE("available"),
        PENDING("pending"),
        SOLD("sold");

        private final String petStatus;

        PetStatus(String petStatus) {
            this.petStatus = petStatus;
        }

        @JsonValue
        public String getPetStatus() {
            return petStatus;
        }
    }

    public static PetModel positiveCreatePet(){
        return PetModel.builder()
                .id(Integer.parseInt(faker.number().digits(7)))
                .name(faker.ancient().primordial())
                .photoUrls(Arrays.asList(faker.internet().url(), faker.internet().url()))
                .petStatus(PetStatus.AVAILABLE)
                .build();
    }

    public static PetModel negativeCreatePetNoName(){
        return PetModel.builder()
                .id(faker.number().randomDigit())
                .petStatus(PetStatus.SOLD)
                .photoUrls(Arrays.asList(faker.internet().url(), faker.internet().url()))
                .build();
    }

    public static PetModel negativeCreatePetWrongId(){
        return PetModel.builder()
                .id(faker.number().numberBetween(-99999, -1))
                .name(faker.matz().quote())
                .petStatus(PetStatus.SOLD)
                .photoUrls(Arrays.asList(faker.internet().url(), faker.internet().url()))
                .build();
    }

}
