package site.easy.to.build.crm.config.api;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Data
public class ApiUserDetails implements UserDetailsService {
    UserRepository userRepository;
    HttpSession session;

    public ApiUserDetails (UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password;
        User user = userRepository.findByUsername(username).size() == 1  ? userRepository.findByUsername(username).get(0) : null;
        List<GrantedAuthority> authorities;
        
        if(user == null) {
            throw new UsernameNotFoundException("user details not found for the user : " + username);
        } else {
            if(user.getStatus().equals("suspended")) {
                HttpServletResponse httpServletResponse =
                        ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
                try {
                    assert httpServletResponse != null;
                    httpServletResponse.sendRedirect("/account-suspended");
                } catch (IOException e) {
                    return null;
                }
            }
            password = user.getPassword();
            session.setAttribute("loggedInUserId", user.getId());
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
        }
        return new org.springframework.security.core.userdetails.User(username, password,authorities);
    }
}
