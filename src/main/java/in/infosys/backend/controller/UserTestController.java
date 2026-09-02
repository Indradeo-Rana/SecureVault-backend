package in.infosys.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class UserTestController {


    @GetMapping
    public ResponseEntity<String> test(){

        return ResponseEntity
                .ok( "Protected API accessed successfully");
    }



}
