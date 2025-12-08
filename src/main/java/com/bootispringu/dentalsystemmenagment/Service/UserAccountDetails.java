package com.bootispringu.dentalsystemmenagment.Service;

import com.bootispringu.dentalsystemmenagment.Entity.UserAccount;
import com.bootispringu.dentalsystemmenagment.Repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetails implements UserDetailsService {
    private UserAccountRepository userAccountRepository;

    public UserAccountDetails(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username).
                orElseThrow(()-> new UsernameNotFoundException("User not found :" + username));

        return User.withUsername(userAccount.getUsername())
                .password(userAccount.getPassword())
                .roles(userAccount.getRole().name())
                .build();
    }
}
