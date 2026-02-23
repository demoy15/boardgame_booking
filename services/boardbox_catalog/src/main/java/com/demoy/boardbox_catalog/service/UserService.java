package com.demoy.boardbox_catalog.service;

import com.demoy.boardbox_catalog.dto.UserDto;
import com.demoy.boardbox_catalog.model.User;
import com.demoy.boardbox_catalog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<UserDto> createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());

        return Mono.fromCallable(() -> userRepository.save(user))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::toDto);
    }

    public Mono<UserDto> getUserById(UUID id) {
        return Mono.fromCallable(() -> userRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.isPresent() ? Mono.just(toDto(opt.get())) : Mono.empty());
    }

    public Mono<UserDto> getUserByUsername(String username) {
        return Mono.fromCallable(() -> userRepository.findByUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> opt.isPresent() ? Mono.just(toDto(opt.get())) : Mono.empty());
    }

    public Mono<UserDto> updateUser(UUID id, UserDto userDto) {
        return Mono.fromCallable(() -> userRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(opt -> {
                    if (opt.isPresent()) {
                        User user = opt.get();
                        user.setUsername(userDto.username());
                        user.setEmail(userDto.email());
                        user.setFirstName(userDto.firstName());
                        user.setLastName(userDto.lastName());
                        
                        return Mono.fromCallable(() -> userRepository.save(user))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(this::toDto);
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Mono<Void> deleteUser(UUID id) {
        return Mono.fromCallable(() -> {
            userRepository.deleteById(id);
            return null;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt()
        );
    }
}