package in.infosys.backend.service;

import in.infosys.backend.dto.PasswordGeneratorRequestDto;
import in.infosys.backend.dto.PasswordStrengthRequestDto;
import in.infosys.backend.dto.PasswordStrengthResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordStrengthService {

    public PasswordStrengthResponseDto analyzePassword(
            PasswordStrengthRequestDto  requestDto
    ){
        //1. validate the req
        if(requestDto == null  || requestDto.getPassword() == null ){
            throw new IllegalArgumentException("Password is required");
        }

        String password = requestDto.getPassword();

        //2. check empty password
        if(password.isEmpty()){
            throw  new IllegalArgumentException("Password cannot be empty");
        }

        //3. variables
        int score = 0;
        List<String> suggestions = new ArrayList<>();

        //4. check password length
        if(password.length() >= 12){
            score++;
        }else{
            suggestions.add("Password length should be 12");
        }

        //5. check uppercase
        if (containsUppercase(password)) {
            score++;
        } else {
            suggestions.add("Add at least one upper case letter");
        }

        // 6. check lowercase
        if(containsLowercase(password)){
            score++;
        }else{
            suggestions.add("Add at least one  lowercase letter");
        }

        // 7. check number
        if(containsNumber(password)){
            score++;
        }else{
            suggestions.add("Add at least one number ");
        }

        // 8. check special character
        if(containsSpecialCharacter(password)){
            score++;
        }else{
            suggestions.add("Add at least one special character");
        }

        // 9. Determine the length
        String strength;
        if(score <= 2){
            strength =" Weak";
        } else if(score < 7){
            strength = "Medium";
        }else{
            strength = "Strong";
        }

        // 10. Return response
        return new PasswordStrengthResponseDto(
                score,
                strength,
                suggestions
        );
    }

    // check uppercase
    private boolean containsUppercase(String password){
        for(char ch : password.toCharArray()){
            if(Character.isUpperCase(ch)){
                return  true;
            }
        }
        return false;
    }
    // check lowercase
    private boolean containsLowercase(String password){
        for(char ch : password.toCharArray()){
            if(Character.isLowerCase(ch)){
                return  true;
            }
        }
        return false;
    }

    // check numbers
    private boolean containsNumber(String password){
        for(char ch : password.toCharArray()){
            if(Character.isDigit(ch)){
                return  true;
            }
        }
        return false;
    }

    // check special character
    private boolean containsSpecialCharacter(String password){
        for(char ch : password.toCharArray()){
            if(Character.isLetterOrDigit(ch)){
                return  true;
            }
        }
        return false;
    }
}
