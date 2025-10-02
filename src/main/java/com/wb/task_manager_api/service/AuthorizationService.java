package com.wb.task_manager_api.service;

import com.wb.task_manager_api.domain.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o usuário pelo email
        UserDetails userDetails = userRepository.findByEmail(username);

        // 2. Verifica se o usuário foi encontrado e, se não, lança a exceção esperada pelo Spring Security
        if (userDetails == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        return userDetails;
    }
}
