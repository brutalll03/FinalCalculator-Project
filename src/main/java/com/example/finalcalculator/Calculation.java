package com.example.finalcalculator;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Entity
@Data
public class Calculation {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int num1;
    private int num2;
    private int sumResult;
    private int multiplyResult;
    private double divisionResult;
    private int subtractionResult;
    private int modulusResult;

    private String userEmail;

    @Transient
    public int getModulusResult() {
        return modulusResult;
    }
    private String operation;

    @Transient
    public String getResult() {
        switch (operation) {
            case "add":
                return String.valueOf(sumResult);
            case "subtract":
                return String.valueOf(subtractionResult);
            case "multiply":
                return String.valueOf(multiplyResult);
            case "divide":
                return String.valueOf(divisionResult);
            case "modulus":
                return String.valueOf(modulusResult);
            default:
                return "Invalid Operation";



        }
    }
}