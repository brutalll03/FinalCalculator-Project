package com.example.finalcalculator;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, HttpSession session) {
        Optional<AppUser> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            session.setAttribute("userEmail", email);
            return "redirect:/api/calculate";
        } else {
            return "redirect:/api/login?error";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, HttpSession session) {
        Optional<AppUser> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return "redirect:/api/login?error=exists";
        } else {
            userRepository.save(new AppUser(email));
            session.setAttribute("userEmail", email);
            return "redirect:/api/calculate";
        }
    }



    @GetMapping("/calculate")
    public String showCalculator(Model model, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return "redirect:/api/login";
        }
        // Fetch calculations by user email
        List<Calculation> calculationHistory = calculationRepository.findByUserEmail(userEmail);
        model.addAttribute("calculationHistory", calculationHistory);

        model.addAttribute("calculation", new Calculation());

        return "calculator";
    }

    @PostMapping("/calculate")
    public String performCalculation(@ModelAttribute Calculation calculation, Model model,
                                     @RequestParam(name = "operation", defaultValue = "add") String operation) {
        if ("add".equals(operation)) {
            // Addition
            calculation.setSumResult(calculation.getNum1() + calculation.getNum2());
            calculation.setMultiplyResult(0); // Reset multiplication result
            model.addAttribute("message", "Addition result: " + calculation.getSumResult());
        } else if ("multiply".equals(operation)) {
            // Multiplication
            calculation.setMultiplyResult(calculation.getNum1() * calculation.getNum2());
            calculation.setSumResult(0); // Reset addition result
            model.addAttribute("message", "Multiplication result: " + calculation.getMultiplyResult());
        } else if ("divide".equals(operation)) {
            // Division
            if (calculation.getNum2() != 0) {
                calculation.setDivisionResult((double) calculation.getNum1() / calculation.getNum2());
                calculation.setSumResult(0); // Reset addition result
                calculation.setMultiplyResult(0); // Reset multiplication result
                model.addAttribute("message", "Division result: " + calculation.getDivisionResult());
            } else {
                model.addAttribute("message", "Cannot divide by zero!");
            }
        } else if ("subtract".equals(operation)) {
            // Subtraction
            calculation.setSubtractionResult(calculation.getNum1() - calculation.getNum2());
            calculation.setSumResult(0); // Reset addition result
            calculation.setMultiplyResult(0); // Reset multiplication result
            calculation.setDivisionResult(0); // Reset division result
            model.addAttribute("message", "Subtraction result: " + calculation.getSubtractionResult());
        } else if ("modulus".equals(operation)) {
            // Modulus
            if (calculation.getNum2() != 0) {
                calculation.setModulusResult(calculation.getNum1() % calculation.getNum2());
                calculation.setSumResult(0); // Reset addition result
                calculation.setMultiplyResult(0); // Reset multiplication result
                calculation.setDivisionResult(0); // Reset division result
                calculation.setSubtractionResult(0); // Reset subtraction result
                model.addAttribute("message", "Modulus result: " + calculation.getModulusResult());
            } else {
                model.addAttribute("message", "Cannot calculate modulus with zero!");
            }
        }

        calculationRepository.save(calculation);
        List<Calculation> calculationHistory = calculationRepository.findAll();
        model.addAttribute("calculationHistory", calculationHistory);
        return "calculator";
    }

    @GetMapping("/history")
    public String showCalculationHistory(Model model) {
        List<Calculation> calculationHistory = calculationRepository.findAll();
        model.addAttribute("calculationHistory", calculationHistory);
        return "history";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("userEmail"); // Remove the userEmail attribute from the session
        return "redirect:/api/login"; // Redirect to the login page
    }
}