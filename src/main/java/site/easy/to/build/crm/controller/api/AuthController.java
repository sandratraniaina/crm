package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.util.JWTUtil;
import site.easy.to.build.crm.util.response.Response;
import site.easy.to.build.crm.util.response.ResponseUtil;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {
    private AuthenticationManager authenticationManager;
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Response<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return ResponseUtil.sendResponse(HttpStatus.OK, true, "User logged in successfully", new AuthResponse(token, userDetails));
        } catch (Exception e) {
            return ResponseUtil.sendResponse(HttpStatus.INTERNAL_SERVER_ERROR, false, "An error has occured while loggin in", null);
        }
    }
}

@Data
class AuthRequest {
    private String username;
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AuthResponse {
    private String token;
    private UserDetails userDetails;
}
